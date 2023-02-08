package cz.catmeows.emulator.tonda.m6809;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.TickListener;

public class Cpu6809 {

    public final static int CC_CARRY = 0x01;
    public final static int CC_OVER = 0x02;
    public final static int CC_ZERO = 0x04;
    public final static int CC_NEG = 0x08;
    public final static int CC_IRQ = 0x10;
    public final static int CC_HALF = 0x20;
    public final static int CC_FIRQ = 0x40;
    public final static int CC_ENTIRE = 0x80;


    private int regA;
    private int regB;
    private int regX;
    private int regY;
    private int regU;
    private int regS;
    private int regDP;
    private int regPC;
    private int regCC;

    private AddressSpace mem;
    private TickListener tickListener;


    public Cpu6809(AddressSpace memory, TickListener tickListener) {
        this.mem = memory;
        this.tickListener = tickListener;
    }

    public int getDReg() {
        return (regA<<8)|regB;
    }

    public int getAReg() {
        return regA;
    }

    public int getBReg() {
        return regB;
    }

    public int getXreg() {
        return regX;
    }

    public int getYReg() {
        return regY;
    }

    public int getUReg() {
        return regU;
    }

    public int getSReg() {
        return regS;
    }

    public int getPCReg() {
        return regPC;
    }

    public int getDPReg() {
        return regDP;
    }

    public int getCCReg() {
        return regCC;
    }

    public void setDReg(int value) {
        regB = value&0xff;
        regA = (value&0xff00)>>8;
    }

    public void setAReg(int value) {
        regA = value&0xff;
    }

    public void setBReg(int value) {
        regB = value&0xff;
    }

    public void setXReg(int value) {
        regX = value&0xffff;
    }

    public void setYReg(int value) {
        regY = value&0xffff;
    }

    public void setUReg(int value) {
        regU = value&0xffff;
    }

    public void setSReg(int value) {
        regS = value&0xffff;
    }

    public void setPCReg(int value) {
        regPC = value&0xffff;
    }

    public void setDPReg(int value) {
        regDP = value&0xff;
    }

    public void setCCReg(int value) {
        regCC = value&0xff;
    }

    public boolean getCCZero() {
        return getCCFlag(CC_ZERO);
    }
    public boolean getCCCarry() {
        return getCCFlag(CC_CARRY);
    }
    public boolean getCCOverflow() {
        return getCCFlag(CC_OVER);
    }
    public boolean getCCNegative() {
        return getCCFlag(CC_NEG);
    }
    public boolean getCCIRQ() {
        return getCCFlag(CC_IRQ);
    }

    public boolean getCCHalfCarry() {
        return getCCFlag(CC_HALF);
    }

    public boolean getCCFIRQ() {
        return getCCFlag(CC_FIRQ);
    }

    public boolean getCCEntire() {
        return getCCFlag(CC_ENTIRE);
    }

    private boolean getCCFlag(int flagBit) {
        return (regCC&flagBit)==flagBit;
    }

    public void setCCZero(boolean ccZero) {
        setCCFlag(CC_ZERO, ccZero);
    }

    public void setCCCarry(boolean ccCarry) {
        setCCFlag(CC_CARRY, ccCarry);
    }

    public void setCCOverflow(boolean ccOverflow) {
        setCCFlag(CC_OVER, ccOverflow);
    }

    private void setCCFlag(int flagBit, boolean bit) {
        if (bit) {
            setCCReg(regCC|flagBit);
        } else {
            setCCReg(regCC&(flagBit^0xFFFF));
        }
    }

    public void nextInstruction() {

    }
}
