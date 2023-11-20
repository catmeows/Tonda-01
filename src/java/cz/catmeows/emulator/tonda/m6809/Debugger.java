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
        debuggerDialog.dispose();
        debuggerDialog = null;
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
                            opcodeDisplay.append(directModeBuilder("NEG", mem, ptr));
                            delta = 2;
            }
            case 0x03 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("COM", mem, ptr));
                            delta = 2;
            }
            case 0x04 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("LSR", mem, ptr));
                            delta = 2;
            }
            case 0x06 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ROR", mem, ptr));
                            delta = 2;
            }
            case 0x07 -> {  byteDisplay.append(getXBytes(mem, ptr, 2));
                            opcodeDisplay.append(directModeBuilder("ASR", mem, ptr));
                            delta = 2;  }


            default -> {    byteDisplay.append(getXBytes(mem, ptr, 1));
                            opcodeDisplay.append("???");
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
    public void actionPerformed(ActionEvent e) {

        debuggerDialog = new DebuggerWindow(parentFrame, this);
        setEnabled(true);
    }

    private class LineDeltaPair {
        int delta;
        StringBuilder line;
    }
}
