package flight.reservation;
import flight.reservation.flight.Flight;
import flight.reservation.flight.Schedule;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.order.FlightOrder;
import flight.reservation.payment.CreditCard;
import flight.reservation.plane.Helicopter;
import flight.reservation.plane.PassengerPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observer Pattern Tests")
public class ObserverPatternTest {
    private Schedule schedule;
    private Customer customer;

    @Nested
    @DisplayName("ScheduledFlight Observer behavior")
    class ScheduledFlightObserverTests {

        class TestObserver implements flight.reservation.flightObserver.FlightObserver {
            private final java.util.List<String> messages = new java.util.ArrayList<>();

            @Override
            public void update(ScheduledFlight flight, String message) {
                messages.add(message);
            }

            public java.util.List<String> getMessages() {
                return messages;
            }
        }

        class TestPassenger extends Passenger {
            private final java.util.List<String> messages = new java.util.ArrayList<>();

            public TestPassenger(String name) {
                super(name);
            }

            @Override
            public void update(ScheduledFlight flight, String message) {
                messages.add(message);
            }

            public java.util.List<String> getMessages() {
                return messages;
            }
        }

        private ScheduledFlight flight;
        private Airport a1;
        private Airport a2;

        @BeforeEach
        void setup() {
            a1 = new Airport("A1", "A1C", "Loc1");
            a2 = new Airport("A2", "A2C", "Loc2");
            flight = new ScheduledFlight(1, a1, a2, new Helicopter("H1"), Date.from(Instant.now()));
        }

        @Test
        @DisplayName("addObserver and notifyObserver deliver messages to observers")
        void addObserverAndNotify() {
            TestObserver obs = new TestObserver();
            flight.addObserver(obs);

            flight.notifyObserver("Test message");

            assertEquals(1, obs.getMessages().size());
            assertEquals("Test message", obs.getMessages().get(0));
        }

        @Test
        @DisplayName("addPassengers registers passengers as observers and notifies them")
        void addPassengersRegistersAndNotifies() {
            TestPassenger p1 = new TestPassenger("P1");
            TestPassenger p2 = new TestPassenger("P2");

            flight.addPassengers(java.util.Arrays.asList(p1, p2));

            assertEquals(1, p1.getMessages().size());
            assertEquals("You have been added to the flight.", p1.getMessages().get(0));
            assertEquals(1, p2.getMessages().size());
            assertEquals("You have been added to the flight.", p2.getMessages().get(0));
        }

        @Test
        @DisplayName("setCurrentPrice notifies observers with the new price message")
        void setCurrentPriceNotifiesObservers() {
            TestObserver obs = new TestObserver();
            flight.addObserver(obs);

            flight.setCurrentPrice(250.5);

            assertFalse(obs.getMessages().isEmpty());
            assertEquals("The price has changed to: 250.5", obs.getMessages().get(0));
        }
    }
}
