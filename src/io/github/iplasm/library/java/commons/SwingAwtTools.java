package io.github.iplasm.library.java.commons;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class SwingAwtTools {

  public static String detectURLOnHTMLDocument(JEditorPane componentToEvaluate) {
    JEditorPane htmlEditor = componentToEvaluate;
    HTMLDocument doc = (HTMLDocument) htmlEditor.getDocument();
    int textStart = htmlEditor.getSelectionStart();
    int textEnd = htmlEditor.getSelectionEnd();

    if (htmlEditor.getSelectedText() == null) { // In case there's no selection, we detect
                                                // consider
                                                // the character at the caret position
      textEnd = componentToEvaluate.getCaretPosition() + 1;
      textStart = componentToEvaluate.getCaretPosition();
    }

    for (int i = textStart; i < textEnd; i++) {
      Element characterElement = doc.getCharacterElement(i);
      SimpleAttributeSet charElemSimpleAttributeSet =
          new SimpleAttributeSet(characterElement.getAttributes().copyAttributes());

      MutableAttributeSet aTagAttributes =
          (MutableAttributeSet) charElemSimpleAttributeSet.getAttribute(HTML.Tag.A);

      if (aTagAttributes != null) {
        ArrayList<?> aTagAttributeKeys = Collections.list(aTagAttributes.getAttributeNames());
        for (Object aTagAttrKey : aTagAttributeKeys) {
          if (aTagAttrKey.toString().equalsIgnoreCase("href")) {
            // System.out.println(aTagAttributes.getAttribute(aTagAttrKey));
            return aTagAttributes.getAttribute(aTagAttrKey).toString();
          }
        }
      }
    }
    return "";
  }

  public static String autodetectLinkFromNeighbor(int caretPosition, String docText) {
    int surroundingNeighborhoodLeftIndex = caretPosition;
    int surroundingNeighborhoodRightIndex = caretPosition;
    if (caretPosition != 0) {
      while (surroundingNeighborhoodLeftIndex > 0) {
        String previousChar = docText.substring(surroundingNeighborhoodLeftIndex - 1,
            surroundingNeighborhoodLeftIndex);
        if (previousChar.trim().equals("")) {
          break;
        }
        surroundingNeighborhoodLeftIndex--;
      }
    }

    if (caretPosition != docText.length() - 1) {
      while (surroundingNeighborhoodRightIndex < docText.length()) {
        String nextChar = docText.substring(surroundingNeighborhoodRightIndex,
            surroundingNeighborhoodRightIndex + 1);
        if (nextChar.trim().equals("")) {
          break;
        }
        surroundingNeighborhoodRightIndex++;
      }
    }
    return docText.substring(surroundingNeighborhoodLeftIndex, surroundingNeighborhoodRightIndex);
  }

  /**
   * Attempts to detect a string that could serve as url or path on JEditorPane. It does not matter
   * if the user has made a text selection or not. The type of editor kit is irrelevant.
   * 
   * Detections are first considered on selected text basis, and if no selection exist, then caret
   * position is used as basis to inquire on its neighboring characters.
   * 
   * If the editor kit is of HTMLEditorKit type, its underlying html will be examined for the 'a'
   * tag and the 'href' attribute. If none is detected, then the detection will proceed as if it was
   * plain text.
   * 
   * @throws IllegalArgumentException if no valid detection whatsoever could be made
   */
  public static String detectPossibleURLorPathOnEditor(JEditorPane editorPane) {
    boolean isTextualLink = false;

    String urlOrPath = "";
    if (editorPane.getEditorKit() instanceof HTMLEditorKit) {
      urlOrPath = detectURLOnHTMLDocument(editorPane);
    }

    if (urlOrPath == "") {
      isTextualLink = true;
      if (editorPane.getSelectedText() != null) {
        urlOrPath = editorPane.getSelectedText().trim();
      }
      // Case: no selection, editable text component
      else {// if (editorPane.isEditable()) {
        int caretPosition = editorPane.getCaretPosition();
        String docText = "";
        try {
          docText = editorPane.getDocument().getText(0, editorPane.getDocument().getLength());
        } catch (BadLocationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        urlOrPath = autodetectLinkFromNeighbor(caretPosition, docText);
      }
      if (TextUtils.containsLineBreaks(urlOrPath)) {
        throw new IllegalArgumentException("Plain text selections can not contain line breaks.");
      }
    }
    return urlOrPath;
  }

  public static Component getFocusedComponent() {

    Window fosusedWindow = null;
    for (Window w : Window.getWindows()) {
      if (w.isFocused()) {
        fosusedWindow = w;
      }
    }
    return fosusedWindow == null ? null : fosusedWindow.getFocusOwner();
  }

  public static void browseURLOrPathViaDesktop(URI uri) throws IOException {
    Desktop desktop = Desktop.getDesktop();
    if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
      desktop.browse(uri);
    }
  }

  public static String getTrimmedSelectedText(JTextComponent editor) {
    String selectedTxt = editor == null ? "" : editor.getSelectedText();
    return selectedTxt == null ? "" : selectedTxt.trim();
  }


}
