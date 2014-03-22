package util;

import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * @author HUNG
 *
 */
public class SwingUtils {

	/**
     * Source: http://stackoverflow.com/questions/102171/method-that-returns-the-line-number-for-a-given-jtextpane-position
     * Returns an int containing the wrapped line index at the given position
     * @param component JTextPane
     * @param int pos
     * @return int
     */
    public static int getLineNumber(JTextArea component, int pos) 
    {
      int posLine;
      int y = 0;

      try
      {
        Rectangle caretCoords = component.modelToView(pos);
        y = (int) caretCoords.getY();
      }
      catch (Exception ex)
      {
      }

      int lineHeight = component.getFontMetrics(component.getFont()).getHeight();
      posLine = (y / lineHeight);
      return posLine;
    }
    
    /**
     * Scrolls a JScrollPane to a position in the JTextArea.
     * @param jScrollPane
     * @param jTextArea
     * @param position
     */
    public static void scrollToPosition(JScrollPane jScrollPane, JTextArea jTextArea, int position) {
    	jScrollPane.getVerticalScrollBar().setValue((getLineNumber(jTextArea, position) - 3) * 18 );
    }
    
}
