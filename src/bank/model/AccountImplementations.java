package bank.model;

import bank.exception.BankingException;




class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double INTEREST_RATE = 0.035; 
    private static final double MIN_BALANCE = 1000.0;
    private static final double WITHDRAWAL_LIMIT = 50000.0;

    public SavingsAccount(String accountNumber, String customerId, double initialBalance) {
        super(accountNumber, customerId, initialBalance, "LKR");
    }
    public SavingsAccount(String accountNumber, String customerId, double initialBalance, String currency) {
        super(accountNumber, customerId, initialBalance, currency);
    }

    @Override public AccountType getAccountType() { return AccountType.SAVINGS; }
    @Override public double calculateInterest() { return getBalance() * INTEREST_RATE / 12; }
    @Override public double getWithdrawalLimit() { return WITHDRAWAL_LIMIT; }
    @Override public double getMinimumBalance() { return getCurrency().equals("LKR") ? MIN_BALANCE : 0; }
    @Override public String getAccountDescription() {
        return "Savings Account | 3.5% p.a. | Min Balance: " + getCurrency() + " " +
               (getCurrency().equals("LKR") ? "1,000" : "0");
    }
}




class CheckingAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double OVERDRAFT_LIMIT = 10000.0;
    private static final double INTEREST_RATE = 0.01;
    private static final double WITHDRAWAL_LIMIT = 200000.0;

    public CheckingAccount(String accountNumber, String customerId, double initialBalance) {
        super(accountNumber, customerId, initialBalance, "LKR");
    }
    public CheckingAccount(String accountNumber, String customerId, double initialBalance, String currency) {
        super(accountNumber, customerId, initialBalance, currency);
    }

    @Override
    public void withdraw(double amount) throws BankingException {
        if (amount <= 0) throw new BankingException("Amount must be positive.");
        double effectiveOverdraft = getCurrency().equals("LKR") ? OVERDRAFT_LIMIT : 0;
        if ((getBalance() - amount) < -effectiveOverdraft)
            throw new BankingException("Exceeds overdraft limit of " + getCurrency() + " " + effectiveOverdraft);
        setBalance(getBalance() - amount);
    }

    @Override public AccountType getAccountType() { return AccountType.CHECKING; }
    @Override public double calculateInterest() { return getBalance() > 0 ? getBalance() * INTEREST_RATE / 12 : 0; }
    @Override public double getWithdrawalLimit() { return WITHDRAWAL_LIMIT; }
    @Override public double getMinimumBalance() { return getCurrency().equals("LKR") ? -OVERDRAFT_LIMIT : 0; }
    @Override public String getAccountDescription() {
        return "Checking Account | Overdraft: " + getCurrency() + " " +
               (getCurrency().equals("LKR") ? "10,000" : "N/A") + " | 1% p.a.";
    }
}




class StudentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double INTEREST_RATE = 0.02;
    private static final double WITHDRAWAL_LIMIT = 10000.0;
    private String universityName;
    private String studentId;

    public StudentAccount(String accountNumber, String customerId, double initialBalance,
                          String universityName, String studentId) {
        super(accountNumber, customerId, initialBalance, "LKR");
        this.universityName = universityName;
        this.studentId = studentId;
    }
    public StudentAccount(String accountNumber, String customerId, double initialBalance,
                          String universityName, String studentId, String currency) {
        super(accountNumber, customerId, initialBalance, currency);
        this.universityName = universityName;
        this.studentId = studentId;
    }

    @Override public AccountType getAccountType() { return AccountType.STUDENT; }
    @Override public double calculateInterest() { return getBalance() * INTEREST_RATE / 12; }
    @Override public double getWithdrawalLimit() { return WITHDRAWAL_LIMIT; }
    @Override public double getMinimumBalance() { return 0; }
    @Override public String getAccountDescription() {
        return "Student Account | 2% p.a. | No Min Balance | Daily Limit: " + getCurrency() + " 10,000";
    }
    public String getUniversityName() { return universityName; }
    public String getStudentId() { return studentId; }
}




class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double INTEREST_RATE = 0.09; 
    private static final double EARLY_WITHDRAWAL_PENALTY = 0.02;
    private int termMonths;
    private java.time.LocalDateTime maturityDate;
    private boolean matured;

    public FixedDepositAccount(String accountNumber, String customerId, double amount, int termMonths) {
        super(accountNumber, customerId, amount, "LKR");
        this.termMonths = termMonths;
        this.maturityDate = java.time.LocalDateTime.now().plusMonths(termMonths);
        this.matured = false;
    }
    public FixedDepositAccount(String accountNumber, String customerId, double amount, int termMonths, String currency) {
        super(accountNumber, customerId, amount, currency);
        this.termMonths = termMonths;
        this.maturityDate = java.time.LocalDateTime.now().plusMonths(termMonths);
        this.matured = false;
    }

    @Override
    public void withdraw(double amount) throws BankingException {
        if (!matured) {
            double penalty = getBalance() * EARLY_WITHDRAWAL_PENALTY;
            throw new BankingException("Fixed Deposit not matured. Early withdrawal penalty: " + getCurrency() + " " +
                    String.format("%.2f", penalty) + ". Matures on: " + maturityDate.toLocalDate());
        }
        super.withdraw(amount);
    }

    @Override public AccountType getAccountType() { return AccountType.FIXED_DEPOSIT; }
    @Override public double calculateInterest() { return getBalance() * INTEREST_RATE / 12; }
    @Override public double getWithdrawalLimit() { return getBalance(); }
    @Override public double getMinimumBalance() { return 0; }
    @Override public String getAccountDescription() {
        return "Fixed Deposit | 9% p.a. | Term: " + termMonths + " months | Matures: " + maturityDate.toLocalDate();
    }
    public boolean isMatured() {
        if (!matured && java.time.LocalDateTime.now().isAfter(maturityDate)) matured = true;
        return matured;
    }
    public java.time.LocalDateTime getMaturityDate() { return maturityDate; }
    public int getTermMonths() { return termMonths; }
}
