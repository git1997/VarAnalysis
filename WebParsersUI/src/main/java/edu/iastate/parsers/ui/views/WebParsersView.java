package edu.iastate.parsers.ui.views;

import java.io.File;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.core.PhpExecuterAndParser;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.DataModelToHtmlTokens;
import edu.iastate.parsers.html.htmlparser.HtmlTokensToSaxNodes;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;

/**
 * 
 * @author HUNG
 *
 */
public class WebParsersView extends GenericView {

	private TreeViewer lexResultTreeViewer, saxResultTreeViewer, domResultTreeViewer;

	private StyledText domStyledText;
	
	/**
	 * Constructor
	 */
	public WebParsersView() {
		super(3, 4);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		if (buttonNumber == 0)
			button.setText("Run Lexer");
		else if (buttonNumber == 1)
			button.setText("Run SaxParser");
		else 
			button.setText("Run DomParser");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("Lex Result");
			lexResultTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new LexResultTreeViewer());
			return lexResultTreeViewer.getControl();
		}
		else if (tabNumber == 1) {
			tabItem.setText("Sax Result");
			saxResultTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new SaxResultTreeViewer());
			return saxResultTreeViewer.getControl();
		}
		else if (tabNumber == 2){
			tabItem.setText("Dom Result");
			domResultTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new DomResultTreeViewer());
			return domResultTreeViewer.getControl();
		}
		else {
			tabItem.setText("Dom (Text)");
			domStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return domStyledText;
		}
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		if (buttonNumber == 0) {
			DataModel dataModel = new PhpExecuter().execute(file);
			CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
		
			lexResultTreeViewer.setInput(new GenericTreeViewer.TreeInput(lexResult));
			lexResultTreeViewer.expandToLevel(2);
			tabFolder.setSelection(0);
		}
		else if (buttonNumber == 1) {
			DataModel dataModel = new PhpExecuter().execute(file);
			CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
			CondList<HtmlSaxNode> saxResult = new HtmlTokensToSaxNodes().parse(lexResult);
		
			saxResultTreeViewer.setInput(new GenericTreeViewer.TreeInput(saxResult));
			saxResultTreeViewer.expandToLevel(2);
			tabFolder.setSelection(1);
		}
		else {
			HtmlDocument domResult = new PhpExecuterAndParser().executeAndParse(file);
		
			domResultTreeViewer.setInput(domResult);
			domResultTreeViewer.expandToLevel(2);
			tabFolder.setSelection(2);
			domStyledText.setText(domResult.toIfdefString());
		}
	}
	
}