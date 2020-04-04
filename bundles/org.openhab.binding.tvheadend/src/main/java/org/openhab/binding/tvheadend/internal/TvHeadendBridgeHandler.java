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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.tvheadend.internal.api.TvHeadendApiEndpointHandler;
import org.openhab.binding.tvheadend.internal.api.v19.Serverinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TvHeadendBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christian Schlipp - Initial contribution
 */
public class TvHeadendBridgeHandler extends BaseBridgeHandler implements TvHeadendThingHandlerAnnotationInterface {

    private final Logger logger = LoggerFactory.getLogger(TvHeadendBridgeHandler.class);

    private TvHeadendConfiguration config;
    private HttpClient httpClient;
    private TvHeadendApiEndpointHandler<Serverinfo> serverinfo;

    public ScheduledFuture<?> pollingScheduler;

    public TvHeadendBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    private void setupHttpClient() throws Exception {
        httpClient = new HttpClient();
        AuthenticationStore authStore = httpClient.getAuthenticationStore();
        authStore.addAuthentication(new DigestAuthentication(new URI(config.getIpAddress()), Authentication.ANY_REALM,
                config.getUsername(), config.getPassword()));
        httpClient.start();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.error("Start handleCommand!");
        if (command instanceof RefreshType) {
            try {
                serverinfo.updateChannel(this, channelUID);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("Exception during Channel Refresh {}", e);
            }
        }
        // This binding is read only and does not support any other commands!
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        config = getConfigAs(TvHeadendConfiguration.class);
        try {
            setupHttpClient();
            serverinfo = new TvHeadendApiEndpointHandler<Serverinfo>(httpClient, config, new Serverinfo());

            // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
            updateStatus(ThingStatus.UNKNOWN);

            // background initialization:
            scheduler.execute(new ConnectionCheck());
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, "Failed to connect to server!");
            logger.error("Exception durin initialization {}", e.getLocalizedMessage());
        }
        logger.error("Finished initializing!");
    }

    @Override
    public void dispose() {
        if (pollingScheduler != null && !pollingScheduler.isCancelled()) {
            pollingScheduler.cancel(true);
        }
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.GONE);
    }

    /**
     * Runnable for Connection Check
     */
    @NonNullByDefault
    private class ConnectionCheck implements Runnable {
        @Override
        public void run() {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            try {
                if (serverinfo.checkAvailability()) {
                    logger.error("available");
                    if (serverinfo.getEndpointData().getApiVersion() >= 19) {
                        logger.error("api match");
                        if (pollingScheduler != null && !pollingScheduler.isCancelled()) {
                            pollingScheduler.cancel(false);
                        }
                        pollingScheduler = scheduler.scheduleWithFixedDelay(() -> {
                            serverinfo.update();
                            try {
                                serverinfo.updateAllChannels(TvHeadendBridgeHandler.this);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                logger.error("Exception while updating State {}", e);
                            }
                        }, config.getPollingInterval(), config.getPollingInterval(), TimeUnit.SECONDS);
                        updateStatus(ThingStatus.ONLINE);
                    } else {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                                "API version " + serverinfo.getEndpointData().getApiVersion() + " incompatible!");
                    }
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
                }
            } catch (Exception e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void updateStateExternal(String channelUID, State state) {
        updateState(channelUID, state);
    }
}
