package cz.catmeows.emulator.tonda;

import java.awt.*;

public interface SwingDisplay extends Runnable {
    void setPixel(int pixel, int color);
    void requestRefresh();
    void waitForRefresh();
}
