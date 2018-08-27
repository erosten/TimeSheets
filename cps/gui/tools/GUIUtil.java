
package cps.gui.tools;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GUIUtil {

  // /**
  // * not sure what this does lol.
  // */
  // public static void differentLookAndFeel() {
  // try {
  // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
  // } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
  // | UnsupportedLookAndFeelException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  public static JComponent makeTextPanel(String text) {
    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(SwingConstants.CENTER);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);
    return panel;
  }
}
