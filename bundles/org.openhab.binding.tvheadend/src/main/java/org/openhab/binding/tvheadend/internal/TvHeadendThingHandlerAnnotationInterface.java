package org.openhab.binding.tvheadend.internal;

import org.eclipse.smarthome.core.types.State;

public interface TvHeadendThingHandlerAnnotationInterface {
    public void updateStateExternal(String channelUID, State state);
}

