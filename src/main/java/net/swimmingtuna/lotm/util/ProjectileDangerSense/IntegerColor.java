package net.swimmingtuna.lotm.util.ProjectileDangerSense;

/**
 * For some reason color in minecraft is encoded in A-R-G-B order, not R-G-B-A.
 */
public class IntegerColor {
    private final int color;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    /**
     * @param color ARGB
     */
    public IntegerColor(int color) {
        this.color = color;
        red = color >> 24 & 255;
        green = color >> 16 & 255;
        blue = color >> 8 & 255;
        alpha = color & 255;
    }

    public int getIntColor() {
        return color;
    }

    public int getRed() {
        return red;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getAlpha() {
        return alpha;
    }

}
