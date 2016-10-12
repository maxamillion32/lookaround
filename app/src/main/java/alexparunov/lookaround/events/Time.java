package alexparunov.lookaround.events;


public class Time {
    private int hours;
    private int minutes;

    public Time() {
        this.setHours(0);
        this.setMinutes(0);
    }

    public Time(int hours, int minutes) {
        this.setHours(hours);
        this.setMinutes(minutes);
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

    public boolean isBefore(Time time) {
        int totalMinutes1 = this.hours*60 + this.minutes;
        int totalMinutes2 = time.hours*60 + time.minutes;

        return totalMinutes1 < totalMinutes2;
    }

    @Override
    public String toString() {
        return this.hours + ":" + this.minutes;
    }
}