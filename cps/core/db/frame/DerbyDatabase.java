
package cps.core.db.frame;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.eclipse.persistence.jpa.JpaHelper;

/**
 * This class provides an abstraction on the DerbyDatabase. Given a valid persistence.xml and
 * database name, this class can instantiate and provide database related methods to the outside
 * world such as connecting, starting, shutting down and restarting. This class also provides
 * crucial backbone support for DerbyDatabasePortal by encapsulating EclipseLink entityManager
 * related methods, allowing the DerbyDatabasePortal to focus solely on DAO related material.
 * 
 * @author Erik Rosten, 2017
 *
 */
public enum DerbyDatabase {

  TestDB("TestDB", "Test") , ProgramDB("ProgramDB", "Program") , BugTrackerDB("BugTrackerDB",
      "BugTracker");

  private final String PERSISTENCE_UNIT_NAME;

  private final String DB_NAME;

  private EntityManagerFactory factory = null;

  private EntityManager entityManager = null;

  private EmbeddedConnectionPoolDataSource dataSource = null;

  private Connection conn = null;

  private boolean isShutDown_ = true;

  // private methods & constructor
  /**
   * Takes in a database name and a persistence unit name, and sets it to the correct instance
   * variables
   * 
   * 
   * @param dbName
   *          From the relevant javadocs:
   *          <p>
   *          https://db.apache.org/derby/docs/10.12/publishedapi/org/apache/derby/jdbc/BasicEmbeddedDataSource40.html
   *          <p>
   *          Set the database name. Setting this property is mandatory. If a database named wombat
   *          at g:/db needs to be accessed, database name should be set to "g:/db/wombat".
   * 
   *          However, the database will NOT be booted in this constructor.
   * 
   * @param persistenceUnitName
   *          the name of the persistence-unit defined in persistence.xml - usually something like
   *          "Program"
   */
  private DerbyDatabase(final String dbName, final String persistenceUnitName) {
    this.DB_NAME = System.getProperty("user.home") + "/Desktop/" + dbName;
    this.PERSISTENCE_UNIT_NAME = persistenceUnitName;
  }

  // if setCreateDatabase is flagged, the database will be created when a connection is obtained
  // from the datasource (in the connect object)
  private EmbeddedConnectionPoolDataSource createDataSource(String dbname) {
    dataSource = new EmbeddedConnectionPoolDataSource();
    dataSource.setDatabaseName(dbname);
    dataSource.setCreateDatabase("create");
    return dataSource;
  }

  private void checkShutdown() {
    if (isShutDown_) {
      throw new IllegalStateException("Cannot do that operation while the database is shut down");
      // TODO add custom error handling
    }
  }

  // public database methods
  /**
   * Attempts to connect to the database according to the database name and persistence unit name
   * set by the constructor. An EmbeddedConnectionPoolDataSource() is created, and the database is
   * automatically set to create if it does not exist. This method then checks for (null || closed)
   * on the connection, and attempts to get a connection from the DataSource if true. This method
   * then checks for (null || !isOpen) on the entityManagerFactory from the persistence unit name,
   * attempting to create a new one if true. This method then checks for a null || !isOpen on the
   * entityManager, creating a new one from the factory if true. Lastly, this method returns a
   * boolean denoting that dataSource, connection,entityManagerFactory, and entityManger are
   * properly set.
   * <p>
   * 
   * 
   * 
   * @return true iff the dataSource, connection, factory, and entityManager are non-null, the
   *         connection is NOT closed, and the factory and entityManager are open.
   * @throws SQLException
   *           if a database access error occurs determining whether the connection is closed, or if
   *           if a database access error occurs from the dataSource getConnection() call
   */

  public boolean connect() throws SQLException {
    if (isConnected()) {
      return true;
    }
    createDataSource(DB_NAME);
    if (conn == null || conn.isClosed()) {
      conn = dataSource.getConnection();
    }
    if (factory == null || !factory.isOpen()) {
      factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }
    if (entityManager == null || !entityManager.isOpen()) {
      entityManager = factory.createEntityManager();
    }
    isShutDown_ = false;
    return isConnected();
  }

  /**
   * Checks if this database is connected and ready for persist operations. Connection validity is
   * determined by "the dataSource, connection, factory, and entityManager are non-null, whether the
   * connection is NOT closed, and the factory and entityManager are open."
   * 
   * @return a boolean denoting whether this derby database is connected.
   * @throws SQLException
   *           if a database access error occurs determining whether the connection is closed.
   */
  public boolean isConnected() throws SQLException {
    return ((dataSource != null) && (conn != null) && (!conn.isClosed()) && (factory != null)
        && (entityManager != null) && (entityManager.isOpen()) && (factory.isOpen()));
  }

  /**
   * Restarts the underlying derby database, utilizing the shutdown and connect methods within this
   * class, instantiating a new instance of the EmbeddedDriver class inbetween.
   * 
   * @return true iff restart was successful
   * @throws SQLException
   *           from shutdown and connect methods within this class.
   * @throws ClassNotFoundException
   *           cannot find org.apache.derby.jdbc.EmbeddedDriver, cannot proceed with the method
   */
  public boolean restart() throws SQLException, ClassNotFoundException {
    // closes factory, entityManager, and connection (and shuts down derby db)
    shutdown();
    try {
      // recommended by the Apache Derby documentation to restart as per
      // https://db.apache.org/derby/docs/10.5/devguide/tdevdvlp20349.html
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    } catch (final IllegalAccessException ignored) {
      // thrown when class does not have a public no arg constructor
      // EmbeddedDriver HAS a public no arg constructor per the javadocs
      // https://db.apache.org/derby/docs/10.13/publishedapi/org/apache/derby/jdbc/EmbeddedDriver.html
      // thus we can ignore this error
    } catch (final InstantiationException ignored) {
      // thrown during newInstance() if the class given has issues being called
      // since this code is directly recommended from the provider, we choose to
      // ignore this exception
    }
    return connect();
  }

  /**
   * Shuts down the derby database, closing the entity manager, entityManager factory, setting the
   * DataSource to shutdown, and checking to see if it was successfully shutdown by confirming the
   * SQLException error code 45000, and state 08006 when trying to re-establish a dataSource
   * connection. If the database is already shutdown, this method does nothing.
   * 
   * @throws SQLException
   *           Derby did not shutdown correctly.
   */
  public void shutdown() throws SQLException {
    if (this.isShutDown_) {
      return;
    }
    entityManager.close();
    factory.close();
    dataSource.setShutdownDatabase("shutdown");
    // test Derby Database to see if it shutdown "normally"
    try {
      dataSource.getConnection();
    } catch (final SQLException e) {
      // make sure we get the correct error codes
      if (e.getErrorCode() != 45000 || !"08006".equals(e.getSQLState())) {
        throw e;
      }
      isShutDown_ = true;
      return;
    }
    throw new IllegalStateException(
        "Datasource connection was intact after attempting to shutdown");
  }

  /**
   * Resets the derby database (clearing all table data), does NOT shut it down and restart. Be
   * careful with this method, not sure how it works under there. (magic) Relevant javadocs:
   * <p>
   * http://grepcode.com/file/repo1.maven.org/maven2/org.eclipse.persistence/eclipselink/2.5.1/org/eclipse/persistence/internal/jpa/EntityManagerFactoryImpl.java
   * http://www.eclipse.org/eclipselink/api/2.1/org/eclipse/persistence/jpa/JpaHelper.html
   */
  public void reset() {
    final Map<String, String> properties = new HashMap<String, String>();
    // specify ddl generation on startup
    properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
    // tell the ddl to be output to the database
    properties.put("eclipselink.ddl-generation.output-mode", "database");
    // this causes DDL generation to occur on refreshMetadata rather than wait
    // until an em is obtained
    properties.put("eclipselink.deploy-on-startup", "true");
    // cast factory into an "eclipselink" factory
    JpaHelper.getEntityManagerFactory(factory).refreshMetadata(properties);
    // any new entity manager created after refreshingmetadata will reflect the
    // properties
    entityManager = factory.createEntityManager();
  }

  // package level DAO methods
  /**
   * Returns a criteriaBuilder from the entityManager.
   * 
   * @return a usable, new criteriaBuilder from the database's entityManager.
   */
  CriteriaBuilder getCriteriaBuilder() {
    return entityManager.getCriteriaBuilder();
  }

  /**
   * Persists an object into the database. If the object given is null, or is already contained in
   * the entityManager, no action is taken and false is returned. Otherwise, a transaction is
   * started and the entityManager attempts to add the object to the persistence context and
   * database.
   * 
   * @param object
   *          the object to persist in the database.
   * @return a boolean denoting whether the object is already in the entityManager, or if the
   *         persistence was a success
   * @throws IllegalArgumentException
   *           if the object passed was not an entity.
   * @throws IllegalStateException
   *           if a null object is given
   */
  boolean persist(Object object) {
    checkShutdown();
    if (object == null || entityManager.contains(object)) {
      return false;
    }
    // EntityExists exceptions will never be thrown, because it
    // is checked for in the method (and returns false). TransactionRequired exceptions will never
    // be
    // thrown because it is surrounded in a transaction begin and commit, and this program is
    // single-threaded.
    entityManager.getTransaction().begin();
    entityManager.persist(object);
    entityManager.getTransaction().commit();
    return true;
  }

  /**
   * This method flushes the database, updating entries with the persistence context. This method is
   * intended to be used to save edits to data within objects.
   * 
   * @throws PersistenceException
   *           if flush fails.
   * 
   */
  void update() throws PersistenceException {
    checkShutdown();
    entityManager.getTransaction().begin();
    entityManager.flush();
    entityManager.getTransaction().commit();
  }

  /**
   * Removes a managed entity object from the database. If the entityManager does not contain the
   * object, or it is null, false is returned to avoid further exceptions. Otherwise, a transaction
   * is started and the entityManager attempts to remove the object from the persistence context and
   * database.
   * 
   * @param object
   *          the object to remove
   * @return a boolean true if the object is removed and false if entityManager could not find the
   *         object.
   * @throws IllegalArgumentException
   *           if the object passed was not an entity.
   */
  boolean remove(Object object) {
    checkShutdown();
    if (object == null || !entityManager.contains(object)) {
      return false;
    }
    entityManager.getTransaction().begin();
    entityManager.remove(object);
    entityManager.getTransaction().commit();
    return true;
  }

  /**
   * Checks if the instance is a managed entity instance belonging to the current persistence
   * context.
   * <p>
   * Reference javadocs:
   * <p>
   * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html
   * 
   * @param object
   *          to search the persistence context for
   * @return a boolean denoting whether the search for the object was successful
   * @throws IllegalArgumentException
   *           if the object given was not an entity.
   */
  boolean contains(Object object) {
    checkShutdown();
    return entityManager.contains(object);
  }

  /**
   * Finds an entity object for the given class and id.
   * 
   * @param clazz
   *          The class of the object to be found.
   * @param id
   *          The ID of the object to be found (UUID), specifications are under 'BaseEntity'
   *          javadocs.
   * @return the found entity instance or null if the entity does not exist or a null id was given
   * @throws IllegalArgumentException
   *           if class given by clazz argument is not an Entity
   */
  <T extends BaseEntity> T find(Class<T> clazz, String id) {
    checkShutdown();
    if (id == null || clazz == null) {
      return null;
    }
    return this.entityManager.find(clazz, id);
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
  <T extends BaseEntity> T getSingleResult(CriteriaQuery<T> query) {
    checkShutdown();
    return this.entityManager.createQuery(query).getSingleResult();
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
  <T extends BaseEntity> List<T> getMultiResultFrom(CriteriaQuery<T> query) {
    checkShutdown();
    return this.entityManager.createQuery(query).getResultList();
  }

  <T extends BaseEntity> TypedQuery<T> getTypedQuery(CriteriaQuery<T> query) {
    return this.entityManager.createQuery(query);
  }

  // public metaData methods
  /**
   * Uses connection metaData to give a String representation of a Table in the database.
   * 
   * @param givenTableName
   *          the name of the table in the database
   * @param ignoreId
   *          if true, the table does not take the id value as part of the String value returned
   * @return a string representation of the given table in the database
   * 
   */
  public String getTableSnapshot(String givenTableName, boolean ignoreId) {
    String databaseString = "";
    try (Statement st = conn.createStatement()) {
      try (ResultSet mrs = conn.getMetaData().getTables(null, null, null, new String[] {
          "TABLE" })) {
        while (mrs.next()) {
          final String tableName = mrs.getString(3);
          // if not table we're looking for, continue
          if (!tableName.equals(givenTableName)) {
            continue;
          }
          databaseString += "\nTable Name: " + tableName + "\n";
          try (ResultSet rs = st.executeQuery("select * from \"" + tableName + "\"")) {
            final ResultSetMetaData metadata = rs.getMetaData();
            while (rs.next()) {
              for (int i = 0; i < metadata.getColumnCount(); i++) {
                // if we're not checking id, skip the id column
                if (ignoreId && metadata.getColumnLabel(i + 1).toLowerCase().contains("id")) {
                  continue;
                }
                databaseString += " Row:\n";
                databaseString += "    Column Name: " + metadata.getColumnLabel(i + 1) + ",  \n";
                databaseString += "    Column Type: " + metadata.getColumnTypeName(i + 1) + ",  \n";
                databaseString += "    Column Value: " + rs.getObject(i + 1) + "\n";
              }
            }
          } catch (final SQLException sqle) {
            // TODO ERROR HANDLING
            // Main.addError("Selecting table data went wrong for: " +
            // tableName);
            // Main.addError(sqle.getMessage());
          }
        }
      } catch (final SQLException sqle) {
        // TODO ERROR HANDLING
        // Main.addError("Error inside ResultSet");
        // Main.addError(sqle.getMessage());
      }
    } catch (final SQLException sqle) {
      // TODO ERROR HANDLING
      // Main.addError("Error inside statement..DB connection likely dead");
      // Main.addError(sqle.getMessage());
    }
    return databaseString;
  }

  /**
   * Uses connection metaData to give a String representation of the database.
   * 
   * @param ignoreIds
   *          if true, ignores the id values of all tables when returning the string
   * @return a String representation of the database
   */
  public String getSnapshot(boolean ignoreIds) {
    String databaseString = "";
    try (Statement st = conn.createStatement()) {
      try (ResultSet mrs = conn.getMetaData().getTables(null, null, null, new String[] {
          "TABLE" })) {
        while (mrs.next()) {
          final String tableName = mrs.getString(3);
          databaseString += "\nTable Name: " + tableName + "\n";
          try (ResultSet rs = st.executeQuery("select * from \"" + tableName + "\"")) {
            final ResultSetMetaData metadata = rs.getMetaData();
            while (rs.next()) {
              for (int i = 0; i < metadata.getColumnCount(); i++) {
                // if we're not checking id, skip the id column
                if (ignoreIds && metadata.getColumnLabel(i + 1).toLowerCase().contains("id")) {
                  continue;
                }
                databaseString += " Row:\n";
                databaseString += "    Column Name: " + metadata.getColumnLabel(i + 1) + ",  \n";
                databaseString += "    Column Type: " + metadata.getColumnTypeName(i + 1) + ",  \n";
                databaseString += "    Column Value: " + rs.getObject(i + 1) + "\n";
              }
            }
          } catch (final SQLException sqle) {
            // TODO ERROR HANDLING
            // Main.addError("Selecting table data went wrong for: " +
            // tableName);
            // Main.addError(sqle.getMessage());
          }
        }
      } catch (final SQLException sqle) {
        // TODO ERROR HANDLING
        // Main.addError("Error inside ResultSet");
        // Main.addError(sqle.getMessage());
      }
    } catch (final SQLException sqle) {
      // TODO ERROR HANDLING
      // Main.addError("Error inside statement..DB connection likely dead");
      // Main.addError(sqle.getMessage());
    }
    return databaseString;
  }

  /**
   * Prints all Table names in the database to the console.
   */
  public void printTableNames() {
    try {
      final Statement st = conn.createStatement();
      final ResultSet mrs = conn.getMetaData().getTables(null, null, null, new String[] {
          "TABLE" });
      conn.setAutoCommit(false);
      while (mrs.next()) {
        final String tableName = mrs.getString(3);
        if (!tableName.equals("SEQUENCE")) {
          // automatically generated table
          System.out.println("Table Name: " + tableName);
        }
      }
      st.close();
      mrs.close();
    } catch (final SQLException sqle) {
      // TODO ERROR HANDLING
      // Main.addError("Printing table Names failed");
      // Main.addError(sqle.getMessage());
    }
  }

  /**
   * Prints the Table name and Data from the given table name.
   * 
   * @param tableName
   *          the name of the table in the database
   */
  public void printTable(String tableName) {
    try {
      final Statement st = conn.createStatement();
      final ResultSet mrs = conn.getMetaData().getTables(null, null, null, new String[] {
          "TABLE" });
      while (mrs.next()) {
        final String metaDataTableName = mrs.getString(3);
        if (!metaDataTableName.equals(tableName)) {
          continue;
        }
        System.out.println("\n\n\n\nTable Name: " + tableName);
        try (ResultSet rs = st.executeQuery("select * from \"" + tableName + "\"")) {
          final ResultSetMetaData metadata = rs.getMetaData();
          while (rs.next()) {
            System.out.println(" Row:");
            for (int i = 0; i < metadata.getColumnCount(); i++) {
              System.out.println("    Column Name: " + metadata.getColumnLabel(i + 1) + ",  ");
              System.out.println("    Column Type: " + metadata.getColumnTypeName(i + 1) + ",  ");
              final Object value = rs.getObject(i + 1);
              System.out.println("    Column Value: " + value + "\n");
            }
          }
          rs.close();
        } catch (final SQLException sqle) {
          // TODO ERROR HANDLING
          // Main.addError("Selecting table data went wrong for: " + tableName);
          // Main.addError(sqle.getMessage());
        }
      }
      st.close();
      mrs.close();
    } catch (final SQLException sqle) {
      // TODO ERROR HANDLING
      // Main.addError("Printing tables failed");
      // Main.addError(sqle.getMessage());
    }
  }

  /**
   * Prints all Table Names and Data in the database to the console.
   */
  public void printTables() {
    try {
      final Statement st = conn.createStatement();
      final ResultSet mrs = conn.getMetaData().getTables(null, null, null, new String[] {
          "TABLE" });
      // Database.getConnection().setAutoCommit(false);
      while (mrs.next()) {
        final String tableName = mrs.getString(3);
        System.out.println("\n\n\n\nTable Name: " + tableName);
        try (ResultSet rs = st.executeQuery("select * from \"" + tableName + "\"")) {
          final ResultSetMetaData metadata = rs.getMetaData();
          while (rs.next()) {
            System.out.println(" Row:");
            for (int i = 0; i < metadata.getColumnCount(); i++) {
              System.out.println("    Column Name: " + metadata.getColumnLabel(i + 1) + ",  ");
              System.out.println("    Column Type: " + metadata.getColumnTypeName(i + 1) + ",  ");
              final Object value = rs.getObject(i + 1);
              System.out.println("    Column Value: " + value + "\n");
            }
          }
          rs.close();
        } catch (final SQLException sqle) {
          // TODO ERROR HANDLING
          // Main.addError("Selecting table data went wrong for: " + tableName);
          // Main.addError(sqle.getMessage());
        }
      }
      st.close();
      mrs.close();
    } catch (final SQLException sqle) {
      // TODO ERROR HANDLING
      // Main.addError("Printing tables failed");
      // Main.addError(sqle.getMessage());
    }
  }

  /**
   * Prints the class names of all EntityManager managed entities.
   */
  public void printManagedEntities() {
    for (final EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
      final String className = entity.getName();
      // System.out.println("Trying select * from: " + className);
      final Query query = entityManager.createQuery("From " + className + " c");
      final Iterator<?> test = query.getResultList().iterator();
      if (!query.getResultList().isEmpty()) {
        System.out.println("Class: " + className);
      }
      while (test.hasNext()) {
        System.out.println("      " + test.next().toString());
      }
    }
  }

  // getters
  /**
   * Return the database name used to create a dataSource with.
   * 
   * @return the database name
   */
  public String getName() {
    return this.DB_NAME;
  }

  /**
   * Return the persistence unit name used to create an entitymanagerfactory with.
   * 
   * @return the persistence unit name
   */
  public String getPersistenceUnitName() {
    return this.PERSISTENCE_UNIT_NAME;
  }

  public ResultSet getResultSetFor(String baseTable, List<String> tableNames) {

    try {
      conn.setAutoCommit(false);
      String SQL = "";
      // for (int i = 0; i < tableNames.size(); i++) {
      SQL += "select * from \"" + baseTable + "\" INNER JOIN \"" + tableNames.get(0) + "\" ON \""
          + baseTable + "\".ID = \"" + tableNames.get(0) + "\"." + baseTable + "_ID";
      // }
      // SQL = "select * from \"Employee\" INNER JOIN \"EmployeeWage\" ON \"Employee\".ID =
      // \"EmployeeWage\".EMPLOYEE_ID";
      ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY).executeQuery(SQL);
      return rs;
    } catch (SQLException sqle) {
      // TODO add error handling
      return null;
    }
  }
}
