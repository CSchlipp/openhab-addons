
package org.openhab.binding.tvheadend.internal.api.v19.dvr.autorec;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grid {

    @SerializedName("entries")
    @Expose
    public List<Entry> entries = new ArrayList<Entry>();
    @SerializedName("total")
    @Expose
    public Integer total;

}
