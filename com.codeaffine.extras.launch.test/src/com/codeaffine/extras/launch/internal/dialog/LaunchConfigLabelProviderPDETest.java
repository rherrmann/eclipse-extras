package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.DETAIL;
import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode;
import com.codeaffine.extras.launch.test.LaunchConfigRule;
import com.codeaffine.extras.test.util.ProjectHelper;

public class LaunchConfigLabelProviderPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();
  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();
  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private DuplicatesDetector duplicatesDetector;
  private Collection<LaunchConfigLabelProvider> labelProviders;

  @Before
  public void setUp() {
    duplicatesDetector = mock( DuplicatesDetector.class );
  }

  @After
  public void tearDown() {
    for( LaunchConfigLabelProvider labelProvider : labelProviders ) {
      labelProvider.dispose();
    }
  }

  @Test
  public void testGetImage() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
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
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() );
  }

  @Test
  public void testGetListTextForDuplicate() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    when( duplicatesDetector.isDuplicateElement( launchConfig ) ).thenReturn( true );
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() + " - " + launchConfig.getType().getName() );
  }

  @Test
  public void testGetListTextForDuplicateInContainer() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    saveToContainer( launchConfig );
    when( duplicatesDetector.isDuplicateElement( launchConfig ) ).thenReturn( true );
    LaunchConfigLabelProvider labelProvider = createLabelProvider( LIST );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() + " - " + launchConfig.getFile().getParent().getName() );
  }

  @Test
  public void testGetDetailText() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    LaunchConfigLabelProvider labelProvider = createLabelProvider( DETAIL );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() + " - " + launchConfig.getType().getName() );
  }

  @Test
  public void testGetDetailTextForDuplicate() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    when( duplicatesDetector.isDuplicateElement( launchConfig ) ).thenReturn( true );
    LaunchConfigLabelProvider labelProvider = createLabelProvider( DETAIL );

    String text = labelProvider.getText( launchConfig );

    assertThat( text ).isEqualTo( launchConfig.getName() + " - " + launchConfig.getType().getName() );
  }

  @Test
  public void testGetDetailTextForContainerLaunchConfig() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createLaunchConfig();
    saveToContainer( launchConfig );
    LaunchConfigLabelProvider labelProvider = createLabelProvider( DETAIL );

    String text = labelProvider.getText( launchConfig );

    String expected
      = launchConfig.getName()
      + " - " + launchConfig.getType().getName()
      + " (" + launchConfig.getFile().getParent().getName() + ")";
    assertThat( text ).isEqualTo( expected );
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

  private LaunchConfigLabelProvider createLabelProvider( LabelMode labelMode ) {
    labelProviders = new ArrayList<>();
    Display display = displayHelper.getDisplay();
    LaunchConfigLabelProvider result = new LaunchConfigLabelProvider( display, duplicatesDetector, labelMode );
    labelProviders.add( result );
    return result;
  }

  private void saveToContainer( ILaunchConfigurationWorkingCopy launchConfig ) throws CoreException {
    launchConfig.setContainer( projectHelper.getProject() );
    launchConfig.doSave();
  }

}
