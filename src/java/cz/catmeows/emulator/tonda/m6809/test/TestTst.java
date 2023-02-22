package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestTst extends BaseTest {

    public TestResult testTstDirect0x00(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("TST direct 0x00");
        memory.write(0x1000, 0x0d);
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
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        return finish();
    }

    public TestResult testTstDirect0x80(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("TST direct 0x80");
        memory.write(0x1000, 0x0d);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x80);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        return finish();
    }

    public TestResult testTstDirect0x10(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("TST direct 0x10");
        memory.write(0x1000, 0x0d);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x10);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x10, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        return finish();
    }




}
