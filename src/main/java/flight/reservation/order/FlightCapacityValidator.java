package flight.reservation.order;

public class FlightCapacityValidator extends OrderValidationHandler {
    @Override
    protected boolean check(OrderValidationContext context) {
        return context.getFlights()
                .stream()
                .allMatch(flight -> {
                    try {
                        return flight.getAvailableCapacity() >= context.getPassengers().size();
                    } catch (NoSuchFieldException e) {
                        return false;
                    }
                });
    }
}
