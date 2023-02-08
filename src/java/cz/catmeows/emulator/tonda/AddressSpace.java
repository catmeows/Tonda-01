package cz.catmeows.emulator.tonda;

public interface AddressSpace {

    int read(int address);

    void write(int address, int value);
}
