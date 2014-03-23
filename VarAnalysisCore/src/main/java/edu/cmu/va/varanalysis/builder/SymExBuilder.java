package edu.cmu.va.varanalysis.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import edu.cmu.va.varanalysis.model.SymExModel;
import edu.cmu.va.varanalysis.processing.FileProcessor;
import errormodel.SymExErrorHandler;
import errormodel.SymExException;

public class SymExBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				symexPHP(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				if (resource instanceof IFile) {
					deleteMarkers((IFile) resource);
					SymExModel.getInstance().updateDModel((IFile) resource,
							null);
				}
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				symexPHP(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			symexPHP(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	class ErrorHandler implements SymExErrorHandler {

		private IFile file;

		public ErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SymExException e, int severity) {
			SymExBuilder.this.addMarker(file, e.getMessage(), e.getOffset(),
					severity);
		}

		@Override
		public void error(SymExException exception) {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void fatalError(SymExException exception) {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void warning(SymExException exception) {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "edu.cmu.va.varanalysis.core.symexbuilder";

	private static final String MARKER_TYPE = "edu.cmu.va.varanalysis.core.symexproblem";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void symexPHP(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".php")) {
			IFile ifile = (IFile) resource;
			deleteMarkers(ifile);

			SymExErrorHandler reporter = new ErrorHandler(ifile);
			try {
				new FileProcessor().process(ifile, reporter);
			} catch (Exception e1) {
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}

}
