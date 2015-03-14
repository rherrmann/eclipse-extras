package com.codeaffine.extras.ide.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.ide.internal.IDEExtrasPlugin;



public class DynamicWorkingSetWizardPDETest {

  private DynamicWorkingSetWizard wizard;

  @Before
  public void setUp() {
    wizard = new DynamicWorkingSetWizard();
  }

  @After
  public void tearDown() {
    wizard.dispose();
  }

  @Test
  public void testGetDefaultPageImage() {
    Image image = wizard.getDefaultPageImage();

    assertThat( image ).isNotNull();
  }

  @Test
  public void testGetWindowTitle() {
    String windowTitle = wizard.getWindowTitle();

    assertThat( windowTitle ).isNotEmpty();
  }

  @Test
  public void testGetDialogSettings() {
    IDialogSettings dialogSettings = wizard.getDialogSettings();

    assertThat( dialogSettings ).isEqualTo( IDEExtrasPlugin.getInstance().getDialogSettings() );
  }

  @Test
  public void testAddPages() {
    wizard.init( PlatformUI.getWorkbench(), null );

    wizard.addPages();

    assertThat( wizard.getPages() ).hasSize( 1 );
    assertThat( wizard.getPages()[ 0 ] ).isInstanceOf( DynamicWorkingSetPage.class );
    assertThat( wizard.getStartingPage() ).isEqualTo( wizard.getPages()[ 0 ] );
  }

  @Test
  public void testCreateWorkingSet() {
    wizard.init( PlatformUI.getWorkbench(), null );
    wizard.addPages();
    DynamicWorkingSetPage startingPage = ( DynamicWorkingSetPage )wizard.getStartingPage();

    IWorkingSet workingSet = startingPage.getSelection();

    assertThat( workingSet.getName() ).isEmpty();
    assertThat( workingSet.getElements() ).isEmpty();
    assertThat( workingSet.isSelfUpdating() ).isTrue();
    assertThat( workingSet.isAggregateWorkingSet() ).isFalse();
    assertThat( workingSet.getId() ).isEqualTo( DynamicWorkingSet.ID );
    assertThat( workingSet.getLabel() ).isEmpty();
    assertThat( workingSet.getImageDescriptor() ).isNotNull();
  }

}
