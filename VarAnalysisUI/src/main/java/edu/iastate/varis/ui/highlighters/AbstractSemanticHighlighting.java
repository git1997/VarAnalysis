package edu.iastate.varis.ui.highlighters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;

import edu.cmu.va.varanalysis.ui.highlighting.IColorConstants;
import edu.cmu.va.varanalysis.ui.highlighting.StringLitHighlighting;
import edu.iastate.varis.ui.core.Varis;

public abstract class AbstractSemanticHighlighting implements ISemanticHighlighting, ISemanticHighlightingExtension2 {
	
	private final String preferenceKey = this.getClass().getName();
	
	//http://www.yellowpipe.com/yis/tools/hex-to-rgb/color-converter.php

	@Override
	public String getBackgroundColorPreferenceKey() {
		return "bgcolor";
	}



	@Override
	public String getBoldPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
	}

	@Override
	public String getColorPreferenceKey() {
		return "varis_color";
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnabledPreferenceKey() {
		return "varis_enabled";
	}

	@Override
	public String getItalicPreferenceKey() {
		return "varis_italic";
	}

	@Override
	public String getStrikethroughPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
	}

	@Override
	public String getUnderlinePreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
	}

	/*
	private ISourceModule sourceModule = null;

	private SemanticHighlightingStyle style = new SemanticHighlightingStyle(
			getPreferenceKey());

	private List<Position> list;

	private final String preferenceKey = this.getClass().getName();

	public String getPreferenceKey() {
		return preferenceKey;
	}

	public SemanticHighlightingStyle getStyle() {
		return style;
	}

	public ISourceModule getSourceModule() {
		if (sourceModule == null) {
			throw new IllegalStateException("Source module cannot be null");
		}
		return sourceModule;
	}

	protected AbstractSemanticHighlighting highlight(ISourceRange range) {
		if (range == null) {
			throw new IllegalArgumentException("Range cannot be null");
		}
		return highlight(range.getOffset(), range.getLength());
	}

	protected AbstractSemanticHighlighting highlight(ASTNode node) {
		if (node == null) {
			throw new IllegalArgumentException("Node cannot be null");
		}
		return highlight(node.getStart(), node.getLength());
	}

	protected AbstractSemanticHighlighting highlight(int start, int length) {
		if (list == null) {
			throw new IllegalStateException();
		}
		list.add(new Position(start, length));
		return this;
	}

	public Position[] consumes(Program program) {
		if (program != null) {
			list = new ArrayList<Position>();
			sourceModule = program.getSourceModule();
			AbstractSemanticApply apply = getSemanticApply();
			program.accept(apply);
			return list.toArray(new Position[list.size()]);
		}
		return new Position[0];
	}

	public Position[] consumes(IStructuredDocumentRegion region) {
		if (region.getStart() == 0) {
			Program program = getProgram(region);
			return consumes(program);
		}
		return new Position[0];
	}

	protected Program getProgram(final IStructuredDocumentRegion region) {// region.getParentDocument().get()
		sourceModule = null;
		// resolve current sourceModule
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PHPUiPlugin.getActivePage();
				if (page != null) {
					IEditorPart editor = page.getActiveEditor();
					if (editor instanceof PHPStructuredEditor) {
						PHPStructuredEditor phpStructuredEditor = (PHPStructuredEditor) editor;
						if (phpStructuredEditor.getTextViewer() != null
								&& phpStructuredEditor != null
								&& phpStructuredEditor.getDocument() == region
										.getParentDocument()) {
							if (phpStructuredEditor != null
									&& phpStructuredEditor.getTextViewer() != null) {
								sourceModule = (ISourceModule) phpStructuredEditor
										.getModelElement();
							}
						}
					}
				}
			}
		});

		// resolve AST
		Program program = null;
		if (sourceModule != null) {
			try {
				program = SharedASTProvider.getAST(sourceModule,
						SharedASTProvider.WAIT_YES, null);
			} catch (ModelException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return program;
	}

	public abstract AbstractSemanticApply getSemanticApply();

	public abstract void initDefaultPreferences();

	public int compareTo(AbstractSemanticHighlighting highlighter) {
		return getPriority() - highlighter.getPriority();
	}

	public int getPriority() {
		return 100;
	}
	*/
}
