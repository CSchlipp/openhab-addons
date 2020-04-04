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
package org.openhab.binding.tvheadend.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link TvHeadendBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Christian Schlipp - Initial contribution
 */
@NonNullByDefault
public class TvHeadendBindingConstants {

    private static final String BINDING_ID = "tvheadend";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_TVHSERVER = new ThingTypeUID(BINDING_ID, "bridge");

    // List of all Channel ids
    public static final String CHANNEL_SW_VERSION = "sw_version";
    public static final String CHANNEL_API_VERSION = "api_version";
    public static final String CHANNEL_NAME = "name";
    public static final String CHANNEL_CAPABILITES = "capabilities";
}
