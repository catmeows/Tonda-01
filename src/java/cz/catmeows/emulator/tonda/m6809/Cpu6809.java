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

    public void setCCHalf(boolean ccHalf) {
        setCCFlag(CC_HALF, ccHalf);
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
        int result = mem.read(address&0xffff)&0xff;
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

    private int readWord(int address) {
        int hi = readByte(address);
        return ((hi<<8)+readByte(address+1))&0xffff;
    }

    private void writeWord(int address, int value) {
        int lo = value&0xff;
        int hi = (value>>8)&0xff;
        writeByte(address, hi);
        writeByte(address, lo);
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
                ea = getEaDirect();
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
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperCom(value));
                break;
            case 0x04:
                //LSR direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperLsr(value));
                break;
            case 0x05:
                //ilegal LSR direct, 6
                break;
            case 0x06:
                //ROR direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperRor(value));
                break;
            case 0x07:
                //ASR direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperAsr(value));
                break;
            case 0x08:
                //ASL,LSL direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperAsl(value));
                break;
            case 0x09:
                //ROL direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperRol(value));
                break;
            case 0x0a:
                //DEC direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperDec(value));
                break;
            case 0x0b:
                //illegal
                break;
            case 0x0c:
                //INC direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperInc(value));
                break;
            case 0x0d:
                //TST direct, 6
                ea = getEaDirect();
                value = readByte(ea);
                readByteAtFFFF();
                helperTst(value);
                readByteAtFFFF();
                break;
            case 0x0e:
                //JMP direct, 3
                ea = getEaDirect();
                setPCReg(ea);
                break;
            case 0x0f:
                //CLR direct, 6
                ea = getEaDirect();
                readByte(ea);
                readByteAtFFFF();
                writeByte(ea, helperClr());
                break;
            case 0x10:
                //Page 2
                page0x10();
                break;
            case 0x11:
                //Page 3
                page0x11();
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
                readByteAtFFFF();
                break;
            case 0x1B:
                //illegal
                break;
            case 0x1C:
                //ANDCC immediate, 3
                setCCReg((getImmediate()&getCCReg())&0xff);
                readByteAtFFFF();
                break;
            case 0x1D:
                //SEX, 2
                regA = ((regB&0x80)==0x80)?0xff:0x00;
                setCCNegative(regB);
                setCCZero(regB);
                readByteAtFFFF();
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
                helperPull(false);
            case 0x36:
                //PSHU, 5+
                helperPush(true);
                break;
            case 0x37:
                //PULU, 5+
                helperPull(true);
                break;
            case 0x38:
                //illegal
                break;
            case 0x39:
                //RTS, 5
                readByteAtFFFF();
                helperReturn();
                break;
            case 0x3A:
                //ABX, 3
                readByte(regPC);
                readByteAtFFFF();
                regX = (regX+regB)&0xffff;
                break;
            case 0x3B:
                //RTI, 6/15
                helperRti();
                break;
            case 0x3C:
                //CWAI, 20
                helperCwai();
                break;
            case 0x3D:
                //MUL, 11
                helperMul();
                break;
            case 0x3E:
                //illegal
                break;
            case 0x3F:
                //SWI, 19
                helperSwi();
                break;
            case 0x40:
                //NEGA, 2
                regA = helperNeg(regA);
                readByte(regPC);
                break;
            case 0x41:
                //illegal
                break;
            case 0x42:
                //illegal
                break;
            case 0x43:
                //COMA, 2
                regA = helperCom(regA);
                readByte(regPC);
                break;
            case 0x44:
                //LSRA, 2
                regA = helperLsr(regA);
                readByte(regPC);
                break;
            case 0x45:
                //illegal
                break;
            case 0x46:
                //RORA, 2
                regA = helperRor(regA);
                readByte(regPC);
                break;
            case 0x47:
                //ASRA, 2
                regA = helperAsr(regA);
                readByte(regPC);
                break;
            case 0x48:
                //ASLA, 2
                regA = helperAsl(regA);
                readByte(regPC);
                break;
            case 0x49:
                //ROLA, 2
                regA = helperRol(regA);
                readByte(regPC);
                break;
            case 0x4A:
                //DECA, 2
                regA = helperDec(regA);
                readByte(regPC);
                break;
            case 0x4B:
                //illegal
                break;
            case 0x4C:
                //INCA, 2
                regA = helperInc(regA);
                readByte(regPC);
                break;
            case 0x4D:
                //TSTA, 2
                helperTst(regA);
                readByte(regPC);
                break;
            case 0x4E:
                //illegal
                break;
            case 0x4F:
                //CLRA, 2
                regA = helperClr();
                readByte(regPC);
                break;
            case 0x50:
                //NEGB, 2
                regB = helperNeg(regB);
                readByte(regPC);
                break;
            case 0x51:
                //illegal
                break;
            case 0x52:
                //illegal
                break;
            case 0x53:
                //COMB, 2
                regB = helperCom(regB);
                readByte(regPC);
                break;
            case 0x54:
                //LSRB, 2
                regB = helperLsr(regB);
                readByte(regPC);
                break;
            case 0x55:
                //illegal
                break;
            case 0x56:
                //RORB, 2
                regB = helperRor(regB);
                readByte(regPC);
                break;
            case 0x57:
                //ASRB, 2
                regB = helperAsr(regB);
                readByte(regPC);
                break;
            case 0x58:
                //ASLB, 2
                regB = helperAsl(regB);
                readByte(regPC);
                break;
            case 0x59:
                //ROLB, 2
                regB = helperRol(regB);
                readByte(regPC);
                break;
            case 0x5A:
                //DECB, 2
                regB = helperDec(regB);
                readByte(regPC);
                break;
            case 0x5B:
                //illegal
                break;
            case 0x5C:
                //INCB, 2
                regB = helperInc(regB);
                readByte(regPC);
                break;
            case 0x5D:
                //TSTB, 2
                helperTst(regB);
                readByte(regPC);
                break;
            case 0x5E:
                //illegal
                break;
            case 0x5F:
                //CLRB, 2
                regB = helperClr();
                readByte(regPC);
                break;
            case 0x60:
                //NEG indexed, 6+
                ea = indexedEa();
                value = helperNeg(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x61:
                //illegal
                break;
            case 0x62:
                //illegal
                break;
            case 0x63:
                //COM indexed, 6+
                ea = indexedEa();
                value = helperCom(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x64:
                //LSR indexed, 6+
                ea = indexedEa();
                value = helperLsr(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x65:
                //illegal
                break;
            case 0x66:
                //ROR indexed, 6+
                ea = indexedEa();
                value = helperRor(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x67:
                //ASR indexed, 6+
                ea = indexedEa();
                value = helperAsr(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x68:
                //ASL indexed, 6+
                ea = indexedEa();
                value = helperAsl(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x69:
                //ROL indexed, 6+
                ea = indexedEa();
                value = helperRol(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x6A:
                //DEC indexed, 6+
                ea = indexedEa();
                value = helperDec(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x6B:
                //illegal
                break;
            case 0x6C:
                //INC indexed, 6+
                ea = indexedEa();
                value = helperInc(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x6D:
                //TST indexed, 6+
                ea = indexedEa();
                helperTst(readByte(ea));
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x6E:
                //JMP indexed, 3+
                regPC = indexedEa();
                break;
            case 0x6F:
                //CLR indexed, 6+
                ea = indexedEa();
                readByte(ea);
                value = helperClr();
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x70:
                //NEG extended, 7
                ea = extendedEA();
                value = helperNeg(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x71:
                //illegal
                break;
            case 0x72:
                //illegal
                break;
            case 0x73:
                //COM extended, 7
                ea = extendedEA();
                value = helperCom(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x74:
                //LSR extended, 7
                ea = extendedEA();
                value = helperLsr(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x75:
                //illegal
                break;
            case 0x76:
                //ROR extended, 7
                ea = extendedEA();
                value = helperRor(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x77:
                //ASR extended, 7
                ea = extendedEA();
                value = helperAsr(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x78:
                //ASL extended, 7
                ea = extendedEA();
                value = helperAsl(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x79:
                //ROL extended, 7
                ea = extendedEA();
                value = helperRol(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x7A:
                //DEC extended, 7
                ea = extendedEA();
                value = helperDec(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x7B:
                //illegal
                break;
            case 0x7C:
                //INC extended, 7
                ea = extendedEA();
                value = helperInc(readByte(ea));
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x7D:
                //TST extended, 7
                ea = extendedEA();
                helperTst(readByte(ea));
                readByteAtFFFF();
                readByteAtFFFF();
                break;
            case 0x7E:
                //JMP extended, 4
                regPC = extendedEA();
                readByteAtFFFF();
                break;
            case 0x7F:
                //CLR extended, 7
                ea = extendedEA();
                readByte(ea);
                value = helperClr();
                readByteAtFFFF();
                writeByte(ea, value);
                break;
            case 0x80:
                //SUBA immediate, 2
                regA = helperSub(regA,getImmediate());
                break;
            case 0x81:
                //CMPA immediate, 2
                helperSub(regA, getImmediate());
                break;
            case 0x82:
                //SBC immediate, 2
                regA = helperSbc(regA, getImmediate());
                break;
            case 0x83:
                //SUBD immediate, 4
                setDReg(helperSub16(getDReg(), getImmediateWord()));
                break;
            case 0x84:
                //ANDA immediate, 2
                regA = helperAnd(regA, getImmediate());
                break;
            case 0x85:
                //BITA immediate, 2
                helperAnd(regA, getImmediate());
                break;
            case 0x86:
                //LDA immediate, 2
                regA = helperLd(getImmediate());
                break;
            case 0x87:
                //illegal
                break;
            case 0x88:
                //EORA immediate, 2
                regA = helperEor(regA, getImmediate());
                break;
            case 0x89:
                //ADCA immediate, 2
                regA = helperAdc(regA, getImmediate());
                break;
            case 0x8A:
                //ORA immediate, 2
                regA = helperOr(regA, getImmediate());
                break;
            case 0x8B:
                //ADDA immediate, 2
                regA = helperAdd(regA, getImmediate());
                break;
            case 0x8C:
                //CMPX immediate, 4
                helperSub16(regX, getImmediateWord());
                break;
            case 0x8D:
                //BSR relative, 7
                helperBranchSubroutine();
                break;
            case 0x8E:
                //LDX immediate, 3
                regX = helperLd16(getImmediateWord());
                break;
            case 0x8F:
                //illegal
                break;
            case 0x90:
                //SUBA direct, 4
                ea = getEaDirect();
                regA = helperSub(regA, readByte(ea));
                break;
            case 0x91:
                //CMPA direct, 4
                ea = getEaDirect();
                helperSub(regA, readByte(ea));
                break;
            case 0x92:
                //SBCA direct, 4
                ea = getEaDirect();
                regA = helperSub(regA, readByte(ea)+(getCCCarry()?1:0));
                break;
            case 0x93:
                //SUBD direct, 6
                ea = getEaDirect();
                setDReg(helperSub16(getDReg(), readWord(ea)));
                break;
            case 0x94:
                //ANDA direct, 4
                ea = getEaDirect();
                regA = helperAnd(regA, readByte(ea));
                break;
            case 0x95:
                //BITA direct, 4
                ea = getEaDirect();
                helperAnd(regA, readByte(ea));
                break;
            case 0x96:
                //LDA direct, 4
                ea = getEaDirect();
                regA = helperLd(readByte(ea));
                break;
            case 0x97:
                //STA direct, 4
                ea = getEaDirect();
                helperLd(regA);
                writeByte(ea, regA);
                break;
            case 0x98:
                //EORA direct, 4
                ea = getEaDirect();
                regA = helperEor(regA, readByte(ea));
                break;
            case 0x99:
                //ADCA direct, 4
                ea = getEaDirect();
                regA = helperAdc(regA, readByte(ea));
                break;
            case 0x9A:
                //ORA direct, 4
                ea = getEaDirect();
                regA = helperOr(regA, readByte(ea));
                break;
            case 0x9B:
                //ADDA direct, 4
                ea = getEaDirect();
                regA = helperAdd(regA, readByte(ea));
                break;
            case 0x9C:
                //CMPX direct, 6
                ea = getEaDirect();
                helperSub16(regX, readWord(ea));
                break;
            case 0x9D:
                //JSR direct, 7
                ea = getEaDirect();
                readByte(ea);
                readByteAtFFFF();
                pushWord(regPC);
                regPC = ea;
                break;
            case 0x9E:
                //LDX direct, 5
                ea = getEaDirect();
                regX = helperLd16(readWord(ea));
                break;
            case 0x9F:
                //STX direct, 5
                ea = getEaDirect();
                helperLd16(regX);
                writeWord(ea, regX);
                break;
            case 0xA0:
                //SUBA indexed, 4+
                ea = indexedEa();
                regA = helperSub(regA, readByte(ea));
                break;
            case 0xA1:
                //CMPA indexed, 4+
                ea = indexedEa();
                helperSub(regA, readByte(ea));
                break;
            case 0xA2:
                //SBCA indexed, 4+
                ea = indexedEa();
                regA = helperSbc(regA, readByte(ea));
                break;
            case 0xA3:
                //SUBD indexed, 6+
                ea = indexedEa();
                setDReg(helperSub16(getDReg(), readWord(ea)));
                break;
            case 0xA4:
                //ANDA indexed, 4+
                ea = indexedEa();
                regA = helperAnd(regA, readByte(ea));
                break;
            case 0xA5:
                //BITA indexed, 4+
                ea = indexedEa();
                helperAnd(regA, readByte(ea));
                break;
            case 0xA6:
                //LDA indexed, 4+
                ea = indexedEa();
                regA = helperLd(readByte(ea));
                break;
            case 0xA7:
                //STA indexed, 4+
                ea = indexedEa();
                writeByte(ea, helperLd(regA));
                break;
            case 0xA8:
                //EORA indexed, 4+
                ea = indexedEa();
                regA = helperEor(regA, readByte(ea));
                break;
            case 0xA9:
                //ADCA indexed, 4+
                ea = indexedEa();
                regA = helperAdc(regA, readByte(ea));
                break;
            case 0xAA:
                //ORA indexed, 4+
                ea = indexedEa();
                regA = helperOr(regA, readByte(ea));
                break;
            case 0xAB:
                //ADDA indexed, 4+
                ea = indexedEa();
                regA = helperAdd(regA, readByte(ea));
                break;
            case 0xAC:
                //CMPX indexed, 6+
                ea = indexedEa();
                helperSub16(regX, readWord(ea));
                break;
            case 0xAD:
                //JSR indexed, 7+
                ea = indexedEa();
                readByte(ea);
                readByteAtFFFF();
                pushWord(regPC);
                regPC = ea;
                break;
            case 0xAE:
                //LDX indexed, 5+
                ea = indexedEa();
                regX = helperLd16(ea);
                break;
            case 0xAF:
                //STX indexed, 5+
                ea = indexedEa();
                helperLd16(regX);
                writeWord(ea, regX);
                break;
            case 0xB0:
                //SUBA extended, 5
                ea = extendedEA();
                regA = helperSub(regA, readByte(ea));
                break;
            case 0xB1:
                //CMPA extended, 5
                ea = extendedEA();
                helperSub(regA, readByte(ea));
                break;
            case 0xB2:
                //SBCA extended, 5
                ea = extendedEA();
                regA = helperSbc(regA, readByte(ea));
                break;
            case 0xB3:
                //SUBD extended, 7
                ea = extendedEA();
                setDReg(helperSub16(getDReg(), readWord(ea)));
                break;
            case 0xB4:
                //ANDA extended, 5
                ea = extendedEA();
                regA = helperAnd(regA, readByte(ea));
                break;
            case 0xB5:
                //BITA extended, 5
                ea = extendedEA();
                helperAnd(regA, readByte(ea));
                break;
            case 0xB6:
                //LDA extended, 5
                ea = extendedEA();
                regA = helperLd(readByte(ea));
                break;
            case 0xB7:
                //STA extended, 5
                ea = extendedEA();
                writeByte(ea, helperLd(regA));
                break;
            case 0xB8:
                //EORA extended, 5
                ea = extendedEA();
                regA = helperEor(regA, readByte(ea));
                break;
            case 0xB9:
                //ADCA extended, 5
                ea = extendedEA();
                regA = helperAdc(regA, readByte(ea));
                break;
            case 0xBA:
                //ORA extended, 5
                ea = extendedEA();
                regA = helperOr(regA, readByte(ea));
                break;
            case 0xBB:
                //ADDA extended, 5
                ea = extendedEA();
                regA = helperAdd(regA, readByte(ea));
                break;
            case 0xBC:
                //CMPX extended, 7
                ea = extendedEA();
                helperSub16(regX, readWord(ea));
                break;
            case 0xBD:
                //JSR extended, 8
                ea = extendedEA();
                readByte(ea);
                readByteAtFFFF();
                pushWord(regPC);
                regPC = ea;
                break;
            case 0xBE:
                //LDX extended, 6
                ea = extendedEA();
                regX = helperLd16(ea);
                break;
            case 0xBF:
                //STX extended, 6
                ea = extendedEA();
                helperLd16(regX);
                writeWord(ea, regX);
                break;
            case 0xC0:
                //SUBB immed, 2
                regB = helperSub(regB,getImmediate());
                break;
            case 0xC1:
                //CMPB immed, 2
                helperSub(regB, getImmediate());
                break;
            case 0xC2:
                //SBCB immed, 2
                regB = helperSbc(regB,getImmediate());
                break;
            case 0xC3:
                //ADDD immed, 4
                setDReg(helperAdd16(getDReg(),getImmediateWord()));
                break;
            case 0xC4:
                //ANDB immed, 2
                regB = helperAnd(regB,getImmediate());
                break;
            case 0xC5:
                //BITB immed, 2
                helperAnd(regB,getImmediate());
                break;
            case 0xC6:
                //LDB immed, 2
                regB = helperLd(getImmediate());
                break;
            case 0xC7:
                //illegal
                break;
            case 0xC8:
                //EORB immed, 2
                regB = helperEor(regB, getImmediate());
                break;
            case 0xC9:
                //ADCB immed, 2
                regB = helperAdc(regB, getImmediate());
                break;
            case 0xCA:
                //ORB immed, 2
                regB = helperOr(regB, getImmediate());
                break;
            case 0xCB:
                //ADDB immed, 2
                regB = helperAdd(regB, getImmediate());
                break;
            case 0xCC:
                //LDD immed, 3
                setDReg(helperLd16(getImmediateWord()));
                break;
            case 0xCD:
                //illegal
                break;
            case 0xCE:
                //LDU immed, 3
                regU = helperLd16(getImmediateWord());
                break;
            case 0xCF:
                //illegal
                break;
            case 0xD0:
                //SUBB direct, 4
                ea = getEaDirect();
                regB = helperSub(regB, readByte(ea));
                break;
            case 0xD1:
                //CMPB direct, 4
                ea = getEaDirect();
                helperSub(regB, readByte(ea));
                break;
            case 0xD2:
                //SBCB direct, 4
                ea = getEaDirect();
                regB = helperSbc(regB, readByte(ea));
                break;
            case 0xD3:
                //ADDD direct, 6
                ea = getEaDirect();
                setDReg(helperAdd16(getDReg(),readWord(ea)));
                break;
            case 0xD4:
                //ANDB direct, 4
                ea = getEaDirect();
                regB = helperAnd(regB, readByte(ea));
                break;
            case 0xD5:
                //BITB direct, 4
                ea = getEaDirect();
                helperAnd(regB, readByte(ea));
                break;
            case 0xD6:
                //LDB direct, 4
                ea = getEaDirect();
                regB = helperLd(readByte(ea));
                break;
            case 0xD7:
                //STB direct, 4
                ea = getEaDirect();
                helperLd(regB);
                writeByte(ea, regB);
                break;
            case 0xD8:
                //EORB direct, 4
                ea = getEaDirect();
                regB = helperEor(regB, readByte(ea));
                break;
            case 0xD9:
                //ADCB direct, 4
                ea = getEaDirect();
                regB = helperAdc(regB, readByte(ea));
                break;
            case 0xDA:
                //ORB direct, 4
                ea = getEaDirect();
                regB = helperOr(regB, readByte(ea));
                break;
            case 0xDB:
                //ADDB direct, 4
                ea = getEaDirect();
                regB = helperAdd(regB, readByte(ea));
                break;
            case 0xDC:
                //LDD direct, 5
                ea = getEaDirect();
                setDReg(helperLd16(readWord(ea)));
                break;
            case 0xDD:
                //STD direct, 5
                ea = getEaDirect();
                helperLd16(getDReg());
                writeWord(ea, getDReg());
                break;
            case 0xDE:
                //LDU direct, 5
                ea = getEaDirect();
                regU = helperLd16(readWord(ea));
                break;
            case 0xDF:
                //STU direct, 5
                ea = getEaDirect();
                helperLd16(regU);
                writeWord(ea, regU);
                break;
            case 0xE0:
                //SUBB indexed, 4+
                ea = indexedEa();
                regB = helperSub(regB, readByte(ea));
                break;
            case 0xE1:
                //CMPB indexed, 4+
                ea = indexedEa();
                helperSub(regB, readByte(ea));
                break;
            case 0xE2:
                //SBCB indexed, 4+
                ea = indexedEa();
                regB = helperSbc(regB, readByte(ea));
                break;
            case 0xE3:
                //ADDD indexed, 6+
                ea = indexedEa();
                setDReg(helperAdd16(getDReg(),readWord(ea)));
                break;
            case 0xE4:
                //ANDB indexed, 4+
                ea = indexedEa();
                regB = helperAnd(regB, readByte(ea));
                break;
            case 0xE5:
                //BITB indexed, 4+
                ea = indexedEa();
                helperAnd(regB, readByte(ea));
                break;
            case 0xE6:
                //LDB indexed, 4+
                ea = indexedEa();
                regB = helperLd(readByte(ea));
                break;
            case 0xE7:
                //STB indexed, 4+
                ea = indexedEa();
                helperLd(regB);
                writeByte(ea, regB);
                break;
            case 0xE8:
                //EORB indexed, 4+
                ea = indexedEa();
                regB = helperEor(regB, readByte(ea));
                break;
            case 0xE9:
                //ADCB indexed, 4+
                ea = indexedEa();
                regB = helperAdc(regB, readByte(ea));
                break;
            case 0xEA:
                //ORB indexed, 4+
                ea = indexedEa();
                regB = helperOr(regB, readByte(ea));
                break;
            case 0xEB:
                //ADDB indexed, 4+
                ea = indexedEa();
                regB = helperAdd(regB, readByte(ea));
                break;
            case 0xEC:
                //LDD indexed, 5+
                ea = indexedEa();
                setDReg(helperLd16(readWord(ea)));
                break;
            case 0xED:
                //STD indexed, 5+
                ea = indexedEa();
                helperLd16(getDReg());
                writeWord(ea, getDReg());
                break;
            case 0xEE:
                //LDU indexed, 5+
                ea = indexedEa();
                regU = helperLd16(readWord(ea));
                break;
            case 0xEF:
                //STU indexed, 5+
                ea = indexedEa();
                helperLd16(regU);
                writeWord(ea, regU);
                break;
            case 0xF0:
                //SUBB extended, 5
                ea = extendedEA();
                regB = helperSub(regB, readByte(ea));
                break;
            case 0xF1:
                //CMPB extended, 5
                ea = extendedEA();
                helperSub(regB, readByte(ea));
                break;
            case 0xF2:
                //SBCB extended, 5
                ea = extendedEA();
                regB = helperSbc(regB, readByte(ea));
                break;
            case 0xF3:
                //ADDD extended, 7
                ea = extendedEA();
                setDReg(helperAdd16(getDReg(),readWord(ea)));
                break;
            case 0xF4:
                //ANDB extended, 5
                ea = extendedEA();
                regB = helperAnd(regB, readByte(ea));
                break;
            case 0xF5:
                //BITB extended, 5
                ea = extendedEA();
                helperAnd(regB, readByte(ea));
                break;
            case 0xF6:
                //LDB extended, 5
                ea = extendedEA();
                regB = helperLd(readByte(ea));
                break;
            case 0xF7:
                //STB extended, 5
                ea = extendedEA();
                helperLd(regB);
                writeByte(ea, regB);
                break;
            case 0xF8:
                //EORB extended, 5
                ea = extendedEA();
                regB = helperEor(regB, readByte(ea));
                break;
            case 0xF9:
                //ADCB extended, 5
                ea = extendedEA();
                regB = helperAdc(regB, readByte(ea));
                break;
            case 0xFA:
                //ORB extended, 5
                ea = extendedEA();
                regB = helperOr(regB, readByte(ea));
                break;
            case 0xFB:
                //ADDB extended, 5
                ea = extendedEA();
                regB = helperAdd(regB, readByte(ea));
                break;
            case 0xFC:
                //LDD extended, 6
                ea = extendedEA();
                setDReg(helperLd16(readWord(ea)));
                break;
            case 0xFD:
                //STD extended, 6
                ea = extendedEA();
                helperLd16(getDReg());
                writeWord(ea, getDReg());
                break;
            case 0xFE:
                //LDU extended, 6
                ea = extendedEA();
                regU = helperLd16(readWord(ea));
                break;
            case 0xFF:
                //STU extended, 6
                ea = extendedEA();
                helperLd16(getDReg());
                writeWord(ea, regU);
                break;
            default:

        }


    }

    private void page0x10() {
        int opcode = readByte(regPC);
        switch (opcode&0xff) {
            case 0x21:
                //LBRN 5
                helperLongBranch(false);
                break;
            case 0x22:
                //LBHI 5/6
                helperLongBranch(!(getCCCarry()||getCCZero()));
                break;
            case 0x23:
                //LBLS 5/6
                helperLongBranch(getCCCarry()||getCCZero());
                break;
            case 0x24:
                //LBCC 5/6
                helperLongBranch(!getCCCarry());
                break;
            case 0x25:
                //LBCS 5/6
                helperLongBranch(getCCCarry());
                break;
            case 0x26:
                //LBNE 5/6
                helperLongBranch(!getCCZero());
                break;
            case 0x27:
                //LBEQ 5/6
                helperLongBranch(getCCZero());
                break;
            case 0x28:
                //LBVC 5/6
                helperLongBranch(!getCCOverflow());
                break;
            case 0x29:
                //LBVS 5/6
                helperLongBranch(getCCOverflow());
                break;
            case 0x2A:
                //LBPL 5/6
                helperLongBranch(!getCCNegative());
                break;
            case 0x2B:
                //LBMI 5/6
                helperLongBranch(getCCNegative());
                break;
            case 0x2C:
                //LBGE 5/6
                helperLongBranch(getCCNegative()==getCCOverflow());
                break;
            case 0x2D:
                //LBLT 5/6
                helperLongBranch(getCCNegative()!=getCCOverflow());
                break;
            case 0x2E:
                //LBGT 5/6
                helperLongBranch(!(getCCZero()&&(getCCNegative()!=getCCOverflow())));
                break;
            case 0x2F:
                //LBLE 5/6
                helperLongBranch(getCCZero()||(getCCNegative()!=getCCOverflow()));
                break;
            case 0x3F:
                //SWI2 20
                break;
            case 0x83:
                //CMPD immediate, 5
                break;
            case 0x8C:
                //CMPY immediate, 5
                break;
            case 0x8E:
                //LDY  immediate, 4
                break;
            case 0x93:
                //CMPD direct, 7
                break;
            case 0x9C:
                //CMPY direct, 7
                break;
            case 0x9E:
                //LDY direct, 6
                break;
            case 0x9F:
                //STY direct, 6
                break;
            case 0xA3:
                //CMPD indexed, 7+
                break;
            case 0xAC:
                //CMPY indexed, 7+
                break;
            case 0xAE:
                //LDY indexed, 6+
                break;
            case 0xAF:
                //STY indexed, 6+
                break;
            case 0xB3:
                //CMPD extended, 8
                break;
            case 0xBC:
                //CMPY extended, 8
                break;
            case 0xBE:
                //LDY extended, 7
                break;
            case 0xBF:
                //STY extended, 7
                break;
            case 0xCE:
                //LDS immediate, 4
                break;
            case 0xDE:
                //LDS direct, 6
                break;
            case 0xDF:
                //STS direct, 6
                break;
            case 0xEE:
                //LDS indexed, 6+
                break;
            case 0xEF:
                //STS indexed, 6+
                break;
            case 0xFE:
                //LDS extended, 7
                break;
            case 0xFF:
                //STS extended, 7
                break;
            default:
                //TODO illegal opcode
        }

    }

    private void page0x11() {
        int opcode = readByte(regPC);
        switch (opcode&0xff) {
            case 0x3F:
                //SWI3 20
                break;
            case 0x83:
                //CMPU immediate, 5
                break;
            case 0x8C:
                //CMPS immediate, 5
                break;
            case 0x93:
                //CMPU direct, 7
                break;
            case 0x9C:
                //CMPS direct, 7
                break;
            case 0xA3:
                //CMPU indexed, 7+
                break;
            case 0xAC:
                //CMPS indexed, 7+
                break;
            case 0xB3:
                //CMPU extended, 8
                break;
            case 0xBC:
                //CMPS extended, 8
                break;
            default:
                //TODO illegal
        }

    }

    private void incPC() {
        regPC = (regPC+1)&0xffff;
    }

    private int getEaDirect() {
        int lowEA = getImmediate();
        readByteAtFFFF();
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

    private void helperBranchSubroutine() {
        int ofsLo = getImmediate();
        int ofsHi = ((ofsLo&0x80)==0x80)?0xff:0x00;
        int ea = (regPC+((ofsHi<<8)+ofsLo))&0xffff;
        readByteAtFFFF();
        readByte(ea);
        readByteAtFFFF();
        pushWord(regPC);
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

    private int extendedEA() {
        int ea = getImmediateWord();
        readByteAtFFFF();
        return ea;
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

    private void helperPull(boolean uStack) {
        int postByte = getImmediate();
        int sp = uStack?regU:regS;
        int lo,hi;
        readByteAtFFFF();
        readByteAtFFFF();
        if ((postByte&0x01)==0x01) {
            regCC = readByte(sp);
            sp = incWord(sp);
        }
        if ((postByte&0x02)==0x02) {
            regA = readByte(sp);
            sp = incWord(sp);
        }
        if ((postByte&0x04)==0x04) {
            regB = readByte(sp);
            sp = incWord(sp);
        }
        if ((postByte&0x08)==0x08) {
            regDP = readByte(sp);
            sp = incWord(sp);
        }
        if ((postByte&0x10)==0x10) {
            hi = readByte(sp);
            sp = incWord(sp);
            lo = readByte(sp);
            sp = incWord(sp);
            regX = (hi<<8)+lo;
        }
        if ((postByte&0x20)==0x20) {
            hi = readByte(sp);
            sp = incWord(sp);
            lo = readByte(sp);
            sp = incWord(sp);
            regY = (hi<<8)+lo;
        }
        if ((postByte&0x40)==0x40) {
            hi = readByte(sp);
            sp = incWord(sp);
            lo = readByte(sp);
            sp = incWord(sp);
            if (uStack) {
                regU = (hi<<8)+lo;
            } else {
                regS = (hi<<8)+lo;
            }
        }
        if ((postByte&0x80)==0x80) {
            hi = readByte(sp);
            sp = incWord(sp);
            lo = readByte(sp);
            sp = incWord(sp);
            regPC = (hi<<8)+lo;
        }
        if (uStack) {
            regU = sp;
        } else {
            regS = sp;
        }
    }

    private void helperReturn() {
        regPC = popWord();
        readByteAtFFFF();
    }

    private void helperRti() {
        readByte(regPC);
        regCC = popByte();
        if (getCCEntire()) {
            regA = popByte();
            regB = popByte();
            regDP = popByte();
            regX = popWord();
            regY = popWord();
            regU = popWord();
        }
        regPC = popWord();
        readByteAtFFFF();
    }

    private void helperCwai() {
        regCC = (regCC&getImmediate())|CC_ENTIRE;
        readByte(regPC);
        readByteAtFFFF();
        pushAll();
        do {
            readByteAtFFFF();
        } while (!(lineNMI||lineIRQ||lineFIRQ));
        if (lineNMI) {
            regPC = readWord(0xfffc);
        } else if (lineIRQ) {
            regPC = readWord(0xfff8);
        } else {
            regPC = readWord(0xfff6);
        }
        readByteAtFFFF();
    }

    private void helperMul() {
        int result = regA * regB;
        regA = (result & 0xff00) >> 8;
        regB = result & 0xff;
        setCCZero((result & 0xffff) == 0);
        setCCCarry((result & 0x80) == 0x80);
        readByte(regPC);
        for (int i = 1;i<=9;i++) {
            readByteAtFFFF();
        }
    }

    private void helperSwi() {
        readByte(regPC);
        readByteAtFFFF();
        pushAll();
        readByteAtFFFF();
        regPC = readWord(0xfffa);
        readByteAtFFFF();
    }

    private int helperSub(int minuend, int subtrahend) {
        int result = minuend - subtrahend;
        setCCNegative(result);
        setCCZero(result);
        setCCCarry((result&0x100)==0x100);
        setCCOverflow(((result ^ minuend ^ subtrahend ^ (result>>1)) & 0x80) == 0x80);
        return result&0xff;
    }

    private int helperSbc(int minuend, int subtrahend) {
        return helperSub(minuend, subtrahend + (getCCCarry()?1:0));
    }

    private int helperSub16(int minuend, int subtrahend) {
        int result = minuend - subtrahend;
        setCCNegative((result&0x8000)==0x8000);
        setCCZero((result&0xffff)==0x0000);
        setCCCarry((result&0x10000)==0x10000);
        setCCOverflow(((result ^ minuend ^ subtrahend ^ (result>>1)) & 0x8000) == 0x8000);
        readByteAtFFFF();
        return result & 0xffff;
    }

    private int helperAnd(int destination, int source) {
        int result = (destination & source)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCOverflow(false);
        return result;
    }

    private int helperEor(int destination, int source) {
        int result = (destination ^ source)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCOverflow(false);
        return result;
    }

    private int helperOr(int destination, int source) {
        int result = (destination | source)&0xff;
        setCCZero(result);
        setCCNegative(result);
        setCCOverflow(false);
        return result;
    }

    private int helperAdd(int destination, int source) {
        int result = destination + source;
        setCCZero(result);
        setCCNegative(result);
        setCCCarry((result&0x100)==0x100);
        setCCOverflow(((result ^ destination ^ source ^ (result>>1)) & 0x80) == 0x80);
        setCCHalf(((result ^ destination ^ source)&0x10)==0x10);
        return result&0xff;
    }

    private int helperAdc(int destination, int source) {
        return helperAdd(destination, source + (getCCCarry()?1:0));
    }

    private int helperAdd16(int destination, int source) {
        int result = destination + source;
        setCCNegative((result&0x8000)==0x8000);
        setCCZero((result&0xffff)==0x0000);
        setCCCarry((result&0x10000)==0x10000);
        setCCOverflow(((result ^ destination ^ source ^ (result>>1)) & 0x8000) == 0x8000);
        readByteAtFFFF();
        return result & 0xffff;
    }

    private int helperLd(int value) {
        setCCZero(value);
        setCCNegative(value);
        setCCOverflow(false);
        return value&0xff;
    }

    private int helperLd16(int value) {
        setCCZero((value&0xffff)==0x0000);
        setCCNegative((value&0x8000)==0x8000);
        setCCOverflow(false);
        return value&0xffff;
    }

    private void pushAll() {
        pushWord(regPC);
        pushWord(regU);
        pushWord(regY);
        pushWord(regX);
        pushByte(regDP);
        pushByte(regB);
        pushByte(regA);
        pushByte(regCC);
    }

    private void pushPCAndCC() {
        pushWord(regPC);
        pushByte(regCC);
    }

    private void pushByte(int aByte) {
        regS = decWord(regS);
        writeByte(regS, aByte);
    }

    private void pushWord(int aWord) {
        regS = decWord(regS);
        writeByte(regS, aWord&0xff);
        regS = decWord(regS);
        writeByte(regS, (aWord&0xff00)>>8);
    }

    private int popByte() {
        int result = readByte(regS);
        regS = incWord(regS);
        return result;
    }

    private int popWord() {
        int hi = readByte(regS);
        regS = incWord(regS);
        int lo = readByte(regS);
        regS = incWord(regS);
        return (hi<<8)+lo;
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

    private static int incWord(int word) {
        return (word+1)&0xffff;
    }

    private static int getLowByte(int word) {
        return word&0xff;
    }

    private static int getHighByte(int word) {
        return (word&0xff00)>>8;
    }

}
