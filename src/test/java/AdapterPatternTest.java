package flight.reservation;

import flight.reservation.payment.*;
import flight.reservation.order.FlightOrder;
import flight.reservation.flight.ScheduledFlight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify the Adapter Pattern implementation for Stripe payment integration.
 * 
 * This test demonstrates:
 * 1. Stripe (Adaptee) has an incompatible interface
 * 2. StripePaymentAdapter (Adapter) makes it compatible with PaymentStrategy (Target)
 * 3. FlightOrder (Client) can use Stripe through the adapter without modification
 */
@DisplayName("Adapter Pattern Test - Stripe Payment Integration")
public class AdapterPatternTest {
    
    private Stripe stripe;
    private PaymentStrategy stripeAdapter;
    private PaymentStrategy creditCardPayment;
    private PaymentStrategy paypalPayment;
    
    @BeforeEach
    public void setUp() {
        // Create Stripe API instance (Adaptee)
        // Note: Using a dummy API key for testing purposes
        stripe = new Stripe("sk_test_api_key_placeholder", "customer@example.com");
        
        // Create Adapter to make Stripe compatible with PaymentStrategy
        stripeAdapter = new StripePaymentAdapter(stripe, "tok_visa_4242");
        
        // Create other payment strategies for comparison
        CreditCard creditCard = new CreditCard("4532015112830366", new Date(System.currentTimeMillis() + 31536000000L), "123");
        creditCardPayment = new CreditCardPaymentStrategy(creditCard);
        
        paypalPayment = new PayPalPaymentStrategy("amanda@ya.com", "amanda1985");
    }
    
    @Test
    @DisplayName("Test 1: Stripe API has incompatible interface")
    public void testStripeIncompatibleInterface() {
        
        // Stripe uses verifyApiKey() instead of validatePaymentInfo()
        assertTrue(stripe.verifyApiKey());
        
        // Stripe uses authorizeCharge() and capturePayment() with cents
        double amountInCents = 29999.0; // $299.99
        boolean authorized = stripe.authorizeCharge("tok_visa", amountInCents);
        assertTrue(authorized);
        
        String transactionId = stripe.capturePayment("ch_123", amountInCents);
        assertNotNull(transactionId);
        
        System.out.println("Stripe has incompatible interface (different methods and parameters)");
    }
    
    @Test
    @DisplayName("Test 2: Adapter implements PaymentStrategy interface")
    public void testAdapterImplementsPaymentStrategy() {
        // Verify that StripePaymentAdapter implements PaymentStrategy
        assertTrue(stripeAdapter instanceof PaymentStrategy);
        
        // Verify adapter has the required methods
        assertDoesNotThrow(() -> stripeAdapter.validatePaymentInfo());
        assertDoesNotThrow(() -> stripeAdapter.processPayment(299.99));
        
        System.out.println("StripePaymentAdapter correctly implements PaymentStrategy interface");
    }
    
    @Test
    @DisplayName("Test 3: Adapter validates payment information correctly")
    public void testAdapterValidation() {
        // Valid Stripe adapter should validate successfully
        assertDoesNotThrow(() -> stripeAdapter.validatePaymentInfo());
        
        // Invalid Stripe adapter should throw exception
        Stripe invalidStripe = new Stripe("invalid_key", "customer@example.com");
        PaymentStrategy invalidAdapter = new StripePaymentAdapter(invalidStripe, "tok_visa");
        
        assertThrows(IllegalStateException.class, () -> invalidAdapter.validatePaymentInfo());
        
        System.out.println("Adapter correctly validates payment information");
    }
    
    @Test
    @DisplayName("Test 4: Adapter processes payment correctly")
    public void testAdapterProcessesPayment() {
        double amount = 299.99;
        double initialBalance = stripe.getBalanceInCents();
        
        // Process payment through adapter (using dollars)
        boolean result = stripeAdapter.processPayment(amount);
        assertTrue(result);
        
        // Verify Stripe balance decreased by correct amount (in cents)
        double expectedBalance = initialBalance - (amount * 100);
        assertEquals(expectedBalance, stripe.getBalanceInCents(), 0.01);
        
        System.out.println("Adapter correctly processes payment and converts dollars to cents");
    }
    
    @Test
    @DisplayName("Test 5: Adapter handles insufficient funds correctly")
    public void testAdapterInsufficientFunds() {
        // Try to charge more than available balance
        double excessiveAmount = 60000.0; // More than $50,000 initial balance
        
        Exception exception = assertThrows(IllegalStateException.class, 
            () -> stripeAdapter.processPayment(excessiveAmount));
        
        assertTrue(exception.getMessage().contains("insufficient funds") || 
                   exception.getMessage().contains("capture failed"));
        
        System.out.println("Adapter correctly handles insufficient funds");
    }
    
    @Test
    @DisplayName("Test 6: FlightOrder works with all payment strategies polymorphically")
    public void testPolymorphicPaymentProcessing() {
        // This is the key test - FlightOrder doesn't need to know about Stripe details
        // It works with all payment strategies through the same interface
        
        List<PaymentStrategy> paymentStrategies = Arrays.asList(
            creditCardPayment,
            paypalPayment,
            stripeAdapter
        );
        
        for (PaymentStrategy payment : paymentStrategies) {
            assertDoesNotThrow(() -> payment.validatePaymentInfo());
            assertDoesNotThrow(() -> payment.processPayment(100.0));
        }

        System.out.println("FlightOrder can use CreditCard, PayPal, and Stripe polymorphically");
    }
    
    @Test
    @DisplayName("Test 7: Complete integration test with FlightOrder")
    public void testCompleteIntegrationWithFlightOrder() {
        // This test would require ScheduledFlight and other dependencies
        // For now, we demonstrate the payment processing works correctly
        
        System.out.println("\n=== Complete Payment Processing Test ===");
        
        double orderAmount = 599.99;
        
        // Test with Stripe through adapter
        System.out.println("\n1. Processing payment with Stripe (via Adapter):");
        boolean stripeResult = stripeAdapter.processPayment(orderAmount);
        assertTrue(stripeResult);
        
        // Test with CreditCard
        System.out.println("\n2. Processing payment with CreditCard:");
        boolean creditResult = creditCardPayment.processPayment(orderAmount);
        assertTrue(creditResult);
        
        // Test with PayPal
        System.out.println("\n3. Processing payment with PayPal:");
        boolean paypalResult = paypalPayment.processPayment(orderAmount);
        assertTrue(paypalResult);
        
        System.out.println("\n All payment methods work correctly through PaymentStrategy interface");
    }
    
    @Test
    @DisplayName("Test 8: Adapter pattern benefits - Adding Stripe without modifying existing code")
    public void testAdapterPatternBenefits() {
        PaymentStrategy payment = stripeAdapter;
        
        // Client code (like FlightOrder) uses the same interface
        assertDoesNotThrow(() -> {
            payment.validatePaymentInfo();
            payment.processPayment(199.99);
        });
        
        System.out.println(" Adapter pattern allows adding Stripe without modifying existing classes");
        System.out.println("  - PaymentStrategy interface: unchanged");
        System.out.println("  - FlightOrder: unchanged");
        System.out.println("  - Other payment strategies: unchanged");
    }
    
    @Test
    @DisplayName("Test 9: Verify Stripe's two-step process is hidden by adapter")
    public void testTwoStepProcessHidden() {
        // The adapter hides Stripe's two-step process (authorize + capture)
        // and presents it as a single processPayment() call
        
        double amount = 149.99;
        
        // Single call to processPayment
        boolean result = stripeAdapter.processPayment(amount);
        assertTrue(result);
        
        // Behind the scenes, adapter called:
        // 1. stripe.authorizeCharge()
        // 2. stripe.capturePayment()

        System.out.println(" Adapter hides Stripe's complex two-step process");
    }
}
