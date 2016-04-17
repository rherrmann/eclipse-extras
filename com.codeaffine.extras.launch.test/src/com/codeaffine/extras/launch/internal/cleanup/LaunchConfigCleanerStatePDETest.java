package com.codeaffine.extras.launch.internal.cleanup;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigCleanerStatePDETest {

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();
  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private ILaunchManager launchManager;

  @Before
  public void setUp() {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
  }

  @Test
  public void testSave() throws CoreException {
    File file = new File( tempFolder.getRoot(), "state.txt" );
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();

    new LaunchConfigCleanerState( launchManager, file ).save( launchConfig );

    assertThat( restore( file ) ).containsOnly( launchConfig );
  }

  @Test
  public void testSaveWithExistingFile() throws IOException, CoreException {
    File file = tempFolder.newFile();
    Files.write( file.toPath(), asList( "foo\nbar\n" ), UTF_8 );
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();

    new LaunchConfigCleanerState( launchManager, file ).save( launchConfig );

    assertThat( restore( file ) ).containsOnly( launchConfig );
  }

  @Test
  public void testRestoreWithNonExistingFile() {
    File file = new File( tempFolder.getRoot(), "does-not-exist" );

    ILaunchConfiguration[] launchConfigs = restore( file );

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testRestoreWithEmptyFile() throws IOException {
    File file = tempFolder.newFile();

    ILaunchConfiguration[] launchConfigs = restore( file );

    assertThat( launchConfigs ).isEmpty();
  }

  @Test
  public void testRestoreWithNonExistingLaunchConfigName() throws IOException {
    File file = tempFolder.newFile();
    Files.write( file.toPath(), asList( "LC-does-not-exist" ), UTF_8 );

    ILaunchConfiguration[] launchConfigs = restore( file );

    assertThat( launchConfigs ).isEmpty();
  }

  private ILaunchConfiguration[] restore( File file ) {
    return new LaunchConfigCleanerState( launchManager, file ).restore();
  }
}
