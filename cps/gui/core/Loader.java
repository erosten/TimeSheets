
package cps.gui.core;

import cps.core.ProgramPortal;
import cps.core.db.frame.DerbyDatabase;
import cps.err.logging.LoggerGenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.slf4j.Logger;

public class Loader implements ActionListener {

  private static Logger LOG = LoggerGenerator.getLoggerFor(Loader.class, "src/programLog.log");

  private static JLabel progressInfo = new JLabel("");
  private static JFrame frame = new JFrame("CPS Assistant");
  private static JPanel loadingPanel = new JPanel();
  private static JPanel loadingDescPanel = new JPanel();

  public static void start() {
    LOG.info("Starting Program");
    progressInfo.setVerticalAlignment(SwingConstants.BOTTOM);
    loadingDescPanel.setLayout(new BorderLayout());
    loadingDescPanel.add(progressInfo);
    ImageIcon loading = new ImageIcon("src/resources/ajax-loader-gif.gif");
    JLabel progressGif = new JLabel(loading);
    progressGif.setText("Loading...");
    progressGif.setVerticalAlignment(SwingConstants.CENTER);
    loadingPanel.setLayout(new BorderLayout());
    loadingPanel.add(progressGif, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(300, 100));
    frame.setLayout(new BorderLayout());
    frame.add(loadingPanel, BorderLayout.CENTER);
    frame.add(loadingDescPanel, BorderLayout.SOUTH);
    // center frame
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    // check for updates
    checkForAvailableUpdates();
    // load db
    loadDB();
    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
  }

  private static void checkForAvailableUpdates() {
    progressInfo.setText("Checking for updates...");
    String gitVersion = "";
    try {
      gitVersion = Updater.getLatestVersion();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    String currentVersion = Updater.getCurrentVersion();
    System.out.println(gitVersion);
    System.out.println(currentVersion);

  }

  private static void loadDB() {
    String errorString = "Database connection failed.";
    progressInfo.setText("Loading Database..");
    try {
      // if portal is located in try statement, closes DB while GUI is still running
      ProgramPortal portal = new ProgramPortal(DerbyDatabase.ProgramDB);
      progressInfo.setText("Database successfully loaded.");
      MainFrame.createAndShowGUI(portal);
      LOG.info("Exiting Program");
    } catch (SQLException sqle) {
      if (DBAlreadyBooted(sqle)) {
        errorString = "Database is booted in another instance.";
        JOptionPane.showMessageDialog(null, errorString, "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {

  }

  private static boolean DBAlreadyBooted(SQLException sqle) {
    if ("XJ040".equals(sqle.getSQLState()) && sqle.getErrorCode() == 40000) {
      sqle = sqle.getNextException();
      if ("XSDB6".equals(sqle.getSQLState()) && sqle.getErrorCode() == 45000) {
        return true;
      }
    }
    return false;
  }
}
