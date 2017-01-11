package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class DynamicWorkingSetFactoryPDETest {

  @Test
  public void testConstructor() {
    DynamicWorkingSetFactory factory = new DynamicWorkingSetFactory();

    assertThat( factory.getWorkingSetManager() ).isEqualTo( platformWorkingSetManager() );
  }

  @Test
  public void testCreateWorkingSet() {
    DynamicWorkingSetFactory factory = new DynamicWorkingSetFactory();

    IWorkingSet workingSet = factory.createWorkingSet();

    assertThat( workingSet.getName() ).isEmpty();
    assertThat( workingSet.getId() ).isEqualTo( DynamicWorkingSet.ID );
    assertThat( workingSet.getElements() ).isEmpty();
  }

  private static IWorkingSetManager platformWorkingSetManager() {
    return PlatformUI.getWorkbench().getWorkingSetManager();
  }
}
