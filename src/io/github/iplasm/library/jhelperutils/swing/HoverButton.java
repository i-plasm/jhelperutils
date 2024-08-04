package io.github.iplasm.library.jhelperutils.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class HoverButton extends JButton {
  public HoverButton(String text) {
    super(text);
    setBorderPainted(false);
    setBackground(UIManager.getColor("control"));
    setBorder(new EmptyBorder(5, 5, 5, 5));
    setFocusable(false);

    Color selColor = UIManager.getColor("MenuItem.selectionBackground");

    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        setBackground(selColor.brighter());
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
        setBackground(UIManager.getColor("control"));
      }
    });

    addAncestorListener(new AncestorListener() {
      @Override
      public void ancestorRemoved(AncestorEvent event) {
        setBackground(UIManager.getColor("control"));
      }

      @Override
      public void ancestorMoved(AncestorEvent event) {}

      @Override
      public void ancestorAdded(AncestorEvent event) {}
    });
  }
}
