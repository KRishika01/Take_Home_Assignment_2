package flight.reservation.flightSubject;

import flight.reservation.flightObserver.FlightObserver;

public interface FlightSubject {
    public void addObserver(FlightObserver observer);
    public void removeObserver(FlightObserver observer);
    public void notifyObserver(String message);
}
