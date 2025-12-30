package GUI;
import java.util.Map;
import flightManagment.Flight;

public class ReportGeneration {

	
	
	public static double generateFlightReport(Map<Integer, Flight> flights) {
		double totalOccupancy = 0.0;

		for (Flight flight : flights.values()) {
		    totalOccupancy += flight.getOccupancyRate();
		}
		double averageOccupancy = totalOccupancy / flights.size();
		

		return averageOccupancy;
	}
	
	/**
	 * Build a multiline per-flight occupancy report using the same occupancy logic.
	 */
	public static String generateFlightReportLines(Map<Integer, Flight> flights) {
		if (flights == null || flights.isEmpty()) return "No flights available to report.";
		StringBuilder sb = new StringBuilder();
		for (Flight flight : flights.values()) {
			double occupancy = flight.getOccupancyRate();
			// If occupancy is zero but plane has seat counts (e.g., seats reserved directly), try fallback
			if (occupancy == 0.0 && flight.getPlane() != null && flight.getPlane().getCapacity() > 0) {
				double fallback = (double) flight.getPlane().getFulledSeatsCount() * 100.0 / flight.getPlane().getCapacity();
				occupancy = fallback;
			}
			sb.append(String.format("Flight %d: %.2f%% occupied (%d/%d)\n", flight.getFlightNum(), occupancy,
						 (flight.getPlane() != null) ? flight.getPlane().getFulledSeatsCount() : 0,
						 (flight.getPlane() != null) ? flight.getPlane().getCapacity() : 0));
		}
		sb.append("Report complete. Processed " + flights.size() + " flights.");
		return sb.toString();
	}
		

}