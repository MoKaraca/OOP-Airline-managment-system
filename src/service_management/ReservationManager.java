package service_management;

import flightManagment.Flight;
import flightManagment.Plane;
import flightManagment.Seat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;

public class ReservationManager {
	
	public static boolean canelReservation(String reservationId, Database database) {
        String key = String.valueOf(reservationId);
        
        // Check if reservation exists
        if (database.getReservations().containsKey(key)) {
            
            // 1. Update Plane Capacity (Free up the seat)
            Reservation res = database.getReservations().get(key);
            if (res.getFlight() != null && res.getFlight().getPlane() != null) {
                Plane plane = res.getFlight().getPlane();
                // Logic: Increasing empty seats. 
                // Note: ideally the Seat object's reservedStatus should also be set to false here,
                // but your Seat.java handles counters inside setReservedStatus too. 
                // To be safe and consistent with your Seat logic:
                if (res.getSeat() != null) {
                    res.getSeat().setReservedStatus(false, plane); 
                }
            }

            // 2. Remove the Reservation from Map
            database.getReservations().remove(key);
            FileOp.saveFile("src/reservations.csv", database.getReservations().values(), false, true,
                            "reservationCode,flightNum,passengerId,seatNum,dateOfReservation");

            // 3. Find and Remove the Linked Ticket (Cascading Delete)
            Integer ticketIdToRemove = null;
            if (database.getTickets() != null) {
                for (Ticket t : database.getTickets().values()) {
                    if (t.getReservation() != null && t.getReservation().getReservationCode().equals(key)) {
                        ticketIdToRemove = t.getTicketId();
                        break; 
                    }
                }
            }

            // 4. Save updated Tickets file
            if (ticketIdToRemove != null) {
                database.getTickets().remove(ticketIdToRemove);
                FileOp.saveFile("src/tickets.csv", database.getTickets().values(), false, true,
                                "ticketNum,reservationCode,price,baggaeWeight");
            }

            return true;
        }
        return false;
	}

	public static Reservation createReservation(Flight flight, Passenger passenger, Seat seat, LocalDate date, int seatLvl, Database database) {
		Reservation reservation = new Reservation(flight, passenger, seat, date);
		seat.setLevel(seatLvl);
		database.getReservations().put(String.valueOf(reservation.getReservationCode()), reservation);
		
		FileOp.saveFile("src/reservations.csv", database.getReservations().values(), false, true,
						"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");
		return reservation;
	}

	public static Ticket issueTicket(Reservation reservation, double price, int baggaeWeight, Database database) {
		Ticket ticket = new Ticket(reservation, price, baggaeWeight);
		database.getTickets().put(ticket.getTicketId(), ticket);
		reservation.getFlight().addTicket(ticket);
		
		FileOp.saveFile("src/tickets.csv", database.getTickets().values(), false, true,
						"ticketNum,reservationCode,price,baggaeWeight");
		return ticket;
	}

	/**
	 * Search reservations by reservation ID
	 */
	public static List<Reservation> searchByReservationId(String reservationId, Database database) {
		List<Reservation> results = new ArrayList<>();
		if (database.getReservations() != null) {
			for (Reservation r : database.getReservations().values()) {
				if (r.getReservationCode() != null && r.getReservationCode().toLowerCase().contains(reservationId.toLowerCase())) {
					results.add(r);
				}
			}
		}
		return results;
	}

	/**
	 * Search reservations by passenger name (first name or surname)
	 */
	public static List<Reservation> searchByPassengerName(String name, Database database) {
		List<Reservation> results = new ArrayList<>();
		if (database.getReservations() != null) {
			for (Reservation r : database.getReservations().values()) {
				if (r.getPassenger() != null) {
					String fullName = r.getPassenger().getName() + " " + r.getPassenger().getSurname();
					if (fullName.toLowerCase().contains(name.toLowerCase())) {
						results.add(r);
					}
				}
			}
		}
		return results;
	}

	/**
	 * Search reservations by date of reservation
	 */
	public static List<Reservation> searchByDate(LocalDate date, Database database) {
		List<Reservation> results = new ArrayList<>();
		if (database.getReservations() != null) {
			for (Reservation r : database.getReservations().values()) {
				if (r.getDateOfReservation() != null && r.getDateOfReservation().equals(date)) {
					results.add(r);
				}
			}
		}
		return results;
	}

	/**
	 * Generic search that filters reservations by all criteria at once
	 * Any field can be null to skip filtering on that field
	 */
	public static List<Reservation> searchReservations(String reservationId, String passengerName, LocalDate date, Database database) {
		List<Reservation> results = new ArrayList<>();
		
		if (database.getReservations() != null) {
			for (Reservation r : database.getReservations().values()) {
				boolean matches = true;
				
				// Check reservation ID
				if (reservationId != null && !reservationId.trim().isEmpty()) {
					if (r.getReservationCode() == null || !r.getReservationCode().toLowerCase().contains(reservationId.toLowerCase())) {
						matches = false;
					}
				}
				
				// Check passenger name
				if (matches && passengerName != null && !passengerName.trim().isEmpty()) {
					if (r.getPassenger() == null) {
						matches = false;
					} else {
						String fullName = r.getPassenger().getName() + " " + r.getPassenger().getSurname();
						if (!fullName.toLowerCase().contains(passengerName.toLowerCase())) {
							matches = false;
						}
					}
				}
				
				// Check date
				if (matches && date != null) {
					if (r.getDateOfReservation() == null || !r.getDateOfReservation().equals(date)) {
						matches = false;
					}
				}
				
				if (matches) {
					results.add(r);
				}
			}
		}
		
		return results;
	}
}