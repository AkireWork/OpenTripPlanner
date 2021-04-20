package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.Stop;

import java.util.*;

public class TripTimesByWeekdays {
    public String weekdays;
    public List<TripTimeByStopName> tripTimeByStopNameList = Lists.newArrayList();

    public TripTimesByWeekdays() {}

    public TripTimesByWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public void addTripTimeByWeekdays(TripTimeShort tripTimeShort, Stop stop, String weekdays) {
        if (this.weekdays.equals(weekdays)) {
            if (this.tripTimeByStopNameList.stream().noneMatch(tripTimeByStop1 -> tripTimeByStop1.stopName.equals(stop.getName()))) {//if is same day and trip time add returns true, we added a new time, otherwise need to add dayName to weekdays
                this.tripTimeByStopNameList.add(new TripTimeByStopName(stop.getName(), tripTimeShort));
            }
        }
    }

    @Override
    public String toString() {
        return "TripTimeByWeekdays{" +
                "weekdays='" + weekdays + '\'' +
                ", tripTimeByStopNameList=" + tripTimeByStopNameList +
                '}';
    }

    public static class TripTimeByStopName {
        public String stopName;
        public TripTimeShort tripTimeShort;

        public TripTimeByStopName(String stopName, TripTimeShort tripTimeShort) {
            this.stopName = stopName;
            this.tripTimeShort = tripTimeShort;
        }

        public boolean containsStopTime(int departureTime) {
            return this.tripTimeShort.scheduledDeparture == departureTime;
        }

        public int closestStopTime(int departureTime) {
            return Math.abs(this.tripTimeShort.scheduledDeparture - departureTime);
        }

        @Override
        public String toString() {
            return "TripTimeByStopName{" +
                    "stopName='" + stopName + '\'' +
                    ", tripTimeShortList=" + tripTimeShort +
                    '}';
        }
    }
}
