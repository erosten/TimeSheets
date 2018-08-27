
package cps.gui;

import cps.core.ProgramPortal;
import cps.core.model.employee.Employee;
import cps.core.model.frame.Wage;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobTime;
import cps.core.model.timeSheet.JobWage;
import cps.core.model.timeSheet.TimeSheet;
import cps.gui.tools.GridBagUtil;
import cps.gui.tools.JButtonGroup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class CreateNewJob extends JPanel implements ActionListener {

  private static final long serialVersionUID = -1989816558177858854L;
  private static ProgramPortal programPortal;
  private static final NumberFormat nf = NumberFormat.getCurrencyInstance();
  private final Border labelBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
  private static TimeSheet tsheet;

  // employee selection fields
  // protected static JTextField employeeField;
  protected static JLabel employeeListLabel;
  protected static JList<Employee> employeeList;
  protected static DefaultListModel<Employee> employeeListModel = new DefaultListModel<>();
  protected static JScrollPane scrollableEmployeeList;
  protected static JList<Employee> selectedEmployees;
  protected static DefaultListModel<Employee> selectedEmployeeListModel = new DefaultListModel<>();
  protected static JScrollPane scrollableSelectedEmployees;
  protected static JLabel selectedEmployeesLabel;
  protected static JButton addEmployee;
  protected static JButton removeEmployee;
  protected static List<Employee> selectedEmployeesArray = new ArrayList<>();

  // employee wage selection fields
  protected static JList<Wage> wageList;
  protected static DefaultListModel<Wage> wageListModel = new DefaultListModel<>();
  protected static JScrollPane scrollableWageList;
  protected static JLabel wageListLabel;
  protected static Wage selectedWage;

  // flat rate fields
  protected static JCheckBox flatRateCheckBox;
  protected static JLabel flatRateCheckerLabel;
  protected static JTextField flatRateHourField;
  protected static JLabel flatRateHourLabel;

  // job time fields
  protected static JTextField timeInField;
  protected static JTextField timeOutField;
  protected static JLabel timeInLabel;
  protected static JLabel timeOutLabel;
  protected static JButton addTime;
  protected static JButton removeTime;
  protected static JList<JobTime> addedTimes;
  protected static DefaultListModel<JobTime> addedTimesListModel = new DefaultListModel<>();
  protected static JScrollPane scrollableTimesList;

  // std deduction fields
  protected static JLabel stdDeducLabel;
  protected static JRadioButton stdDeducBefore;
  protected static JRadioButton stdDeducAfter;
  protected static JButtonGroup stdDeducButtons = new JButtonGroup();

  // trivial job fields
  protected static JTextField codeField;
  protected static JLabel codeLabel;
  protected static JTextField travelField;
  protected static JLabel travelLabel;
  protected static JDatePicker datePicker;
  protected static JLabel dateLabel;

  // misc
  protected static JButton addButton;
  protected JLabel actionLabel;

  /**
   * creates a gui window to create a new employee.
   */
  public CreateNewJob() {
    setLayout(new GridBagLayout());
    actionLabel = new JLabel(" ");
    actionLabel.setForeground(Color.red);
    // employee fields
    // employeeField = new JTextField(10);
    // employeeField.setEditable(true);
    employeeListLabel = new JLabel("Available Employees");
    employeeListLabel.setLabelFor(employeeList);
    employeeListLabel.setBorder(labelBorder);
    employeeList = new JList<>(employeeListModel);
    for (Employee emp : programPortal.findActiveEmployees()) {
      employeeListModel.addElement(emp);
    }
    ListSelectionModel listSelectionModel = employeeList.getSelectionModel();
    listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
    scrollableEmployeeList = new JScrollPane(employeeList);
    scrollableEmployeeList.setPreferredSize(new Dimension(200, 150));
    scrollableEmployeeList.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableEmployeeList.setBorder(labelBorder);
    selectedEmployees = new JList<>(selectedEmployeeListModel);
    selectedEmployees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    scrollableSelectedEmployees = new JScrollPane(selectedEmployees);
    scrollableSelectedEmployees.setPreferredSize(new Dimension(200, 150));
    scrollableSelectedEmployees.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableSelectedEmployees.setBorder(labelBorder);
    selectedEmployeesLabel = new JLabel("Selected Employees");
    selectedEmployeesLabel.setLabelFor(selectedEmployees);
    selectedEmployeesLabel.setBorder(labelBorder);
    addEmployee = new JButton("Add Employee");
    addEmployee.addActionListener(this);
    removeEmployee = new JButton("Remove Employee");
    removeEmployee.addActionListener(this);
    // wage list fields
    wageListLabel = new JLabel("Available Wages");
    wageListLabel.setLabelFor(wageList);
    wageListLabel.setBorder(labelBorder);
    wageList = new JList<>(wageListModel);
    wageListModel.addElement(tsheet.getTravelWage());
    scrollableWageList = new JScrollPane(wageList);
    scrollableWageList.setPreferredSize(new Dimension(200, 150));
    scrollableWageList.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableWageList.setBorder(labelBorder);
    // flat rate fields
    flatRateCheckBox = new JCheckBox("Flat Rate");
    flatRateCheckBox.addActionListener(this);
    flatRateHourLabel = new JLabel("Equivalent Hours: ");
    flatRateHourLabel.setLabelFor(flatRateHourField);
    flatRateHourLabel.setBorder(labelBorder);
    flatRateHourField = new JTextField(5);
    flatRateHourField.setEditable(false);
    // hour fields
    timeInLabel = new JLabel("Time In: ");
    timeInLabel.setLabelFor(timeInField);
    timeInLabel.setBorder(labelBorder);
    timeOutLabel = new JLabel("Time Out: ");
    timeOutLabel.setLabelFor(timeOutField);
    timeOutLabel.setBorder(labelBorder);
    timeInField = new JTextField(5);
    timeInField.setEditable(true);
    timeOutField = new JTextField(5);
    timeOutField.setEditable(true);
    addTime = new JButton("Add Time");
    addTime.addActionListener(this);
    removeTime = new JButton("Remove Time");
    removeTime.addActionListener(this);
    addedTimes = new JList<>(addedTimesListModel);
    addedTimes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    scrollableTimesList = new JScrollPane(addedTimes);
    scrollableTimesList.setPreferredSize(new Dimension(150, 100));
    scrollableTimesList.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableTimesList.setBorder(labelBorder);
    // std deduc fields
    stdDeducLabel = new JLabel("Standard Deduction: ");
    stdDeducLabel.setBorder(labelBorder);
    stdDeducBefore = new JRadioButton("Before");
    stdDeducBefore.setActionCommand("before");
    stdDeducBefore.setBorder(labelBorder);
    stdDeducBefore.setSelected(false);
    stdDeducAfter = new JRadioButton("After");
    stdDeducAfter.setActionCommand("after");
    stdDeducAfter.setBorder(labelBorder);
    stdDeducAfter.setSelected(false);
    stdDeducButtons.add(stdDeducBefore);
    stdDeducButtons.add(stdDeducAfter);
    // trivial fields
    codeLabel = new JLabel("Job Code: ");
    codeLabel.setLabelFor(codeField);
    codeLabel.setBorder(labelBorder);
    codeField = new JTextField(10);
    codeField.setEditable(true);
    datePicker = new JDatePicker(new UtilDateModel());
    dateLabel = new JLabel("Date: ");
    dateLabel.setLabelFor(datePicker);
    dateLabel.setBorder(labelBorder);
    travelLabel = new JLabel("Travel: ");
    travelLabel.setLabelFor(travelField);
    travelLabel.setBorder(labelBorder);
    travelField = new JTextField(10);
    travelField.setEditable(true);

    addButton = new JButton("Add Job");
    addButton.addActionListener(this);

    // put it all together

    // create and add directions
    final JPanel directionPanel = new JPanel();
    directionPanel.setLayout(new GridBagLayout());
    final JLabel directions1 = new JLabel("Please fill in the information below.");
    final JLabel directions2 = new JLabel("* Denotes required fields.");
    directions1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    directions2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    GridBagUtil.addComponent(directionPanel, directions1, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(directionPanel, directions2, 0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(directionPanel, actionLabel, 1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, directionPanel, 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL);

    // create and add employee fields

    final JPanel employeePanel = new JPanel();
    employeePanel.setLayout(new GridBagLayout());

    GridBagUtil.addComponent(employeePanel, employeeListLabel, 0, 0, 1, 1, 1.0, 1.0,
        GridBagConstraints.SOUTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, scrollableEmployeeList, 0, 1, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, wageListLabel, 1, 0, 1, 1, 1.0, 1.0,
        GridBagConstraints.SOUTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, scrollableWageList, 1, 1, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, addEmployee, 1, 2, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, selectedEmployeesLabel, 2, 0, 1, 1, 1.0, 1.0,
        GridBagConstraints.SOUTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, scrollableSelectedEmployees, 2, 1, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(employeePanel, removeEmployee, 2, 2, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, employeePanel, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL);

    // assemble hour fields
    final JPanel hourPanel = new JPanel();
    hourPanel.setLayout(new GridBagLayout());

    GridBagUtil.addComponent(hourPanel, flatRateCheckBox, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, flatRateHourLabel, 1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, flatRateHourField, 2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, timeInLabel, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, timeInField, 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, timeOutLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, timeOutField, 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, addTime, 0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, scrollableTimesList, 2, 1, 1, 2, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(hourPanel, removeTime, 2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, hourPanel, 0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL);

    // std deduc fields
    final JPanel stdDeducPanel = new JPanel();
    stdDeducPanel.setLayout(new GridBagLayout());

    GridBagUtil.addComponent(stdDeducPanel, stdDeducLabel, 0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(stdDeducPanel, stdDeducBefore, 1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(stdDeducPanel, stdDeducAfter, 2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, stdDeducPanel, 0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL);

    // trivial fields
    final JPanel trivialPanel = new JPanel();
    trivialPanel.setLayout(new GridBagLayout());

    GridBagUtil.addComponent(trivialPanel, codeLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(trivialPanel, codeField, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(trivialPanel, dateLabel, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(trivialPanel, datePicker, 1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(trivialPanel, travelLabel, 0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(trivialPanel, travelField, 1, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, trivialPanel, 0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL);

    // final button
    GridBagUtil.addComponent(this, addButton, 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final Object src = e.getSource();
    if (src.equals(addEmployee)) {
      selectedWage = wageList.getSelectedValue();
      if (selectedWage != null) {
        for (Employee emp : selectedEmployeesArray) {
          if (!selectedEmployeeListModel.contains(emp)) {
            selectedEmployeeListModel.addElement(emp);
          }
        }
      }
    } else if (src.equals(removeEmployee)) {
      int selectedIndex = selectedEmployees.getSelectedIndex();
      if (selectedIndex != -1) {
        selectedEmployeeListModel.remove(selectedIndex);
      }
    } else if (src.equals(flatRateCheckBox)) {
      if (flatRateCheckBox.isSelected()) {
        flatRateHourField.setEditable(true);
      }
    } else if (src.equals(addTime)) {
      int startHours = Integer.parseInt(timeInField.getText().substring(0, 2));
      final int startMinutes = Integer.parseInt(timeInField.getText().substring(2));
      int endHours = Integer.parseInt(timeOutField.getText().substring(0, 2));
      final int endMinutes = Integer.parseInt(timeOutField.getText().substring(2));
      final LocalTime timeIn = new LocalTime(startHours, startMinutes);
      final LocalTime timeOut = new LocalTime(endHours, endMinutes);
      addedTimesListModel.addElement(new JobTime(timeIn, false, timeOut, false));
    } else if (src.equals(removeTime)) {
      int selectedIndex = addedTimes.getSelectedIndex();
      if (selectedIndex != -1) {
        addedTimesListModel.remove(selectedIndex);
      }
    } else if (src.equals(addButton)) {
      for (Employee emp : selectedEmployeesArray) {
        final JobEntry.Builder jb = new JobEntry.Builder(emp);
        int day = datePicker.getModel().getDay();
        // datePicker months are 0 based
        // localDate months are 1 based
        int month = datePicker.getModel().getMonth() + 1;
        int year = datePicker.getModel().getYear();
        jb.date(new LocalDate(year, month, day));
        jb.code(codeField.getText());
        jb.travelString(travelField.getText());
        jb.wage(new JobWage(selectedWage.getName(), selectedWage.getRate()));
        for (JobTime jobTime : Collections.list(addedTimesListModel.elements())) {
          jb.addTime(jobTime);
        }
        if (flatRateCheckBox.isSelected()) {
          jb.flatRateHours(new BigDecimal(flatRateHourField.getText()));
        }
        if (stdDeducBefore.isSelected()) {
          jb.stdDeduction(true);
        } else if (stdDeducAfter.isSelected()) {
          jb.stdDeduction(false);
        }
        programPortal.addJobEntry(jb.build());
      }
    }
  }

  /**
   * shows the created window to create an job.
   *
   * @param pp
   *          the programportal to interact with
   */
  public static void createAndShowFrame(ProgramPortal pp, Frame mainFrame, TimeSheet timeSheet) {
    programPortal = pp;
    tsheet = timeSheet;
    // Create and set up the window
    final JDialog dialog = new JDialog(mainFrame, "Create a New Job");
    // Add content to the window.
    dialog.add(new CreateNewJob());
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);

    // if frame width is too small (550 or below), truncates all the text fields
    // really small
    dialog.setPreferredSize(new Dimension(620, 650));
    dialog.setMinimumSize(new Dimension(620, 650));
    // dialog.setMaximumSize(new Dimension(500, 560));

    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Display the window.
    dialog.pack();
    dialog.setVisible(true);
    // centers the frame
    dialog.setLocationRelativeTo(null);
    // auto selects the add employee button (if you press enter)
    dialog.getRootPane().setDefaultButton(addButton);
    // auto puts the cursor on the first name Field
    // firstNameField.requestFocusInWindow();
  }

  /**
   * find the text on the button the user pushed.
   *
   * @param buttonGroup
   *          the group of buttons the user had options from
   * @return the string of the button the user pressed.
   */
  public String getSelectedButtonText(ButtonGroup buttonGroup) {
    for (final Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons
        .hasMoreElements();) {
      final AbstractButton button = buttons.nextElement();
      if (button.isSelected()) {
        return button.getText();
      }
    }

    return null;
  }

  class SharedListSelectionHandler implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
      List<Integer> selectedEmployeeIndices = new ArrayList<>();

      boolean isAdjusting = e.getValueIsAdjusting();
      if (isAdjusting) {
        return;
      }
      if (lsm.isSelectionEmpty()) {
        return;
      } else {
        // Find out which indexes are selected.
        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        for (int i = minIndex; i <= maxIndex; i++) {
          if (lsm.isSelectedIndex(i)) {
            selectedEmployeeIndices.add(i);
          }
        }
      }
      // update WageList
      Employee emp = null;
      List<Wage> wagesToAdd = new ArrayList<>();
      List<Wage> existingWages = Collections.list(wageListModel.elements());
      for (int empIndex : selectedEmployeeIndices) {
        emp = employeeListModel.get(empIndex);
        selectedEmployeesArray.add(emp);
        for (Wage wage : emp.getWages()) {
          boolean shouldAdd = true;
          if (existingWages.isEmpty()) {
            System.out.println("empty");
            wagesToAdd.add(wage);
          } else {
            for (Wage existingWage : existingWages) {
              if (existingWage.hasSameNameAs(wage)) {
                shouldAdd = false;
              }
            }
            if (shouldAdd) {
              wagesToAdd.add(wage);
            }
          }
        }
      }
      for (Wage wageToAdd : wagesToAdd) {
        wageListModel.addElement(wageToAdd);
      }
    }
  }
}
