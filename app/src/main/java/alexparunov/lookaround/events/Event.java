package alexparunov.lookaround.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Event {
    private LatLng coordinates;
    private Date startTime;
    private Date endTime;
    private String title;
    private String description;
    private String tag;
    private FirebaseUser createdBy;

    public Event() {}

    public Event(LatLng coordinates, Date startTime, Date endTime, String title,
                 String description, String tag, FirebaseUser createdBy) {
        this.coordinates = coordinates;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.createdBy = createdBy;
    }
    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public FirebaseUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(FirebaseUser createdBy) {
        this.createdBy = createdBy;
    }
}
