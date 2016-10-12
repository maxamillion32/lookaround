package alexparunov.lookaround.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;



public class Event {


    private LatLng coordinates;
    private Time startTime;
    private Time endTime;
    private String title;
    private String description;
    private String tag;
    private FirebaseUser createdBy;

    public Event() {}

    public Event(LatLng coordinates, Time startTime, Time endTime, String title,
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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
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
