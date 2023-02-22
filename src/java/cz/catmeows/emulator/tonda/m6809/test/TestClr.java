package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestClr extends BaseTest {

    public TestResult testClrDirect0x01(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("CLR direct 0x01");
        memory.write(0x1000, 0x0f);
        memory.write(0x1001, 0x01);
        memory.write(0x0101, 0xff);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0101)==0x00, "result");
        expect(cpu.getCCNegative()==false, "CC Negative");
        expect(cpu.getCCZero()==true, "CC Zero");
        expect(cpu.getCCOverflow()==false, "CC Overflow");
        expect(cpu.getCCCarry()==false, "CC Carry");
        return finish();
    }
}
