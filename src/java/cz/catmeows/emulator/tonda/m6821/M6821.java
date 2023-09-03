package cz.catmeows.emulator.tonda.m6821;

import cz.catmeows.emulator.tonda.AddressSpace;

public class M6821 implements AddressSpace {

    private KeyboardMatrix keyboardMatrix;

    public M6821(KeyboardMatrix keyboardMatrix) {
        this.keyboardMatrix = keyboardMatrix;
    }
    @Override
    public int read(int address) {
        return 0;
    }

    @Override
    public void write(int address, int value) {

    }
}
