package com.codeaffine.extras.workingset.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

public class DynamicWorkingSetFactory implements WorkingSetFactory {

  private final IWorkingSetManager workingSetManager;

  public DynamicWorkingSetFactory() {
    this.workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
  }

  public IWorkingSetManager getWorkingSetManager() {
    return workingSetManager;
  }

  @Override
  public IWorkingSet createWorkingSet() {
    IWorkingSet result = workingSetManager.createWorkingSet( "", new IAdaptable[ 0 ] );
    result.setId( DynamicWorkingSet.ID );
    return result;
  }

}
