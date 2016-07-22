package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.test.LaunchModeHelper.createLaunchMode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.dialogs.DialogSettings;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.LaunchModeSetting;



public class LaunchModeSettingTest {

  private ILaunchManager launchManager;
  private DialogSettings dialogSettings;
  private LaunchModeSetting launchModeSetting;

  @Test
  public void testGetLaunchManager() {
    assertThat( launchModeSetting.getLaunchManager() ).isSameAs( launchManager );
  }

  @Test
  public void testDefaultLaunchMode() {
    ILaunchMode debug = createLaunchMode( DEBUG_MODE );
    setSupportedLaunchModes( debug );

    String launchModeId = launchModeSetting.getLaunchModeId();

    assertThat( launchModeId ).isEqualTo( DEBUG_MODE );
  }

  @Test
  public void testSetLaunchMode() {
    ILaunchMode debug = createLaunchMode( DEBUG_MODE );
    ILaunchMode run = createLaunchMode( RUN_MODE );
    setSupportedLaunchModes( debug, run );

    launchModeSetting.setLaunchModeId( RUN_MODE );

    assertThat( launchModeSetting.getLaunchModeId() ).isEqualTo( RUN_MODE );
  }

  @Test
  public void testGetUnknownLaunchMode() {
    ILaunchMode debug = createLaunchMode( DEBUG_MODE );
    setSupportedLaunchModes( debug );

    launchModeSetting.setLaunchModeId( RUN_MODE );

    assertThat( launchModeSetting.getLaunchModeId() ).isEqualTo( DEBUG_MODE );
  }

  @Before
  public void setUp() {
    launchManager = mock( ILaunchManager.class );
    dialogSettings = new DialogSettings( "section-name" );
    launchModeSetting = new LaunchModeSetting( launchManager, dialogSettings );
  }

  private void setSupportedLaunchModes( ILaunchMode... launchModes ) {
    when( launchManager.getLaunchModes() ).thenReturn( launchModes );
  }
}
