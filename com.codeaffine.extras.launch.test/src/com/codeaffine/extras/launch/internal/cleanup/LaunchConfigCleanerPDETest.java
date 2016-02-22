package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;
import com.codeaffine.extras.test.util.ProjectHelper;

@SuppressWarnings("restriction")
public class LaunchConfigCleanerPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();
  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private LaunchPreferences launchPreferences;
  private LaunchConfigCleaner launchConfigCleaner;

  @Before
  public void setUp() {
    launchPreferences = new LaunchPreferences( new PreferenceStore() );
    launchConfigCleaner = new LaunchConfigCleaner( launchPreferences );
  }

  @After
  public void tearDown() {
    launchConfigCleaner.uninstall();
  }

  @Test
  public void testCleanupAfterNextLaunch() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();
    launch( launchConfig );

    launch( launchConfigRule.createLaunchConfig().doSave() );

    assertThat( getLaunchConfigs() ).extracting( "name" ).doesNotContain( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupAfterLaunchingAgain() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();
    launch( launchConfig );

    launch( launchConfig );

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupWhenLaunchConfigRenamed() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();

    ILaunch launch = launchConfig.launch( RUN_MODE, null );
    String newName = renameLaunchConfig( launchConfig );
    launch.terminate();

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( newName );
  }

  @Test
  public void testNoCleanupWithImmediatelyStoredLaunchConfig() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration workingCopy = launchConfigRule.createLaunchConfig().doSave();
    ILaunchConfiguration launchConfig = storeLaunchConfigInWorkspace( workingCopy );
    launch( launchConfig );

    launch( launchConfigRule.createLaunchConfig().doSave() );

    assertThat( launchConfig.getFile().exists() ).isTrue();
    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupWithLaterStoredLaunchConfig() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();
    launch( launchConfig );
    launchConfig = storeLaunchConfigInWorkspace( launchConfig );

    launch( launchConfigRule.createLaunchConfig().doSave() );

    assertThat( launchConfig.getFile().exists() ).isTrue();
    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  private ILaunchConfiguration storeLaunchConfigInWorkspace( ILaunchConfiguration launchConfig )
    throws CoreException
  {
    ILaunchConfigurationWorkingCopy workingCopy = launchConfig.getWorkingCopy();
    workingCopy.setContainer( projectHelper.getProject() );
    return workingCopy.doSave();
  }

  @Test
  public void testNoCleanupIfDisabled() throws CoreException {
    prepareLaunchPreferences( false, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();

    launch( launchConfig );

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupIfTypeNotSelected() throws CoreException {
    prepareLaunchPreferences( true, null );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();

    launch( launchConfig );

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupAfterUninstall() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();
    launchConfigCleaner.uninstall();

    launch( launchConfig );

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupWhenLaunchConfigDeleted() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();

    ILaunch launch = launchConfig.launch( RUN_MODE, null );
    launchConfig.delete();
    launch.terminate();

    assertThat( getLaunchConfigs() ).extracting( "name" ).doesNotContain( launchConfig.getName() );
  }

  @Test
  public void testNoCleanupWhenLaunchConfigWasCreatedInDialog() throws CoreException {
    prepareLaunchPreferences( true, launchConfigRule.getPublicTestLaunchConfigType() );
    launchConfigCleaner.install();
    ILaunchConfiguration launchConfig = createLaunchConfigInDialog();

    launch( launchConfig );

    assertThat( getLaunchConfigs() ).extracting( "name" ).contains( launchConfig.getName() );
  }

  private void prepareLaunchPreferences( boolean cleanupEnabled, ILaunchConfigurationType selectedType ) {
    launchPreferences.setCleanupGeneratedLaunchConfigs( cleanupEnabled );
    if( selectedType != null ) {
      launchPreferences.setCleanupGenerateLaunchConfigTypes( selectedType.getIdentifier() );
    } else {
      launchPreferences.setCleanupGenerateLaunchConfigTypes( "" );
    }
  }

  private ILaunchConfiguration createLaunchConfigInDialog() throws CoreException {
    ILaunchConfigurationDialog dialog = mock( ILaunchConfigurationDialog.class );
    LaunchConfigurationsDialog.setCurrentlyVisibleLaunchConfigurationDialog( dialog );
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();
    LaunchConfigurationsDialog.setCurrentlyVisibleLaunchConfigurationDialog( null );
    return launchConfig;
  }

  private static String renameLaunchConfig( ILaunchConfiguration launchConfig ) throws CoreException {
    String newName = launchConfig.getName() + "-renamed";
    ILaunchConfigurationWorkingCopy workingCopy = launchConfig.getWorkingCopy();
    workingCopy.rename( newName );
    workingCopy.doSave();
    return newName;
  }

  private static void launch( ILaunchConfiguration launchConfig ) throws CoreException {
    ILaunch launch = launchConfig.launch( RUN_MODE, null );
    launch.terminate();
  }

  private static ILaunchConfiguration[] getLaunchConfigs() throws CoreException {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
  }

}
