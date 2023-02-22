package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestAsr extends BaseTest {

    public TestResult testAsrDirect0x81(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASR direct 0x81");
        memory.write(0x1000, 0x07);
        memory.write(0x1001, 0x10);
        memory.write(0x0110, 0x81);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0110)==0xc0, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }

    public TestResult testAsrDirect0x02(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASR direct 0x02");
        memory.write(0x2000, 0x07);
        memory.write(0x2001, 0x10);
        memory.write(0x0110, 0x02);
        cpu.setPCReg(0x2000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x2002, "PC");
        expect(memory.read(0x0110)==0x01, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }

    public TestResult testAsrDirect0x01(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("ASR direct 0x01");
        memory.write(0x2000, 0x07);
        memory.write(0x2001, 0x10);
        memory.write(0x0110, 0x01);
        cpu.setPCReg(0x2000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x2002, "PC");
        expect(memory.read(0x0110)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCCarry()==true, "CC Carry");
        return finish();
    }


}
