package com.codeaffine.extras.archive.internal.extract;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

class ArchiveExtractorJob extends Job {
  private final ArchiveEntry[] archiveEntries;
  private final ExtractLocation extractLocation;

  ArchiveExtractorJob(ArchiveEntry[] archiveEntries, ExtractLocation extractLocation) {
    super(getJobName(extractLocation));
    this.archiveEntries = archiveEntries;
    this.extractLocation = extractLocation;
  }

  @Override
  protected IStatus run(IProgressMonitor progressMonitor) {
    IStatus result = Status.OK_STATUS;
    try {
      createExtractOperation().run(progressMonitor);
    } catch (InterruptedException ie) {
      // ignore, this exception is thrown when an OperationCanceledException was
      // raised
    } catch (InvocationTargetException exception) {
      result = toStatus(exception.getTargetException());
    }
    return result;
  }

  private IRunnableWithProgress createExtractOperation() {
    IRunnableWithProgress operation;
    if (extractLocation.isWorkspaceRelative()) {
      operation = createWorkspaceExtractOperation();
    } else {
      operation = createFilesystemExtractOperation();
    }
    return operation;
  }

  private IRunnableWithProgress createFilesystemExtractOperation() {
    return new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
        try {
          extract(progressMonitor);
        } catch (OperationCanceledException oce) {
          throw new InterruptedException(oce.getMessage());
        } catch (CoreException ce) {
          throw new InvocationTargetException(ce);
        }
      }
    };
  }

  private IRunnableWithProgress createWorkspaceExtractOperation() {
    return new WorkspaceModifyOperation(getWorkspaceSchedulingRule()) {
      @Override
      protected void execute(IProgressMonitor monitor) throws CoreException {
        extract(monitor);
      }
    };
  }

  private IResource getWorkspaceSchedulingRule() {
    return ResourcesPlugin.getWorkspace().getRoot().findMember(extractLocation.getPath());
  }

  private void extract(IProgressMonitor monitor) throws CoreException {
    beginProgressMonitor(monitor);
    try {
      for (ArchiveEntry archiveEntry : archiveEntries) {
        ArchiveExtractor archiveExtractor = new ArchiveExtractor(archiveEntry, monitor);
        archiveExtractor.extract(new DelegatingExtractWriter(extractLocation));
      }
    } finally {
      monitor.done();
    }
  }

  private void beginProgressMonitor(IProgressMonitor progressMonitor) {
    int ticks = new FileEntryCounter(archiveEntries).count();
    progressMonitor.beginTask("Extracting files", ticks);
  }

  private IStatus toStatus(Throwable exception) {
    String path = extractLocation.getPath().toPortableString();
    String msg = MessageFormat.format("Failed to extract files to {0}.", path);
    return StatusUtil.createStatus(IStatus.ERROR, msg, exception);
  }

  private static String getJobName(ExtractLocation extractLocation) {
    String path = extractLocation.getPath().toPortableString();
    return MessageFormat.format("Extracting files to {0}...", path);
  }
}
