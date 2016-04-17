package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.Before;
import org.junit.Test;

public class ToggleTerminateBeforeRelaunchActionTest {

  private DebugUIPreferences preferences;

  @Before
  public void setUp() {
    preferences = new DebugUIPreferences( new PreferenceStore() );
  }

  @Test
  public void testText() {
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    String text = action.getText();

    assertThat( text ).isNotEmpty();
  }

  @Test
  public void testId() {
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    String id = action.getId();

    assertThat( id ).isEqualTo( ToggleTerminateBeforeRelaunchAction.ID );
  }

  @Test
  public void testStyle() {
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    int style = action.getStyle();

    assertThat( style ).isEqualTo( IAction.AS_CHECK_BOX );
  }

  @Test
  public void testIsCheckedWhenDisabled() {
    preferences.setTerminateBeforeRelaunch( false );
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    boolean checked = action.isChecked();

    assertThat( checked ).isFalse();
  }

  @Test
  public void testIsCheckedWhenEnabled() {
    preferences.setTerminateBeforeRelaunch( true );
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    boolean checked = action.isChecked();

    assertThat( checked ).isTrue();
  }

  @Test
  public void testRunWhileDisabled() {
    preferences.setTerminateBeforeRelaunch( false );
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    action.run();

    assertThat( preferences.isTerminateBeforeRelaunch() ).isTrue();
  }

  @Test
  public void testRunWhileEnabled() {
    preferences.setTerminateBeforeRelaunch( true );
    ToggleTerminateBeforeRelaunchAction action = new ToggleTerminateBeforeRelaunchAction( preferences );

    action.run();

    assertThat( preferences.isTerminateBeforeRelaunch() ).isFalse();
  }
}
