package flight.reservation.order;

import flight.reservation.Airport;
import flight.reservation.Customer;
import flight.reservation.Passenger;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.plane.PassengerPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightOrderBuilderTest {

    private Customer customer;
    private ScheduledFlight flight;
    private Airport departure;
    private Airport arrival;

    @BeforeEach
    public void setUp() {
        departure = new Airport("Berlin Airport", "BER", "Berlin, Berlin");
        arrival = new Airport("Frankfurt Airport", "FRA", "Frankfurt, Hesse");
        customer = new Customer("Alice", "alice@example.com");
        flight = new ScheduledFlight(1, departure, arrival, new PassengerPlane("Antonov AN2"), new Date());
    }

    @Test
    public void testSuccessfulBuild() {
        FlightOrder order = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(Arrays.asList("Bob", "Charlie"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0)
                .build();

        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals(100.0, order.getPrice());
        assertEquals(2, order.getPassengers().size());
        assertEquals("Bob", order.getPassengers().get(0).getName());
        assertEquals("Charlie", order.getPassengers().get(1).getName());
    }

    @Test
    public void testMissingMandatoryFields() {
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer);
        // Missing flights and passengers
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void testNoFlyListCustomer() {
        Customer noFlyCustomer = new Customer("Peter", "peter@example.com");
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(noFlyCustomer)
                .withPassengers(Arrays.asList("Bob"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Customer is on the no-fly list", exception.getMessage());
    }

    @Test
    public void testNoFlyListPassenger() {
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(Arrays.asList("Johannes"))
                .onFlights(Arrays.asList(flight))
                .atPrice(100.0);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("One or more passengers are on the no-fly list", exception.getMessage());
    }

    @Test
    public void testInsufficientCapacity() {
        // Antonov AN2 has capacity of 15
        List<String> sixteenPassengers = Arrays.asList(
                "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", 
                "P9", "P10", "P11", "P12", "P13", "P14", "P15", "P16");
        
        FlightOrderBuilder builder = new FlightOrderBuilder()
                .forCustomer(customer)
                .withPassengers(sixteenPassengers)
                .onFlights(Arrays.asList(flight))
                .atPrice(1000.0);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("Insufficient capacity"));
    }
}
