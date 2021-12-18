package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.codeaffine.extras.launch.internal.Images;

public class TerminateLaunchesAction extends Action {

  public static final String ID = TerminateLaunchesAction.class.getName();

  private IStructuredSelection selection;

  public TerminateLaunchesAction() {
    super("&Terminate All");
    this.selection = StructuredSelection.EMPTY;
    setId(ID);
    setImageDescriptor(Images.getImageDescriptor(Images.TERMINATE_ALL));
    setEnabled(false);
  }

  public void setSelection(IStructuredSelection selection) {
    this.selection = selection;
    setEnabled(!getTerminatableLaunches().isEmpty());
  }

  public IStructuredSelection getSelection() {
    return selection;
  }

  @Override
  public void run() {
    if (isEnabled()) {
      new TerminateLaunchesJob(getTerminatableLaunches()).schedule();
    }
  }

  private List<ILaunch> getTerminatableLaunches() {
    Collection<ILaunchConfiguration> selectedLaunchConfigs = getSelectedLaunchConfigs();
    return Stream.of(DebugPlugin.getDefault().getLaunchManager().getLaunches())
        .filter(launch -> selectedLaunchConfigs.contains(launch.getLaunchConfiguration())).filter(ILaunch::canTerminate)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Collection<ILaunchConfiguration> getSelectedLaunchConfigs() {
    return (Collection<ILaunchConfiguration>) selection.toList().stream()
        .filter(element -> element instanceof ILaunchConfiguration).collect(Collectors.toList());
  }

  static class TerminateLaunchesJob extends Job {
    static final Class<TerminateLaunchesJob> FAMILY = TerminateLaunchesJob.class;

    private final MultiStatus status;
    private final Collection<ILaunch> launches;

    public TerminateLaunchesJob(Collection<ILaunch> launches) {
      super("Terminate Launches");
      this.status = new MultiStatus(PLUGIN_ID, 0, "Failed to terminate launch", null);
      this.launches = launches;
      setSystem(true);
    }

    @Override
    public boolean belongsTo(Object family) {
      return family == FAMILY;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      launches.forEach(this::terminateLaunch);
      return status;
    }

    private void terminateLaunch(ILaunch launch) {
      try {
        if (launch.canTerminate()) {
          launch.terminate();
        }
      } catch (DebugException e) {
        status.merge(e.getStatus());
      }
    }
  }

}
