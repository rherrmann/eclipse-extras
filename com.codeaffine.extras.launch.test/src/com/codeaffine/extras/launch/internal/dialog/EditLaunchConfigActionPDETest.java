package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class EditLaunchConfigActionPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private LaunchSelectionDialog dialog;
  private EditLaunchConfigAction action;

  @Before
  public void setUp() {
    dialog = mock( LaunchSelectionDialog.class );
    action = spy( new EditLaunchConfigAction( dialog ) );
  }

  @Test
  public void testGetId() {
    assertThat( action.getId() ).isEqualTo( EditLaunchConfigAction.ID );
  }

  @Test
  public void testGetText() {
    String text = action.getText();

    assertThat( text ).isNotEmpty();
  }

  @Test
  public void testGetImageDescriptor() {
    ImageDescriptor imageDescriptor = action.getImageDescriptor();

    assertThat( imageDescriptor ).isNull();
  }

  @Test
  public void testInitialEnablement() {
    assertThat( action.isEnabled() ).isFalse();
  }

  @Test
  public void testInitialSelection() {
    assertThat( action.getSelection().isEmpty() ).isTrue();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetSelection() {
    StructuredSelection selection = new StructuredSelection();
    action.setSelection( selection );

    IStructuredSelection returnedSelection = action.getSelection();

    assertThat( returnedSelection ).isSameAs( selection );
  }

  @Test
  public void testSetSelectionWithSingleLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig();

    action.setSelection( new StructuredSelection( launchConfig ) );

    assertThat( action.isEnabled() ).isTrue();
  }

  @Test
  public void testSetSelectionWithMultipleLaunchConfigs() throws CoreException {
    ILaunchConfiguration launchConfig1 = launchConfigRule.createPublicLaunchConfig();
    ILaunchConfiguration launchConfig2 = launchConfigRule.createPublicLaunchConfig();

    action.setSelection( new StructuredSelection( new Object[] { launchConfig1, launchConfig2 } ) );

    assertThat( action.isEnabled() ).isFalse();
  }

  @Test
  public void testSetSelectionWithNonLaunchConfig() {
    action.setSelection( new StructuredSelection( new Object() ) );

    assertThat( action.isEnabled() ).isFalse();
  }

  @Test
  public void testRun() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig();
    action.setSelection( new StructuredSelection( launchConfig ) );
    doReturn( true ).when( action ).editLaunchConfig( launchConfig );

    action.run();

    verify( action ).editLaunchConfig( launchConfig );
    verify( dialog ).close( Window.CANCEL );
  }

  @Test
  public void testRunWhenDialogIsCancelled() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig();
    action.setSelection( new StructuredSelection( launchConfig ) );
    doReturn( false ).when( action ).editLaunchConfig( launchConfig );

    action.run();

    verify( action ).editLaunchConfig( launchConfig );
    verify( dialog ).refresh();
  }

  @Test
  public void testRunWhileDisabled() {
    action.setSelection( new StructuredSelection( new Object() ) );

    action.run();

    verify( action, never() ).editLaunchConfig( any( ILaunchConfiguration.class ) );
  }
}
