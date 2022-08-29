package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.ServiceCalendar;
import org.opentripplanner.model.ServiceCalendarDate;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.trippattern.TripTimes;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TripTimesByWeekdaysParts {
    public static final int MAX_PART_SIZE = 36;

    public String identifier;
    public String weekdays;
    public int parts;
    public List<TripTimesByWeekdays> tripTimesByWeekdaysList = Lists.newArrayList();
    public CalendarDatesByFirstStoptime calendarDatesByFirstStoptime;
    public List<FeedScopedId> serviceIds = Lists.newArrayList();
    public String validFrom;
    public String validTill;

    public TripTimesByWeekdaysParts(String weekdaysGroup, int firstStoptime, List<ServiceCalendarDate> serviceCalendarDates, String identifier, FeedScopedId serviceId, ServiceDate validFrom, ServiceDate validTill) {
        this.parts = 1;
        this.weekdays = weekdaysGroup;
        this.tripTimesByWeekdaysList.add(new TripTimesByWeekdays());
        this.calendarDatesByFirstStoptime = new CalendarDatesByFirstStoptime(firstStoptime, serviceCalendarDates);
        this.identifier = identifier;
        this.validFrom = new SimpleDateFormat("dd.MM.yyyy").format(validFrom.getAsDate());
        this.validTill = new SimpleDateFormat("dd.MM.yyyy").format(validTill.getAsDate());
        this.serviceIds.add(serviceId);
    }

    public void addTripTimeByWeekdays(TripTimeShort tripTimeShort, Stop stop, String weekdays) {
        if (this.weekdays.equals(weekdays)) {
            if (this.tripTimesByWeekdaysList.get(parts - 1).tripTimeByStopNameList.size() < MAX_PART_SIZE) {
                this.tripTimesByWeekdaysList.get(parts - 1).addTripTimeByWeekdays(tripTimeShort, stop, weekdays);
            } else {
                this.parts++;
                TripTimesByWeekdays tripTimesByWeekdays = new TripTimesByWeekdays();
                tripTimesByWeekdays.addTripTimeByWeekdays(tripTimeShort, stop, weekdays);
                this.tripTimesByWeekdaysList.add(tripTimesByWeekdays);
            }
        }
    }

    public boolean containsTrip(TripTimes tripTimes, Stop[] stops, String identifier) {
        for (TripTimesByWeekdays tripTimesByWeekdays : tripTimesByWeekdaysList) {
            if (!tripTimesByWeekdays.tripTimeByStopNameList.isEmpty()) {
                TripTimeByStopName tripTimeByStopName = tripTimesByWeekdays.tripTimeByStopNameList.get(0);
                if (tripTimeByStopName.tripTimeShort.scheduledDeparture != tripTimes.getScheduledDepartureTime(0) || !this.identifier.equals(identifier) || !tripTimeByStopName.stopName.equals(stops[0].getName())) {
                    return false;
                } else {
                    for (int i = 1; i < tripTimesByWeekdays.tripTimeByStopNameList.size() && i < stops.length; i++) {
                        tripTimeByStopName = tripTimesByWeekdays.tripTimeByStopNameList.get(i);
                        if (!tripTimeByStopName.stopName.equals(stops[i].getName()) || tripTimeByStopName.tripTimeShort.scheduledDeparture != tripTimes.getScheduledDepartureTime(i)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void mergeWeekdays(String weekdays, FeedScopedId serviceId) {
        this.weekdays = Stream.of(weekdays.split("")).sorted(Comparator.comparingInt("ETKNRLP"::indexOf))
                .distinct().filter(item-> !item.isEmpty()).collect(Collectors.joining());
        StringBuilder result = new StringBuilder();
        if ("ETKNRLP".contains(this.weekdays) && this.weekdays.length() > 2) {
            char[] chars = this.weekdays.toCharArray();
            result.append(chars[0]).append("-").append(chars[this.weekdays.length() - 1]);
        } else {
            result.append(String.join(",", this.weekdays.split("")));
        }
        this.weekdays = result.toString();
        this.serviceIds.add(serviceId);
    }

    @Override
    public String toString() {
        return "TripTimesByWeekdaysPart{" +
                "parts=" + parts +
                ", tripTimeByStopNameList=" + tripTimesByWeekdaysList +
                '}';
    }


    public static class TripTimesByWeekdays {
        public List<TripTimeByStopName> tripTimeByStopNameList = Lists.newArrayList();

        public void addTripTimeByWeekdays(TripTimeShort tripTimeShort, Stop stop, String weekdays) {
            if (this.tripTimeByStopNameList.stream().noneMatch(tripTimeByStop1 -> tripTimeByStop1.tripTimeShort.equals(tripTimeShort))) {//if is same day and trip time add returns true, we added a new time, otherwise need to add dayName to weekdays
                this.tripTimeByStopNameList.add(new TripTimeByStopName(stop.getName(), tripTimeShort));
            }
        }

        @Override
        public String toString() {
            return "TripTimeByWeekdays{" +
                    ", tripTimeByStopNameList=" + tripTimeByStopNameList +
                    '}';
        }
    }

    public static class TripTimeByStopName {
        public String stopName;
        public TripTimeShort tripTimeShort;
        public boolean differentDeparture = false;

        public TripTimeByStopName(String stopName, TripTimeShort tripTimeShort) {
            this.stopName = stopName;
            this.tripTimeShort = tripTimeShort;
            if (tripTimeShort.scheduledDeparture != tripTimeShort.scheduledArrival)
                this.differentDeparture = true;
        }

        @Override
        public String toString() {
            return "TripTimeByStopName{" +
                    "stopName='" + stopName + '\'' +
                    ", tripTimeShortList=" + tripTimeShort +
                    '}';
        }
    }

    public static class CalendarDatesByFirstStoptime {
        public int time;
        public List<CalendarDateException> calendarDateExceptions = Lists.newArrayList();

        public CalendarDatesByFirstStoptime(int time, List<ServiceCalendarDate> serviceCalendarDates) {
            this.time = time;
            for (ServiceCalendarDate serviceCalendarDate : serviceCalendarDates) {
                this.addCalendarDateException(serviceCalendarDate);
            }
        }

        private void addCalendarDateException(ServiceCalendarDate serviceCalendarDate) {
            Optional<CalendarDateException> optionalCalendarDateException = this.calendarDateExceptions.stream()
                    .filter(calendarDateException -> calendarDateException.exceptionType == serviceCalendarDate.getExceptionType()).findFirst();
            if (optionalCalendarDateException.isPresent()) {
                optionalCalendarDateException.get().addDate(serviceCalendarDate.getDate().getAsDate());
            } else {
                this.calendarDateExceptions.add(new CalendarDateException(
                        serviceCalendarDate.getExceptionType(), serviceCalendarDate.getDate().getAsDate()));
            }
        }
    }

    public static class CalendarDateException {
        public int exceptionType;
        public List<Date> dates = Lists.newArrayList();

        public CalendarDateException(int exceptionType, Date date) {
            this.exceptionType = exceptionType;
            this.dates.add(date);
        }

        public void addDate(Date date) {
            this.dates.add(date);
        }
    }
}
