
package cps.gui.core;

import cps.core.ProgramPortal;
import cps.gui.tools.Constants;
import cps.gui.tools.GridBagUtil;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainFrame extends JPanel {

  // nf and labelBorder unused
  // private static final NumberFormat nf = NumberFormat.getCurrencyInstance();
  // private final Border labelBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

  private static final long serialVersionUID = -6201671873795688899L;
  // main parent frame
  public static JFrame frame = new JFrame("CPS Assistant");
  // main frame controller
  // private static MainFrameController mfc;
  // menu variables
  private JList<String> menuList;
  private static JScrollPane scrollableMenuList;
  private JLabel menuListLabel;
  private JPanel menuPane;
  private JPanel mainPanel = new JPanel();
  private JTextArea defaultText = new JTextArea(20, 50);
  // employee panel
  private static JPanel employeePanel;
  private static JPanel timeSheetPanel;

  public static void createAndShowGUI(ProgramPortal portal) {
    setFramePreferences();
    addFrameContent(portal);
    // Display the window.
    // frame.setMinimumSize(new Dimension(900, 50));
    frame.validate();
    frame.pack();
    frame.setVisible(true);
  }

  private static void setFramePreferences() {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getRootPane().setBorder(Constants.emptyBorder);
    frame.setLayout(new BorderLayout());
  }

  private static void addFrameContent(ProgramPortal portal) {
    employeePanel = new EmployeePanel(new EmployeePanelController(portal));
    timeSheetPanel = new TimeSheetPanel(new TimeSheetPanelController(portal));
    frame.add(new MainFrame(), BorderLayout.CENTER);
  }

  // private constructor and component setup
  private MainFrame() {
    super(new GridBagLayout());
    this.setupMenusList();
    this.addComponents();
  }

  private void setupMenusList() {
    menuList = new JList<>();
    String[] menuOptions = { "Employees", "TimeSheets" };
    menuList.setListData(menuOptions);
    menuListLabel = new JLabel();
    menuListLabel.setText("Menu Options");
    menuListLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    scrollableMenuList = new JScrollPane(menuList);
    scrollableMenuList.setPreferredSize(new Dimension(150, 300));
    scrollableMenuList.setAlignmentX(Component.CENTER_ALIGNMENT);
    menuPane = new JPanel();
    menuPane.setLayout(new BoxLayout(menuPane, BoxLayout.Y_AXIS));
    menuPane.add(menuListLabel);
    menuPane.add(scrollableMenuList);
    menuPane.setMinimumSize(new Dimension(150, 300));
    menuPane.setMaximumSize(new Dimension(150, 300));

    menuList.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (menuList.getSelectedIndex() == 0) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "EmployeePanel");
          } else if (menuList.getSelectedIndex() == 1) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "TimeSheetPanel");
          }
        }
      }
    });
    menuList.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (menuList.getSelectedIndex() == 0) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "EmployeePanel");
          } else if (menuList.getSelectedIndex() == 1) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "TimeSheetPanel");
          }
        }
      }
    });
  }

  public String getSelectedMenu() {
    return menuList.getSelectedValue();
  }

  private void addComponents() {
    // JPanel actionLabelDescriptionPane = new JPanel();
    // actionLabelDescriptionPane.setLayout(new GridBagLayout());
    mainPanel.setLayout(new CardLayout());
    GridBagUtil.addComponent(this, menuPane, 0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.VERTICAL, new Insets(0, 10, 0, 10));
    GridBagUtil.addComponent(this, mainPanel, 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
        GridBagConstraints.BOTH, new Insets(18, 10, 0, 10));
    mainPanel.add(employeePanel, "EmployeePanel");
    mainPanel.add(timeSheetPanel, "TimeSheetPanel");
    mainPanel.add(defaultText, "Default");
    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Default");
  }

}
