
package cps.gui.core;

import cps.console.tools.Display;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobTime;
import cps.core.model.timeSheet.TimeSheet;
import cps.gui.tools.GridBagUtil;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;

public class TimeSheetPanel extends JPanel {

  private static final long serialVersionUID = -7841981420093819850L;
  // private static final NumberFormat nf = NumberFormat.getCurrencyInstance();
  private final Border labelBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
  DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
  // controller
  private static TimeSheetPanelController tpc;

  // timeSheet selectors
  private JComboBox<String> timeSheetYearsList = new JComboBox<>();
  private DefaultComboBoxModel<TimeSheet> tsListModel = new DefaultComboBoxModel<>();
  private JComboBox<TimeSheet> timeSheetsList = new JComboBox<>(tsListModel);
  // job selector
  private DefaultComboBoxModel<JobEntry> jobListModel = new DefaultComboBoxModel<>();
  private JComboBox<JobEntry> empJobsList = new JComboBox<>(jobListModel);
  // timeSheet display components
  private JTextArea coverText = new JTextArea(40, 80);
  private JScrollPane timeSheetDataScrollPane;
  private JTabbedPane tSheet = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
  private TimeSheet displayedTsheet;
  private JobEntry displayedJob;
  // job data pane
  private JPanel jobDataScrollPane = new JPanel();
  // job data pane components
  private JLabel codeLabel;
  private JTextField codeField;
  private JDatePicker datePicker;
  private JLabel dateLabel;
  private DefaultComboBoxModel<JobTime> jobTimeListModel = new DefaultComboBoxModel<>();
  private JComboBox<JobTime> jobTimesList = new JComboBox<>(jobTimeListModel);
  private JLabel jobTimesLabel;
  private JLabel timeInLabel;
  private JTextField timeInField;
  private JLabel timeOutLabel;
  private JTextField timeOutField;
  private JLabel totalTimeLabel;
  private JTextField totalTimeField;
  private JLabel regLabel;
  private JTextField regField;
  private JLabel otLabel;
  private JTextField otField;
  private JLabel dtLabel;
  private JTextField dtField;
  private JLabel travelLabel;
  private JTextField travelField;
  // buttons
  private JPanel buttonPane = new JPanel();
  private JButton addNewJobButton;
  private JButton deleteJobButton;
  private JButton updateJobButton;

  TimeSheetPanel(TimeSheetPanelController tpController) {
    super(new GridBagLayout());

    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    tpc = tpController;
    tpc.setTimeSheetPanel(this);
    this.createTimeSheetPanel();
    this.addComponents();
  }

  private void createTimeSheetPanel() {
    List<TimeSheet> timeSheets = tpc.getTimeSheets();
    timeSheetYearsList.setActionCommand("yearList");
    List<String> timeSheetYearArrayList = new ArrayList<>();
    for (TimeSheet ts : timeSheets) {
      String yearString = Integer.toString(ts.getStartDate().getYear());
      if (!timeSheetYearArrayList.contains(yearString)) {
        timeSheetYearArrayList.add(yearString);
        timeSheetYearsList.addItem(yearString);
      }
    }
    timeSheetYearsList.addActionListener(tpc);
    timeSheetsList.setActionCommand("tsList");
    timeSheetsList.addActionListener(tpc);
    if (!timeSheetYearArrayList.isEmpty()) {
      timeSheetYearsList.setSelectedIndex(0);
      timeSheetsList.setSelectedIndex(0);

    }
    empJobsList.setActionCommand("jobList");
    empJobsList.addActionListener(tpc);
    // set size otherwise tabs on bottom will float around
    empJobsList.setMinimumSize(new Dimension(150, 50));
    empJobsList.setMaximumSize(new Dimension(150, 50));
    // add job fields
    jobDataScrollPane.setLayout(new GridBagLayout());
    codeField = new JTextField(10);
    codeField.setEditable(true);
    codeField.putClientProperty("FIELD", TimeSheetPanelController.CODE_CHANGE);
    codeLabel = new JLabel("Code: ");
    codeLabel.setBorder(labelBorder);
    codeLabel.setLabelFor(codeField);
    dateLabel = new JLabel("Date: ");
    dateLabel.setLabelFor(datePicker);
    datePicker = new JDatePicker(new UtilDateModel());
    jobTimesList.addActionListener(tpc);
    jobTimesList.setActionCommand("jobTimeList");
    jobTimesLabel = new JLabel("Times: ");
    jobTimesLabel.setLabelFor(jobTimesList);
    jobTimesLabel.setBorder(labelBorder);
    timeInField = new JTextField(4);
    timeInField.setEditable(true);
    timeInField.putClientProperty("FIELD", TimeSheetPanelController.TIME_IN_CHANGE);
    timeInLabel = new JLabel("Time In: ");
    timeInLabel.setBorder(labelBorder);
    timeInLabel.setLabelFor(timeInField);
    timeOutField = new JTextField(4);
    timeOutField.setEditable(true);
    timeOutField.putClientProperty("FIELD", TimeSheetPanelController.TIME_OUT_CHANGE);
    timeOutLabel = new JLabel("Time Out: ");
    timeOutLabel.setBorder(labelBorder);
    timeOutLabel.setLabelFor(timeOutField);
    totalTimeField = new JTextField(3);
    totalTimeField.setEditable(false);
    totalTimeLabel = new JLabel("Total Time: ");
    totalTimeLabel.setBorder(labelBorder);
    totalTimeLabel.setLabelFor(totalTimeField);
    regLabel = new JLabel("Reg: ");
    regLabel.setLabelFor(regField);
    regField = new JTextField(3);
    regField.setEditable(false);
    otLabel = new JLabel("OT: ");
    otLabel.setLabelFor(otField);
    otField = new JTextField(3);
    otField.setEditable(false);
    dtLabel = new JLabel("DT: ");
    dtLabel.setLabelFor(dtField);
    dtField = new JTextField(3);
    dtField.setEditable(false);
    travelLabel = new JLabel("Travel: ");
    travelLabel.setLabelFor(travelField);
    travelField = new JTextField(5);
    travelField.setEditable(true);
    travelField.putClientProperty("FIELD", TimeSheetPanelController.TRAVEL_CHANGE);
    // add buttons
    buttonPane.setLayout(new GridBagLayout());
    addNewJobButton = new JButton("Add New Job(s)");
    addNewJobButton.setActionCommand("addnew");
    addNewJobButton.addActionListener(tpc);
    deleteJobButton = new JButton("Delete");
    deleteJobButton.setActionCommand("delete");
    deleteJobButton.addActionListener(tpc);
    updateJobButton = new JButton("Update");
    updateJobButton.setActionCommand("update");
    updateJobButton.addActionListener(tpc);
  }

  void updateTimeSheetSelection() {
    tsListModel.removeAllElements();
    for (TimeSheet timeSheet : tpc.getTimeSheets()) {
      if (((String) timeSheetYearsList.getSelectedItem()).equals(Integer.toString(timeSheet
          .getStartDate().getYear()))) {
        tsListModel.addElement(timeSheet);
      }
    }
  }

  void setSelectedJob() {
    displayedJob = jobListModel.getElementAt(jobListModel.getIndexOf(jobListModel
        .getSelectedItem()));
  }

  JobEntry getSelectedJob() {
    return displayedJob;
  }

  void populateJobList(EmployeeSheet es) {
    clearJobList();
    clearJobTimes();
    for (JobEntry jobEntry : es.getJobs()) {
      jobListModel.addElement(jobEntry);
    }
  }

  private void clearJobList() {
    jobListModel.removeAllElements();
  }

  private void clearJobTimes() {
    // code and date are automatically set with new selected entry
    jobTimesList.removeActionListener(tpc);
    jobTimesList.removeAllItems();
    jobTimesList.addActionListener(tpc);

  }

  void setSelectedTimeSheet() {
    displayedTsheet = tsListModel.getElementAt(tsListModel.getIndexOf(tsListModel
        .getSelectedItem()));
  }

  TimeSheet getSelectedTimeSheet() {
    return displayedTsheet;
  }

  private void clearTimeSheetSelection() {
    int numTabs = tSheet.getTabCount();
    if (numTabs > 1) {
      for (int i = numTabs; i > 1; i--) {
        tSheet.removeTabAt(i - 1);
      }
    }
  }

  void displaySelectedJob() {
    clearJobTimes();
    if (displayedJob != null) {
      codeField.setText(displayedJob.getCode());
      datePicker.getModel().setDay(displayedJob.getDate().getDayOfMonth());
      datePicker.getModel().setMonth(displayedJob.getDate().getMonthOfYear() - 1);
      datePicker.getModel().setYear(displayedJob.getDate().getYear());
      datePicker.getModel().setSelected(true);
      List<JobTime> jobTimes = displayedJob.getTimes();
      // must remove and add action listener or concurrentmodification is thrown
      jobTimesList.removeActionListener(tpc);
      for (JobTime jobTime : jobTimes) {
        this.jobTimeListModel.addElement(jobTime);
      }
      jobTimesList.addActionListener(tpc);
      if (jobTimesList.getItemCount() > 0) {
        jobTimesList.setSelectedIndex(0);
      }
      displaySelectedTime();
      travelField.setText(displayedJob.getTravel());
    }
  }

  void displaySelectedTime() {
    if (displayedJob != null && jobTimesList.getSelectedIndex() != -1) {
      timeInField.setText(displayedJob.getTimesIn().get(jobTimesList.getSelectedIndex())
          .getTimeString());
      timeOutField.setText(displayedJob.getTimesOut().get(jobTimesList.getSelectedIndex())
          .getTimeString());
      regField.setText(Display.getHoursString(displayedJob.getTimes().get(jobTimesList
          .getSelectedIndex()).getRegular()));
      otField.setText(Display.getHoursString(displayedJob.getTimes().get(jobTimesList
          .getSelectedIndex()).getOver()));
      dtField.setText(Display.getHoursString(displayedJob.getTimes().get(jobTimesList
          .getSelectedIndex()).getDouble()));
      totalTimeField.setText(Display.getHoursString(displayedJob.getTimes().get(jobTimesList
          .getSelectedIndex()).getDuration()));
    }
  }

  private void createNewCover() {
    timeSheetDataScrollPane = new JScrollPane(coverText);
    timeSheetDataScrollPane.setVerticalScrollBarPolicy(
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    timeSheetDataScrollPane.setHorizontalScrollBarPolicy(
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    // text area
    coverText.setFont(new Font("Monospaced", Font.PLAIN, 12));
    coverText.setEditable(false);
    tSheet.insertTab("Cover", null, timeSheetDataScrollPane, "Cover Sheet", 0);
    tSheet.setMnemonicAt(0, KeyEvent.VK_1);
    tSheet.setSelectedIndex(0);
    coverText.setText(Display.display(displayedTsheet));
  }

  void updateDisplay() {
    // call create new cover before clearing selection so there will always be one tab (error is
    // thrown by JTabbedPane otherwise)
    createNewCover();
    clearTimeSheetSelection();
    for (EmployeeSheet eSheet : displayedTsheet.getEmployeeSheets()) {
      JTextArea jta = new JTextArea();
      jta.setFont(new Font("Monospaced", Font.PLAIN, 12));
      jta.setText(Display.display(eSheet));
      jta.setEditable(false);
      JScrollPane jsp = new JScrollPane(jta);
      tSheet.insertTab(eSheet.getEmployee().getAbbreviation(), null, jsp, eSheet.getEmployee()
          .getAbbreviation() + " Sheet", tSheet.getTabCount());
    }
    tSheet.setSelectedIndex(0);
    tSheet.addChangeListener(new ChangeListener() {

      @Override
      // populate job entries
      public void stateChanged(ChangeEvent e) {
        if (tSheet.getSelectedIndex() == 0) {
          return;
        }
        populateJobList(displayedTsheet.getEmployeeSheets().get(tSheet.getSelectedIndex() - 1));
      }
    });

  }

  void displaySelectedTimeSheet() {
    TimeSheet timeSheet = tsListModel.getElementAt(tsListModel.getIndexOf(tsListModel
        .getSelectedItem()));
    // catch the user selecting the same year and action command on the tsList activating before any
    // timeSheets exist
    if (timeSheet == null) {
      return;
    }
    if (timeSheet.equals(displayedTsheet)) {
      return;
    } else {
      setSelectedTimeSheet();
    }
    // call create new cover before clearing selection so there will always be one tab (error is
    // thrown by JTabbedPane otherwise)
    createNewCover();
    clearTimeSheetSelection();
    for (EmployeeSheet eSheet : timeSheet.getEmployeeSheets()) {
      JTextArea jta = new JTextArea();
      jta.setFont(new Font("Monospaced", Font.PLAIN, 12));
      jta.setText(Display.display(eSheet));
      jta.setEditable(false);
      JScrollPane jsp = new JScrollPane(jta);
      tSheet.insertTab(eSheet.getEmployee().getAbbreviation(), null, jsp, eSheet.getEmployee()
          .getAbbreviation() + " Sheet", tSheet.getTabCount());
    }
    tSheet.setSelectedIndex(0);
    tSheet.addChangeListener(new ChangeListener() {

      @Override
      // populate job entries
      public void stateChanged(ChangeEvent e) {
        if (tSheet.getSelectedIndex() == 0) {
          return;
        }
        populateJobList(displayedTsheet.getEmployeeSheets().get(tSheet.getSelectedIndex() - 1));
      }
    });

  }

  private void addComponents() {
    // JPanel actionLabelDescriptionPane = new JPanel();
    // actionLabelDescriptionPane.setLayout(new GridBagLayout());
    GridBagUtil.addComponent(this, timeSheetYearsList, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, timeSheetsList, 1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, empJobsList, 2, 0, 4, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, tSheet, 0, 1, 2, 3, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, jobDataScrollPane, 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, buttonPane, 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    // buttonPane fields
    GridBagUtil.addComponent(buttonPane, addNewJobButton, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(buttonPane, deleteJobButton, 0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(buttonPane, updateJobButton, 0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    // jobDateScrollPane fields
    GridBagUtil.addComponent(jobDataScrollPane, codeLabel, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, codeField, 1, 0, 3, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, dateLabel, 0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, datePicker, 1, 1, 3, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, jobTimesLabel, 0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, jobTimesList, 1, 2, 3, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, timeInLabel, 0, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, timeInField, 1, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, timeOutLabel, 0, 4, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, timeOutField, 1, 4, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, totalTimeLabel, 0, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, totalTimeField, 1, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, regLabel, 2, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, regField, 3, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, otLabel, 2, 4, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, otField, 3, 4, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, dtLabel, 2, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, dtField, 3, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(jobDataScrollPane, travelLabel, 0, 6, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(jobDataScrollPane, travelField, 1, 6, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);

  }

  public List<JTextField> getTextFields() {
    return Arrays.asList(new JTextField[] { codeField, travelField, timeInField, timeOutField });
  }

  public JDatePicker getDate() {
    return datePicker;
  }

  public JobTime getTime() {
    return (JobTime) jobTimeListModel.getSelectedItem();
  }

  void update() {
    updateDisplay();
  }
}
