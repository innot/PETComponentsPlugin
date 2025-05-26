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

import de.neemann.digital.draw.library.ComponentManager;
import de.neemann.digital.draw.library.ComponentSource;
import de.neemann.digital.draw.library.ElementLibrary;
import de.neemann.digital.draw.library.InvalidNodeException;
import de.neemann.digital.draw.shapes.GenericShape;
import de.neemann.digital.gui.Main;

/**
 * A library of components required for the PET Digital simulation project.
 * <p>
 *     The main components are:
 *     <li>6502 CPU</li>
 *     <li>6520 PIA</li>
 *     <li>6522 VIA</li>
 *     <li>PET Keyboard input</li>
 * </p>
 * Also included is a modified version of the original {@link de.neemann.digital.core.memory.RAMSinglePortSel}
 * element, which fills the ram with random data to make it behave more like a real old-school static ram.
 */
public class PETComponentSource implements ComponentSource {

	/**
     * Register the components provided by this ComponentSource.
     *
     * @param manager the ComponentManager
     * @throws InvalidNodeException InvalidNodeException
     */
    @Override
    public void registerComponents(ComponentManager manager) throws InvalidNodeException {

    	manager.addComponent("PET Custom", MOS6502.DESCRIPTION,
    			(attr, inputs, outputs) -> new GenericShape("6502", inputs, outputs, attr.getLabel(), true, 10));

    	manager.addComponent("PET Custom", MOS6522.DESCRIPTION, 
    			(attr, inputs, outputs) -> new GenericShape("6522", inputs, outputs, attr.getLabel(), true, 10));

    	manager.addComponent("PET Custom", MOS6520.DESCRIPTION, 
    			(attr, inputs, outputs) -> new GenericShape("6520", inputs, outputs, attr.getLabel(), true, 10));
    	
    	manager.addComponent("PET Custom", PETKeyboard.DESCRIPTION,
    			(attr, inputs, outputs) -> new GenericShape("PETKeyboard", inputs, outputs, attr.getLabel(), true, 10));

		manager.addComponent("PET Custom/component", RAMSinglePortSelRnd.DESCRIPTION);


	}

    /**
     * Start Digital with this ComponentSource attached to make debugging easier.
     * IMPORTANT: Remove the jar from Digital settings!!!
     *
     * @param args args
     */
    public static void main(String[] args) {
        new Main.MainBuilder()
                .setLibrary(new ElementLibrary().registerComponentSource(new PETComponentSource()))
                .openLater();
    }
}
