
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.LocalTime;

@Entity(name = "JobTime")
@Access(AccessType.FIELD)
@Table(name = "\"JobTime\"")
public class JobTime implements BaseEntity, Comparable<JobTime> {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = Time.class)
  @JoinColumn(name = "time_in_id", nullable = false, referencedColumnName = "id")
  private Time timeIn_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = Time.class)
  @JoinColumn(name = "time_out_id", nullable = false, referencedColumnName = "id")
  private Time timeOut_;

  @Column(precision = 31, scale = 2, name = "TimeBetween")
  private BigDecimal timeBetween_;

  // distribution of times

  @Column(precision = 31, scale = 2, name = "RegularTime")
  private BigDecimal regularTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "OverTime")
  private BigDecimal overTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleTime")
  private BigDecimal doubleTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleHalfTime")
  private BigDecimal doubleHalfTime_ = BigDecimal.ZERO;

  // context variables
  @Column(precision = 31, scale = 2, name = "SameDayContext")
  private BigDecimal sameDay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "SameWeekContext")
  private BigDecimal sameWeek_ = BigDecimal.ZERO;

  @Column(name = "hasWorkedSixDays")
  private boolean hasWorkedSix_;

  @Column(precision = 31, scale = 2, name = "EffectiveHours")
  private BigDecimal effectiveHours_ = BigDecimal.ZERO;

  public JobTime() {
    // @entity requires no arg constructor
  }

  public JobTime(LocalTime timeIn, LocalTime timeOut) {
    this.timeIn_ = new Time(timeIn);
    this.timeOut_ = new Time(timeOut);
    this.timeBetween_ = this.computeTimeBetween();
    effectiveHours_ = timeBetween_;
  }

  public JobTime(LocalTime timeIn, boolean timeInUncertain, LocalTime timeOut,
      boolean timeOutUncertain) {
    this.timeIn_ = new Time(timeIn, timeInUncertain);
    this.timeOut_ = new Time(timeOut, timeOutUncertain);
    this.timeBetween_ = this.computeTimeBetween();
    effectiveHours_ = timeBetween_;
  }

  protected void setTimesAs(Time timeIn, Time timeOut) {
    this.timeIn_ = timeIn;
    this.timeOut_ = timeOut;
    this.timeBetween_ = this.computeTimeBetween();
    effectiveHours_ = timeBetween_;
  }

  protected void setEffectiveHours(BigDecimal flatRateHours) {
    effectiveHours_ = flatRateHours;
  }

  protected void updateTimeDistribution(BigDecimal sameDay, BigDecimal sameWeek,
      boolean hasWorkedSix) {
    sameDay_ = sameDay;
    sameWeek_ = sameWeek;
    hasWorkedSix_ = hasWorkedSix;
    updateValues(sameDay, sameWeek, hasWorkedSix);
    checkValues();
  }

  protected BigDecimal sameDayContext() {
    return sameDay_;
  }

  protected BigDecimal sameWeekContext() {
    return sameWeek_;
  }

  private void checkValues() {
    System.out.println("Total: " + effectiveHours_);
    System.out.println("Regular: " + regularTime_);
    System.out.println("Over: " + overTime_);
    System.out.println("Double: " + doubleTime_);
    System.out.println("Double x 1.5: " + doubleHalfTime_);
    if (regularTime_.add(overTime_).add(doubleTime_).add(doubleHalfTime_).compareTo(
        effectiveHours_) != 0) {
      throw new IllegalStateException("Time distribution did not add to total");
    }

  }

  private void updateValues(BigDecimal sameDay, BigDecimal sameWeek, boolean hasWorkedSix) {
    final BigDecimal jobTimeHours = this.getDuration();
    final BigDecimal sameDayHours = sameDay;
    final BigDecimal workWeekHours = sameWeek;
    System.out.println("Same day hours for " + timeIn_ + " - " + timeOut_ + ": " + sameDayHours);
    System.out.println("Same week hours for " + timeIn_ + " - " + timeOut_ + ": " + workWeekHours);
    System.out.println("Has Worked Last Six Days " + timeIn_ + " - " + timeOut_ + ": "
        + hasWorkedSix);
    // constants
    final BigDecimal eightHundred = new BigDecimal("800");
    final BigDecimal twelveHundred = new BigDecimal("1200");
    final BigDecimal fourHundred = new BigDecimal("400");
    final BigDecimal zero = BigDecimal.ZERO;
    final BigDecimal forty = new BigDecimal("4000");
    // previous work week hours cannot be above 40
    // work week case 1
    // work week hours already above 40
    if (!hasWorkedSix && workWeekHours.compareTo(forty) >= 0) {
      regularTime_ = zero;
      overTime_ = jobTimeHours;
      doubleTime_ = zero;
      doubleHalfTime_ = zero;
      return;
      // work week case 2
      // work week hours plus this job time will send over 40
    } else if (!hasWorkedSix && workWeekHours.add(jobTimeHours).compareTo(forty) >= 0) {
      regularTime_ = forty.subtract(workWeekHours);
      overTime_ = jobTimeHours.subtract(regularTime_);
      doubleTime_ = zero;
      doubleHalfTime_ = zero;
      return;
    } else {
      // CASE 1
      // previous hours >= 12 - all doubletime
      if ((sameDayHours.compareTo(twelveHundred) > -1)) {
        if (!hasWorkedSix) {
          regularTime_ = zero;
          overTime_ = zero;
          doubleTime_ = jobTimeHours;
          doubleHalfTime_ = zero;
        } else {
          regularTime_ = zero;
          overTime_ = zero;
          doubleTime_ = zero;
          doubleHalfTime_ = jobTimeHours;
        }
        return;
      }
      // case 2,3 - prev hours >=8 -it's overtime, may go into doubletime
      if (sameDayHours.compareTo(eightHundred) > -1) {
        // case 2 - will NOT send into doubletime
        // formula used: job's hrs <= 4 - (prev hrs - 8)
        // if equal, job's hrs will send it exactly to 12 hours (no double
        // required)
        if (jobTimeHours.compareTo(fourHundred.subtract(sameDayHours.subtract(eightHundred))) < 1) {
          if (!hasWorkedSix) {
            regularTime_ = zero;
            overTime_ = jobTimeHours;
            doubleTime_ = zero;
            doubleHalfTime_ = zero;
          } else {
            overTime_ = zero;
            doubleTime_ = jobTimeHours;
            doubleHalfTime_ = zero;
          }
          return;
        }
        // case 3 - job's hrs > 4 - (prev hrs - 8)
        // will DEFINITELY send into doubletime
        if (jobTimeHours.compareTo(fourHundred.subtract(sameDayHours.subtract(eightHundred))) > 0) {
          if (!hasWorkedSix) {
            regularTime_ = zero;
            overTime_ = fourHundred.subtract(sameDayHours.subtract(eightHundred));
            doubleTime_ = jobTimeHours.subtract(overTime_);
            doubleHalfTime_ = zero;
          } else {
            regularTime_ = zero;
            overTime_ = zero;
            doubleTime_ = fourHundred.subtract(sameDayHours.subtract(eightHundred));
            doubleHalfTime_ = jobTimeHours.subtract(doubleTime_);
          }
          return;
        }
      }
      // at this point, prev hours must be under 8
      // case 4 - total + prev hours < 8
      // will not send into overtime
      if (jobTimeHours.add(sameDayHours).compareTo(eightHundred) < 1) {
        if (!hasWorkedSix) {
          regularTime_ = jobTimeHours;
          overTime_ = zero;
          doubleTime_ = zero;
          doubleHalfTime_ = zero;
        } else {
          regularTime_ = zero;
          overTime_ = jobTimeHours;
          doubleTime_ = zero;
          doubleHalfTime_ = zero;
        }
        return;
      }
      // case 5 - total + prev < 12
      // will not send into doubletime, but will send into overtime
      if (jobTimeHours.add(sameDayHours).compareTo(twelveHundred) < 1) {
        if (!hasWorkedSix) {
          regularTime_ = eightHundred.subtract(sameDayHours);
          overTime_ = jobTimeHours.subtract(regularTime_);
          doubleTime_ = zero;
          doubleHalfTime_ = zero;
        } else {
          regularTime_ = zero;
          overTime_ = eightHundred.subtract(sameDayHours);
          doubleTime_ = jobTimeHours.subtract(overTime_);
          doubleHalfTime_ = zero;
        }
        return;
      }
      // case 6 - total+ prev > 12
      // will send into doubletime
      if (jobTimeHours.add(sameDayHours).compareTo(twelveHundred) > -1) {
        if (!hasWorkedSix) {
          regularTime_ = eightHundred.subtract(sameDayHours);
          overTime_ = fourHundred;
          doubleTime_ = jobTimeHours.subtract(regularTime_.add(overTime_));
          doubleHalfTime_ = zero;
        } else {
          regularTime_ = zero;
          overTime_ = eightHundred.subtract(sameDayHours);
          doubleTime_ = fourHundred;
          doubleHalfTime_ = jobTimeHours.subtract(overTime_.add(doubleTime_));
        }
        return;
      }
    }
    throw new IllegalStateException("State unaccounted for. [JobEntry update]");
  }

  public Time getTimeIn() {
    return timeIn_;
  }

  public int getTimeInHour() {
    return timeIn_.getHourOfDay();
  }

  public int getTimeInMinute() {
    return timeIn_.getMinuteOfHour();
  }

  public Time getTimeOut() {
    return timeOut_;
  }

  public int getTimeOutHour() {
    return timeOut_.getHourOfDay();
  }

  public int getTimeOutMinute() {
    return timeOut_.getMinuteOfHour();
  }

  protected boolean hasUncertainTime() {
    return timeIn_.isUncertain() || timeOut_.isUncertain();
  }

  public BigDecimal getDuration() {
    return effectiveHours_;
  }

  public BigDecimal getRegular() {
    return regularTime_;
  }

  public BigDecimal getOver() {
    return overTime_;
  }

  public BigDecimal getDouble() {
    return doubleTime_;
  }

  public BigDecimal getDoubleHalf() {
    return doubleHalfTime_;
  }

  // starts and ends before
  protected boolean isBefore(JobTime otherJobTime) {
    final boolean isTimeOutEarlier = this.timeOut_.isBefore(otherJobTime.timeOut_);
    final boolean isTimeInEarlier = this.timeIn_.isBefore(otherJobTime.timeIn_);
    return isTimeOutEarlier && isTimeInEarlier;
  }

  // starts and ends after
  protected boolean isAfter(JobTime otherJobTime) {
    final boolean isTimeInLater = this.timeIn_.isAfter(otherJobTime.timeIn_);
    final boolean isTimeOutLater = this.timeOut_.isAfter(otherJobTime.timeOut_);
    return isTimeOutLater && isTimeInLater;
  }

  private BigDecimal computeTimeBetween() {
    final MathContext mc = MathContext.DECIMAL128;
    final BigDecimal sixty = new BigDecimal("60");
    final BigDecimal oneHundred = new BigDecimal("100");
    final BigDecimal twentyFourHundred = new BigDecimal("2400");
    BigDecimal jobHours = timeIn_.minutesUntil(timeOut_).multiply(oneHundred).divide(sixty, mc);
    if (jobHours.compareTo(BigDecimal.ZERO) < 0) {
      jobHours = twentyFourHundred.add(jobHours);
    }
    return jobHours;
  }

  @Override
  public int compareTo(JobTime other) {
    return this.timeIn_.compareTo(other.timeIn_);
  }

  @Override
  public String getId() {
    return id;
  }

  public String dumpInfo() {
    String outString = "";
    outString += "Same day hours for " + timeIn_ + " - " + timeOut_ + ": " + sameDayContext()
        + "\n";
    outString += "Same week hours for " + timeIn_ + " - " + timeOut_ + ": " + sameWeekContext()
        + "\n";
    outString += "Has Worked Last Six Days " + timeIn_ + " - " + timeOut_ + ": " + hasWorkedSix_
        + "\n";
    outString += timeIn_.getTimeString() + " - " + timeOut_.getTimeString();
    return outString;
  }

  @Override
  public String toString() {
    return timeIn_.getTimeString() + " - " + timeOut_.getTimeString();
  }

}
