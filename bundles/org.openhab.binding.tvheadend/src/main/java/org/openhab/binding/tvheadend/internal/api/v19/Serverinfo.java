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
package org.openhab.binding.tvheadend.internal.api.v19;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.tvheadend.internal.TvHeadendBindingConstants;
import org.openhab.binding.tvheadend.internal.TvHeadendStateHandler;
import org.openhab.binding.tvheadend.internal.api.TvHeadendApiEndpoint;

/**
 * Parsed Serverinfo
 *
 * @author Christian Schlipp - Initial contribution
 */
public class Serverinfo extends TvHeadendApiEndpoint {

    private static final String endpointUrl = "/api/serverinfo";

    @SerializedName("sw_version")
    @Expose
    private String swVersion;
    @SerializedName("api_version")
    @Expose
    private Integer apiVersion;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("capabilities")
    @Expose
    private List<String> capabilities;

    public String getSwVersion() {
        return swVersion;
    }

    @TvHeadendStateHandler(channelUID = TvHeadendBindingConstants.CHANNEL_SW_VERSION)
    public StringType getConvertedSwVersion() {
        return new StringType(swVersion);
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

    @TvHeadendStateHandler(channelUID = TvHeadendBindingConstants.CHANNEL_API_VERSION)
    public StringType getConvertedApiVersion() {
        return new StringType((apiVersion != null) ? apiVersion.toString() : "");
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getName() {
        return name;
    }

    @TvHeadendStateHandler(channelUID = TvHeadendBindingConstants.CHANNEL_NAME)
    public StringType getConvertedName() {
        return new StringType(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    @TvHeadendStateHandler(channelUID = TvHeadendBindingConstants.CHANNEL_CAPABILITES)
    public StringType getConvertedCapabilities() {
        if (capabilities != null && !capabilities.isEmpty()) {
            return new StringType(String.join(", ", capabilities));
        }
        return new StringType();
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }

}