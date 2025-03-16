package io.github.iplasm.library.jhelperutils.swing.simplebreadcrumb;

public class BreadcrumbDetails {
  String text;
  String tooltip;
  Runnable runnable;

  BreadcrumbDetails(String text, String tooltip, Runnable runnable) {
    this.text = text;
    this.tooltip = tooltip;
    this.runnable = runnable;
  }

  public String getText() {
    return text;
  }

  public String getTooltip() {
    return tooltip;
  }

  public Runnable getRunnable() {
    return runnable;
  }
}
