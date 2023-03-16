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

    private boolean lineNMI;
    private boolean lineIRQ;
    private boolean lineFIRQ;

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

    public void setLineNMI(boolean nmi) {
        lineNMI = nmi;
    }

    public void setLineIRQ(boolean irq) {
        lineIRQ = irq;
    }

    public void setLineFIRQ(boolean firq) {
        lineFIRQ = firq;
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
            case 0x09:
                //ROL direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperRol(value));
                tickListener.tick();
                break;
            case 0x0a:
                //DEC direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperDec(value));
                tickListener.tick();
                break;
            case 0x0b:
                //illegal
                break;
            case 0x0c:
                //INC direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperInc(value));
                tickListener.tick();
                break;
            case 0x0d:
                //TST direct, 6
                ea = getDirectLow();
                tickListener.tick();
                value = mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                helperTst(value);
                tickListener.tick();
                break;
            case 0x0e:
                //JMP direct, 3
                ea = getDirectLow();
                setPCReg(ea);
                tickListener.tick();
                break;
            case 0x0f:
                //CLR direct, 6
                ea = getDirectLow();
                tickListener.tick();
                mem.read(ea);
                tickListener.tick();
                tickListener.tick();
                mem.write(ea, helperClr());
                tickListener.tick();
                break;
            case 0x10:
                //Page 2
                //TODO Implement page 2 opcodes
                break;
            case 0x11:
                //Page 3
                //TODO Implement page 3 opcodes
                break;
            case 0x12:
                //NOP, 2
                tickListener.tick();
                break;
            case 0x13:
                //SYNC, 4+
                tickListener.tick();
                do {
                    tickListener.tick();
                } while (!(lineNMI||lineIRQ||lineFIRQ));
                tickListener.tick();
                break;
            case 0x14:
                //illegal
                break;
            case 0x15:
                //illegal
                break;
            case 0x16:
                //LBRA, 5
                helperLongBranch(true);
                break;
            case 0x17:
                //LBSR, 9
                helperLongSubroutine();
                break;
            case 0x18:
                //illegal
                break;
            case 0x19:
                //DAA, 2
                helperDaa();
                break;
            case 0x1A:
                //ORCC immediate, 3
                setCCReg((getImmediate()|getCCReg())&0xff);
                tickListener.tick();
                break;
            case 0x1B:
                //illegal
                break;
            case 0x1C:
                //ANDCC immediate, 3
                setCCReg((getImmediate()&getCCReg())&0xff);
                tickListener.tick();
                break;
            case 0x1D:
                //SEX, 2
                regA = ((regB&0x80)==0x80)?0xff:0x00;
                setCCNegative((regB&0x80)==0x80);
                setCCZero(regB==0);
                tickListener.tick();
                break;
            case 0x1E:
                //EXG postbyte, 8
                helperExg(getImmediate());
                break;
            case 0x1F:
                //TFR postbyte, 6
                helperTfr(getImmediate());
                break;
            case 0x20:
                //BRA offset, 3
                helperBranch(true);
                break;
            case 0x21:
                //BRN offset, 3
                helperBranch(false);
                break;
            case 0x22:
                //BHI offset, 3
                helperBranch(!(getCCCarry()||getCCZero()));
                break;
            case 0x23:
                //BLS offset, 3
                helperBranch(getCCCarry()||getCCZero());
                break;
            case 0x24:
                //BCC offset, 3
                helperBranch(!getCCCarry());
                break;
            case 0x25:
                //BCS offset, 3
                helperBranch(getCCCarry());
                break;
            case 0x26:
                //BNE offset, 3
                helperBranch(!getCCZero());
                break;
            case 0x27:
                //BEQ offset, 3
                helperBranch(getCCZero());
                break;
            case 0x28:
                //BVC offset, 3
                helperBranch(!getCCOverflow());
                break;
            case 0x29:
                //BVS offset, 3
                helperBranch(getCCOverflow());
                break;
            case 0x2A:
                //BPL offset, 3
                helperBranch(!getCCNegative());
                break;
            case 0x2B:
                //BMI offset, 3
                helperBranch(getCCNegative());
                break;
            case 0x2C:
                //BGE offset, 3
                helperBranch(getCCNegative()==getCCOverflow());
                break;
            case 0x2D:
                //BLT offset, 3
                helperBranch(getCCNegative()!=getCCOverflow());
                break;
            case 0x2E:
                //BGT offset, 3
                helperBranch((!getCCZero())&&(getCCNegative()==getCCOverflow()));
                break;
            case 0x2F:
                //BLE offset, 3
                helperBranch(getCCZero()||(getCCNegative()!=getCCOverflow()));
                break;
            case 0x30:
                //LEAX ,4+

            case 0x31:
                //LEAY ,4+


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

    private int getImmediate() {
        int value = mem.read(regPC)&0xff;
        incPC();
        tickListener.tick();
        return value;
    }

    private int getImmediateWord() {
        int ofsHi = mem.read(regPC);
        incPC();
        tickListener.tick();
        int ofsLo = mem.read(regPC);
        incPC();
        tickListener.tick();
        return ((ofsHi<<8)+ofsLo)&0xffff;
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

    private int helperRol(int value) {
        int result = ((value<<1)|(getCCCarry()?0x01:00))&0xff;
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCCarry((value&0x80)==0x80);
        return result;
    }

    private int helperDec(int value) {
        int result = (value-1)&0xff;
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCOverflow((value&0xff)==0x80);
        return result;
    }

    private int helperInc(int value) {
        int result = (value+1)&0xff;
        setCCZero(result==0);
        setCCNegative((result&0x80)==0x80);
        setCCOverflow((value&0xff)==0x7f);
        return result;
    }

    private void helperTst(int value) {
        setCCZero((value&0xff)==0);
        setCCNegative((value&0x80)==0x80);
        setCCOverflow(false);
    }

    private int helperClr() {
        setCCZero(true);
        setCCNegative(false);
        setCCOverflow(false);
        setCCCarry(false);
        return 0;
    }

    private void helperBranch(boolean condition) {
        int ofsLo = mem.read(regPC);
        incPC();
        tickListener.tick();
        int ofsHi = ((ofsLo&0x80)==0x80)?0xff:0x00;
        tickListener.tick();
        if (condition) {
            setPCReg((regPC+((ofsHi<<8)+ofsLo))&0xffff);
        }
    }

    private void helperLongBranch(boolean condition) {
        int ofsHi = mem.read(regPC);
        incPC();
        tickListener.tick();
        int ofsLo = mem.read(regPC);
        incPC();
        tickListener.tick();
        tickListener.tick();
        if (condition) {
            setPCReg((regPC+((ofsHi<<8)+ofsLo))&0xffff);
            tickListener.tick();
        }
    }

    private void helperLongSubroutine() {
        int ofsHi = mem.read(regPC);
        incPC();
        tickListener.tick();
        int ofsLo = mem.read(regPC);
        incPC();
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
        int ea = (regPC+((ofsHi<<8)+ofsLo))&0xffff;
        mem.read(ea);
        tickListener.tick();
        tickListener.tick();
        regS=(regS-1)&0xffff;
        mem.write(regS, regPC&0xff);
        tickListener.tick();
        regS=(regS-1)&0xffff;
        mem.write(regS, (regPC&0xff00)>>8);
        regPC = ea;
        tickListener.tick();
    }

    private void helperDaa() {
        int lowNibble = regA&0x0f;
        int hiNibble = (regA&0xf0)>>4;
        /*  So now we are after ADDA or ADCA instruction
            for lower nibble, either decimal overflowed to hexadecimal e.g. 6+6=0xC
                              or it overflowed to next nibble e.g. 9+9=0x12 (half-carry flag is set)
                              if so, we need to adjust lower nibble
        */
        int loCorrection = ((lowNibble>9)|getCCHalfCarry())?0x06:00;
        /*
            for higher nibble, decimal overflowed to hexadecimal
                               or result overflowed to next byte
                               or the lower nibble overflowed to hexadecimal and higher nibble is 9 or greater:
                                  if so, loCorrection will add 6 to low nibble, causing increase in hi nibble too
                                  and moving hi nibble outside of decimal range
                               note, if half-carry is set then higher nibble was increased by one during ADDA/ADCA
                               already
        */
        int hiCorrection = (getCCCarry() || (hiNibble>9) || ((hiNibble>8)&&(lowNibble>9))) ? 0x60:0x00;
        int result = regA + (loCorrection | hiCorrection);
        /*
            set carry if there is overflow to next byte, the carry flag should be preserved for following ADCA
            if there was carry after last ADCA/ADDA e.g. 0x90+0x90=0x120, we need it to preserve for following ADCA
        */
        setCCCarry(getCCCarry() || ((result&0x100) == 0x100));
        setCCZero((result&0xff)==0);
        setCCNegative((result&0x80)==0x80);
        regA = result;
        tickListener.tick();
    }

    private void helperExg(int postByte) {
        int r1 = postByte&0xf;
        int r2 = (postByte>>4)&0xf;
        int r1Value = getRegByPostByte(r1);
        int r2Value = getRegByPostByte(r2);
        setRegByPostByte(r1, r2Value);
        setRegByPostByte(r2, r1Value);
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
    }

    private void helperTfr(int postByte) {
        setRegByPostByte((postByte>>4)&0xf, getRegByPostByte(postByte&0xf));
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
        tickListener.tick();
    }

    private void setRegByPostByte(int code, int value) {
        switch (code) {
            case 0x0:
                setDReg(value);
                break;
            case 0x1:
                regX = value&0xffff;
                break;
            case 0x2:
                regY = value&0xffff;
                break;
            case 0x3:
                regU = value&0xffff;
                break;
            case 0x4:
                regS = value&0xffff;
                break;
            case 0x5:
                regPC = value&0xffff;
                break;
            case 0x8:
                regA = value&0xff;
                break;
            case 0x9:
                regB = value&0xff;
                break;
            case 0xA:
                regCC = value&0xff;
                break;
            case 0xB:
                regDP = value&0xff;
                break;
        }
    }

    private int getRegByPostByte(int code) {
        switch (code) {
            case 0x0:
                return getDReg();
            case 0x1:
                return regX;
            case 0x2:
                return regY;
            case 0x3:
                return regU;
            case 0x4:
                return regS;
            case 0x5:
                return regPC;
            case 0x8:
                return 0xff00+regA;
            case 0x9:
                return 0xff00+regB;
            case 0xA:
                return 0xff00+regCC;
            case 0xB:
                return 0xff00+regDP;
        }
        return 0xffff;
    }

    private int indexedEa() {
        int postByte = getImmediate();
        if ((postByte&0x80)==0) {
            int ea = indexedGetReg(postByte);
            return (ea + ((postByte&0x1f)|(((postByte&0x10)==0x10)?0xffe0:0x0000)))&0xffff;
        }
        if (postByte==0x9f) {
            int addr = getImmediateWord();
            tickListener.tick();
            return getIndirectEa(addr);
        }
        int ea=-1;
        switch (postByte&0x0f) {
            case 0x0:
                //R+
                ea = indexedGetReg(postByte);
                indexedRegPlus(postByte);
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x1:
                //R++
                ea = indexedGetReg(postByte);
                indexedRegPlus(postByte);
                indexedRegPlus(postByte);
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x2:
                //-R
                indexedRegMinus(postByte);
                ea = indexedGetReg(postByte);
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x3:
                //--R
                indexedRegMinus(postByte);
                indexedRegMinus(postByte);
                ea = indexedGetReg(postByte);
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x4:
                //,R
                ea = indexedGetReg(postByte);
                tickListener.tick();
                break;
            case 0x5:
                //B,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),regB);
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x6:
                //A,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),regA);
                tickListener.tick();
                tickListener.tick();
                break;
            case 0x8:
                //n,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),getImmediate());
                tickListener.tick();
                break;
            case 0x9:
                //nn,R
                ea = add16BitTo16Bit(indexedGetReg(postByte),getImmediateWord());
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0xB:
                //D,R
                ea = add16BitTo16Bit(indexedGetReg(postByte), getDReg());
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
            case 0xC:
                //n,PC
                int ofs = getImmediate();
                ea = addExtended8BitTo16Bit(regPC, ofs);
                tickListener.tick();
                break;
            case 0xD:
                //nn,PC
                ofs = getImmediateWord();
                ea = add16BitTo16Bit(regPC, ofs);
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                tickListener.tick();
                break;
        }
        if ((postByte&0x10)==0x10) {
            ea = getIndirectEa(ea);
        }
        return ea;
    }

    private int getIndirectEa(int addr) {
        int hi = mem.read(addr);
        tickListener.tick();
        int lo = mem.read((addr+1)&0xffff);
        tickListener.tick();
        tickListener.tick();
        return ((hi<<8)+lo)&0xffff;
    }

    private int indexedGetReg(int postByte) {
        switch (postByte&0x60) {
            case 0x00:
                return regX;
            case 0x20:
                return regY;
            case 0x40:
                return regU;
        }
        return regS;
    }

    private void indexedRegMinus(int postByte) {
        switch (postByte&0x60) {
            case 0x00:
                regX = (regX - 1)&0xffff;
                break;
            case 0x20:
                regY = (regY - 1)&0xffff;
                break;
            case 0x40:
                regU = (regU - 1)&0xffff;
                break;
            case 0x60:
                regS = (regS - 1)&0xffff;
                break;
        }
    }

    private void indexedRegPlus(int postByte) {
        switch (postByte&0x60) {
            case 0x00:
                regX = (regX + 1)&0xffff;
                break;
            case 0x20:
                regY = (regY + 1)&0xffff;
                break;
            case 0x40:
                regU = (regU + 1)&0xffff;
                break;
            case 0x60:
                regS = (regS + 1)&0xffff;
                break;
        }
    }

    private int addExtended8BitTo16Bit(int aWord, int aByte) {
        return (aWord + ((aByte&0x80)==0x80?0xffff:0x0000))&0xffff;
    }

    private int add16BitTo16Bit(int aWord, int anotherWord) {
        return (aWord+anotherWord)&0xffff;
    }

}
