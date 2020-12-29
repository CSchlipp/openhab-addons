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
import org.eclipse.smarthome.core.common.ThreadPoolManager;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.wlanthermo.internal.WlanThermoBindingConstants;
import org.openhab.binding.wlanthermo.internal.WlanThermoExtendedConfiguration;
import org.openhab.binding.wlanthermo.internal.api.nano.data.Data;
import org.openhab.binding.wlanthermo.internal.api.nano.settings.Settings;
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
    private WlanThermoNanoV1CommandHandler wlanThermoNanoV1CommandHandler = new WlanThermoNanoV1CommandHandler();
    private final HttpClient httpClient;
    private @Nullable ScheduledFuture<?> pollingScheduler;
    private final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(WlanThermoBindingConstants.WLANTHERMO_THREAD_POOL);
    private final Gson gson = new Gson();
    private Data data = new Data();
    private Settings settings = new Settings();

    public WlanThermoNanoV1Handler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing WlanThermo Nano V1!");
        config = getConfigAs(WlanThermoExtendedConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);
        try {
            if (config.getUsername() != null && !config.getUsername().isEmpty() && config.getPassword() != null
                    && !config.getPassword().isEmpty()) {
                AuthenticationStore authStore = httpClient.getAuthenticationStore();
                authStore.addAuthentication(new DigestAuthentication(config.getUri(), Authentication.ANY_REALM,
                        config.getUsername(), config.getPassword()));
            }
            scheduler.schedule(this::checkConnection, config.getPollingInterval(), TimeUnit.SECONDS);

            logger.debug("Finished initializing WlanThermo Nano V1!");
        } catch (URISyntaxException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Failed to initialize WlanThermo Nano V1!");
            logger.debug("Failed to initialize WlanThermo Nano V1!", e);
        }
    }

    private void checkConnection() {
        try {
            if (httpClient.GET(config.getUri()).getStatus() == 200) {
                updateStatus(ThingStatus.ONLINE);
                ScheduledFuture<?> oldScheduler = pollingScheduler;
                if (oldScheduler != null) {
                    oldScheduler.cancel(false);
                }
                pollingScheduler = scheduler.scheduleWithFixedDelay(this::update, 0, config.getPollingInterval(),
                        TimeUnit.SECONDS);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "WlanThermo Nano V1 not found under given address.");
            }
        } catch (URISyntaxException | InterruptedException | ExecutionException | TimeoutException e) {
            logger.debug("Failed to connect to WlanThermo Nano V1.", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Could not connect to WlanThermo Nano V1 at " + config.getIpAddress());
            ScheduledFuture<?> oldScheduler = pollingScheduler;
            if (oldScheduler != null) {
                oldScheduler.cancel(false);
            }
            pollingScheduler = scheduler.schedule(this::checkConnection, config.getPollingInterval(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            State s = wlanThermoNanoV1CommandHandler.getState(channelUID, data, settings);
            if (s != null)
                updateState(channelUID, s);
        } else {
            if (wlanThermoNanoV1CommandHandler.setState(channelUID, command, data)) {
                logger.debug("Data updated, pushing changes");
                push();
            } else {
                logger.debug("Could not handle command of type {} for channel {}!",
                        command.getClass().toGenericString(), channelUID.getId());
            }
        }
    }

    private void update() {
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
                State state = wlanThermoNanoV1CommandHandler.getState(channel.getUID(), data, settings);
                if (state != null) {
                    updateState(channel.getUID(), state);
                } else {
                    // if we could not obtain a state, try trigger instead
                    String trigger = wlanThermoNanoV1CommandHandler.getTrigger(channel.getUID(), data);
                    if (trigger != null) {
                        triggerChannel(channel.getUID(), trigger);
                    }
                }
            }
        } catch (URISyntaxException | InterruptedException | ExecutionException | TimeoutException e) {
            logger.debug("Update failed, checking connection", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Update failed, reconnecting...");
            ScheduledFuture<?> oldScheduler = pollingScheduler;
            if (oldScheduler != null) {
                oldScheduler.cancel(false);
            }
            for (Channel channel : thing.getChannels()) {
                updateState(channel.getUID(), UnDefType.UNDEF);
            }
            checkConnection();
        }
    }

    private void push() {
        data.getChannel().forEach(c -> {
            try {
                String json = gson.toJson(c);
                logger.debug("Pushing: {}", json);
                URI uri = config.getUri("/setchannels");
                int status = httpClient.POST(uri).content(new StringContentProvider(json), "application/json")
                        .timeout(5, TimeUnit.SECONDS).send().getStatus();
                if (status == 401) {
                    updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "No or wrong login credentials provided. Please configure username/password for write access to WlanThermo Nano V1!");
                } else if (status != 200) {
                    updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Failed to update channel "
                            + c.getName() + " on WlanThermo Nano V1, Statuscode " + status + " on URI " + uri.toString());
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (InterruptedException | TimeoutException | ExecutionException | URISyntaxException e) {
                updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Failed to update channel " + c.getName() + " on WlanThermo Nano V1!");
                logger.debug("Failed to update channel {} on WlanThermo Nano V1", c.getName(), e);
            }
        });
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
