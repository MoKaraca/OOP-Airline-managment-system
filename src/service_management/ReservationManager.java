package service_management;
import java.time.LocalDate;
import flightManagment.Flight;
import flightManagment.Plane;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import flightManagment.Seat;
import reservation_ticketing.Ticket;
public class ReservationManager {
	
	public static boolean canelReservation(String reservationId, Database database) {
    String key = String.valueOf(reservationId);
	Plane plane = database.reservations.get(key).getFlight().getPlane();
	plane.setEmptySeatsCount(plane.getEmptySeatsCount() + 1);

		// 1. Check if reservation exists in the main reservation map
		if (database.reservations.containsKey(key)) {
			
			// --- STEP 1: Remove the Reservation ---
			database.reservations.remove(key);
			
			// Save reservations.csv
			FileOp.saveFile("src/reservations.csv", database.reservations.values(), false, true,
							"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");

			// --- STEP 2: Find and Remove the Linked Ticket ---
			Integer ticketIdToRemove = null;

			if (database.tickets != null) {
				for (Ticket t : database.tickets.values()) {
					// Safety check: ensure the ticket has a valid reservation object
					if (t.getReservation() != null) {
						// Check if the code matches
						if (t.getReservation().getReservationCode().equals(key)) {
							ticketIdToRemove = t.getTicketId();
							break; // Found it
						}
					}
				}
			}

			// --- STEP 3: Remove Ticket & Save File ---
			if (ticketIdToRemove != null) {
				database.tickets.remove(ticketIdToRemove);
				
				// Save tickets.csv
				FileOp.saveFile("src/tickets.csv", database.tickets.values(), false, true,
								"ticketNum,reservationCode,price,baggaeWeight");
			}

			return true;
		}
		return false;




		
	}

	public static Reservation createReservation(Flight flight,Passenger passenger,Seat seat,LocalDate date,int seatLvl,Database database) {
		Reservation reservation = new Reservation(flight,passenger,seat,date);
		seat.setLevel(seatLvl);
		database.reservations.put(String.valueOf(reservation.getReservationCode()), reservation);
		FileOp.saveFile("src/reservations.csv", database.reservations.values(),false,true,
						"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");
		return reservation;
	}
	public static Ticket issueTicket(Reservation reservation,double price,int baggaeWeight,Database database) {
		Ticket ticket = new Ticket(reservation,price,baggaeWeight);
		database.tickets.put(ticket.getTicketId(), ticket);
		reservation.getFlight().addTicket(ticket);
		FileOp.saveFile("src/tickets.csv", database.tickets.values(),false,true,
						"ticketNum,reservationCode,price,baggaeWeight");
		return ticket;
	}
}