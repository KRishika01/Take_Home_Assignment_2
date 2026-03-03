package flight.reservation;

import flight.reservation.flight.Flight;
import flight.reservation.flight.Schedule;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.order.FlightOrder;
import flight.reservation.payment.*;
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

@DisplayName("Strategy Pattern Tests - Core 10 Tests")
public class StrategyPatternTest {

    @Nested
    @DisplayName("1. Payment Strategy Interface Tests")
    class PaymentStrategyInterfaceTests {

        private Schedule schedule;
        private Customer customer;
        private FlightOrder order;

        @BeforeEach
        void setup() {
            schedule = new Schedule();
            schedule.clear();
            
            Airport startAirport = new Airport("Berlin Airport", "BER", "Berlin, Berlin");
            Airport destinationAirport = new Airport("Frankfurt Airport", "FRA", "Frankfurt, Hesse");
            Flight flight = new Flight(1, startAirport, destinationAirport, new PassengerPlane("A380"));
            Date departure = TestUtil.addDays(Date.from(Instant.now()), 3);
            schedule.scheduleFlight(flight, departure);
            
            customer = new Customer("John Doe", "john@example.com");
            ScheduledFlight scheduledFlight = schedule.searchScheduledFlight(flight.getNumber());
            order = customer.createOrder(Arrays.asList("John"), Arrays.asList(scheduledFlight), 500.0);
        }

        @Test
        @DisplayName("Test 1: FlightOrder accepts any PaymentStrategy implementation")
        void orderAcceptsAnyStrategy() {
            CreditCard validCard = Mockito.mock(CreditCard.class, Mockito.CALLS_REAL_METHODS);
            Mockito.when(validCard.isValid()).thenReturn(true);
            validCard.setAmount(1000.0);
            PaymentStrategy creditCardStrategy = new CreditCardPaymentStrategy(validCard);
            
            assertDoesNotThrow(() -> order.processPayment(creditCardStrategy));
            assertTrue(order.isClosed());
        }

        @Test
        @DisplayName("Test 2: Multiple payment strategies can be used interchangeably")
        void multipleStrategiesInterchangeable() {
            ScheduledFlight scheduledFlight = schedule.searchScheduledFlight(1);
            FlightOrder order1 = customer.createOrder(Arrays.asList("Alice"), Arrays.asList(scheduledFlight), 300.0);
            FlightOrder order2 = customer.createOrder(Arrays.asList("Bob"), Arrays.asList(scheduledFlight), 400.0);
            
            // Pay order1 with CreditCard
            CreditCard card = Mockito.mock(CreditCard.class, Mockito.CALLS_REAL_METHODS);
            Mockito.when(card.isValid()).thenReturn(true);
            card.setAmount(1000.0);
            order1.processPayment(new CreditCardPaymentStrategy(card));
            
            // Pay order2 with PayPal
            order2.processPayment(new PayPalPaymentStrategy("amanda@ya.com", "amanda1985"));
            
            assertTrue(order1.isClosed());
            assertTrue(order2.isClosed());
        }
    }

    @Nested
    @DisplayName("2. CreditCardPaymentStrategy Tests")
    class CreditCardPaymentStrategyTests {

        @Test
        @DisplayName("Test 3: Valid credit card processes payment successfully")
        void validCreditCardProcessesPayment() {
            CreditCard creditCard = Mockito.mock(CreditCard.class, Mockito.CALLS_REAL_METHODS);
            Mockito.when(creditCard.isValid()).thenReturn(true);
            creditCard.setAmount(1000.0);
            PaymentStrategy strategy = new CreditCardPaymentStrategy(creditCard);
            
            boolean result = strategy.processPayment(500.0);
            
            assertTrue(result);
            assertEquals(500.0, creditCard.getAmount());
        }

        @Test
        @DisplayName("Test 4: Invalid credit card throws IllegalStateException")
        void invalidCreditCardThrowsException() {
            CreditCard creditCard = Mockito.mock(CreditCard.class, Mockito.CALLS_REAL_METHODS);
            Mockito.when(creditCard.isValid()).thenReturn(false);
            PaymentStrategy strategy = new CreditCardPaymentStrategy(creditCard);
            
            assertThrows(IllegalStateException.class, () -> strategy.processPayment(500.0));
        }

        @Test
        @DisplayName("Test 5: Insufficient balance throws IllegalStateException")
        void insufficientBalanceThrowsException() {
            CreditCard creditCard = Mockito.mock(CreditCard.class, Mockito.CALLS_REAL_METHODS);
            Mockito.when(creditCard.isValid()).thenReturn(true);
            creditCard.setAmount(100.0);
            PaymentStrategy strategy = new CreditCardPaymentStrategy(creditCard);
            
            assertThrows(IllegalStateException.class, () -> strategy.processPayment(500.0));
        }
    }

    @Nested
    @DisplayName("3. PayPalPaymentStrategy Tests")
    class PayPalPaymentStrategyTests {

        @Test
        @DisplayName("Test 6: Valid PayPal credentials process payment successfully")
        void validPayPalProcessesPayment() {
            PaymentStrategy strategy = new PayPalPaymentStrategy("amanda@ya.com", "amanda1985");
            
            boolean result = strategy.processPayment(500.0);
            
            assertTrue(result);
        }

        @Test
        @DisplayName("Test 7: Invalid PayPal credentials throw IllegalStateException")
        void invalidPayPalThrowsException() {
            PaymentStrategy strategy = new PayPalPaymentStrategy("invalid@email.com", "wrongpass");
            
            assertThrows(IllegalStateException.class, () -> strategy.processPayment(500.0));
        }
    }

    @Nested
    @DisplayName("4. Strategy Pattern Extensibility")
    class StrategyExtensibilityTests {

        /**
         * Mock Bitcoin payment strategy to demonstrate extensibility
         */
        class BitcoinPaymentStrategy implements PaymentStrategy {
            private final String walletAddress;
            private final double balance;

            public BitcoinPaymentStrategy(String walletAddress, double balance) {
                this.walletAddress = walletAddress;
                this.balance = balance;
            }

            @Override
            public boolean validatePaymentInfo() throws IllegalStateException {
                if (walletAddress == null || walletAddress.isEmpty()) {
                    throw new IllegalStateException("Invalid Bitcoin wallet address");
                }
                return true;
            }

            @Override
            public boolean processPayment(double amount) throws IllegalStateException {
                validatePaymentInfo();
                if (balance < amount) {
                    throw new IllegalStateException("Insufficient Bitcoin balance");
                }
                System.out.println("Paying " + amount + " using Bitcoin from wallet: " + walletAddress);
                return true;
            }
        }

        @Test
        @DisplayName("Test 8: New payment strategy (Bitcoin) can be added without modifying FlightOrder")
        void newStrategyWithoutModifyingOrder() {
            Schedule schedule = new Schedule();
            schedule.clear();
            
            Airport start = new Airport("New York", "JFK", "USA");
            Airport dest = new Airport("London", "LHR", "UK");
            Flight flight = new Flight(1, start, dest, new PassengerPlane("A380"));
            schedule.scheduleFlight(flight, TestUtil.addDays(Date.from(Instant.now()), 7));
            
            Customer customer = new Customer("Crypto User", "crypto@example.com");
            ScheduledFlight scheduledFlight = schedule.searchScheduledFlight(flight.getNumber());
            FlightOrder order = customer.createOrder(Arrays.asList("Crypto"), Arrays.asList(scheduledFlight), 500.0);
            
            // Use new Bitcoin strategy - NO CHANGES TO FlightOrder needed!
            PaymentStrategy bitcoinStrategy = new BitcoinPaymentStrategy("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", 1000.0);
            boolean result = order.processPayment(bitcoinStrategy);
            
            assertTrue(result);
            assertTrue(order.isClosed());
        }

        @Test
        @DisplayName("Test 9: New payment strategy validates correctly")
        void newStrategyValidates() {
            PaymentStrategy validBitcoin = new BitcoinPaymentStrategy("validWallet123", 1000.0);
            assertTrue(validBitcoin.validatePaymentInfo());
            
            PaymentStrategy invalidBitcoin = new BitcoinPaymentStrategy("", 1000.0);
            assertThrows(IllegalStateException.class, invalidBitcoin::validatePaymentInfo);
        }

        @Test
        @DisplayName("Test 10: Different payment strategies can retry on same order")
        void differentStrategiesCanRetry() {
            Schedule schedule = new Schedule();
            schedule.clear();
            
            Airport start = new Airport("Paris", "CDG", "France");
            Airport dest = new Airport("Tokyo", "NRT", "Japan");
            Flight flight = new Flight(1, start, dest, new PassengerPlane("A380"));
            schedule.scheduleFlight(flight, TestUtil.addDays(Date.from(Instant.now()), 5));
            
            Customer customer = new Customer("Test User", "test@example.com");
            ScheduledFlight scheduledFlight = schedule.searchScheduledFlight(flight.getNumber());
            FlightOrder order = customer.createOrder(Arrays.asList("Passenger"), Arrays.asList(scheduledFlight), 750.0);
            
            // First attempt with insufficient Bitcoin balance - FAILS
            PaymentStrategy insufficientBitcoin = new BitcoinPaymentStrategy("wallet123", 100.0);
            assertThrows(IllegalStateException.class, () -> order.processPayment(insufficientBitcoin));
            assertFalse(order.isClosed());
            
            // Second attempt with valid PayPal - SUCCEEDS
            boolean result = order.processPayment(new PayPalPaymentStrategy("amanda@ya.com", "amanda1985"));
            assertTrue(result);
            assertTrue(order.isClosed());
        }
    }
}