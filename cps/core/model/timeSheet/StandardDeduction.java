
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "StandardDeduction")
@Access(AccessType.FIELD)
@Table(name = "\"StandardDeduction\"")
public class StandardDeduction implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = Time.class)
  @JoinColumn(name = "std_deduc_time_in_id", nullable = false, referencedColumnName = "id")
  private Time timeIn_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = Time.class)
  @JoinColumn(name = "std_deduc_time_out_id", nullable = false, referencedColumnName = "id")
  private Time timeOut_;

  @Transient
  private int stdDeductionTime_ = TimeSheetConstants.STD_DEDUC_TIME;

  @Column(precision = 31, scale = 2, name = "StdDeducMileage")
  BigDecimal stdDeductionMileage_ = new BigDecimal("23.0");

  public StandardDeduction() {

  }

  protected StandardDeduction(JobTime jobTime, boolean isBefore) {
    if (isBefore) {
      timeOut_ = jobTime.getTimeIn();
      timeIn_ = timeOut_.minusMinutes(stdDeductionTime_);
    } else {
      timeIn_ = jobTime.getTimeOut();
      timeOut_ = timeIn_.plusMinutes(stdDeductionTime_);
    }
  }

  public Time getTimeIn() {
    return timeIn_;
  }

  public Time getTimeOut() {
    return timeOut_;
  }

  public BigDecimal getStdDeducMileage() {
    return stdDeductionMileage_;
  }

  @Override
  public String getId() {
    return id;
  }
}
