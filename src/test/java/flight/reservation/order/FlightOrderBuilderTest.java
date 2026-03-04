package flight.reservation.order;

import flight.reservation.Airport;
import flight.reservation.Customer;
import flight.reservation.flight.Flight;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.plane.Helicopter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FlightOrderBuilderTest {

    private Customer customer;
    private ScheduledFlight flight;

    @BeforeEach
    public void setup() {
        customer = new Customer("Test User", "test@test.com");
        Airport start = new Airport("Start", "ST", "Location A");
        Airport end = new Airport("End", "EN", "Location B");
        Flight f = new Flight(1, start, end, new Helicopter("H1"));
        flight = new ScheduledFlight(1, start, end, f.getAircraft(), new Date());
    }

    @Test
    public void testSuccessfulBuild() {
        FlightOrder order = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(Arrays.asList("Alice", "Bob"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0)
                .build();

        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals(100.0, order.getPrice());
        assertEquals(2, order.getPassengers().size());
        assertEquals(1, order.getScheduledFlights().size());
    }

    @Test
    public void testMissingMandatoryFields() {
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer);
        
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void testNoFlyListCustomer() {
        Customer badCustomer = new Customer("Peter", "peter@bad.com");
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(badCustomer)
                .withPassengers(Arrays.asList("Alice"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void testNoFlyListPassenger() {
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(Arrays.asList("Johannes"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void testInsufficientCapacity() throws NoSuchFieldException {
        // Helicopter H1 has capacity 4. Add 4 passengers.
        flight.addPassengers(Arrays.asList(new flight.reservation.Passenger("P1"), 
                                          new flight.reservation.Passenger("P2"),
                                          new flight.reservation.Passenger("P3"),
                                          new flight.reservation.Passenger("P4")));
        
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(Arrays.asList("Alice"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0);

        assertThrows(IllegalStateException.class, builder::build);
    }
}
