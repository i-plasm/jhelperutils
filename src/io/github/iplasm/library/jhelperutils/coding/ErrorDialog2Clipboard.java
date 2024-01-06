package io.github.iplasm.library.jhelperutils.coding;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Utility to detect whenever a JOptionPane dialog displaying an error message appears, allowing the
 * user to copy the error to clipboard.
 * 
 * <p>
 * For initializing this tool in the default configuration, use <code>defaultInit</code> method. For
 * non-default configuration, invoke any of the constructors provided and then invoke
 * <code>activateListener()</code>.
 * 
 * <p>
 * To stop monitoring: if <code>shouldShowStopMonitoringButton</code> was set to true, the user can
 * stop monitoring via the GUI. In order to stop the monitoring programmatically, invoke
 * <code>removeListener</code>
 */
public class ErrorDialog2Clipboard implements PropertyChangeListener {

  enum TitleCriterium {
    BEGINS, EQUALS, CONTAINS
  };

  private String targetErrorDialogTitle;
  private boolean shouldShowStopMonitoringButton;

  private JDialog lastJDialog = null;
  private String errorString = "";

  static void defaultInit(String targetErrorDialogTitle) {
    Window fosusedWindow = null;
    for (Window w : Window.getWindows()) {
      if (w.isFocused()) {
        fosusedWindow = w;
      }
    }
    List<PropertyChangeListener> filteresListeners =
        ErrorDialog2Clipboard.countAllActivatedListeners();

    if (filteresListeners.size() == 0) {
      ErrorDialog2Clipboard l = new ErrorDialog2Clipboard(targetErrorDialogTitle, true);
      l.activateListener();
    } else if (filteresListeners.size() == 0) {
      JOptionPane.showMessageDialog(fosusedWindow, "ErrorDialog2Clipboard is already running",
          "ErrorDialog2Clipboard", JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(fosusedWindow,
          "There should not be more than one registered ErrorDialog2Clipboard listeners! ",
          "ErrorDialog2Clipboard", JOptionPane.ERROR_MESSAGE);
    }
  }

  public ErrorDialog2Clipboard(String targetErrorDialogTitle,
      boolean shouldShowStopMonitoringButton) {
    super();
    this.targetErrorDialogTitle = targetErrorDialogTitle;
    this.shouldShowStopMonitoringButton = shouldShowStopMonitoringButton;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getNewValue() != null) {

      // Error dialog is currently being displayed
      if (lastJDialog != null && lastJDialog.isShowing()) {
        return;
      }

      // Error dialog has been just closed.
      if (lastJDialog != null) {
        // lastDialog must be nullified before invoking the method
        lastJDialog = null;
        showCopyToClipboardMessage(errorString);
        errorString = null;
        return;
      }

      // Detecting a newly opened and focused error dialog
      Window window = SwingUtilities.getWindowAncestor((Component) evt.getNewValue());

      if (window instanceof JDialog
          && ((JDialog) window).getTitle().equalsIgnoreCase(targetErrorDialogTitle)) {

        JDialog currentDialog = (JDialog) window;

        if (currentDialog.getContentPane().getComponentCount() == 1
            && currentDialog.getContentPane().getComponent(0) instanceof JOptionPane) {

          JOptionPane joptionPane = (JOptionPane) currentDialog.getContentPane().getComponent(0);
          if (joptionPane.getMessageType() == JOptionPane.ERROR_MESSAGE) {
            // A target error dialog has been found!
            lastJDialog = currentDialog;
            errorString = joptionPane.getMessage().toString();
          }
        }
      }
    }
  }

  public static List<PropertyChangeListener> countAllActivatedListeners() {
    List<PropertyChangeListener> filteresListeners = Stream
        .of(KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .getPropertyChangeListeners("permanentFocusOwner"))
        .filter(it -> it.getClass().getName().contains(ErrorDialog2Clipboard.class.getName()))
        .collect(Collectors.toList());
    return filteresListeners;
  }

  public void activateListener() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addPropertyChangeListener("permanentFocusOwner", this);
  }

  public void removeListener() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .removePropertyChangeListener("permanentFocusOwner", this);
  }

  public void showCopyToClipboardMessage(String errorString) {

    String msg = "Error message detected:" + System.lineSeparator() + System.lineSeparator()
        + (errorString.length() > 100 ? errorString.substring(0, 100) + "..." : errorString);

    Window fosusedWindow = null;
    for (Window w : Window.getWindows()) {
      if (w.isFocused()) {
        fosusedWindow = w;
      }
    }
    String[] options;

    if (shouldShowStopMonitoringButton) {
      options = new String[] {"Copy to Clipboard", "No", "Stop monitoring"};
    } else {
      options = new String[] {"Copy to Clipboard", "No"};
    }

    int result = JOptionPane.showOptionDialog(fosusedWindow, msg, "ErrorDialog2Clipboard",
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

    if (result == 0) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (clipboard == null) {
        JOptionPane.showMessageDialog(fosusedWindow, "Failed to copy to clipboard",
            "ErrorDialog2Clipboard", JOptionPane.ERROR_MESSAGE);
      }
      StringSelection sel = new StringSelection(errorString);
      try {
        clipboard.setContents(sel, null);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(fosusedWindow, "Failed to copy to clipboard",
            "ErrorDialog2Clipboard", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
      }
    } else if (result == 2) {
      removeListener();
    }
  }

}
