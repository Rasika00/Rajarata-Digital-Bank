package bank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1000);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateUserId() { return "USR" + counter.incrementAndGet(); }
    public static String generateAccountNumber() { return "RDB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + counter.incrementAndGet(); }
    public static String generateTransactionId() { return "TXN" + LocalDateTime.now().format(FMT) + counter.incrementAndGet(); }
    public static String generateLoanId() { return "LN" + counter.incrementAndGet(); }
    public static String generateBillId() { return "BILL" + counter.incrementAndGet(); }
    public static String generateNotificationId() { return "NTF" + counter.incrementAndGet(); }
}
