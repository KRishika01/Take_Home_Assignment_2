package flight.reservation.payment;

/**
 * Third-party Stripe payment gateway API.
 * This class represents an external payment system with an INCOMPATIBLE interface.
 * 
 * Key differences from our PaymentStrategy interface:
 * 1. Uses different method names (authorizeCharge, capturePayment vs processPayment)
 * 2. Uses cents instead of dollars for amounts
 * 3. Requires two-step payment process (authorize + capture)
 * 4. Has different validation method (verifyApiKey vs validatePaymentInfo)
 */
public class Stripe {
    
    private String apiKey;
    private String accountEmail;
    private double balanceInCents;
    
    /**
     * Constructor for Stripe API
     * @param apiKey The API key for authentication (must start with "sk_")
     * @param accountEmail The account email associated with this Stripe account
     */
    public Stripe(String apiKey, String accountEmail) {
        this.apiKey = apiKey;
        this.accountEmail = accountEmail;
        this.balanceInCents = 5000000.0; // $50,000 initial balance in cents
    }
    
    /**
     * Stripe's method to verify the API key and account.
     * This is DIFFERENT from our validatePaymentInfo() method.
     * 
     * @return true if API key is valid and properly formatted
     */
    public boolean verifyApiKey() {
        if (apiKey == null || !apiKey.startsWith("sk_")) {
            System.out.println("Stripe API: Invalid API key format");
            return false;
        }
        
        if (accountEmail == null || !accountEmail.contains("@")) {
            System.out.println("Stripe API: Invalid account email");
            return false;
        }
        
        System.out.println("Stripe API: Account verified for " + accountEmail);
        return true;
    }
    
    /**
     * Stripe's method to authorize a charge.
     * This is the FIRST step in Stripe's two-step payment process.
     * Note: Amount is in CENTS, not dollars.
     * 
     * @param paymentToken The payment token for this transaction
     * @param amountInCents The amount to authorize in cents (e.g., 29999 = $299.99)
     * @return true if authorization is successful
     */
    public boolean authorizeCharge(String paymentToken, double amountInCents) {
        System.out.println("Stripe API: Authorizing charge of $" + (amountInCents / 100));
        
        if (paymentToken == null || paymentToken.isEmpty()) {
            System.out.println("Stripe API: Invalid payment token");
            return false;
        }
        
        if (amountInCents <= 0) {
            System.out.println("Stripe API: Invalid amount");
            return false;
        }
        
        if (!verifyApiKey()) {
            System.out.println("Stripe API: Authorization failed - API key not verified");
            return false;
        }
        
        System.out.println("Stripe API: Charge authorized successfully");
        return true;
    }
    
    /**
     * Stripe's method to capture a previously authorized payment.
     * This is the SECOND step in Stripe's two-step payment process.
     * Note: Amount is in CENTS, not dollars.
     * 
     * @param chargeId The charge ID from authorization
     * @param amountInCents The amount to capture in cents
     * @return Transaction ID if successful, null if failed
     */
    public String capturePayment(String chargeId, double amountInCents) {
        System.out.println("Stripe API: Capturing payment of $" + (amountInCents / 100));
        
        if (chargeId == null || chargeId.isEmpty()) {
            System.out.println("Stripe API: Invalid charge ID");
            return null;
        }
        
        if (balanceInCents < amountInCents) {
            System.out.printf("Stripe API: Insufficient funds - Available: $%.2f, Required: $%.2f%n", 
                             balanceInCents / 100, amountInCents / 100);
            return null;
        }
        
        // Deduct from balance
        balanceInCents -= amountInCents;
        
        // Generate transaction ID
        String transactionId = "txn_" + System.currentTimeMillis();
        
        System.out.printf("Stripe API: Payment captured successfully. Transaction ID: %s%n", transactionId);
        System.out.printf("Stripe API: Remaining balance: $%.2f%n", balanceInCents / 100);
        
        return transactionId;
    }
    
    /**
     * Get the current balance in cents
     * @return balance in cents
     */
    public double getBalanceInCents() {
        return balanceInCents;
    }
    
    /**
     * Get the account email
     * @return account email
     */
    public String getAccountEmail() {
        return accountEmail;
    }
    
    /**
     * Get the API key
     * @return API key
     */
    public String getApiKey() {
        return apiKey;
    }
}
