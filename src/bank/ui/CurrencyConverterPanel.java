package bank.ui;

import bank.service.CurrencyConverter;

import javax.swing.*;
import java.awt.*;




public class CurrencyConverterPanel extends JPanel {

    private JComboBox<String> fromCombo, toCombo;
    private JTextField amountField;
    private JLabel resultLabel, rateLabel;

    public CurrencyConverterPanel(Object user, MainFrame mainFrame) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Currency Converter"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        card.setPreferredSize(new Dimension(480, 400));

        String[] currencies = CurrencyConverter.getSupportedCurrencies();

        fromCombo = new JComboBox<>(currencies);
        fromCombo.setSelectedItem("USD");
        fromCombo.setFont(UITheme.FONT_BODY);
        fromCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        toCombo = new JComboBox<>(currencies);
        toCombo.setSelectedItem("LKR");
        toCombo.setFont(UITheme.FONT_BODY);
        toCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        amountField = UIComponents.createTextField("Enter amount");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        rateLabel = UIComponents.label("", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        rateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        resultLabel.setForeground(UITheme.PRIMARY);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton convertBtn = UIComponents.createButton("Convert", UITheme.PRIMARY, Color.WHITE, -1, 44);
        convertBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        convertBtn.addActionListener(e -> doConvert());

        
        JButton swapBtn = UIComponents.createButton("⇄ Swap", UITheme.ACCENT, Color.WHITE, -1, 36);
        swapBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        swapBtn.addActionListener(e -> {
            String f = (String) fromCombo.getSelectedItem();
            String t = (String) toCombo.getSelectedItem();
            fromCombo.setSelectedItem(t);
            toCombo.setSelectedItem(f);
            doConvert();
        });

        
        fromCombo.addActionListener(e -> updateRateLabel());
        toCombo.addActionListener(e -> updateRateLabel());

        
        JPanel rateTable = buildRateTable();

        card.add(UIComponents.formRow("From Currency", fromCombo));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("To Currency", toCombo));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Amount", amountField));
        card.add(Box.createVerticalStrut(8));
        card.add(rateLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(swapBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(convertBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(resultLabel);

        JPanel wrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        wrapper.setOpaque(false);
        wrapper.add(card);
        wrapper.add(rateTable);

        add(wrapper, BorderLayout.CENTER);
        updateRateLabel();
    }

    private void updateRateLabel() {
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();
        rateLabel.setText("Rate: " + CurrencyConverter.getRateDescription(from, to));
    }

    private void doConvert() {
        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) { resultLabel.setText("Enter an amount"); return; }
        try {
            double amount = Double.parseDouble(amtStr);
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            double result = CurrencyConverter.convert(amount, from, to);
            resultLabel.setText(CurrencyConverter.format(amount, from) + " = " + CurrencyConverter.format(result, to));
            updateRateLabel();
        } catch (NumberFormatException ex) {
            resultLabel.setText("Invalid amount");
        }
    }

    private JPanel buildRateTable() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        card.add(UIComponents.sectionHeader("Exchange Rates (vs LKR)"), BorderLayout.NORTH);

        String[] currencies = CurrencyConverter.getSupportedCurrencies();
        String[] cols = {"Currency", "Rate (1 unit = LKR)"};
        Object[][] data = new Object[currencies.length][2];
        for (int i = 0; i < currencies.length; i++) {
            double rate = CurrencyConverter.getRateToLKR(currencies[i]);
            data[i][0] = currencies[i];
            data[i][1] = String.format("LKR %.4f", rate);
        }

        JTable table = UIComponents.createStyledTable(data, cols);
        card.add(UIComponents.scrollPane(table), BorderLayout.CENTER);

        JLabel note = UIComponents.label("* Rates are indicative. Actual rates may vary.", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        card.add(note, BorderLayout.SOUTH);
        return card;
    }
}
