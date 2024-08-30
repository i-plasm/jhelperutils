package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.util.TimerTask;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ZoomCoalescenceManager {
  private Timer timer;
  private String name = "";
  private int delay;
  private volatile int zoom;
  private Runnable runnable;

  public ZoomCoalescenceManager(int delay, String name) {
    this.name = name;
    this.delay = delay;
    timer = new Timer(delay, e -> {
      timer.stop();
      runnable.run();
      // Setting the "normal" delay at the end; needed in case the delay was temporarily altered
      // by `requestImmediately()`
      timer.setDelay(ZoomCoalescenceManager.this.delay);
    });
  }


  public void requestImmediately(int zoom) {
    this.zoom = zoom;
    timer.setDelay(0);
    request(zoom);
  }

  public void request(int zoom) {
    this.zoom = zoom;
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> {
        timer.restart();
      });
    } else {
      timer.restart();
    }
  }

  public void restart(int zoom) {
    this.zoom = zoom;
    timer.restart();
  }

  public void restartDelayed(int delayBeforeRestart, int zoom) {
    this.zoom = zoom;
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        SwingUtilities.invokeLater(() -> timer.restart());
      }
    };
    java.util.Timer timer = new java.util.Timer("restart_delayed");
    timer.schedule(task, delayBeforeRestart);
  }

  public String getName() {
    return name;
  }

  public int getZoom() {
    return zoom;
  }


  public void setRunnable(Runnable runnable) {
    this.runnable = runnable;
  }
}
