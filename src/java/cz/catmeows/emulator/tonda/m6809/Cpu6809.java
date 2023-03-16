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

    public void setCCZero(int value) {
        setCCZero((value&0xff)==0);
    }

    public void setCCCarry(boolean ccCarry) {
        setCCFlag(CC_CARRY, ccCarry);
    }

    public void setCCNegative(boolean ccNegative) {
        setCCFlag(CC_NEG, ccNegative);
    }

    public void setCCNegative(int value) {
        setCCNegative((value&0x80)==0x80);
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

    private int readByte(int address) {
        int result = mem.read(address&0xffff);
        tickListener.tick();
        return result;
    }

    private void writeByte(int address, int value) {
        mem.write(address, value);
        tickListener.tick();
    }

    private int readByteAtFFFF() {
        return readByte(0xffff);
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
        int opcode = readByte(regPC);
        //TODO debugger
        incPC();


        switch (opcode) {
            case 0x00:
                //NEG direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = mem.read(ea);
                readByteAtFFFF();
                writeByte(ea, helperNeg(value));
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
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperCom(value));
                break;
            case 0x04:
                //LSR direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperLsr(value));
                break;
            case 0x05:
                //ilegal LSR direct, 6
                break;
            case 0x06:
                //ROR direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperRor(value));
                break;
            case 0x07:
                //ASR direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperAsr(value));
                break;
            case 0x08:
                //ASL,LSL direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperAsl(value));
                break;
            case 0x09:
                //ROL direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperRol(value));
                break;
            case 0x0a:
                //DEC direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperDec(value));
                break;
            case 0x0b:
                //illegal
                break;
            case 0x0c:
                //INC direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperInc(value));
                break;
            case 0x0d:
                //TST direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                value = readByte(ea);
                readByteAtFFFF();
                helperTst(value);
                readByteAtFFFF();
                break;
            case 0x0e:
                //JMP direct, 3
                ea = getDirectLow();
                setPCReg(ea);
                readByteAtFFFF();
                break;
            case 0x0f:
                //CLR direct, 6
                ea = getDirectLow();
                readByteAtFFFF();
                readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperClr());
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
                readByte(regPC);
                break;
            case 0x13:
                //SYNC, 4+
                readByte(regPC);
                do {
                    //while waiting for interrupt, address lines are in high impendance state
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
                value = indexedEa();
                setCCZero(value==0);
                regX = value;
                readByteAtFFFF();
                break;
            case 0x31:
                //LEAY ,4+
                value = indexedEa();
                setCCZero(value==0);
                regY = value;
                readByteAtFFFF();
                break;
            case 0x32:
                //LEAS ,4+
                regS = indexedEa();
                readByteAtFFFF();
                break;
            case 0x33:
                //LEAU ,4+
                regU = indexedEa();
                readByteAtFFFF();
                break;
            case 0x34:
                //PSHS, 5+
                helperPush(false);
                break;
            case 0x35:
                //PULS, 5+

            case 0x36:
                //PSHU, 5+
                helperPush(true);
                break;
            case 0x37:
                //PULU, 5+
        }


    }

    private void incPC() {
        regPC = (regPC+1)&0xffff;
    }

    private int getDirectLow() {
        int lowEA = getImmediate();
        return (regDP<<8)+lowEA;
    }

    private int getImmediate() {
        int value = readByte(regPC)&0xff;
        incPC();
        return value;
    }

    private int getImmediateWord() {
        int ofsHi = getImmediate();
        int ofsLo = getImmediate();
        return ((ofsHi<<8)+ofsLo)&0xffff;
    }

    private int helperNeg(int value) {
        int result = (0 - (value&0xff))&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCCarry(result!=0);
        setCCOverflow(result==0x80);
        return result;
    }

    private int helperCom(int value) {
        int result = (value^0xff)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCCarry(true);
        setCCOverflow(false);
        return result;
    }

    private int helperLsr(int value) {
        int result = ((value&0xff)>>1);
        setCCZero(result);
        setCCNegative(false);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperRor(int value) {
        int result = ((value&0xff)>>1)|(getCCCarry()?0x80:0x00);
        setCCZero(result);
        setCCNegative(result);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperAsr(int value) {
        int result = ((value&0xff)>>1)|(value&0x80);
        setCCZero(result);
        setCCNegative(result);
        setCCCarry((value&0x01)==0x01);
        return result;
    }

    private int helperAsl(int value) {
        int result = (value<<1)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCCarry((value&0x80)==0x80);
        setCCOverflow(((value^result)&0x80)==0x80); // b7 xor b6 of original operand
        return result;
    }

    private int helperRol(int value) {
        int result = ((value<<1)|(getCCCarry()?0x01:00))&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCCarry((value&0x80)==0x80);
        return result;
    }

    private int helperDec(int value) {
        int result = (value-1)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCOverflow((value&0xff)==0x80);
        return result;
    }

    private int helperInc(int value) {
        int result = (value+1)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCOverflow((value&0xff)==0x7f);
        return result;
    }

    private void helperTst(int value) {
        setCCZero(value);
        setCCNegative(value);
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
        int ofsLo = getImmediate();
        int ofsHi = ((ofsLo&0x80)==0x80)?0xff:0x00;
        readByteAtFFFF();
        if (condition) {
            setPCReg((regPC+((ofsHi<<8)+ofsLo))&0xffff);
        }
    }

    private void helperLongBranch(boolean condition) {
        int offset = getImmediateWord();
        readByteAtFFFF();
        if (condition) {
            setPCReg((regPC+offset)&0xffff);
            readByteAtFFFF();
        }
    }

    private void helperLongSubroutine() {
        int offset = getImmediateWord();
        readByteAtFFFF();
        readByteAtFFFF();
        int ea = (regPC+offset)&0xffff;
        readByte(ea);
        readByteAtFFFF();
        regS = decWord(regS);
        writeByte(regS, regPC&0xff);
        regS = decWord(regS);
        writeByte(regS, (regPC&0xff00)>>8);
        regPC = ea;
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
        setCCZero(result);
        setCCNegative(result);
        regA = result;
        readByte(regPC);
    }

    private void helperExg(int postByte) {
        int r1 = postByte&0xf;
        int r2 = (postByte>>4)&0xf;
        int r1Value = getRegByPostByte(r1);
        int r2Value = getRegByPostByte(r2);
        setRegByPostByte(r1, r2Value);
        setRegByPostByte(r2, r1Value);
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
    }

    private void helperTfr(int postByte) {
        setRegByPostByte((postByte>>4)&0xf, getRegByPostByte(postByte&0xf));
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
        readByteAtFFFF();
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
            // 5b,R
            int ea = indexedGetReg(postByte);
            readByte(regPC+1);
            readByteAtFFFF();
            return (ea + ((postByte&0x1f)|(((postByte&0x10)==0x10)?0xffe0:0x0000)))&0xffff;
        }
        if (postByte==0x9f) {
            // [addr]
            int addr = getImmediateWord();
            readByte(regPC+1);
            return getIndirectEa(addr);
        }
        int ea=-1;
        switch (postByte&0x0f) {
            case 0x0:
                //R+
                ea = indexedGetReg(postByte);
                indexedRegPlus(postByte);
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x1:
                //R++
                ea = indexedGetReg(postByte);
                indexedRegPlus(postByte);
                indexedRegPlus(postByte);
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x2:
                //-R
                indexedRegMinus(postByte);
                ea = indexedGetReg(postByte);
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x3:
                //--R
                indexedRegMinus(postByte);
                indexedRegMinus(postByte);
                ea = indexedGetReg(postByte);
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x4:
                //,R
                ea = indexedGetReg(postByte);
                readByte(regPC+1);
                break;
            case 0x5:
                //B,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),regB);
                readByte(regPC+1);
                readByteAtFFFF();
                break;
            case 0x6:
                //A,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),regA);
                readByte(regPC+1);
                readByteAtFFFF();
                break;
            case 0x8:
                //n,R
                ea = addExtended8BitTo16Bit(indexedGetReg(postByte),getImmediate());
                readByteAtFFFF();
                break;
            case 0x9:
                //nn,R
                ea = add16BitTo16Bit(indexedGetReg(postByte),getImmediateWord());
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0xB:
                //D,R
                ea = add16BitTo16Bit(indexedGetReg(postByte), getDReg());
                readByte(regPC+1);
                readByte(regPC+2);
                readByte(regPC+3);
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0xC:
                //n,PC
                int ofs = getImmediate();
                ea = addExtended8BitTo16Bit(regPC, ofs);
                readByteAtFFFF();
                break;
            case 0xD:
                //nn,PC
                ofs = getImmediateWord();
                ea = add16BitTo16Bit(regPC, ofs);
                readByte(regPC+1);
                readByteAtFFFF();
                readByteAtFFFF();
                readByteAtFFFF();
                break;
        }
        if ((postByte&0x10)==0x10) {
            ea = getIndirectEa(ea);
        }
        return ea;
    }

    private int getIndirectEa(int addr) {
        int hi = readByte(addr);
        int lo = readByte((addr+1)&0xffff);
        readByteAtFFFF();
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

    private void helperPush(boolean uStack) {
        int postByte = getImmediate();
        int sp = uStack?regU:regS;
        int otherSp = uStack?regS:regU;
        readByteAtFFFF();
        readByteAtFFFF();
        readByte(sp);
        if ((postByte&0x80)==0x80) {
            sp = decWord(sp);
            writeByte(sp, getLowByte(regPC));
            sp = decWord(sp);
            writeByte(sp, getHighByte(regPC));
        }
        if ((postByte&0x40)==0x40) {
            sp = decWord(sp);
            writeByte(sp, getLowByte(otherSp));
            sp = decWord(sp);
            writeByte(sp, getHighByte(otherSp));
        }
        if ((postByte&0x20)==0x20) {
            sp = decWord(sp);
            writeByte(sp, getLowByte(regY));
            sp = decWord(sp);
            writeByte(sp, getHighByte(regY));
        }
        if ((postByte&0x10)==0x10) {
            sp = decWord(sp);
            writeByte(sp, getLowByte(regX));
            sp = decWord(sp);
            writeByte(sp, getHighByte(regX));
        }
        if ((postByte&0x08)==0x08) {
            sp = decWord(sp);
            writeByte(sp, getLowByte(regDP));
        }
        if ((postByte&0x04)==0x04) {
            sp = decWord(sp);
            writeByte(sp, regB);
        }
        if ((postByte&0x02)==0x02) {
            sp = decWord(sp);
            writeByte(sp, regA);
        }
        if ((postByte&0x01)==0x01) {
            sp = decWord(sp);
            writeByte(sp, regCC);
        }
        if (uStack) {
            regU = sp;
        } else {
            regS = sp;
        }
    }

    private static int addExtended8BitTo16Bit(int aWord, int aByte) {
        return (aWord + ((aByte&0x80)==0x80?0xffff:0x0000))&0xffff;
    }

    private static int add16BitTo16Bit(int aWord, int anotherWord) {
        return (aWord+anotherWord)&0xffff;
    }

    private static int decWord(int word) {
         return (word-1)&0xffff;
    }



    private int getLowByte(int word) {
        return word&0xff;
    }

    private int getHighByte(int word) {
        return (word&0xff00)>>8;
    }

}
