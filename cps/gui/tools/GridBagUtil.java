
package cps.gui.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.text.JTextComponent;

public class GridBagUtil {

  public static class Builder {

    private int gridx = GridBagConstraints.RELATIVE;
    private int gridy = GridBagConstraints.RELATIVE;
    private int gridwidth = 1;
    private int gridheight = 1;
    private double weightx = 0;
    private double weighty = 0;
    private int anchor = GridBagConstraints.CENTER;
    private int fill = GridBagConstraints.NONE;
    private Insets insets = new Insets(0, 0, 0, 0);
    private int ipadx = 0;
    private int ipady = 0;

    public Builder gridx(int gridx) {
      this.gridx = gridx;
      return this;
    }

    public Builder gridy(int gridy) {
      this.gridy = gridy;
      return this;
    }

    public Builder gridwidth(int gridwidth) {
      this.gridwidth = gridwidth;
      return this;
    }

    public Builder gridheight(int gridheight) {
      this.gridheight = gridheight;
      return this;
    }

    public Builder weightx(double weightx) {
      this.weightx = weightx;
      return this;
    }

    public Builder weighty(double weighty) {
      this.weighty = weighty;
      return this;
    }

    public Builder anchor(int anchor) {
      this.anchor = anchor;
      return this;
    }

    public Builder fill(int fill) {
      this.fill = fill;
      return this;
    }

    public Builder insets(Insets insets) {
      this.insets = insets;
      return this;
    }

    public Builder ipadx(int ipadx) {
      this.ipadx = ipadx;
      return this;
    }

    public Builder weighty(int ipady) {
      this.ipady = ipady;
      return this;
    }

    public GridBagConstraints build() {
      return new GridBagConstraints(
          gridx,
          gridy,
          gridwidth,
          gridheight,
          weightx,
          weighty,
          anchor,
          fill,
          insets,
          ipadx,
          ipady);
    }
  }

  public static boolean checkJTextComponentForEmpty(JTextComponent... components) {
    return Arrays.asList(components).stream().anyMatch(component -> component.getText().isEmpty());
  }

  public static void addComponent(Container container, Component component) {
    container.add(component, new GridBagUtil.Builder().build());
  }

  // add a component to a container with most common GridBagConstraints
  public static void addComponent(Container container, Component component, int gridx, int gridy,
      int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
    final GridBagConstraints gbc = new GridBagConstraints(
        gridx,
        gridy,
        gridwidth,
        gridheight,
        weightx,
        weighty,
        anchor,
        fill,
        new Insets(0, 0, 0, 0),
        0,
        0);
    container.add(component, gbc);
  }

  public static void addComponent(Container container, Component component, int gridx, int gridy,
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

  public static void addComponent(Container container, Component component, int gridx, int gridy,
      int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill,
      Insets insets, int ipadx, int ipady) {
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
        ipadx,
        ipady);
    container.add(component, gbc);
  }
}
