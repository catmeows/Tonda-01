package cz.catmeows.emulator.tonda.marta;

public class MartaRegisters {
    private int memoryBankAt0000;
    private int videoPage;
    private int videoMode = 0;
    private MartaColors colors;
    private int lowFirqLatch;
    private int firqCounter = 0;
    private boolean firqAllowed = false;
    public MartaRegisters (MartaColors colorRegisters) {
        this.colors = colorRegisters;
    }
    public void writeControlRegister(int ctrlValues) {
        videoMode = ctrlValues & 0x01;
        switch ((ctrlValues & 0x18) >> 3) {
            case 0:
                memoryBankAt0000 = 0x0000;
                break;
            case 1:
                memoryBankAt0000 = 0xc000;
                break;
            case 2:
            case 3:
                memoryBankAt0000 = 0xe000;
        }
        videoPage = ((ctrlValues & 0x04) >> 2)==0 ? 0x0000 : 0xc000;
        firqAllowed = (ctrlValues & 0x80) == 0x80;
        firqCounter = ((ctrlValues & 0x40)<<2) + lowFirqLatch;
    }

    public void writeLowFirqCounter(int lowFirqCounter) {
        lowFirqLatch = lowFirqCounter;
    }
    public void writeColorRegister(int index, int color) {
        colors.setColor(index, color);
    }

    public void writeSoundRegister(int index, int value) {
        //TODO
    }
    public int getMemoryBankAt0000() {
        return memoryBankAt0000;
    }
    public int getVideoPage() {
        return videoPage;
    }
    public int getVideoMode() {
        return videoMode;
    }
    public boolean isFirqAllowed() {
        return firqAllowed;
    }
    public int getFirqCounter() {
        return firqCounter;
    }

}
