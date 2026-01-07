package service_management;

import flightManagment.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightManager {
	
	// Placeholder for extraction logic if needed separately
	public static void createFlight(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour, Duration duration) {
		// Logic handled in overloaded method below
	}

	public static Database extractFileData() throws FileNotFoundException {
		Database data = new Database();
		
		// Using relative paths ensures portability
		data.setPlanes(FileOp.getPlaneData("planes.csv"));
		data.setFlights(FileOp.getFlightData("flights.csv", data.getPlanes()));
		data.setPassengers(FileOp.getPassengerData("passengers.csv"));
		data.setReservations(FileOp.getReservationData("reservations.csv", data.getFlights(), data.getPassengers()));
		data.setTickets(FileOp.getTicketData("tickets.csv", data.getReservations(), data.getFlights()));
		// try loading staff data (optional)
		try {
			data.setStaffMembers(FileOp.getStaffData("staff.csv"));
		} catch (Exception e) {
			System.out.println("No staff data loaded: " + e.getMessage());
		}
		return data;
	}

	public static Flight createFlight(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour, Duration duration, Plane plane, Database db) {
		Flight flight = new Flight(flightNum, departurePlace, arrivalPlace, date, hour, duration, plane);
		db.getFlights().put(flightNum, flight);
		FileOp.saveFile("flights.csv", db.getFlights().values(), false, true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return flight;
	}

	public static Plane createPlane(int planeId, String model, int capacity, int rows, Database db) {
		Plane plane = new Plane(planeId, model, capacity, rows);
		db.getPlanes().put(planeId, plane);
		FileOp.saveFile("planes.csv", db.getPlanes().values(), false, true,
				        "planeId,model,capacity,rows");
		return plane;
	}

	public static Flight updateFlight(Database db, Flight flight) {
		if (db.getFlights().containsKey(flight.getFlightNum())) {
			db.getFlights().put(flight.getFlightNum(), flight);
			FileOp.saveFile("flights.csv", db.getFlights().values(), false, true,
					        "flightNum,departure,arrival,date,time,duration,planeId");
		}
		return flight;
	}

	public static boolean deleteFlight(Database db, int flightNum) {
		if (!db.getFlights().containsKey(flightNum)) {
			return false;
		}
		db.getFlights().remove(flightNum);
		FileOp.saveFile("flights.csv", db.getFlights().values(), false, true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return true;
	}
}