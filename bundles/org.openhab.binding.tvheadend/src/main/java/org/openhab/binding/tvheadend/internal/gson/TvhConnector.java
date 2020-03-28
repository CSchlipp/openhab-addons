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
package org.openhab.binding.tvheadend.internal.gson;

import com.google.gson.Gson;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.openhab.binding.tvheadend.internal.TvHeadendConfiguration;
import org.openhab.binding.tvheadend.internal.TvHeadendHandler;
import org.openhab.binding.tvheadend.internal.gson.api19.Serverinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Connector for TVH API
 *
 * @author Christian Schlipp - Initial contribution
 */

public class TvhConnector {

    private static @Nullable TvhConnector connector = null;
    private final TvHeadendConfiguration config;
    private Optional<Serverinfo> serverinfo = Optional.empty();
    private final Logger logger = LoggerFactory.getLogger(TvhConnector.class);
    private static @Nullable HttpClient httpClient;

    private TvhConnector (TvHeadendConfiguration config) {
        this.config = config;
    }

    public static TvhConnector getConnector(TvHeadendConfiguration config) throws Exception {
        if (connector == null) {
            connector = new TvhConnector(config);
            setupHttpClient(config);
        }
        return connector;
    }

    private static void setupHttpClient(TvHeadendConfiguration config) throws Exception {
        httpClient = new HttpClient();
        AuthenticationStore authStore = httpClient.getAuthenticationStore();
        authStore.addAuthentication(new DigestAuthentication(new URI(config.getIpAddress()), Authentication.ANY_REALM, config.getUsername(), config.getPassword()));
        httpClient.start();
    }

    public void updateAll() {
        logger.debug("Updating TVH...");
        serverinfo = updateServerinfo();
    }

    private Optional<Serverinfo> updateServerinfo() {
        String url = config.getIpAddress()+":"+config.getPort()+"/api/serverinfo";

        try {
            String json = httpClient.GET(url).getContentAsString();
            logger.debug("Received JSON {}", json);
            return Optional.of(new Gson().fromJson(json, Serverinfo.class));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Exception while fetching JSON: {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    public Serverinfo getServerinfo() {
        //Check if present, otherwise update
        if (!serverinfo.isPresent()) {
            serverinfo = updateServerinfo();
        }
        // return result or empty Object
        return serverinfo.orElse(new Serverinfo());
    }


}
