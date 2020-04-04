
package org.openhab.binding.tvheadend.internal.api.v19.epg.events;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grid {

    @SerializedName("totalCount")
    @Expose
    public Integer totalCount;
    @SerializedName("entries")
    @Expose
    public List<Entry> entries = new ArrayList<Entry>();

}
