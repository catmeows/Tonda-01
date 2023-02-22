package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestRol extends BaseTest {

    public TestResult testRolDirect0x00CarrySet(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROL direct 0x00, Carry=true");
        memory.write(0x1000, 0x09);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        cpu.setCCCarry(true);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x01, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testRolDirect0x00CarryReset(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROL direct 0x00, Carry=false");
        memory.write(0x1000, 0x09);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
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
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testRolDirect0x81CarrySet(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROL direct 0x81, Carry=true");
        memory.write(0x1000, 0x09);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x81);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        cpu.setCCCarry(true);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x03, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testRolDirect0x40CarryReset(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ROL direct 0x40, Carry=true");
        memory.write(0x1000, 0x09);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x40);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        cpu.setCCCarry(true);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x81, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }


}
