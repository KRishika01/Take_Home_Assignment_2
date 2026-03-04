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

    public FlightOrder build() {
        validate();

        FlightOrder order = new FlightOrder(flights);
        order.setCustomer(this.customer);
        order.setPrice(this.price);
        List<Passenger> passengers = passengerNames.stream()
                .map(Passenger::new)
                .collect(Collectors.toList());
        order.setPassengers(passengers);

        flights.forEach(f -> f.addPassengers(passengers));

        return order;
    }

    private void validate() {
        if (customer == null || flights == null || passengerNames == null) {
            throw new IllegalStateException("Mandatory fields (customer, flights, passengers) are missing");
        }

        if (FlightOrder.getNoFlyList().contains(customer.getName())) {
            throw new IllegalStateException("Customer is on the no-fly list");
        }

        if (passengerNames.stream().anyMatch(passenger -> FlightOrder.getNoFlyList().contains(passenger))) {
            throw new IllegalStateException("One or more passengers are on the no-fly list");
        }

        for (ScheduledFlight flight : flights) {
            try {
                if (flight.getAvailableCapacity() < passengerNames.size()) {
                    throw new IllegalStateException("Insufficient capacity on flight " + flight.getNumber());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException("Error checking flight capacity", e);
            }
        }
    }
}
