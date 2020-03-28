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
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link TvHeadendConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Christian Schlipp - Initial contribution
 */
@NonNullByDefault
public class TvHeadendConfiguration {

    /**
     * IP Address of TVH server.
     */
    @NonNullByDefault({})
    private String ipAddress;

    /**
     * Port of TVH server.
     */
    @Nullable
    private Integer port;

    /**
     * Username of TVH server user.
     */
    @Nullable
    private String username;

    /**
     * Password of TVH server user.
     */
    @Nullable
    private String password;

    /**
     * Polling interval
     */
    private int pollingInterval = 10;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return (port != null) ? port : 9981;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
