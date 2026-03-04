package flight.reservation.order;

import flight.reservation.Customer;
import flight.reservation.Passenger;
import flight.reservation.flight.ScheduledFlight;

import java.util.List;
import java.util.stream.Collectors;

public class FlightOrderBuilder {

    private Customer customer;
    private List<String> passengerNames;
    private List<ScheduledFlight> flights;
    private double price;

    public FlightOrderBuilder forCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public FlightOrderBuilder withPassengers(List<String> passengerNames) {
        this.passengerNames = passengerNames;
        return this;
    }

    public FlightOrderBuilder onFlights(List<ScheduledFlight> flights) {
        this.flights = flights;
        return this;
    }

    public FlightOrderBuilder atPrice(double price) {
        this.price = price;
        return this;
    }

    private void validate() {
        if (customer == null || passengerNames == null || flights == null) {
            throw new IllegalStateException("Customer, passengers, and flights must all be set before building an order.");
        }
        if (FlightOrder.getNoFlyList().contains(customer.getName())) {
            throw new IllegalStateException("Customer is on the no-fly list.");
        }
        boolean passengerOnNoFlyList = passengerNames.stream()
                .anyMatch(name -> FlightOrder.getNoFlyList().contains(name));
        if (passengerOnNoFlyList) {
            throw new IllegalStateException("One or more passengers are on the no-fly list.");
        }
        boolean capacityOk = flights.stream().allMatch(scheduledFlight -> {
            try {
                return scheduledFlight.getAvailableCapacity() >= passengerNames.size();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return false;
            }
        });
        if (!capacityOk) {
            throw new IllegalStateException("One or more flights do not have enough available capacity.");
        }
    }

    public FlightOrder build() {
        validate();

        List<Passenger> passengers = passengerNames.stream()
                .map(Passenger::new)
                .collect(Collectors.toList());

        FlightOrder order = new FlightOrder(flights);
        order.customer = customer;
        order.price = price;
        order.passengers = passengers;

        flights.forEach(sf -> sf.addPassengers(passengers));

        return order;
    }
}
