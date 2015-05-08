package com.codeaffine.extras.internal.workingset;

import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;


public class WorbenchWorkingSetsProvider implements WorkingSetsProvider {

  @Override
  public IWorkingSet[] getWorkingSets() {
    return PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
  }
}
