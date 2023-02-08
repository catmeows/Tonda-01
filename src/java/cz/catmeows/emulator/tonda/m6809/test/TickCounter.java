package cz.catmeows.emulator.tonda.m6809.test;

import cz.catmeows.emulator.tonda.TickListener;

public class TickCounter implements TickListener {

    int ticks = 0;
    public void reset() {
        ticks = 0;
    }

    @Override
    public void tick() {
        ticks++;
    }

    int getTicks() {
        return ticks;
    }
}
