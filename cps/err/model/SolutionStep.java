
package cps.err.model;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaDateTimeConverter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;

@Entity
public class SolutionStep implements BaseEntity {

  @Id
  @Column(name = "id")
  private String id = createId();

  @Basic
  @Column(name = "Step")
  private String solutionStep;
  @Basic
  @Column(name = "stepNum")
  private int stepNum;

  @Converter(converterClass = JodaDateTimeConverter.class, name = "solutionStepDateConverter")
  @JoinColumn(name = "dateAdded")
  @Basic
  private DateTime dateAdded;

  protected SolutionStep() {
    // never call this
  }

  public SolutionStep(String solutionStep, int stepNum) {
    this.solutionStep = solutionStep;
    this.stepNum = stepNum;
    this.dateAdded = DateTime.now();
  }

  public String getStep() {
    return solutionStep;
  }

  public int getStepNum() {
    return stepNum;
  }

  public DateTime getDateAdded() {
    return dateAdded;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((solutionStep == null) ? 0 : solutionStep.hashCode());
    result = prime * result + stepNum;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SolutionStep other = (SolutionStep) obj;
    if (dateAdded == null) {
      if (other.dateAdded != null)
        return false;
    } else if (!dateAdded.equals(other.dateAdded))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (solutionStep == null) {
      if (other.solutionStep != null)
        return false;
    } else if (!solutionStep.equals(other.solutionStep))
      return false;
    if (stepNum != other.stepNum)
      return false;
    return true;
  }
}
