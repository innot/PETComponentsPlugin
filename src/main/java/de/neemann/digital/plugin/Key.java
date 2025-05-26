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

/**
 * PET keyboard matrix info for a specific key.
 * <p>
 * The Key class contains the row and column for of a key in the PET keyboard matrix.
 * It also contains a flag to indicate if the shift key is needed to generate this keypress.
 */
public class Key {
    final public String name;
    final private int row;
    final private int column;
    final private boolean shift;

    /**
     * Create a new Key object with shift = false.
     *
     * @param name   The name of the key (info only)
     * @param row    The keyboard matrix row (0 to 9) this key is on.
     * @param column The keyboard matrix column (0 to 7) this key is on.
     */
    public Key(String name, int row, int column) {
        this(name, row, column, false);
    }

    /**
     * Create a new Key object.
     *
     * @param name   The name of the key (info only)
     * @param row    The keyboard matrix row (0 to 9) this key is on.
     * @param column The keyboard matrix column (0 to 7) this key is on.
     * @param shift  If <code>true</code>, the shift key needs to be pressed to generate this key.
     */
    public Key(String name, int row, int column, boolean shift) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.shift = shift;
    }

    /**
     * @return the row of the key in the PET keyboard matrix (for 0 to 9).
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column of the key in the PET keyboard matrix (for 0 to 7).
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return <code>true</code> if the shift key is needed to generate this keypress.
     */
    public boolean isShift() {
        return shift;
    }

}