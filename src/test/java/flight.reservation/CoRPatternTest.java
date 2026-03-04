package flight.reservation;

import flight.reservation.flight.ScheduledFlight;
import flight.reservation.order.*;
import flight.reservation.plane.PassengerPlane;
import flight.reservation.plane.Aircraft;
import flight.reservation.Airport;
import flight.reservation.Customer;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CoRPatternTest {

    private ScheduledFlight createFlight() throws Exception {
        Aircraft aircraft = new PassengerPlane("A380");
        Airport dep = new Airport("JFK", "JFK", "New York");
        Airport arr = new Airport("LHR", "LHR", "London");

        return new ScheduledFlight(1, dep, arr, aircraft, new Date());
    }

    private OrderValidationHandler buildChain() {
        OrderValidationHandler chain = new CustomerNoFlyValidator();
        chain.setNext(new PassengerNoFlyValidator())
             .setNext(new FlightCapacityValidator());
        return chain;
    }

    @Test
    void testCustomerNoFlyFails() throws Exception {
        Customer customer = new Customer("Peter", "p@test.com");
        ScheduledFlight flight = createFlight();

        OrderValidationContext context =
            new OrderValidationContext(null, List.of("Alice"), List.of(flight), customer);

        assertFalse(buildChain().validate(context));
    }

    @Test
    void testPassengerNoFlyFails() throws Exception {
        Customer customer = new Customer("John", "j@test.com");
        ScheduledFlight flight = createFlight();

        OrderValidationContext context =
            new OrderValidationContext(null, List.of("Johannes"), List.of(flight), customer);

        assertFalse(buildChain().validate(context));
    }

    @Test
    void testValidationSuccess() throws Exception {
        Customer customer = new Customer("John", "j@test.com");
        ScheduledFlight flight = createFlight();

        OrderValidationContext context =
            new OrderValidationContext(null, List.of("Alice", "Bob"), List.of(flight), customer);

        assertTrue(buildChain().validate(context));
    }
}