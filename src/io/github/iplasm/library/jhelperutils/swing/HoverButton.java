package io.github.iplasm.library.jhelperutils.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class HoverButton extends JButton {
  private static final Font MODEL_FONT = new JButton().getFont();

  public HoverButton(String text) {
    super(text);
    setBorderPainted(false);
    setBackground(UIManager.getColor("control"));
    setBorder(new EmptyBorder(5, 5, 5, 5));
    setFocusable(false);
    setFont(MODEL_FONT.deriveFont(40f).deriveFont(Font.PLAIN));

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
    
    addHierarchyListener(new HierarchyListener() {
      @Override
      public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 ||
            (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
          if (!e.getComponent().isDisplayable()) {
            setBackground(UIManager.getColor("control"));
          }
        }
      }
    });
  }
}
