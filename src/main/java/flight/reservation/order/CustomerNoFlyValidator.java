package flight.reservation.order;

public class CustomerNoFlyValidator extends OrderValidationHandler{
    @Override
    protected boolean check(OrderValidationContext context) {
        return !FlightOrder.getNoFlyList()
                .contains(context.getCustomer().getName());
    }
}
