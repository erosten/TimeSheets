
package cps.core.db.frame;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Provides CRUD operations given a valid AbstractDerbyDatabase. A database is 'valid' if it can be
 * connected to, which implies a working DataSource, Connection, entityManagerFactory, and
 * entityManager. An outside package class can extend this class with the cps.core.db.frame package
 * and a derby embedded database+eclipselink solution, and build upon this class to create more
 * specific DAO solutions.
 * 
 * @author Erik Rosten, 2017
 *
 */
public abstract class DerbyDatabaseDAO {

  private DerbyDatabase db = null;

  /**
   * Given an AbstractDerbyDatabase, the constructor sets the private instance variable and attempts
   * to connect to the database.
   * 
   * @param db
   *          the DerbyDatabase to connect to.
   * @throws SQLException
   *           if the database cannot be connected to.
   */
  protected DerbyDatabaseDAO(DerbyDatabase db) throws SQLException {
    this.db = db;
    this.db.connect();
  }

  /**
   * 'Create' option of CRUD. Add an object to the database where the class is an annotated entity,
   * and the object doesn't already exist.
   * 
   * @param clazz
   *          the class of the entity object
   * @param t
   *          the object to add to the database
   * @return a boolean denoting whether the object was added. False values are returned from null
   *         objects, null classes, and existing objects.
   */
  protected <T extends BaseEntity> boolean add(Class<T> clazz, T t) {
    if (t == null || clazz == null) {
      return false;
    }
    // implements equals as a check through the List returned by findAll
    for (final T obj : this.findAll(clazz)) {
      if (obj.equals(t)) {
        // database contained an identical instance of the class
        // nothing done
        return false;
      }
    }
    return this.db.persist(t);
  }

  /**
   * 'U' operation of CRUD. Updates the database with the existing persistence context. Intended for
   * use on object edits.
   * 
   * @throws PersistenceException
   *           if flush fails.
   */
  protected <T extends BaseEntity> void save() {
    this.db.update();
  }

  /**
   * 'R' operation of CRUD. Removes an existing entity from the persistence context and database.
   * 
   * @param t
   *          the entity to remove
   * @return a boolean denoting success of removal. False values are thrown by null objects and not
   *         finding the object in the persistence context to remove.
   */
  protected <T extends BaseEntity> boolean remove(T t) {
    if (t == null) {
      return false;
    }
    return this.db.remove(t);
  }

  // below are read operations

  /**
   * Checks if the instance is a managed entity instance belonging to the current persistence
   * context.
   * <p>
   * Reference javadocs:
   * <p>
   * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html
   * 
   * @param t
   *          object to search the persistence context for
   * @param clazz
   *          the class of the object to search for
   * @return a boolean denoting whether the search for the object was successful
   * @throws IllegalArgumentException
   *           if the object given was not an entity.
   */
  protected <T extends BaseEntity> boolean contains(Class<T> clazz, T t) {
    return this.contains(clazz, t.getId());
  }

  /**
   * Checks if the instance is a managed entity instance belonging to the current persistence
   * context.
   * <p>
   * Reference javadocs:
   * <p>
   * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html
   * 
   * @param id
   *          the id of the object to search the persistence context for
   * @param clazz
   *          the class of the object to search for
   * @return a boolean denoting whether the search for the object was successful
   * @throws IllegalArgumentException
   *           if the object given was not an entity.
   */
  protected <T extends BaseEntity> boolean contains(Class<T> clazz, String id) {
    final T obj = this.findById(clazz, id);
    if (obj == null) {
      return false;
    }
    return this.db.contains(obj);
  }

  /**
   * Finds an entity object based on their class and id.
   * 
   * @param clazz
   *          the class of the entity object to find
   * @param id
   *          the id of the object
   * @return the object of the specified class and id number, null if either argument is null, or if
   *         the object was not found
   */
  protected <T extends BaseEntity> T findById(Class<T> clazz, String id) {
    return (((id == null) || (id == null)) ? null : this.db.find(clazz, id));
  }

  /**
   * Finds all the entity objects for a given entity class. If the argument is null,
   * Collections.emptyList() is returned.
   * 
   * @param clazz
   *          the entity class to find all objects of
   * @return a list of entity objects of the given class
   */
  protected <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
    if (clazz == null) {
      return Collections.emptyList();
    }
    final CriteriaBuilder cb = this.db.getCriteriaBuilder();
    final CriteriaQuery<T> query = cb.createQuery(clazz);
    final Root<T> from = query.from(clazz);
    query.select(from);
    return this.db.getMultiResultFrom(query);
  }

  protected <T extends BaseEntity> List<T> findByAttributes(Class<T> clazz,
      Map<String, String> attributes) {
    // set up the Criteria query
    CriteriaBuilder cb = this.db.getCriteriaBuilder();
    CriteriaQuery<T> query = cb.createQuery(clazz);
    Root<T> from = query.from(clazz);
    List<Predicate> predicates = new ArrayList<>();
    for (String attributeKey : attributes.keySet()) {
      Path<Object> attributeValue = from.get(attributeKey);
      if (attributeValue != null) {
        predicates.add(cb.like(((Expression) attributeValue), "%" + attributes.get(attributeKey)
            + "%"));
      }
    }
    query.where(predicates.toArray(new Predicate[] {}));
    TypedQuery<T> q = db.getTypedQuery(query);

    return q.getResultList();
  }

  /**
   * Returns a criteriaBuilder from the database's entitymanager.
   * 
   * @return a usable, new criteriaBuilder from the database's entityManager.
   */
  protected CriteriaBuilder getCriteriaBuilder() {
    return this.db.getCriteriaBuilder();
  }

  /**
   * Create an instance of TypedQuery for executing a criteria query and Executes a SELECT query
   * that returns a single result.
   * <p>
   * Reference javadocs:
   * <p>
   * https://docs.oracle.com/javaee/7/api/index.html?javax/persistence/TypedQuery.html
   * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html
   * <p>
   * 
   * 
   * @param query
   *          the CriteriaQuery to get a single result from
   * @return a single, unique object satisfying the criteriaQuery
   * @throws IllegalArgumentException
   *           if the criteria query is found to be invalid during creation
   */
  protected <T extends BaseEntity> T getSingleResultFrom(CriteriaQuery<T> query) {
    return this.db.getSingleResult(query);
  }

  /**
   * Create an instance of TypedQuery for executing a criteria query and Executes a SELECT query and
   * return the query results as a typed List.
   * <p>
   * Reference javadocs:
   * <p>
   * https://docs.oracle.com/javaee/7/api/index.html?javax/persistence/TypedQuery.html
   * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html
   * <p>
   * 
   * 
   * @param query
   *          the CriteriaQuery to get a resultList from
   * @return a List of objects satisfying the criteriaQuery
   * @throws IllegalArgumentException
   *           if the criteria query is found to be invalid during creation
   */
  protected <T extends BaseEntity> List<T> getMultiResultFrom(CriteriaQuery<T> query) {
    return this.db.getMultiResultFrom(query);
  }
}
