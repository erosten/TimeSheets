
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "JobEntries")
@Access(AccessType.FIELD)
@Table(name = "\"JobEntries\"")
public class JobEntries implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = JobEntry.class, orphanRemoval = true)
  @JoinTable(name = "JobEntries_Jobs",
      joinColumns = @JoinColumn(name = "JobEntries_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "JobEntry_id", referencedColumnName = "id"))
  private List<JobEntry> jobEntries_ = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = WageTotals.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "WageTotals_ID",
      referencedColumnName = "id",
      unique = true,
      nullable = false,
      insertable = true)
  private WageTotals wageTotals_ = new WageTotals();

  @Column(precision = 31, scale = 2, name = "Mileage")
  private BigDecimal mileage_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "MileagePay")
  private BigDecimal mileagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "FlatRatePay")
  private BigDecimal flatRatePay_ = BigDecimal.ZERO;

  public JobEntries() {
    // @entity requires no arg constructor
  }

  protected void update() {
    Collections.sort(jobEntries_);
    mileage_ = BigDecimal.ZERO;
    mileagePay_ = BigDecimal.ZERO;
    flatRatePay_ = BigDecimal.ZERO;
    for (JobEntry jobEntry : jobEntries_) {
      mileage_ = mileage_.add(jobEntry.getMileage());
      mileagePay_ = mileagePay_.add(jobEntry.getMileagePay());
      flatRatePay_ = flatRatePay_.add(jobEntry.getFlatRatePay());
    }
    wageTotals_.updateValues(jobEntries_);
  }

  protected void add(JobEntry jobEntry) {
    if (!jobEntries_.contains(jobEntry)) {
      jobEntries_.add(jobEntry);
    }
    update();
  }

  protected void remove(JobEntry jobEntry) {
    if (jobEntries_.contains(jobEntry)) {
      if (!jobEntries_.remove(jobEntry)) {
        throw new IllegalStateException(
            "Job removal for " + jobEntry.getCode()
                + " failed, ArrayList.remove() returned false for given entry");
      }
      update();
    }
  }

  protected BigDecimal getTotalTimeOn(Wage wage) {
    return wageTotals_.getTotalTimeOn(wage);
  }

  protected BigDecimal getRegularTimeOn(Wage wage) {
    return wageTotals_.getRegularTimeOn(wage);
  }

  protected BigDecimal getOverTimeOn(Wage wage) {
    return wageTotals_.getOverTimeOn(wage);
  }

  protected BigDecimal getDoubleTimeOn(Wage wage) {
    return wageTotals_.getDoubleTimeOn(wage);
  }

  protected BigDecimal getDoubleHalfTimeOn(Wage wage) {
    return wageTotals_.getDoubleHalfTimeOn(wage);
  }

  protected BigDecimal getTotalPayOn(Wage wage) {
    return wageTotals_.getTotalPayOn(wage);
  }

  protected BigDecimal getRegularPayOn(Wage wage) {
    return wageTotals_.getRegularPayOn(wage);
  }

  protected BigDecimal getOverPayOn(Wage wage) {
    return wageTotals_.getOverPayOn(wage);
  }

  protected BigDecimal getDoublePayOn(Wage wage) {
    return wageTotals_.getDoublePayOn(wage);
  }

  protected BigDecimal getDoubleHalfPayOn(Wage wage) {
    return wageTotals_.getDoubleHalfPayOn(wage);
  }

  protected BigDecimal getRegularPay() {
    return wageTotals_.getRegularPay();
  }

  protected BigDecimal getOverPay() {
    return wageTotals_.getOverPay();
  }

  protected BigDecimal getDoublePay() {
    return wageTotals_.getDoublePay();
  }

  protected BigDecimal getTotalDoubleHalfPay() {
    return wageTotals_.getDoubleHalfPay();
  }

  protected BigDecimal getWagePay() {
    return wageTotals_.getWagePay();
  }

  protected BigDecimal getTotalPay() {
    return wageTotals_.getTotalPay();
  }

  protected BigDecimal getRegularTime() {
    return wageTotals_.getRegularTime();
  }

  protected BigDecimal getOverTime() {
    return wageTotals_.getOverTime();
  }

  protected BigDecimal getDoubleTime() {
    return wageTotals_.getDoubleTime();
  }

  protected BigDecimal getTotalDoubleHalfTime() {
    return wageTotals_.getDoubleHalfTime();
  }

  protected BigDecimal getTotalHours() {
    return wageTotals_.getTotalHours();
  }

  protected BigDecimal getMileage() {
    return mileage_;
  }

  protected BigDecimal getMileagePay() {
    return mileagePay_;
  }

  protected List<JobEntry> get() {
    Collections.sort(jobEntries_);
    return jobEntries_;
  }

  protected List<Wage> getUniqueWages() {
    return wageTotals_.getUniqueNamedWages();
  }

  protected int getNumUniqueWages() {
    return getUniqueWages().size();
  }

  @Override
  public String getId() {
    return id;
  }

}
