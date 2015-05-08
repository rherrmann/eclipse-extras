package com.codeaffine.extras.internal.workingset;

import static com.codeaffine.extras.internal.workingset.DynamicWorkingSet.ID;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.synchronizedSet;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceDelta.ADDED;
import static org.eclipse.core.resources.IResourceDelta.CHANGED;
import static org.eclipse.core.resources.IResourceDelta.REMOVED;
import static org.eclipse.ui.IWorkingSetManager.CHANGE_WORKING_SET_ADD;
import static org.eclipse.ui.IWorkingSetManager.CHANGE_WORKING_SET_NAME_CHANGE;
import static org.eclipse.ui.IWorkingSetManager.CHANGE_WORKING_SET_REMOVE;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.PlatformUI;


public class DynamicWorkingSetUpdater
  implements IWorkingSetUpdater, IPropertyChangeListener, IResourceChangeListener
{

  private final IWorkingSetManager workingSetManager;
  private final IWorkspace workspace;
  private final ThreadLocal<Boolean> inPropertyChange;
  private final Set<IWorkingSet> workingSets;

  public DynamicWorkingSetUpdater() {
    workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    workspace = ResourcesPlugin.getWorkspace();
    inPropertyChange = new ThreadLocal<Boolean>();
    workingSets = synchronizedSet( new HashSet<IWorkingSet>() );
    initialize();
  }

  private void initialize() {
    workingSetManager.addPropertyChangeListener( this );
    workspace.addResourceChangeListener( this, POST_CHANGE );
  }

  @Override
  public void add( IWorkingSet workingSet ) {
    update( workingSet );
    workingSets.add( workingSet );
  }

  @Override
  public boolean remove( IWorkingSet workingSet ) {
    return workingSets.remove( workingSet );
  }

  @Override
  public boolean contains( IWorkingSet workingSet ) {
    return workingSets.contains( workingSet );
  }

  @Override
  public void dispose() {
    workspace.removeResourceChangeListener( this );
    workingSetManager.removePropertyChangeListener( this );
    workingSets.clear();
  }

  @Override
  public void propertyChange( PropertyChangeEvent event ) {
    if( inPropertyChange.get() == null ) {
      inPropertyChange.set( TRUE );
      try {
        handlePropertyChange( event );
      } finally {
        inPropertyChange.remove();
      }
    }
  }

  @Override
  public void resourceChanged( IResourceChangeEvent event ) {
    if( event.getDelta() != null ) {
      if( isUpdateNeeded( event ) ) {
        update();
      }
    }
  }

  private void handlePropertyChange( PropertyChangeEvent event ) {
    if( isWorkingSetAddEvent( event ) ) {
      add( getWorkingSet( event ) );
    } else if( isWorkingSetNameChangeEvent( event ) ) {
      update( getWorkingSet( event ) );
    } else if( isWorkingSetRemoveEvent( event ) ) {
      remove( getWorkingSet( event ) );
    }
  }

  private boolean isUpdateNeeded( IResourceChangeEvent event ) {
    ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
    try {
      event.getDelta().accept( visitor );
    } catch( CoreException ce ) {
      throw new RuntimeException( ce );
    }
    return visitor.isUpdateNeeded();
  }

  private void update() {
    for( IWorkingSet workingSet : workingSets.toArray( new IWorkingSet[ 0 ] ) ) {
      update( workingSet );
    }
  }

  private static void update( IWorkingSet workingSet ) {
    new WorkingSetContentUpdater( workingSet ).updateElements();
  }

  private static boolean isWorkingSetAddEvent( PropertyChangeEvent event ) {
    return CHANGE_WORKING_SET_ADD.equals( event.getProperty() ) && isDynamicWorkingSet( event );
  }

  private static boolean isWorkingSetRemoveEvent( PropertyChangeEvent event ) {
    return CHANGE_WORKING_SET_REMOVE.equals( event.getProperty() ) && isDynamicWorkingSet( event );
  }

  private static boolean isWorkingSetNameChangeEvent( PropertyChangeEvent event ) {
    return CHANGE_WORKING_SET_NAME_CHANGE.equals( event.getProperty() ) && isDynamicWorkingSet( event );
  }

  private static boolean isDynamicWorkingSet( PropertyChangeEvent event ) {
    IWorkingSet workingSet = getWorkingSet( event );
    return ID.equals( workingSet.getId() );
  }

  private static IWorkingSet getWorkingSet( PropertyChangeEvent event ) {
    IWorkingSet result = ( IWorkingSet )event.getNewValue();
    if( result == null ) {
      result = ( IWorkingSet )event.getOldValue();
    }
    return result;
  }

  private class ResourceDeltaVisitor implements IResourceDeltaVisitor {

    private boolean updateNeeded;

    @Override
    public boolean visit( IResourceDelta delta ) throws CoreException {
      updateNeeded = isProjectChange( delta );
      return !updateNeeded;
    }

    private boolean isProjectChange( IResourceDelta delta ) {
      int kind = delta.getKind();
      IResource resource = delta.getResource();
      return ( kind == ADDED || kind == CHANGED || kind == REMOVED ) && resource instanceof IProject;
    }

    boolean isUpdateNeeded() {
      return updateNeeded;
    }
  }
}
