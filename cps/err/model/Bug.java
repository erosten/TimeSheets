
package cps.err.model;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaDateTimeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 * @author Erik Rosten
 * 
 *         if state is solved, class becomes immutable and an illegalstateException is thrown if any
 *         method is called that would modify the class
 *
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "\"Bug\"")
public class Bug implements Comparable<Bug>, BaseEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Basic
  @Column(name = "Title")
  private String title_;

  @Basic
  @Column(name = "Description")
  private String description_;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = SolutionStep.class)
  @JoinTable(name = "Bug_SolutionStep",
      joinColumns = @JoinColumn(name = "Bug_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "SolutionStep_id", referencedColumnName = "id"))
  private List<SolutionStep> solutionSteps_ = new ArrayList<>();

  @Converter(converterClass = JodaDateTimeConverter.class, name = "bugDateTimeConverter")
  @Column(name = "BugDateSubmitted")
  @Basic
  private List<DateTime> modificationDates_ = new ArrayList<>();

  @Basic
  @Column(name = "Solved")
  private boolean solved_ = false;

  @Basic
  @Column(name = "BugNum")
  private int bugNum;

  public Bug() {

  }

  public Bug(String title, String description, int bugNum) {
    this.title_ = title;
    this.description_ = description;
    this.bugNum = bugNum;
    this.id = this.bugNum + "-" + createId().substring(0, 6);
  }

  public Bug(String title, String description, String solution) {
    this.title_ = title;
    this.description_ = description;
    this.solutionSteps_.add(new SolutionStep(solution, this.getNextSolutionStepNum()));
    this.solved_ = true;
  }

  public void addSolutionStep(String solutionStep) {
    checkState();
    this.solutionSteps_.add(new SolutionStep(solutionStep, this.getNextSolutionStepNum()));
  }

  protected boolean removeSolutionStep(SolutionStep solutionStep) {
    checkState();
    // one element array using removeSolutionSteps
    return this.removeSolutionSteps(Arrays.asList(solutionStep));
  }

  public boolean removeSolutionSteps(List<SolutionStep> list) {
    checkState();
    Iterator<SolutionStep> iter = list.iterator();
    while (iter.hasNext()) {
      SolutionStep step = iter.next();
      if (list.contains(step)) {
        iter.remove();
      }
    }
    return true;
  }

  public SolutionStep getSolutionStep(int solutionStepNumber) {
    return this.solutionSteps_.get(solutionStepNumber - 1);
  }

  // last string in solution steps
  public void solve(String solution) {
    // method checks state before adding
    this.addSolutionStep(solution);
    this.solved_ = true;
  }

  protected void setTitle(String title) {
    checkState();
    this.title_ = title;
  }

  protected void setDesc(String desc) {
    checkState();
    this.description_ = desc;
  }

  public String getTitle() {
    return this.title_;
  }

  public String getDesc() {
    return this.description_;
  }

  public List<SolutionStep> getSolutionSteps() {
    return this.solutionSteps_;
  }

  public int getNextSolutionStepNum() {
    return this.solutionSteps_.size() + 1;
  }

  private void checkState() {
    if (this.solved_) {
      throw new IllegalStateException();
    }
  }

  @Override
  public int compareTo(Bug other) {
    return this.id.compareTo(other.id);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    final Bug other = (Bug) obj;

    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String getId() {
    return this.id;
  }
}
