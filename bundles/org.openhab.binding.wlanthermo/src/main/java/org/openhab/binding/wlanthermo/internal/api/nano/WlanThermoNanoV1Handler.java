/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.wlanthermo.internal.api.nano;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.openhab.binding.wlanthermo.internal.WlanThermoException;
import org.openhab.binding.wlanthermo.internal.WlanThermoExtendedConfiguration;
import org.openhab.binding.wlanthermo.internal.WlanThermoUnknownChannelException;
import org.openhab.binding.wlanthermo.internal.api.nano.dto.data.Data;
import org.openhab.binding.wlanthermo.internal.api.nano.dto.settings.Settings;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link WlanThermoNanoV1Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christian Schlipp - Initial contribution
 */
@NonNullByDefault
public class WlanThermoNanoV1Handler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(WlanThermoNanoV1Handler.class);

    private WlanThermoExtendedConfiguration config = new WlanThermoExtendedConfiguration();
    private final HttpClient httpClient;
    private @Nullable ScheduledFuture<?> pollingScheduler;
    private final Gson gson = new Gson();
    private @Nullable Data data = new Data();
    private @Nullable Settings settings = new Settings();

    public WlanThermoNanoV1Handler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        config = getConfigAs(WlanThermoExtendedConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);
        try {
            if (!config.getUsername().isEmpty() && !config.getPassword().isEmpty()) {
                AuthenticationStore authStore = httpClient.getAuthenticationStore();
                authStore.addAuthentication(new DigestAuthentication(config.getUri(), Authentication.ANY_REALM,
                        config.getUsername(), config.getPassword()));
            }
            pollingScheduler = scheduler.schedule(this::checkConnectionAndUpdate, config.getPollingInterval(),
                    TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Failed to initialize WlanThermo Nano: " + e.getMessage());
        }
    }

    private void checkConnectionAndUpdate() {
        if (this.thing.getStatus() == ThingStatus.OFFLINE) {
            try {
                if (httpClient.GET(config.getUri()).getStatus() == 200) {
                    updateStatus(ThingStatus.ONLINE);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                            "WlanThermo not found under given address.");
                }
            } catch (URISyntaxException | ExecutionException | TimeoutException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Could not connect to WlanThermo at " + config.getIpAddress() + ": " + e.getMessage());
            } catch (InterruptedException e) {
                logger.debug("Connection check interrupted. {}", e.getMessage());
            }
        } else {
            try {
                // Update objects with data from device
                String json = httpClient.GET(config.getUri("/data")).getContentAsString();
                data = gson.fromJson(json, Data.class);
                logger.debug("Received at /data: {}", json);
                json = httpClient.GET(config.getUri("/settings")).getContentAsString();
                settings = gson.fromJson(json, Settings.class);
                logger.debug("Received at /settings: {}", json);

                // Update channels
                for (Channel channel : thing.getChannels()) {
                    try {
                        State state = WlanThermoNanoV1CommandHandler.getState(channel.getUID(), data, settings);
                        updateState(channel.getUID(), state);
                    } catch (WlanThermoUnknownChannelException e) {
                        // if we could not obtain a state, try trigger instead
                        String trigger = WlanThermoNanoV1CommandHandler.getTrigger(channel.getUID(), data);
                        triggerChannel(channel.getUID(), trigger);
                    }
                }
            } catch (URISyntaxException | ExecutionException | TimeoutException | WlanThermoException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Update failed: " + e.getMessage());
                for (Channel channel : thing.getChannels()) {
                    updateState(channel.getUID(), UnDefType.UNDEF);
                }
            } catch (InterruptedException e) {
                logger.debug("Update interrupted. {}", e.getMessage());
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            try {
                State s = WlanThermoNanoV1CommandHandler.getState(channelUID, data, settings);
                updateState(channelUID, s);
            } catch (WlanThermoException e) {
                logger.debug("Could not handle command of type {} for channel {}!",
                        command.getClass().toGenericString(), channelUID.getId());
            }
        } else {
            if (WlanThermoNanoV1CommandHandler.setState(channelUID, command, data)) {
                logger.debug("Data updated, pushing changes");
                scheduler.execute(this::push);
            } else {
                logger.debug("Could not handle command of type {} for channel {}!",
                        command.getClass().toGenericString(), channelUID.getId());
            }
        }
    }

    private void push() {
        if (data == null) {
            return;
        }
        for (org.openhab.binding.wlanthermo.internal.api.nano.dto.data.Channel c : data.getChannel()) {
            try {
                String json = gson.toJson(c);
                URI uri = config.getUri("/setchannels");
                int status = httpClient.POST(uri).content(new StringContentProvider(json), "application/json")
                        .timeout(5, TimeUnit.SECONDS).send().getStatus();
                if (status == 401) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "No or wrong login credentials provided. Please configure username/password for write access to WlanThermo!");
                    break;
                } else if (status != 200) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Failed to update channel "
                            + c.getName() + " on device, Statuscode " + status + " on URI " + uri.toString());
                    logger.debug("Payload sent: {}", json);
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (TimeoutException | ExecutionException | URISyntaxException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Failed to update channel " + c.getName() + " on device: " + e.getMessage());
            } catch (InterruptedException e) {
                logger.debug("Push interrupted. {}", e.getMessage());
            }
        }
    }

    @Override
    public void dispose() {
        ScheduledFuture<?> oldScheduler = pollingScheduler;
        if (oldScheduler != null) {
            boolean stopped = oldScheduler.cancel(true);
            logger.debug("Stopped polling: {}", stopped);
        }
        pollingScheduler = null;
    }
}
