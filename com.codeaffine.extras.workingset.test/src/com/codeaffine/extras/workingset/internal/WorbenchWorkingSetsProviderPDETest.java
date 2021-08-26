package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.workingset.internal.WorbenchWorkingSetsProvider;

public class WorbenchWorkingSetsProviderPDETest {

  private IWorkingSetManager workingSetManager;
  private IWorkingSet workingSet;

  @Before
  public void setUp() {
    workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    workingSet = workingSetManager.createWorkingSet( "", new IAdaptable[ 0 ] );
  }

  @After
  public void tearDown() {
    workingSetManager.removeWorkingSet( workingSet );
  }

  @Test
  public void testGetWorkingSets() {
    workingSetManager.addWorkingSet( workingSet );

    IWorkingSet[] workingSets = new WorbenchWorkingSetsProvider().getWorkingSets();

    assertThat( workingSets ).contains( workingSet );
  }

  @Test
  public void testGetWorkingSetsWhenNoneRegistered() {
    IWorkingSet[] workingSets = new WorbenchWorkingSetsProvider().getWorkingSets();

    assertThat( workingSets ).doesNotContain( workingSet );
  }
}
