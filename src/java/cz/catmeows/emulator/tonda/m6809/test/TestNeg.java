package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.TickListener;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestNeg extends BaseTest {

    public TestResult testNegDirectPositive(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("NEG direct positive number");
        memory.write(0x1000, 0x00);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0x01);
        cpu.setPCReg(0x1000);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0xFF, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testNegDirectNegative(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("NEG direct negative number");
        memory.write(0x1000, 0x00);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0xF0);
        cpu.setPCReg(0x1000);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0x10, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testNegDirectZero(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("NEG direct number zero");
        memory.write(0x1000, 0x00);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0x00);
        cpu.setPCReg(0x1000);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6,"ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testNegDirect0x80(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("NEG direct number 0x80");
        memory.write(0x1000, 0x00);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0x80);
        cpu.setPCReg(0x1000);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==true, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }
}
