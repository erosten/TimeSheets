
package cps.gui.core.err;

import cps.gui.tools.Constants;
import cps.gui.tools.GridBagUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class BugEntryMainFrame extends JPanel {

  private static final long serialVersionUID = -1943693673111077905L;

  private static BugEntryMainFrameController mfc;

  // parent frame
  private static JFrame frame = new JFrame("Bug Manager");

  // buglist related fields
  private static JList<String> bugList;
  private static JScrollPane scrollableBugList;
  private JLabel bugListLabel;

  // text area related fields
  private JTextArea descriptionTextArea;
  private JList<String> bugDescriptionList;
  private JScrollPane descriptionScrollPane;
  private JTextArea addSolutionStepTextArea;
  private JScrollPane solutionScrollPane;

  // button fields
  private JButton addBugButton;
  private JButton viewBugButton;
  private JButton removeBugButton;
  private JButton addSolutionStepButton;
  private JPanel buttonPanel;

  // action label at top
  private static JTextField actionLabel;

  // private constructor and frame setup methods
  private BugEntryMainFrame(BugEntryMainFrameController mfcontroller) {
    super(new GridBagLayout());
    // set the list of bugs
    this.setupBugList();
    this.setupTextFieldsAndAreas();
    this.setupButtonPanel();
    this.setupActionLabel();
    this.addComponents();
  }

  private void setupBugList() {
    bugList = new JList<>();
    bugListLabel = new JLabel();
    bugListLabel.setText("Bug List");
    bugListLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableBugList = new JScrollPane(bugList);
    scrollableBugList.setPreferredSize(new Dimension(150, 300));
    scrollableBugList.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  private void setupTextFieldsAndAreas() {
    setupDescriptionTextArea();
    setupSolutionStepTextArea();
  }

  private void setupDescriptionTextArea() {
    descriptionTextArea = new JTextArea();
    descriptionTextArea.setEditable(false);
    JList<String> bugDescription = new JList<>();

    descriptionScrollPane = new JScrollPane(descriptionTextArea);
    descriptionScrollPane.setPreferredSize(new Dimension(500, 180));
  }

  private void setupSolutionStepTextArea() {
    addSolutionStepTextArea = new JTextArea();
    addSolutionStepTextArea.setEditable(false);
    addSolutionStepTextArea.setText(Constants.defaultSolutionStepText);
    // Clear the default text if the area is clicked
    addSolutionStepTextArea.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (addSolutionStepTextArea.getText().equals(Constants.defaultSolutionStepText)) {
          if (addSolutionStepTextArea.isEditable()) {
            addSolutionStepTextArea.setText("");
          }
        }
      }
    });
    // set the text area to a scroll pane
    solutionScrollPane = new JScrollPane(addSolutionStepTextArea);
    solutionScrollPane.setPreferredSize(new Dimension(500, 120));
  }

  private void setupButtonPanel() {
    addBugButton = new JButton("Add New");
    addBugButton.addActionListener(mfc);
    addBugButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    addBugButton.setActionCommand("AddBug");

    viewBugButton = new JButton("View");
    viewBugButton.addActionListener(mfc);
    viewBugButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    viewBugButton.setActionCommand("ViewBug");

    removeBugButton = new JButton("Remove");
    removeBugButton.addActionListener(mfc);
    removeBugButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    removeBugButton.setActionCommand("RemoveBug");

    addSolutionStepButton = new JButton("Add Solution Step");
    addSolutionStepButton.addActionListener(mfc);
    addSolutionStepButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    addSolutionStepButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    addSolutionStepButton.setActionCommand("AddSolutionStep");

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(viewBugButton);
    buttonPanel.add(addBugButton);
    buttonPanel.add(removeBugButton);
    buttonPanel.add(addSolutionStepButton);
    buttonPanel.setBorder(Constants.emptyBorder);
  }

  private void setupActionLabel() {
    actionLabel = new JTextField();
    actionLabel.setForeground(Color.red);
    actionLabel.setEditable(false);
    actionLabel.setBorder(descriptionScrollPane.getBorder());
  }

  private void addComponents() {
    JPanel bugListPane = new JPanel();
    bugListPane.setLayout(new BoxLayout(bugListPane, BoxLayout.Y_AXIS));
    bugListPane.add(bugListLabel);
    bugListPane.add(scrollableBugList);
    JPanel actionLabelDescriptionPane = new JPanel();
    actionLabelDescriptionPane.setLayout(new GridBagLayout());
    GridBagUtil.addComponent(this, bugListPane, 0, 0, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.BOTH, new Insets(0, 10, 0, 10));
    GridBagUtil.addComponent(actionLabelDescriptionPane, actionLabel, 0, 0, 1, 1, 1.0, 0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(actionLabelDescriptionPane, descriptionScrollPane, 0, 1, 1, 1, 1.0,
        0.8, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    GridBagUtil.addComponent(actionLabelDescriptionPane, solutionScrollPane, 0, 2, 1, 1, 0, 0.2,
        GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL);
    GridBagUtil.addComponent(this, actionLabelDescriptionPane, 1, 1, 1, 2, 1.0, 1.0,
        GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    GridBagUtil.addComponent(this, buttonPanel, 3, 0, 1, 2, 0, 0, GridBagConstraints.NORTH,
        GridBagConstraints.EAST);
  }

  public static void createAndShowGUI(BugEntryMainFrameController mfcontroller) {
    mfc = mfcontroller;
    setFramePreferences();
    // Set the main frame controller
    addFrameContent(mfcontroller);
    // Display the window.
    frame.validate();
    frame.pack();
    frame.setVisible(true);
  }

  private static void setFramePreferences() {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getRootPane().setBorder(Constants.emptyBorder);
    frame.setLayout(new BorderLayout());
  }

  private static void addFrameContent(BugEntryMainFrameController mfcontroller) {
    BugEntryMainFrame mainFrame = new BugEntryMainFrame(mfc);
    mainFrame.updateBugList(mfc.getBugs());
    mfc.setMainFrame(mainFrame);
    frame.add(mainFrame, BorderLayout.CENTER);
  }

  // public interaction methods
  public void updateBugList(String bugOptions[]) {
    bugList.setListData(bugOptions);
    scrollableBugList.repaint();
    scrollableBugList.revalidate();
  }

  public void setActionLabel(String text) {
    actionLabel.setText(text);
  }

  public void resetActionLabel() {
    actionLabel.setText("");
  }

  public void setSolutionStepEditable(boolean editable) {
    addSolutionStepTextArea.setEditable(editable);
  }

  public String getSolutionStepText() {
    return addSolutionStepTextArea.getText();
  }

  public void updateDescriptionText(String descriptionText) {
    descriptionTextArea.setText(descriptionText);
    descriptionScrollPane.repaint();
    descriptionScrollPane.revalidate();
  }

  public String getSelectedBug() {
    return bugList.getSelectedValue();
  }

  public void resetSolutionStepTextArea() {
    addSolutionStepTextArea.setText(Constants.defaultSolutionStepText);
  }
}
