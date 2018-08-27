
package cps.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class MainFrame {

  private static JFrame frame = null;

  private static JLabel label = null;

  // private final JTextArea textArea = null;

  private static int secondsToClose = 2;

  // implements an action listener
  private class ClickListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      for (int i = secondsToClose; i > -1; i--) {
        // try {
        MainFrame.label.setText("Closing in " + i + " seconds");
        try {
          Thread.sleep(i * 1000);
        } catch (final InterruptedException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      // If you want the GUI to behave as if you clicked the X close button then
      // you need to
      // dispatch a window closing event to the Window. The ExitAction from
      // Closing An
      // Application
      // allows you to add this functionality to a menu item or any component
      // that uses
      // Actions
      // easily. -stackoverflow
      frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
  }

  /**
   * test class.
   */
  public static void view() {
    // creates window
    frame = new JFrame();
    // set window size (w,l)
    frame.setSize(500, 500);
    // set window title
    frame.setTitle("Command Line");
    // set what to do on close
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Create the menu bar. Make it have a green background.
    final JMenuBar greenMenuBar = new JMenuBar();
    greenMenuBar.setOpaque(true);
    greenMenuBar.setBackground(Color.green);
    greenMenuBar.setForeground(Color.green);
    greenMenuBar.setPreferredSize(new Dimension(200, 20));
    // create new button
    final JButton button = new JButton("Click me to close");
    // sets initial label text
    label = new JLabel("");
    //
    final JPanel panel = new JPanel();
    panel.add(button);
    panel.add(label);
    frame.getContentPane().setBackground(new Color(154, 165, 127));
    frame.getContentPane().add(label, BorderLayout.CENTER);
    frame.setJMenuBar(greenMenuBar);
    frame.add(panel);
    // instantiate the click listener class described at top
    final ActionListener listener = new MainFrame().new ClickListener();
    // watch the button
    button.addActionListener(listener);
    // make frame visible
    frame.setVisible(true);
  }
}
