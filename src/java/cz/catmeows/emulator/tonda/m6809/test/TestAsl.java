package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestAsl extends BaseTest {

    public TestResult testAslDirect0x80(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASL direct 0x80");
        memory.write(0x1000, 0x08);
        memory.write(0x1001, 0x02);
        memory.write(0x0110, 0x80);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0001)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        expect(cpu.getCCOverflow()==true, "CC Overflow");
        return finish();
    }

    public TestResult testAslDirect0x40(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASL direct 0x40");
        memory.write(0x1000, 0x08);
        memory.write(0x1001, 0x02);
        memory.write(0x0002, 0x40);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0002)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        expect(cpu.getCCOverflow()==true, "CC Overflow");
        return finish();
    }

    public TestResult testAslDirect0xC0(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASL direct 0xC0");
        memory.write(0x1000, 0x08);
        memory.write(0x1001, 0x02);
        memory.write(0x0002, 0xc0);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0002)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        return finish();
    }
}
