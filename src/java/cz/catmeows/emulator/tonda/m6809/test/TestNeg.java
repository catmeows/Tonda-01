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
        expect(tickCounter.getTicks()==6);
        expect(cpu.getPCReg()==0x1002);
        expect(memory.read(0x0000)==0xFE);
        expect(cpu.getCCNegative()==true);
        expect(cpu.getCCZero()==false);
        expect(cpu.getCCOverflow()==false);
        expect(cpu.getCCCarry()==true);
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
        expect(tickCounter.getTicks()==6);
        expect(cpu.getPCReg()==0x1002);
        expect(memory.read(0x0000)==0x10);
        expect(cpu.getCCNegative()==false);
        expect(cpu.getCCZero()==false);
        expect(cpu.getCCOverflow()==false);
        expect(cpu.getCCCarry()==true);
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
        expect(tickCounter.getTicks()==6);
        expect(cpu.getPCReg()==0x1002);
        expect(memory.read(0x0000)==0x00);
        expect(cpu.getCCNegative()==false);
        expect(cpu.getCCZero()==true);
        expect(cpu.getCCOverflow()==false);
        expect(cpu.getCCCarry()==false);
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
        expect(tickCounter.getTicks()==6);
        expect(cpu.getPCReg()==0x1002);
        expect(memory.read(0x0000)==0x80);
        expect(cpu.getCCNegative()==true);
        expect(cpu.getCCZero()==false);
        expect(cpu.getCCOverflow()==true);
        expect(cpu.getCCCarry()==true);
        return finish();
    }
}
