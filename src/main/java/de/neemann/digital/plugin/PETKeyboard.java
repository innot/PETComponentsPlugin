
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

import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import static de.neemann.digital.core.element.PinInfo.input;

import java.util.LinkedList;

/**
 * The Keyboard component
 */
public class PETKeyboard extends Node implements Element {

    /**
     * The keyboard description
     */
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(PETKeyboard.class, //
            input("KEY_IN", "ASCII key code"),
            input("KEY_AVAIL", "Key available signal"),
            input("ROW", "10-Bit Row line to scan"),
            input("CLOCK", "Clock to check for new keys. KEY_AVAIL is checked on positive edge.").setClock())
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.LABEL);

    private final String label;

    // inputs
    private ObservableValue key_in;
    private ObservableValue key_avail;
    private ObservableValue row;
    private ObservableValue clock;

    // output
    private final ObservableValue columns;

    // internal state
    private int[] matrixState;
    private int rowVal;
    private int lastRowVal = 11;
    private Integer lastKey = null;
    private boolean lastClock = false;
    private final KeyboardMatrix keyMatrix = new PET2001NGraphicsKeyboardMatrix();
    private final LinkedList<Integer> keyQueue = new LinkedList<>();

    /**
     * Creates a new keyboard instance
     *
     * @param attributes the attributes
     */
    public PETKeyboard(ElementAttributes attributes) {
        columns = new ObservableValue("Columns", 8)
                .setDescription("8-bit columns value for the current row.")
                .setPinDescription(DESCRIPTION);

        label = attributes.getLabel();
        matrixState = new int[11];
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        key_in = inputs.get(0).checkBits(16, this).addObserverToValue(this);
        key_avail = inputs.get(1).checkBits(1, this).addObserverToValue(this);
        row = inputs.get(2).checkBits(10, this).addObserverToValue(this);
        clock = inputs.get(3).checkBits(1, this).addObserverToValue(this);
    }

    @Override
    public ObservableValues getOutputs() {
        return new ObservableValues(columns);
    }

    @Override
    public void readInputs() {

        if (clock.getBool() && !lastClock) {
            // On the positive edge of the clock, get the key and add it to the input queue.
            if (key_avail.getBool()) {
                int keycode = (int) key_in.getValue();
                keyQueue.add(keycode);
                System.out.println("Keycode: " + keycode + " added to queue");
            }
        }
        lastClock = clock.getBool();

        // the number of the Matrix Row the PET wants to check.
        rowVal = inputToColumn((int) row.getValue());
        if (rowVal == lastRowVal) return;

        lastRowVal = rowVal;

        // If it is the first row, set up the key matrix for any pending key presses.
        if (rowVal == 0) {
            // clear the matrix
            matrixState = new int[11];

            // Check if a key is available, but only if the last key was null.
            // Always insert a no-key-press between the last key and the next key.
            // Otherwise, the PET will think the key was pressed continuously and ignore it.
            if (lastKey != null) {
                lastKey = null;
                return;
            }
            Integer key = keyQueue.poll();
            if (key != null) {
                System.out.println("Keycode: " + key + " read from queue");
                setKeyMatrix(key);
                lastKey = key;
            }
        }
    }

    @Override
    public void writeOutputs() {
        columns.setValue(~matrixState[rowVal]);
    }

    /**
     * @return the keyboard label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Convert the active column line to the number of the active line.
     * <p>
     * Note: if more than one column is active, the lowest active column is returned.
     *
     * @param column 10-bit column with one active bit set.
     * @return number between 0 and 9, or 10 if no active column is set.
     */
    private int inputToColumn(int column) {
        for (int i = 0; i < 10; i++) {
            if ((column & (1 << i)) != 0)
                return i;
        }
        return 10; // Indicates no active column. This might happen during initialisation or during testing.
    }

    private void setKeyMatrix(int keycode) {
        Key key = keyMatrix.getKey(keycode);
        if (key != null) {
            matrixState[key.getRow()] |= (1 << key.getColumn());
            if (key.isShift()) {
                matrixState[8] |= 1;    // Shift key is Row 8, Column 0
            }
        }
    }
}