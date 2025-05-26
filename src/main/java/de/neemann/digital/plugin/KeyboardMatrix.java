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
 * The KeyboardMatrix Interface is used to map ASCII characters to the PET keyboard matrix.
 */
public interface KeyboardMatrix {

	/**
	 * Convert ASCII character to the PET keyboard matrix {@link Key} object.
	 *
	 * @param keycode	ASCII character code.
	 * @return {@link Key} object or null if the character is not mapped.
	 */
    Key getKey(int keycode);

}
