package edu.iastate.varis.ui.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import edu.iastate.varis.ui.core.VarisManager;

/**
 * 
 * @author HUNG
 *
 */
public class VarisCompletionComputer implements IScriptCompletionProposalComputer {

	private final static String[] proposals =
		{ "accesskey", "class", "contenteditable", "contextmenu", "dir", "draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate" }; 

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		if (!VarisManager.getInstance().isEnabled())
			return new ArrayList<ICompletionProposal>(0);
		
		List<ICompletionProposal> result= new ArrayList<ICompletionProposal>(proposals.length);
		for (int i = 0; i < proposals.length; i++) {
			IContextInformation info = new ContextInformation(proposals[i], "Varis Recommendation");
			result.add(new CompletionProposal(proposals[i], arg0.getInvocationOffset(), 0, proposals[i].length(), null, proposals[i], info, "<b>Varis Recommendation:</b><br />Attribute: " + proposals[i]));
		}
		return result;
	}
	
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sessionEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
