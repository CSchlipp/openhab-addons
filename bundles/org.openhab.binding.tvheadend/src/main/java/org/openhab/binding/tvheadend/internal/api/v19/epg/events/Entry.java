
package org.openhab.binding.tvheadend.internal.api.v19.epg.events;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("eventId")
    @Expose
    public Integer eventId;
    @SerializedName("episodeUri")
    @Expose
    public String episodeUri;
    @SerializedName("serieslinkUri")
    @Expose
    public String serieslinkUri;
    @SerializedName("channelName")
    @Expose
    public String channelName;
    @SerializedName("channelUuid")
    @Expose
    public String channelUuid;
    @SerializedName("channelNumber")
    @Expose
    public String channelNumber;
    @SerializedName("channelIcon")
    @Expose
    public String channelIcon;
    @SerializedName("start")
    @Expose
    public Integer start;
    @SerializedName("stop")
    @Expose
    public Integer stop;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("subtitle")
    @Expose
    public String subtitle;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("genre")
    @Expose
    public List<Integer> genre = new ArrayList<Integer>();
    @SerializedName("nextEventId")
    @Expose
    public Integer nextEventId;
    @SerializedName("widescreen")
    @Expose
    public Integer widescreen;
    @SerializedName("subtitled")
    @Expose
    public Integer subtitled;

}
