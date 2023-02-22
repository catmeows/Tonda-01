package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.Ram;
import cz.catmeows.emulator.tonda.m6809.Cpu6809;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Ram ram = new Ram();
        TickCounter tickCounter = new TickCounter();
        Cpu6809 cpu = new Cpu6809(ram, tickCounter);

        long start = System.currentTimeMillis();
        long times = 0;
        TestNeg instance = new TestNeg();
        while ((System.currentTimeMillis()-start)<5000) {
            instance.testNegDirectZero(cpu, ram, tickCounter);
            times++;
        }
        System.out.println("CPU speed "+(6*times/5/1000000.0f));

        List<TestResult> results = new ArrayList<>();

        results.add(new TestNeg().testNegDirectNegative(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirectPositive(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirect0x80(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirectZero(cpu, ram, tickCounter));
        
        results.add(new TestCom().testComPositive(cpu, ram, tickCounter));
        results.add(new TestCom().testComNegative(cpu, ram, tickCounter));
        results.add(new TestCom().testCom0xFF(cpu, ram, tickCounter));

        results.add(new TestLsr().testLsrDirect0x01(cpu,ram, tickCounter));
        results.add(new TestLsr().testLsrDirect0x00(cpu,ram, tickCounter));
        results.add(new TestLsr().testLsrDirect0x81(cpu,ram, tickCounter));
        results.add(new TestLsr().testLsrDirect0x40(cpu,ram, tickCounter));

        results.add(new TestRor().testRorDirect0x00CarrySet(cpu,ram, tickCounter));
        results.add(new TestRor().testRorDirect0x01CarryReset(cpu, ram, tickCounter));

        results.add(new TestAsr().testAsrDirect0x81(cpu, ram, tickCounter));
        results.add(new TestAsr().testAsrDirect0x01(cpu, ram, tickCounter));
        results.add(new TestAsr().testAsrDirect0x02(cpu, ram, tickCounter));

        results.add(new TestAsl().testAslDirect0xC0(cpu, ram, tickCounter));
        results.add(new TestAsl().testAslDirect0x80(cpu, ram, tickCounter));
        results.add(new TestAsl().testAslDirect0x40(cpu, ram, tickCounter));

        results.add(new TestRol().testRolDirect0x00CarrySet(cpu, ram, tickCounter));
        results.add(new TestRol().testRolDirect0x00CarryReset(cpu, ram, tickCounter));
        results.add(new TestRol().testRolDirect0x40CarryReset(cpu, ram, tickCounter));
        results.add(new TestRol().testRolDirect0x81CarrySet(cpu, ram, tickCounter));

        int errors=0;
        for (TestResult tr: results
             ) {
            System.out.println((tr.getResult()?"ok  :":"ERR :")+tr.getDescription());
            if (!tr.getResult()) {
                errors++;
            }
        }
        System.out.println("ERRORS: "+errors);
    }
}
