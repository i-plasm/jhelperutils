package io.github.iplasm.library.jhelperutils.swing.notifications;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import io.github.iplasm.library.jhelperutils.swing.GradientJPanel;

public class SwingNotifications {

  enum Location {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;
  }

  public enum MessageType {
    INFO, ERROR
  }

  private boolean useDarkModeFont = false;
  private boolean autoClose = false;

  private final JFrame auxFrame;
  private final JTextArea messagePane;
  private final JTextArea callerAppPane;
  private final JTextArea subjectPane;
  private final Timer timer;
  private final GradientJPanel panel;
  private final Location location = Location.TOP_RIGHT;
  private final JLabel expandLabel;
  private final JPanel glass;

  private int calculatedMinHeight;
  private boolean isMaximizedState;

  private static final int SECONDS = 5;
  private static final int WIDTH = 300;
  private static final int MIN_HEIGHT = 60;
  private static final int MAX_HEIGHT = 160;
  private static final Color DEFAULT_FIRST_COLOR = Color.decode("#9adbfe");
  private static final Color DEFAULT_SECOND_COLOR = Color.WHITE;
  private static final Color LIGHT_FONT_COLOR = Color.WHITE.darker();
  private static final Color DARK_FONT_COLOR = new Color(75, 75, 75);;

  private static SwingNotifications instance;
  private static final List<NotificationData> NOTIFICATIONS_LOG = new ArrayList<>();

  public static SwingNotifications getInstance() {
    if (instance == null) {
      instance = new SwingNotifications();
    }
    return instance;
  }

  private SwingNotifications() {

    Font liberationFont = null;

    List<String> liberationFontList = Stream
        .of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
        .filter(it -> it.toLowerCase().contains("liberation sans")).collect(Collectors.toList());
    if (!liberationFontList.isEmpty()) {
      liberationFont = new Font(liberationFontList.get(0), Font.PLAIN, 10);
    }

    auxFrame = new JFrame();
    auxFrame.setUndecorated(true);
    auxFrame.setAlwaysOnTop(true);
    auxFrame.setAutoRequestFocus(false);
    auxFrame.setLocationRelativeTo(null);
    auxFrame.setFocusableWindowState(false);

    messagePane = new JTextArea();
    subjectPane = new JTextArea();
    callerAppPane = new JTextArea();

    JSeparator separator = new javax.swing.JSeparator(SwingConstants.HORIZONTAL);
    separator.setPreferredSize(new Dimension(0, 2));
    separator.setOpaque(true);

    panel = new GradientJPanel();
    panel.setFirstColor(DEFAULT_FIRST_COLOR);
    panel.setSecondColor(DEFAULT_SECOND_COLOR);
    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    panel.setLayout(new BorderLayout(5, 5));

    JPanel backgroundPanel = new JPanel();
    backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.PAGE_AXIS));
    panel.setShadowThickness(5);
    panel.setSecondPointX(200);
    panel.add(backgroundPanel, BorderLayout.CENTER);

    backgroundPanel.add(callerAppPane);
    backgroundPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    backgroundPanel.add(separator);
    backgroundPanel.add(Box.createRigidArea(new Dimension(0, 5)));

    messagePane.setMargin(new Insets(0, 0, 0, 0));
    subjectPane.setMargin(new Insets(0, 0, 0, 0));
    callerAppPane.setMargin(new Insets(0, 0, 0, 0));
    messagePane.setLineWrap(true);
    messagePane.setWrapStyleWord(true);
    messagePane.setAlignmentX(Component.LEFT_ALIGNMENT);
    messagePane.setOpaque(false);
    callerAppPane.setLineWrap(true);
    callerAppPane.setWrapStyleWord(true);
    callerAppPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    callerAppPane.setOpaque(false);
    subjectPane.setLineWrap(true);
    subjectPane.setWrapStyleWord(true);
    subjectPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    subjectPane.setOpaque(false);

    separator.setBackground(Color.LIGHT_GRAY);
    separator.setForeground(Color.LIGHT_GRAY);

    if (liberationFont != null) {
      callerAppPane.setFont(liberationFont.deriveFont(Font.BOLD, 12));
      messagePane.setFont(liberationFont.deriveFont(Font.PLAIN, 15));
      subjectPane.setFont(liberationFont.deriveFont(Font.BOLD, 13));
    } else {
      callerAppPane.setFont(callerAppPane.getFont().deriveFont(Font.BOLD, 12));
      messagePane.setFont(messagePane.getFont().deriveFont(Font.PLAIN, 15));
      subjectPane.setFont(subjectPane.getFont().deriveFont(Font.BOLD, 13));
    }

    setForegroundColors();
    subjectPane.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel contentsPanel = new JPanel(new BorderLayout(5, 5));
    JPanel buttonsPanel = new JPanel();

    contentsPanel.add(subjectPane, BorderLayout.NORTH);
    contentsPanel.add(messagePane, BorderLayout.CENTER);

    bottomPanel.add(contentsPanel, BorderLayout.CENTER);
    bottomPanel.add(buttonsPanel, BorderLayout.EAST);
    bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    backgroundPanel.add(bottomPanel);
    bottomPanel.setOpaque(false);
    buttonsPanel.setOpaque(false);
    contentsPanel.setOpaque(false);
    backgroundPanel.setOpaque(false);

    panel.setOpaque(false);
    auxFrame.getContentPane().add(panel);
    auxFrame.setBackground(new Color(0, 0, 0, 0));
    auxFrame.getRootPane().setBackground(new Color(0, 0, 0, 0));
    auxFrame.getRootPane().setOpaque(false);

    glass = (JPanel) auxFrame.getGlassPane();
    glass.setVisible(true);

    expandLabel = new JLabel("\u25BC");
    expandLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
    expandLabel.setBackground(new Color(0, 0, 0, 0));
    expandLabel.setOpaque(true);
    // expandLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, false));
    buttonsPanel.add(expandLabel);

    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        boolean isInsideExpandButton = isPointInsideComponent(e, expandLabel);
        boolean isInsideAppTitle = isPointInsideComponent(e, callerAppPane);
        boolean isInsideCaption = isPointInsideComponent(e, subjectPane);
        if (isInsideExpandButton && expandLabel.getBackground().getAlpha() == 0) {
          Color firstColor = panel.getFirstColor();
          expandLabel.setBackground(
              new Color(firstColor.getRed(), firstColor.getGreen(), firstColor.getBlue(), 100));
        } else if (!isInsideExpandButton && expandLabel.getBackground().getAlpha() != 0) {
          expandLabel.setBackground(new Color(0, 0, 0, 0));
        }

        if (isInsideExpandButton || isInsideCaption) {
          glass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          if (isInsideCaption) {
            glass.setToolTipText("Open notifications log");
          }
        } else {
          glass.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          glass.setToolTipText("");
        }
      }

    };

    glass.addMouseMotionListener(mouseAdapter);

    ActionListener listener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        auxFrame.setVisible(false);
      }
    };

    timer = new Timer(SECONDS * 1000, listener);
    timer.setRepeats(false);

  }

  public void showNotification(String text, String caption, String callerApp, MessageType type) {
    launchNotification(text, caption, callerApp, type, 80, false);
  }

  private void setForegroundColors() {
    subjectPane.setForeground(useDarkModeFont ? Color.WHITE : Color.BLACK);
    callerAppPane.setForeground(useDarkModeFont ? Color.WHITE : Color.BLACK);
    messagePane.setForeground(useDarkModeFont ? LIGHT_FONT_COLOR : DARK_FONT_COLOR);
  }

  private void expandNotification(String text, String caption, String callerApp, MessageType type) {
    launchNotification(text, caption, callerApp, type, 0, true);
  }

  private void contractNotification(String text, String caption, String callerApp,
      MessageType type) {
    launchNotification(text, caption, callerApp, type, 80, true);
  }

  private void launchNotification(String text, String caption, String callerApp, MessageType type,
      int truncateSize, boolean isReshowOfNotification) {
    timer.stop();
    if (truncateSize > 0 || !isReshowOfNotification) {
      auxFrame.setVisible(false);
      auxFrame.dispose();
    }
    if (!isReshowOfNotification) {
      isMaximizedState = false;
      NotificationData.add(text, caption, callerApp, new Date(), type);
    }

    String displayedText = text;
    if (truncateSize > 0) {
      if (text.length() > truncateSize) {
        displayedText = text.substring(0, 80) + "...";
      }
    }
    messagePane.setText(displayedText);
    callerAppPane.setText(callerApp);

    subjectPane.setText(caption);
    if (caption.equals("")) {
      subjectPane.setVisible(false);
    } else {
      subjectPane.setVisible(true);
    }

    if (calculatedMinHeight == 0) {
      auxFrame.pack();
      calculatedMinHeight = (int) panel.getMinimumSize().getHeight();
    }
    if (truncateSize == 0) {
      auxFrame.pack();
      auxFrame.setPreferredSize(new Dimension(WIDTH, (int) panel.getMinimumSize().getHeight()));
      auxFrame.setSize(new Dimension(WIDTH, (int) panel.getMinimumSize().getHeight()));
    } else {
      auxFrame.setMinimumSize(new Dimension(WIDTH, calculatedMinHeight));
      auxFrame.setPreferredSize(new Dimension(WIDTH, calculatedMinHeight));
      auxFrame.setSize(new Dimension(WIDTH, calculatedMinHeight));
    }

    NotificationPosition pos = new NotificationPosition(location,
        new Dimension(WIDTH, auxFrame.getHeight()), auxFrame.getGraphicsConfiguration());
    auxFrame.setLocation(pos.effectiveCoordinates.x, pos.effectiveCoordinates.y);
    auxFrame.setVisible(true);

    MyMouseAdapter mouseAdapter = new MyMouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (autoClose) {
          timer.stop();
        }

        boolean isInsideExpandButton = isPointInsideComponent(e, expandLabel);
        boolean isInsideAppTitle = isPointInsideComponent(e, callerAppPane);
        boolean isInsideCaption = isPointInsideComponent(e, subjectPane);
        if (isInsideExpandButton) {
          if (!isMaximizedState) {
            isMaximizedState = true;
            expandNotification(text, caption, callerApp, type);
          } else {
            isMaximizedState = false;
            contractNotification(text, caption, callerApp, type);
          }
        } else if (isInsideCaption) {
          displayLog();
        } else {
          auxFrame.setVisible(false);
        }
      }

    };

    for (MouseListener l : glass.getMouseListeners()) {
      if (l instanceof MyMouseAdapter) {
        glass.removeMouseListener(l);
      }
    }
    glass.addMouseListener(mouseAdapter);

    if (autoClose) {
      timer.start();
    }
  }

  public static boolean isPointInsideComponent(MouseEvent e, Component component) {
    boolean isPointInsideExpandButton = component.bounds()
        .contains(SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), component));
    return isPointInsideExpandButton;
  }

  static abstract class MyMouseAdapter extends MouseAdapter {
  }

  public void displayLog() {
    displayLog("", "");
  }

  public void displayLog(String header, String title) {
    String prefix = header == null || header.equals("") ? ""
        : header + "\n\n" + "------------------------------";
    String log = prefix + NotificationData.getSessionLog().stream().map(it -> it.toString())
        .collect(Collectors.joining("\n\n" + "------------------------------"));
    JTextArea textArea = new JTextArea(log);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane() {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(300, 500);
      }

    };
    scrollPane.getViewport().add(textArea);
    JOptionPane.showMessageDialog(auxFrame, scrollPane, "Notifications Log - " + title,
        JOptionPane.INFORMATION_MESSAGE);
  }


  static class NotificationData {
    private String text;
    private String caption;
    private String callerApp;
    private Date date;
    private MessageType type;

    private NotificationData(String text, String caption, String callerApp, Date date,
        MessageType type) {
      super();
      this.text = text;
      this.caption = caption;
      this.callerApp = callerApp;
      this.date = date;
      this.type = type;
    }

    public static void add(String text, String caption, String callerApp, Date date,
        MessageType type) {
      NOTIFICATIONS_LOG.add(0, new NotificationData(text, caption, callerApp, date, type));
    }

    @Override
    public String toString() {
      return "\n" + "DATE: " + date.toString() + "\n" + "CALLER: " + callerApp + "\n" +
          "CAPTION: " + caption + "\n" + "TEXT: " + "\n" + text;
    }

    public static List<NotificationData> getSessionLog() {
      return new ArrayList<>(NOTIFICATIONS_LOG);
    }

    public String getText() {
      return text;
    }

    public String getCaption() {
      return caption;
    }

    public String getCallerApp() {
      return callerApp;
    }


    public static Optional<NotificationData> getLatestNotification() {
      return NOTIFICATIONS_LOG.size() > 0 ? Optional.of(NOTIFICATIONS_LOG.get(0))
          : Optional.empty();
    }

    public MessageType getType() {
      return type;
    }

  }

  static class NotificationPosition {
    Point effectiveCoordinates;

    NotificationPosition(Location location, Dimension preferredComponentSize,
        GraphicsConfiguration gc) {

      Rectangle bounds = gc.getBounds();
      Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

      Rectangle effectiveScreenArea = new Rectangle();

      effectiveScreenArea.x = bounds.x + screenInsets.left;
      effectiveScreenArea.y = bounds.y + screenInsets.top;
      effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
      effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;

      int x = 0;
      int y = 0;
      int offset = 5;

      double prefWidth = preferredComponentSize.getWidth();
      double prefHeight = preferredComponentSize.getHeight();

      if (location == Location.TOP_LEFT) {
        x = effectiveScreenArea.x + offset;
        y = effectiveScreenArea.y + offset;
      } else if (location == Location.TOP_RIGHT) {
        x = (int) (effectiveScreenArea.x + effectiveScreenArea.width - prefWidth - offset);
        y = effectiveScreenArea.y + offset;
      } else if (location == Location.TOP_CENTER) {

      } else if (location == Location.BOTTOM_LEFT) {
        x = effectiveScreenArea.x + offset;
        y = (int) (effectiveScreenArea.y + effectiveScreenArea.height - prefHeight - offset);
      } else if (location == Location.BOTTOM_RIGHT) {
        x = (int) (effectiveScreenArea.x + effectiveScreenArea.width - prefWidth - offset);
        y = (int) (effectiveScreenArea.y + effectiveScreenArea.height - prefHeight - offset);
      } else if (location == Location.BOTTOM_CENTER) {

      }
      int effectiveX = x;
      int effectiveY = y;
      effectiveCoordinates = new Point(effectiveX, effectiveY);
    }
  }

  public boolean isAutoClose() {
    return autoClose;
  }

  public void setAutoClose(boolean shouldAutoClose) {
    autoClose = shouldAutoClose;
  }


  public void showLatestNotification() {
    Optional<NotificationData> latest = NotificationData.getLatestNotification();
    if (latest.isPresent()) {
      NotificationData data = latest.get();
      launchNotification(data.getText(), data.getCaption(), data.getCallerApp(), data.getType(), 80,
          true);
    } else {
      JOptionPane.showMessageDialog(null,
          "No notifications have been sent in this session so far.");
    }
  }

  public void setGradientColors(Color firstColor, Color secondColor, MessageType infoMessageType) {
    panel.setFirstColor(firstColor);
    panel.setSecondColor(secondColor);
  }

  public void useDarkModeFont(boolean useDark) {
    useDarkModeFont = useDark;
    setForegroundColors();
  }
}
