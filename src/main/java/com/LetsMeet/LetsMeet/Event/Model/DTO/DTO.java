package com.LetsMeet.LetsMeet.Event.Model.DTO;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DTO {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss a";
    public static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    public static class DateTimeData {
        public String json;
        public String startDate;
        public String startTime;
        public String endDate;
        public String endTime;

        DateTimeData(DateTimeRange dateTimeRange){
            this.json = dateTimeRange.toJson();
            this.startDate = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.startTime = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.endDate = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.endDate = LDT_FORMATTER.format(dateTimeRange.getStart());
        }
    }

    public static class PollData {
        public String json;
        public String name;
        public Map<String, Integer> options;

        PollData(Poll poll){
            this.json = poll.toJson();
            this.name = poll.getName();
            this.options = poll.getOptions();
        }
    }
}
