package cz.catmeows.emulator.tonda;

import cz.catmeows.emulator.tonda.marta.MartaColors;
import cz.catmeows.emulator.tonda.marta.MartaRegisters;
import cz.catmeows.emulator.tonda.marta.MartaVideo;

public class Marta implements AddressSpace {


    public Ram ram;
    private int register = 0;

    private int[] pixels = new int[336*288];

    private MartaColors martaColors;
    private MartaRegisters martaRegisters;

    private MartaVideo martaVideo;

    private SwingDisplay display;

    public Marta(Ram ram, SwingDisplay display) {
        this.ram = ram;
        this.display  = display;
        martaColors = new MartaColors();
        martaRegisters = new MartaRegisters(martaColors);
        martaVideo = new MartaVideo(martaRegisters, martaColors, ram, display);
    }
    @Override
    public int read(int address) {
        return 0xff;
    }

    @Override
    public void write(int address, int value) {
        if ((address&0x01)==0x00) {
            register = value & 0x3F;
        } else {
            switch (register) {
                case 0x00:
                    martaRegisters.writeControlRegister(value);
                    break;
                case 0x01:
                    martaRegisters.writeLowFirqCounter(value);
                    break;
                case 0x0F:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x18:
                case 0x19:
                case 0x1A:
                case 0x1B:
                case 0x1C:
                case 0x1D:
                case 0x1E:
                case 0x1F:
                    martaRegisters.writeColorRegister(register-0x0f, value);
                    break;
                case 0x20:
                case 0x21:
                case 0x22:
                case 0x23:
                case 0x30:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x38:
                    //FLO0[LLLLLLLL]
                    break;
                case 0x39:
                    //FHI0[....HHHH]
                    break;
                case 0x3A:
                    //SNL0[ssssssss]
                    break;
                case 0x3B:
                    //SNH0[SSSSSSSS]
                    break;
                case 0x3C:
                    //FLO0[LLLLLLLL]
                    break;
                case 0x3D:
                    //FHI0[....HHHH]
                    break;
                case 0x3E:
                    //SNL0[ssssssss]
                    break;
                case 0x3F:
                    //SNH0[SSSSSSSS]
                    break;
            }
        }
    }

    public boolean hasIrq(int tick) {
        if ((tick>=0) && (tick<=31)) {
            return true;
        }
        return false;
    }

    public boolean hasFirq(int tick) {
       if (martaRegisters.isFirqAllowed()) {
           if ((tick / 114) == martaRegisters.getFirqCounter()) {
               int lineCycle = tick % 114;
               if ( (lineCycle > 31 ) && (lineCycle < 64 )) return true;
           }
       }
        return false;
    }

    public void doVideo(int tick) {
        martaVideo.drawPixels(tick);
    }

    public int getBankOffset() {
       return martaRegisters.getMemoryBankAt0000();
    }

    public int[] getPixels() {
        return martaVideo.getPixels();
    }
}
