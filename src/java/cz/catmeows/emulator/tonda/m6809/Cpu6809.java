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

    public void setCCNegative(boolean ccNegative) {
        setCCFlag(CC_NEG, ccNegative);
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
        int ea, value;
        int opcode = mem.read(regPC);
        //TODO debugger
        incPC();
        tickListener.tick();

        switch (opcode) {
            case 0x00:
                //NEG direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperNeg(value));
                tickListener.tick();
                break;
            case 0x01:
                //illegal NEG direct, 6
                break;
            case 0x02:
                //illegal
                break;
            case 0x03:
                //COM direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperCom(value));
                tickListener.tick();
                break;
            case 0x04:
                //LSR direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperLsr(value));
                tickListener.tick();
                break;
            case 0x05:
                //ilegal LSR direct, 6
                break;
            case 0x06:
                //ROR direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperRor(value));
                tickListener.tick();
                break;
            case 0x07:
                //ASR direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperAsr(value));
                tickListener.tick();
                break;
            case 0x08:
                //ASL,LSL direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperAsl(value));
                tickListener.tick();
                break;
        }


    }

    private void incPC() {
        regPC = (regPC+1)&0xffff;
    }

    private int getDirectLow() {
        int lowEA = mem.read(regPC);
        incPC();
        tickListener.tick();
        return (regDP<<8)+lowEA;
    }

    private int helperNeg(int value) {
        int result = (0 - (value&0xff))&0xff;
        setCCZero(result==0);
        setCCNegative((result & 0x80)==0x80);
        setCCCarry(result!=0);
        setCCOverflow(result==0x80);
        return result;
    }

    private int helperCom(int value) {
        int result = (value^0xff)&0xff;
        setCCZero(result==0);
        setCCNegative((result & 0x80)==0x80);
        setCCCarry(true);
        setCCOverflow(false);
        return result;
    }

    private int helperLsr(int value) {
        int result = ((value&0xff)>>1);
        setCCZero(result==0);
        setCCNegative(false);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperRor(int value) {
        int result = ((value&0xff)>>1)|(getCCCarry()?0x80:0x00);
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperAsr(int value) {
        int result = ((value&0xff)>>1)|(value&0x80);
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperAsl(int value) {
        int result = (value<<1)&0xff;
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCCarry((value&0x80)==0x80);
        setCCOverflow(((value^result)&0x80)==0x80); // b7 xor b6 of original operand
        return result;
    }
}
