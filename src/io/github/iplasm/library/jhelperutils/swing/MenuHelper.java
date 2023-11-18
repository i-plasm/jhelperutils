package io.github.iplasm.library.jhelperutils.swing;

import java.util.concurrent.Callable;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MenuHelper {

  public static JMenuItem makeMenuItem(String text, String name, Runnable doThis) {
    JMenuItem menuItem = new JMenuItem(text);
    menuItem.setName(name);
    menuItem.addActionListener(l -> doThis.run());
    return menuItem;
  }

  public static class FloatingMsgPopup extends JPopupMenu {
  }

  public static String getMenuItemValidation(String defaultMenuItemText, String name,
      Runnable doThis, Callable<String> prevalidator) {
    String validation = defaultMenuItemText;
    try {
      String prevalidation = prevalidator.call();
      if (prevalidation == null || !prevalidation.equals(""))
        validation = prevalidation;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return validation;
  }

  public static JMenuItem makeMenuItem(String text, String name, Runnable doThis,
      int maxTextLength) {
    if (text.length() > maxTextLength)
      text = text.substring(0, maxTextLength) + "...";
    JMenuItem menuItem = new JMenuItem(text);
    menuItem.setName(name);
    menuItem.addActionListener(l -> doThis.run());
    return menuItem;
  }

  public static JMenuItem createHeaderNoticeMenuItem(String message, String title) {
    message = "<html><body width='600px'; style=\"font-size: 12px\">"
        + "[x CLOSE]&nbsp;&nbsp;&nbsp;&nbsp;" + title + "<br><br>" + message + "<br><br>"
        + "</body></html>";
    JMenuItem menuItem = new JMenuItem(message);
    return menuItem;
  }

  public static JMenuItem createContentNoticeMenuItem(String message, String title) {
    message = "<html><body width='600px'; style=\"font-size: 12px\">" + "<br>" + title + "<br><br>"
        + message + "<br><br>" + "</body></html>";
    JMenuItem menuItem = new JMenuItem(message);
    return menuItem;
  }

  public static String floatingMenuItemUnderlinedActionHTML(String legend,
      String actionDisplayTitle) {
    return String.format("%s%s%s", "<html><body width='600px'; style=\"font-size: 12px\">",
        legend + "<br> <a href=\"" + actionDisplayTitle + "\">" + actionDisplayTitle + "</a>",
        "</body></html>");
  }

  /*
   * This panel is meant to be added to a FloatingMsgPopup
   */
  public static JPanel createFloatingActionPanel(String menuText, String name, String buttonText,
      Runnable runnable) {
    Box b1 = Box.createHorizontalBox();
    JPanel panel = new JPanel();
    JLabel label = new JLabel(menuText);
    JButton actionButton = new JButton(buttonText);
    panel.add(label);
    panel.add(actionButton);
    b1.add(panel);
    b1.add(Box.createHorizontalGlue());
    actionButton.addActionListener(l -> {
      actionButton.setEnabled(false);
      runnable.run();
    });
    actionButton.setFocusable(false);
    return panel;
  }


}
