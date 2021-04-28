package org.opentripplanner.index.model;

import com.google.common.collect.Lists;
import org.opentripplanner.model.ServiceCalendarDate;
import org.opentripplanner.model.Stop;

import java.util.*;

public class TripTimesByWeekdays {
    public String weekdays;
    public List<TripTimeByStopName> tripTimeByStopNameList = Lists.newArrayList();
    public CalendarDatesByFirstStoptime calendarDatesByFirstStoptime;

    public TripTimesByWeekdays() {
    }

    public TripTimesByWeekdays(String weekdaysGroup, int firstStoptime, List<ServiceCalendarDate> serviceCalendarDates) {
        this.weekdays = weekdaysGroup;
        this.calendarDatesByFirstStoptime = new CalendarDatesByFirstStoptime(firstStoptime, serviceCalendarDates);
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
