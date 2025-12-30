package reservation_ticketing;

import flightManagment.Flight;
import service_management.SeatManager;

public class Passenger implements Runnable {
	private long passengerId;
	private String name;
	private String surname;
	private long contactNum;
	private  boolean SyncType; // true for method level false for block level
	private Flight flight;
	
	public Passenger(long passengerId,String name,String surname,long contactNum) {
		this.passengerId = passengerId;
		this.name = name;
		this.surname = surname;
		this.contactNum = contactNum;
	}
	
	public boolean isSyncType() {
		return SyncType;
	}

	public void setSyncType(boolean syncType) {
		SyncType = syncType;
	}

	public Passenger(boolean SyncType,long passengerId,String name,String surname,long contactNum,Flight flight) {
		this.SyncType = SyncType;
		this.passengerId = passengerId;
		this.name = name;
		this.surname = surname;
		this.contactNum = contactNum;
		this.flight = flight;
	}

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		 this.passengerId = passengerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public long getContactNum() {
		return contactNum;
	}

	public void setContactNum(long contactNum) {
		this.contactNum = contactNum;
	}
	
	public String toString() {
	    return getPassengerId() + "," +
	           getName() + "," +
	           getSurname() + "," +
	           getContactNum();
	}

	@Override
	public void run() {
		if (SyncType) {
            SeatManager.reserveSeatSafe(flight);
        } else {
            SeatManager.reserveSeatUnsafe(flight);
        }
	}

	
}
