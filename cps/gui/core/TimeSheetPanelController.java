
package cps.gui.core;

import cps.core.ProgramPortal;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobTime;
import cps.core.model.timeSheet.Time;
import cps.core.model.timeSheet.TimeSheet;
import cps.gui.CreateNewJob;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jdatepicker.JDatePicker;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TimeSheetPanelController implements ActionListener {

  private ProgramPortal portal;
  private TimeSheetPanel timeSheetPanel;

  // field identifiers
  static final int CODE_CHANGE = 0;
  static final int TIME_IN_CHANGE = 1;
  static final int TIME_OUT_CHANGE = 2;
  static final int TRAVEL_CHANGE = 3;

  public TimeSheetPanelController(ProgramPortal portal) {
    this.portal = portal;

  }

  public void setTimeSheetPanel(TimeSheetPanel tp) {
    timeSheetPanel = tp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand.equals("yearList")) {
      timeSheetPanel.updateTimeSheetSelection();
    } else if (actionCommand.equals("tsList")) {
      timeSheetPanel.displaySelectedTimeSheet();
    } else if (actionCommand.equals("jobList")) {
      timeSheetPanel.setSelectedJob();
      timeSheetPanel.displaySelectedJob();
    } else if (actionCommand.equals("jobTimeList")) {
      timeSheetPanel.displaySelectedTime();
    } else if (actionCommand.equals("addnew")) {
      addNewAction();
    } else if (actionCommand.equals("delete")) {
      deleteAction();
    } else if (actionCommand.equals("update")) {
      updateAction();
    } else {
      JOptionPane.showMessageDialog(null, "Action Command not recognized", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void addNewAction() {
    if (timeSheetPanel.getSelectedTimeSheet() == null) {
      JOptionPane.showMessageDialog(null, "Please Create a TimeSheet", "Warning",
          JOptionPane.WARNING_MESSAGE);
    }
    CreateNewJob.createAndShowFrame(portal, MainFrame.frame, timeSheetPanel.getSelectedTimeSheet());
    timeSheetPanel.update();
  }

  private void deleteAction() {
    JobEntry job = timeSheetPanel.getSelectedJob();
    if (job == null) {
      JOptionPane.showMessageDialog(null, "No Job Selected", "Warning",
          JOptionPane.WARNING_MESSAGE);
    } else {
      int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + job
          .getCode() + "?", "Warning", JOptionPane.YES_NO_OPTION);
      if (reply == JOptionPane.YES_OPTION) {
        portal.removeJobEntry(job.getId());
        timeSheetPanel.update();
      }
    }
  }

  private void updateAction() {
    JobEntry job = timeSheetPanel.getSelectedJob();
    for (JTextField field : timeSheetPanel.getTextFields()) {
      switch ((int) field.getClientProperty("FIELD")) {
        case CODE_CHANGE :
          if (!field.getText().equals(job.getCode())) {
            job.setCode(field.getText());
          }
          break;
        case TRAVEL_CHANGE :
          if (!field.getText().equals(job.getTravel())) {
            job.setTravel(field.getText());
          }
          break;
        case TIME_IN_CHANGE :
          JobTime jobTimeIn = timeSheetPanel.getTime();
          if (!field.getText().equals(jobTimeIn.getTimeIn().getTimeString())) {
            int startHours = Integer.parseInt(field.getText().substring(0, 2));
            final int startMinutes = Integer.parseInt(field.getText().substring(2));
            final LocalTime timeIn = new LocalTime(startHours, startMinutes);
            job.setTime(jobTimeIn, new Time(timeIn), jobTimeIn.getTimeOut());
          }
          break;
        case TIME_OUT_CHANGE :
          JobTime jobTimeOut = timeSheetPanel.getTime();
          if (!field.getText().equals(jobTimeOut.getTimeOut().getTimeString())) {
            int startHours = Integer.parseInt(field.getText().substring(0, 2));
            final int startMinutes = Integer.parseInt(field.getText().substring(2));
            final LocalTime timeOut = new LocalTime(startHours, startMinutes);
            job.setTime(jobTimeOut, jobTimeOut.getTimeIn(), new Time(timeOut));
          }
          break;
        default :
          break;
      }
    }
    JDatePicker datePicker = timeSheetPanel.getDate();
    int day = datePicker.getModel().getDay();
    // datePicker months are 0 based
    // localDate months are 1 based
    int month = datePicker.getModel().getMonth() + 1;
    int year = datePicker.getModel().getYear();
    LocalDate pickedDate = new LocalDate(year, month, day);
    if (!job.getDate().equals(pickedDate)) {
      job.setDate(pickedDate);
    }
    portal.updateJobEntry(job);
    timeSheetPanel.update();
  }

  public List<TimeSheet> getTimeSheets() {
    return portal.findAllTimeSheets();
  }
}
