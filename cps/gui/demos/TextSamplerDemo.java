
package cps.gui.demos;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//for layout managers and more
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
//for action events
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 * TextSamplerDemo.java requires the following files:
 *   TextSamplerDemoHelp.html (which references images/dukeWaveRed.gif)
 *   images/Pig.gif
 *   images/sound.gif
 */
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class TextSamplerDemo extends JPanel implements ActionListener {

  private static final long serialVersionUID = 8554794008489043316L;
  private final String newline = "\n";
  protected static final String textFieldString = "JTextField";
  protected static final String passwordFieldString = "JPasswordField";
  protected static final String ftfString = "JFormattedTextField";
  protected static final String buttonString = "JButton";

  protected JLabel actionLabel;

  /**
   * test class.
   */
  public TextSamplerDemo() {
    setLayout(new BorderLayout());

    // Create a regular text field.
    final JTextField textField = new JTextField(10);
    textField.setActionCommand(textFieldString);
    textField.addActionListener(this);

    // Create a password field.
    final JPasswordField passwordField = new JPasswordField(10);
    passwordField.setActionCommand(passwordFieldString);
    passwordField.addActionListener(this);

    // Create a formatted text field.
    final JFormattedTextField ftf = new JFormattedTextField(
        java.util.Calendar.getInstance().getTime());
    ftf.setActionCommand(textFieldString);
    ftf.addActionListener(this);

    // Create some labels for the fields.
    final JLabel textFieldLabel = new JLabel(textFieldString + ": ");
    textFieldLabel.setLabelFor(textField);
    final JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
    passwordFieldLabel.setLabelFor(passwordField);
    final JLabel ftfLabel = new JLabel(ftfString + ": ");
    ftfLabel.setLabelFor(ftf);

    // Create a label to put messages during an action event.
    actionLabel = new JLabel("Type text in a field and press Enter.");
    actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    // Lay out the text controls and the labels.
    final JPanel textControlsPane = new JPanel();
    final GridBagLayout gridbag = new GridBagLayout();
    final GridBagConstraints c = new GridBagConstraints();

    textControlsPane.setLayout(gridbag);

    final JLabel[] labels = { textFieldLabel, passwordFieldLabel, ftfLabel };
    final JTextField[] textFields = { textField, passwordField, ftf };
    addLabelTextRows(labels, textFields, textControlsPane);

    c.gridwidth = GridBagConstraints.REMAINDER; // last
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1.0;
    textControlsPane.add(actionLabel, c);
    textControlsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
        "Text Fields"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Create a text area.
    final JTextArea textArea = new JTextArea(
        "This is an editable JTextArea. " + "A text area is a \"plain\" text component, "
            + "which means that although it can display text "
            + "in any font, all of the text is in the same font.");
    textArea.setFont(new Font("Serif", Font.ITALIC, 16));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    final JScrollPane areaScrollPane = new JScrollPane(textArea);
    areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    areaScrollPane.setPreferredSize(new Dimension(250, 250));
    areaScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Plain Text"), BorderFactory.createEmptyBorder(5, 5, 5,
            5)), areaScrollPane.getBorder()));

    // Create an editor pane.
    final JEditorPane editorPane = createEditorPane();
    final JScrollPane editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(250, 145));
    editorScrollPane.setMinimumSize(new Dimension(10, 10));

    // Create a text pane.
    final JTextPane textPane = createTextPane();
    final JScrollPane paneScrollPane = new JScrollPane(textPane);
    paneScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    paneScrollPane.setPreferredSize(new Dimension(250, 155));
    paneScrollPane.setMinimumSize(new Dimension(10, 10));

    // Put the editor pane and the text pane in a split pane.
    final JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        editorScrollPane,
        paneScrollPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setResizeWeight(0.5);
    final JPanel rightPane = new JPanel(new GridLayout(1, 0));
    rightPane.add(splitPane);
    rightPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
        "Styled Text"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Put everything together.
    final JPanel leftPane = new JPanel(new BorderLayout());
    leftPane.add(textControlsPane, BorderLayout.PAGE_START);
    leftPane.add(areaScrollPane, BorderLayout.CENTER);

    add(leftPane, BorderLayout.LINE_START);
    add(rightPane, BorderLayout.LINE_END);
  }

  private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, Container container) {
    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.EAST;
    final int numLabels = labels.length;

    for (int i = 0; i < numLabels; i++) {
      c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
      c.fill = GridBagConstraints.NONE; // reset to default
      c.weightx = 0.0; // reset to default
      container.add(labels[i], c);

      c.gridwidth = GridBagConstraints.REMAINDER; // end row
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      container.add(textFields[i], c);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final String prefix = "You typed \"";
    if (textFieldString.equals(e.getActionCommand())) {
      final JTextField source = (JTextField) e.getSource();
      actionLabel.setText(prefix + source.getText() + "\"");
    } else if (passwordFieldString.equals(e.getActionCommand())) {
      final JPasswordField source = (JPasswordField) e.getSource();
      actionLabel.setText(prefix + new String(source.getPassword()) + "\"");
    } else if (buttonString.equals(e.getActionCommand())) {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  private JEditorPane createEditorPane() {
    final JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    final java.net.URL helpURL = TextSamplerDemo.class.getResource("TextSamplerDemoHelp.html");
    if (helpURL != null) {
      try {
        editorPane.setPage(helpURL);
      } catch (final IOException e) {
        System.err.println("Attempted to read a bad URL: " + helpURL);
      }
    } else {
      System.err.println("Couldn't find file: TextSampleDemoHelp.html");
    }

    return editorPane;
  }

  private JTextPane createTextPane() {
    final String[] initString = { "This is an editable JTextPane, ", // regular
        "another ", // italic
        "styled ", // bold
        "text ", // small
        "component, ", // large
        "which supports embedded components..." + newline, // regular
        " " + newline, // button
        "...and embedded icons..." + newline, // regular
        " ", // icon
        newline + "JTextPane is a subclass of JEditorPane that "
            + "uses a StyledEditorKit and StyledDocument, and provides "
            + "cover methods for interacting with those objects." };

    final String[] initStyles = { "regular", "italic", "bold", "small", "large", "regular",
        "button", "regular", "icon", "regular" };

    final JTextPane textPane = new JTextPane();
    final StyledDocument doc = textPane.getStyledDocument();
    addStylesToDocument(doc);

    try {
      for (int i = 0; i < initString.length; i++) {
        doc.insertString(doc.getLength(), initString[i], doc.getStyle(initStyles[i]));
      }
    } catch (final BadLocationException ble) {
      System.err.println("Couldn't insert initial text into text pane.");
    }

    return textPane;
  }

  protected void addStylesToDocument(StyledDocument doc) {
    // Initialize some styles.
    final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    final Style regular = doc.addStyle("regular", def);
    StyleConstants.setFontFamily(def, "SansSerif");

    Style s = doc.addStyle("italic", regular);
    StyleConstants.setItalic(s, true);

    s = doc.addStyle("bold", regular);
    StyleConstants.setBold(s, true);

    s = doc.addStyle("small", regular);
    StyleConstants.setFontSize(s, 10);

    s = doc.addStyle("large", regular);
    StyleConstants.setFontSize(s, 16);

    s = doc.addStyle("icon", regular);
    StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
    final ImageIcon pigIcon = createImageIcon("images/Pig.gif", "a cute pig");
    if (pigIcon != null) {
      StyleConstants.setIcon(s, pigIcon);
    }

    s = doc.addStyle("button", regular);
    StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
    final ImageIcon soundIcon = createImageIcon("images/sound.gif", "sound icon");
    final JButton button = new JButton();
    if (soundIcon != null) {
      button.setIcon(soundIcon);
    } else {
      button.setText("BEEP");
    }
    button.setCursor(Cursor.getDefaultCursor());
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setActionCommand(buttonString);
    button.addActionListener(this);
    StyleConstants.setComponent(s, button);
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path, String description) {
    final java.net.URL imgURL = TextSamplerDemo.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked from the event
   * dispatch thread.
   */
  private static void createAndShowGUI() {
    // Create and set up the window.
    final JFrame frame = new JFrame("TextSamplerDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add content to the window.
    frame.add(new TextSamplerDemo());

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * test class main method.
   */
  public static void main(String[] args) {
    // Schedule a job for the event dispatching thread:
    // creating and showing this application's GUI.
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        // Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        createAndShowGUI();
      }
    });
  }
}