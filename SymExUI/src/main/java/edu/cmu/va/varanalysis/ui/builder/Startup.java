package edu.cmu.va.varanalysis.ui.builder;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		for (IProject p:ResourcesPlugin.getWorkspace().getRoot().getProjects())
			try {
				p.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//		IProject#build(int,String,Map,IProgressMonitor)
	}

}
