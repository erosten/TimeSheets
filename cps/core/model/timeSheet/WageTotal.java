
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

// 1. package level constructors
// 2. public getters for all values
// 3. VARIABLES: ID, REGpay, OTpay, DTpay,
// REGhours, OT hours, DT hours,
// total hours, total pay, wage used,
// wage related jobs for a timesheet period

@Entity(name = "WageTotal")
@Table(name = "\"WageTotal\"")
@Access(AccessType.FIELD)
public class WageTotal implements BaseEntity, Comparable<WageTotal> {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Column(precision = 31, scale = 4, name = "DoubleHalfPay")
  private BigDecimal doubleHalfPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleHalfTime")
  private BigDecimal doubleHalfTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "DoublePay")
  private BigDecimal doublePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleTime")
  private BigDecimal doubleTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "OverPay")
  private BigDecimal overPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "OverTime")
  private BigDecimal overTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "RegularPay")
  private BigDecimal regularPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "RegularTime")
  private BigDecimal regularTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "WagePay")
  private BigDecimal wagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "TotalPay")
  private BigDecimal totalPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "TotalTime")
  private BigDecimal totalHours_ = BigDecimal.ZERO;

  @OneToOne(orphanRemoval = false, targetEntity = JobWage.class)
  @JoinColumn(name = "WageTotal_ID", referencedColumnName = "id")
  private Wage wage_;

  // calculated values
  @Column(precision = 31, scale = 4, name = "MileagePay")
  private BigDecimal mileagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "Mileage")
  private BigDecimal mileage_ = BigDecimal.ZERO;

  public WageTotal() {
  }

  // @entity requires empty argument constructor
  protected WageTotal(JobEntry jobEntry) {
    this.wage_ = jobEntry.getWage();
    List<JobEntry> jobEntries = new ArrayList<>();
    jobEntries.add(jobEntry);
    updateValues(jobEntries);
  }

  protected void updateValues(List<JobEntry> jobEntries) {
    regularPay_ = BigDecimal.ZERO;
    overPay_ = BigDecimal.ZERO;
    doublePay_ = BigDecimal.ZERO;
    doubleHalfPay_ = BigDecimal.ZERO;
    wagePay_ = BigDecimal.ZERO;
    totalPay_ = BigDecimal.ZERO;
    regularTime_ = BigDecimal.ZERO;
    overTime_ = BigDecimal.ZERO;
    doubleTime_ = BigDecimal.ZERO;
    doubleHalfTime_ = BigDecimal.ZERO;
    totalHours_ = BigDecimal.ZERO;
    mileagePay_ = BigDecimal.ZERO;
    mileage_ = BigDecimal.ZERO;
    for (JobEntry jobEntry : jobEntries) {
      if (jobEntry.getWage().hasSameNameAs(this.wage_)) {
        regularPay_ = regularPay_.add(jobEntry.getRegularPay());
        overPay_ = overPay_.add(jobEntry.getOverPay());
        doublePay_ = doublePay_.add(jobEntry.getDoublePay());
        doubleHalfPay_ = doubleHalfPay_.add(jobEntry.getDoubleHalfPay());
        wagePay_ = wagePay_.add(jobEntry.getTotalWagePay());
        regularTime_ = regularTime_.add(jobEntry.getRegularTime());
        overTime_ = overTime_.add(jobEntry.getOverTime());
        doubleTime_ = doubleTime_.add(jobEntry.getDoubleTime());
        doubleHalfTime_ = doubleHalfTime_.add(jobEntry.getDoubleHalfTime());
        totalHours_ = totalHours_.add(jobEntry.getTotalHours());
        totalPay_ = totalPay_.add(jobEntry.getTotalPay());
        mileagePay_ = mileagePay_.add(jobEntry.getMileagePay());
        mileage_ = mileage_.add(jobEntry.getMileage());
      }
    }
    checkValues();
  }

  private void checkValues() {
    // checks
    boolean payCheck = totalPay_.subtract(mileagePay_).compareTo(regularPay_.add(overPay_).add(
        doublePay_).add(doubleHalfPay_)) == 0;
    boolean payCheck2 = wagePay_.add(mileagePay_).compareTo(totalPay_) == 0;
    // if you use .equals, it may throw an error where they do not add up
    // properly due to .0 at the end
    boolean hourCheck = totalHours_.compareTo(regularTime_.add(overTime_).add(doubleTime_).add(
        doubleHalfTime_)) == 0;
    if (!payCheck) {
      throw new IllegalStateException(
          "Pay did not add up properly in wageTotal, total was " + totalPay_ + "mileage was "
              + mileagePay_ + "subtracted was " + totalPay_.subtract(mileagePay_)
              + " added pay was " + regularPay_ + " + " + overPay_ + " + " + doublePay_ + " + "
              + doubleHalfPay_);
    }
    if (!hourCheck) {
      throw new IllegalStateException(
          "Hours did not add to total " + totalHours_ + ", it was " + regularTime_ + " + "
              + overTime_ + " + " + doubleTime_ + " + " + doubleHalfTime_ + " = " + regularTime_
                  .add(overTime_).add(doubleTime_).add(doubleHalfTime_));
    }
    if (!payCheck2) {
      throw new IllegalStateException("wage pay plus mileage did not add up to total pay");
    }
  }

  // public getters for all values

  public BigDecimal getDoubleHalfPay() {
    return this.doubleHalfPay_;
  }

  public BigDecimal getDoubleHalfTime() {
    return this.doubleHalfTime_;
  }

  public BigDecimal getDoublePay() {
    return this.doublePay_;
  }

  public BigDecimal getDoubleTime() {
    return this.doubleTime_;
  }

  public BigDecimal getOverPay() {
    return this.overPay_;
  }

  public BigDecimal getOverTime() {
    return this.overTime_;
  }

  public BigDecimal getRegularPay() {
    return this.regularPay_;
  }

  public BigDecimal getRegularTime() {
    return this.regularTime_;
  }

  public BigDecimal getTotalPay() {
    return this.totalPay_;
  }

  public BigDecimal getWagePay() {
    return wagePay_;
  }

  public BigDecimal getTotalHours() {
    return this.totalHours_;
  }

  public Wage getWage() {
    return this.wage_;
  }

  public BigDecimal getMileage() {
    return this.mileage_;
  }

  public BigDecimal getMileagePay() {
    return this.mileagePay_;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int compareTo(WageTotal o) {
    return this.wage_.compareTo(o.getWage());
  }
}
