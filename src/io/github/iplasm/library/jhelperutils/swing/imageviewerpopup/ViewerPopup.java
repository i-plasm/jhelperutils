package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.net.URI;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import io.github.iplasm.library.jhelperutils.swing.HoverButton;


public abstract class ViewerPopup<T extends Component> extends JPopupMenu {

  public static final String PREVIEW_COMP_NAME = "img_preview_hover";

  HoverButton btnPreview;
  T hookedComponent;
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
    btnPreview = new HoverButton("<html><p><font size=12>&#128269;</font></p></html>");

    btnCopyPath.addActionListener(l -> copyURL());
    btnInfo.addActionListener(l -> displayHelp());

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

        if (popup != null && !isPointContainedInHookedComp && !popup.getVisibleRect().contains(p)) {
          popup.setVisible(false);
        }

      };
    };

    btnPreview.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        JFrame hoverWindow = createHoverWindow(ViewerPopup.this.getCurrentImage(),
            ViewerPopup.this.getImgBackground(), ViewerPopup.this.getSuggstedPreviewLocation(),
            SwingUtilities.getWindowAncestor(ViewerPopup.this).getGraphicsConfiguration());
        hoverWindow.setVisible(true);
      };
    });

    btnInfo.addMouseListener(exitAdapter);
    btnCopyPath.addMouseListener(exitAdapter);

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

  public void hookToComponent(T component) {
    this.hookedComponent = component;
    String compImg = getImageURI().toString();
    this.previousImage = previousImage == null ? compImg : currentImage;
    this.currentImage = compImg;
    this.imgBackground = getBackgroundColor();
    setVisible(false);
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

  public static Rectangle getMaxWindowBounds(GraphicsConfiguration config) {
    Rectangle bounds = null;
    bounds = config.getBounds();
    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
    System.out.println("bounds" + bounds.x + "; " + bounds.y + ";  width:" + bounds.width
        + "; height: " + bounds.height);
    System.out.println("insets" + "left: " + insets.left + " ; right: " + insets.right + "; top: "
        + insets.top + "; bottom: " + insets.bottom);
    bounds.x += insets.left;
    bounds.y += insets.top;
    bounds.width -= insets.left + insets.right;
    bounds.height -= insets.top + insets.bottom;


    return bounds;
  }

  static JFrame createHoverWindow(String imgUrl, String bgColor, Point suggestedLocation,
      GraphicsConfiguration config) {
    String html = imageHTMLWithBgColor(imgUrl, bgColor);
    JLabel label = new JLabel(html);
    JFrame frame;
    JFrame dummyFrame = new JFrame();
    // frame.setName(PREVIEW_COMP_NAME);

    dummyFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
    dummyFrame.add(label);
    dummyFrame.pack();
    Rectangle maxBounds = getMaxWindowBounds(config);
    Rectangle dummyFrameBounds = dummyFrame.getBounds();
    boolean isImgShowingFully = dummyFrame.getBounds().width < maxBounds.width
        && dummyFrame.getBounds().height < maxBounds.height;
    dummyFrame.dispose();

    if (!isImgShowingFully) {
      frame = new JFrame();
      // frame.setName(PREVIEW_COMP_NAME);
      final JPanel panel = new JPanel() {
        @Override
        public Dimension getPreferredSize() {
          return new Dimension(label.getBounds().width, label.getBounds().height);
        }

      };

      panel.add(label);
      JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int width = frame.getWidth();
          int height = frame.getHeight();
          Point location = frame.getLocation();
          frame.setVisible(false);
          frame.dispose();
          previewFullSize(imgUrl, bgColor, width, height, location);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          if (!label.isShowing()) {
            return;
          }
          if ((e.getLocationOnScreen().y < label.getLocationOnScreen().y)
              || (e.getLocationOnScreen().y < scrollPane.getViewportBorderBounds().y)) {
            frame.setVisible(false);
            frame.dispose();
          }
        }

      });

      frame.addWindowListener(new WindowAdapter() {

        @Override
        public void windowDeactivated(WindowEvent e) {
          if (frame.isVisible()) {
            frame.setVisible(false);
            frame.dispose();
          }
        }
      });
      frame.addWindowFocusListener(new WindowFocusListener() {
        @Override
        public void windowLostFocus(WindowEvent e) {
          if (frame.isVisible()) {
            frame.setVisible(false);
            frame.dispose();
          }
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {}
      });

      frame.add(scrollPane);
      frame.setUndecorated(true);
      frame.setSize(maxBounds.width, maxBounds.height);
      // frame.pack();
      frame.setLocation(0, 0);
    } else {
      frame = new JFrame();
      // frame.setName(PREVIEW_COMP_NAME);
      frame.setUndecorated(true);
      frame.setLayout(new FlowLayout(FlowLayout.CENTER));
      frame.add(label);


      int x = maxBounds.width > dummyFrameBounds.width + suggestedLocation.x ? suggestedLocation.x
          : maxBounds.width - dummyFrameBounds.width;
      int y = maxBounds.height > dummyFrameBounds.height + suggestedLocation.y ? suggestedLocation.y
          : maxBounds.height - dummyFrameBounds.height;

      frame.setLocation(x, y);
      frame.pack();

      label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
          frame.setVisible(false);
          frame.dispose();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
          Point location = frame.getLocation();
          frame.setVisible(false);
          frame.dispose();
          previewFullSize(imgUrl, bgColor, maxBounds, location);
        }

      });

    }

    addEscapeListener(frame);
    return frame;
  }

  public Point getSuggstedPreviewLocation() {
    return btnPreview.getLocationOnScreen();
  }

  public static void addEscapeListener(JFrame frame) {
    Action dispatchClosing = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
      }
    };

    KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    JRootPane rootPane = frame.getRootPane();
    rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "closeWindow");
    rootPane.getActionMap().put("closeWindow", dispatchClosing);
  }

  static void previewFullSize(String imgUrl, String bgColor, Rectangle maxBounds,
      Point suggestedLocation) {
    String html = imageHTMLWithBgColor(imgUrl, bgColor);
    JLabel label = new JLabel(html);
    JFrame frame;
    frame = new JFrame();
    frame.setName(PREVIEW_COMP_NAME);
    final JPanel panel = new JPanel();
    panel.add(label);
    JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    frame.add(scrollPane);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addEscapeListener(frame);
    frame.pack();
    int x = maxBounds.width > frame.getBounds().width + suggestedLocation.x ? suggestedLocation.x
        : maxBounds.width - frame.getBounds().width;
    int y = maxBounds.height > frame.getBounds().height + suggestedLocation.y ? suggestedLocation.y
        : maxBounds.height - frame.getBounds().height;

    frame.setLocation(new Point(x, y));
    frame.setVisible(true);
  }

  static void previewFullSize(String imgUrl, String bgColor, int width, int height,
      Point location) {
    String html = imageHTMLWithBgColor(imgUrl, bgColor);
    JLabel label = new JLabel(html);
    JFrame frame;
    frame = new JFrame();
    frame.setName(PREVIEW_COMP_NAME);
    final JPanel panel = new JPanel();
    panel.add(label);
    JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    frame.add(scrollPane);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addEscapeListener(frame);
    frame.pack();
    frame.setSize(width, height);
    frame.setLocation(location);
    frame.setVisible(true);
  }


  protected abstract String getBackgroundColor();

  protected abstract URI getImageURI();

}
