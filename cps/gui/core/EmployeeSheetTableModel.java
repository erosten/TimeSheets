
package cps.gui.core;

import cps.core.model.frame.Wage;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.Time;
import cps.excel.TimeSheet.IntPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.joda.time.LocalDate;

public class EmployeeSheetTableModel extends AbstractTableModel {

  private final int COLUMN_DATE = 0;
  private final int COLUMN_CODE = 1;
  private final int COLUMN_TIME_IN = 2;
  private final int COLUMN_TIME_OUT = 3;
  private int COLUMN_TRAVEL;
  private int COLUMN_COUNT;
  private int ROW_COUNT;
  private String longestCode = "";

  private List<JobEntry> jobs;
  private List<Wage> jobWages;
  private List<IntPair> newJobRows = new ArrayList<>();
  private List<Integer> noJobRows = new ArrayList<>();

  public EmployeeSheetTableModel(EmployeeSheet employeeSheet) {
    // this.portal = pp;
    jobs = employeeSheet.getJobs();
    jobWages = employeeSheet.getWagesUsed();
    COLUMN_TRAVEL = COLUMN_TIME_OUT + jobWages.size() * 3 + 1;
    COLUMN_COUNT = COLUMN_TRAVEL + 1;
    for (JobEntry je : jobs) {
      int jobStart = ROW_COUNT + 1;
      newJobRows.add(new IntPair(jobStart - 1, jobs.indexOf(je)));
      ROW_COUNT = ROW_COUNT + je.getTimes().size();
      if (je.hasStdDeduction()) {
        ROW_COUNT = ROW_COUNT + 1;
      }
      for (int i = jobStart; i <= ROW_COUNT; i++) {
        noJobRows.add(i + 1);
      }
      // save longest job code for column sizing
      if (je.getCode().length() > this.longestCode.length()) {
        longestCode = je.getCode();
      }
    }

    fireTableStructureChanged();
  }

  @Override
  public Class<?> getColumnClass(int column) throws IllegalStateException {
    switch (column) {
      case COLUMN_DATE :
        return LocalDate.class;
      case COLUMN_CODE :
        return String.class;
      case COLUMN_TIME_IN :
        return Time.class;
      case COLUMN_TIME_OUT :
        return Time.class;
      default :
        if (column == COLUMN_TRAVEL) {
          return String.class;
        }
        if (column < COLUMN_COUNT) {
          return BigDecimal.class;
        } else {
          return Object.class;
        }
    }
  }

  @Override
  public int getColumnCount() throws IllegalStateException {
    return COLUMN_COUNT;
  }

  @Override
  public String getColumnName(int column) throws IllegalStateException {
    switch (column) {
      case COLUMN_DATE :
        return "Date";
      case COLUMN_CODE :
        return "Code";
      case COLUMN_TIME_IN :
        return "Time In";
      case COLUMN_TIME_OUT :
        return "Time Out";
      default :
        if (column == COLUMN_TRAVEL) {
          return "Travel";
        }
        if (column < COLUMN_COUNT) {
          if ((column - 1) % 3 == 0) {
            return "Reg";
          } else if ((column - 2) % 3 == 0) {
            return "OT";
          } else if ((column) % 3 == 0) {
            return "DT";
          }
        }
    }
    return "";
  }

  @Override
  public int getRowCount() throws IllegalStateException {
    return ROW_COUNT;
  }

  @Override
  public Object getValueAt(int row, int column) throws IllegalStateException {
    int jobRow = 0;
    boolean found = false;
    for (IntPair ip : this.newJobRows) {
      if (ip.getX() == row) {
        jobRow = ip.getY();
        found = true;
      }
    }
    if (found == true) {
      JobEntry job = jobs.get(jobRow);
      if (column == COLUMN_DATE) {
        return job.getDateString();
      } else if (column == COLUMN_CODE) {
        return job.getCode();
      } else if (column == COLUMN_TIME_IN) {
        return job.getTimes().get(0).getTimeIn().getTimeString();
      } else if (column == COLUMN_TIME_OUT) {
        return job.getTimes().get(0).getTimeOut().getTimeString();
      } else if (column == COLUMN_TRAVEL) {
        return job.getTravel();
      } else {
        if (column < COLUMN_COUNT) {
          if (job.isMultiLineEntry()) {
            return "";
          }
          // jobWages.indexOf(job.getWage());
          // reg col
          if ((column - 1) % 3 == 0) {
            if (job.getRegularTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 1 + jobWages.indexOf(job.getWage()) * 3) {
              return "";
            } else {
              return job.getRegularTime();
            }
          } else if ((column - 2) % 3 == 0) {
            if (job.getOverTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 2 + jobWages.indexOf(job.getWage()) * 3) {
              return "";
            } else {
              return job.getOverTime();
            }
          } else if ((column) % 3 == 0) {
            if (job.getDoubleTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 3 + jobWages.indexOf(job.getWage()) * 3) {
              return "";
            } else {
              return job.getDoubleTime();
            }
          }
        }
      }
    } else {
      int rowsUntilNewJob = 0;
      // increment down until job row found
      jobRowSearch : for (int i = row; i >= 0; i--) {
        rowsUntilNewJob = rowsUntilNewJob + 1;
        // search pairs to see if a job row is found
        for (IntPair ip : this.newJobRows) {
          if (ip.getX() == i) {
            jobRow = ip.getY();
            break jobRowSearch;
          }
        }
      }
      // job row is found
      if (column == COLUMN_DATE) {
        return "";
      } else if (column == COLUMN_CODE) {
        return "";
      } else if (column == COLUMN_TIME_IN) {
        return jobs.get(jobRow).getTimes().get(rowsUntilNewJob - 1).getTimeIn().getTimeString();
      } else if (column == COLUMN_TIME_OUT) {
        return jobs.get(jobRow).getTimes().get(rowsUntilNewJob - 1).getTimeOut().getTimeString();
      } else if (column == COLUMN_TRAVEL) {
        return jobs.get(jobRow).getTravel();
      } else {
        if (column < COLUMN_COUNT) {
          // reg col
          if ((column - 1) % 3 == 0) {
            if (jobs.get(jobRow).getRegularTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 1 + jobWages.indexOf(jobs.get(jobRow).getWage()) * 3) {
              return "";
            } else {
              return jobs.get(jobRow).getRegularTime();
            }
          } else if ((column - 2) % 3 == 0) {
            if (jobs.get(jobRow).getOverTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 2 + jobWages.indexOf(jobs.get(jobRow).getWage()) * 3) {
              return "";
            } else {
              return jobs.get(jobRow).getOverTime();
            }
          } else if ((column) % 3 == 0) {
            if (jobs.get(jobRow).getDoubleTime().compareTo(BigDecimal.ZERO) == 0) {
              return "";
            } else if (column != 3 + 3 + jobWages.indexOf(jobs.get(jobRow).getWage()) * 3) {
              return "";
            } else {
              return jobs.get(jobRow).getDoubleTime();
            }
          }
        }
      }
    }
    return "";
  }

  public String getLongestJobCode() {
    return this.longestCode;
  }

  public void fireTableDataChanged(EmployeeSheet employeeSheet) {
    jobs = employeeSheet.getJobs();
    jobWages = employeeSheet.getWagesUsed();
    super.fireTableDataChanged();
  }

}
