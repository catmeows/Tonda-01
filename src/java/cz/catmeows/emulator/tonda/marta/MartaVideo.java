package cz.catmeows.emulator.tonda.marta;

import cz.catmeows.emulator.tonda.Ram;
import cz.catmeows.emulator.tonda.SwingDisplay;

public class MartaVideo {

    private static final int TICKS_IN_LINE = 114;
    private static final int FIRST_TOP_BORDER_LINE = 15;
    private static final int LAST_TOP_BORDER_LINE = FIRST_TOP_BORDER_LINE + 55;
    private static final int FIRST_BOTTOM_BORDER_LINE = LAST_TOP_BORDER_LINE + 177;
    private static final int LAST_BOTTOM_BORDER_LINE = FIRST_BOTTOM_BORDER_LINE + 55;

    private static final int PIXELS_WIDTH = 336;
    private static final int PIXEL_HEIGHT = 288;
    private static final int HZ_FIRST_VISIBLE_CYCLE = 26;
    private static final int HZ_LAST_VISIBLE_CYCLE = 109;
    private static final int BORDER_COLOR_IDX = 0;
    private static final int ATTR_OFFSET = 40*176;


    private MartaRegisters martaRegisters;
    private MartaColors martaColors;

    private Ram ram;

    private int[] pixels = new int[PIXELS_WIDTH*PIXEL_HEIGHT];

    private int latchOne;
    private int latchTwo;
    private int latchOneTemp;

    private SwingDisplay display;


    public MartaVideo(MartaRegisters martaRegisters, MartaColors martaColors, Ram ram, SwingDisplay display) {
        this.martaRegisters = martaRegisters;
        this.martaColors = martaColors;
        this.ram = ram;
        this.display = display;
        martaColors.setColor(0, 6);
        martaColors.setColor(1, 12);
    }

    public int[] getPixels() {
        return pixels;
    }

    public void drawPixels(int tick) {
        //visible area: 336 (8+320+8) x 288 (56+176+56)
        //15 lines invisible
        //56 lines border
        //176 lines pixel area (26 cycles invisible, 2 cycles of border, 80 cycles of pixel area, 2 cycles of border,
        // 4 cycles invisible)
        //56 lines of border
        int line = tick / TICKS_IN_LINE;
        if ((line < FIRST_TOP_BORDER_LINE ) || (line > LAST_BOTTOM_BORDER_LINE)) {
            return;
        }
        if ( ((line>=FIRST_TOP_BORDER_LINE ) && (line <= LAST_TOP_BORDER_LINE))
                || (line >= FIRST_BOTTOM_BORDER_LINE) ) {
            drawBorders(tick);
        } else {
            drawScreen(tick);
        }
    }

    private void drawBorders(int tick) {
        int cycleInLine = tick % TICKS_IN_LINE;
        if ((cycleInLine >= HZ_FIRST_VISIBLE_CYCLE) && (cycleInLine <= HZ_LAST_VISIBLE_CYCLE)) {
            int pixelPtr = ((tick / TICKS_IN_LINE) - FIRST_TOP_BORDER_LINE) * PIXELS_WIDTH
                    + (cycleInLine - HZ_FIRST_VISIBLE_CYCLE) * 4;
            int color = martaColors.getColor(BORDER_COLOR_IDX);
            pixels[pixelPtr] = color;
            pixels[pixelPtr + 1] = color;
            pixels[pixelPtr + 2] = color;
            pixels[pixelPtr + 3] = color;
        }
    }

    private void drawScreen(int tick) {
        int cycleInLine = tick % TICKS_IN_LINE;
        int pixelLine = (tick / TICKS_IN_LINE) - LAST_TOP_BORDER_LINE - 1;
        if (    (cycleInLine == HZ_FIRST_VISIBLE_CYCLE) ||
                (cycleInLine == (HZ_FIRST_VISIBLE_CYCLE+1)) ||
                (cycleInLine == (HZ_LAST_VISIBLE_CYCLE-1)) ||
                (cycleInLine == (HZ_LAST_VISIBLE_CYCLE)) ) {
            drawBorders(tick);
        }

        if (martaRegisters.getVideoMode()==0) {
            if ((cycleInLine >= HZ_FIRST_VISIBLE_CYCLE) && (cycleInLine < (HZ_LAST_VISIBLE_CYCLE - 3))) {
                if ((cycleInLine % 2)==0) {
                    latchOneTemp = latchOne;
                    latchOne = getPixelByteMode0(pixelLine, cycleInLine);
                } else {
                    latchTwo = getAttrByteMode0(pixelLine, cycleInLine);
                }
            }
            if ((cycleInLine >= (HZ_FIRST_VISIBLE_CYCLE+2)) && (cycleInLine < (HZ_LAST_VISIBLE_CYCLE - 1))
                && ((cycleInLine % 2) == 0)) {
                int inkColor = martaColors.getColor((latchTwo & 0x0f) + 1);
                int paperColor = martaColors.getColor((latchTwo >> 4) + 1);
                int pixelBase = (pixelLine + 56) * PIXELS_WIDTH + (cycleInLine - HZ_FIRST_VISIBLE_CYCLE)*4;
                pixels[pixelBase] = ( latchOneTemp & 0x80) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 1] = (latchOneTemp & 0x40) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 2] = (latchOneTemp & 0x20) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 3] = (latchOneTemp & 0x10) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 4] = (latchOneTemp & 0x08) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 5] = (latchOneTemp & 0x04) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 6] = (latchOneTemp & 0x02) == 0 ? paperColor : inkColor;
                pixels[pixelBase + 7] = (latchOneTemp & 0x01) == 0 ? paperColor : inkColor;
            }
        } else {
            //mode 1
            if ((cycleInLine >= HZ_FIRST_VISIBLE_CYCLE) && (cycleInLine < (HZ_LAST_VISIBLE_CYCLE - 3))) {
                if ((cycleInLine % 2)==0) {
                    latchOneTemp = latchOne;
                    latchOne = getPixelByteMode1(pixelLine, cycleInLine);
                } else {
                    latchTwo = getPixelByteMode1(pixelLine, cycleInLine);
                }
            }
            if ((cycleInLine >= (HZ_FIRST_VISIBLE_CYCLE+2)) && (cycleInLine < (HZ_LAST_VISIBLE_CYCLE - 1))
                    && ((cycleInLine % 2) == 0)) {
                int pixelBase = (pixelLine + 56) * PIXELS_WIDTH + (cycleInLine - HZ_FIRST_VISIBLE_CYCLE*4);
                int pixel0 = martaColors.getColor(((latchOneTemp & 0xf0) >> 4) + 1);
                int pixel1 = martaColors.getColor((latchOneTemp & 0x0f) + 1);
                int pixel2 = martaColors.getColor(((latchTwo & 0xf0) >> 4) + 1);
                int pixel3 = martaColors.getColor((latchTwo & 0x0f) + 1);
                pixels[pixelBase] = pixel0;
                pixels[pixelBase + 1] = pixel0;
                pixels[pixelBase + 2] = pixel1;
                pixels[pixelBase + 3] = pixel1;
                pixels[pixelBase + 4] = pixel2;
                pixels[pixelBase + 5] = pixel2;
                pixels[pixelBase + 6] = pixel3;
                pixels[pixelBase + 7] = pixel3;

            }
        } //end mode 1
    }

    private int getPixelByteMode0(int pixelLine, int cycleInLine) {
        int basePtr = martaRegisters.getVideoPage();
        return ram.read(basePtr + pixelLine*40 +
                (cycleInLine-HZ_FIRST_VISIBLE_CYCLE)>>1 );

    }

    private int getAttrByteMode0(int pixelLine, int cycleInLine) {
        int basePtr = martaRegisters.getVideoPage();
        return ram.read(basePtr + ATTR_OFFSET +
                (pixelLine>>3)*40 + (cycleInLine - HZ_FIRST_VISIBLE_CYCLE)>>1 + 1);
    }

    private int getPixelByteMode1(int pixelLine, int cycleInLine) {
        int basePtr = martaRegisters.getVideoPage();
        return ram.read(basePtr + (pixelLine>>1)*40 + cycleInLine - HZ_FIRST_VISIBLE_CYCLE);
    }
}
