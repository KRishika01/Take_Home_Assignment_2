package flight.reservation.order;

public class PassengerNoFlyValidator extends OrderValidationHandler {
    protected boolean check(OrderValidationContext context) {
        return context.getPassengers()
                .stream()
                .noneMatch(name -> FlightOrder.getNoFlyList().contains(name));
    }

}
