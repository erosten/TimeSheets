
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.LocalTime;

@Entity(name = "JobTimes")
@Access(AccessType.FIELD)
@Table(name = "\"JobTimes\"")
public class JobTimes implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = JobTime.class)
  @JoinColumn(name = "JobTimes_ID", referencedColumnName = "id")
  private List<JobTime> jobTimes_ = new ArrayList<>();

  @Column(precision = 31, scale = 2, name = "RegularTime")
  private BigDecimal regularTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "OverTime")
  private BigDecimal overTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleTime")
  private BigDecimal doubleTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleHalfTime")
  private BigDecimal doubleHalfTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "TotalTime")
  private BigDecimal totalTime_ = BigDecimal.ZERO;

  // context variables
  @Basic
  @Column(name = "IsFlatRate")
  private boolean isFlatRate_;

  @Column(precision = 31, scale = 2, name = "FlatRateHours")
  private BigDecimal flatRateHours_ = BigDecimal.ZERO;

  public JobTimes() {

  }

  protected void addTime(JobTime jobTime) {
    jobTimes_.add(jobTime);
    totalTime_ = totalTime_.add(jobTime.getDuration());
  }

  protected void addTime(LocalTime timeIn, LocalTime timeOut) {
    if (isFlatRate_ && (jobTimes_.size() >= 1)) {
      throw new IllegalStateException(
          "JobEntries that are flat rate AND multi-timed are not supported");
    }
    JobTime jobTime = new JobTime(timeIn, timeOut);
    jobTimes_.add(jobTime);
    totalTime_ = totalTime_.add(jobTime.getDuration());
  }

  protected void addTime(LocalTime timeIn, boolean timeInUncertain, LocalTime timeOut,
      boolean timeOutUncertain) {
    if (isFlatRate_ && (jobTimes_.size() >= 1)) {
      throw new IllegalStateException(
          "JobEntries that are flat rate AND multi-timed are not supported");
    }
    JobTime jobTime = new JobTime(timeIn, timeInUncertain, timeOut, timeOutUncertain);
    jobTimes_.add(jobTime);
    totalTime_ = totalTime_.add(jobTime.getDuration());
  }

  public void setTime(JobTime jobTime, Time timeIn, Time timeOut) {
    totalTime_ = totalTime_.subtract(jobTime.getDuration());
    jobTimes_.get(jobTimes_.indexOf(jobTime)).setTimesAs(timeIn, timeOut);
    totalTime_ = totalTime_.add(jobTime.getDuration());
  }

  protected void flatRateHours(BigDecimal flatRateHours) {
    isFlatRate_ = true;
    flatRateHours_ = flatRateHours;
    jobTimes_.get(0).setEffectiveHours(flatRateHours);
  }

  protected BigDecimal getTotalHours() {
    if (!isFlatRate_) {
      return totalTime_.setScale(0, RoundingMode.HALF_UP);
    } else {
      return flatRateHours_.setScale(0, RoundingMode.HALF_UP);
    }
  }

  protected List<JobTime> get() {
    Collections.sort(jobTimes_);
    return jobTimes_;
  }

  protected boolean areFlatRate() {
    return isFlatRate_;
  }

  protected boolean hasUncertainTime() {
    for (JobTime jobTime : jobTimes_) {
      if (jobTime.hasUncertainTime()) {
        return true;
      }
    }
    return false;
  }

  protected BigDecimal getRegularTime() {
    return this.regularTime_.setScale(0, RoundingMode.HALF_UP);
  }

  protected BigDecimal getOverTime() {
    return this.overTime_.setScale(0, RoundingMode.HALF_UP);
  }

  protected BigDecimal getDoubleTime() {
    return this.doubleTime_.setScale(0, RoundingMode.HALF_UP);
  }

  protected BigDecimal getDoubleAndaHalfTime() {
    return this.doubleHalfTime_.setScale(0, RoundingMode.HALF_UP);
  }

  // returns true iff times are COMPLETELY before other times, else returns
  // false
  protected boolean hasTimeBefore(JobTimes otherJobTimes) {
    for (int i = 0; i < jobTimes_.size(); i++) {
      for (int j = 0; j < otherJobTimes.get().size(); j++) {
        if (jobTimes_.get(i).isBefore(otherJobTimes.get().get(j))) {
          return true;
        }
      }
    }
    return false;
  }

  protected boolean hasMultipleTimes() {
    return jobTimes_.size() > 1;
  }

  protected List<Time> getTimesIn() {
    List<Time> timesIn = new ArrayList<>();
    for (JobTime jobTime : this.jobTimes_) {
      timesIn.add(jobTime.getTimeIn());
    }
    return timesIn;
  }

  protected List<Time> getTimesOut() {
    List<Time> timesOut = new ArrayList<>();
    for (JobTime jobTime : this.jobTimes_) {
      timesOut.add(jobTime.getTimeOut());
    }
    return timesOut;
  }

  protected BigDecimal getTotalTimeBefore(JobTime otherJobTime) {
    BigDecimal totalHours = BigDecimal.ZERO;
    for (JobTime jobTime : jobTimes_) {
      if (jobTime.isBefore(otherJobTime)) {
        totalHours = totalHours.add(jobTime.getDuration());
      }
    }
    return totalHours.setScale(0, RoundingMode.HALF_UP);
  }

  protected BigDecimal getRegularTimeBefore(JobTime otherJobTime) {
    BigDecimal totalRegHours = BigDecimal.ZERO;
    for (JobTime jobTime : jobTimes_) {
      if (jobTime.isBefore(otherJobTime)) {
        totalRegHours = totalRegHours.add(jobTime.getRegular());
      }
    }
    return totalRegHours.setScale(0, RoundingMode.HALF_UP);
  }

  protected void updateValues() {
    BigDecimal regTime = BigDecimal.ZERO;
    BigDecimal overTime = BigDecimal.ZERO;
    BigDecimal doubleTime = BigDecimal.ZERO;
    BigDecimal doubleHalfTime = BigDecimal.ZERO;
    BigDecimal totalTime = BigDecimal.ZERO;
    for (JobTime jobTime : jobTimes_) {
      regTime = regTime.add(jobTime.getRegular());
      overTime = overTime.add(jobTime.getOver());
      doubleTime = doubleTime.add(jobTime.getDouble());
      doubleHalfTime = doubleHalfTime.add(jobTime.getDoubleHalf());
      totalTime = totalTime.add(jobTime.getRegular().add(jobTime.getOver()).add(jobTime.getDouble())
          .add(jobTime.getDoubleHalf()));
    }
    this.regularTime_ = regTime;
    this.overTime_ = overTime;
    this.doubleTime_ = doubleTime;
    this.doubleHalfTime_ = doubleHalfTime;
    this.checkValues(this.getTotalHours());
  }

  private void checkValues(BigDecimal totalHours) {
    System.out.println("Total: " + totalHours);
    System.out.println("Regular: " + regularTime_);
    System.out.println("Over: " + overTime_);
    System.out.println("Double: " + doubleTime_);
    System.out.println("Double x 1.5: " + doubleHalfTime_);
    if (totalHours.compareTo((regularTime_.add(overTime_).add(doubleTime_).add(doubleHalfTime_))
        .setScale(0, RoundingMode.HALF_UP)) != 0) {
      throw new IllegalStateException(
          "Hours did not add to total " + totalHours + ", it was " + regularTime_ + " + "
              + overTime_ + " + " + doubleTime_ + " + " + doubleHalfTime_ + " = " + regularTime_
                  .add(overTime_).add(doubleTime_).add(doubleHalfTime_));
    }
  }

  @Override
  public String getId() {
    return id;
  }

}
