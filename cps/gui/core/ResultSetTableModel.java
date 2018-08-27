
package cps.gui.core;

import cps.core.db.frame.DerbyDatabase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 4177640582098323288L;
  // connection unused
  // private Connection connection;
  // private final Statement statement;
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int numberOfRows;

  // private boolean connectedToDatabase = false;
  private DerbyDatabase DDB;

  public ResultSetTableModel(DerbyDatabase DDB, String baseTable, List<String> tableNames)
      throws SQLException {
    this.DDB = DDB;
    resultSet = DDB.getResultSetFor(baseTable, tableNames);
    try {
      metaData = resultSet.getMetaData();
    } catch (SQLException e) {
      // TODO Error Handling
      e.printStackTrace();
    }

    // statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
    // ResultSet.CONCUR_READ_ONLY);

    if (!DDB.isConnected()) {
      throw new IllegalStateException("Not Connected to Database");
    }

    // resultSet = statement.executeQuery(query);
    // metaData = resultSet.getMetaData();
    try {
      resultSet.last();
    } catch (SQLException e) {
      // TODO Error Handling
      e.printStackTrace();
    }
    try {
      numberOfRows = resultSet.getRow();
    } catch (SQLException e) {
      // TODO Error Handling
      e.printStackTrace();
    }

    fireTableStructureChanged();

  }

  @Override
  public Class<?> getColumnClass(int column) throws IllegalStateException {
    try {
      if (!DDB.isConnected()) {
        throw new IllegalStateException("Not Connected to Database");
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Not Connected to Database");
    }
    try {
      String className = metaData.getColumnClassName(column + 1);
      return Class.forName(className);
    } catch (ClassNotFoundException | SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return Object.class;
  }

  @Override
  public int getColumnCount() throws IllegalStateException {
    try {
      if (!DDB.isConnected()) {
        throw new IllegalStateException("Not Connected to Database");
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Not Connected to Database");
    }

    try {
      return metaData.getColumnCount();
    } catch (SQLException sex) {
      System.out.println(sex.getMessage());
    }
    return 0;
  }

  @Override
  public String getColumnName(int column) throws IllegalStateException {
    try {
      if (!DDB.isConnected()) {
        throw new IllegalStateException("Not Connected to Database");
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Not Connected to Database");
    }

    try {
      return metaData.getColumnName(column + 1);
    } catch (SQLException sex) {
      System.out.println(sex.getMessage());
    }

    return "";
  }

  @Override
  public int getRowCount() throws IllegalStateException {
    try {
      if (!DDB.isConnected()) {
        throw new IllegalStateException("Not Connected to Database");
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Not Connected to Database");
    }
    return numberOfRows;
  }

  @Override
  public Object getValueAt(int row, int column) throws IllegalStateException {
    try {
      if (!DDB.isConnected()) {
        throw new IllegalStateException("Not Connected to Database");
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {

      resultSet.absolute(row + 1);
      return resultSet.getObject(column + 1);
    } catch (SQLException sex) {
      System.out.println(sex.getMessage());
    }

    return "";
  }
  //
  // public void disconnectFromDatabase() {
  // if (connectedToDatabase) {
  // try {
  // resultSet.close();
  // statement.close();
  // connection.close();
  // } catch (SQLException sex) {
  // System.out.println(sex.getMessage());
  // } finally {
  // connectedToDatabase = false;
  // }
  // }
  // }
}
