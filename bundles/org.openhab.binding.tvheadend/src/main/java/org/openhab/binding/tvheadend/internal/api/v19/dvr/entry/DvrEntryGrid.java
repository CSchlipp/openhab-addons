
package org.openhab.binding.tvheadend.internal.api.v19.dvr.entry;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DvrEntryGrid {

    @SerializedName("entries")
    @Expose
    private List<Entry> entries = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
