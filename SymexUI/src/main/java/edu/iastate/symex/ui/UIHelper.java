package edu.iastate.symex.ui;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author HUNG
 *
 */
public class UIHelper {
	
	/*
	 * Get the active editor and related properties
	 */
	
	public static ITextEditor getActiveEditor() {
		return (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}
	
	public static StyledText getActiveEditorStyledText() {
		return (StyledText) getActiveEditor().getAdapter(Control.class);
	}
	
	public static IFile getActiveEditorFile() {
		return ((IFileEditorInput) getActiveEditor().getEditorInput()).getFile();
	}
	
	public static String getProjectName() {
		return getActiveEditorFile().getProject().getName();
	}
	
	public static String getProjectPath() {
		return getActiveEditorFile().getProject().getLocation().toOSString().replace("/", "\\");
	}
	
	public static String getRelativeFilePath() {
		return getActiveEditorFile().getProjectRelativePath().toOSString().replace("/", "\\");
	}
	
	/**
	 * Opens an editor for a file.
	 */
	public static ITextEditor openEditor(IFile file) {
		ITextEditor editor = null;
		try {
			IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorInput editorInput = new FileEditorInput(file);
			String editorId = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName()).getId();	
			
			editor = (ITextEditor) workbenchPage.openEditor(editorInput, editorId);
		} catch (Exception e) {
		}
		return editor;
	}
	
	/**
	 * Opens an editor and selects a string fragment in the file.
	 */
	public static void selectAndReveal(File file, int offset, int length ) {
		if (file != null) {
			ITextEditor editor = UIHelper.openEditor(UIHelper.fileToIFile(file));
			if (editor != null)
				editor.selectAndReveal(offset, length);
		}
	}
	
	/**
	 * Saves all editors.
	 */
	public static boolean saveAllEditors() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return IDE.saveAllEditors(new IResource[] {workspaceRoot}, true);
	}
	
	/**
	 * Converts IFile to File
	 */
	public static File iFileToFile(IFile iFile) {
		return iFile.getRawLocation().makeAbsolute().toFile();
	}
	
	/**
	 * Converts IProject to File
	 */
	public static File iProjectToFile(IProject iProject) {
		return iProject.getFullPath().toFile();
	}
	
	/**
	 * Converts File to IFile
	 */
	public static IFile fileToIFile(File file) {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
		IPath location= Path.fromOSString(file.getAbsolutePath()); 
		IFile iFile= workspace.getRoot().getFileForLocation(location);
		
		return iFile;
	}	
	
	/*
	 * String manipulations
	 */
	
	public static String standardizeText(String string) {
		if (string.isEmpty())
			return "(empty)";
		else
			return string.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
	}
	
	public static String standardizeFilePath(String filePath) {
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString();
		if (filePath.startsWith(workspacePath))
			return filePath.substring(workspacePath.length());
		else
			return filePath;
	}
	
}
