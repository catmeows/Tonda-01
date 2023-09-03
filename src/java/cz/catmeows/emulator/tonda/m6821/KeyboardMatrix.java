package cz.catmeows.emulator.tonda.m6821;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardMatrix implements KeyListener {


    /*

     Row     R2..R0      bits 6      5      4      3      2      1      0

     0       %000             edit   1      2      3      4      5      6
     1       %001             ctrl   Q      W      E      R      T      Y
     2       %010             lshf   A      S      D      F      G      H
     3       %011             space  Z      X      C      V      B      N
     4       %100             right  down  left    /      .      ,      M
     5       %101             rshft  up     '      ;      L      K      J
     6       %110             enter  ]      [      P      O      I      U
     7       %111             del    =      -      0      9      8      7

     */


    private int[] keyMatrix = new int [8];

    public KeyboardMatrix() {
        for (int i=0; i<8; i++) {
            keyMatrix[i] = 0x7f;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_6 -> keyMatrix[0] = keyMatrix[0] & 0xfe;
            case KeyEvent.VK_5 -> keyMatrix[0] = keyMatrix[0] & 0xfd;
            case KeyEvent.VK_4 -> keyMatrix[0] = keyMatrix[0] & 0xfb;
            case KeyEvent.VK_3 -> keyMatrix[0] = keyMatrix[0] & 0xf7;
            case KeyEvent.VK_2 -> keyMatrix[0] = keyMatrix[0] & 0xef;
            case KeyEvent.VK_1 -> keyMatrix[0] = keyMatrix[0] & 0xdf;
            case KeyEvent.VK_TAB -> keyMatrix[0] = keyMatrix[0] & 0xbf;

            case KeyEvent.VK_Y -> keyMatrix[1] = keyMatrix[1] & 0xfe;
            case KeyEvent.VK_T -> keyMatrix[1] = keyMatrix[1] & 0xfd;
            case KeyEvent.VK_R -> keyMatrix[1] = keyMatrix[1] & 0xfb;
            case KeyEvent.VK_E -> keyMatrix[1] = keyMatrix[1] & 0xf7;
            case KeyEvent.VK_W -> keyMatrix[1] = keyMatrix[1] & 0xef;
            case KeyEvent.VK_Q -> keyMatrix[1] = keyMatrix[1] & 0xdf;
            case KeyEvent.VK_CAPS_LOCK -> keyMatrix[1] = keyMatrix[1] & 0xbf;

            case KeyEvent.VK_H -> keyMatrix[2] = keyMatrix[2] & 0xfe;
            case KeyEvent.VK_G -> keyMatrix[2] = keyMatrix[2] & 0xfd;
            case KeyEvent.VK_F -> keyMatrix[2] = keyMatrix[2] & 0xfb;
            case KeyEvent.VK_D -> keyMatrix[2] = keyMatrix[2] & 0xf7;
            case KeyEvent.VK_S -> keyMatrix[2] = keyMatrix[2] & 0xef;
            case KeyEvent.VK_A -> keyMatrix[2] = keyMatrix[2] & 0xdf;
            //left shift resolved separately

            case KeyEvent.VK_N -> keyMatrix[3] = keyMatrix[3] & 0xfe;
            case KeyEvent.VK_B -> keyMatrix[3] = keyMatrix[3] & 0xfd;
            case KeyEvent.VK_V -> keyMatrix[3] = keyMatrix[3] & 0xfb;
            case KeyEvent.VK_C -> keyMatrix[3] = keyMatrix[3] & 0xf7;
            case KeyEvent.VK_X -> keyMatrix[3] = keyMatrix[3] & 0xef;
            case KeyEvent.VK_Z -> keyMatrix[3] = keyMatrix[3] & 0xdf;
            case KeyEvent.VK_SPACE -> keyMatrix[3] = keyMatrix[3] & 0xbf;

            case KeyEvent.VK_M -> keyMatrix[4] = keyMatrix[4] & 0xfe;
            case KeyEvent.VK_COMMA -> keyMatrix[4] = keyMatrix[4] & 0xfd;
            case KeyEvent.VK_PERIOD -> keyMatrix[4] = keyMatrix[4] & 0xfb;
            case KeyEvent.VK_SLASH -> keyMatrix[4] = keyMatrix[4] & 0xf7;
            case KeyEvent.VK_LEFT -> keyMatrix[4] = keyMatrix[4] & 0xef;
            case KeyEvent.VK_DOWN -> keyMatrix[4] = keyMatrix[4] & 0xdf;
            case KeyEvent.VK_RIGHT -> keyMatrix[4] = keyMatrix[4] & 0xbf;

            case KeyEvent.VK_J -> keyMatrix[5] = keyMatrix[5] & 0xfe;
            case KeyEvent.VK_K -> keyMatrix[5] = keyMatrix[5] & 0xfd;
            case KeyEvent.VK_L -> keyMatrix[5] = keyMatrix[5] & 0xfb;
            case KeyEvent.VK_SEMICOLON -> keyMatrix[5] = keyMatrix[5] & 0xf7;
            case KeyEvent.VK_QUOTE -> keyMatrix[5] = keyMatrix[5] & 0xef;
            case KeyEvent.VK_UP -> keyMatrix[5] = keyMatrix[5] & 0xdf;
            //right shift resolved separately

            case KeyEvent.VK_U -> keyMatrix[6] = keyMatrix[6] & 0xfe;
            case KeyEvent.VK_I -> keyMatrix[6] = keyMatrix[6] & 0xfd;
            case KeyEvent.VK_O -> keyMatrix[6] = keyMatrix[6] & 0xfb;
            case KeyEvent.VK_P -> keyMatrix[6] = keyMatrix[6] & 0xf7;
            case KeyEvent.VK_OPEN_BRACKET -> keyMatrix[6] = keyMatrix[6] & 0xef;
            case KeyEvent.VK_CLOSE_BRACKET -> keyMatrix[6] = keyMatrix[6] & 0xdf;
            case KeyEvent.VK_ENTER -> keyMatrix[6] = keyMatrix[6] & 0xbf;

            case KeyEvent.VK_7 -> keyMatrix[7] = keyMatrix[7] & 0xfe;
            case KeyEvent.VK_8 -> keyMatrix[7] = keyMatrix[7] & 0xfd;
            case KeyEvent.VK_9 -> keyMatrix[7] = keyMatrix[7] & 0xfb;
            case KeyEvent.VK_0 -> keyMatrix[7] = keyMatrix[7] & 0xf7;
            case KeyEvent.VK_MINUS -> keyMatrix[7] = keyMatrix[7] & 0xef;
            case KeyEvent.VK_EQUALS -> keyMatrix[7] = keyMatrix[7] & 0xdf;
            case KeyEvent.VK_BACK_SPACE -> keyMatrix[7] = keyMatrix[7] & 0xbf;

        }
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) && (e.getKeyCode() == KeyEvent.VK_SHIFT)) {
            keyMatrix[2] = keyMatrix[2] & 0xbf;
        }
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) && (e.getKeyCode() == KeyEvent.VK_SHIFT)) {
            keyMatrix[4] = keyMatrix[4] & 0xbf;
        }
        System.out.println("Pressed");
        printKeyboardState();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_6 -> keyMatrix[0] = keyMatrix[0] | 0x01;
            case KeyEvent.VK_5 -> keyMatrix[0] = keyMatrix[0] | 0x02;
            case KeyEvent.VK_4 -> keyMatrix[0] = keyMatrix[0] | 0x04;
            case KeyEvent.VK_3 -> keyMatrix[0] = keyMatrix[0] | 0x08;
            case KeyEvent.VK_2 -> keyMatrix[0] = keyMatrix[0] | 0x10;
            case KeyEvent.VK_1 -> keyMatrix[0] = keyMatrix[0] | 0x20;
            case KeyEvent.VK_TAB -> keyMatrix[0] = keyMatrix[0] | 0x40;

            case KeyEvent.VK_Y -> keyMatrix[1] = keyMatrix[1] | 0x01;
            case KeyEvent.VK_T -> keyMatrix[1] = keyMatrix[1] | 0x02;
            case KeyEvent.VK_R -> keyMatrix[1] = keyMatrix[1] | 0x04;
            case KeyEvent.VK_E -> keyMatrix[1] = keyMatrix[1] | 0x08;
            case KeyEvent.VK_W -> keyMatrix[1] = keyMatrix[1] | 0x10;
            case KeyEvent.VK_Q -> keyMatrix[1] = keyMatrix[1] | 0x20;
            case KeyEvent.VK_CAPS_LOCK -> keyMatrix[1] = keyMatrix[1] | 0x40;

            case KeyEvent.VK_H -> keyMatrix[2] = keyMatrix[2] | 0x01;
            case KeyEvent.VK_G -> keyMatrix[2] = keyMatrix[2] | 0x02;
            case KeyEvent.VK_F -> keyMatrix[2] = keyMatrix[2] | 0x04;
            case KeyEvent.VK_D -> keyMatrix[2] = keyMatrix[2] | 0x08;
            case KeyEvent.VK_S -> keyMatrix[2] = keyMatrix[2] | 0x10;
            case KeyEvent.VK_A -> keyMatrix[2] = keyMatrix[2] | 0x20;
            //left shift resolved separately

            case KeyEvent.VK_N -> keyMatrix[3] = keyMatrix[3] | 0x01;
            case KeyEvent.VK_B -> keyMatrix[3] = keyMatrix[3] | 0x02;
            case KeyEvent.VK_V -> keyMatrix[3] = keyMatrix[3] | 0x04;
            case KeyEvent.VK_C -> keyMatrix[3] = keyMatrix[3] | 0x08;
            case KeyEvent.VK_X -> keyMatrix[3] = keyMatrix[3] | 0x10;
            case KeyEvent.VK_Z -> keyMatrix[3] = keyMatrix[3] | 0x20;
            case KeyEvent.VK_SPACE -> keyMatrix[3] = keyMatrix[3] | 0x40;

            case KeyEvent.VK_M -> keyMatrix[4] = keyMatrix[4] | 0x01;
            case KeyEvent.VK_COMMA -> keyMatrix[4] = keyMatrix[4] | 0x02;
            case KeyEvent.VK_PERIOD -> keyMatrix[4] = keyMatrix[4] | 0x04;
            case KeyEvent.VK_SLASH -> keyMatrix[4] = keyMatrix[4] | 0x08;
            case KeyEvent.VK_LEFT -> keyMatrix[4] = keyMatrix[4] | 0x10;
            case KeyEvent.VK_DOWN -> keyMatrix[4] = keyMatrix[4] | 0x20;
            case KeyEvent.VK_RIGHT -> keyMatrix[4] = keyMatrix[4] | 0x40;

            case KeyEvent.VK_J -> keyMatrix[5] = keyMatrix[5] | 0x01;
            case KeyEvent.VK_K -> keyMatrix[5] = keyMatrix[5] | 0x02;
            case KeyEvent.VK_L -> keyMatrix[5] = keyMatrix[5] | 0x04;
            case KeyEvent.VK_SEMICOLON -> keyMatrix[5] = keyMatrix[5] | 0x08;
            case KeyEvent.VK_QUOTE -> keyMatrix[5] = keyMatrix[5] | 0x10;
            case KeyEvent.VK_UP -> keyMatrix[5] = keyMatrix[5] | 0x20;
            //right shift resolved separately

            case KeyEvent.VK_U -> keyMatrix[6] = keyMatrix[6] | 0x01;
            case KeyEvent.VK_I -> keyMatrix[6] = keyMatrix[6] | 0x02;
            case KeyEvent.VK_O -> keyMatrix[6] = keyMatrix[6] | 0x04;
            case KeyEvent.VK_P -> keyMatrix[6] = keyMatrix[6] | 0x08;
            case KeyEvent.VK_OPEN_BRACKET -> keyMatrix[6] = keyMatrix[6] | 0x10;
            case KeyEvent.VK_CLOSE_BRACKET -> keyMatrix[6] = keyMatrix[6] | 0x20;
            case KeyEvent.VK_ENTER -> keyMatrix[6] = keyMatrix[6] | 0x40;

            case KeyEvent.VK_7 -> keyMatrix[7] = keyMatrix[7] | 0x01;
            case KeyEvent.VK_8 -> keyMatrix[7] = keyMatrix[7] | 0x02;
            case KeyEvent.VK_9 -> keyMatrix[7] = keyMatrix[7] | 0x04;
            case KeyEvent.VK_0 -> keyMatrix[7] = keyMatrix[7] | 0x08;
            case KeyEvent.VK_MINUS -> keyMatrix[7] = keyMatrix[7] | 0x10;
            case KeyEvent.VK_EQUALS -> keyMatrix[7] = keyMatrix[7] | 0x20;
            case KeyEvent.VK_BACK_SPACE -> keyMatrix[7] = keyMatrix[7] | 0x40;

        }
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) && (e.getKeyCode() == KeyEvent.VK_SHIFT)) {
            keyMatrix[2] = keyMatrix[2] | 0x40;
        }
        if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) && (e.getKeyCode() == KeyEvent.VK_SHIFT)) {
            keyMatrix[4] = keyMatrix[4] | 0x40;
        }
        System.out.println("Released");
        printKeyboardState();

    }

    private void printKeyboardState() {
        for (int i=0; i<8; i++) {
            System.out.println(Integer.toBinaryString(keyMatrix[i]));
        }
        System.out.println("------------------");
    }
}
