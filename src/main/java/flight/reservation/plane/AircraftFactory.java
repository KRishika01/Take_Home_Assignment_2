package flight.reservation.plane;

public class AircraftFactory {
    public Aircraft createAircraft(String model) {
        switch(model) {
            case "H1":
            case "H2":
                return new Helicopter(model);
            
            case "HypaHype":
                return new PassengerDrone(model);
            
            case "A380":
            case "A350":
            case "Embraer 190":
            case "Antonov AN2":
                return new PassengerPlane(model);
            
            default:
                throw new IllegalArgumentException(String.format("Model type '%s' is not recognized", model));
        }
    }
}