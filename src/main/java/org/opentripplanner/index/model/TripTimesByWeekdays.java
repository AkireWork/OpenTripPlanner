package org.opentripplanner.index.model;

import com.google.common.collect.Lists;

import java.util.List;

public class TripTimesByWeekdays {
    public String weekdays;
    public int arrival;
    public List<TripTimeShort> tripTimeShortList = Lists.newArrayList();

    public TripTimesByWeekdays(String weekday) {
        this.weekdays = weekday;
    }

    public TripTimesByWeekdays(String weekdays, List<TripTimeShort> list) {
        this.weekdays = weekdays;
        this.tripTimeShortList = list;
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdays{" +
                "weekdays='" + weekdays + '\'' +
                ", arrival=" + arrival +
                ", tripTimeShortList=" + tripTimeShortList +
                '}';
    }
}
