
package cps.core.model.employee;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

// immutable
@Embeddable
@Access(AccessType.FIELD)
public class PersonName implements Comparable<PersonName> {

  @Column(name = "FirstName")
  private String firstName_;

  @Column(name = "LastName")
  private String lastName_;

  @Column(name = "MiddleName")
  private String middlePart_;

  @Column(name = "Prefix")
  private String prefix_;

  @Column(name = "Suffix")
  private String suffix_;

  protected PersonName() {
    // defining @entity requires private no-argument constructor
  }

  private PersonName(NameBuilder nb) {
    this.firstName_ = nb.firstName_;
    this.lastName_ = nb.lastName_;
    this.middlePart_ = nb.middlePart_;
    this.prefix_ = nb.prefix_;
    this.suffix_ = nb.suffix_;
  }

  // getters for fields
  public String getFirstName() {
    return firstName_;
  }

  public String getLastName() {
    return lastName_;
  }

  public String getMiddleName() {
    // can be initial
    return middlePart_;
  }

  // e.g. mr. dr. mrs. ms.
  public String getPrefix() {
    return prefix_;
  }

  // e.g. jr. sr.
  public String getSuffix() {
    return suffix_;
  }

  /**
   * Returns a string of the Full name of the person, At full capacity, Mr. Dennis Roger Smith Jr
   * 
   * @return a string representation of the Full Name of a person
   */
  public String getFullName() {
    final String outPrefix = this.getPrefix().equals("") ? "" : this.getPrefix() + " ";
    final String outFirst = this.getFirstName().equals("") ? "" : this.getFirstName() + " ";
    final String outMiddle = this.getMiddleName().equals("") ? "" : this.getMiddleName() + " ";
    final String outLast = this.getLastName().equals("")
        ? ""
        : this.getLastName() + (this.getSuffix().equals("") ? "" : " ");
    return outPrefix + outFirst + outMiddle + outLast + this.getSuffix();
  }

  /**
   * Returns a string of the Last, First Names of the person, At full capacity, Hanley, Kevin.
   * 
   * @return a string representation of the Last Name, First Name of a person
   */
  public String getFullNameLastFirst() {
    return lastName_ + ", " + firstName_ + (this.getSuffix().equals("")
        ? ""
        : " " + this.getSuffix());
  }

  /**
   * Returns a string of the First Last names of the person, At full capacity, Kevin Hanley.
   * 
   * @return a string representation of the First Name Last Name
   */
  public String getFullNameFirstLast() {
    return firstName_ + " " + lastName_ + (this.getSuffix().equals("")
        ? ""
        : " " + this.getSuffix());
  }

  // boolean null checkers
  boolean hasMiddleName() {
    return middlePart_.equals("");
  }

  boolean hasPrefix() {
    return prefix_.equals("");
  }

  boolean hasSuffix() {
    return suffix_.equals("");
  }

  // methods to return new object w/ new field
  public PersonName withFirstName(String firstName) {
    return new NameBuilder(firstName, lastName_).middleName(middlePart_).prefix(prefix_).suffix(
        suffix_).build();
  }

  public PersonName withLastName(String lastName) {
    return new NameBuilder(firstName_, lastName).middleName(middlePart_).prefix(prefix_).suffix(
        suffix_).build();
  }

  public PersonName withMiddleName(String middleName) {
    return new NameBuilder(firstName_, lastName_).middleName(middleName).prefix(prefix_).suffix(
        suffix_).build();
  }

  public PersonName withPrefix(String prefix) {
    return new NameBuilder(firstName_, lastName_).middleName(middlePart_).prefix(prefix).suffix(
        suffix_).build();
  }

  public PersonName withSuffix(String suffix) {
    return new NameBuilder(firstName_, lastName_).middleName(middlePart_).prefix(prefix_).suffix(
        suffix).build();
  }

  @Override
  public int compareTo(PersonName other) {
    // if first names are equal, compare by last name
    if (!this.firstName_.equals(other.firstName_)) {
      return this.firstName_.compareTo(other.firstName_);
    } else {
      return this.lastName_.compareTo(other.lastName_);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstName_ == null) ? 0 : firstName_.hashCode());
    result = prime * result + ((lastName_ == null) ? 0 : lastName_.hashCode());
    result = prime * result + ((middlePart_ == null) ? 0 : middlePart_.hashCode());
    result = prime * result + ((prefix_ == null) ? 0 : prefix_.hashCode());
    result = prime * result + ((suffix_ == null) ? 0 : suffix_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PersonName)) {
      return false;
    }
    final PersonName other = (PersonName) obj;
    if (firstName_ == null) {
      if (other.firstName_ != null) {
        return false;
      }
    } else if (!firstName_.equals(other.firstName_)) {
      return false;
    }
    if (lastName_ == null) {
      if (other.lastName_ != null) {
        return false;
      }
    } else if (!lastName_.equals(other.lastName_)) {
      return false;
    }
    if (middlePart_ == null) {
      if (other.middlePart_ != null) {
        return false;
      }
    } else if (!middlePart_.equals(other.middlePart_)) {
      return false;
    }
    if (prefix_ == null) {
      if (other.prefix_ != null) {
        return false;
      }
    } else if (!prefix_.equals(other.prefix_)) {
      return false;
    }
    if (suffix_ == null) {
      if (other.suffix_ != null) {
        return false;
      }
    } else if (!suffix_.equals(other.suffix_)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.getFullName();
  }

  public static class NameBuilder {

    private String firstName_;

    private String lastName_;

    private String middlePart_;

    private String prefix_;

    private String suffix_;

    public NameBuilder(String first, String second) {
      this.firstName_ = first;
      this.lastName_ = second;
    }

    public NameBuilder firstName(String firstName) {
      this.firstName_ = firstName;
      return this;
    }

    public NameBuilder lastName(String lastName) {
      this.lastName_ = lastName;
      return this;
    }

    public NameBuilder middleName(String middle) {
      this.middlePart_ = middle;
      return this;
    }

    public NameBuilder prefix(String prefix) {
      this.prefix_ = prefix;
      return this;
    }

    public NameBuilder suffix(String suffix) {
      this.suffix_ = suffix;
      return this;
    }

    /**
     * Attempts to build a PersonName object with whatever data the builder has been given.
     * 
     * @return a valid PersonName object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if the first or last name fields are empty or null (unset).
     */
    public PersonName build() {
      if (this.firstName_ == null) {
        throw new IllegalArgumentException("First Name cannot be null");
      }
      if (this.lastName_ == null) {
        throw new IllegalArgumentException("Last Name cannot be null");
      }
      if (this.firstName_.equals("")) {
        throw new IllegalArgumentException("First Name cannot be blank");
      }
      if (this.lastName_.equals("")) {
        throw new IllegalArgumentException("Last Name cannot be blank");
      }
      if (this.prefix_ == null) {
        this.prefix_ = "";
      }
      if (this.suffix_ == null) {
        this.suffix_ = "";
      }
      if (this.middlePart_ == null) {
        this.middlePart_ = "";
      }
      return new PersonName(this);
    }
  }
}
