
package org.openhab.binding.tvheadend.internal.api.v19.dvr.autorec;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("enabled")
    @Expose
    public Boolean enabled;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("directory")
    @Expose
    public String directory;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("fulltext")
    @Expose
    public Boolean fulltext;
    @SerializedName("channel")
    @Expose
    public String channel;
    @SerializedName("tag")
    @Expose
    public String tag;
    @SerializedName("btype")
    @Expose
    public Integer btype;
    @SerializedName("content_type")
    @Expose
    public Integer contentType;
    @SerializedName("star_rating")
    @Expose
    public Integer starRating;
    @SerializedName("start")
    @Expose
    public String start;
    @SerializedName("start_window")
    @Expose
    public String startWindow;
    @SerializedName("start_extra")
    @Expose
    public Integer startExtra;
    @SerializedName("stop_extra")
    @Expose
    public Integer stopExtra;
    @SerializedName("weekdays")
    @Expose
    public List<Integer> weekdays = new ArrayList<Integer>();
    @SerializedName("minduration")
    @Expose
    public Integer minduration;
    @SerializedName("maxduration")
    @Expose
    public Integer maxduration;
    @SerializedName("minyear")
    @Expose
    public Integer minyear;
    @SerializedName("maxyear")
    @Expose
    public Integer maxyear;
    @SerializedName("minseason")
    @Expose
    public Integer minseason;
    @SerializedName("maxseason")
    @Expose
    public Integer maxseason;
    @SerializedName("pri")
    @Expose
    public Integer pri;
    @SerializedName("record")
    @Expose
    public Integer record;
    @SerializedName("retention")
    @Expose
    public Integer retention;
    @SerializedName("removal")
    @Expose
    public Integer removal;
    @SerializedName("maxcount")
    @Expose
    public Integer maxcount;
    @SerializedName("maxsched")
    @Expose
    public Integer maxsched;
    @SerializedName("config_name")
    @Expose
    public String configName;
    @SerializedName("owner")
    @Expose
    public String owner;
    @SerializedName("creator")
    @Expose
    public String creator;

}
