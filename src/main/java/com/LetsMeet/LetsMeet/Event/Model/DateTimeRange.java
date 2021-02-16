package com.LetsMeet.LetsMeet.Event.Model;
import java.io.Serializable;
import java.util.Date;

public class DateTimeRange implements Serializable{
    
    private Date start;
    private Date end;


    public DateTimeRange(Date start, Date end){
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "DateTimeRange [end=" + end + ", start=" + start + "]";
    }

    
}
