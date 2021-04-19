package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.Stop;

import java.util.Comparator;
import java.util.List;

public class TripTimesByStopName {
    public String stopName;
    public List<WeekdaysTrip> weekdaysTrips = Lists.newArrayList();

    public TripTimesByStopName() {}

    public TripTimesByStopName(String stopName) {
        this.stopName = stopName;
    }

    public TripTimesByStopName(String stopName, TripTimeShort tripTimeShort, String weekdays) {
        this.stopName = stopName;
        this.weekdaysTrips.add(new WeekdaysTrip(weekdays, tripTimeShort));
    }

    public String computeScore(TripTimeShort tripTime, Stop stop, String weekdays) {
        int sameStopName = this.stopName.equals(stop.getName()) ? 1 : 0;
        int sameWeekdays = this.weekdaysTrips.stream()
                .anyMatch(tripTimesByStop -> tripTimesByStop.weekdays.equals(weekdays)) ? 2 : 0;
        int sameStopTime = this.weekdaysTrips.stream()
                .anyMatch(tripTimesByStop -> tripTimesByStop.containsStopTime(tripTime.scheduledDeparture)) ? 4 : 0;
        int occurrenceScore = sameWeekdays + sameStopName + sameStopTime;
        int closestStoptimeDistance = this.weekdaysTrips.stream()
                .min(Comparator.comparingInt(o -> o.stopTimeDistance(tripTime.scheduledDeparture)))
                .map(tripTimesByStopName -> tripTimesByStopName.stopTimeDistance(tripTime.scheduledDeparture)).orElse(0);
        return ""+occurrenceScore+":"+closestStoptimeDistance;
    }

    public void addWeekdaysTrip(TripTimeShort tripTimeShort, Stop stop, String weekdays) {
        if (this.stopName.equals(stop.getName())) {
            this.weekdaysTrips.add(new WeekdaysTrip(weekdays, tripTimeShort));
        }
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdays{" +
                "stopName='" + stopName + '\'' +
                ", weekdaysTrips=" + weekdaysTrips +
                '}';
    }

    public static class WeekdaysTrip {
        public String weekdays;
        public TripTimeShort tripTimeShort;

        public WeekdaysTrip(String weekdays, TripTimeShort tripTimeShort) {
            this.weekdays = weekdays;
            this.tripTimeShort = tripTimeShort;
        }

        public boolean containsStopTime(int departureTime) {
            return this.tripTimeShort.scheduledDeparture == departureTime;
        }

        public int stopTimeDistance(int departureTime) {
            return Math.abs(this.tripTimeShort.scheduledDeparture - departureTime);
        }

        @Override
        public String toString() {
            return "TripTimeByStopName{" +
                    "stopName='" + weekdays + '\'' +
                    ", tripTimeShort=" + tripTimeShort +
                    '}';
        }
    }
}
