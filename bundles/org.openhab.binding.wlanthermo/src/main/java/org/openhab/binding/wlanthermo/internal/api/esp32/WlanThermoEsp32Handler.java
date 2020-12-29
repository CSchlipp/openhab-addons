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
package org.openhab.binding.wlanthermo.internal.api.esp32;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
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
import org.openhab.binding.wlanthermo.internal.api.esp32.data.Data;
import org.openhab.binding.wlanthermo.internal.api.esp32.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link WlanThermoEsp32Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christian Schlipp - Initial contribution
 */
@NonNullByDefault
public class WlanThermoEsp32Handler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(WlanThermoEsp32Handler.class);

    private WlanThermoExtendedConfiguration config = new WlanThermoExtendedConfiguration();
    private WlanThermoEsp32CommandHandler wlanThermoEsp32CommandHandler = new WlanThermoEsp32CommandHandler();
    private final HttpClient httpClient;
    private @Nullable ScheduledFuture<?> pollingScheduler;
    private final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(WlanThermoBindingConstants.WLANTHERMO_THREAD_POOL);
    private final Gson gson = new Gson();
    private Data data = new Data();
    private Settings settings = new Settings();

    public WlanThermoEsp32Handler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing WlanThermo Esp32!");
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

            logger.debug("Finished initializing WlanThermo Esp32!");
        } catch (URISyntaxException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Failed to initialize WlanThermo Esp32!");
            logger.debug("Failed to initialize WlanThermo Esp32!", e);
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
                        "WlanThermo Esp32 not found under given address.");
            }
        } catch (URISyntaxException | InterruptedException | ExecutionException | TimeoutException e) {
            logger.debug("Failed to connect to WlanThermo Esp32.", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Could not connect to WlanThermo Esp32 at " + config.getIpAddress());
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
            State s = wlanThermoEsp32CommandHandler.getState(channelUID, data, settings);
            if (s != null)
                updateState(channelUID, s);
        } else {
            if (wlanThermoEsp32CommandHandler.setState(channelUID, command, data)) {
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

            // Update Channels if required
            Map<String, String> properties = editProperties();
            Boolean pmEnabled = settings.getFeatures().getBluetooth();
            int pmChannels = pmEnabled ? data.getPitmaster().getPm().size() : 0;
            int tempChannels = data.getChannel().size();

            // Update properties
            properties.put(WlanThermoBindingConstants.PROPERTY_MODEL, settings.getDevice().getDevice());
            properties.put(WlanThermoBindingConstants.PROPERTY_SERIAL, settings.getDevice().getSerial());
            properties.put(WlanThermoBindingConstants.PROPERTY_ESP32_BT_ENABLED,
                    settings.getFeatures().getBluetooth().toString());
            properties.put(WlanThermoBindingConstants.PROPERTY_ESP32_PM_ENABLED, pmEnabled.toString());
            properties.put(WlanThermoBindingConstants.PROPERTY_ESP32_TEMP_CHANNELS, String.valueOf(tempChannels));
            properties.put(WlanThermoBindingConstants.PROPERTY_ESP32_PM_CHANNELS, String.valueOf(pmChannels));
            updateProperties(properties);

            // Update channel state
            for (Channel channel : thing.getChannels()) {
                State state = wlanThermoEsp32CommandHandler.getState(channel.getUID(), data, settings);
                if (state != null) {
                    updateState(channel.getUID(), state);
                } else {
                    // if we could not obtain a state, try trigger instead
                    String trigger = wlanThermoEsp32CommandHandler.getTrigger(channel.getUID(), data);
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
                            "No or wrong login credentials provided. Please configure username/password for write access to WlanThermo Esp32!");
                } else if (status != 200) {
                    updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Failed to update channel "
                            + c.getName() + " on WlanThermo Esp32, Statuscode " + status + " on URI " + uri.toString());
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (InterruptedException | TimeoutException | ExecutionException | URISyntaxException e) {
                updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Failed to update channel " + c.getName() + " on WlanThermo Esp32!");
                logger.debug("Failed to update channel {} on WlanThermo Esp32", c.getName(), e);
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
