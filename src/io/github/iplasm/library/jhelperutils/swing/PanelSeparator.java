package io.github.iplasm.library.jhelperutils.swing;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class PanelSeparator extends JPanel {
  public PanelSeparator(Color color) {
    Border border = BorderFactory.createMatteBorder(3, 0, 0, 0, color);
    setBorder(
        BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 0, 0, 0)));
    setBackground(new Color(0, 0, 0, 2));
    Dimension size = new Dimension(120, 6);
    setSize(size);
    setPreferredSize(new Dimension(size));
    setMaximumSize(new Dimension(size));
  }
}
