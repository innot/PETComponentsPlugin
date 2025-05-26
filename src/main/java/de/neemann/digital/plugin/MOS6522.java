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

import de.innot.sim6502.Sim6522;
import de.innot.sim6502.Sim6522Input;
import de.innot.sim6502.Sim6522Output;
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
 * A 6522 VIA (Versatile Interface Adapter) simulator Component.
 * <p>
 * This class wraps the {@link Sim6522} class and maps its in- and outputs to the {@link Node} interface.
 * <p>
 * Note: All inputs and outputs have the logic levels as a real 6520.
 * <p>
 */
public class MOS6522 extends Node implements Element {

    /**
     * The MOS6522 {@link ElementTypeDescription}
     */
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(MOS6522.class, //
            input("CS1"), //
            input("~CS2"), //
            input("RS", "Register Adress [0..3]"), //
            input("R/~W"), //
            input("~RESET"), input("PHI2").setClock(), //
            input("CA1"), //
            input("CB1"), //
            input("PA_in"), //
            input("PB_in")) //
            .addAttribute(Keys.ROTATE).addAttribute(Keys.LABEL);

    private final ElementAttributes attr;
    private final String label;

    private ObservableValue cs1In;
    private ObservableValue cs2In;
    private ObservableValue rsIn;
    private ObservableValue rwIn;
    private ObservableValue resetIn;
    private ObservableValue clockIn;
    // The bidirectional pins
    private ObservableValue dataIn;
    private ObservableValue paIn;
    private ObservableValue pbIn;
    private ObservableValue ca1In;
    private ObservableValue ca2In;
    private ObservableValue cb1In;
    private ObservableValue cb2In;

    // The outputs
    private final ObservableValue irqOut;
    private final ObservableValue ca2Out;
    private final ObservableValue paOut;
    private final ObservableValue padirOut;
    private final ObservableValue pbOut;
    private final ObservableValue pbdirOut;
    private final ObservableValue cb2Out;

    private final ObservableValue dataOut;

    private final Sim6522 sim6522;
    private final Sim6522Input sim_input;
    private Sim6522Output sim_output;

    /**
     * Creates a new instance
     *
     * @param attr the element attributes
     */
    public MOS6522(ElementAttributes attr) {
        super(true);
        this.attr = attr;
        label = attr.getLabel();

        irqOut = new ObservableValue("~IRQ", 1).setPinDescription(DESCRIPTION);

        ca2Out = new ObservableValue("CA2", 1).setToHighZ().setPinDescription(DESCRIPTION).setBidirectional();

        paOut = new ObservableValue("PA", 8).setToHighZ().setPinDescription(DESCRIPTION);
        padirOut = new ObservableValue("PA_DIR", 8).setPinDescription(DESCRIPTION);

        pbOut = new ObservableValue("PB", 8).setToHighZ().setPinDescription(DESCRIPTION);
        pbdirOut = new ObservableValue("PB_DIR", 8).setPinDescription(DESCRIPTION);

        cb2Out = new ObservableValue("CB2", 1).setToHighZ().setPinDescription(DESCRIPTION).setBidirectional();

        dataOut = new ObservableValue("D", 8).setToHighZ().setPinDescription(DESCRIPTION).setBidirectional();

        sim6522 = new Sim6522();
        sim_input = new Sim6522Input();
        sim_output = new Sim6522Output();
    }

    @Override
    public ObservableValues getOutputs() {
        return new ObservableValues(dataOut, irqOut, ca2Out, paOut, padirOut, pbOut, pbdirOut, cb2Out);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        cs1In = inputs.get(0).checkBits(1, this).addObserverToValue(this);
        cs2In = inputs.get(1).checkBits(1, this).addObserverToValue(this);
        rsIn = inputs.get(2).checkBits(4, this).addObserverToValue(this);
        rwIn = inputs.get(3).checkBits(1, this).addObserverToValue(this);
        resetIn = inputs.get(4).checkBits(1, this).addObserverToValue(this);
        clockIn = inputs.get(5).checkBits(1, this).addObserverToValue(this);
        ca1In = inputs.get(6).checkBits(1, this).addObserverToValue(this);
        cb1In = inputs.get(7).checkBits(1, this).addObserverToValue(this);
        paIn = inputs.get(8).checkBits(8, this).addObserverToValue(this);
        pbIn = inputs.get(9).checkBits(8, this).addObserverToValue(this);

        dataIn = inputs.get(10).checkBits(8, this).addObserverToValue(this);
        ca2In = inputs.get(11).checkBits(1, this).addObserverToValue(this);
        cb2In = inputs.get(12).checkBits(1, this).addObserverToValue(this);
    }

    private boolean last_clock = false;

    @Override
    public void readInputs() throws NodeException {

        boolean new_clock = clockIn.getBool();

        // get the current input pin values
        sim_input.data = (int) dataIn.getValue();
        sim_input.cs1 = cs1In.getBool();
        sim_input.cs2 = cs2In.getBool();
        sim_input.rs = (int) rsIn.getValue();
        sim_input.rw = rwIn.getBool();
        sim_input.phi2 = clockIn.getBool();
        sim_input.reset = resetIn.getBool();
        sim_input.pa = (int) paIn.getValue();
        sim_input.pb = (int) pbIn.getValue();
        sim_input.ca1 = ca1In.getBool();
        sim_input.ca2 = ca2In.getBool();
        sim_input.cb1 = cb1In.getBool();
        sim_input.cb2 = cb2In.getBool();

        try {
            // Do not execute tick if the clock has not changed.
            if (new_clock == last_clock)
                return;
            last_clock = new_clock;

            if (new_clock) {
                // run on the rising edge of phi2
                // (all CPU lines have been set on the previous low part of phi2)
                sim_output = sim6522.tick(sim_input);
            }
        } catch (Exception e) {
            throw new NodeException("Error in 6522 simulation", e);
        }
    }

    @Override
    public void writeOutputs() {
        irqOut.setBool(sim_output.irq);
        paOut.setValue(sim_output.pa);
        padirOut.setValue(sim_output.pa_dir);
        pbOut.setValue(sim_output.pb);
        pbdirOut.setValue(sim_output.pb_dir);
        ca2Out.setBool(sim_output.ca2);
        cb2Out.setBool(sim_output.cb2);

        if (sim_input.rw && sim_input.cs1 && !sim_input.cs2) {
            // Read cycle and the chip is enabled: Set data bus value
            dataOut.setValue(sim_output.data);
        } else {
            // Write cycle or chip disabled: Disable data bus
            dataOut.setToHighZ();
        }
    }

}
