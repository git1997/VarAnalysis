package edu.iastate.varis.ui.contentassist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;

import edu.iastate.varis.ui.core.Varis;

/**
 * 
 * @author HUNG
 *
 */
public class VarisCompletionComputer implements org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer {

	protected final static String[] fgProposals=
		{ "accesskey", "class", "contenteditable", "contextmenu", "dir", "draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate" }; 

	
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
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		if (!Varis.varisEnabled())
			return new ArrayList<ICompletionProposal>(0);
		
		List<ICompletionProposal> result= new ArrayList<ICompletionProposal>(fgProposals.length);
		for (int i= 0; i < fgProposals.length; i++) {
			IContextInformation info= new ContextInformation(fgProposals[i], fgProposals[i] + " HTML attribute (from Varis)");
			result.add(new CompletionProposal(fgProposals[i], arg0.getInvocationOffset(), 0, fgProposals[i].length(), null, fgProposals[i], info, "HTML attributes (from Varis)"));
		}
		return result;
	}

	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
