package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestDec extends BaseTest {

    public TestResult testDecDirect0x00(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("DEC direct 0x00");
        memory.write(0x1000, 0x0a);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0xff, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        return finish();
    }

    public TestResult testDecDirect0x80(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("DEC direct 0x80");
        memory.write(0x1000, 0x0a);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x80);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);;
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x7f, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==true, "CC Overflow");
        return finish();
    }

}
