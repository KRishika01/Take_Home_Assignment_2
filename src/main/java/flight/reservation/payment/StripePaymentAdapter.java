package flight.reservation.payment;

/**
 * ADAPTER PATTERN IMPLEMENTATION
 * 
 * StripePaymentAdapter adapts the incompatible Stripe API to work with our PaymentStrategy interface.
 * This allows us to use Stripe as a payment method without changing our existing codebase that relies on PaymentStrategy.
 * 
 * This adapter solves the following incompatibilities:
 * 1. Method name mismatch: Stripe uses authorizeCharge() + capturePayment(), we use processPayment()
 * 2. Parameter mismatch: Stripe uses cents, we use dollars
 * 3. Validation mismatch: Stripe uses verifyApiKey(), we use validatePaymentInfo()
 * 4. Process mismatch: Stripe requires 2-step process, we have single-step payment
 */
public class StripePaymentAdapter implements PaymentStrategy {
    
    // The adaptee - the incompatible Stripe API we're adapting
    private final Stripe stripe;
    
    // Payment token required for Stripe transactions
    private final String paymentToken;
    
    /**
     * Constructor for the Adapter
     * 
     * @param stripe The Stripe API instance (Adaptee)
     * @param paymentToken The payment token for Stripe transactions
     */
    public StripePaymentAdapter(Stripe stripe, String paymentToken) {
        this.stripe = stripe;
        this.paymentToken = paymentToken;
    }
    
    /**
     * Adapts Stripe's verifyApiKey() method to our PaymentStrategy's validatePaymentInfo() method.
     * 
     * This method translates the call from our interface to Stripe's interface.
     * 
     * @return true if payment information is valid
     * @throws IllegalStateException if validation fails
     */
    @Override
    public boolean validatePaymentInfo() throws IllegalStateException {
        // Validate payment token
        if (paymentToken == null || paymentToken.isEmpty()) {
            throw new IllegalStateException("Stripe payment token is not set");
        }
        
        // Call Stripe's validation method (different name and logic)
        boolean isValid = stripe.verifyApiKey();
        
        if (!isValid) {
            throw new IllegalStateException("Stripe payment information is not valid");
        }
        
        return true;
    }
    
    /**
     * Adapts Stripe's two-step payment process (authorizeCharge + capturePayment) 
     * to our single-step processPayment() method.
     * 
     * This method handles:
     * 1. Currency conversion (dollars to cents)
     * 2. Two-step Stripe process (authorize then capture)
     * 3. Error handling and validation
     * 
     * @param amount The amount to charge in DOLLARS
     * @return true if payment was processed successfully
     * @throws IllegalStateException if payment processing fails
     */
    @Override
    public boolean processPayment(double amount) throws IllegalStateException {
        // Validate payment information first
        validatePaymentInfo();
        
        System.out.println("Paying " + amount + " using Stripe (via Adapter).");

        // Convert dollars to cents
        double amountInCents = amount * 100;

        // Authorize the charge using Stripe's API
        boolean authorized = stripe.authorizeCharge(paymentToken, amountInCents);
        
        if (!authorized) {
            throw new IllegalStateException("Stripe authorization failed");
        }
        
        // Capture the payment using Stripe's API
        // Generate a charge ID (in real implementation, this would come from authorize step)
        String chargeId = "ch_" + System.currentTimeMillis();
        String transactionId = stripe.capturePayment(chargeId, amountInCents);
        
        if (transactionId == null) {
            throw new IllegalStateException("Stripe payment capture failed - insufficient funds or invalid charge");
        }
        
        System.out.println("Stripe payment completed successfully via Adapter. Transaction: " + transactionId);
        return true;
    }
    
    /**
     * Get the adapted Stripe instance (for testing/debugging purposes)
     * @return the Stripe instance being adapted
     */
    public Stripe getStripe() {
        return stripe;
    }
}
