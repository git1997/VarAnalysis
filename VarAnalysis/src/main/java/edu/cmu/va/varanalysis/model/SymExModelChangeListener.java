package edu.cmu.va.varanalysis.model;

import org.eclipse.core.resources.IFile;

public interface SymExModelChangeListener {
	void modelUpdated(IFile file);
}
