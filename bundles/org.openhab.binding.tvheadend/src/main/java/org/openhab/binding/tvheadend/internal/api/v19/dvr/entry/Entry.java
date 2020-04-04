
package org.openhab.binding.tvheadend.internal.api.v19.dvr.entry;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("create")
    @Expose
    private Integer create;
    @SerializedName("watched")
    @Expose
    private Integer watched;
    @SerializedName("start")
    @Expose
    private Integer start;
    @SerializedName("start_extra")
    @Expose
    private Integer startExtra;
    @SerializedName("start_real")
    @Expose
    private Integer startReal;
    @SerializedName("stop")
    @Expose
    private Integer stop;
    @SerializedName("stop_extra")
    @Expose
    private Integer stopExtra;
    @SerializedName("stop_real")
    @Expose
    private Integer stopReal;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("channel_icon")
    @Expose
    private String channelIcon;
    @SerializedName("channelname")
    @Expose
    private String channelname;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("fanart_image")
    @Expose
    private String fanartImage;
    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("disp_title")
    @Expose
    private String dispTitle;
    @SerializedName("subtitle")
    @Expose
    private Subtitle subtitle;
    @SerializedName("disp_subtitle")
    @Expose
    private String dispSubtitle;
    @SerializedName("disp_summary")
    @Expose
    private String dispSummary;
    @SerializedName("description")
    @Expose
    private Description description;
    @SerializedName("disp_description")
    @Expose
    private String dispDescription;
    @SerializedName("disp_extratext")
    @Expose
    private String dispExtratext;
    @SerializedName("pri")
    @Expose
    private Integer pri;
    @SerializedName("retention")
    @Expose
    private Integer retention;
    @SerializedName("removal")
    @Expose
    private Integer removal;
    @SerializedName("playposition")
    @Expose
    private Integer playposition;
    @SerializedName("playcount")
    @Expose
    private Integer playcount;
    @SerializedName("config_name")
    @Expose
    private String configName;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("directory")
    @Expose
    private String directory;
    @SerializedName("errorcode")
    @Expose
    private Integer errorcode;
    @SerializedName("errors")
    @Expose
    private Integer errors;
    @SerializedName("data_errors")
    @Expose
    private Integer dataErrors;
    @SerializedName("dvb_eid")
    @Expose
    private Integer dvbEid;
    @SerializedName("noresched")
    @Expose
    private Boolean noresched;
    @SerializedName("norerecord")
    @Expose
    private Boolean norerecord;
    @SerializedName("fileremoved")
    @Expose
    private Integer fileremoved;
    @SerializedName("autorec")
    @Expose
    private String autorec;
    @SerializedName("autorec_caption")
    @Expose
    private String autorecCaption;
    @SerializedName("timerec")
    @Expose
    private String timerec;
    @SerializedName("timerec_caption")
    @Expose
    private String timerecCaption;
    @SerializedName("parent")
    @Expose
    private String parent;
    @SerializedName("child")
    @Expose
    private String child;
    @SerializedName("content_type")
    @Expose
    private Integer contentType;
    @SerializedName("copyright_year")
    @Expose
    private Integer copyrightYear;
    @SerializedName("broadcast")
    @Expose
    private Integer broadcast;
    @SerializedName("episode_disp")
    @Expose
    private String episodeDisp;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("filesize")
    @Expose
    private Integer filesize;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("sched_status")
    @Expose
    private String schedStatus;
    @SerializedName("duplicate")
    @Expose
    private Integer duplicate;
    @SerializedName("first_aired")
    @Expose
    private Integer firstAired;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("category")
    @Expose
    private List<Object> category = null;
    @SerializedName("credits")
    @Expose
    private Credits credits;
    @SerializedName("keyword")
    @Expose
    private List<Object> keyword = null;
    @SerializedName("genre")
    @Expose
    private List<Integer> genre = null;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getCreate() {
        return create;
    }

    public void setCreate(Integer create) {
        this.create = create;
    }

    public Integer getWatched() {
        return watched;
    }

    public void setWatched(Integer watched) {
        this.watched = watched;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getStartExtra() {
        return startExtra;
    }

    public void setStartExtra(Integer startExtra) {
        this.startExtra = startExtra;
    }

    public Integer getStartReal() {
        return startReal;
    }

    public void setStartReal(Integer startReal) {
        this.startReal = startReal;
    }

    public Integer getStop() {
        return stop;
    }

    public void setStop(Integer stop) {
        this.stop = stop;
    }

    public Integer getStopExtra() {
        return stopExtra;
    }

    public void setStopExtra(Integer stopExtra) {
        this.stopExtra = stopExtra;
    }

    public Integer getStopReal() {
        return stopReal;
    }

    public void setStopReal(Integer stopReal) {
        this.stopReal = stopReal;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFanartImage() {
        return fanartImage;
    }

    public void setFanartImage(String fanartImage) {
        this.fanartImage = fanartImage;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getDispTitle() {
        return dispTitle;
    }

    public void setDispTitle(String dispTitle) {
        this.dispTitle = dispTitle;
    }

    public Subtitle getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    public String getDispSubtitle() {
        return dispSubtitle;
    }

    public void setDispSubtitle(String dispSubtitle) {
        this.dispSubtitle = dispSubtitle;
    }

    public String getDispSummary() {
        return dispSummary;
    }

    public void setDispSummary(String dispSummary) {
        this.dispSummary = dispSummary;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getDispDescription() {
        return dispDescription;
    }

    public void setDispDescription(String dispDescription) {
        this.dispDescription = dispDescription;
    }

    public String getDispExtratext() {
        return dispExtratext;
    }

    public void setDispExtratext(String dispExtratext) {
        this.dispExtratext = dispExtratext;
    }

    public Integer getPri() {
        return pri;
    }

    public void setPri(Integer pri) {
        this.pri = pri;
    }

    public Integer getRetention() {
        return retention;
    }

    public void setRetention(Integer retention) {
        this.retention = retention;
    }

    public Integer getRemoval() {
        return removal;
    }

    public void setRemoval(Integer removal) {
        this.removal = removal;
    }

    public Integer getPlayposition() {
        return playposition;
    }

    public void setPlayposition(Integer playposition) {
        this.playposition = playposition;
    }

    public Integer getPlaycount() {
        return playcount;
    }

    public void setPlaycount(Integer playcount) {
        this.playcount = playcount;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public Integer getDataErrors() {
        return dataErrors;
    }

    public void setDataErrors(Integer dataErrors) {
        this.dataErrors = dataErrors;
    }

    public Integer getDvbEid() {
        return dvbEid;
    }

    public void setDvbEid(Integer dvbEid) {
        this.dvbEid = dvbEid;
    }

    public Boolean getNoresched() {
        return noresched;
    }

    public void setNoresched(Boolean noresched) {
        this.noresched = noresched;
    }

    public Boolean getNorerecord() {
        return norerecord;
    }

    public void setNorerecord(Boolean norerecord) {
        this.norerecord = norerecord;
    }

    public Integer getFileremoved() {
        return fileremoved;
    }

    public void setFileremoved(Integer fileremoved) {
        this.fileremoved = fileremoved;
    }

    public String getAutorec() {
        return autorec;
    }

    public void setAutorec(String autorec) {
        this.autorec = autorec;
    }

    public String getAutorecCaption() {
        return autorecCaption;
    }

    public void setAutorecCaption(String autorecCaption) {
        this.autorecCaption = autorecCaption;
    }

    public String getTimerec() {
        return timerec;
    }

    public void setTimerec(String timerec) {
        this.timerec = timerec;
    }

    public String getTimerecCaption() {
        return timerecCaption;
    }

    public void setTimerecCaption(String timerecCaption) {
        this.timerecCaption = timerecCaption;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getCopyrightYear() {
        return copyrightYear;
    }

    public void setCopyrightYear(Integer copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public Integer getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Integer broadcast) {
        this.broadcast = broadcast;
    }

    public String getEpisodeDisp() {
        return episodeDisp;
    }

    public void setEpisodeDisp(String episodeDisp) {
        this.episodeDisp = episodeDisp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchedStatus() {
        return schedStatus;
    }

    public void setSchedStatus(String schedStatus) {
        this.schedStatus = schedStatus;
    }

    public Integer getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Integer duplicate) {
        this.duplicate = duplicate;
    }

    public Integer getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(Integer firstAired) {
        this.firstAired = firstAired;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Object> getCategory() {
        return category;
    }

    public void setCategory(List<Object> category) {
        this.category = category;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public List<Object> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<Object> keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getGenre() {
        return genre;
    }

    public void setGenre(List<Integer> genre) {
        this.genre = genre;
    }

}
