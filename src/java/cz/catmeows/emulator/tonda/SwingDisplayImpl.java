package cz.catmeows.emulator.tonda;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SwingDisplayImpl extends JPanel implements Runnable, SwingDisplay {

    public static final int DISPLAY_WIDTH = 336;
    public static final int DISPLAY_HEIGHT = 288;
    private final BufferedImage img; // = new BufferedImage(DISPLAY_WIDTH, DISPLAY_HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] rgb;
    private boolean doRefresh = false;

    public SwingDisplayImpl() {
        super();
        this.setPreferredSize(new Dimension(DISPLAY_WIDTH*2, DISPLAY_HEIGHT*2));
        GraphicsConfiguration gfxConfig = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        img = gfxConfig.createCompatibleImage(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        rgb = new int[DISPLAY_WIDTH * DISPLAY_HEIGHT];

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, DISPLAY_WIDTH * 2, DISPLAY_HEIGHT * 2, null);
    }

    public void setPixels(int[] pixels) {
        rgb = pixels;
    }

    public synchronized void requestRefresh() {
        doRefresh = true;
        notifyAll();
    }

    public synchronized void waitForRefresh() {
        while (doRefresh) {
            try {
                wait(1);
            } catch (InterruptedException ie) {
                break;
            }
        }
    }

    public void run() {
        doRefresh = false;
        while (true) {
            synchronized (this) {
                try {
                    wait(1);
                } catch (InterruptedException ie) {
                    break;
                }
            }
            if (doRefresh) {
                img.setRGB(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, rgb, 0, DISPLAY_WIDTH);
                validate();
                repaint();
                synchronized (this) {
                    doRefresh = false;
                    notifyAll();
                }
            }
        }
    }
}
