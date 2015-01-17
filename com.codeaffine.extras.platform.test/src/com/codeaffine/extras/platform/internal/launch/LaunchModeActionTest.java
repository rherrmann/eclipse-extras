package com.codeaffine.extras.platform.internal.launch;

import static com.codeaffine.extras.platform.test.LaunchModeHelper.createLaunchMode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.platform.test.LaunchModeHelper;

public class LaunchModeActionTest {

  private LaunchModeSetting launchModeSetting;
  private ILaunchMode launchMode;

  @Test
  public void testGetText() {
    LaunchModeAction action = createLaunchModeAction();

    String text = action.getText();

    assertThat( text ).isNotEmpty();
  }

  @Test
  public void testGetStyle() {
    LaunchModeAction action = createLaunchModeAction();

    int style = action.getStyle();

    assertThat( style ).isEqualTo( IAction.AS_CHECK_BOX );
  }

  @Test
  public void testGetMenuCreator() {
    LaunchModeAction action = createLaunchModeAction();

    IMenuCreator menuCreator = action.getMenuCreator();

    assertThat( menuCreator ).isNull();
  }

  @Test
  public void testInitialCheckState() {
    changeCurrentLaunchMode( launchMode );;
    LaunchModeAction action = createLaunchModeAction();

    assertThat( action.isChecked() ).isTrue();
  }

  @Test
  public void testUpdateForCurrentLaunchMode() {
    changeCurrentLaunchMode( createLaunchMode( DEBUG_MODE ) );

    LaunchModeAction action = createLaunchModeAction();
    changeCurrentLaunchMode( launchMode );;

    action.update();

    assertThat( action.isChecked() ).isTrue();
  }

  @Test
  public void testUpdateForNonCurrentLaunchMode() {
    LaunchModeAction action = createLaunchModeAction();
    changeCurrentLaunchMode( createLaunchMode( DEBUG_MODE ) );

    action.update();

    assertThat( action.isChecked() ).isFalse();
  }

  @Test
  public void testRun() {
    LaunchModeAction action = createLaunchModeAction();

    action.run();

    verify( launchModeSetting ).setLaunchModeId( launchMode.getIdentifier() );
  }

  @Before
  public void setUp() {
    launchModeSetting = mock( LaunchModeSetting.class );
    launchMode = LaunchModeHelper.createLaunchMode( RUN_MODE );
  }

  private LaunchModeAction createLaunchModeAction() {
    return new LaunchModeAction( launchModeSetting, launchMode );
  }

  private void changeCurrentLaunchMode( ILaunchMode currentLaunchMode ) {
    when( launchModeSetting.getLaunchMode() ).thenReturn( currentLaunchMode );
  }

}
