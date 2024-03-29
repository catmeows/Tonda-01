package cz.catmeows.emulator.tonda;

import cz.catmeows.emulator.tonda.m6809.Debugger;
import cz.catmeows.emulator.tonda.m6821.KeyboardMatrix;
import cz.catmeows.emulator.tonda.ui.SwingDisplay;
import cz.catmeows.emulator.tonda.ui.SwingDisplayImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class Emulator {

    private final SwingDisplay display;
    private JFrame mainWindow;

    private TondaSystem system;

    private KeyListener keyboardMatrix;

    private Debugger debugger = new Debugger(mainWindow);

    public Emulator() {
        keyboardMatrix = new KeyboardMatrix();
        display = new SwingDisplayImpl();
        system = new TondaSystem(display, (KeyboardMatrix) keyboardMatrix, debugger);
        SwingUtilities.invokeLater(()->startGui());
        System.out.println("Started");

    }

    private void startGui() {
        mainWindow = new JFrame("TondaOne");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setJMenuBar(buildMenuBar());
        mainWindow.setContentPane((Container) display);
        //mainWindow.setPreferredSize(new Dimension(740, 600));
        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.addKeyListener(keyboardMatrix);


        System.out.println("Will start GUI");
        new Thread(display).start();
        System.out.println("Will start system");
        new Thread(system).start();

    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem insertTapeItem = new JMenuItem("Insert tape");
        fileMenu.add(insertTapeItem);
        JMenuItem ejectTapeItem = new JMenuItem("Eject tape");
        fileMenu.add(ejectTapeItem);
        JMenuItem insertDiskItem = new JMenuItem("Insert disk");
        fileMenu.add(insertDiskItem);
        JMenuItem ejectDiskItem = new JMenuItem("Eject disk");
        fileMenu.add(ejectDiskItem);
        JMenuItem loadSnapshotItem = new JMenuItem("Load snapshot");
        fileMenu.add(loadSnapshotItem);
        JMenuItem saveSnapshotItem = new JMenuItem("Save snapshot");
        fileMenu.add(saveSnapshotItem);
        JMenuItem loadBinaryItem = new JMenuItem("Load binary");
        fileMenu.add(loadBinaryItem);
        JMenuItem saveBinaryItem = new JMenuItem("Save binary");
        fileMenu.add(saveBinaryItem);
        JMenuItem captureScreenItem = new JMenuItem("Capture screen");
        fileMenu.add(captureScreenItem);

        JMenu machineMenu = new JMenu("Machine");
        JMenuItem breakItem = new JMenuItem("Press BREAK");
        machineMenu.add(breakItem);
        JCheckBoxMenuItem diskEnableItem = new JCheckBoxMenuItem("Enable disk");
        machineMenu.add(diskEnableItem);
        machineMenu.addSeparator();
        ButtonGroup printerGroup = new ButtonGroup();
        JRadioButtonMenuItem noneItem = new JRadioButtonMenuItem("No printer");
        noneItem.setSelected(true);
        printerGroup.add(noneItem);
        machineMenu.add(noneItem);
        JRadioButtonMenuItem matrixItem = new JRadioButtonMenuItem("ALFA-40 printer");
        printerGroup.add(matrixItem);
        machineMenu.add(matrixItem);
        JRadioButtonMenuItem plotterItem = new JRadioButtonMenuItem("INKER plotter");
        printerGroup.add(plotterItem);
        machineMenu.add(plotterItem);
        machineMenu.addSeparator();
        ButtonGroup speedGroup = new ButtonGroup();
        JRadioButtonMenuItem pauseItem = new JRadioButtonMenuItem("Pause");
        speedGroup.add(pauseItem);
        machineMenu.add(pauseItem);
        JRadioButtonMenuItem halfItem = new JRadioButtonMenuItem("Half speed");
        speedGroup.add(halfItem);
        machineMenu.add(halfItem);
        JRadioButtonMenuItem normalItem = new JRadioButtonMenuItem("Normal speed");
        normalItem.setSelected(true);
        speedGroup.add(normalItem);
        machineMenu.add(normalItem);
        JRadioButtonMenuItem doubleItem = new JRadioButtonMenuItem("Double speed");
        speedGroup.add(doubleItem);
        machineMenu.add(doubleItem);
        machineMenu.addSeparator();
        JMenuItem resetItem = new JMenuItem("Reset");
        machineMenu.add(resetItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem tapeBrowserItem = new JMenuItem("Tape Browser");
        toolsMenu.add(tapeBrowserItem);
        JMenuItem diskBrowserItem = new JMenuItem("Disk Browser");
        toolsMenu.add(diskBrowserItem);
        JMenuItem debuggerItem = new JMenuItem("Debugger");
        debuggerItem.addActionListener(debugger);
        toolsMenu.add(debuggerItem);
        JMenuItem printoutBrowserItem = new JMenuItem("Printout browser");
        toolsMenu.add(printoutBrowserItem);
        JMenuItem quickTypeItem = new JMenuItem("QuickType");
        toolsMenu.add(quickTypeItem);
        JMenuItem keyboardHelperItem = new JMenuItem("Keyboard");
        toolsMenu.add(keyboardHelperItem);

        bar.add(fileMenu);
        bar.add(machineMenu);
        bar.add(toolsMenu);

        return bar;
    }

}
