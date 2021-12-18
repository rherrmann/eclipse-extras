package com.codeaffine.extras.jdt.internal.prefs;

import static com.codeaffine.extras.jdt.internal.prefs.WorkspaceScopePreferences.PREF_SHOW_JUNIT_STATUS_BAR;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.PreferenceStore;
import org.junit.Before;
import org.junit.Test;

public class WorkspaceScopePreferencesTest {

  private PreferenceStore store;
  private WorkspaceScopePreferences workspaceScopePreferences;

  @Before
  public void setUp() {
    store = new PreferenceStore();
    workspaceScopePreferences = new WorkspaceScopePreferences(store);
  }

  @Test
  public void testSetShowJUnitStatusBar() {
    workspaceScopePreferences.setShowJUnitStatusBar(true);

    assertThat(store.getBoolean(PREF_SHOW_JUNIT_STATUS_BAR)).isTrue();
  }

  @Test
  public void testIsShowJUnitStatusBar() {
    store.setValue(PREF_SHOW_JUNIT_STATUS_BAR, true);

    assertThat(workspaceScopePreferences.isShowJUnitStatusBar()).isTrue();
  }

  @Test
  public void testGetPreferenceStore() {
    assertThat(workspaceScopePreferences.getPreferenceStore()).isSameAs(store);
  }
}
