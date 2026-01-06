package service_management;

import StaffManagement.Staff;
import flightManagment.Flight;
import flightManagment.Plane;
import flightManagment.Seat;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.CodeSource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;

public class FileOp {
    
    // Helper to detect integers
    private static boolean isInteger(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /*
     * Data persistence strategy:
     * --------------------------
     * When the application is packaged as a JAR the bundled resources inside
     * the JAR are read-only. To allow runtime persistence we treat the files
     * in `resources/` as templates only. On first run we copy those CSV files
     * out of the JAR into a writable `data/` folder located at
     * `System.getProperty("user.dir")/data/` so the application can read and
     * write them freely. After the initialization, all reads and writes use
     * the external `data/` directory.
     */

    private static final String[] DEFAULT_CSV_FILES = new String[] {
        "planes.csv", "flights.csv", "passengers.csv", "reservations.csv", "tickets.csv", "staff.csv"
    };

    // Determine the application directory (location next to the JAR or classes folder)
    private static Path getAppDirectory() {
        try {
            CodeSource cs = FileOp.class.getProtectionDomain().getCodeSource();
            if (cs != null) {
                URI uri = cs.getLocation().toURI();
                Path loc = Paths.get(uri);
                if (Files.isRegularFile(loc)) { // running from a JAR file
                    return loc.getParent().toAbsolutePath().normalize();
                } else { // running from classes directory in IDE
                    return loc.toAbsolutePath().normalize();
                }
            }
        } catch (Exception e) {
            // fallthrough to default
        }
        return Paths.get(".").toAbsolutePath().normalize();
    }

    private static Path getDataDir() {
        Path data = getAppDirectory().resolve("data");
        try {
            if (!Files.exists(data)) Files.createDirectories(data);
        } catch (IOException e) {
            // If we cannot create data dir next to JAR, fallback to a local ./data
            data = Paths.get("data").toAbsolutePath().normalize();
            try { if (!Files.exists(data)) Files.createDirectories(data); } catch (IOException ex) { /* ignore */ }
        }
        return data;
    }

    private static String basename(String path) {
        if (path == null) return null;
        path = path.replace("\\", "/");
        int idx = path.lastIndexOf('/');
        return (idx >= 0) ? path.substring(idx + 1) : path;
    }

    // Copy a resource from the classpath (JAR) into the data/ directory.
    private static void copyResourceToData(String resourceName) throws IOException {
        String base = basename(resourceName);
        Path out = getDataDir().resolve(base);

        ClassLoader cl = FileOp.class.getClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is == null) is = cl.getResourceAsStream("resources/" + resourceName);
        if (is == null) is = cl.getResourceAsStream("src/resources/" + resourceName);

        if (is == null) {
            // No bundled template found; create an empty file to allow writes later.
            if (!Files.exists(out)) Files.createFile(out);
            return;
        }

        // Copy resource stream to target path
        try (InputStream in = is) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Ensure data directory exists and default CSVs are present (copied from JAR templates if missing)
    private static void ensureDataInitialized() {
        Path data = getDataDir();
        for (String csv : DEFAULT_CSV_FILES) {
            Path f = data.resolve(csv);
            if (!Files.exists(f)) {
                try {
                    copyResourceToData(csv);
                } catch (IOException e) {
                    // If copy fails, create an empty file as a fallback so app can continue
                    try { Files.createFile(f); } catch (IOException ex) { /* ignore */ }
                }
            }
        }
    }

    // Resolve a file under the data/ directory. Caller should call ensureDataInitialized() first.
    private static Path resolveDataFile(String requested) {
        String base = basename(requested);
        return getDataDir().resolve(base);
    }

    // Public reader: returns a BufferedReader to the file in data/ (initializes/copies defaults if needed)
    public static BufferedReader getResourceReader(String resourceName) throws FileNotFoundException {
        ensureDataInitialized();
        Path p = resolveDataFile(resourceName);
        if (!Files.exists(p)) {
            try { copyResourceToData(resourceName); } catch (IOException e) { /* ignore */ }
        }
        if (!Files.exists(p)) throw new FileNotFoundException("Data file not found: " + resourceName);
        try {
            return Files.newBufferedReader(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileNotFoundException("Unable to open data file: " + p.toAbsolutePath().toString());
        }
    }

    public static Map<Long, Staff> getStaffData(String fileName) throws FileNotFoundException {
        Map<Long, Staff> staffMap = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 3 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Staff s = new Staff(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2]
                    );
                    staffMap.put((long)s.getStaffID(), s);
                } catch (Exception e) {
                    System.out.println("Error parsing staff row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return staffMap;
    }

    public static Map<Integer, Flight> getFlightData(String fileName, Map<Integer, Plane> planes) throws FileNotFoundException {
        Map<Integer, Flight> flights = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 7 || !isInteger(d[0]) || !isInteger(d[6])) {
                    // System.out.println("Skipping invalid/heading flight row: " + line);
                    continue;
                }
                try {
                    Plane originalPlane = planes.get(Integer.parseInt(d[6]));
                	Plane flightSpecificPlane = (originalPlane != null) ? originalPlane.getCopy() : null;
                    Flight f = new Flight(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2],
                            LocalDate.parse(d[3]),
                            LocalTime.parse(d[4]),
                            Duration.parse(d[5]),
                            flightSpecificPlane);
                    flights.put(f.getFlightNum(), f);
                } catch (Exception e) {
                    System.out.println("Error parsing flight row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return flights;
    }

    public static Map<Long, Passenger> getPassengerData(String fileName) throws FileNotFoundException {
        Map<Long, Passenger> passengers = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Passenger p = new Passenger(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2],
                            Long.parseLong(d[3])
                    );
                    passengers.put(p.getPassengerId(), p);
                } catch (Exception e) {
                    System.out.println("Error parsing passenger row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return passengers;
    }

    public static Map<Integer, Plane> getPlaneData(String fileName) throws FileNotFoundException {
        Map<Integer, Plane> planes = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Plane plane = new Plane(
                            Integer.parseInt(d[0]),
                            d[1],
                            Integer.parseInt(d[2]),
                            Integer.parseInt(d[3])
                    );
                    planes.put(plane.getPlaneID(), plane);
                } catch (Exception e) {
                    System.out.println("Error parsing plane row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return planes;
    }

    public static Map<String, Reservation> getReservationData(String fileName, Map<Integer, Flight> flights, Map<Long, Passenger> passengers) throws FileNotFoundException {
        Map<String, Reservation> reservations = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 5 || !isInteger(d[1]) || !isInteger(d[2])) {
                    continue;
                }
                try {
                    String reservationId = d[0];
                    int flightNum = Integer.parseInt(d[1]);
                    long passengerId = Long.parseLong(d[2]);
                    Flight flight = flights.get(flightNum);
                    String seatNum = d[3];
                    Passenger passenger = passengers.get(passengerId);

                    if (flight == null || passenger == null || flight.getPlane() == null) {
                        System.out.println("Invalid linking in reservation: " + Arrays.toString(d));
                        continue;
                    }
                    Seat seat = flight.getPlane().getSeatByNumber(seatNum);
                    if (seat == null) {
                        System.out.println("Seat not found: " + seatNum);
                        continue;
                    }
                    if (seat.isReservedStatus()) {
                        // Already reserved, skip or log
                        continue;
                    }
                    seat.setReservedStatus(true, flight.getPlane());
                    Reservation tmp = new Reservation(d[0], flight, passenger, seat, LocalDate.parse(d[4]));
                    reservations.put(reservationId, tmp);
                } catch (Exception e) {
                    System.out.println("Error parsing reservation row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return reservations;
    }

    public static Map<Integer, Ticket> getTicketData(String fileName, Map<String, Reservation> reservations, Map<Integer, Flight> flights) throws FileNotFoundException {
        Map<Integer, Ticket> tickets = new HashMap<>();
        BufferedReader br = getResourceReader(fileName);
        try (Scanner sc = new Scanner(br)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Reservation rs = reservations.get(d[1]);
                    if (rs == null) continue; 
                    
                    Ticket t = new Ticket(
                            Integer.parseInt(d[0]),
                            rs,
                            Double.parseDouble(d[2]),
                            Integer.parseInt(d[3])
                    );
                    tickets.put(t.getTicketId(), t);
                } catch (Exception e) {
                    System.out.println("Error parsing ticket row: " + line + " -> " + e.getMessage());
                }
            }
        } finally {
            try { br.close(); } catch (IOException e) { /* ignore close errors */ }
        }
        return tickets;
    }

    public static <T> void saveFile(String fileName, Collection<T> data, boolean append, boolean writeHeader, String header) {
        // All writes go to the writable data/ directory (not inside the JAR)
        ensureDataInitialized();
        Path path = resolveDataFile(fileName);
        boolean fileExists = false;
        try {
            fileExists = Files.exists(path) && Files.size(path) > 0;
        } catch (IOException e) {
            fileExists = Files.exists(path);
        }

        OpenOption[] opts;
        if (append) {
            opts = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.APPEND };
        } else {
            opts = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING };
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, opts)) {
            // 1. Write Header
            if (writeHeader && (!fileExists || !append)) {
                writer.write(header);
                writer.newLine();
            }
            // 2. Write Data
            for (T item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to data file: " + path.toAbsolutePath().toString());
            e.printStackTrace();
        }
    }
}