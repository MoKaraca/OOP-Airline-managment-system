package flightManagment;
import java.time.*;
public class route extends Flight{
	private LocalDateTime departure;
	private LocalDateTime arrival;
	
	public route(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour,
			Duration duration,Plane plane) {
		super(flightNum, departurePlace, arrivalPlace, date, hour, duration,plane);
		this.departure = LocalDateTime.of(date, hour);
		this.arrival = departure.plus(duration);
	}

	@Override
	public String toString() {
		return "route [departure=" + departure + ", arrival=" + arrival + "]";
	}

	public LocalDateTime getDeparture() {
		return departure;
	}

	public void setDeparture(LocalDateTime departure) {
		this.departure = departure;
	}

	public LocalDateTime getArrival() {
		return arrival;
	}

	public void setArrival(LocalDateTime arrival) {
		this.arrival = arrival;
	}
	
	
	
	
	

	
	
}
