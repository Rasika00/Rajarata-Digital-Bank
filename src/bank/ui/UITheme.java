package bank.ui;

import java.awt.*;





public class UITheme {

    
    public static final Color PRIMARY       = new Color(63, 81, 181);
    public static final Color PRIMARY_DARK  = new Color(46, 60, 142);
    public static final Color PRIMARY_LIGHT = new Color(121, 134, 203);
    public static final Color SECONDARY     = new Color(34, 139, 94);
    public static final Color ACCENT        = new Color(234, 120, 73);
    public static final Color TEAL          = new Color(45, 142, 154);
    
    public static final Color DANGER        = new Color(239, 68, 68);
    public static final Color SUCCESS       = SECONDARY;
    public static final Color WARNING       = new Color(245, 158, 11);
    public static final Color INFO          = new Color(59, 130, 246);
    
    
    public static final Color BG_DARK       = new Color(15, 23, 42);
    public static final Color BG_SIDEBAR    = new Color(25, 35, 56);
    public static final Color BG_MAIN       = new Color(244, 247, 251);
    public static final Color BG_CARD       = new Color(255, 255, 255, 232);
    public static final Color BG_INPUT      = new Color(241, 245, 249);
    public static final Color BG_SOFT       = new Color(226, 232, 240);
    public static final Color GLASS_BORDER  = new Color(255, 255, 255, 120);
    
    
    public static final Color TEXT_PRIMARY  = new Color(15, 23, 42);
    public static final Color TEXT_MUTED    = new Color(86, 101, 124);
    public static final Color TEXT_LIGHT    = new Color(241, 245, 249);
    
    
    public static final Color BORDER        = new Color(226, 232, 240);
    public static final Color BORDER_STRONG = new Color(203, 213, 225);
    public static final Color TABLE_HEADER  = new Color(248, 250, 252);
    public static final Color TABLE_ALT     = new Color(241, 245, 249);
    public static final Color SHADOW        = new Color(15, 23, 42, 14);
    public static final Color LOGOUT_BTN    = new Color(239, 68, 68);

    
    public static final Color OVERLAY_DARK  = new Color(0, 0, 0, 24);
    public static final Color OVERLAY_LIGHT = new Color(255, 255, 255, 24);
    public static final Color TRANSPARENT   = new Color(0, 0, 0, 0);

    
    public static final String FONT_FAMILY  = "Poppins";
    public static final Font FONT_TITLE     = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_HEADING   = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBHEAD   = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY      = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL     = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_MONO      = new Font("Consolas", Font.PLAIN, 13);

    
    public static final int SIDEBAR_WIDTH   = 260;
    public static final int SIDEBAR_COLLAPSED_WIDTH = 92;
    public static final int CARD_RADIUS     = 18;
    public static final int SPACE_1         = 8;
    public static final int SPACE_2         = 16;
    public static final int SPACE_3         = 24;
    public static final int SPACE_4         = 32;
    public static final Insets CARD_PADDING = new Insets(24, 26, 24, 26);
}
