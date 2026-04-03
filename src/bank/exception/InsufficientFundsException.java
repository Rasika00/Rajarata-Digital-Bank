package bank.exception;

public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException(double available, double required) {
        super(String.format("Insufficient funds. Available: LKR %.2f, Required: LKR %.2f", available, required));
    }
}
