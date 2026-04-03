package bank.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BillPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billId;
    private String customerId;
    private String accountNumber;
    private BillType billType;
    private String provider;
    private String referenceNumber;
    private double amount;
    private LocalDate paymentDate;
    private BillStatus status;

    public BillPayment(String billId, String customerId, String accountNumber, BillType billType,
                       String provider, String referenceNumber, double amount) {
        this.billId = billId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.billType = billType;
        this.provider = provider;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.paymentDate = LocalDate.now();
        this.status = BillStatus.PENDING;
    }

    public String getBillId() { return billId; }
    public String getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public BillType getBillType() { return billType; }
    public String getProvider() { return provider; }
    public String getReferenceNumber() { return referenceNumber; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus s) { this.status = s; }

    public Object[] toTableRow() {
        return new Object[]{ billId, billType, provider, referenceNumber,
                String.format("LKR %.2f", amount), paymentDate.toString(), status };
    }
}
