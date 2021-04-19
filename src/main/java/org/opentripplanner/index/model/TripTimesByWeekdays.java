package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.Stop;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TripTimesByWeekdays {
    public String weekdays;
    public List<TripTimesByStop> tripTimesByStops = Lists.newArrayList();

    public TripTimesByWeekdays() {}

    public TripTimesByWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public TripTimesByWeekdays(String weekdays, List<TripTimesByStop> list) {
        this.weekdays = weekdays;
        this.tripTimesByStops = list;
    }

    public String computeScore(TripTimeShort tripTime, Stop stop, String weekdays) {
        int sameWeekdays = this.weekdays.equals(weekdays) ? 1 : 0;
        int sameStopName = this.tripTimesByStops.stream()
                .anyMatch(tripTimesByStop -> tripTimesByStop.stopName.equals(stop.getName())) ? 2 : 0;
        int sameStopTime = this.tripTimesByStops.stream()
                .anyMatch(tripTimesByStop -> tripTimesByStop.containsStopTime(tripTime.scheduledDeparture)) ? 4 : 0;
        int occurrenceScore = sameWeekdays + sameStopName + sameStopTime;
        int closestStoptimeDistance = this.tripTimesByStops.stream()
                .min(Comparator.comparingInt(o -> o.closestStopTime(tripTime.scheduledDeparture)))
                .map(tripTimesByStopName -> tripTimesByStopName.closestStopTime(tripTime.scheduledDeparture)).orElse(0);
        return ""+occurrenceScore+":"+closestStoptimeDistance;
    }

    public void addTripTimeByWeekdays(TripTimeShort tripTime, Stop stop, String weekdays) {
        if (this.weekdays.equals(weekdays)) {
            Optional<TripTimesByStop> tripTimesByStop = this.tripTimesByStops.stream()
                    .filter(tripTimesByStop1 -> tripTimesByStop1.stopName.equals(stop.getName()))
                    .findFirst();

            if (tripTimesByStop.isPresent()) {//if is same day and trip time add returns true, we added a new time, otherwise need to add dayName to weekdays
                tripTimesByStop.get().addTripTimeShort(tripTime);
            } else {
                this.tripTimesByStops.add(new TripTimesByStop(stop.getName(), tripTime));
            }
        }
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdays{" +
                "weekdays='" + weekdays + '\'' +
                ", tripTimeShortList=" + tripTimesByStops +
                '}';
    }

    public static class TripTimesByStop {
        public String stopName;
        public List<TripTimeShort> tripTimeShortList = Lists.newArrayList();

        public TripTimesByStop(String stopName, TripTimeShort tripTimeShort) {
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

        public void sort() {
            this.tripTimeShortList.sort(Comparator.comparingInt(o -> o.scheduledDeparture));
        }

        public void addTripTimeShort(TripTimeShort tripTimeShort) {
            Optional<TripTimeShort> first = this.tripTimeShortList.stream()
                    .filter(tripTimeShort1 -> tripTimeShort1.scheduledDeparture == tripTimeShort.scheduledDeparture)
                    .findFirst();
            if (!first.isPresent()) {
                this.tripTimeShortList.add(tripTimeShort);
                if (this.tripTimeShortList.size() > 1) {
                    sort();
                }
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
