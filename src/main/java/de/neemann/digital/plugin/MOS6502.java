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

import static de.neemann.digital.core.element.PinInfo.input;

import de.innot.sim6502.Sim6502;
import de.innot.sim6502.Sim6502Input;
import de.innot.sim6502.Sim6502Output;
import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.draw.elements.PinException;

/**
 * A 6502 CPU simulator Component.
 * <p>
 * This class wraps the {@link Sim6502} class and maps its in- and outputs to the {@link Node} interface.
 * <p>
 * Note: All inputs and outputs have the logic levels as a real 6502, i.e. RESET, NMI and IRQ
 * are 'active low' while RDY is 'active high'.
 * <p>
 */
public class MOS6502 extends Node implements Element {
	
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(MOS6502.class,
            input("RDY","READY line, active high"),
            input("~RESET", "RESET line, active low" ),
            input("~NMI","NMI line, active low"),
            input("~IRQ", "IRQ line, active low"),
            input("PHI0","Clock input. Output is updated on negative edge.").setClock())
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.LABEL);

    private final ElementAttributes attr;
    private final String label;

    // The node inputs
    private ObservableValue rdyIn;
    private ObservableValue resetIn;
    private ObservableValue nmiIn;
    private ObservableValue irqIn;
    private ObservableValue clockIn;
    private ObservableValue dataIn;

    // The node outputs
    private final ObservableValue dataOut;
    private final ObservableValue addrOut;
    private final ObservableValue syncOut;
    private final ObservableValue rwOut;
    private final ObservableValue phi1Out;
    private final ObservableValue phi2Out;

    // The Sim6502 simulation
    private final Sim6502 sim6502;
    private final Sim6502Input sim_input;
    private Sim6502Output sim_output;
    
    /**
     * Creates a new instance
     *
     * @param attr the element attributes
     */
    public MOS6502(ElementAttributes attr) {
        super(true);
        this.attr = attr;
        label = attr.getLabel();
        dataOut = new ObservableValue("DA", 8)
                .setDescription("Bidirectional 8-bit Data bus")
                .setToHighZ()
                .setPinDescription(DESCRIPTION)
                .setBidirectional();

        addrOut = new ObservableValue("AD", 16)
                .setDescription("16-bit Address bus")
                .setToHighZ()
                .setPinDescription(DESCRIPTION)
                .setBidirectional();
        
        rwOut = new ObservableValue("R/~W", 1)
                .setDescription("Read/Write signal, high during read and low during write cycle")
        		.setPinDescription(DESCRIPTION);

        syncOut = new ObservableValue("SYNC", 1)
                .setDescription("Synchronize signal, high during instruction fetch cycle")
        		.setPinDescription(DESCRIPTION);

        phi1Out = new ObservableValue("PHI1", 1)
                .setDescription("Inverted Clock signal for external components")
        		.setPinDescription(DESCRIPTION);

        phi2Out = new ObservableValue("PHI2", 1)
                .setDescription("Clock signal for external components")
        		.setPinDescription(DESCRIPTION);
        
        sim6502 = new Sim6502();
        sim_input = new Sim6502Input();
        sim_output = new Sim6502Output();
    }

    
    @Override
    public ObservableValues getOutputs() throws PinException {
    	return new ObservableValues(dataOut, addrOut, rwOut, syncOut, phi1Out, phi2Out);
    }


    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        rdyIn = inputs.get(0).checkBits(1, this).addObserverToValue(this);
        resetIn= inputs.get(1).checkBits(1, this).addObserverToValue(this);
        nmiIn= inputs.get(2).checkBits(1, this).addObserverToValue(this);
        irqIn= inputs.get(3).checkBits(1, this).addObserverToValue(this);
        clockIn= inputs.get(4).checkBits(1, this).addObserverToValue(this);
        dataIn = inputs.get(5).checkBits(8, this).addObserverToValue(this);
    }

    private boolean last_clock = false;

    @Override
    public void readInputs() throws NodeException {
    	boolean new_clock = clockIn.getBool();
    	
    	// return if the clock is unchanged.
    	if (new_clock == last_clock) return;
    	last_clock = new_clock;
    	
    	// get the current input pin values
    	sim_input.ready = rdyIn.getBool();
    	sim_input.reset = !resetIn.getBool();	// The sim6502 uses active high
    	sim_input.nmi = !nmiIn.getBool();		// dito
    	sim_input.irq = !irqIn.getBool();		// dito
    	sim_input.data = (int)dataIn.getValue();

    	try {
	    	if (!new_clock) {
	        	sim_output = sim6502.tick(sim_input);
	    	}
    	} catch (Exception e){
    		throw new NodeException("Error in 6502 simulation",e);
    	}
    }


    @Override
    public void writeOutputs() throws NodeException {
    	addrOut.setValue(sim_output.addr);
    	syncOut.setBool(sim_output.sync);
    	rwOut.setBool(sim_output.rw);

        // Set the data bus to output when R/W is low. Otherwise, set to high-Z.
    	if (!sim_output.rw) {
    		dataOut.setValue(sim_output.data);
    	} else {
    		dataOut.setToHighZ();
    	}
    	
    	phi2Out.setBool(last_clock);
    	phi1Out.setBool(!last_clock);
    }
}

