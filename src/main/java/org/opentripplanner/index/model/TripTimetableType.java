package org.opentripplanner.index.model;

import org.opentripplanner.model.Trip;

import java.util.List;

/**
 * Created by Vahur Kaar (vahurkaar@gmail.com) on 10/19/21.
 */
public class TripTimetableType implements Comparable<TripTimetableType> {

  public String weekdays;
  public Trip trip;
  public List<TripTimeShort> times;

  public TripTimetableType(String weekdaysGroup, Trip trip, List<TripTimeShort> times) {
    this.weekdays = weekdaysGroup;
    this.trip = trip;
    this.times = times;
  }

  @Override
  public int compareTo(TripTimetableType o) {
    return times.get(0).scheduledDeparture - o.times.get(0).scheduledDeparture;
  }
}
