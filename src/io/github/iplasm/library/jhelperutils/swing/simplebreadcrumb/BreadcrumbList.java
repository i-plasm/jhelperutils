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
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;
import io.github.iplasm.library.jhelperutils.swing.WrapLayout;

public class BreadcrumbList extends JPanel {

  private static Container makeContainer(int overlap) {
    JPanel p = new JPanel(new WrapLayout(FlowLayout.LEADING, -overlap, 5)) {
      @Override
      public boolean isOptimizedDrawingEnabled() {
        return false;
      }
    };
    p.setBorder(BorderFactory.createEmptyBorder(4, overlap + 4, 4, 4));
    p.setOpaque(false);
    return p;
  }

  private static Component makeBreadcrumbList(List<String> list) {
    Container p = makeContainer(10 + 1);
    ButtonGroup bg = new ButtonGroup();
    list.forEach(title -> {
      AbstractButton b = makeButton(null, new TreePath(title), Color.PINK);
      p.add(b);
      bg.add(b);
    });
    return p;
  }

  private static Component makeBreadcrumbListWithToolTip(List<BreadcrumbDetails> list,
      Color hoverColor) {
    Container p = makeContainer(10 + 1);
    ButtonGroup bg = new ButtonGroup();
    list.forEach(listItem -> {
      AbstractButton b = makeButton(null, new TreePath(listItem.getText()), hoverColor);
      b.setToolTipText(listItem.getTooltip());
      b.addActionListener(l -> listItem.getRunnable().run());
      p.add(b);
      bg.add(b);
    });
    return p;
  }

  private static AbstractButton makeButton(JTree tree, TreePath path, Color color) {
    AbstractButton b = new JRadioButton(path.getLastPathComponent().toString()) {
      @Override
      public boolean contains(int x, int y) {
        return Optional.ofNullable(getIcon())
            .filter(it -> ArrowToggleButtonBarCellIcon.class.isInstance(it))
            .map(i -> ((ArrowToggleButtonBarCellIcon) i).getShape()).map(s -> s.contains(x, y))
            .orElseGet(() -> super.contains(x, y));
      }
    };
    if (Objects.nonNull(tree)) {
      b.addActionListener(e -> {
        JRadioButton r = (JRadioButton) e.getSource();
        tree.setSelectionPath(path);
        r.setSelected(true);
      });
    }
    b.setIcon(new ArrowToggleButtonBarCellIcon());
    b.setContentAreaFilled(false);
    b.setBorder(BorderFactory.createEmptyBorder());
    b.setVerticalAlignment(SwingConstants.CENTER);
    b.setVerticalTextPosition(SwingConstants.CENTER);
    b.setHorizontalAlignment(SwingConstants.CENTER);
    b.setHorizontalTextPosition(SwingConstants.CENTER);
    b.setFocusPainted(false);
    b.setOpaque(false);
    b.setBackground(color);
    return b;
  }
}
