package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_PRIVATE;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigurationRule;
import com.codeaffine.extras.test.util.ProjectHelper;

public class LaunchConfigProviderPDETest {

  @Rule
  public final LaunchConfigurationRule launchConfigRule = new LaunchConfigurationRule();
  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private ILaunchManager launchManager;
  private LaunchPreferences launchPreferences;
  private LaunchConfigProvider launchConfigProvider;

  @Before
  public void setUp() {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    launchPreferences = new LaunchPreferences();
    launchPreferences.setFilterLaunchConfigsInClosedProjects( false );
    launchPreferences.setFilterLaunchConfigsInDeletedProjects( false );
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
    launchPreferences.setFilterLaunchConfigsInDeletedProjects( true );
    IResource resource = createLaunchConfigForResource();
    ProjectHelper.delete( resource.getProject() );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testFilterWhenMappedProjectWasClosed() throws CoreException {
    launchPreferences.setFilterLaunchConfigsInClosedProjects( true );
    IResource resource = createLaunchConfigForResource();
    resource.getProject().close( null );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testKeepWhenMappedProjectWasDeletedAndFilterClosedOptionOn() throws CoreException {
    launchPreferences.setFilterLaunchConfigsInClosedProjects( true );
    IResource resource = createLaunchConfigForResource();
    ProjectHelper.delete( resource.getProject() );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).hasSize( 1 );
  }

  @Test
  public void testKeepWhenMappedProjectWasClosedAndFilterDeletedOptionOn() throws CoreException {
    launchPreferences.setFilterLaunchConfigsInDeletedProjects( true );
    IResource resource = createLaunchConfigForResource();
    resource.getProject().close( null );

    ILaunchConfiguration[] launchConfigs = launchConfigProvider.getLaunchConfigurations();

    assertThat( launchConfigs ).hasSize( 1 );
  }

  private IResource createLaunchConfigForResource() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    IFile resource = projectHelper.createFile();
    launchConfig.setMappedResources( new IResource[] { resource } );
    launchConfig.doSave();
    return resource;
  }

}
