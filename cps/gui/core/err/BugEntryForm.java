
package cps.gui.core.err;

import cps.err.TrackerPortal;
import cps.err.model.Bug;
import cps.gui.tools.Constants;
import cps.gui.tools.GridBagUtil;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class BugEntryForm extends JPanel implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 2858073244645117584L;
  private static TrackerPortal tp = null;

  private JTextField bugTitleTextField;
  private JTextArea bugDescTextArea;
  private JScrollPane textAreaScrollPane;

  private JLabel bugTitleFieldLabel;
  private JLabel bugDescAreaLabel;

  private JButton submitButton;
  private JButton cancelButton;
  private JPanel buttonPanel;

  private JLabel actionLabel;

  public BugEntryForm(TrackerPortal tracker) {
    super(new GridBagLayout());

    tp = tracker;

    this.setTextAreasAndFields();
    this.setLabels();
    this.setButtonPanel();
    this.setActionLabel();
    this.addComponents();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (userPressedCancel(e)) {
      return;
    }
    if (fieldsAreValid()) {
      tryBugAdd();
    }
  }

  private boolean userPressedCancel(ActionEvent e) {
    JFrame parentFrame = (JFrame) SwingUtilities.getRoot(this);
    if (e.getSource() == cancelButton) {
      parentFrame.dispose();
      return true;
    }
    return false;
  }

  private boolean fieldsAreValid() {
    if (GridBagUtil.checkJTextComponentForEmpty(bugTitleTextField, bugDescTextArea)) {
      actionLabel.setText("Fields cannot be empty");
      Toolkit.getDefaultToolkit().beep();
      return false;
    }
    return true;
  }

  private void tryBugAdd() {
    JFrame parentFrame = (JFrame) SwingUtilities.getRoot(this);
    String title = bugTitleTextField.getText();
    String description = bugDescTextArea.getText();
    if (!tp.addBug(new Bug(title, description, tp.getNextBugNum()))) {
      JOptionPane.showMessageDialog(null, "Bug save to database failed..contact coder for info",
          "Error", JOptionPane.ERROR_MESSAGE);
    }
    parentFrame.dispose();
  }

  private void setTextAreasAndFields() {
    setBugTitleTextField();
    setBugDescriptionArea();
  }

  private void setBugTitleTextField() {
    bugTitleTextField = new JTextField(20);
    bugTitleTextField.addActionListener(this);
  }

  private void setBugDescriptionArea() {
    bugDescTextArea = new JTextArea(5, 20);
    bugDescTextArea.setEditable(true);
    textAreaScrollPane = new JScrollPane(bugDescTextArea);
  }

  private void setLabels() {
    setBugTitleLabel();
    setBugDescLabel();
  }

  private void setBugTitleLabel() {
    bugTitleFieldLabel = new JLabel("Enter the title for bug # " + tp.getNextBugNum() + ": ");
    bugTitleFieldLabel.setBorder(Constants.emptyBorder);
    bugTitleFieldLabel.setLabelFor(bugTitleTextField);
  }

  private void setBugDescLabel() {
    bugDescAreaLabel = new JLabel("Enter a description below");
    bugDescAreaLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    bugDescAreaLabel.setLabelFor(bugTitleTextField);
  }

  private void setButtonPanel() {
    submitButton = new JButton("Submit Bug");
    submitButton.addActionListener(this);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(cancelButton);
    buttonPanel.add(submitButton);
  }

  private void setActionLabel() {
    actionLabel = new JLabel();
    actionLabel.setForeground(Color.red);
    // set to place in the bottom left - same approximate row as buttons
    actionLabel.setBorder(BorderFactory.createEmptyBorder(5, 50, 8, 10));
  }

  private void addComponents() {
    // Add Components to this panel.
    // uses default gridBagConstraints
    GridBagUtil.addComponent(this, bugTitleFieldLabel);
    GridBagUtil.addComponent(this, bugTitleTextField, 1, 0, GridBagConstraints.REMAINDER, 1, 0, 0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, bugDescAreaLabel, 0, 1, GridBagConstraints.REMAINDER, 1, 0, 0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, buttonPanel, 3, 3, GridBagConstraints.REMAINDER, 1, 0, 0,
        GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, actionLabel, 0, 3, GridBagConstraints.REMAINDER, 1, 0, 0,
        GridBagConstraints.WEST, GridBagConstraints.NONE);
    GridBagUtil.addComponent(this, textAreaScrollPane, 0, 2, GridBagConstraints.REMAINDER, 1, 1, 1,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH);
  }

}
