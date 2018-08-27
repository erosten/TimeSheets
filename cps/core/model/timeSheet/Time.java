
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaLocalTimeConverter;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;

@Entity(name = "Time")
@Access(AccessType.FIELD)
@Table(name = "\"Time\"")
public class Time implements BaseEntity, Comparable<Time> {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Converter(converterClass = JodaLocalTimeConverter.class, name = "jobTimeConverter")
  @Convert("jobTimeConverter")
  @Column(name = "TimeOfJob")
  private LocalTime time_;

  @Basic
  @Column(name = "isUncertain")
  private boolean isUncertain;

  public Time() {
  }

  /**
   * Creates a Time object based on the LocalTime given. Uncertainty defaults to false. To set
   * uncertainty, use Time(LocalTime time, boolean isUncertain) constructor.
   * 
   * @param time
   *          LocalTime object of the time to be set
   */
  public Time(LocalTime time) {
    this.time_ = time;
    isUncertain = false;
  }

  public Time(LocalTime time, boolean isUncertain) {
    this.time_ = time;
    this.isUncertain = isUncertain;
  }

  protected Time minusMinutes(int min) {
    return new Time(new LocalTime(this.time_.minusMinutes(min)));
  }

  protected Time plusMinutes(int min) {
    return new Time(new LocalTime(this.time_.plusMinutes(min)));
  }

  protected boolean isBefore(Time otherTime) {
    return time_.isBefore(otherTime.time_);
  }

  protected boolean isAfter(Time otherTime) {
    return time_.isAfter(otherTime.time_);
  }

  public int getHourOfDay() {
    return time_.getHourOfDay();
  }

  public int getMinuteOfHour() {
    return time_.getMinuteOfHour();
  }

  public Time markUncertain() {
    return new Time(new LocalTime(this.time_), true);
  }

  public Time markCertain() {
    return new Time(new LocalTime(this.time_), false);
  }

  public boolean isUncertain() {
    return isUncertain;
  }

  /**
   * Returns a time String representation of the Time, four digits always. E.g. 0930 or 0600
   * 
   * @return a String representation of the time, always four digits.
   */
  public String getTimeString() {
    String hourComponent = "";
    String minuteComponent = "";
    hourComponent = Integer.toString(time_.getHourOfDay());
    minuteComponent = Integer.toString(time_.getMinuteOfHour());
    if (hourComponent == "0") {
      hourComponent = "00";
    }
    if (minuteComponent == "0") {
      minuteComponent = "00";
    }
    if (minuteComponent.length() == 1) {
      minuteComponent = "0" + minuteComponent;
    }
    if (hourComponent.length() == 1) {
      hourComponent = "0" + hourComponent;
    }
    return hourComponent + minuteComponent;
  }

  protected BigDecimal minutesUntil(Time otherTime) {
    final Instant time1 = time_.toDateTimeToday().toInstant();
    final Instant time2 = otherTime.time_.toDateTimeToday().toInstant();
    final long durationBetween = new Duration(time1, time2).getStandardMinutes();
    return new BigDecimal(durationBetween);
  }

  protected BigDecimal minutesSince(Time otherTime) {
    final Instant time1 = time_.toDateTimeToday().toInstant();
    final Instant time2 = otherTime.time_.toDateTimeToday().toInstant();
    final long durationBetween = new Duration(time2, time1).getStandardMinutes();
    return new BigDecimal(durationBetween);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return time_.toString();
  }

  @Override
  public int compareTo(Time other) {
    return time_.compareTo(other.time_);
  }

}
