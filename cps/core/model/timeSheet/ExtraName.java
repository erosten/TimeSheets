
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "ExtraName")
@Table(name = "\"ExtraName\"")
@Access(AccessType.FIELD)
public class ExtraName implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Basic
  @Column(name = "ExtraName")
  private String name_;

  public ExtraName() {
  }

  protected ExtraName(String name) {
    name_ = name;
  }

  protected String getName() {
    return this.name_;
  }

  @Override
  public String getId() {
    return id;
  }
}
