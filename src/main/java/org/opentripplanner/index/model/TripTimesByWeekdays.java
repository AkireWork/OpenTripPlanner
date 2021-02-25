package org.opentripplanner.index.model;

import com.google.common.collect.Lists;

import java.util.List;

public class TripTimesByWeekdays {
    public String weekdays;
    public List<TripTimeShort> tripTimeShortList = Lists.newArrayList();

    public TripTimesByWeekdays(String weekday) {
        this.weekdays = weekday;
    }
}
