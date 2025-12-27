/*
	private int planeID;
	private String planeModel;
	private int capacity;
	private String seatM[][];
	
		public Plane(int planeId,String planeModel,int capacity,int seatAmount) {
			this.planeID = planeId;
			this.planeModel = planeModel;
			this.capacity = capacity;
			this.seatM = new String[seatAmount][seatAmount];
		}
		
		private int flightNum;
		private String departurePlace;
		private String arrivalPlace;
		private LocalDate date;
		private LocalTime hour; // decimal cant be more than 0.59
		private Duration duration; // decimal cant be more than 0.59 // brings back duration
		
		//LocalTime time = LocalTime.of(14, 30);
		//LocalDateTime dt = LocalDateTime.of(2026, 1, 9, 14, 30);


		public Flight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration) {
			this.flightNum = flightNum;
			this.departurePlace = departurePlace;
			this.arrivalPlace = arrivalPlace;
			this.date = date;
			this.hour = hour;
			this.duration = duration;
			
		}
		
		private LocalDateTime departure;
		private LocalDateTime arrival;
		
		public route(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour,
				Duration duration) {
			super(flightNum, departurePlace, arrivalPlace, date, hour, duration);
			this.departure = LocalDateTime.of(date, hour);
			this.arrival = departure.plus(duration);
		}
		
		private double weight;
		Ticket ticket;
			public Baggage(double weight,Ticket ticket) {
				setWeight(weight);
				this.ticket = ticket;
			}
			
			private int passengerId;
			private String name;
			private String surname;
			private long contactNum;
			
			public Passenger(int passengerId,String name,String surname,long contactNum) {
				this.passengerId = passengerId;
				this.name = name;
				this.surname = surname;
				this.contactNum = contactNum;
			}
			
			private String reservationCode;
			private Flight flight;
			private Passenger passenger;
			private Seat seat;
			private LocalDate dateOfReservation;
			
				public Reservation(String reservationCode,Flight flight,Passenger passenger,Seat seat,LocalDate dateOfReservation) {
					this.reservationCode = reservationCode;
					this.flight = flight;
					this.passenger = passenger;
					this.seat = seat;
					this.dateOfReservation = dateOfReservation;
				}
				
				private int ticketId;
				private Reservation reservation;
				private double price;
				private int baggageAllowance;
				
					public Ticket(int ticketId,Reservation reservation,double price,int baggageAllowance) {
						this.ticketId = ticketId;
						this.reservation = reservation;
						this.price = price;
						this.baggageAllowance = baggageAllowance;
					}
*/
