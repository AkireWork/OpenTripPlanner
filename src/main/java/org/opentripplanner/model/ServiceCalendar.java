/* This file is based on code copied from project OneBusAway, see the LICENSE file for further information. */
package org.opentripplanner.model;

import org.apache.commons.lang3.StringUtils;
import org.opentripplanner.model.calendar.ServiceDate;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Note that I decided to call this class ServiceCalendar instead of Calendar,
 * so as to avoid confusion with java.util.Calendar
 *
 * @author bdferris
 */
public final class ServiceCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    private FeedScopedId serviceId;

    private int monday;

    private int tuesday;

    private int wednesday;

    private int thursday;

    private int friday;

    private int saturday;

    private int sunday;

    private String weekdaysString;

    private String weekdaysGroup;

    private ServiceDate startDate;

    private ServiceDate endDate;

    public FeedScopedId getServiceId() {
        return serviceId;
    }

    public void setServiceId(FeedScopedId serviceId) {
        this.serviceId = serviceId;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public ServiceDate getStartDate() {
        return startDate;
    }

    public void setStartDate(ServiceDate startDate) {
        this.startDate = startDate;
    }

    public ServiceDate getEndDate() {
        return endDate;
    }

    public void setEndDate(ServiceDate endDate) {
        this.endDate = endDate;
    }

    public String getWeekdaysGroup() {
        return weekdaysGroup;
    }

    public String getWeekdaysString() {
        return weekdaysString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ServiceCalendar that = (ServiceCalendar) o;
        return monday == that.monday && tuesday == that.tuesday && wednesday == that.wednesday
                && thursday == that.thursday && friday == that.friday && saturday == that.saturday
                && sunday == that.sunday && Objects.equals(serviceId, that.serviceId) && Objects
                .equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(serviceId, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                        startDate, endDate);
    }

    public String toString() {
        return "<ServiceCalendar " + this.serviceId + " [" + this.monday + this.tuesday
                + this.wednesday + this.thursday + this.friday + this.saturday + this.sunday + "]>";
    }

    private String replaceDays(String[] daysStrings) {
        String match = "ETKNRLP";
        StringBuilder result = new StringBuilder();
        for (String days : daysStrings) {
            if (match.contains(days) && days.length() > 2) {
                char[] chars = days.toCharArray();
                result.append(chars[0]).append("-").append(chars[days.length() - 1]).append(",");
            } else {
                result.append(String.join(",", days.split(""))).append(",");
            }
        }
        return result.substring(0, result.length() - 1);
    }

    private String collectWeekdaysString(String replaceUnMatchWith) {
        String result = Stream.of(monday == 1 ? "E" : "",
                tuesday == 1 ? "T" : replaceUnMatchWith,
                wednesday == 1 ? "K" : replaceUnMatchWith,
                thursday == 1 ? "N" : replaceUnMatchWith,
                friday == 1 ? "R" : replaceUnMatchWith,
                saturday == 1 ? "L" : replaceUnMatchWith,
                sunday == 1 ? "P" : ""
        ).distinct().filter(item-> !item.isEmpty()).collect(Collectors.joining());

        return result.replaceAll("^,*|,*$", "");
    }

    public void makeWeekdaysGroup() {
        weekdaysString = collectWeekdaysString("");
        weekdaysGroup = replaceDays(collectWeekdaysString(",").split(","));
    }
}
