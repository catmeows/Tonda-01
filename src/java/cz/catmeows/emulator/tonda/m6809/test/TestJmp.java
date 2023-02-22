package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestJmp extends BaseTest {

    public TestResult testJmpDirect0x00(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("JMP direct 0x00");
        memory.write(0x1000, 0x0e);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==3, "ticks");
        expect(cpu.getPCReg()==0x0100, "PC");
        return finish();
    }

    public TestResult testJmpDirect0xff(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("JMP direct 0xFF");
        memory.write(0x1000, 0x0e);
        memory.write(0x1001, 0xff);
        memory.write(0x01ff, 0x00);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x00);
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==3, "ticks");
        expect(cpu.getPCReg()==0x00ff, "PC");
        return finish();
    }
}
