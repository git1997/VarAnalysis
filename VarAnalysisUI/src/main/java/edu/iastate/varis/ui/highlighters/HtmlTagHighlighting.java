package edu.iastate.varis.ui.highlighters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

import edu.iastate.varis.ui.core.Varis;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTagHighlighting extends AbstractSemanticHighlighting {

	@Override
	public IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "PreferenceStore");
		store.setDefault("varis_enabled", true);
		store.setDefault("varis_italic", false);
		store.setDefault("varis_color","#358287");
		return store;
	}
	
	@Override
	public Position[] consumes(IStructuredDocumentRegion region) {
		if (Varis.varisEnabled() && region.getStart() == 0) {
			List<Position> list = new ArrayList<Position>();
			list.add(new Position(36, 5));
			list.add(new Position(278, 7));
			
			list.add(new Position(119, 6));
			list.add(new Position(180, 6));
			list.add(new Position(231, 6));
			
			return list.toArray(new Position[list.size()]);
		}
		return new Position[0];
	}

}