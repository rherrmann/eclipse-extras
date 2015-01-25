package com.codeaffine.extras.ide.internal.launch;

import static com.codeaffine.extras.platform.test.LaunchManagerHelper.createLaunchConfig;
import static com.codeaffine.extras.platform.test.LaunchManagerHelper.deleteLaunchConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.ide.internal.launch.LaunchConfigSelectionHistory;
import com.codeaffine.extras.platform.test.LaunchManagerHelper;

public class LaunchConfigSelectionHistoryPDETest {

  private LaunchConfigSelectionHistory history;
  private ILaunchConfigurationWorkingCopy launchConfig;
  private XMLMemento memento;

  @Test
  public void testRestore() {
    history.storeItemToMemento( launchConfig, memento );

    ILaunchConfiguration restoredLaunchConfig
      = ( ILaunchConfiguration )history.restoreItemFromMemento( memento );

    assertThat( restoredLaunchConfig.contentsEqual( launchConfig ) ).isTrue();
  }

  @Test
  public void testRestoreWhenLaunchConfigWasDeleted() throws CoreException {
    history.storeItemToMemento( launchConfig, memento );
    LaunchManagerHelper.deleteLaunchConfig( launchConfig.getName() );

    Object restoredLaunchConfig = history.restoreItemFromMemento( memento );

    assertThat( restoredLaunchConfig ).isNull();
  }

  @Before
  public void setUp() throws CoreException {
    memento = XMLMemento.createWriteRoot( "root" );
    launchConfig = createLaunchConfig();
    launchConfig.doSave();
    history = new LaunchConfigSelectionHistory( DebugPlugin.getDefault().getLaunchManager() );
  }

  @After
  public void tearDown() throws CoreException {
    deleteLaunchConfig( launchConfig.getName() );
  }
}
