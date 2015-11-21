package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_PRIVATE;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.LaunchConfigProvider;
import com.codeaffine.extras.launch.test.LaunchConfigRule;
import com.codeaffine.extras.test.util.ProjectHelper;

public class LaunchConfigProviderPDETest {

  private static final String DEBUG_PLUGIN_ID = IDebugUIConstants.PLUGIN_ID;
  private static final String PREF_FILTER_LAUNCH_CLOSED = DEBUG_PLUGIN_ID + ".PREF_FILTER_LAUNCH_CLOSED";
  private static final String PREF_FILTER_LAUNCH_DELETED = DEBUG_PLUGIN_ID + ".PREF_FILTER_LAUNCH_DELETED";

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();
  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private ScopedPreferenceStore preferenceStore;
  private ILaunchManager launchManager;
  private LaunchConfigProvider launchConfigProvider;

  @Before
  public void setUp() {
    preferenceStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, DEBUG_PLUGIN_ID );
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    setFilterLaunchConfigsInClosedProjects( false );
    setFilterLaunchConfigsInDeletedProjects( false );
    launchConfigProvider = new LaunchConfigProvider( launchManager );
  }

  @Test
  public void testNoLaunchConfigs() throws CoreException {
    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testSingleLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createLaunchConfig().doSave();

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).containsOnly( launchConfig );
  }

  @Test
  public void testKeepWhenPrivate() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    launchConfig.setAttribute( ATTR_PRIVATE, true );
    launchConfig.doSave();

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).hasSize( 1 );
  }

  @Test
  public void testFilterWhenMappedProjectWasDeleted() throws CoreException {
    setFilterLaunchConfigsInDeletedProjects( true );
    IResource resource = createLaunchConfigForResource();
    ProjectHelper.delete( resource.getProject() );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testFilterWhenMappedProjectWasClosed() throws CoreException {
    setFilterLaunchConfigsInClosedProjects( true );
    IResource resource = createLaunchConfigForResource();
    resource.getProject().close( null );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testKeepWhenMappedProjectWasDeletedAndFilterClosedOptionOn() throws CoreException {
    setFilterLaunchConfigsInClosedProjects( true );
    IResource resource = createLaunchConfigForResource();
    ProjectHelper.delete( resource.getProject() );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).hasSize( 1 );
  }

  @Test
  public void testKeepWhenMappedProjectWasClosedAndFilterDeletedOptionOn() throws CoreException {
    setFilterLaunchConfigsInDeletedProjects( true );
    IResource resource = createLaunchConfigForResource();
    resource.getProject().close( null );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).hasSize( 1 );
  }

  private void setFilterLaunchConfigsInDeletedProjects( boolean value ) {
    preferenceStore.setValue( PREF_FILTER_LAUNCH_DELETED, value );
  }

  private void setFilterLaunchConfigsInClosedProjects( boolean value ) {
    preferenceStore.setValue( PREF_FILTER_LAUNCH_CLOSED, value );
  }

  private IResource createLaunchConfigForResource() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    IFile resource = projectHelper.createFile();
    launchConfig.setMappedResources( new IResource[] { resource } );
    launchConfig.doSave();
    return resource;
  }

}
