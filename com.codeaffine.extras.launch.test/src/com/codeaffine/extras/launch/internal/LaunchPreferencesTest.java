package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Before;
import org.junit.Test;

public class LaunchPreferencesTest {

  private PreferenceStore preferenceStore;
  private LaunchPreferences launchPreferences;

  @Before
  public void setUp() {
    preferenceStore = new PreferenceStore();
    launchPreferences = new LaunchPreferences( preferenceStore );
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullPreferenceStore() {
    new LaunchPreferences( null );
  }

  @Test
  public void testGetPreferenceStore() {
    IPreferenceStore preferenceStore = new LaunchPreferences().getPreferenceStore();

    assertThat( preferenceStore ).isInstanceOf( ScopedPreferenceStore.class );
  }

  @Test
  public void testIsFilterLaunchConfigsInClosedProjects() {
    preferenceStore.setValue( LaunchPreferences.PREF_FILTER_LAUNCH_CLOSED, true );

    boolean value = launchPreferences.isFilterLaunchConfigsInClosedProjects();

    assertThat( value ).isTrue();
  }

  @Test
  public void testSetFilterLaunchConfigsInClosedProjects() {
    launchPreferences.setFilterLaunchConfigsInClosedProjects( true );

    boolean value = launchPreferences.isFilterLaunchConfigsInClosedProjects();

    assertThat( value ).isTrue();
  }

  @Test
  public void testIsFilterLaunchConfigsInDeletedProjects() {
    preferenceStore.setValue( LaunchPreferences.PREF_FILTER_LAUNCH_DELETED, true );

    boolean value = launchPreferences.isFilterLaunchConfigsInDeletedProjects();

    assertThat( value ).isTrue();
  }

  @Test
  public void testSetFilterLaunchConfigsInDeletedProjects() {
    launchPreferences.setFilterLaunchConfigsInDeletedProjects( true );

    boolean value = launchPreferences.isFilterLaunchConfigsInDeletedProjects();

    assertThat( value ).isTrue();
  }

}
