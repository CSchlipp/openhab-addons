package org.openhab.binding.tvheadend.internal.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.tvheadend.internal.TvHeadendConfiguration;
import org.openhab.binding.tvheadend.internal.TvHeadendThingHandlerAnnotationInterface;
import org.openhab.binding.tvheadend.internal.annotation.TvHeadendStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TvHeadendApiEndpointHandler<T extends TvHeadendApiEndpoint> {

    private final Logger logger = LoggerFactory.getLogger(TvHeadendApiEndpointHandler.class);

    private TvHeadendConfiguration config;
    private String endpointUrl;
    private T defaultEndpointData;
    private Optional<T> endpointData = Optional.empty();
    private HttpClient httpClient;

    public boolean isAvailable;



    public TvHeadendApiEndpointHandler(HttpClient httpClient, TvHeadendConfiguration config, T defaultEndpointData) {
        this.config = config;
        this.defaultEndpointData = defaultEndpointData;
        if (!defaultEndpointData.getEndpointUrl().startsWith("/")) {
            this.endpointUrl = "/" + defaultEndpointData.getEndpointUrl();
        } else {
            this.endpointUrl = defaultEndpointData.getEndpointUrl();
        }
        this.httpClient = httpClient;
    }

    public void update() {
        update(false);
    }

    private void update(boolean force) {
        if (!isAvailable && !force) {
            return;
        }

        String url = config.getIpAddress()+":"+config.getPort() + endpointUrl;

        try {
            String json = httpClient.GET(url).getContentAsString();
            endpointData = (Optional<T>) Optional.of(new Gson().fromJson(json, defaultEndpointData.getClass()));
            logger.error("Received JSON {}", json);
        } catch (InterruptedException | ExecutionException | TimeoutException | JsonSyntaxException e) {
            logger.error("Exception while fetching JSON: {}", e.getLocalizedMessage());
            endpointData = Optional.empty();
        }
    }

    public T getEndpointData() {
        return endpointData.orElse(defaultEndpointData);
    }

    public Runnable getApiEndpointUpdater() {
        return new ApiEndpointUpdater();
    }


    private class ApiEndpointUpdater implements Runnable {

        @Override
        public void run() {
            if (isAvailable) update();
        }
    }

    public boolean checkAvailability() throws Exception {
        isAvailable = false;
        update(true);
        isAvailable = endpointData.isPresent();
        logger.error("{}", isAvailable);
        return isAvailable;
    }

    public void updateChannel(TvHeadendThingHandlerAnnotationInterface thingHandler, ChannelUID channel)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> c = this.getClass();
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(TvHeadendStateHandler.class)) {
                TvHeadendStateHandler annotation = m.getDeclaredAnnotation(TvHeadendStateHandler.class);
                if (annotation.isList() && channel.getId().contains(annotation.channelUID())) {
                    // check for correct method!
                    m.invoke(this, thingHandler, channel);
                } else if (!annotation.isList() && annotation.channelUID().equals(channel.getId())) {
                    thingHandler.updateStateExternal(annotation.channelUID(),
                            (State) m.invoke(this, (Object[]) null));
                }
            } 
        }
    }

    public void updateAllChannels(TvHeadendThingHandlerAnnotationInterface thingHandler)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> c = this.getClass();
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(TvHeadendStateHandler.class)) {
                thingHandler.updateStateExternal(m.getDeclaredAnnotation(TvHeadendStateHandler.class).channelUID(),
                        (State) m.invoke(this, (Object[]) null));
            } 
        }    
    }
    

}
