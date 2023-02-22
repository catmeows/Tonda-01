package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestRor extends BaseTest {

    public TestResult testRorDirect0x00CarrySet(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROR direct 0x00, Carry=true");
        memory.write(0x1000, 0x06);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        cpu.setCCCarry(true);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testRorDirect0x01CarryReset(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROR direct 0x01, Carry=false");
        memory.write(0x1000, 0x06);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x01);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        cpu.setCCCarry(false);
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
}
