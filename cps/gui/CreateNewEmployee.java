
package cps.gui;

import cps.core.ProgramPortal;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;
import cps.gui.tools.JButtonGroup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;

public class CreateNewEmployee extends JPanel implements ActionListener {

  private static final long serialVersionUID = -1989816558177858854L;
  private static ProgramPortal programPortal;
  private static final NumberFormat nf = NumberFormat.getCurrencyInstance();
  private final Border labelBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
  protected static final String prefixFieldString = "Prefix";
  protected static final String firstNameFieldString = "First Name";
  protected static final String middleNameFieldString = "Middle Name";
  protected static final String lastNameFieldString = "Last Name";
  protected static final String suffixFieldString = "Suffix";
  protected static final String abbreviationFieldString = "Abbreviation";
  protected static final String abbreviationTakenString = "Taken";
  protected static final String abbreviationNotTakenString = "Valid";
  protected static final String regularWageFieldString = "Regular Rate";
  protected static final String woodWageFieldString = "Wood Rate";
  protected static final String langString = nf.format(new BigDecimal("0.5"));
  protected static JComboBox<String> prefixList;
  protected static JComboBox<String> suffixList;
  protected static JTextField firstNameField;
  protected static JTextField middleNameField;
  protected static JTextField lastNameField;
  protected static JTextField suffixField;
  protected static JTextField abbreviationField;
  protected static JTextField abbreviationTakenField;
  protected static JFormattedTextField regularWageField;
  protected static JFormattedTextField woodWageField;
  protected static JRadioButton noLangButton;
  protected static JRadioButton langButton;
  protected static JButtonGroup langButtons = new JButtonGroup();

  protected static JButton button;

  protected JLabel actionLabel;

  /**
   * creates a gui window to create a new employee.
   */
  public CreateNewEmployee() {

    final String[] prefixStrings = { "", "Mr.", "Ms.", "Mrs.", "Dr." };
    final String[] suffixStrings = { "", "Jr.", "Sr." };

    // Indices start at 0, so 4 specifies no prefix.
    prefixList = new JComboBox<String>(prefixStrings);
    prefixList.setSelectedIndex(0);
    suffixList = new JComboBox<String>(suffixStrings);
    suffixList.setSelectedIndex(0);

    // Create a first name text field.
    firstNameField = new JTextField(20);
    firstNameField.setActionCommand(firstNameFieldString);

    // Create a middle name text field.
    middleNameField = new JTextField(20);
    middleNameField.setActionCommand(middleNameFieldString);

    // Create a last name text field.
    lastNameField = new JTextField(20);
    lastNameField.setActionCommand(lastNameFieldString);

    // Create an abbreviation text field.
    abbreviationField = new JTextField(20);
    abbreviationField.setActionCommand(abbreviationFieldString);
    abbreviationField.addActionListener(this);

    // Create an uneditable text field denoting when the abbreviation is taken
    abbreviationTakenField = new JTextField(5);
    abbreviationTakenField.setActionCommand("abbreviationTaken");
    abbreviationTakenField.setEditable(false);

    // Create a Regular wage text field.
    regularWageField = new JFormattedTextField(
        new NumberFormatter(java.text.NumberFormat.getCurrencyInstance(Locale.US)));
    regularWageField.setActionCommand(regularWageFieldString);
    regularWageField.setValue(new Float(00.00));
    regularWageField.setColumns(10);
    // Create a Wood wage text field.
    woodWageField = new JFormattedTextField(java.text.NumberFormat.getCurrencyInstance());
    woodWageField.setActionCommand(woodWageFieldString);
    woodWageField.setValue(new Float(00.00));
    woodWageField.setColumns(10);
    // Create Language radio buttons
    // Create a no lang button
    noLangButton = new JRadioButton(nf.format(BigDecimal.ZERO));
    noLangButton.setActionCommand(nf.format(BigDecimal.ZERO));
    noLangButton.setBorder(labelBorder);
    noLangButton.setSelected(true);
    // Create a lang button
    langButton = new JRadioButton(langString);
    langButton.setActionCommand(langString);
    langButton.setBorder(labelBorder);
    langButtons.add(noLangButton);
    langButtons.add(langButton);

    // Create some labels for the fields.

    // Set prefix label
    final JLabel prefixFieldLabel = new JLabel(prefixFieldString + ": ");
    prefixFieldLabel.setBorder(labelBorder);
    prefixFieldLabel.setLabelFor(prefixList);
    // Set first name label
    final JLabel firstNameFieldLabel = new JLabel(firstNameFieldString + ":* ");
    firstNameFieldLabel.setBorder(labelBorder);
    firstNameFieldLabel.setLabelFor(firstNameField);
    // Set middle name label
    final JLabel middleNameFieldLabel = new JLabel(middleNameFieldString + ": ");
    middleNameFieldLabel.setBorder(labelBorder);
    middleNameFieldLabel.setLabelFor(middleNameField);
    // Set last name label
    final JLabel lastNameFieldLabel = new JLabel(lastNameFieldString + ":* ");
    lastNameFieldLabel.setBorder(labelBorder);
    lastNameFieldLabel.setLabelFor(lastNameField);
    // Set suffix label
    final JLabel suffixFieldLabel = new JLabel(suffixFieldString + ": ");
    suffixFieldLabel.setBorder(labelBorder);
    suffixFieldLabel.setLabelFor(suffixList);
    // Set abbreviation label
    final JLabel abbreviationFieldLabel = new JLabel(abbreviationFieldString + ":* ");
    abbreviationFieldLabel.setBorder(labelBorder);
    abbreviationFieldLabel.setLabelFor(abbreviationField);
    // Set regular wage label
    final JLabel regularWageFieldLabel = new JLabel(regularWageFieldString + ":* ");
    regularWageFieldLabel.setBorder(labelBorder);
    regularWageFieldLabel.setLabelFor(regularWageField);
    // Set wood wage label
    final JLabel woodWageFieldLabel = new JLabel(woodWageFieldString + ":* ");
    woodWageFieldLabel.setBorder(labelBorder);
    woodWageFieldLabel.setLabelFor(woodWageField);
    // Set lang label
    final JLabel langFieldLabel = new JLabel("Language Bonus:* ");
    langFieldLabel.setBorder(labelBorder);
    langFieldLabel.setLabelFor(noLangButton);
    // Create a label to put messages during an action event.
    // 22 spaces
    actionLabel = new JLabel(" ");
    actionLabel.setForeground(Color.red);
    // actionLabel.setBorder(labelBorder);

    // Lay out the gridbag for the Combo boxes and text fields

    final GridBagLayout gridbag = new GridBagLayout();
    setLayout(gridbag);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
        "Employee Fields"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    final JPanel namePane = new JPanel();
    final JPanel wagePane = new JPanel();
    final JPanel langPanel = new JPanel();
    namePane.setLayout(gridbag);
    namePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
        "Name Fields"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    wagePane.setLayout(gridbag);
    wagePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
        "Wage Fields"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    addComponent(namePane, prefixFieldLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, prefixList, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(namePane, firstNameFieldLabel, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, firstNameField, 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    addComponent(namePane, middleNameFieldLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, middleNameField, 1, 2, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    addComponent(namePane, lastNameFieldLabel, 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, lastNameField, 1, 3, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    addComponent(namePane, suffixFieldLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, suffixList, 1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(namePane, abbreviationFieldLabel, 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(namePane, abbreviationField, 1, 5, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    addComponent(namePane, abbreviationTakenField, 2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(wagePane, regularWageFieldLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(wagePane, regularWageField, 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(wagePane, woodWageFieldLabel, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(wagePane, woodWageField, 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL);
    addComponent(wagePane, langFieldLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(langPanel, noLangButton, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(langPanel, langButton, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(wagePane, langPanel, 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    // Add Panes to Frame
    final JPanel directionPanel = new JPanel();
    directionPanel.setLayout(gridbag);
    final JLabel directions1 = new JLabel("Please fill in the information below.");
    final JLabel directions2 = new JLabel("* Denotes required fields.");
    directions1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    directions2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    addComponent(directionPanel, directions1, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(directionPanel, directions2, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE);
    addComponent(directionPanel, actionLabel, 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE);
    addComponent(this, directionPanel, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
        GridBagConstraints.BOTH);
    addComponent(this, namePane, 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
        GridBagConstraints.BOTH);
    addComponent(this, wagePane, 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.SOUTH,
        GridBagConstraints.BOTH);

    final JPanel bottomPane = new JPanel(gridbag);

    // create new button
    button = new JButton("Add Employee");
    button.addActionListener(this);
    addComponent(bottomPane, button, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
        GridBagConstraints.NONE, new Insets(10, 300, 0, 0));
    actionLabel.setLabelFor(button);
    // addComponent(bottomPane, actionLabel, 0, 0, 1, 1, 0.0, 0.0,
    // GridBagConstraints.CENTER,
    // GridBagConstraints.NONE);
    addComponent(this, bottomPane, 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE,
        GridBagConstraints.BOTH);
  }

  private static void addComponent(Container container, Component component, int gridx, int gridy,
      int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
    final Insets insets = new Insets(0, 0, 0, 0);
    final GridBagConstraints gbc = new GridBagConstraints(
        gridx,
        gridy,
        gridwidth,
        gridheight,
        weightx,
        weighty,
        anchor,
        fill,
        insets,
        0,
        0);
    container.add(component, gbc);
  }

  private static void addComponent(Container container, Component component, int gridx, int gridy,
      int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill,
      Insets insets) {
    final GridBagConstraints gbc = new GridBagConstraints(
        gridx,
        gridy,
        gridwidth,
        gridheight,
        weightx,
        weighty,
        anchor,
        fill,
        insets,
        0,
        0);
    container.add(component, gbc);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final Object src = e.getSource();
    if (src == abbreviationField) {
      final String givenAbbreviation = abbreviationField.getText();
      for (final Employee employee : programPortal.findAllEmployees()) {
        if (employee.getAbbreviation().equalsIgnoreCase(givenAbbreviation)) {
          abbreviationTakenField.setText(abbreviationTakenString);
          return;
        } else {
          abbreviationTakenField.setText(abbreviationNotTakenString);
        }
      }
    }
    if (firstNameField.getText().isEmpty()) {
      actionLabel.setText("First Name cannot be empty");
      Toolkit.getDefaultToolkit().beep();
    } else if (lastNameField.getText().isEmpty()) {
      actionLabel.setText("Last Name cannot be empty ");
      Toolkit.getDefaultToolkit().beep();
    } else if (abbreviationField.getText().isEmpty()) {
      actionLabel.setText("Abbreviation cannot be empty ");
      Toolkit.getDefaultToolkit().beep();
    } else if (new BigDecimal(((Number) regularWageField.getValue()).doubleValue()).equals(
        BigDecimal.ZERO)) {
      actionLabel.setText("Regular Wage cannot be 0");
      Toolkit.getDefaultToolkit().beep();
    } else if (new BigDecimal(((Number) woodWageField.getValue()).doubleValue()).equals(
        BigDecimal.ZERO)) {
      actionLabel.setText("Wood Wage cannot be 0");
      Toolkit.getDefaultToolkit().beep();
    } else {
      // check if abbreviation exists
      final String givenAbbreviation = abbreviationField.getText();
      for (final Employee employee : programPortal.findAllEmployees()) {
        if (employee.getAbbreviation().equalsIgnoreCase(givenAbbreviation)) {
          abbreviationTakenField.setText(abbreviationTakenString);
          actionLabel.setText("No Action Taken");
          actionLabel.setForeground(Color.black);
          return;
        }
      }
      final String prefix = prefixList.getSelectedItem().toString();
      final String suffix = suffixList.getSelectedItem().toString();
      final String first = firstNameField.getText();
      final String middle = middleNameField.getText();
      final String last = lastNameField.getText();
      final PersonName.NameBuilder nb = new PersonName.NameBuilder(first, last).prefix(prefix)
          .suffix(suffix).middleName(middle);
      nb.prefix(prefix).suffix(suffix).middleName(middle);
      final Employee.Builder eb = new Employee.Builder(nb.build(), abbreviationField.getText());
      try {
        eb.langBonus(new BigDecimal(
            nf.parse(this.getSelectedButtonText(langButtons)).doubleValue()));
      } catch (final ParseException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      EmployeeWage.Builder wb = new EmployeeWage.Builder(
          "Regular",
          new BigDecimal(((Number) regularWageField.getValue()).doubleValue()));
      eb.wage(wb);
      wb = new EmployeeWage.Builder(
          "Wood",
          new BigDecimal(((Number) woodWageField.getValue()).doubleValue()));
      eb.wage(wb);
      wb = new EmployeeWage.Builder(
          "Premium",
          new BigDecimal(((Number) regularWageField.getValue()).doubleValue()).add(new BigDecimal(
              "3.00")));
      eb.wage(wb);
      final Employee employee = eb.build();
      if (programPortal.addNewEmployee(employee)) {
        actionLabel.setText("Employee Added [" + programPortal.findAllEmployees().size() + "]");
        actionLabel.setForeground(Color.green);
      } else {
        if (actionLabel.getText().trim().isEmpty() || actionLabel.getText().contains("Employee")) {
          actionLabel.setText("No Action Taken");
          actionLabel.setForeground(Color.black);
        }
      }
    }

  }

  /**
   * shows the created window to create an employee.
   *
   * @param pp
   *          the programportal to interact with
   */
  public static void createAndShowFrame(ProgramPortal pp, Frame mainFrame) {
    programPortal = pp;
    // Create and set up the window
    final JDialog dialog = new JDialog(mainFrame, "Create a New Employee");
    // Add content to the window.
    dialog.add(new CreateNewEmployee());
    dialog.setModalityType(ModalityType.APPLICATION_MODAL);

    // if frame width is too small (550 or below), truncates all the text fields
    // really small
    dialog.setPreferredSize(new Dimension(500, 560));
    dialog.setMinimumSize(new Dimension(500, 560));
    dialog.setMaximumSize(new Dimension(500, 560));

    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Display the window.
    dialog.pack();
    dialog.setVisible(true);
    // centers the frame
    dialog.setLocationRelativeTo(null);
    // auto selects the add employee button (if you press enter)
    dialog.getRootPane().setDefaultButton(button);
    // auto puts the cursor on the first name Field
    firstNameField.requestFocusInWindow();
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
}
