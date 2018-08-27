
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "JobPay")
@Access(AccessType.FIELD)
@Table(name = "\"JobPay\"")
public class JobPay implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Column(precision = 31, scale = 4, name = "RegularPay")
  private BigDecimal regularPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "OverPay")
  private BigDecimal overPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "DoublePay")
  private BigDecimal doublePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "DoubleHalfPay")
  private BigDecimal doubleAndaHalfPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "TotalPay")
  private BigDecimal totalWagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "MileageReimbursement")
  private BigDecimal mileageReimbursement_ = BigDecimal.ZERO;

  public JobPay() {
    // @entity requires no arg constructor
  }

  protected void updateValues(JobTimes jobTimes, Wage wageUsed, BigDecimal mileage,
      BigDecimal stdMileageRate, BigDecimal langValue) {
    MathContext mc = MathContext.UNLIMITED;
    BigDecimal effectiveRate = wageUsed.getRate();
    if (wageUsed.getName().equals(TimeSheetConstants.REGULAR_WAGE) || wageUsed.getName().equals(
        TimeSheetConstants.PREMIUM_WAGE)) {
      effectiveRate = wageUsed.getRate().add(langValue);
    }
    this.mileageReimbursement_ = mileage.multiply(stdMileageRate, mc);
    final BigDecimal regularTimeRate = effectiveRate;
    final BigDecimal overTimeRate = effectiveRate.multiply(new BigDecimal("1.5"));
    final BigDecimal doubleTimeRate = effectiveRate.multiply(new BigDecimal("2.0"));
    final BigDecimal doubleHalfTimeRate = effectiveRate.multiply(new BigDecimal("2.5"));

    // set pay based on the hours set in global variables
    BigDecimal oneHundred = new BigDecimal("100");
    regularPay_ = regularTimeRate.multiply(jobTimes.getRegularTime().divide(oneHundred));
    overPay_ = overTimeRate.multiply(jobTimes.getOverTime().divide(oneHundred));
    doublePay_ = doubleTimeRate.multiply(jobTimes.getDoubleTime().divide(oneHundred));
    doubleAndaHalfPay_ = doubleHalfTimeRate.multiply(jobTimes.getDoubleAndaHalfTime().divide(
        oneHundred));
    totalWagePay_ = regularPay_.add(overPay_).add(doublePay_).add(doubleAndaHalfPay_);
    System.out.println("Reg pay: " + regularTimeRate + " x " + jobTimes.getRegularTime().divide(
        oneHundred) + " = " + regularPay_);
    System.out.println("Over pay: " + overPay_);
    System.out.println("Double pay: " + doublePay_);
    System.out.println("Double Half pay: " + doubleAndaHalfPay_);
    System.out.println("Total pay: " + totalWagePay_);
    checkValues();
  }

  private void checkValues() {
    if (totalWagePay_.compareTo((regularPay_.add(overPay_).add(doublePay_).add(
        doubleAndaHalfPay_))) != 0) {
      throw new IllegalStateException(
          "Pay did not add to total " + totalWagePay_ + ", it was " + regularPay_ + " + " + overPay_
              + " + " + doublePay_ + " + " + doubleAndaHalfPay_ + " = " + regularPay_.add(overPay_)
                  .add(doublePay_).add(doubleAndaHalfPay_));
    }
  }

  protected BigDecimal getRegularPay() {
    return this.regularPay_;
  }

  protected BigDecimal getOverPay() {
    return this.overPay_;
  }

  protected BigDecimal getDoublePay() {
    return this.doublePay_;
  }

  protected BigDecimal getDoubleHalfPay() {
    return this.doubleAndaHalfPay_;
  }

  protected BigDecimal getTotalPay() {
    return this.totalWagePay_.add(this.mileageReimbursement_);
  }

  protected BigDecimal getTotalWagePay() {
    return this.totalWagePay_;
  }

  protected BigDecimal getMileagePay() {
    return this.mileageReimbursement_;
  }

  @Override
  public String getId() {
    return id;
  }
}
