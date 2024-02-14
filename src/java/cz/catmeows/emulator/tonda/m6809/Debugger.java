package cz.catmeows.emulator.tonda.m6809;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.ui.DebuggerWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Debugger implements ActionListener {

    boolean enabled = false;

    boolean canContinue = false;
    JFrame parentFrame;

    DebuggerWindow debuggerDialog;


    public Debugger(JFrame frame) {
        parentFrame = frame;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(boolean enable) {
        enabled = enable;
    }

    public synchronized void setCanContinue(boolean canContinue) {
        this.canContinue = canContinue;
    }

    public synchronized void closeWindow() {
        setEnabled(false);
        //debuggerDialog.dispose();
        debuggerDialog.setVisible(false);
    }

    public boolean displayState(int PCreg, int Dreg, int Xreg, int Yreg, int Sreg, int Ureg, int CCreg, int DPreg, AddressSpace mem ) {

        StringBuilder sb = new StringBuilder(" Registers:\n");
        sb.append(" PC:"+formatHex(PCreg,4)+' ');
        sb.append(" DP:"+formatHex(DPreg, 2)+'\n');
        sb.append(" S :"+formatHex(Sreg,4)+' ');
        sb.append(" U :"+formatHex(Ureg,4)+'\n');
        sb.append(" X :"+formatHex(Xreg,4)+' ');
        sb.append(" Y :"+formatHex(Yreg,4)+'\n');
        sb.append(" D :"+formatHex(Dreg,4)+'\n');
        sb.append(" CC:"+formatFlags(CCreg)+'\n');
        sb.append('\n');
        int ptr = PCreg;
        for (int i=0;i<8;i++) {
            LineDeltaPair pair = decodeAtPtr(ptr,mem);
            sb.append(pair.line);
            sb.append('\n');
            ptr = ptr+pair.delta;
        }

        debuggerDialog.setContent(sb.toString());
        return canContinue;

    }

    private LineDeltaPair decodeAtPtr(int ptr, AddressSpace mem) {
        LineDeltaPair result = new LineDeltaPair();
        StringBuilder byteDisplay = new StringBuilder();
        StringBuilder opcodeDisplay = new StringBuilder();
        int delta = 1;
        int opcode = mem.read(ptr);
        switch (opcode) {
            case 0x00 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("NEG  ", mem, ptr));
                            delta = 2;
            }
            case 0x03 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("COM  ", mem, ptr));
                            delta = 2;
            }
            case 0x04 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("LSR  ", mem, ptr));
                            delta = 2;
            }
            case 0x06 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ROR  ", mem, ptr));
                            delta = 2;
            }
            case 0x07 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ASR  ", mem, ptr));
                            delta = 2;
            }
            case 0x08 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ASL  ", mem, ptr));
                            delta = 2;
            }
            case 0x09 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ROL  ", mem, ptr));
                            delta = 2;
            }
            case 0x0A -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("DEC  ", mem, ptr));
                            delta = 2;
            }
            case 0x0C -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("INC  ", mem, ptr));
                            delta = 2;
            }
            case 0x0D -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("TST  ", mem, ptr));
                            delta = 2;
            }
            case 0x0E -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("JMP  ", mem, ptr));
                            delta = 2;
            }
            case 0x0F -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("CLR  ", mem, ptr));
                            delta = 2;
            }
            //case 0x10 -> {

            //}
            //case 0x11 -> {

            //}
            case 0x12 -> {  byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("NOP  ");
                            delta = 1;
            }
            case 0x13 -> {  byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("SYNC ");
                            delta = 1;
            }
            case 0x16 -> {  byteDisplay.append(getXBytes(mem, ptr, 3));
                            opcodeDisplay.append(longBranchBuilder("LBRA ", mem, ptr));
                            delta = 3;
            }
            case 0x17 -> {  byteDisplay.append(getXBytes(mem, ptr, 3));
                            opcodeDisplay.append(longBranchBuilder("LBSR ", mem, ptr));
                            delta = 3;
            }
            case 0x19 -> {  byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("DAA  ");
                            delta = 1;
            }
            case 0x1A -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(immediateByteBuilder("ORCC ", mem, ptr));
                            delta = 2;
            }
            case 0x1C -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(immediateByteBuilder("ANDCC", mem, ptr));
                            delta = 2;
            }
            case 0x1D -> {  byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("SEX  ");
                            delta = 1;
            }
            case 0x1E -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(transferExchangeBuilder("EXG  ", mem, ptr));
                            delta = 2;
            }
            case 0x1F -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(transferExchangeBuilder("TFR  ", mem, ptr));
                            delta = 2;
            }
            case 0x20 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(shortBranchBuilder("BRA  ", mem, ptr));
                            delta = 2;
            }
            case 0x21 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(shortBranchBuilder("BRN  ", mem, ptr));
                            delta = 2;
            }
            case 0x22 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(shortBranchBuilder("BHI  ", mem, ptr));
                            delta = 2;
            }
            case 0x23 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(shortBranchBuilder("BLS  ", mem, ptr));
                            delta = 2;
            }

            case 0x24 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BHS  ", mem, ptr));
                delta = 2;
            }
            case 0x25 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BLO  ", mem, ptr));
                delta = 2;
            }

            case 0x26 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BNE  ", mem, ptr));
                delta = 2;
            }
            case 0x27 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BEQ  ", mem, ptr));
                delta = 2;
            }

            case 0x28 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BVC  ", mem, ptr));
                delta = 2;
            }
            case 0x29 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BVS  ", mem, ptr));
                delta = 2;
            }
            case 0x2A -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BPL  ", mem, ptr));
                delta = 2;
            }
            case 0x2B -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BMI  ", mem, ptr));
                delta = 2;
            }
            case 0x2C -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BGE  ", mem, ptr));
                delta = 2;
            }
            case 0x2D -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BLT  ", mem, ptr));
                delta = 2;
            }
            case 0x2E -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BGT  ", mem, ptr));
                delta = 2;
            }
            case 0x2F -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                opcodeDisplay.append(shortBranchBuilder("BLE  ", mem, ptr));
                delta = 2;
            }




            default -> {    byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("???  ");
            }
        }
        byteDisplay.append("                   ".substring(0, 18-byteDisplay.length()));
        result.line = byteDisplay.append(opcodeDisplay);
        result.delta = delta;
        return result;
    }

    private StringBuilder getXBytes(AddressSpace mem, int ptr, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatHex(ptr,4)).append(": ");
        for (int i=0;i<count;i++) {
            sb.append(formatHex(mem.read(ptr+i),2));
            sb.append(' ');
        }
        return sb;
    }

    private StringBuilder directModeBuilder(String opcode, AddressSpace mem, int ptr) {
        return new StringBuilder(opcode).append(" <").append(formatHex(mem.read(ptr+1), 2));
    }

    private StringBuilder longBranchBuilder(String opcode, AddressSpace mem, int ptr) {
        int offset = mem.read(ptr + 1)*256 + mem.read(ptr + 2);
        int destination = 0xffff & (ptr + 3 + offset);
        return new StringBuilder(opcode).append(" ").append(formatHex(destination, 4));
    }

    private StringBuilder shortBranchBuilder(String opcode, AddressSpace mem, int ptr) {
        int offset = mem.read(ptr) + ((mem.read(ptr) & 0x80) == 0x80?0xff00:0x0000);
        int destination = 0xffff & (ptr + 2 + offset);
        return new StringBuilder(opcode).append(" ").append(formatHex(destination, 4));
    }

    private StringBuilder immediateByteBuilder(String opcode, AddressSpace mem, int ptr) {
        return new StringBuilder(opcode).append(" #").append(formatHex(mem.read(ptr+1), 2));
    }

    private StringBuilder transferExchangeBuilder(String opcode, AddressSpace mem, int ptr) {
        int postByte = mem.read(ptr + 1);
        return new StringBuilder(opcode).append(" ").append(transferExchangeRegs((postByte&0xF0)>>4))
                .append(",").append(transferExchangeRegs(postByte&0x0F));
    }

    private StringBuilder indexedBuilder(String opcode, AddressSpace mem, int ptr) {
        int postByte = mem.read(ptr+1);
        return new StringBuilder();
    }

    private String decodeRegisters(int postbyte) {
        int regs = postbyte & 0x60;
        if (regs == 0x00) {
            return "X";
        } else if (regs == 0x20) {
            return "Y";
        } else if (regs == 0x40) {
            return "U";
        } else {
            return "S";
        }
    }

    private String transferExchangeRegs(int register) {
        switch (register) {
            case 0x0 -> {
                return "D";
            }
            case 0x1 -> {
                return "X";
            }
            case 0x2 -> {
                return "Y";
            }
            case 0x3 -> {
                return "U";
            }
            case 0x4 -> {
                return "S";
            }
            case 0x5 -> {
                return "PC";
            }
            case 0x8 -> {
                return "A";
            }
            case 0x9 -> {
                return "B";
            }
            case 0xA -> {
                return "CC";
            }
            case 0xB -> {
                return "DP";
            }
            default -> {
                return "??";
            }
        }
    }

    private StringBuilder formatFlags(int CCreg) {
        StringBuilder sb = new StringBuilder();
        sb.append((CCreg & 0x80)==0x80?'E':'e');
        sb.append((CCreg & 0x40)==0x40?'F':'f');
        sb.append((CCreg & 0x20)==0x20?'H':'h');
        sb.append((CCreg & 0x10)==0x10?'I':'i');
        sb.append((CCreg & 0x08)==0x08?'N':'n');
        sb.append((CCreg & 0x04)==0x04?'Z':'z');
        sb.append((CCreg & 0x02)==0x02?'V':'v');
        sb.append((CCreg & 0x01)==0x01?'C':'c');
        return sb;
    }

    private StringBuilder formatHex(int number, int digits) {
       StringBuilder sb = new StringBuilder();
       for (int i=0; i<digits; i++) {
           int nibble=(number & 0xf);
           switch (nibble) {
               case 0 -> sb.insert(0,'0');
               case 1 -> sb.insert(0,'1');
               case 2 -> sb.insert(0,'2');
               case 3 -> sb.insert(0,'3');
               case 4 -> sb.insert(0,'4');
               case 5 -> sb.insert(0,'5');
               case 6 -> sb.insert(0,'6');
               case 7 -> sb.insert(0,'7');
               case 8 -> sb.insert(0,'8');
               case 9 -> sb.insert(0,'9');
               case 10 -> sb.insert(0,'A');
               case 11 -> sb.insert(0,'B');
               case 12 -> sb.insert(0,'C');
               case 13 -> sb.insert(0,'D');
               case 14 -> sb.insert(0,'E');
               case 15 -> sb.insert(0,'F');
           }
           number = number>>4;
       }
       return sb;
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        if (debuggerDialog==null) {
            System.out.println("Create debug dialog");
            debuggerDialog = new DebuggerWindow(parentFrame, this);
        } else {
            System.out.println("Request focus");
            debuggerDialog.setVisible(true);
            debuggerDialog.requestFocus();
        }
        setEnabled(true);
    }

    private class LineDeltaPair {
        int delta;
        StringBuilder line;
    }
}
