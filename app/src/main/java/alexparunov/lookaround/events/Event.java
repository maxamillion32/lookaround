package alexparunov.lookaround.events;

import java.util.Date;

public class Event {

  private Coordinates coordinates;
  private Time startTime;
  private Time endTime;
  private String title;
  private String description;
  private String tag;
  private User createdBy;
  private Date createdAt;

  public Event() {
  }

  public Event(Coordinates coordinates, Time startTime, Time endTime, String title,
               String description, String tag, Date createdAt) {
    this.coordinates = coordinates;
    this.startTime = startTime;
    this.endTime = endTime;
    this.title = title;
    this.description = description;
    this.tag = tag;
    this.createdAt = createdAt;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
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

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return
        "Coordinates:\n" + this.coordinates.toString() + "\n" +
            "Title: " + this.title + "\n" +
            "Description: " + this.description + "\n" +
            "Tag: " + this.tag + "\n" +
            "Starting time: " + this.startTime.toString() + "\n" +
            "Ending time: " + this.endTime.toString() + "\n" +
            "Created at: " + this.createdAt.toString() + "\n" +
            "Created by:\n" + this.createdBy.toString() + "\n";
  }
}
