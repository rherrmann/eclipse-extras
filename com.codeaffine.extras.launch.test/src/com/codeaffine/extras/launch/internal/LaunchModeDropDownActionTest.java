package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.launch.test.LaunchModeHelper.createLaunchMode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.launch.internal.LaunchModeDropDownAction;
import com.codeaffine.extras.launch.internal.LaunchModeSetting;

public class LaunchModeDropDownActionTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ILaunchManager launchManager;
  private LaunchModeSetting launchModeSetting;

  @Test
  public void testMenuCreator() {
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );

    IMenuCreator menuCreator = action.getMenuCreator();

    assertThat( menuCreator ).isSameAs( action );
  }

  @Test
  public void testText() {
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );

    String text = action.getText();

    assertThat( text ).isNotEmpty();
  }

  @Test(expected=UnsupportedOperationException.class)
  public void testGetMenuForControl() {
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );
    action.getMenu( ( Control )null );
  }

  @Test
  public void testGetMenuForMenu() {
    ILaunchMode run = createLaunchMode( RUN_MODE );
    ILaunchMode debug = createLaunchMode( DEBUG_MODE );
    when( launchManager.getLaunchModes() ).thenReturn( new ILaunchMode[]{ run, debug } );
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );

    Menu menu = action.getMenu( new Menu( displayHelper.createShell() ) );

    assertThat( menu.getItemCount() ).isEqualTo( 2 );
    assertThat( menu.getItem( 0 ).getText() ).isNotEmpty();
    assertThat( menu.getItem( 0 ).getImage() ).isNull();
  }

  @Test
  public void testDispose() {
    ILaunchMode run = createLaunchMode( RUN_MODE );
    when( launchManager.getLaunchModes() ).thenReturn( new ILaunchMode[]{ run } );
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );
    Menu menu = action.getMenu( new Menu( displayHelper.createShell() ) );

    action.dispose();

    assertThat( menu.isDisposed() ).isTrue();
  }

  @Before
  public void setUp() {
    launchManager = mock( ILaunchManager.class );
    when( launchManager.getLaunchModes() ).thenReturn( new ILaunchMode[ 0 ] );
    DialogSettings dialogSettings = new DialogSettings( "section-name" );
    launchModeSetting = new LaunchModeSetting( launchManager, dialogSettings );
  }
}
