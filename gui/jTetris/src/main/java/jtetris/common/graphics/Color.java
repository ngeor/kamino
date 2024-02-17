package jtetris.common.graphics;

/**
 * Color abstraction for both Swing and JavaFX.
 * Created by ngeor on 16/06/17.
 */
public final class Color {
    private static final double DARKER_FACTOR = 0.7;
    private final int red;
    private final int green;
    private final int blue;

    /**
     * Creates an instance of this class.
     */
    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(double red, double green, double blue) {
        this((int) red, (int) green, (int) blue);
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    Color darker() {
        return new Color(red * DARKER_FACTOR, green * DARKER_FACTOR, blue * DARKER_FACTOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Color color = (Color) o;

        return red == color.red && green == color.green && blue == color.blue;
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + green;
        result = 31 * result + blue;
        return result;
    }

    @Override
    public String toString() {
        return "Color R: " + red + " G: " + green + " B: " + blue;
    }
}
