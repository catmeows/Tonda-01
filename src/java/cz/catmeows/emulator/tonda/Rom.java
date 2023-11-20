package cz.catmeows.emulator.tonda;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Rom implements AddressSpace {

    int [] rom;

    public Rom(int size) {
        rom = new int[size];
    }

    public void load(String fileName) throws IOException {
        File f = new File(fileName);
        System.out.println("Rom path: "+f.getAbsolutePath());
            InputStream is = new FileInputStream(fileName);

            byte[] allBytes = is.readAllBytes();
            for (int i=0;i<allBytes.length;i++) {
                rom[i]=allBytes[i]&0xff;
            }
    }

    @Override
    public int read(int address) {
        return rom[address];
    }

    @Override
    public void write(int address, int value) {
        //do nothing
    }
}
