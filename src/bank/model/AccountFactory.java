package bank.model;

import bank.exception.BankingException;




public class AccountFactory {

    





    public static Account createAccount(AccountType type, String accountNumber, String customerId,
                                        double initialBalance, Object... extras) throws BankingException {
        
        String currency = "LKR";
        Object[] filteredExtras = extras;
        if (extras.length > 0) {
            Object last = extras[extras.length - 1];
            if (last instanceof String) {
                String lastStr = (String) last;
                if (lastStr.matches("[A-Z]{3}") && !lastStr.equals("N/A")) {
                    currency = lastStr;
                    filteredExtras = java.util.Arrays.copyOf(extras, extras.length - 1);
                }
            }
        }

        
        double amountInLKR = initialBalance;
        if (!currency.equals("LKR")) {
            amountInLKR = bank.service.CurrencyConverter.convert(initialBalance, currency, "LKR");
        }

        switch (type) {
            case SAVINGS:
                if (amountInLKR < 1000) throw new BankingException("Savings account requires minimum LKR 1,000 (or equivalent) initial deposit.");
                return new SavingsAccount(accountNumber, customerId, initialBalance, currency);
            case CHECKING:
                return new CheckingAccount(accountNumber, customerId, initialBalance, currency);
            case STUDENT:
                String uni = filteredExtras.length > 0 ? (String) filteredExtras[0] : "Unknown University";
                String sid = filteredExtras.length > 1 ? (String) filteredExtras[1] : "N/A";
                return new StudentAccount(accountNumber, customerId, initialBalance, uni, sid, currency);
            case FIXED_DEPOSIT:
                if (amountInLKR < 10000) throw new BankingException("Fixed Deposit requires minimum LKR 10,000 (or equivalent).");
                int term = filteredExtras.length > 0 && filteredExtras[0] instanceof Integer
                    ? (int) filteredExtras[0] : 12;
                return new FixedDepositAccount(accountNumber, customerId, initialBalance, term, currency);
            default:
                throw new BankingException("Unknown account type.");
        }
    }
}
