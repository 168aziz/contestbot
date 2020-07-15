package model;

import java.time.ZonedDateTime;

public class Contest {

    private String name;
    private ContestList event;
    private String url;
    private ZonedDateTime end;
    private ZonedDateTime start;
    private boolean in_24_hours;

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public ContestList getEvent() {
        return event;
    }

    public void setEvent(ContestList event) {
        this.event = event;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIn_24_hours() {
        return in_24_hours;
    }

    public void setIn_24_hours(boolean in_24_hours) {
        this.in_24_hours = in_24_hours;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return name + " " + event + " " + url;
    }
}
