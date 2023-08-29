package cz.catmeows.emulator.tonda;

public interface SwingDisplay extends Runnable {
    void setPixels(int[] pixels);

    void requestRefresh();
    void waitForRefresh();
}
