
package cps.gui.core;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.frame.Wage;
import cps.gui.tools.GridBagUtil;
import cps.gui.tools.JButtonGroup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;

public class EmployeePanel extends JPanel {

  private static final NumberFormat nf = NumberFormat.getCurrencyInstance();
  private final Border labelBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
  // controller
  private static EmployeePanelController epc;
  // employee data table
  private JTable employeeTable;
  private EmployeeTableModel employeeTableModel;
  private JScrollPane employeeDataScrollPane;
  // employee panel components
  private JLabel eIDLabel;
  private JLabel ePrefixNameLabel;
  private JLabel eFirstNameLabel;
  private JLabel eMiddleNameLabel;
  private JLabel eLastNameLabel;
  private JLabel eSuffixNameLabel;
  private JComboBox<String> prefixList;
  private JComboBox<String> suffixList;
  private final String[] prefixStrings = { "", "Mr.", "Ms.", "Mrs.", "Dr." };
  private final String[] suffixStrings = { "", "Jr.", "Sr." };
  private JLabel eAbbLabel;
  private JRadioButton noLangButton;
  private JRadioButton langButton;
  private JButtonGroup langButtons = new JButtonGroup();
  private JTextField eIDField;
  private JTextField ePrefixNameField;
  private JTextField eFirstNameField;
  private JTextField eMiddleNameField;
  private JTextField eLastNameField;
  private JTextField eSuffixNameField;
  private JTextField eAbbField;
  // wage fields
  private JList<String> wageList;
  private static JScrollPane scrollableWageList;
  private JLabel wageListLabel;
  private JLabel wageNameLabel;
  private JLabel wageRateLabel;
  private JTextField wageNameField;
  private JTextField wageRateField;
  // buttons
  private JButton addNewEmployeeButton;
  private JButton deleteEmployeeButton;
  private JButton updateEmployeeButton;

  // private constructor and frame setup methods
  EmployeePanel(EmployeePanelController epController) {
    super(new GridBagLayout());
    epc = epController;
    epc.setEmployeePanel(this);
    this.createEmployeePanel();
    this.setupEmployeeTable();
    this.addComponents();
  }

  private void createEmployeePanel() {
    // create id components
    eIDField = new JTextField(20);
    eIDField.setEditable(false);
    eIDLabel = new JLabel("Employee ID:");
    eIDLabel.setBorder(labelBorder);
    eIDLabel.setLabelFor(eIDField);
    // create name components
    // Indices start at 0, so 4 specifies no prefix.
    prefixList = new JComboBox<String>(prefixStrings);
    prefixList.setSelectedIndex(0);
    prefixList.setActionCommand("Prefix");
    prefixList.addActionListener(epc);
    suffixList = new JComboBox<String>(suffixStrings);
    suffixList.setSelectedIndex(0);
    suffixList.setActionCommand("Suffix");
    suffixList.addActionListener(epc);

    ePrefixNameField = new JTextField(5);
    ePrefixNameField.setEditable(false);
    ePrefixNameField.putClientProperty("FIELD", EmployeePanelController.PREFIX_CHANGE);
    ePrefixNameLabel = new JLabel("Name Prefix: ");
    ePrefixNameLabel.setBorder(labelBorder);
    ePrefixNameLabel.setLabelFor(ePrefixNameField);
    eFirstNameField = new JTextField(20);
    eFirstNameField.putClientProperty("FIELD", EmployeePanelController.FNAME_CHANGE);
    eFirstNameLabel = new JLabel("First Name: ");
    eFirstNameLabel.setBorder(labelBorder);
    eFirstNameLabel.setLabelFor(eFirstNameField);
    eMiddleNameField = new JTextField(20);
    eMiddleNameField.putClientProperty("FIELD", EmployeePanelController.MNAME_CHANGE);
    eMiddleNameLabel = new JLabel("Middle Name: ");
    eMiddleNameLabel.setBorder(labelBorder);
    eMiddleNameLabel.setLabelFor(eMiddleNameField);
    eLastNameField = new JTextField(20);
    eLastNameField.putClientProperty("FIELD", EmployeePanelController.LNAME_CHANGE);
    eLastNameLabel = new JLabel("Last Name: ");
    eLastNameLabel.setBorder(labelBorder);
    eLastNameLabel.setLabelFor(eLastNameField);
    eSuffixNameField = new JTextField(5);
    eSuffixNameField.setEditable(false);
    eSuffixNameField.putClientProperty("FIELD", EmployeePanelController.SUFFIX_CHANGE);
    eSuffixNameLabel = new JLabel("Name Suffix: ");
    eSuffixNameLabel.setBorder(labelBorder);
    eSuffixNameLabel.setLabelFor(eSuffixNameField);
    // set abbreviation & lang components
    eAbbField = new JTextField(20);
    eAbbField.putClientProperty("FIELD", EmployeePanelController.ABB_CHANGE);
    eAbbLabel = new JLabel("Abbreviation: ");
    eAbbLabel.setBorder(labelBorder);
    eAbbLabel.setLabelFor(eAbbField);
    // Create Language radio buttons
    // Create a no lang button
    noLangButton = new JRadioButton(nf.format(BigDecimal.ZERO));
    noLangButton.setActionCommand(nf.format(BigDecimal.ZERO));
    noLangButton.setBorder(labelBorder);
    noLangButton.setSelected(true);
    noLangButton.addItemListener(epc);
    // Create a lang button
    langButton = new JRadioButton(nf.format(new BigDecimal("0.50")));
    langButton.setActionCommand(nf.format(new BigDecimal("0.50")));
    langButton.setBorder(labelBorder);
    langButton.addItemListener(epc);
    langButtons.add(noLangButton);
    langButtons.add(langButton);
    // create bottom buttons
    addNewEmployeeButton = new JButton("Add New");
    addNewEmployeeButton.setActionCommand("addNew");
    addNewEmployeeButton.addActionListener(epc);
    deleteEmployeeButton = new JButton("Delete");
    deleteEmployeeButton.setActionCommand("delete");
    deleteEmployeeButton.addActionListener(epc);
    updateEmployeeButton = new JButton("Update");
    updateEmployeeButton.setActionCommand("update");
    updateEmployeeButton.addActionListener(epc);
    // wage fields
    wageList = new JList<>();
    wageListLabel = new JLabel();
    wageListLabel.setText("Wage List");
    wageListLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableWageList = new JScrollPane(wageList);
    scrollableWageList.setPreferredSize(new Dimension(150, 100));
    scrollableWageList.setAlignmentX(Component.CENTER_ALIGNMENT);
    wageListLabel.setLabelFor(scrollableWageList);
    scrollableWageList.setBorder(labelBorder);
    wageList.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          String wageInfo = wageList.getSelectedValue();
          int parsePoint = wageInfo.indexOf("[", 0);
          String wageName = wageInfo.substring(0, parsePoint - 1);
          int parseEnd = wageInfo.indexOf("]");
          String wageRate = wageInfo.substring(parsePoint + 1, parseEnd);
          wageNameField.setText(wageName);
          wageRateField.setText(wageRate);
        }
      }
    });
    wageList.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        String wageInfo = wageList.getSelectedValue();
        int parsePoint = wageInfo.indexOf("[", 0);
        String wageName = wageInfo.substring(0, parsePoint - 1);
        int parseEnd = wageInfo.indexOf("]");
        String wageRate = wageInfo.substring(parsePoint + 1, parseEnd);
        wageNameField.setText(wageName);
        wageRateField.setText(wageRate);
      }
    });
    wageNameField = new JTextField(10);
    wageNameField.putClientProperty("FIELD", EmployeePanelController.WNAME_CHANGE);
    wageNameLabel = new JLabel("Wage Name: ");
    wageNameLabel.setBorder(labelBorder);
    wageNameLabel.setLabelFor(wageNameField);
    wageRateField = new JTextField(6);
    wageRateField.putClientProperty("FIELD", EmployeePanelController.WRATE_CHANGE);
    wageRateLabel = new JLabel("Wage Rate: ");
    wageRateLabel.setBorder(labelBorder);
    wageRateLabel.setLabelFor(wageRateField);

  }

  private void setEmployeeTableData(Employee emp) {
    eIDField.setText(emp.getId().toString());
    ePrefixNameField.setText(emp.getPrefix());
    eFirstNameField.setText(emp.getFirstName());
    eMiddleNameField.setText(emp.getMiddleName());
    eLastNameField.setText(emp.getLastName());
    eSuffixNameField.setText(emp.getSuffix());
    eAbbField.setText(emp.getAbbreviation());
    prefixList.setSelectedItem(emp.getPrefix());
    suffixList.setSelectedItem(emp.getSuffix());
    if (emp.getLanguageBonus().compareTo(BigDecimal.ZERO) == 0) {
      noLangButton.setSelected(true);
      langButton.setSelected(false);
    } else {
      noLangButton.setSelected(false);
      langButton.setSelected(true);
      langButton.setText(nf.format(emp.getLanguageBonus()));
    }
    List<String> wages = new ArrayList<>();
    for (Wage wage : emp.getWages()) {
      wages.add(wage.getName() + " [" + wage.getRate() + "]");
    }
    wageList.setListData(wages.toArray(new String[0]));
    scrollableWageList.repaint();
    scrollableWageList.revalidate();
  }

  private void setupEmployeeTable() {
    employeeTableModel = new EmployeeTableModel(epc.getProgramPortal());
    employeeTable = new JTable(employeeTableModel);
    employeeTable.setPreferredScrollableViewportSize(new Dimension(50, 100));
    employeeDataScrollPane = new JScrollPane(employeeTable);
    employeeTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
      try {
        if (employeeTable.getSelectedRow() >= 0) {
          String employee_id = (String) employeeTable.getValueAt(employeeTable.getSelectedRow(), 0);
          Employee emp = epc.getEmployee(employee_id);
          this.setEmployeeTableData(emp);
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    });
  }

  private void addComponents() {
    // JPanel actionLabelDescriptionPane = new JPanel();
    // actionLabelDescriptionPane.setLayout(new GridBagLayout());
    GridBagUtil.addComponent(this, employeeDataScrollPane, 0, 0, 3, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, eIDLabel, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eIDField, 1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, ePrefixNameLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, prefixList, 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, ePrefixNameField, 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eFirstNameLabel, 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eFirstNameField, 1, 3, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, eMiddleNameLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eMiddleNameField, 1, 4, 2, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, eLastNameLabel, 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eLastNameField, 1, 5, 2, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, eSuffixNameLabel, 0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, suffixList, 1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eSuffixNameField, 2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eAbbLabel, 0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, eAbbField, 1, 7, 2, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, noLangButton, 0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, langButton, 1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, wageListLabel, 0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, scrollableWageList, 0, 10, 1, 2, 0.0, 0.0,
        GridBagConstraints.NORTH, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, wageNameLabel, 1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, wageNameField, 2, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, wageRateLabel, 1, 11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, wageRateField, 2, 11, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, addNewEmployeeButton, 0, 12, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, updateEmployeeButton, 1, 12, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, deleteEmployeeButton, 3, 12, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
  }

  public String getSelectedEmployeeID() {
    return this.eIDField.getText();
  }

  public String getSelectedWageID() {
    Employee emp = null;
    if (getSelectedEmployeeID() != null) {
      emp = epc.getEmployee(getSelectedEmployeeID());
      String selectedWageName = wageList.getSelectedValue();
      if (selectedWageName == null) {
        return "";
      }
      selectedWageName = selectedWageName.substring(0, selectedWageName.indexOf("[") - 1);
      for (EmployeeWage wage : emp.getWages()) {
        if (wage.getName().equals(selectedWageName)) {
          return wage.getId();
        }
      }
    }
    return "";
  }

  public void updateEmployeeTableFrom(Employee emp) {
    this.setEmployeeTableData(emp);
    employeeTableModel.fireTableDataChanged();
    employeeTable.repaint();
    employeeTable.revalidate();
  }

  public void updateEmployeeTable() {
    employeeTableModel.fireTableDataChanged();
    employeeTable.repaint();
    employeeTable.revalidate();
  }

  public JPanel getEmployeePanel() {
    return this;
  }

  public List<JTextField> getTextFields() {
    return Arrays.asList(new JTextField[] { ePrefixNameField, eFirstNameField, eMiddleNameField,
        eLastNameField, eSuffixNameField, eAbbField, wageNameField, wageRateField });
  }

  public JComboBox<String> getPrefixList() {
    return prefixList;
  }

  public JComboBox<String> getSuffixList() {
    return suffixList;
  }

  public void updatePrefixSuffixTexts() {
    this.ePrefixNameField.setText((String) prefixList.getSelectedItem());
    this.eSuffixNameField.setText((String) suffixList.getSelectedItem());
  }

  public BigDecimal getLanguageBonus() {
    if (noLangButton.isSelected()) {
      return new BigDecimal(noLangButton.getText().substring(1, noLangButton.getText().length()));
    } else {
      return new BigDecimal(langButton.getText().substring(1, langButton.getText().length()));

    }
  }

}
