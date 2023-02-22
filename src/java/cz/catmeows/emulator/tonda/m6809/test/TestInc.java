package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.AddressSpace;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

public class TestInc extends BaseTest {

    public TestResult testIncDirect0xff(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("INC direct 0xFF");
        memory.write(0x1000, 0x0c);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0xff);
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

    public TestResult testIncDirect0x80(Cpu6809 cpu, AddressSpace memory, TickCounter tickCounter) {
        init("INC direct 0x7f");
        memory.write(0x1000, 0x0c);
        memory.write(0x1001, 0x00);
        memory.write(0x0100, 0x7f);
        cpu.setPCReg(0x1000);
        cpu.setDPReg(0x01);;
        tickCounter.reset();
        cpu.nextInstruction();
        expect(tickCounter.getTicks()==6, "ticks");
        expect(cpu.getPCReg()==0x1002, "PC");
        expect(memory.read(0x0100)==0x80, "result");
        expect(cpu.getCCNegative()==true, "CC Negative");
        expect(cpu.getCCZero()==false, "CC Zero");
        expect(cpu.getCCOverflow()==true, "CC Overflow");
        return finish();
    }
}
