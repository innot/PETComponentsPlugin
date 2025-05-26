/*
 * Copyright (c) 2025  Thomas Holland <thomas@innot.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.neemann.digital.plugin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Keyboard Matrix for the PET2000N resp. CBM3000 Series with the graphics keyboard.
 * <p>
 * Due to the limitations of the {@link de.neemann.digital.gui.components.terminal.KeyboardDialog}
 * UI class, which does not support most special keys (except for the cursor keys),
 * the PET special keys are mapped to CTRL + KEY sequences.
 * <p>
 * Currently supported CTRL + KEY sequences:
 * <pre>
 *      | PET Key | CTRL + KEY |
 *      |---------|------------|
 *      |HOME     | CTRL-F     |
 *      |CLR      | CTRL-G     |
 *      |DEL      | CTRL-H     |
 *      |INST     | CTRL-I     |
 *      |RVS      | CTRL-O     |
 *      |RVS OFF  | CTRL-P     |
 *      |←        | CTRL-L     |
 *      |↑        | CTRL-U     |
 *      |RUN/STOP | ESC        |
 * </pre>
 * <p>
 * Because we do not have a way to determine if the SHIFT key was pressed
 * (except for the letters), the graphics PETSCII symbols on all non-letter
 * keys cannot be generated.
 */
public class PET2001NGraphicsKeyboardMatrix implements KeyboardMatrix {

    private final ArrayList<Key> keys;


    public PET2001NGraphicsKeyboardMatrix() {

        keys = new ArrayList<>(Collections.nCopies(256, null));

        keys.set(0x00, new Key("", 0, 0));

        keys.set(0x06, new Key("HOME", 0, 6, false)); // CTRL-F for HOME
        keys.set(0x07, new Key("CLR", 0, 6, true)); // CTRL-G for CLR
        keys.set(0x08, new Key("DEL", 1, 7, false)); // CTRL-H for DEL
        keys.set(0x09, new Key("INST", 1, 7, true)); // CTRL-I for INST

        keys.set(0x0a, new Key("RETURN", 6, 5));

        keys.set(0x0c, new Key("←", 0, 5)); // CTRL-L for left arrow

        keys.set(0x0f, new Key("RVS", 9, 0)); // CTRL-O for RVS on
        keys.set(0x10, new Key("OFF", 9, 0, true)); // CTRL-P for RVS off
        keys.set(0x11, new Key("CRSR UP", 1, 6, true)); // Codes from Digital Keyboard Component (CTRL-Q)
        keys.set(0x12, new Key("CRSR DOWN", 1, 6, false)); // Codes from Digital Keyboard Component (CTRL-R)
        keys.set(0x13, new Key("CRSR LEFT", 0, 7, true)); // Codes from Digital Keyboard Component (CTRL-S)
        keys.set(0x14, new Key("CRSR RIGHT", 0, 7, false)); // Codes from Digital Keyboard Component (CTRL-T)
        keys.set(0x15, new Key("↑", 2, 5)); // CTRL-U for up arrow

        keys.set(0x1b, new Key("RUN/STOP", 9, 4)); // ESC for RUN/STOP

        keys.set(0x20, new Key("SPACE", 9, 2));
        keys.set(0x21, new Key("!", 0, 0));
        keys.set(0x22, new Key("''", 1, 0));
        keys.set(0x23, new Key("#", 0, 1));
        keys.set(0x24, new Key("$", 1, 1));
        keys.set(0x25, new Key("%", 0, 2));
        keys.set(0x26, new Key("&", 0, 3));
        keys.set(0x27, new Key("'", 1, 2));
        keys.set(0x28, new Key("(", 0, 4));
        keys.set(0x29, new Key(")", 1, 4));

        keys.set(0x2a, new Key("*", 5, 7));
        keys.set(0x2b, new Key("+", 7, 7));
        keys.set(0x2c, new Key(",", 7, 3));
        keys.set(0x2d, new Key("-", 8, 7));
        keys.set(0x2e, new Key(".", 9, 6));
        keys.set(0x2f, new Key("/", 3, 7));

        keys.set(0x30, new Key("0", 8, 6));
        keys.set(0x31, new Key("1", 6, 6));
        keys.set(0x32, new Key("2", 7, 6));
        keys.set(0x33, new Key("3", 6, 7));
        keys.set(0x34, new Key("4", 4, 6));
        keys.set(0x35, new Key("5", 5, 6));
        keys.set(0x36, new Key("6", 4, 7));
        keys.set(0x37, new Key("7", 2, 6));
        keys.set(0x38, new Key("8", 3, 6));
        keys.set(0x39, new Key("9", 2, 7));

        keys.set(0x3a, new Key(":", 5, 4));
        keys.set(0x3b, new Key(";", 6, 4));
        keys.set(0x3c, new Key("<", 9, 3));
        keys.set(0x3d, new Key("=", 9, 7));
        keys.set(0x3e, new Key(">", 8, 4));

        keys.set(0x3f, new Key("?", 7, 4));
        keys.set(0x40, new Key("@", 8, 1, true));
        keys.set(0x41, new Key("A", 4, 0, true));
        keys.set(0x42, new Key("B", 6, 2, true));
        keys.set(0x43, new Key("C", 6, 1, true));
        keys.set(0x44, new Key("D", 4, 1, true));
        keys.set(0x45, new Key("E", 2, 1, true));
        keys.set(0x46, new Key("F", 5, 1, true));
        keys.set(0x47, new Key("G", 4, 2, true));
        keys.set(0x48, new Key("H", 5, 2, true));
        keys.set(0x49, new Key("I", 3, 3, true));
        keys.set(0x4a, new Key("J", 4, 3, true));
        keys.set(0x4b, new Key("K", 5, 3, true));
        keys.set(0x4c, new Key("L", 4, 4, true));
        keys.set(0x4d, new Key("M", 6, 3, true));
        keys.set(0x4e, new Key("N", 7, 2, true));
        keys.set(0x4f, new Key("O", 2, 4, true));
        keys.set(0x50, new Key("P", 3, 4, true));
        keys.set(0x51, new Key("Q", 2, 0, true));
        keys.set(0x52, new Key("R", 3, 1, true));
        keys.set(0x53, new Key("S", 5, 0, true));
        keys.set(0x54, new Key("T", 2, 2, true));
        keys.set(0x55, new Key("U", 2, 3, true));
        keys.set(0x56, new Key("V", 7, 1, true));
        keys.set(0x57, new Key("W", 3, 0, true));
        keys.set(0x58, new Key("X", 7, 0, true));
        keys.set(0x59, new Key("Y", 3, 2, true));
        keys.set(0x5a, new Key("Z", 6, 0, true));

        keys.set(0x5b, new Key("[", 9, 1));
        keys.set(0x5c, new Key("\\", 1, 3));
        keys.set(0x5d, new Key("]", 8, 2));

        keys.set(0x61, new Key("a", 4, 0));
        keys.set(0x62, new Key("b", 6, 2));
        keys.set(0x63, new Key("c", 6, 1));
        keys.set(0x64, new Key("d", 4, 1));
        keys.set(0x65, new Key("e", 2, 1));
        keys.set(0x66, new Key("f", 5, 1));
        keys.set(0x67, new Key("g", 4, 2));
        keys.set(0x68, new Key("h", 5, 2));
        keys.set(0x69, new Key("i", 3, 3));
        keys.set(0x6a, new Key("j", 4, 3));
        keys.set(0x6b, new Key("k", 5, 3));
        keys.set(0x6c, new Key("l", 4, 4));
        keys.set(0x6d, new Key("m", 6, 3));
        keys.set(0x6e, new Key("n", 7, 2));
        keys.set(0x6f, new Key("o", 2, 4));
        keys.set(0x70, new Key("p", 3, 4));
        keys.set(0x71, new Key("q", 2, 0));
        keys.set(0x72, new Key("r", 3, 1));
        keys.set(0x73, new Key("s", 5, 0));
        keys.set(0x74, new Key("t", 2, 2));
        keys.set(0x75, new Key("u", 2, 3));
        keys.set(0x76, new Key("v", 7, 1));
        keys.set(0x77, new Key("w", 3, 0));
        keys.set(0x78, new Key("x", 7, 0));
        keys.set(0x79, new Key("y", 3, 2));
        keys.set(0x7a, new Key("z", 6, 0));

        keys.set(0x7f, new Key("DEL", 1, 7));
    }

    @Override
    public Key getKey(int keycode) {
        return keys.get(keycode);
    }

}
