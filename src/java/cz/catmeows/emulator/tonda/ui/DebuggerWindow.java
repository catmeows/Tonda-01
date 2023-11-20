package cz.catmeows.emulator.tonda.ui;

import cz.catmeows.emulator.tonda.m6809.Debugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DebuggerWindow extends JDialog implements ActionListener {

    JTextArea textArea = new JTextArea();
    JButton debugExitButton = new JButton("Run");
    JButton debugStepButton = new JButton("Single Step");

    Debugger debugger;

    public synchronized void setContent(String content) {
        textArea.setText(content);
    }

    public DebuggerWindow(JFrame parentFrame, Debugger debugger) {
        super(parentFrame,"Debugger", false);
        this.debugger = debugger;
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                debugger.setCanContinue(true);
                debugger.setEnabled(false);
                super.windowClosing(e);
            }
        });

        debugExitButton.addActionListener(this);
        debugStepButton.addActionListener(this);

        textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        textArea.setColumns(40);
        textArea.setRows(20);
        textArea.setEditable(false);

        add(textArea);
        add(debugExitButton);
        add(debugStepButton);
        setVisible(true);
        pack();
        setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == debugExitButton) {
            debugger.closeWindow();

        }
        if (e.getSource() == debugStepButton) {
            debugger.setCanContinue(true);
        }
    }
}
