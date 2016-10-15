package alexparunov.lookaround.events;

import java.util.Date;

public class Event {

  private Coordinates coordinates;
  private DateTime startDateTime;
  private DateTime endDateTime;
  private String title;
  private String description;
  private String tag;
  private User createdBy;
  private Date createdAt;

  public Event() {
  }

  public Event(Coordinates coordinates, DateTime startDateTime, DateTime endDateTime, String title,
               String description, String tag, Date createdAt) {
    this.coordinates = coordinates;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
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

  public DateTime getStartTime() {
    return startDateTime;
  }

  public void setStartTime(DateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public DateTime getEndTime() {
    return endDateTime;
  }

  public void setEndTime(DateTime endDateTime) {
    this.endDateTime = endDateTime;
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
            "Starting time: " + this.startDateTime.toString() + "\n" +
            "Ending time: " + this.endDateTime.toString() + "\n" +
            "Created at: " + this.createdAt.toString() + "\n" +
            "Created by:\n" + this.createdBy.toString() + "\n";
  }
}
