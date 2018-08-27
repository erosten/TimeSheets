
package cps.core.model.employee;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "Employees")
@Access(AccessType.FIELD)
@Table(name = "\"Employee\"")
public class Employee implements Comparable<Employee>, BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Embedded
  private PersonName name_;

  @Basic
  @Column(name = "Abbreviation")
  private String abbreviation_;

  @Basic
  @Column(name = "LanguageBonus", precision = 31, scale = 2)
  private BigDecimal languageBonus_;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee_", targetEntity = EmployeeWage.class)
  @JoinColumn(name = "Employee_ID")
  private final List<EmployeeWage> employeeWages_ = new ArrayList<>();

  @Basic
  @Column(name = "Terminated")
  private boolean terminated;

  protected Employee() {
    // defining @entity requires private no-argument constructor
  }

  private Employee(Builder eb) {
    name_ = eb.name;
    abbreviation_ = eb.abbreviation;
    languageBonus_ = eb.langBonus;
    terminated = false;
    for (final EmployeeWage.Builder wb : eb.wages) {
      wb.employee(this);
      employeeWages_.add(wb.build());
    }
    Collections.sort(this.employeeWages_);
  }

  /**
   * Terminates an employee. Terminating an employee will cause them to no longer be returned by
   * findActiveEmployees(). (findAllEmployees() will always return all employees)
   */
  public void terminate() {
    this.terminated = true;
  }

  /**
   * Sets the employee name to a new PersonName object.
   * 
   * @param name
   */
  public void setName(PersonName name) {
    name_ = name;
  }

  public void setAbbreviation(String abbreviation) {
    abbreviation_ = abbreviation;
  }

  public void setLang(BigDecimal langBonus) {
    languageBonus_ = langBonus;
  }

  public boolean addWage(EmployeeWage employeeWage) {
    if (!employeeWages_.contains(employeeWage)) {
      employeeWages_.add(employeeWage);
      Collections.sort(this.employeeWages_);
      return true;
    } else {
      return false;
    }
  }

  public boolean removeWage(EmployeeWage employeeWage) {
    if (employeeWages_.remove(employeeWage)) {
      Collections.sort(this.employeeWages_);
      return true;
    }
    return false;
  }

  public boolean containsWage(String wageName) {
    for (final EmployeeWage eWage : employeeWages_) {
      if (eWage.getName().equals(wageName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Find the employee wage given by a string wage name.
   * 
   * @param wageName
   *          the name of the wage to be found
   * @return a valid employee wage object
   * @throws IllegalArgumentException
   *           when the wage is not found
   * 
   */
  public Wage getWage(String wageName) {
    for (final EmployeeWage wage : employeeWages_) {
      if (wage.getName().equals(wageName)) {
        return wage;
      }
    }
    throw new IllegalArgumentException("Wage didn't exist");
  }

  public boolean isTerminated() {
    return this.terminated;
  }

  // public getters
  public PersonName getName() {
    return name_;
  }

  public String getFullName() {
    return name_.getFullName();
  }

  public String getFirstName() {
    return this.name_.getFirstName();
  }

  public String getLastName() {
    return this.name_.getLastName();
  }

  public String getFirstLastName() {
    return this.name_.getFirstName() + " " + this.name_.getLastName();
  }

  public String getMiddleName() {
    return this.name_.getMiddleName();
  }

  public String getPrefix() {
    return this.name_.getPrefix();
  }

  public String getSuffix() {
    return this.name_.getSuffix();
  }

  public String getAbbreviation() {
    return this.abbreviation_;
  }

  public BigDecimal getLanguageBonus() {
    return this.languageBonus_;
  }

  public List<EmployeeWage> getWages() {
    Collections.sort(this.employeeWages_);
    return this.employeeWages_;
  }

  @Override
  public String getId() {
    return this.id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.abbreviation_ == null) ? 0 : this.abbreviation_.hashCode());
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Employee)) {
      return false;
    }
    final Employee other = (Employee) obj;

    if (this.abbreviation_.equals(other.abbreviation_)) {
      return true;
    }
    if (this.id == null || other.id == null) {
      return false;
    }
    if (this.id.equals(other.id)) {
      return true;
    }
    return false;
  }

  @Override
  public int compareTo(Employee other) {
    if (this.abbreviation_.compareTo(other.getAbbreviation()) == 0) {
      return this.id.compareTo(other.getId());
    } else {
      return this.abbreviation_.compareTo(other.getAbbreviation());
    }
  }

  // Comparator classes
  public static class FirstNameCompare implements Comparator<Employee> {

    @Override
    public int compare(Employee e1, Employee e2) {
      // if equal, compare by last name
      final int firstNameResult = e1.getFirstName().compareTo(e2.getFirstName());
      return ((firstNameResult == 0)
          ? new Employee.LastNameCompare().compare(e1, e2)
          : firstNameResult);
    }
  }

  public static class LastNameCompare implements Comparator<Employee> {

    @Override
    public int compare(Employee e1, Employee e2) {
      final int lastNameResult = e1.getLastName().compareTo(e2.getLastName());
      // if equal return first name compare
      return ((lastNameResult == 0)
          ? new Employee.FirstNameCompare().compare(e1, e2)
          : lastNameResult);
    }
  }

  public static Employee.Builder getBuilder(final PersonName newName,
      final String newAbbreviation) {
    return new Employee.Builder(newName, newAbbreviation);
  }

  // public builder class
  public static class Builder {

    private final PersonName name;

    private String abbreviation;

    // initialized to 0 if user does not set
    private BigDecimal langBonus = BigDecimal.ZERO;

    private final List<EmployeeWage.Builder> wages = new ArrayList<EmployeeWage.Builder>();

    public Builder(final PersonName newName, final String newAbbreviation) {
      this.name = newName;
      this.abbreviation = newAbbreviation;
    }

    public Builder abbreviation(final String newAbbreviation) {
      this.abbreviation = newAbbreviation;
      return this;
    }

    public Builder langBonus(final BigDecimal newLangBonus) {
      this.langBonus = newLangBonus;
      return this;
    }

    public Builder wage(final EmployeeWage.Builder newWage) {
      this.wages.add(newWage);
      return this;
    }

    public Builder wages(final List<EmployeeWage.Builder> newWages) {
      this.wages.addAll(newWages);
      return this;
    }

    /**
     * Attempts to build an Employee object with whatever data the builder has been given.
     * 
     * @return a valid Employee object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if any field is null or empty
     */
    public Employee build() {
      if (this.name == null) {
        throw new IllegalArgumentException("Employee name object was null");
      }
      final String firstName = this.name.getFirstName();
      if (firstName == null || firstName.equals("")) {
        throw new IllegalArgumentException("Employee first name was null or empty");
      }
      final String lastName = this.name.getLastName();
      if (lastName == null || lastName.equals("")) {
        throw new IllegalArgumentException("Employee last name was null or empty");
      }
      if (this.abbreviation == null || this.abbreviation.equals("")) {
        throw new IllegalArgumentException("Abbreviation cannot be null or empty");
      }
      if (this.langBonus == null) {
        throw new IllegalArgumentException("Lang Bonus cannot be null");
      }
      for (final EmployeeWage.Builder wageBuilder : this.wages) {
        if (wageBuilder == null) {
          throw new IllegalArgumentException("Wage builder was null");
        }
      }
      return new Employee(this);
    }
  }

  public String dumpInfo() {
    String outString = "";
    outString += "Employee Code: " + getId() + "\n";
    outString += " Full Name: " + getName() + "\n" + "Abbrev: " + getAbbreviation() + "\n"
        + "Language Bonus +" + getLanguageBonus() + "\n";
    outString += "--------Wages---------" + "\n";
    for (final EmployeeWage wage : getWages()) {
      outString += wage.getName() + ": " + NumberFormat.getCurrencyInstance().format(wage.getRate())
          + "\n";
    }
    outString += "\n";
    return outString;
  }

  @Override
  public String toString() {
    return getLastName() + ", " + getFirstName() + " [" + getAbbreviation() + "]";
  }
}
