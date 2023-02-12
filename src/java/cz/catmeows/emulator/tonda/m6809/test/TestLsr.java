package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestLsr extends BaseTest {

    public TestResult testLsrDirect0x00(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("LSR direct 0x00");
        memory.write(0x1000, 0x04);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testLsrDirect0x01(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("LSR direct 0x01");
        memory.write(0x1000, 0x04);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x01);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testLsrDirect0x81(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("LSR direct 0x81");
        memory.write(0x1000, 0x04);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x81);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x40, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testLsrDirect0x40(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("LSR direct 0x40");
        memory.write(0x1000, 0x04);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x40);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x20, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }
}
