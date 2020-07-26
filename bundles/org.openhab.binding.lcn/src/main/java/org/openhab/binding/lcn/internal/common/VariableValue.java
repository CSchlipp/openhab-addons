/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lcn.internal.common;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;

/**
 * A value of an LCN variable.
 * <p>
 * It internally stores the native LCN value and allows to convert from/into other units.
 * Some conversions allow to specify whether the source value is absolute or relative.
 * Relative values are used to create {@link VariableValue}s that can be added/subtracted from
 * other (absolute) {@link VariableValue}s.
 *
 * @author Tobias Jüttner - Initial Contribution
 * @author Fabian Wolter - Migration to OH2
 */
@NonNullByDefault
public class VariableValue {
    private static final String SENSOR_DEFECTIVE_STATE = "DEFECTIVE";

    /** The absolute, native LCN value. */
    private final long nativeValue;

    /**
     * Constructor with native LCN value.
     *
     * @param nativeValue the native value
     */
    public VariableValue(long nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * Converts to native value. Mask locked bit.
     *
     * @return the converted value
     */
    public long toNative(boolean useSpecialValues) {
        if (useSpecialValues) {
            return nativeValue & 0x7fff;
        } else {
            return nativeValue;
        }
    }

    /**
     * Returns the lock state if value comes from a regulator set-point.
     * If the variable type is not a regulator, the result is undefined.
     *
     * @return true if the regulator is locked
     */
    public boolean isRegulatorLocked() {
        return (this.nativeValue & 0x8000) != 0;
    }

    /**
     * Returns the defective state of the originating sensor for this variable.
     *
     * @return true if the sensor is defective
     */
    public boolean isSensorDefective() {
        return nativeValue == 0x7f00;
    }

    /**
     * Returns the configuration state of the variable.
     *
     * @return true if the variable is configured via LCN-PRO
     */
    public boolean isConfigured() {
        return this.nativeValue != 0xFFFF;
    }

    public State getState(Variable variable) {
        State stateValue;
        if (variable.useLcnSpecialValues() && isSensorDefective()) {
            stateValue = new StringType(SENSOR_DEFECTIVE_STATE);
        } else if (variable.useLcnSpecialValues() && !isConfigured()) {
            stateValue = new StringType("Not configured in LCN-PRO");
        } else {
            stateValue = new DecimalType(toNative(variable.useLcnSpecialValues()));
        }
        return stateValue;
    }
}
