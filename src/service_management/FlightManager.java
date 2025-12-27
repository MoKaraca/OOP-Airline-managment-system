package service_management;
import flightManagment.*;
import java.util.*;
import reservation_ticketing.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightManager {
	
	public FlightManager () {
		
	}
	//first functions create, update and delete flights
	
	public void createFlight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration) {
			//extract data from gui and create flites make sure to save the file before exiting program
		//system needs to set a plane for the user not the user choose 
			
		/*
		 Plane plane = planeService.assignPlane(route, date, expectedPassengers);
		
		Flight flight = new Flight(
		    flightNum,
		    from,
		    to,
		    date,
		    time,
		    duration,
		    plane
		);
 
	  
	 
		 public Plane assignPlane(String route, int expectedPassengers) {
		        for (Plane p : planes.values()) {
		            if (p.getCapacity() >= expectedPassengers && p.isAvailable()) {
		                return p;
		            }
		        }
		*/
		
		
		
	}
	public Database createFlight(String filePath) throws FileNotFoundException {
		//call the file io class functions and create the flights with the input from the csv files
		//since the FileOp class is a helper class not a object type class its functions should be static as well(it has an empty constructor)
		Database data = new Database();
		
		data.planes = FileOp.getPlaneData("/Users/mo/Desktop/AirlineManagment/src/planes.csv");
		data.flights = FileOp.getFlightData("/Users/mo/Desktop/AirlineManagment/src/flights.csv",data.planes);
		data.passengers = FileOp.getPassengerData("/Users/mo/Desktop/AirlineManagment/src/passengers.csv");
		data.reservations = FileOp.getReservationData("/Users/mo/Desktop/AirlineManagment/src/reservations.csv",data.flights,data.passengers);
		data.tickets = FileOp.getTicketData("/Users/mo/Desktop/AirlineManagment/src/tickets.csv",data.reservations,data.flights);
		return data;
		
	}
	public Flight updateFlight(Flight flight) {
		
		return flight;
	}
	public boolean deleteFlight(int flightNum) {
		
		return true;
	}
	 

}