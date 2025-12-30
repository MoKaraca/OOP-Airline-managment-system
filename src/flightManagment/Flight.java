package flightManagment;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import service_management.FlightManager;
import java.util.*;

import reservation_ticketing.Ticket;

import java.io.*;
public class Flight extends FlightManager {
	private int flightNum;
	private String departurePlace;
	private String arrivalPlace;
	private LocalDate date;
	private LocalTime hour; // decimal cant be more than 0.59
	private Duration duration; // decimal cant be more than 0.59 // brings back duration
	private Plane plane;
	private Map<Integer, Ticket> ticketList;
	private double OccupancyRate;
	//LocalTime time = LocalTime.of(14, 30);
	//LocalDateTime dt = LocalDateTime.of(2026, 1, 9, 14, 30);


	public Flight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration,Plane plane) {
		this.flightNum = flightNum;
		this.departurePlace = departurePlace;
		this.arrivalPlace = arrivalPlace;
		this.date = date;
		this.hour = hour;
		this.duration = duration;
		this.plane = plane;
		ticketList = new HashMap<>();
		this.OccupancyRate = 0.0;
		
	}
	public void addTicket(Ticket ticket) {
	    ticketList.put(ticket.getTicketId(), ticket);
	}
	public Map<Integer, Ticket> getTicketList(){
	    return Collections.unmodifiableMap(ticketList); // passing a controlled map instead of raw map safety reasons not needed

	}
	public Plane getPlane() {
		return plane;
	}
	public double getOccupancyRate() {
		OccupancyRate = ((double)ticketList.size() / plane.getCapacity()) * 100;
		return OccupancyRate;
	}
	public void setOccupancyRate(double occupancyRate) {
		OccupancyRate = occupancyRate;
	}
	public void setPlane(Plane plane) {
		this.plane = plane;
	}
	static public Flight getFlightWithId(int flightId) throws FileNotFoundException {

	   

	    return null;
	}

	public int getFlightNum() {
		return flightNum;
	}

	public void setFlightNum(int flightNum) {
		this.flightNum = flightNum;
	}

	public String getDeparturePlace() {
		return departurePlace;
	}

	public void setDeparturePlace(String departurePlace) {
		this.departurePlace = departurePlace;
	}

	public String getArrivalPlace() {
		return arrivalPlace;
	}

	public void setArrivalPlace(String arrivalPlace) {
		this.arrivalPlace = arrivalPlace;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getHour() {
		return hour;
	}

	public void setHour(LocalTime hour) {
		this.hour = hour;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	
	public String toString() {
	    return getFlightNum() + "," +
	           getDeparturePlace() + "," +
	           getArrivalPlace() + "," +
	           getDate() + "," +
	           getHour() + "," +
	           getDuration() +","+ getPlane().getPlaneID();
	}

	
}
