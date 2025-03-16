package io.github.iplasm.library.jhelperutils.swing.simplebreadcrumb;

/**
 * 
 * Modified by: bbarbosa
 * 
 * ----
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 TERAI Atsuhiro
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;

public class ArrowToggleButtonBarCellIcon implements Icon {
  public static final int TH = 10; // The height of a triangle
  private static final int HEIGHT = TH * 2 + 1;
  private static final int WIDTH = 100;
  private Shape shape;

  public Shape getShape() {
    return shape;
  }

  protected Shape makeShape(Container parent, Component c, int x, int y) {
    int w = c.getWidth() - 1;
    int h = c.getHeight() - 1;
    double h2 = Math.round(h * .5);
    double w2 = TH;
    Path2D p = new Path2D.Double();
    p.moveTo(0d, 0d);
    p.lineTo(w - w2, 0d);
    p.lineTo(w, h2);
    p.lineTo(w - w2, h);
    p.lineTo(0d, h);
    if (!Objects.equals(c, parent.getComponent(0))) {
      p.lineTo(w2, h2);
    }
    p.closePath();
    return AffineTransform.getTranslateInstance(x, y).createTransformedShape(p);
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    Container parent = c.getParent();
    if (Objects.isNull(parent)) {
      return;
    }
    shape = makeShape(parent, c, x, y);

    Color bgc = parent.getBackground();
    Color borderColor = Color.GRAY.brighter();
    if (c instanceof AbstractButton) {
      ButtonModel m = ((AbstractButton) c).getModel();
      if (m.isSelected() || m.isRollover()) {
        bgc = c.getBackground();
        borderColor = Color.GRAY;
      }
    }
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g2.setPaint(bgc);
    g2.fill(shape);
    g2.setPaint(borderColor);
    g2.draw(shape);
    g2.dispose();
  }

  @Override
  public int getIconWidth() {
    return WIDTH;
  }

  @Override
  public int getIconHeight() {
    return HEIGHT;
  }
}
