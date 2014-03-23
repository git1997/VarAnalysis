package edu.cmu.va.symexui.model;

import org.eclipse.core.resources.IFile;

public interface SymExModelChangeListener {
	void modelUpdated(IFile file);
}
