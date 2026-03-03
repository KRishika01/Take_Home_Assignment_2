package flight.reservation.flightObserver;
import flight.reservation.flight.ScheduledFlight;
public interface FlightObserver {
    public void update(ScheduledFlight flight, String message);
}
