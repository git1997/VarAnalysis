package symexui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import edu.cmu.va.varanalysis.ui.highlighting.CallGraphNodeHighlighting;
import edu.cmu.va.varanalysis.ui.highlighting.IColorConstants;
import edu.cmu.va.varanalysis.ui.highlighting.StringLitHighlighting;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.cmu.va.SymExUI"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private final IPreferenceStore store = new ScopedPreferenceStore(
			new InstanceScope(), getBundle().getSymbolicName());
	{
		store.setDefault(
				StringLitHighlighting.SEMANTICHIGHLIGHTING_SYMEX_ENDABLED, false);
		store.setDefault(
				CallGraphNodeHighlighting.SEMANTICHIGHLIGHTING_CGNODE_ENDABLED, true);
		
		store.setDefault(StringLitHighlighting.SEMANTICHIGHLIGHTING_SYMEX_BOLD,
				true);
		store.setDefault(
				CallGraphNodeHighlighting.SEMANTICHIGHLIGHTING_CGNODE_UNDERLINE,
				true);

		// PreferenceConverter.setDefault(store,
		// StringLitHighlighting.SEMANTICHIGHLIGHTING_SYMEX_BACKGROUND,
		// IColorConstants.MATCHEDLIT);
		store.setDefault(
				StringLitHighlighting.SEMANTICHIGHLIGHTING_SYMEX_BACKGROUND,
				"#ff3311");
		store.setDefault(
				CallGraphNodeHighlighting.SEMANTICHIGHLIGHTING_CGNODE_BACKGROUND,
				"#ff3311");
		PreferenceConverter.setDefault(store,
				StringLitHighlighting.SEMANTICHIGHLIGHTING_SYMEX_COLOR,
				IColorConstants.STRINGLIT);

	}

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public IPreferenceStore getPreferenceStore() {

		return store;
	}

	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchPage activePage = getActivePage();
		if (activePage != null) {
			return activePage.getActiveEditor();
		}
		return null;
	}

	private IWorkbenchPage internalGetActivePage() {
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
}
