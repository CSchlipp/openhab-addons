
package org.openhab.binding.tvheadend.internal.api.v19.dvr.entry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subtitle {

    @SerializedName("ger")
    @Expose
    private String ger;

    public String getGer() {
        return ger;
    }

    public void setGer(String ger) {
        this.ger = ger;
    }

}
