package cz.catmeows.emulator.tonda.m6821;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.TickListener;

public class M6821 implements AddressSpace {
    /* Minimal implementation that covers only features needed for emulator.
       For example, interrupt capability of CA1, CB1 lines is completely omitted
       because CA1, CB1 are connected to ground and IRQA, IRQB are not connected.

       PB0..PB7 printer data / PB7..PB5 keyboard row selector
       CB2      printer strobe
       PA7      printer busy
       PA6      tape out
       PA5      tape in
       PA4      joystick/keyboard clock
       PA3      joystick/keyboard latch
       PA2      joystick 1 data
       PA1      joystick 2 data
       PA0      keyboard data

     */

    private int portDirA;

    private int portDirB;

    private boolean selectDataA;

    private boolean selectDataB;

    private int portControlCA2mode;

    private int portControlCB2mode;

    private KeyboardMatrix keyboardMatrix;

    private TapeInterface tapeInterface;

    public M6821(KeyboardMatrix keyboardMatrix, TapeInterface tapeInterface) {
        this.keyboardMatrix = keyboardMatrix;
        this.tapeInterface = tapeInterface;
    }

    @Override
    public int read(int address) {
        int registerSelect = address % 4;
        switch (registerSelect) {
            case 0x00 -> {
                return selectDataA ? readDataA() : readDirectionA();
            }
            case 0x01 -> {
                return readControlA();
            }
            case 0x02 -> {
                return selectDataB ? readDataB() : readDirectionB();
            }
            case 0x03 -> {
                return readControlB();
            }
            default -> {
                //we really can't land here
                throw new IllegalArgumentException("Invalid 6821 register");
            }
        }
    }

    @Override
    public void write(int address, int value) {
        int registerSelect = address % 4;
        switch (registerSelect) {
            case 0x00 -> {
                if (selectDataA) {
                    writeDataA(value);
                } else {
                    writeDirectionA(value);
                }
            }
            case 0x01 -> writeControlA(value);
            case 0x02 -> {
                if (selectDataB) {
                    writeDataB(value);
                } else {
                    writeDirectionB(value);
                }
            }
            case 0x03 -> writeControlB((value));
        }
    }

    private void writeControlA(int value) {
        /*

         */

    }

    private void writeControlB(int value) {

    }

    private void writeDirectionA(int value) {
        this.portDirA = value;
    }

    private void writeDirectionB(int value) {
        this.portDirB = value;
    }

    private void writeDataA(int value) {
        if ((portDirA & 0x08)!=0) { //latch
            keyboardMatrix.setLatchState((value & 0x08)==0x08);
                //TODO latch joystick/mouse
        }
        if ((portDirA & 0x10)!=0) { //clock
            keyboardMatrix.setClockState((value & 0x10)==0x10);
                //TODO clock joystick/mouse
        }
        if ((portDirA & 0x40)!=0) { //tape out
            tapeInterface.setState((value & 0x40) == 0x40);
        }
    }

    private void writeDataB(int value) {
        if ((portDirB & 0x80)==0x80) {
            keyboardMatrix.setRow(2, (value & 0x80)==0x80);
        }
        if ((portDirB & 0x40)==0x40) {
            keyboardMatrix.setRow(1, (value & 0x40)==0x40);
        }
        if ((portDirB & 0x20)==0x20) {
            keyboardMatrix.setRow(0, (value & 0x20)==0x20);
        }
    }

    private int readDataA() {
        int result = 0;
        if ((portDirA & 0x01) != 0x01) { //read keyboard bit
            result = result + (keyboardMatrix.getBit()?0x01:0x00);
        }

        if ((portDirA & 0x0) != 0x20) { //tape in
            result = result + (tapeInterface.readState()?0x20:0x00);
        }
        return result;
    }

    private int readDataB() {
        return 0;
    }

    private int readDirectionA() {
        return portDirA;
    }

    private int readDirectionB() {
        return portDirB;
    }

    private int readControlA() {
        return 0;
    }

    private int readControlB() {
        return 0;
    }
}
