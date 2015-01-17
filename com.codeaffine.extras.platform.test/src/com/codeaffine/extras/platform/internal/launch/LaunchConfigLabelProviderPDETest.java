package com.codeaffine.extras.platform.internal.launch;

import static com.codeaffine.extras.platform.internal.launch.LaunchConfigLabelProvider.LabelMode.DETAIL;
import static com.codeaffine.extras.platform.internal.launch.LaunchConfigLabelProvider.LabelMode.LIST;
import static com.codeaffine.extras.platform.test.LaunchManagerHelper.createLaunchConfig;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.platform.internal.launch.LaunchConfigLabelProvider.LabelMode;

public class LaunchConfigLabelProviderPDETest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private Collection<LaunchConfigLabelProvider> labelProviders;

  @Test
  public void testGetImage() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    Image image = labelProvider.getImage( launchConfig );

    assertThat( image ).isNotNull();
  }

  @Test
  public void testGetImageForArbitraryObject() {
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    Image image = labelProvider.getImage( new Object() );

    assertThat( image ).isNull();
  }

  @Test
  public void testGetListText() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() );
  }

  @Test
  public void testGetDetailText() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( DETAIL );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() + " - " + launchConfig.getType().getName() );
  }

  @Test
  public void testGetTextForNullArgument() {
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( null );

    assertThat( text ).isNotNull();
  }

  @Test
  public void testGetTextForArbitraryObject() {
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( new Object() );

    assertThat( text ).isNotNull();
  }

  @Test
  public void testDispose() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );
    Image image = labelProvider.getImage( launchConfig );

    labelProvider.dispose();

    assertThat( image.isDisposed() ).isTrue();
  }

  @After
  public void tearDown() {
    for( LaunchConfigLabelProvider labelProvider : labelProviders ) {
      labelProvider.dispose();
    }
  }

  private LaunchConfigLabelProvider createLabelProvider( LabelMode labelMode ) {
    labelProviders = newArrayList();
    Shell shell = displayHelper.createShell();
    LaunchSelectionDialog dialog = new LaunchSelectionDialog( shell );
    Display display = shell.getDisplay();
    LaunchConfigLabelProvider result = new LaunchConfigLabelProvider( display, dialog, labelMode );
    labelProviders.add( result );
    return result;
  }

}
