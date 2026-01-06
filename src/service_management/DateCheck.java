package service_management;

import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import flightManagment.Flight;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;

public class DateCheck {

    /**
     * Removes flights that have already completed (flight date is in the past or today).
     * Also removes all associated reservations and tickets from the database.
     * Thread-safe: creates a list of flights to remove before modifying the maps.
     * Saves all updated CSV files to persist changes.
     * 
     * @param date The current date to compare against (typically LocalDate.now())
     * @param database The database containing flights, reservations, and tickets
     */
    public static void isDateInPast(LocalDate date, Database database) {
        if (database == null || database.getFlights() == null || database.getFlights().isEmpty()) {
            return;
        }
        
        Map<Integer, Flight> flights = database.getFlights();
        Map<String, Reservation> reservations = database.getReservations();
        Map<Integer, Ticket> tickets = database.getTickets();
        
        // Create separate lists to avoid ConcurrentModificationException
        List<Integer> flightsToRemove = new ArrayList<>();
        List<String> reservationsToRemove = new ArrayList<>();
        List<Integer> ticketsToRemove = new ArrayList<>();
        
        // Step 1: Find all flights that have finished (date is before or equal to today)
        for (Flight flight : flights.values()) {
            if (flight.getDate() != null && (flight.getDate().isBefore(date) || flight.getDate().isEqual(date))) {
                flightsToRemove.add(flight.getFlightNum());
                System.out.println("[DateCheck] Marking for removal - Flight: " + flight.getFlightNum() + " (Date: " + flight.getDate() + ")");
            }
        }
        
        // If no flights to remove, return early
        if (flightsToRemove.isEmpty()) {
            return;
        }
        
        // Step 2: Find all reservations and tickets associated with the expired flights
        if (reservations != null && !reservations.isEmpty()) {
            for (Reservation res : reservations.values()) {
                if (res.getFlight() != null && flightsToRemove.contains(res.getFlight().getFlightNum())) {
                    reservationsToRemove.add(res.getReservationCode());
                    System.out.println("[DateCheck] Removing associated reservation: " + res.getReservationCode());
                }
            }
        }
        
        if (tickets != null && !tickets.isEmpty()) {
            for (Ticket ticket : tickets.values()) {
                if (ticket.getReservation() != null && reservationsToRemove.contains(ticket.getReservation().getReservationCode())) {
                    ticketsToRemove.add(ticket.getTicketId());
                    System.out.println("[DateCheck] Removing associated ticket: " + ticket.getTicketId());
                }
            }
        }
        
        // Step 3: Remove all identified flights
        for (Integer flightNum : flightsToRemove) {
            flights.remove(flightNum);
        }
        
        // Step 4: Remove all associated reservations
        for (String reservationId : reservationsToRemove) {
            reservations.remove(reservationId);
        }
        
        // Step 5: Remove all associated tickets
        for (Integer ticketId : ticketsToRemove) {
            tickets.remove(ticketId);
        }
        
        // Step 6: Save all updated data to CSV files
        System.out.println("[DateCheck] Removed " + flightsToRemove.size() + " flight(s), " + 
                          reservationsToRemove.size() + " reservation(s), and " + 
                          ticketsToRemove.size() + " ticket(s). Saving to CSV...");
        saveAllData(flights, reservations, tickets);
    }
    
    /**
     * Saves all updated data (flights, reservations, and tickets) to their respective CSV files.
     * This ensures database consistency across all related data.
     */
    private static void saveAllData(Map<Integer, Flight> flights, 
                                    Map<String, Reservation> reservations, 
                                    Map<Integer, Ticket> tickets) {
        try {
            // Save updated flights to CSV
            FileOp.saveFile(
                "flights.csv",
                flights.values(),
                false,  // Don't append, overwrite
                true,   // Write header
                "flightNum,departure,arrival,date,time,duration,planeId"
            );
            System.out.println("[DateCheck] ✓ Flights CSV saved successfully.");
            
            // Save updated reservations to CSV
            FileOp.saveFile(
                "reservations.csv",
                reservations.values(),
                false,  // Don't append, overwrite
                true,   // Write header
                "reservationId,flightNum,passengerId,seatNumber,reservationDate"
            );
            System.out.println("[DateCheck] ✓ Reservations CSV saved successfully.");
            
            // Save updated tickets to CSV
            FileOp.saveFile(
                "tickets.csv",
                tickets.values(),
                false,  // Don't append, overwrite
                true,   // Write header
                "ticketId,reservationId,price,status"
            );
            System.out.println("[DateCheck] ✓ Tickets CSV saved successfully.");
            
        } catch (Exception e) {
            System.out.println("[DateCheck] ✗ Error saving CSV files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
