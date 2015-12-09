package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class CleanupPreferencePagePDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();
  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private LaunchPreferences launchPreferences;
  private CleanupPreferencePage preferencePage;

  @Before
  public void setUp() {
    PreferenceStore preferenceStore = new PreferenceStore();
    launchPreferences = new LaunchPreferences( preferenceStore );
    preferencePage = new CleanupPreferencePage( preferenceStore );
  }

  @Test
  public void testCreateContentsWithCleanupDisabled() {
    preferencePage.createContents( displayHelper.createShell() );

    assertThat( preferencePage.cleanupButton.getSelection() ).isFalse();
    assertThat( getCheckedCleanupLaunchConfigTypes() ).isEmpty();
    assertThat( preferencePage.cleanupTypesLabel.getEnabled() ).isFalse();
    assertThat( preferencePage.cleanupTypesViewer.getControl().getEnabled() ).isFalse();
  }

  @Test
  public void testCreateContentsWithCleanupEnabled() {
    launchPreferences.setCleanupGeneratedLaunchConfigs( true );
    preferencePage.createContents( displayHelper.createShell() );

    assertThat( preferencePage.cleanupButton.getSelection() ).isTrue();
  }

  @Test
  public void testCreateContentsWithCleanupLaunchConfigTypes() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();
    prepareLaunchPreferences( true, type );
    preferencePage.createContents( displayHelper.createShell() );

    assertThat( getCheckedCleanupLaunchConfigTypes() ).containsOnly( type );
  }

  @Test
  public void testSelectCleanupEnabledButton() {
    preferencePage.createContents( displayHelper.createShell() );

    preferencePage.cleanupButton.setSelection( true );
    preferencePage.cleanupButton.notifyListeners( SWT.Selection, null );

    assertThat( preferencePage.cleanupTypesLabel.getEnabled() ).isTrue();
    assertThat( preferencePage.cleanupTypesViewer.getControl().getEnabled() ).isTrue();
    assertThat( preferencePage.selectAllButton.getEnabled() ).isTrue();
    assertThat( preferencePage.deselectAllButton.getEnabled() ).isTrue();
    assertThat( launchPreferences.isCleanupGeneratedLaunchConfigs() ).isTrue();
  }

  @Test
  public void testDeselectCleanupEnabledButtonWhileLaunchConfigTypesChecked() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();
    prepareLaunchPreferences( true, type );
    preferencePage.createContents( displayHelper.createShell() );

    preferencePage.cleanupButton.setSelection( false );
    preferencePage.cleanupButton.notifyListeners( SWT.Selection, null );

    assertThat( getCheckedCleanupLaunchConfigTypes() ).containsOnly( type );
  }

  @Test
  public void testSelectAllButton() {
    preferencePage.createContents( displayHelper.createShell() );

    preferencePage.selectAllButton.notifyListeners( SWT.Selection, null );

    assertThat( getCheckedCleanupLaunchConfigTypes() ).isNotEmpty();
    assertThat( getCheckedCleanupLaunchConfigTypes().length ).isEqualTo( getCleanupLaunchConfigTypesCount() );
  }

  @Test
  public void testDeselectAllButton() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();
    prepareLaunchPreferences( true, type );
    preferencePage.createContents( displayHelper.createShell() );
    preferencePage.selectAllButton.notifyListeners( SWT.Selection, null );

    preferencePage.deselectAllButton.notifyListeners( SWT.Selection, null );

    assertThat( getCheckedCleanupLaunchConfigTypes() ).isEmpty();
  }

  @Test
  public void testApplyDefaults() {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    preferencePage.createContents( displayHelper.createShell() );

    preferencePage.performDefaults();

    assertThat( preferencePage.cleanupButton.getSelection() ).isFalse();
    assertThat( getCheckedCleanupLaunchConfigTypes() ).isEmpty();
  }

  @Test
  public void testPerformOk() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();
    preferencePage.createContents( displayHelper.createShell() );
    preferencePage.cleanupButton.setSelection( true );
    preferencePage.cleanupButton.notifyListeners( SWT.Selection, null );
    preferencePage.cleanupTypesViewer.setChecked( type, true );

    preferencePage.performOk();

    assertThat( launchPreferences.isCleanupGeneratedLaunchConfigs() ).isTrue();
    assertThat( launchPreferences.getCleanupGenerateLaunchConfigTypes() ).isEqualTo( type.getIdentifier() );
  }

  @Test
  public void testGetTitle() {
    String title = preferencePage.getTitle();

    assertThat( title ).isNotEmpty();
  }

  @Test
  public void testGetPreferenceStore() {
    IPreferenceStore preferenceStore = preferencePage.getPreferenceStore();

    assertThat( preferenceStore ).isEqualTo( launchPreferences.getPreferenceStore() );
  }

  private Object[] getCheckedCleanupLaunchConfigTypes() {
    return preferencePage.cleanupTypesViewer.getCheckedElements();
  }

  private int getCleanupLaunchConfigTypesCount() {
    return preferencePage.cleanupTypesViewer.getTable().getItemCount();
  }

  private void prepareLaunchPreferences( boolean cleanupEnabled, ILaunchConfigurationType... types ) {
    launchPreferences.setCleanupGeneratedLaunchConfigs( cleanupEnabled );
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    String serializedTypes = new LaunchConfigTypeSerializer( launchManager ).serialize( types );
    launchPreferences.setCleanupGenerateLaunchConfigTypes( serializedTypes );
  }

}
