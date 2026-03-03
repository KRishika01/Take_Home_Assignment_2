
package flight.reservation.payment;

public interface PaymentStrategy {
   
    boolean processPayment(double amount) throws IllegalStateException;
    
   
    boolean validatePaymentInfo() throws IllegalStateException;
}