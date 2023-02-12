package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestCom extends BaseTest {

    public TestResult testComPositive(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("COM direct positive number");
        memory.write(0x1000, 0x03);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0x01);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0xFE, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testComNegative(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("COM direct negative number");
        memory.write(0x1000, 0x03);
        memory.write(0x1001, 0xFE);
        memory.write(0x00FE, 0xF0);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x00FE)==0x0F, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testCom0xFF(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("COM direct 0xFF");
        memory.write(0x1000, 0x03);
        memory.write(0x1001, 0x00);
        memory.write(0x0000, 0xFF);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0000)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }


}
