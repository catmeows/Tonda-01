package cz.catmeows.emulator.tonda;

import cz.catmeows.emulator.tonda.m6809.Cpu6809;
import cz.catmeows.emulator.tonda.m6821.KeyboardMatrix;
import cz.catmeows.emulator.tonda.m6821.M6821;
import cz.catmeows.emulator.tonda.m6821.TapeInterface;
import cz.catmeows.emulator.tonda.marta.Marta;

public class TondaSystem implements Runnable, AddressSpace, TickListener {

    private SwingDisplay display;
    private Ram ram  = new Ram();
    private Rom sysRom = new Rom(16384);

    private Rom diskRom = new Rom(4096);

    private Marta marta;
    private M6821 m6821;
    private DiskSystem diskSystem;
    
    private Cpu6809 cpu;

    private int tickCount = 0;

    private long lastTime = System.currentTimeMillis();

    private boolean diskEnable = false;

    private boolean diskRomMapped = false;

    public TondaSystem(SwingDisplay display, KeyboardMatrix keyboardMatrix) {
        this.display = display;
        marta = new Marta(ram, display);
        cpu = new Cpu6809(this, this);
        m6821 = new M6821(keyboardMatrix, new TapeInterface());
    }

    @Override
    public int read(int address) {
        if (address>=0x0000 && address<=0x1fff) {
            //0x0000..0x1FFF reads Ram mapped to lower 8K
            return ram.read(marta.getBankOffset()+address);
        } else if (address>=0x2000 && address<=0xbfbf) {
            //0x2000..0xBFBF reads Ram
            return ram.read(address);
        } else if (address>=0xbfc0 && address<=0xbfff) {
            //0xBFC0..BFFF reads Marta
            return marta.read(address);
        } else if (address>=0xc000 && address<=0xc0ff) {
            //0xC000..0xC0FF reads either disk Rom or system Rom
            //if disk interface is attached, access to this area
            //switches disk mapper to disk Rom
            if (diskEnable) {
                diskRomMapped = true;
            }
            if (diskRomMapped) {
                return diskRom.read(address - 0xc000);
            } else {
                return sysRom.read(address - 0xc000);
            }
        } else if (address>=0xc100 && address<=0xc1ff) {
            //0xC100..0xC1FF always reads system Rom
            //and also switches disk mapper to system Rom
            diskRomMapped = false;
            return sysRom.read(address - 0xc000);
        } else if (address>=0xc200 && address<=0xcFFF) {
            //0xC200..0xCFFF reads either disk Rom or system Rom
            //depends on state of disk mapper
            if (diskRomMapped) {
                return diskRom.read(address - 0xC000);
            } else {
                return sysRom.read(address - 0xC000);
            }
        } else if (address>=0xd000 && address<=0xdfbf) {
            //0xD000..0xDFBF always reads sytem ROM
            return sysRom.read(address - 0xc000);
        } else if (address>=0xdfc0 && address<=0xdfdf) {
            //0xDFC0..0xDFdf always reads 6821
            return m6821.read(address & 0x03);
        } else if (address>=0xdfe0 && address<=0xdfff) {
            //0xDFE0..0xDFFF access disk system ports
            //or 6821 if sidk is not connected
            if (diskEnable) {
                return diskSystem.read(address & 0xff);
            } else {
                return m6821.read(address & 0x03);
            }
        } else {
            //0xE000..0xFFFF system rom
            return sysRom.read(address - 0xc000);
        }
    }

    @Override
    public void write(int address, int value) {
        if (address>=0x0000 && address<=0x1FFF) {
            //0x0000..0x1FFF writes Ram mapped to lower 8K
            ram.write(marta.getBankOffset()+address, value);
        } else if (address>=0x2000 && address<=0xbfbf) {
            //0x2000..0xBFBF writes Ram
            ram.write(address, value);
        } else if (address>=0xbfc0 && address<=0xbfff) {
            //0xBFC0..BFFF writes Marta
            marta.write(address, value);
        } else if (address>=0xc000 && address<=0xc0ff) {
            //cannot write Rom
            //if disk interface is attached, access to this area
            //switches disk mapper to disk Rom
            if (diskEnable) {
                diskRomMapped = true;
            }
        } else if (address>=0xc100 && address<=0xc1ff) {
            //cannot write Rom
            //and also switches disk mapper to system Rom
            diskRomMapped = false;
        } else if (address>=0xdfc0 && address<=0xdfdf) {
            m6821.write(address & 0x03, value);
        } else if (address>=0xdfe0 && address<=0xdfff) {
            if (diskEnable) {
                diskSystem.write(address & 0xff, value);
            } else {
                m6821.write(address & 0x03, value);
            }
        }
    }

    @Override
    public void tick() {
        marta.doVideo(tickCount);
        if (marta.hasFirq(tickCount)) {
            cpu.irq();
        }
        if (marta.hasIrq(tickCount)) {
            cpu.firq();
        }
        tickCount++;
        if (tickCount==35454) {
            tickCount = 0;
            display.setPixels(marta.getPixels());
            while ((System.currentTimeMillis() - lastTime)<20) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
            lastTime = System.currentTimeMillis();
            display.requestRefresh();
            display.waitForRefresh();
        }
    }

    @Override
    public void run() {
        while (true) {
            cpu.nextInstruction();
        }
    }
}
