package alexparunov.lookaround.events;


import java.util.Calendar;

public class DateTime {
  private int hours;
  private int minutes;
  private int date;
  private int month;

  public DateTime() {
    this.setHours(0);
    this.setMinutes(0);
    this.setDate(0);
    this.setMonth(0);
  }

  public DateTime(int month, int date, int hours, int minutes) {
    this.setHours(hours);
    this.setMinutes(minutes);
    this.setDate(date);
    this.setMonth(month);
  }


  public int getHours() {
    return hours;
  }

  public void setHours(int hours) {
    this.hours = hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public boolean isBefore(DateTime time) {
    Calendar start = Calendar.getInstance();
    start.set(Calendar.YEAR,this.getMonth(),this.getDate(),this.getHours(),this.getMinutes());
    Calendar end = Calendar.getInstance();
    end.set(Calendar.YEAR,time.getMonth(),time.getDate(),time.getHours(),time.getMinutes());

    return start.before(end);
  }

  public int getDate() {
    return date;
  }

  public void setDate(int date) {
    this.date = date;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  @Override
  public String toString() {
    return theMonth(this.month)+" "+this.date+" "+this.hours + ":" + this.minutes;
  }

  private String theMonth(int month){
    String[] monthNames = {"January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"};
    return monthNames[month];
  }
}