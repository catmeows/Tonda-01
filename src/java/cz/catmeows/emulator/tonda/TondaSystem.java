package cz.catmeows.emulator.tonda;

public class TondaSystem implements Runnable, AddressSpace, TickListener {

    private SwingDisplay display;

    public TondaSystem(SwingDisplay display) {
        this.display = display;
    }

    @Override
    public int read(int address) {
        return 0;
    }

    @Override
    public void write(int address, int value) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void run() {

    }
}
