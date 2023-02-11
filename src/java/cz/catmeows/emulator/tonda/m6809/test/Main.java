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
        List<TestResult> results = new ArrayList<>();

        results.add(new TestNeg().testNegDirectNegative(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirectPositive(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirect0x80(cpu, ram, tickCounter));
        results.add(new TestNeg().testNegDirectZero(cpu, ram, tickCounter));


        for (TestResult tr: results
             ) {
            System.out.println((tr.getResult()?"ok  :":"ERR :")+tr.getDescription());
        }
    }
}
