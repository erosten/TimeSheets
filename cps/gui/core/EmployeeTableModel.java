
package cps.gui.core;

import cps.core.ProgramPortal;
import cps.core.model.employee.Employee;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EmployeeTableModel extends AbstractTableModel {

  private static final int COLUMN_ID = 0;
  private static final int COLUMN_ABB = 1;
  private static final int COLUMN_NAME = 2;
  private static final int COLUMN_LANG = 3;
  private static final int COLUMN_TERMINATED = 4;
  private static final int COLUMN_COUNT = 5;

  // private boolean connectedToDatabase = false;
  private ProgramPortal portal;
  // private List<String> employeeIds;
  private List<Employee> employees;

  public EmployeeTableModel(ProgramPortal pp) {
    this.portal = pp;
    employees = portal.findAllEmployees();
    fireTableStructureChanged();
  }

  @Override
  public Class<?> getColumnClass(int column) throws IllegalStateException {
    switch (column) {
      case COLUMN_ID :
        return String.class;
      case COLUMN_ABB :
        return String.class;
      case COLUMN_NAME :
        return String.class;
      case COLUMN_LANG :
        return BigDecimal.class;
      case COLUMN_TERMINATED :
        return String.class;
      default :
        return Object.class;
    }
  }

  @Override
  public int getColumnCount() throws IllegalStateException {
    return COLUMN_COUNT;
  }

  @Override
  public String getColumnName(int column) throws IllegalStateException {
    switch (column) {
      case COLUMN_ID :
        return "ID";
      case COLUMN_ABB :
        return "Abbreviation";
      case COLUMN_NAME :
        return "Full Name";
      case COLUMN_LANG :
        return "Lang Bonus";
      case COLUMN_TERMINATED :
        return "Termination Status";
      default :
        return "";
    }
  }

  @Override
  public int getRowCount() throws IllegalStateException {
    return employees.size();
  }

  @Override
  public Object getValueAt(int row, int column) throws IllegalStateException {
    switch (column) {
      case COLUMN_ID :
        return employees.get(row).getId();
      case COLUMN_ABB :
        return employees.get(row).getAbbreviation();
      case COLUMN_NAME :
        return employees.get(row).getFullName();
      case COLUMN_LANG :
        return employees.get(row).getLanguageBonus();
      case COLUMN_TERMINATED :
        return employees.get(row).isTerminated();
      default :
        return "";
    }
  }

  @Override
  public void fireTableDataChanged() {
    employees = portal.findAllEmployees();
    super.fireTableDataChanged();
  }

}
