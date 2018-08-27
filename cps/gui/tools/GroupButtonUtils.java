
package cps.gui.tools;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class GroupButtonUtils {

  /**
   * Get the button text from a ButtonGroup object by detecting if it is selected.
   * 
   * @param buttonGroup
   *          the ButtonGroup object to look for text in
   * @return the String of the button that is selected
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