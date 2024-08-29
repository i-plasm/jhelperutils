package io.github.iplasm.library.jhelperutils.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class GradientPanel extends JPanel {

  private Color firstColor = Color.MAGENTA;
  private Color secondColor = Color.BLUE;
  private int borderCurveRadius = 20;
  private int secondPointX = 400;
  private int shadowThickness = 0;
  private int shadowOffset = 0;

  public Color getFirstColor() {
    return firstColor;
  }

  public void setUniColor(Color color) {
    this.firstColor = color;
    this.secondColor = color;
  }

  public void setFirstColor(Color firstColor) {
    this.firstColor = firstColor;
  }

  public Color getLastColor() {
    return secondColor;
  }


  public void setSecondColor(Color secondColor) {
    this.secondColor = secondColor;
  }

  public void setSecondPointX(int secondPointX) {
    this.secondPointX = secondPointX;
  }

  public void setBorderCurveRadius(int borderCurveRadius) {
    this.borderCurveRadius = borderCurveRadius;
  }

  public void setShadowThickness(int shadowThickness) {
    this.shadowThickness = shadowThickness;
  }

  public GradientPanel() {}

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    applyQualityProperties(g2d);

    int totalWidth = getWidth();
    int totalHeight = getHeight();
    // Dimension arcs = new Dimension(borderCurveRadius, borderCurveRadius);

    // Drawing shadow
    g2d.setColor(new Color(0, 0, 0, 0.05f)); //
    g2d.fillRoundRect(0 + shadowOffset + shadowThickness, 0 + shadowOffset + shadowThickness,
        totalWidth - 1 - shadowOffset - shadowThickness,
        totalHeight - 1 - shadowOffset - shadowThickness, borderCurveRadius, borderCurveRadius);
    g2d.drawRoundRect(0 + shadowOffset + shadowThickness, 0 + shadowOffset + shadowThickness,
        totalWidth - 1 - shadowOffset - shadowThickness,
        totalHeight - 1 - shadowOffset - shadowThickness, borderCurveRadius, borderCurveRadius);

    // Drawing panel gradient
    GradientPaint gp = new GradientPaint(0, 0, firstColor, secondPointX, totalHeight, secondColor);
    g2d.setPaint(gp);
    g2d.fillRoundRect(0, 0, totalWidth - 1 - shadowThickness, totalHeight - 1 - shadowThickness,
        borderCurveRadius, borderCurveRadius);
    g2d.drawRoundRect(0, 0, totalWidth - 1 - shadowThickness, totalHeight - 1 - shadowThickness,
        borderCurveRadius, borderCurveRadius);
  }

  private static void applyQualityProperties(Graphics2D g2) {
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Secondary properties
    g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
  }

}
