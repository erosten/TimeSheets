
package cps.gui.core.err;

import cps.err.TrackerPortal;
import cps.err.model.Bug;
import cps.gui.tools.Constants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class BugEntryMainFrameController implements ActionListener {

  private static Bug viewedBug;
  private static TrackerPortal tp;
  private static BugEntryMainFrame mainFrame;

  public BugEntryMainFrameController(TrackerPortal tracker) {
    if (tracker == null) {
      throw new IllegalStateException();
    }
    tp = tracker;
  }

  public void setMainFrame(BugEntryMainFrame mf) {
    mainFrame = mf;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("AddBug")) {
      addBugAction();
    } else if (command.equals("ViewBug")) {
      viewBugAction(mainFrame.getSelectedBug());
    } else if (command.equals("AddSolutionStep")) {
      addSolutionStepAction();
    } else if (command.equals("RemoveBug")) {
      removeBugAction();
    }
  }

  private boolean isBugViewed() {
    return viewedBug != null;
  }

  private void addBugAction() {
    // bring up an add bug screen, closing screen updates mainframe list
    JFrame bugEntryFrame = new JFrame("Bug Entry Form");
    bugEntryFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // Add contents to the window.
    bugEntryFrame.add(new BugEntryForm(tp));
    // write a listener to update the list when closed
    bugEntryFrame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosed(WindowEvent we) {
        mainFrame.updateBugList(getBugs());
        we.getWindow().dispose();
      }
    });
    bugEntryFrame.pack();
    bugEntryFrame.setVisible(true);
  }

  private void viewBugAction(String selectedString) {
    // set uneditable text area as description of the bug
    // parse the id of the bug
    String selectionString = selectedString;
    if (selectionString == null) {
      // user clicked view with no bug selected
      mainFrame.setActionLabel("No bug selected");
      return;
    }
    // id is separated by : then bug title
    selectionString = selectionString.substring(0, selectionString.indexOf(":"));
    viewedBug = tp.findById((selectionString));
    if (viewedBug == null) {
      throw new IllegalStateException("Selected a bug that did not exist in database");
    }
    mainFrame.resetActionLabel();
    String descriptionText = "Bug Title: " + viewedBug.getTitle() + "\n" + "Bug Description: "
        + viewedBug.getDesc() + "\n\n";
    descriptionText = descriptionText + viewedBug.getSolutionSteps().stream().map(
        step -> "Solution Step #" + step.getStepNum() + " - " + step.getDateAdded() + ": \n" + step
            .getStep() + "\n\n").reduce("", String::concat);
    mainFrame.updateDescriptionText(descriptionText);
    mainFrame.setSolutionStepEditable(true);
  }

  private void removeBugAction() {
    if (!this.isBugViewed()) {
      mainFrame.setActionLabel("Please view the bug you'd like to delete");
    } else {
      int dialogResult = JOptionPane.showConfirmDialog(null,
          "Are you sure you want to remove this bug from the program?", "Confirmation",
          JOptionPane.WARNING_MESSAGE);
      if (dialogResult == JOptionPane.YES_OPTION) {
        if (!tp.removeBug(viewedBug.getId())) {
          JOptionPane.showMessageDialog(null,
              "Bug removal from database failed..contact coder for info", "Error",
              JOptionPane.ERROR_MESSAGE);
        } else {
          // removal was successful
          mainFrame.updateBugList(getBugs());
          mainFrame.setSolutionStepEditable(false);
          mainFrame.updateDescriptionText("");
          mainFrame.resetSolutionStepTextArea();
          mainFrame.setActionLabel("");
          viewedBug = null;
        }

      }
      // // sets to null if removed
      // viewedBug = tp.findById(viewedBug.getId());
    }
  }

  private void addSolutionStepAction() {
    if (!this.isBugViewed()) {
      mainFrame.setActionLabel("Please view a bug to add a solution step");
      return;
    } else {
      if (mainFrame.getSolutionStepText().isEmpty()) {
        mainFrame.setActionLabel("A solution step requires text");
        return;
      } else if (mainFrame.getSolutionStepText().equals(Constants.defaultSolutionStepText)) {
        mainFrame.setActionLabel("A solution step requires non default text");
        return;
      } else {
        tp.addSolutionStep(viewedBug.getId(), mainFrame.getSolutionStepText());
        String descriptionText = "Bug Title: " + viewedBug.getTitle() + "\n" + "Bug Description: "
            + viewedBug.getDesc() + "\n\n";
        descriptionText = descriptionText + viewedBug.getSolutionSteps().stream().map(
            step -> "Solution Step #" + step.getStepNum() + " - " + step.getDateAdded() + ": \n"
                + step.getStep() + "\n\n").reduce("", String::concat);
        mainFrame.updateDescriptionText(descriptionText);
        mainFrame.setActionLabel("");
      }
    }
  }

  public String[] getBugs() {
    return tp.findAllBugs().stream().map(bug -> bug.getId() + ": " + bug.getTitle()).collect(
        Collectors.toList()).toArray(new String[0]);
  }
}
