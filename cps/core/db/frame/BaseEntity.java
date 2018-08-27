
package cps.core.db.frame;

import java.util.UUID;

/**
 * The BaseEntity is an interface requiring entity objects to have a unique ID. This interface
 * ensures that objects passed to the DerbyDatabaseDao are entities, and have unique id's associated
 * with them. createId() is generally called in the instance variable construction of a class, so
 * when a new constructor instance is called, the new object immediately has an id number associated
 * with it and whether it is persisted or not, is irrelevant. All entity classes must implement
 * BaseEntity to be used with an extended DerbyDatabaseDAO.
 * 
 * @author Erik Rosten, 2017
 *
 */
public interface BaseEntity {

  /**
   * Implementing classes must return the createId() variable.
   * 
   * @return the id of the object/entity (same uuid.toString() as created when the createId() is
   *         called)
   */
  String getId();

  /**
   * Default method providing an id generator. ID's take the form of a UUID (a unique identifier)
   * 
   * @return a String form of the UUID (with dashes)
   */
  default String createId() {
    final UUID uuid = java.util.UUID.randomUUID();
    return uuid.toString();
  }

  /**
   * Implementing objects must implement a UUID based equals.
   * 
   * @param obj
   *          the object to be compared to
   * @return a boolean denoting equality of the two objects
   */
  @Override
  public boolean equals(Object obj);

  /**
   * Implementing objects must implement a UUID based hashcode.
   * 
   * @return an integer representing the unique hashcode of the object
   */
  @Override
  public int hashCode();
}
