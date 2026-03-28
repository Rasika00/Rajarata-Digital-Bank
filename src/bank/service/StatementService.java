package bank.service;

import bank.model.*;
import bank.util.FileHandler;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;





public class StatementService {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final File STATEMENTS_DIR = new File(FileHandler.dataDirectory(), "statements");

    static {
        if (!STATEMENTS_DIR.exists() && !STATEMENTS_DIR.mkdirs()) {
            System.err.println("Unable to create statements directory: " + STATEMENTS_DIR.getAbsolutePath());
        }
    }

    



    public String generateMonthlyStatement(Account account, User customer,
                                           int year, Month month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate   = startDate.withDayOfMonth(startDate.lengthOfMonth());

        
        List<Transaction> monthTxns = account.getTransactions().stream()
            .filter(t -> {
                LocalDate txDate = t.getTimestamp().toLocalDate();
                return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
            })
            .sorted(Comparator.comparing(Transaction::getTimestamp))
            .collect(Collectors.toList());

        
        double openingBalance  = calculateOpeningBalance(account, startDate);
        double totalDebits     = monthTxns.stream()
            .filter(t -> isDebit(t.getType()))
            .mapToDouble(Transaction::getAmount).sum();
        double totalCredits    = monthTxns.stream()
            .filter(t -> !isDebit(t.getType()))
            .mapToDouble(Transaction::getAmount).sum();
        double interestEarned  = monthTxns.stream()
            .filter(t -> t.getType() == TransactionType.INTEREST)
            .mapToDouble(Transaction::getAmount).sum();
        double closingBalance  = monthTxns.isEmpty()
            ? openingBalance
            : monthTxns.get(monthTxns.size() - 1).getBalanceAfter();

        
        StringBuilder sb = new StringBuilder();
        String line  = "=".repeat(72);
        String dline = "-".repeat(72);

        sb.append(line).append("\n");
        sb.append(centre("RAJARATA DIGITAL BANK", 72)).append("\n");
        sb.append(centre("Monthly Account Statement", 72)).append("\n");
        sb.append(centre("Generated: " + LocalDateTime.now().format(TIME_FMT), 72)).append("\n");
        sb.append(line).append("\n\n");

        sb.append("CUSTOMER INFORMATION\n");
        sb.append(dline).append("\n");
        sb.append(String.format("%-25s: %s%n", "Customer Name",   customer.getFullName()));
        sb.append(String.format("%-25s: %s%n", "Customer ID",     customer.getUserId()));
        sb.append(String.format("%-25s: %s%n", "Email",           customer.getEmail()));
        sb.append(String.format("%-25s: %s%n", "Phone",           customer.getPhone()));
        sb.append("\n");

        sb.append("ACCOUNT INFORMATION\n");
        sb.append(dline).append("\n");
        sb.append(String.format("%-25s: %s%n", "Account Number",  account.getAccountNumber()));
        sb.append(String.format("%-25s: %s%n", "Account Type",    account.getAccountType().toString().replace("_", " ")));
        sb.append(String.format("%-25s: %s%n", "Currency",        account.getCurrency()));
        sb.append(String.format("%-25s: %s to %s%n", "Statement Period",
                startDate.format(DATE_FMT), endDate.format(DATE_FMT)));
        sb.append(String.format("%-25s: %s%n", "Account Status",  account.isActive() ? "Active" : "Inactive"));
        sb.append("\n");

        sb.append("STATEMENT SUMMARY\n");
        sb.append(dline).append("\n");
        sb.append(String.format("%-30s: %s %.2f%n", "Opening Balance",  account.getCurrency(), openingBalance));
        sb.append(String.format("%-30s: %s %.2f%n", "Total Credits (+)", account.getCurrency(), totalCredits));
        sb.append(String.format("%-30s: %s %.2f%n", "Total Debits  (-)", account.getCurrency(), totalDebits));
        sb.append(String.format("%-30s: %s %.2f%n", "Interest Earned",  account.getCurrency(), interestEarned));
        sb.append(String.format("%-30s: %s %.2f%n", "Closing Balance",  account.getCurrency(), closingBalance));
        sb.append(String.format("%-30s: %d%n", "Total Transactions",    monthTxns.size()));
        sb.append("\n");

        sb.append("TRANSACTION DETAILS\n");
        sb.append(dline).append("\n");
        sb.append(String.format("%-22s %-18s %-12s %-12s %s%n",
                "Date & Time", "Transaction ID", "Type", "Amount", "Balance"));
        sb.append(dline).append("\n");

        if (monthTxns.isEmpty()) {
            sb.append("  No transactions found for this period.\n");
        } else {
            for (Transaction t : monthTxns) {
                String sign = isDebit(t.getType()) ? "-" : "+";
                sb.append(String.format("%-22s %-18s %-12s %s%-11.2f %.2f%n",
                        t.getTimestamp().format(TIME_FMT),
                        t.getTransactionId(),
                        abbreviateType(t.getType()),
                        sign,
                        t.getAmount(),
                        t.getBalanceAfter()));
                if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                    sb.append(String.format("  → %s%n", t.getDescription()));
                }
            }
        }

        sb.append(dline).append("\n\n");
        sb.append(centre("IMPORTANT NOTICES", 72)).append("\n");
        sb.append(centre("Minimum Balance: " + account.getCurrency() + " " +
                String.format("%.2f", account.getMinimumBalance()), 72)).append("\n");
        sb.append(centre("Interest Rate: " + getInterestRateDescription(account), 72)).append("\n");
        sb.append(centre("For queries, contact support@rajaratabank.lk", 72)).append("\n");
        sb.append(line).append("\n");
        sb.append(centre("*** END OF STATEMENT ***", 72)).append("\n");
        sb.append(line).append("\n");

        String content = sb.toString();

        
        String filename = account.getAccountNumber() + "_" + year + "_" +
                String.format("%02d", month.getValue()) + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(STATEMENTS_DIR, filename)))) {
            pw.print(content);
        } catch (IOException e) {
            System.err.println("Error saving statement: " + e.getMessage());
        }

        return content;
    }

    


    private double calculateOpeningBalance(Account account, LocalDate startDate) {
        double opening = account.getBalance();
        
        List<Transaction> after = account.getTransactions().stream()
            .filter(t -> !t.getTimestamp().toLocalDate().isBefore(startDate))
            .collect(Collectors.toList());
        for (Transaction t : after) {
            if (isDebit(t.getType())) opening += t.getAmount();
            else opening -= t.getAmount();
        }
        return Math.max(opening, 0);
    }

    private boolean isDebit(TransactionType type) {
        return type == TransactionType.WITHDRAWAL
            || type == TransactionType.TRANSFER_OUT
            || type == TransactionType.BILL_PAYMENT
            || type == TransactionType.LOAN_REPAYMENT
            || type == TransactionType.FEE
            || type == TransactionType.PENALTY;
    }

    private String abbreviateType(TransactionType type) {
        switch (type) {
            case DEPOSIT:           return "DEPOSIT";
            case WITHDRAWAL:        return "WITHDRAW";
            case TRANSFER_IN:       return "TRF IN";
            case TRANSFER_OUT:      return "TRF OUT";
            case INTEREST:          return "INTEREST";
            case LOAN_DISBURSEMENT: return "LOAN DIS";
            case LOAN_REPAYMENT:    return "LOAN REP";
            case BILL_PAYMENT:      return "BILL PAY";
            case FEE:               return "FEE";
            case PENALTY:           return "PENALTY";
            default:                return type.toString();
        }
    }

    private String getInterestRateDescription(Account account) {
        switch (account.getAccountType()) {
            case SAVINGS:       return "3.5% per annum";
            case CHECKING:      return "1.0% per annum";
            case STUDENT:       return "2.0% per annum";
            case FIXED_DEPOSIT: return "9.0% per annum";
            default:            return "0.0% per annum";
        }
    }

    private String centre(String text, int width) {
        if (text.length() >= width) return text;
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text;
    }

    


    public List<String> getAvailableStatements(String accountNumber) {
        List<String> files = new ArrayList<>();
        if (STATEMENTS_DIR.exists()) {
            for (File f : Objects.requireNonNull(STATEMENTS_DIR.listFiles())) {
                if (f.getName().startsWith(accountNumber)) {
                    files.add(f.getName());
                }
            }
        }
        Collections.sort(files, Collections.reverseOrder());
        return files;
    }

    


    public String loadStatement(String filename) {
        File f = new File(STATEMENTS_DIR, filename);
        if (!f.exists()) return "Statement file not found.";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            return "Error reading statement: " + e.getMessage();
        }
    }
}
