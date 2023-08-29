package cz.catmeows.emulator.tonda.marta;

public class MartaColors {

    private int[] colors = new int [17];

    public void setColor(int index, int rgb6) {
        colors[index] = 0xFF000000 + ((rgb6&0x30)<<(16+6-4)) + ((rgb6&0x0C)<<(8+6-2)) + ((rgb6&0x03)<<6);
    }

    public int getColor(int index) {
        return colors[index];
    }
 }
