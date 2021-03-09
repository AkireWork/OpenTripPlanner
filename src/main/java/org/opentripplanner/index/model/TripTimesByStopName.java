package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.Stop;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TripTimesByStopName {
    public String stopName;
    public List<TripTimesByDay> tripTimesByDays = Lists.newArrayList();

    public TripTimesByStopName() {}

    public TripTimesByStopName(String stopName) {
        this.stopName = stopName;
    }

    public TripTimesByStopName(String stopName, List<TripTimesByDay> list) {
        this.stopName = stopName;
        this.tripTimesByDays = list;
    }

    public String computeScore(TripTimeShort tripTime, Stop stop, String dayName) {
        int sameStopName = this.stopName.equals(stop.getName()) ? 1 : 0;
        int sameDay = this.tripTimesByDays.stream()
                .anyMatch(tripTimesByDay -> tripTimesByDay.dayName.equals(dayName)) ? 2 : 0;
        int sameStopTime = this.tripTimesByDays.stream()
                .anyMatch(tripTimesByStopName -> tripTimesByStopName.containsStopTime(tripTime.scheduledDeparture)) ? 4 : 0;
        int occurrenceScore = sameDay + sameStopName + sameStopTime;
        int closestStoptimeDistance = this.tripTimesByDays.stream()
                .min(Comparator.comparingInt(o -> o.closestStopTime(tripTime.scheduledDeparture)))
                .map(tripTimesByStopName -> tripTimesByStopName.closestStopTime(tripTime.scheduledDeparture)).orElse(0);
        return ""+occurrenceScore+":"+closestStoptimeDistance;
    }

    public void addTripTimesByDay(TripTimeShort tripTime, Stop stop, String dayName) {
        Optional<TripTimesByDay> tripTimesByDay = this.tripTimesByDays.stream()
                .filter(tripTimesByDay1 -> tripTimesByDay1.dayName.equals(dayName))
                .findFirst();

        if (tripTimesByDay.isPresent()) {//if is same day and trip time add returns true, we added a new time, otherwise need to add dayName to weekdays
            tripTimesByDay.get().addTripTimeShort(tripTime);
        } else {
            this.tripTimesByDays.add(new TripTimesByDay(dayName, tripTime));
        }
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdays{" +
                "weekdays='" + stopName + '\'' +
                ", tripTimeShortList=" + tripTimesByDays +
                '}';
    }

    public static class TripTimesByDay {
        public String dayName;
        public List<TripTimeShort> tripTimeShortList = Lists.newArrayList();

        public TripTimesByDay(String dayName, TripTimeShort tripTimeShort) {
            this.dayName = dayName;
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

        public void addTripTimeShort(TripTimeShort tripTimeShort) {
            Optional<TripTimeShort> first = this.tripTimeShortList.stream()
                    .filter(tripTimeShort1 -> tripTimeShort1.scheduledDeparture == tripTimeShort.scheduledDeparture)
                    .findFirst();
            if (!first.isPresent()) {
                this.tripTimeShortList.add(tripTimeShort);
            }
        }

        @Override
        public String toString() {
            return "TripTimeByStopName{" +
                    "stopName='" + dayName + '\'' +
                    ", tripTimeShortList=" + tripTimeShortList +
                    '}';
        }
    }
}
