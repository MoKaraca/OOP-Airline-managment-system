package service_management;
import flightManagment.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightManager {
	
	
	
	public static Database extractFileData() throws FileNotFoundException {
		//call the file io class functions and create the flights with the input from the csv files
		//since the FileOp class is a helper class not a object type class its functions should be static as well(it has an empty constructor)
		Database data = new Database();
		
		data.setPlanes(FileOp.getPlaneData("/Users/mo/Desktop/AirlineManagment/src/planes.csv"));
		data.setFlights(FileOp.getFlightData("/Users/mo/Desktop/AirlineManagment/src/flights.csv",data.getPlanes()));
		data.setPassengers(FileOp.getPassengerData("/Users/mo/Desktop/AirlineManagment/src/passengers.csv"));
		data.setReservations(FileOp.getReservationData("/Users/mo/Desktop/AirlineManagment/src/reservations.csv",data.getFlights(),data.getPassengers()));
		data.setTickets(FileOp.getTicketData("/Users/mo/Desktop/AirlineManagment/src/tickets.csv",data.getReservations(),data.getFlights()));
		return data;
		
	}
	public static Flight createFlight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration,Plane plane,Database db) {
		Flight flight = new Flight(flightNum,departurePlace,arrivalPlace,date,hour,duration,plane);
		db.getFlights().put(flightNum, flight);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.getFlights().values(),false,true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return flight;
	}
	public static Plane createPlane(int planeId,String model,int capacity,int rows,Database db) {
		Plane plane = new Plane(planeId,model,capacity,rows);
		db.getPlanes().put(planeId, plane);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/planes.csv", db.getPlanes().values(),false,true,
				        "planeId,model,capacity,rows");

		return plane;
	}
	public static Flight updateFlight(Database db,Flight flight) {
		if(db.getFlights().containsKey(flight.getFlightNum())) {
			db.getFlights().put(flight.getFlightNum(), flight);
			FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.getFlights().values(),false,true,
					        "flightNum,departure,arrival,date,time,duration,planeId");
		}
		return flight;
		
	}
	public static boolean deleteFlight(Database db,int flightNum) {
		//delete flight from db and update the csv file
		//return true if deleted false if not found
		if(!db.getFlights().containsKey(flightNum)) {
			return false;
		}
		db.getFlights().remove(flightNum);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.getFlights().values(),false,true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return true;
	}
	 

}