package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.Stop;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TripTimesByWeekdays {
    public String weekdays;
    public List<TripTimesByStopName> tripTimesByStopNames = Lists.newArrayList();

    public TripTimesByWeekdays(String weekday) {
        this.weekdays = weekday;
    }

    public TripTimesByWeekdays(String weekdays, List<TripTimesByStopName> list) {
        this.weekdays = weekdays;
        this.tripTimesByStopNames = list;
    }

    public String computeScore(TripTimeShort tripTime, Stop stop, String dayName) {
        int sameDay = this.weekdays.contains(dayName) ? 1 : 0;
        int sameStopName = this.tripTimesByStopNames.stream()
                .anyMatch(tripTimesByStopName -> tripTimesByStopName.stopName.equals(stop.getName())) ? 2 : 0;
        int sameStopTime = this.tripTimesByStopNames.stream()
                .anyMatch(tripTimesByStopName -> tripTimesByStopName.containsStopTime(tripTime.scheduledDeparture)) ? 4 : 0;
        int occurrenceScore = sameDay + sameStopName + sameStopTime;
        int closestStoptimeDistance = this.tripTimesByStopNames.stream()
                .min(Comparator.comparingInt(o -> o.closestStopTime(tripTime.scheduledDeparture)))
                .map(tripTimesByStopName -> tripTimesByStopName.closestStopTime(tripTime.scheduledDeparture)).orElse(0);
        return ""+occurrenceScore+":"+closestStoptimeDistance;
    }

    public boolean addTripTimesByStopName(TripTimeShort tripTime, Stop stop, String dayName) {
        Optional<TripTimesByStopName> tripTimesByStopName = this.tripTimesByStopNames.stream()
                .filter(tripTimesByStopName1 -> tripTimesByStopName1.stopName.equals(stop.getName()))
                .findFirst();

        if (tripTimesByStopName.isPresent()) {//if is same day and trip time add returns true, we added a new time, otherwise need to add dayName to weekdays
            return this.weekdays.contains(dayName) && tripTimesByStopName.get().addTripTimeShort(tripTime);
        } else {
            this.tripTimesByStopNames.add(new TripTimesByStopName(stop.getName(), tripTime));
            return true;
        }
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdays{" +
                "weekdays='" + weekdays + '\'' +
                ", tripTimeShortList=" + tripTimesByStopNames +
                '}';
    }

    public static class TripTimesByStopName {
        public String stopName;
        public List<TripTimeShort> tripTimeShortList = Lists.newArrayList();

        public TripTimesByStopName(String stopName, TripTimeShort tripTimeShort) {
            this.stopName = stopName;
            this.tripTimeShortList.add(tripTimeShort);
        }

        public boolean containsStopTime(int departureTime) {
            return this.tripTimeShortList.stream()
                    .anyMatch(tripTimeShort -> tripTimeShort.scheduledDeparture == departureTime);
        }

        public int closestStopTime(int departureTime) {
            int distance = Math.abs(this.tripTimeShortList.get(0).scheduledDeparture - departureTime);
            for(int c = 1; c < this.tripTimeShortList.size(); c++){
                int cdistance = Math.abs(this.tripTimeShortList.get(c).scheduledDeparture - departureTime);
                if(cdistance < distance){
                    distance = cdistance;
                }
            }
            return distance;
        }

        public boolean addTripTimeShort(TripTimeShort tripTimeShort) {
            Optional<TripTimeShort> first = this.tripTimeShortList.stream()
                    .filter(tripTimeShort1 -> tripTimeShort1.scheduledDeparture == tripTimeShort.scheduledDeparture)
                    .findFirst();
            if (!first.isPresent()) {
                this.tripTimeShortList.add(tripTimeShort);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "TripTimeByStopName{" +
                    "stopName='" + stopName + '\'' +
                    ", tripTimeShortList=" + tripTimeShortList +
                    '}';
        }
    }
}
