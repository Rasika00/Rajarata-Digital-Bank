package bank.service;

import java.util.HashMap;
import java.util.Map;





public class CurrencyConverter {

    
    
    private static final Map<String, Double> RATES_TO_LKR = new HashMap<>();

    static {
        RATES_TO_LKR.put("LKR", 1.0);
        RATES_TO_LKR.put("USD", 305.50);   
        RATES_TO_LKR.put("EUR", 330.20);   
        RATES_TO_LKR.put("GBP", 385.75);   
        RATES_TO_LKR.put("INR", 3.65);     
        RATES_TO_LKR.put("JPY", 2.05);     
        RATES_TO_LKR.put("AUD", 198.40);   
    }

    






    public static double convert(double amount, String from, String to) {
        if (from.equals(to)) return amount;
        double fromRate = RATES_TO_LKR.getOrDefault(from.toUpperCase(), 1.0);
        double toRate   = RATES_TO_LKR.getOrDefault(to.toUpperCase(), 1.0);
        
        double inLKR = amount * fromRate;
        return inLKR / toRate;
    }

    


    public static String getRateDescription(String from, String to) {
        if (from.equals(to)) return "No conversion needed";
        double rate = convert(1.0, from, to);
        return String.format("1 %s = %.4f %s", from, rate, to);
    }

    


    public static String[] getSupportedCurrencies() {
        return RATES_TO_LKR.keySet().stream().sorted().toArray(String[]::new);
    }

    


    public static double getRateToLKR(String currency) {
        return RATES_TO_LKR.getOrDefault(currency.toUpperCase(), 1.0);
    }

    


    public static String format(double amount, String currency) {
        String symbol;
        switch (currency.toUpperCase()) {
            case "USD": symbol = "$"; break;
            case "EUR": symbol = "€"; break;
            case "GBP": symbol = "£"; break;
            case "INR": symbol = "₹"; break;
            case "JPY": symbol = "¥"; break;
            case "AUD": symbol = "A$"; break;
            default: symbol = "LKR "; break;
        }
        return symbol + String.format("%.2f", amount);
    }
}
