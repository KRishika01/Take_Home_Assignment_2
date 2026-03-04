package flight.reservation;

import flight.reservation.flight.ScheduledFlight;
import flight.reservation.order.CustomerNoFlyValidator;
import flight.reservation.order.FlightCapacityValidator;
import flight.reservation.order.FlightOrder;
import flight.reservation.order.FlightOrderBuilder;
import flight.reservation.order.Order;
import flight.reservation.order.OrderValidationContext;
import flight.reservation.order.OrderValidationHandler;
import flight.reservation.order.PassengerNoFlyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Customer {

    private String email;
    private String name;
    private List<Order> orders;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
        this.orders = new ArrayList<>();
    }

    public FlightOrder createOrder(List<String> passengerNames, List<ScheduledFlight> flights, double price) {
        FlightOrder order = new FlightOrderBuilder()
                .forCustomer(this)
                .withPassengers(passengerNames)
                .onFlights(flights)
                .atPrice(price)
                .build();
        orders.add(order);
        return order;
    }

<<<<<<< HEAD
    // private boolean isOrderValid(List<String> passengerNames,
    // List<ScheduledFlight> flights) {
    // boolean valid = true;
    // valid = valid && !FlightOrder.getNoFlyList().contains(this.getName());
    // valid = valid && passengerNames.stream().noneMatch(passenger ->
    // FlightOrder.getNoFlyList().contains(passenger));
    // valid = valid && flights.stream().allMatch(scheduledFlight -> {
    // try {
    // return scheduledFlight.getAvailableCapacity() >= passengerNames.size();
    // } catch (NoSuchFieldException e) {
    // e.printStackTrace();
    // return false;
    // }
    // });
    // return valid;
    // }
    private boolean isOrderValid(List<String> passengerNames,
            List<ScheduledFlight> flights) {

        OrderValidationContext context = new OrderValidationContext(
                null, // FlightOrder not created yet
                passengerNames,
                flights,
                this);

        OrderValidationHandler chain = new CustomerNoFlyValidator();

        chain
                .setNext(new PassengerNoFlyValidator())
                .setNext(new FlightCapacityValidator());

        return chain.validate(context);
    }

=======
>>>>>>> 885be0a37c9f499a9d9f74ef62825af41608495e
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
