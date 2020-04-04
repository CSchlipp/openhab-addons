
package org.openhab.binding.tvheadend.internal.api.v19.memoryinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("size")
    @Expose
    public Integer size;
    @SerializedName("peak_size")
    @Expose
    public Integer peakSize;
    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("peak_count")
    @Expose
    public Integer peakCount;

}
