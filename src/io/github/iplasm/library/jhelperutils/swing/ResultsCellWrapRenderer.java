package io.github.iplasm.library.jhelperutils.swing;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Note: ResultsCellWrapRenderer can only be used for a single table column
 */
public class ResultsCellWrapRenderer extends JTextArea implements TableCellRenderer {
  private int margin;
  private Color bgColor;
  private Color fgColor;
  private Color selectedBgColor;
  private Color selectedFgColor;
  private Map<String, Color> colorsMap;

  private static final Map<String, Border> BORDER_CACHE = new HashMap();
  private final Function<String, Border> borderFunction =
      key -> getBorderForColor(colorsMap.get(key));

  public ResultsCellWrapRenderer(int margin, Color bgColor, Color selectedBgColor, Color fgColor,
      Color selectedFgColor, Map<String, Color> colorsMap) {
    setLineWrap(true);
    setWrapStyleWord(true);
    this.margin = margin;
    this.bgColor = bgColor;
    this.selectedBgColor = selectedBgColor;
    this.selectedFgColor = selectedFgColor;
    this.fgColor = fgColor;
    this.colorsMap = colorsMap;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    setText((row + 1) + (row >= 9 ? "  " : "   ") + value.toString());
    setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));

    int preferredHeight = getPreferredSize().height + margin;
    if (table.getRowHeight(row) != preferredHeight) {
      table.setRowHeight(row, preferredHeight);
    }

    Border border = BORDER_CACHE.computeIfAbsent(value.toString(), borderFunction);
    setBorder(border);
    if (isSelected) {
      setBackground(selectedBgColor);
      setForeground(selectedFgColor);
    } else {
      setBackground(bgColor);
      setForeground(fgColor);
    }

    return this;
  }

  private static Border getBorderForColor(Color color) {
    Border border = BorderFactory.createMatteBorder(0, 5, 0, 0, color);
    return BorderFactory.createCompoundBorder(border,
        BorderFactory.createEmptyBorder(0, 10, 0, 10));
  }
}
