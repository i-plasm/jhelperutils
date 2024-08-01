package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import io.github.iplasm.library.jhelperutils.swing.HoverButton;


public abstract class ViewerPopup<T extends Component> extends JPopupMenu {

  public static final String PREVIEW_COMP_NAME = "img_preview_hover";

  HoverButton btnPreview;
  T hookedComponent;
  private boolean isCurrentlyDisplayingPreviewTip = false;
  private String previousImage;
  private String currentImage;
  private String imgBackground;

  private ViewerPopup() {
    GridBagConstraints c = new GridBagConstraints();
    this.setLayout(new GridBagLayout());

    // unicode info button
    HoverButton btnInfo = new HoverButton("<html><p><font size=12>&#128712;</font></p></html>");
    btnInfo.setToolTipText("Information & Help");
    // unicode opposition button
    HoverButton btnCopyPath = new HoverButton("<html><p><font size=12>&#9741;</font></p></html>");
    btnCopyPath.setToolTipText("Copy Image URL");

    // unicode Left-Pointing Magnifying Glass button
    btnPreview = new HoverButton("<html><p><font size=12>&#128269;</font></p></html>") {

      @Override
      public Point getToolTipLocation(MouseEvent event) {
        Point p = btnPreview.getLocationOnScreen();
        java.awt.MouseInfo.getPointerInfo().getLocation();

        SwingUtilities.convertPointFromScreen(p, btnPreview);

        JFrame dummyHiddenFrame = getDummyFrame();

        p = new Point(p.x + btnPreview.getBounds().width - btnPreview.getBounds().width / 4, p.y
            + btnPreview.getBounds().height / 2 - dummyHiddenFrame.getPreferredSize().height / 2);
        dummyHiddenFrame.dispose();
        return p;
      }

      private JFrame getDummyFrame() {
        String previousImage = ViewerPopup.this.previousImage;
        String currentImage = ViewerPopup.this.currentImage;
        // if (dummyHiddenFrame != null && previousImage != null &&
        // currentImage.equals(previousImage)) {
        // return dummyHiddenFrame;
        // }
        ViewerPopup.this.previousImage = currentImage;
        JLabel label = new JLabel(imageHTML(currentImage));
        JFrame dummyHiddenFrame = new JFrame();
        dummyHiddenFrame.add(label);
        dummyHiddenFrame.pack();
        return dummyHiddenFrame;
      }

    };

    btnPreview.addActionListener(
        l -> previewFullSize(getCurrentImage(), ViewerPopup.this.getImgBackground()));
    btnCopyPath.addActionListener(l -> copyURL());
    btnInfo.addActionListener(l -> displayHelp());
    final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();

    MouseAdapter exitAdapter = new MouseAdapter() {

      @Override
      public void mouseExited(MouseEvent e) {

        JPopupMenu popup = ViewerPopup.this;

        Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
        boolean isPointContainedInHookedComp = p.x >= getHookedComponent().getLocationOnScreen().x
            && p.x <= (getHookedComponent().getWidth()
                + getHookedComponent().getLocationOnScreen().x)
            && p.y >= getHookedComponent().getLocationOnScreen().y
            && p.y <= (getHookedComponent().getHeight()
                + getHookedComponent().getLocationOnScreen().y);
        SwingUtilities.convertPointFromScreen(p, popup);

        if (popup != null && !isPointContainedInHookedComp && !popup.getVisibleRect().contains(p)
            && !isCurrentlyDisplayingPreviewTip) {
          popup.setVisible(false);
        }

      };
    };

    btnPreview.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        btnPreview.setToolTipText(imageHTMLWithBgColor(ViewerPopup.this.getCurrentImage(),
            ViewerPopup.this.getImgBackground()));
        isCurrentlyDisplayingPreviewTip = true;
      };

      @Override
      public void mouseExited(MouseEvent e) {
        Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, btnPreview);
        if (btnPreview.getVisibleRect().contains(p)) {
          return;
        }
        hidePreviewToolTip(defaultInitialDelay);
      }

    });

    btnInfo.addMouseListener(exitAdapter);
    btnCopyPath.addMouseListener(exitAdapter);

    addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        if (isCurrentlyDisplayingPreviewTip) {
          hidePreviewToolTip(defaultInitialDelay);
        }

      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        hidePreviewToolTip(defaultInitialDelay);
      }
    });

    this.add(btnPreview);
    this.add(btnCopyPath);
    this.add(btnInfo);

    c.weightx = 1d;
    c.weighty = 0d;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.CENTER;
    c.gridy = 0;
    c.insets = new Insets(2, 0, 2, 0);
    c.gridwidth = 4;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.insets = new Insets(0, 0, 0, 0);
  }

  private void copyURL() {

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard == null) {
      JOptionPane.showMessageDialog(null, "Failed to copy to clipboard", "",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    StringSelection sel = new StringSelection(getCurrentImage());
    try {
      clipboard.setContents(sel, null);
      JOptionPane.showMessageDialog(null, "Copied to clipboard:\n" + getCurrentImage(), "",
          JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Failed to copy to clipboard", "",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }

  }

  private void displayHelp() {
    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ViewerPopup.this),
        "Learn more about this tool, and get updates on Github.", "Information & Help",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void hidePreviewToolTip(final int defaultDismissTimeout) {
    btnPreview.setToolTipText("");
    ToolTipManager.sharedInstance().mouseMoved(
        new MouseEvent(btnPreview, -1, System.currentTimeMillis(), 0, 0, 0, 0, 0, 0, false, 0));
    ToolTipManager.sharedInstance().setInitialDelay(defaultDismissTimeout);
    isCurrentlyDisplayingPreviewTip = false;
  };

  public void hookToComponent(T component) {
    this.hookedComponent = component;
    String compImg = getImageURI().toString();
    this.previousImage = previousImage == null ? compImg : currentImage;
    this.currentImage = compImg;
    this.imgBackground = getBackgroundColor();
    setVisible(false);
  }

  public boolean isCurrentlyDisplayingPreviewTip() {
    return isCurrentlyDisplayingPreviewTip;
  }

  public Component getHookedComponent() {
    return hookedComponent;
  }

  public String getCurrentImage() {
    return currentImage;
  }

  public String getImgBackground() {
    return imgBackground;
  }

  private static String imageHTML(String uri) {
    String str = "<img src = \"" + uri + "\">";
    return "<html><body>" + str + "</body></html>";
  }

  private static String imageHTMLWithBgColor(String uri, String bgColor) {
    String str = "<img src = \"" + uri + "\">";
    return "<html><body style=\"background-color:" + bgColor + ";\">" + str + "</body></html>";
  }

  static void previewFullSize(String imgUrl, String bgColor) {
    String html = imageHTMLWithBgColor(imgUrl, bgColor);
    JLabel label = new JLabel(html);
    JFrame frame;
    frame = new JFrame();
    frame.setName(PREVIEW_COMP_NAME);
    frame.setLayout(new FlowLayout(FlowLayout.CENTER));
    frame.add(label);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  protected abstract String getBackgroundColor();

  protected abstract URI getImageURI();

}
