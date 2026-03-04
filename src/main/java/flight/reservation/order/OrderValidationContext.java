package flight.reservation.order;
import flight.reservation.Customer;
import flight.reservation.flight.ScheduledFlight;
import java.util.List;

public class OrderValidationContext {
    private final FlightOrder order;
    private final List<String> passengerNames;
    private final List<ScheduledFlight> flights;
    private final Customer customer;
    public OrderValidationContext(FlightOrder order, List<String> passengernames,List<ScheduledFlight> flights, Customer customer){
        this.order = order;
        this.passengerNames = passengernames;
        this.customer = customer;
        this.flights = flights;
    }
    public FlightOrder getOrder(){
        return order;
    }
    public List<String> getPassengers(){
        return passengerNames;
    }
    public List<ScheduledFlight> getFlights(){
        return flights;
    }
    public Customer getCustomer(){
        return customer;
    }

}
