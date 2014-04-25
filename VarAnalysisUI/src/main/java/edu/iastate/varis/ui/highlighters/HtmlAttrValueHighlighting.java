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
public class HtmlAttrValueHighlighting extends AbstractSemanticHighlighting {

	@Override
	public IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "PreferenceStore");
		store.setDefault("varis_enabled", true);
		store.setDefault("varis_italic", true);
		store.setDefault("varis_color","#4F00FF");
		return store;
	}
	
	@Override
	public Position[] consumes(IStructuredDocumentRegion region) {
		if (Varis.varisEnabled() && region.getStart() == 0) {
			List<Position> list = new ArrayList<Position>();
			list.add(new Position(80, 12));
			
			list.add(new Position(131, 8));
			list.add(new Position(148, 10));
			
			list.add(new Position(192, 8));
			list.add(new Position(209, 10));
			
			list.add(new Position(243, 9));
			
			return list.toArray(new Position[list.size()]);
		}
		return new Position[0];
	}

}
