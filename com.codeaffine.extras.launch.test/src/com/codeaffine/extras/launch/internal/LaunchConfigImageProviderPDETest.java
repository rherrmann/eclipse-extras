package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.launch.internal.LaunchConfigImageProvider;
import com.codeaffine.extras.launch.test.LaunchManagerHelper;

public class LaunchConfigImageProviderPDETest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private LaunchConfigImageProvider launchConfigImageProvider;

  private ILaunchConfigurationWorkingCopy launchConfig;

  @Before
  public void setUp() throws CoreException {
    launchConfig = LaunchManagerHelper.createLaunchConfig();
    launchConfigImageProvider = new LaunchConfigImageProvider( displayHelper.getDisplay() );
  }

  @After
  public void tearDown() throws CoreException {
    launchConfigImageProvider.dispose();
    LaunchManagerHelper.deleteLaunchConfig( launchConfig.getName() );
  }

  @Test
  public void testGetImage() {
    Image image = launchConfigImageProvider.getImage( launchConfig );

    assertThat( image ).isNotNull();
  }

  @Test
  public void testGetImageTwice() {
    Image image1 = launchConfigImageProvider.getImage( launchConfig );
    Image image2 = launchConfigImageProvider.getImage( launchConfig );

    assertThat( image1 ).isSameAs( image2 );
  }
}
