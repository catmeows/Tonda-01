package cz.catmeows.emulator.tonda;

public class Ram implements AddressSpace {

    int[] ram = new int[65536];
    @Override
    public int read(int address) {
        return ram[address];
    }

    @Override
    public void write(int address, int value) {
        ram[address]=value;
    }
}
