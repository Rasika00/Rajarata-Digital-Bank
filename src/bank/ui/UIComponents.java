package bank.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;




public class UIComponents {
    private static BufferedImage cachedCustomLogo;
    private static boolean logoLoadAttempted;

    private static String sanitizeText(String text) {
        if (text == null) return "";
        String cleaned = text.replaceAll("[^\\p{ASCII}]", " ").replaceAll("\\s+", " ").trim();
        return cleaned.isEmpty() ? text : cleaned;
    }

    private static boolean isLight(Color color) {
        double luminance = (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255.0;
        return luminance > 0.72;
    }

    private static String escapeHtml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    public static String wrapHtml(String text, int width) {
        int safeWidth = Math.max(120, width);
        String safeText = escapeHtml(sanitizeText(text));
        return "<html><div style='width:" + safeWidth + "px;'>" + safeText + "</div></html>";
    }

    public static JLabel createPillLabel(String text, Color bg, Color fg) {
        JLabel label = new JLabel(sanitizeText(text));
        label.setFont(UITheme.FONT_SMALL);
        label.setForeground(fg);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return label;
    }

    public static JComponent createBrandMark(String text, int size) {
        String label = sanitizeText(text).toUpperCase();
        final int logoSize = Math.max(28, size);

        final Font brandFont = new Font("Inter", Font.BOLD, Math.max(18, size / 3));

        return new JComponent() {
            @Override public Dimension getPreferredSize() {
                return new Dimension(logoSize, logoSize);
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int diameter = Math.max(1, Math.min(getWidth(), getHeight()) - 2);
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                Shape circle = new Ellipse2D.Double(x, y, diameter, diameter);

                BufferedImage logo = getCustomLogoImage();
                if (logo != null) {
                    int side = Math.min(logo.getWidth(), logo.getHeight());
                    int srcX = (logo.getWidth() - side) / 2;
                    int srcY = (logo.getHeight() - side) / 2;

                    Shape oldClip = g2.getClip();
                    g2.setClip(circle);
                    g2.drawImage(logo, x, y, x + diameter, y + diameter,
                            srcX, srcY, srcX + side, srcY + side, null);
                    g2.setClip(oldClip);
                } else {
                    GradientPaint paint = new GradientPaint(x, y, UITheme.PRIMARY_LIGHT, x + diameter, y + diameter, UITheme.PRIMARY_DARK);
                    g2.setPaint(paint);
                    g2.fill(circle);

                    g2.setColor(new Color(255, 255, 255, 90));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.draw(circle);

                    g2.setColor(Color.WHITE);
                    g2.setFont(brandFont);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(label)) / 2;
                    int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(label, textX, textY);
                }

                g2.setColor(new Color(255, 255, 255, 105));
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(circle);

                g2.dispose();
            }
        };
    }

    private static BufferedImage getCustomLogoImage() {
        if (logoLoadAttempted) return cachedCustomLogo;
        logoLoadAttempted = true;

        // First try bundled classpath resources if present.
        String[] resourceCandidates = new String[] {
            "/logo.png",
            "/assets/logo.png",
            "/data/assets/logo.png"
        };
        for (String resourcePath : resourceCandidates) {
            try {
                java.io.InputStream stream = UIComponents.class.getResourceAsStream(resourcePath);
                if (stream == null) continue;
                try (java.io.InputStream in = stream) {
                    cachedCustomLogo = ImageIO.read(in);
                    if (cachedCustomLogo != null) return cachedCustomLogo;
                }
            } catch (IOException ignored) {
            }
        }

        String fromProperty = System.getProperty("rdb.logo.path");
        String fromEnv = System.getenv("RDB_LOGO_PATH");
        String appPath = System.getProperty("jpackage.app-path");
        String appDir = null;
        if (appPath != null && !appPath.trim().isEmpty()) {
            File launcher = new File(appPath);
            File parent = launcher.getParentFile();
            appDir = parent != null ? parent.getAbsolutePath() : null;
        }

        String codeSourceDir = null;
        try {
            ProtectionDomain pd = UIComponents.class.getProtectionDomain();
            if (pd != null) {
                CodeSource cs = pd.getCodeSource();
                if (cs != null && cs.getLocation() != null) {
                    File source = new File(cs.getLocation().toURI());
                    File base = source.isDirectory() ? source : source.getParentFile();
                    if (base != null) codeSourceDir = base.getAbsolutePath();
                }
            }
        } catch (URISyntaxException | SecurityException ignored) {
        }

        String[] candidates = new String[] {
            fromProperty,
            fromEnv,
            "data/logo.png",
            "data/logo.jpg",
            "data/logo.jpeg",
            "data/assets/logo.png",
            "data/assets/logo.jpg",
            "data/assets/logo.jpeg",
            appDir == null ? null : appDir + File.separator + "logo.png",
            appDir == null ? null : appDir + File.separator + "assets" + File.separator + "logo.png",
            appDir == null ? null : appDir + File.separator + "data" + File.separator + "assets" + File.separator + "logo.png",
            codeSourceDir == null ? null : codeSourceDir + File.separator + "logo.png",
            codeSourceDir == null ? null : codeSourceDir + File.separator + "assets" + File.separator + "logo.png",
            codeSourceDir == null ? null : codeSourceDir + File.separator + "data" + File.separator + "assets" + File.separator + "logo.png"
        };

        for (String path : candidates) {
            if (path == null || path.trim().isEmpty()) continue;
            File f = new File(path.trim());
            if (!f.exists() || !f.isFile()) continue;
            try {
                cachedCustomLogo = ImageIO.read(f);
                if (cachedCustomLogo != null) return cachedCustomLogo;
            } catch (IOException | RuntimeException ignored) {
            }
        }
        return null;
    }

    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color borderColor;

        public RoundedPanel(int radius, Color bg) {
            this(radius, bg, UITheme.BORDER);
        }

        public RoundedPanel(int radius, Color bg, Color borderColor) {
            this.radius = radius;
            this.bg = bg;
            this.borderColor = borderColor;
            setOpaque(false);
            setBackground(bg);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(UITheme.SHADOW);
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, radius, radius);
            
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, radius, radius);
            
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 7, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JPanel createStatCard(String title, String value, String subtitle, Color accent) {
        RoundedPanel card = new RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel titleLbl = new JLabel(sanitizeText(title).toUpperCase());
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        JLabel dot = new JLabel("  ");
        dot.setOpaque(true);
        dot.setBackground(accent);
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        top.add(titleLbl, BorderLayout.WEST);
        top.add(dot, BorderLayout.EAST);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 3));
        content.setOpaque(false);
        JLabel valueLbl = new JLabel(sanitizeText(value));
        valueLbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        valueLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitleLbl = new JLabel(sanitizeText(subtitle));
        subtitleLbl.setFont(UITheme.FONT_SMALL);
        subtitleLbl.setForeground(UITheme.TEXT_MUTED);

        content.add(valueLbl);
        content.add(subtitleLbl);
        card.add(top, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static JButton createButton(String text, Color bg, Color fg, int width, int height) {
        String buttonText = sanitizeText(text);
        final Color overlayLight = UITheme.OVERLAY_LIGHT;
        final Color shadow = new Color(9, 18, 33, 18);
        final boolean isLightBg = isLight(bg);

        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                Color fill = bg;
                if (getModel().isPressed()) fill = bg.darker();
                else if (getModel().isRollover()) fill = isLightBg ? bg.darker() : bg.brighter();
                
                g2.setColor(shadow);
                g2.fillRoundRect(0, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
                
                g2.setColor(isLightBg ? UITheme.BORDER_STRONG : overlayLight);
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 14, 14);
                
                g2.setColor(isLightBg ? UITheme.TEXT_PRIMARY : fg);
                g2.setFont(UITheme.FONT_SUBHEAD);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(buttonText)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(buttonText, x, y);
                g2.dispose();
            }
        };
        btn.setFont(UITheme.FONT_SUBHEAD);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (width > 0) btn.setPreferredSize(new Dimension(width, height));
        else btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, height));
        return btn;
    }

    public static JTextField createTextField(String placeholder) {
        final Color placeholderColor = new Color(UITheme.TEXT_MUTED.getRed(), UITheme.TEXT_MUTED.getGreen(), UITheme.TEXT_MUTED.getBlue(), 145);
        final Border componentBorder = BorderFactory.createCompoundBorder(
            new RoundBorder(UITheme.BORDER, 14),
            BorderFactory.createEmptyBorder(11, 14, 11, 14)
        );

        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!hasFocus() && getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(UITheme.FONT_BODY);
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(sanitizeText(placeholder), insets.left + 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        tf.setFont(UITheme.FONT_BODY);
        tf.setBorder(componentBorder);
        tf.setForeground(UITheme.TEXT_PRIMARY);
        tf.setBackground(UITheme.BG_INPUT);
        tf.setCaretColor(UITheme.PRIMARY_DARK);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { tf.repaint(); }
            public void focusLost(FocusEvent e) { tf.repaint(); }
        });
        return tf;
    }

    public static JPasswordField createPasswordField() {
        final Color placeholderColor = new Color(UITheme.TEXT_MUTED.getRed(), UITheme.TEXT_MUTED.getGreen(), UITheme.TEXT_MUTED.getBlue(), 145);
        final Border componentBorder = BorderFactory.createCompoundBorder(
            new RoundBorder(UITheme.BORDER, 14),
            BorderFactory.createEmptyBorder(11, 14, 11, 14)
        );

        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!hasFocus() && getPassword().length == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(UITheme.FONT_BODY);
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("Enter password", insets.left + 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        pf.setFont(UITheme.FONT_BODY);
        pf.setBorder(componentBorder);
        pf.setBackground(UITheme.BG_INPUT);
        pf.setCaretColor(UITheme.PRIMARY_DARK);
        return pf;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(sanitizeText(text));
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JLabel wrappedLabel(String text, Font font, Color color, int width) {
        JLabel l = new JLabel(wrapHtml(text, width));
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JPanel sectionHeader(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel lbl = new JLabel(sanitizeText(title));
        lbl.setFont(UITheme.FONT_HEADING);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        p.add(lbl, BorderLayout.WEST);
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    public static JTable createStyledTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        return table;
    }

    public static void styleTable(JTable table) {
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(219, 232, 248));
        table.setSelectionForeground(UITheme.TEXT_PRIMARY);
        table.setBackground(UITheme.BG_CARD);
        table.setBorder(null);
        table.getTableHeader().setFont(UITheme.FONT_SUBHEAD);
        table.getTableHeader().setBackground(UITheme.TABLE_HEADER);
        table.getTableHeader().setForeground(UITheme.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    public static JScrollPane scrollPane(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(new RoundBorder(UITheme.BORDER, 16));
        sp.getViewport().setBackground(UITheme.BG_CARD);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        return sp;
    }

    public static JButton createNavButton(String text, String icon) {
        String label = sanitizeText(text);
        String glyph = sanitizeText(icon);
        
        final Color hoverBg = new Color(255, 255, 255, 24);

        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                boolean selected = Boolean.TRUE.equals(getClientProperty("selected"));
                boolean hover = Boolean.TRUE.equals(getClientProperty("hover"));
                
                Color fill = selected ? UITheme.PRIMARY : hover ? hoverBg : UITheme.TRANSPARENT;
                if (selected || hover) {
                    g2.setColor(fill);
                    g2.fillRoundRect(10, 4, getWidth() - 20, getHeight() - 8, 16, 16);
                }

                g2.setColor(selected ? Color.WHITE : new Color(196, 214, 235));
                g2.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 16));
                FontMetrics iconMetrics = g2.getFontMetrics();
                int iconY = (getHeight() + iconMetrics.getAscent() - iconMetrics.getDescent()) / 2;
                g2.drawString(glyph, 24, iconY);

                g2.setColor(selected ? Color.WHITE : getForeground());
                g2.setFont(UITheme.FONT_BODY);
                FontMetrics textMetrics = g2.getFontMetrics();
                int textY = (getHeight() + textMetrics.getAscent() - textMetrics.getDescent()) / 2;
                g2.drawString(label, 52, textY);
                g2.dispose();
            }
        };
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_LIGHT);
        btn.setBackground(UITheme.BG_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setToolTipText(label);
        btn.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH - 24, 46));
        btn.setMinimumSize(new Dimension(UITheme.SIDEBAR_WIDTH - 24, 46));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.putClientProperty("hover", Boolean.TRUE); btn.repaint(); }
            public void mouseExited(MouseEvent e) { btn.putClientProperty("hover", Boolean.FALSE); btn.repaint(); }
        });
        return btn;
    }

    public static void setNavButtonSelected(JButton button, boolean selected) {
        button.putClientProperty("selected", selected);
        button.repaint();
    }

    public static JPanel formRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(sanitizeText(label).toUpperCase());
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    public static JLabel createAlert(String message, Color bg) {
        JLabel lbl = new JLabel("<html>" + sanitizeText(message) + "</html>");
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return lbl;
    }

    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        RoundBorder(Color c, int r) { this.color = c; this.radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }
}
