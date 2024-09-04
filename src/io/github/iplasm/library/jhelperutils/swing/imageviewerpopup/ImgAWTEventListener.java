package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Predicate;
import javax.swing.SwingUtilities;


public abstract class ImgAWTEventListener<T extends Component> implements AWTEventListener {
  private ViewerPopup<T> popup = createViewerPopup();
  private Class<T> clazz;

  public ImgAWTEventListener(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public void eventDispatched(AWTEvent e) {
    if (!(e.getSource() instanceof Component)) {
      return;
    }

    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    boolean isAnotherWindowOnTop = focusOwner != null &&
        SwingUtilities.getWindowAncestor((Component) e.getSource()) != SwingUtilities
            .getWindowAncestor(focusOwner);
    if (!(e.getSource().getClass().isAssignableFrom(clazz)) || isAnotherWindowOnTop) {
      return;
    }

    Component component = (Component) e.getSource();

    if (popup.hookedComponent != null && popup.hookedComponent != component) {
      for (MouseListener l : popup.hookedComponent.getMouseListeners()) {
        if (l instanceof ImageViewerListener) {
          popup.hookedComponent.removeMouseListener(l);
          break;
        }
      }
    } else if (popup.hookedComponent != null && popup.hookedComponent == component) {
      return;
    }

    ImageViewerListener<T> imageViewerlistener =
        new ImageViewerListener<T>((T) component, popup, getImgAvailablePredicate());
    component.addMouseListener(imageViewerlistener);
    imageViewerlistener.mouseEntered(
        new MouseEvent(component, -1, System.currentTimeMillis(), 0, 0, 0, 0, 0, 0, false, 0));
  }

  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.startsWith("windows");
  }

  protected abstract ViewerPopup<T> createViewerPopup();

  protected abstract Predicate<T> getImgAvailablePredicate();

}
