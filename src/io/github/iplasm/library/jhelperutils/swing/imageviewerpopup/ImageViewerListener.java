package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Predicate;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ImageViewerListener<T extends Component> extends MouseAdapter {

  private T component;
  private Timer viewerTimer;
  private ViewerPopup<T> popup;

  public ImageViewerListener(T component, ViewerPopup<T> popup, Predicate<T> isValidImage) {
    this.component = component;
    this.popup = popup;
    popup.hookToComponent(component);
    this.viewerTimer = new Timer(200, null);

    ActionListener timerAction = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        viewerTimer.stop();
        Point pos = component.getMousePosition();
        if (pos == null) {
          return;
        }

        boolean isValidImagePresent = isValidImage.test(component);

        if (!isValidImagePresent) {
          if (popup != null) {
            popup.setVisible(false);
          }
          return;
        }
        if (popup != null && !popup.isShowing()) {
          int x = pos.x - popup.getWidth() / 2;
          int y = pos.y;

          popup.show(component, x, y);
        }

      }
    };

    this.viewerTimer.addActionListener(timerAction);

    for (MouseListener l : popup.getMouseListeners()) {
      if (PopupAdapter.class.isInstance(l)) {
        popup.removeMouseListener(l);
        break;
      }
    }
    popup.addMouseListener(new PopupAdapter());
  }

  @Override
  public void mouseExited(MouseEvent e) {
    Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
    // SwingUtilities.convertPointFromScreen(p, popup);

    if (popup.isShowing()) {
      boolean isPointContainedInPopup = p.x >= popup.getLocationOnScreen().x
          && p.x <= (popup.getWidth() + popup.getLocationOnScreen().x)
          && p.y >= popup.getLocationOnScreen().y
          && p.y <= (popup.getHeight() + popup.getLocationOnScreen().y);

      if (!isPointContainedInPopup) {
        popup.setVisible(false);
      }
      return;
    }

  }

  @Override
  public void mouseEntered(MouseEvent e) {
    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    boolean isAnotherWindowOnTop = focusOwner != null
        && SwingUtilities.getWindowAncestor((Component) e.getSource()) != SwingUtilities
            .getWindowAncestor(focusOwner);
    if (popup.isShowing() || isAnotherWindowOnTop || focusOwner == null) {
      return;
    }
    viewerTimer.start();
    viewerTimer.setRepeats(false);
  }

  class PopupAdapter extends MouseAdapter {

    @Override
    public void mouseExited(MouseEvent e) {
      if (!component.isShowing()) {
        return;
      }
      Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
      boolean isPointContainedInBitmapViewer = p.x >= component.getLocationOnScreen().x
          && p.x <= (component.getWidth() + component.getLocationOnScreen().x)
          && p.y >= component.getLocationOnScreen().y
          && p.y <= (component.getHeight() + component.getLocationOnScreen().y);

      if (!isPointContainedInBitmapViewer) {
        popup.setVisible(false);
      }
    }


  }

}
