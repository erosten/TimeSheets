
package cps.gui.core;

import cps.core.ProgramPortal;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.gui.CreateNewEmployee;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;

import javax.swing.JTextField;

public class EmployeePanelController implements ActionListener, ItemListener {

  private ProgramPortal portal;
  private EmployeePanel employeePanel;
  static final int PREFIX_CHANGE = 0;
  static final int FNAME_CHANGE = 1;
  static final int MNAME_CHANGE = 2;
  static final int LNAME_CHANGE = 3;
  static final int SUFFIX_CHANGE = 4;
  static final int ABB_CHANGE = 5;
  static boolean LANG_CHANGE = false;
  static final int WNAME_CHANGE = 6;
  static final int WRATE_CHANGE = 7;

  public EmployeePanelController(ProgramPortal portal) {
    this.portal = portal;

  }

  public void setEmployeePanel(EmployeePanel ep) {
    employeePanel = ep;
  }

  public ProgramPortal getProgramPortal() {
    return portal;
  }

  public Employee getEmployee(String id) {
    return portal.findEmployeeById(id);
  }

  public Employee getEmployee() {
    return this.getEmployee(employeePanel.getSelectedEmployeeID());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("addNew")) {
      this.addAction();
    } else if (command.equals("delete")) {
      this.deleteAction();
    } else if (command.equals("update")) {
      this.updateAction();
    } else if (command.equals("Prefix")) {
      employeePanel.updatePrefixSuffixTexts();
    } else if (command.equals("Suffix")) {
      employeePanel.updatePrefixSuffixTexts();
    }
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      LANG_CHANGE = true;
    }
  }

  private void addAction() {
    CreateNewEmployee.createAndShowFrame(portal, MainFrame.frame);
    employeePanel.updateEmployeeTable();
  }

  private void deleteAction() {
    portal.terminateEmployee(employeePanel.getSelectedEmployeeID());
    employeePanel.updateEmployeeTable();
  }

  private void updateAction() {
    Employee emp = this.getEmployee(employeePanel.getSelectedEmployeeID());
    EmployeeWage empWage = portal.findWageById(employeePanel.getSelectedWageID());
    if (emp == null) {
      return;
    }
    for (JTextField field : employeePanel.getTextFields()) {
      switch ((int) field.getClientProperty("FIELD")) {
        case FNAME_CHANGE :
          if (!field.getText().equals(emp.getFirstName())) {
            emp.setName(emp.getName().withFirstName(field.getText()));
          }
          break;
        case MNAME_CHANGE :
          if (!field.getText().equals(emp.getMiddleName())) {
            emp.setName(emp.getName().withMiddleName(field.getText()));
          }
          break;
        case LNAME_CHANGE :
          if (!field.getText().equals(emp.getLastName())) {
            emp.setName(emp.getName().withLastName(field.getText()));
          }
          break;
        case PREFIX_CHANGE :
          if (!field.getText().equals(emp.getPrefix())) {
            emp.setName(emp.getName().withPrefix(field.getText()));
          }
          break;
        case SUFFIX_CHANGE :
          if (!field.getText().equals(emp.getSuffix())) {
            emp.setName(emp.getName().withSuffix(field.getText()));
          }
          break;
        case ABB_CHANGE :
          if (!field.getText().equals(emp.getAbbreviation())) {
            emp.setAbbreviation(field.getText());
          }
          break;
        case WNAME_CHANGE :
          if (empWage == null) {
            break;
          }
          if (!field.getText().equals(empWage.getName())) {
            System.out.println("wage name change to " + field.getText());
            empWage.setName(field.getText());
          }
          break;
        case WRATE_CHANGE :
          if (empWage == null) {
            break;
          }
          if (new BigDecimal(field.getText()).compareTo(empWage.getRate()) != 0) {
            empWage.setRate(new BigDecimal(field.getText()));
          }

          break;
        default :
          break;
      }
    }
    if (LANG_CHANGE == true) {
      emp.setLang(employeePanel.getLanguageBonus());
    }
    portal.save();
    employeePanel.updateEmployeeTableFrom(emp);

  }

}
