package flight.reservation.payment;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    private final CreditCard creditCard;

    public CreditCardPaymentStrategy(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public boolean validatePaymentInfo() throws IllegalStateException {
        if (creditCard == null || !creditCard.isValid()) {
            throw new IllegalStateException("Payment information is not set or not valid.");
        }
        return true;
    }

    @Override
    public boolean processPayment(double amount) throws IllegalStateException {
        // Validate first
        validatePaymentInfo();
        
        System.out.println("Paying " + amount + " using Credit Card.");
         
        // Check if card has sufficient balance
        double remainingAmount = creditCard.getAmount() - amount;
        if (remainingAmount < 0) {
            System.out.printf("Card limit reached - Balance: %f%n", remainingAmount);
            throw new IllegalStateException("Card limit reached");
        }
        // Deduct amount from card
        creditCard.setAmount(remainingAmount);
        return true;
    }
}