package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Test;

public class DebugUIPreferencesPDETest {

  @Test
  public void testInitialTerminateBeforeRelaunchPreference() {
    DebugUIPreferences debugUIPreferences = new DebugUIPreferences();

    boolean terminateBeforeRelaunch = debugUIPreferences.isTerminateBeforeRelaunch();

    assertThat( terminateBeforeRelaunch ).isFalse();
  }

  @Test
  public void testPreferenceStoreType() {
    DebugUIPreferences debugUIPreferences = new DebugUIPreferences();

    IPreferenceStore preferenceStore = debugUIPreferences.getPreferenceStore();

    assertThat( preferenceStore ).isInstanceOf( ScopedPreferenceStore.class );
  }

}
