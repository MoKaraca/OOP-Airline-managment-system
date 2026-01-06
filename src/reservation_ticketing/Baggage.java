package reservation_ticketing;
import java.util.*;
public class Baggage {
	private double weight;
	private Ticket ticket;
		public Baggage(double weight,Ticket ticket) {
			setWeight(weight);
			this.ticket = ticket;
		}
		public double getWeight() {
			return weight;
		}
		public void setWeight(double weight) {
			while(this.weight > ticket.getBaggageAllowance()) {
				System.out.println("you exceeded your allowed baggage allowance remove items and try again.");
				Scanner sn = new Scanner(System.in);
				System.out.println("Place your baggage on the scale (enter a weight)");
				this.weight = weight;
				sn.close();
				
			}
			
			if(this.weight <= ticket.getBaggageAllowance()) {
				this.weight = weight;
			}
			System.out.println("weight was set");
			
		}
	
		
}
