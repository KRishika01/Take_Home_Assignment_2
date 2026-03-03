package flight.reservation;

import flight.reservation.flight.ScheduledFlight;
import flight.reservation.flightObserver.FlightObserver;

public class Passenger implements FlightObserver{

    private final String name;

    public Passenger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    @Override
    public void update(ScheduledFlight flight, String message){
        System.out.println("Notification for " + name + ": Flight" + flight.getNumber() + "- " + message);
    }

}
