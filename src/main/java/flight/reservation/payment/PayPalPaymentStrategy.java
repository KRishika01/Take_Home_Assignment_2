package flight.reservation.payment;

public class PayPalPaymentStrategy implements PaymentStrategy {
    private final String email;
    private final String password;

    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean validatePaymentInfo() throws IllegalStateException {
        if (email == null || password == null || !email.equals(Paypal.DATA_BASE.get(password))) {
            throw new IllegalStateException("Payment information is not set or not valid.");
        }
        return true;
    }

    @Override
    public boolean processPayment(double amount) throws IllegalStateException {
        validatePaymentInfo();
        
        System.out.println("Paying " + amount + " using PayPal.");
        return true;
    }
}