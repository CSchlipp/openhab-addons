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

import static org.openhab.binding.tvheadend.internal.TvHeadendBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.tvheadend.internal.gson.TvhConnector;
import org.openhab.binding.tvheadend.internal.gson.api19.Serverinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The {@link TvHeadendHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christian Schlipp - Initial contribution
 */
public class TvHeadendHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(TvHeadendHandler.class);

    private TvHeadendConfiguration config;
    private @Nullable TvhConnector connector;

    public TvHeadendHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.error("Start handleCommand!");
        if (command instanceof RefreshType && connector != null) {
            if (CHANNEL_SW_VERSION.equals(channelUID.getId())) {
                updateState(channelUID, new StringType(connector.getServerinfo().getSwVersion()));
            } else if (CHANNEL_API_VERSION.equals(channelUID.getId())) {
                updateState(channelUID, new DecimalType(connector.getServerinfo().getApiVersion()));
            }
        }
        //This binding is read only and does not support any other commands!
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        config = getConfigAs(TvHeadendConfiguration.class);
        try {
            //obtain connector
            connector = TvhConnector.getConnector(config);

            // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
            updateStatus(ThingStatus.UNKNOWN);

            // background initialization:
            scheduler.execute(new ConnectionCheck());
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, "Failed to connect to server!");
            logger.error("{}", e.getLocalizedMessage());
        }
        logger.debug("Finished initializing!");
    }

    /**
     * Runnable for Connection Check
     */
    @NonNullByDefault
    private class ConnectionCheck implements Runnable {
        @Override
        public void run() {
            Serverinfo serverinfo = connector.getServerinfo();
            if (serverinfo.getApiVersion() != null) {
                if (serverinfo.getApiVersion() >= 19) {
                    updateStatus(ThingStatus.ONLINE);
                    scheduler.scheduleWithFixedDelay(() -> connector.updateAll(), config.getPollingInterval(), config.getPollingInterval(), TimeUnit.SECONDS);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, "Unknown API version"+serverinfo.getApiVersion()+"!");
                }
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, "Failed to connect to server!");
            }
        }
    }
}
