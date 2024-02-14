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

    JPanel breakPointPanel1 = new JPanel();
    JPanel breakPointPanel2 = new JPanel();

    JButton setBrkPoint1 = new JButton("Set");
    JButton resetBrkPoint1 = new JButton("Reset");

    JButton setBrkPoint2 = new JButton("Set");
    JButton resetBrkPoint2 = new JButton("Reset");

    JLabel lblBrkPoint1 = new JLabel("Breakpoint 1:");
    JLabel lblBrkPoint2 = new JLabel("Breakpoint 2:");

    JTextField fieldBrkPoint1 = new JTextField("none", 4);
    JTextField fieldBrkPoint2 = new JTextField("none", 4);

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

        Font textComponentFont = new Font(Font.MONOSPACED, Font.BOLD, 12);

        debugExitButton.addActionListener(this);
        debugStepButton.addActionListener(this);

        textArea.setFont(textComponentFont);
        textArea.setColumns(40);
        textArea.setRows(20);
        textArea.setEditable(false);

        fieldBrkPoint1.setEditable(true);
        fieldBrkPoint2.setEditable(true);
        fieldBrkPoint1.setFont(textComponentFont);
        fieldBrkPoint2.setFont(textComponentFont);

        breakPointPanel1.setLayout(new BoxLayout(breakPointPanel1, BoxLayout.X_AXIS));
        breakPointPanel1.add(lblBrkPoint1);
        breakPointPanel1.add(fieldBrkPoint1);
        breakPointPanel1.add(setBrkPoint1);
        breakPointPanel1.add(resetBrkPoint1);

        breakPointPanel2.setLayout(new BoxLayout(breakPointPanel2, BoxLayout.X_AXIS));
        breakPointPanel2.add(lblBrkPoint2);
        breakPointPanel2.add(fieldBrkPoint2);
        breakPointPanel2.add(setBrkPoint2);
        breakPointPanel2.add(resetBrkPoint2);

        add(textArea);
        add(debugExitButton);
        add(debugStepButton);
        add(breakPointPanel1);
        add(breakPointPanel2);
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
