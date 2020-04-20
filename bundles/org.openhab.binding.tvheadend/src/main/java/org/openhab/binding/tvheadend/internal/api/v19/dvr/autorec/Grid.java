
package org.openhab.binding.tvheadend.internal.api.v19.dvr.autorec;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openhab.binding.tvheadend.internal.TvHeadendBindingConstants;
import org.openhab.binding.tvheadend.internal.annotation.TvHeadendStateHandler;
import org.openhab.binding.tvheadend.internal.api.TvHeadendApiEndpoint;

public class Grid extends TvHeadendApiEndpoint{

    private static final String ENDPOINT_URL = "/api/dvr/autorec";

    @SerializedName("entries")
    @Expose
    public List<Entry> entries = new ArrayList<Entry>();
    @SerializedName("total")
    @Expose
    public Integer total;

    @Override
    public String getEndpointUrl() {
        return ENDPOINT_URL;
    }

    @TvHeadendStateHandler(channelUID = TvHeadendBindingConstants.CHANNEL_DVR_AUTOREC, isList = true)
    public void getEntriesConverted() {
        
    }

    public List<Entry> getEntries() {
        return entries;
    }

}
