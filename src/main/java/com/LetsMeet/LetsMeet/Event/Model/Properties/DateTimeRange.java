package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class DateTimeRange implements Serializable, Comparable<DateTimeRange> {


    private static final long serialVersionUID = 1685466673494860983L;

    private ZonedDateTime start;
    private ZonedDateTime end;

    public DateTimeRange(ZonedDateTime start, ZonedDateTime end){
        this.start = start;
        this.end = end;
    }

    public DateTimeRange(LocalDateTime start, LocalDateTime end, ZoneId zone){
        this(ZonedDateTime.of(start, zone), ZonedDateTime.of(end, zone));
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateTimeRange other = (DateTimeRange) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DateTimeRange [end=" + end + ", start=" + start + "]";
    }

    public Period getPeriod(){
        return Period.between(this.start.toLocalDate(), this.end.toLocalDate());
    }

    public Duration getDuration(){
        return Duration.between(this.start, this.end);
    }

    @Override
    public int compareTo(DateTimeRange o) {
        // TODO Auto-generated method stub
        return 0;
    }
    

    

    
}
