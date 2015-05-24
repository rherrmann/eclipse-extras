package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.eclipse.swt.test.util.SWTIgnoreConditions.NonWindowsPlatform;
import com.codeaffine.extras.workingset.internal.DynamicWorkingSetPage;
import com.codeaffine.extras.workingset.internal.JdtFeature;
import com.codeaffine.test.util.junit.ConditionalIgnoreRule;
import com.codeaffine.test.util.junit.ConditionalIgnoreRule.ConditionalIgnore;

public class DynamicWorkingSetPagePDETest {

  @Rule
  public final DisplayHelper displayHelper  = new DisplayHelper();
  @Rule
  public final ConditionalIgnoreRule ignoreRule = new ConditionalIgnoreRule();

  private TestProjectsProvider projectsProvider;
  private TestableDynamicWorkingSetPage page;

  @Before
  public void setUp() {
    displayHelper.ensureDisplay();
    projectsProvider = new TestProjectsProvider();
    page = new TestableDynamicWorkingSetPage( projectsProvider, mock( JdtFeature.class ) );
  }

  @After
  public void tearDown() {
    page.dispose();
  }

  @Test
  public void testGetTitle() {
    assertThat( page.getTitle() ).isNotEmpty();
  }

  @Test
  public void testGetDescription() {
    assertThat( page.getDescription() ).isNotEmpty();
  }

  @Test
  public void testSetSelection() {
    IWorkingSet workingSet = createWorkingSet();

    page.setSelection( workingSet );

    assertThat( page.getSelection() ).isEqualTo( workingSet );
  }


  @ConditionalIgnore(condition=NonWindowsPlatform.class)
  @Test
  public void testSetVisibleFirstTime() {
    openPageInWizardDialog();

    page.setVisible( true );

    assertThat( page.nameText.isFocusControl() ).isTrue();
    assertThat( page.getMessage() ).isNull();
    assertThat( page.getMessageType() ).isEqualTo( IMessageProvider.NONE );
    assertThat( page.isPageComplete() ).isFalse();
  }

  @ConditionalIgnore(condition=NonWindowsPlatform.class)
  @Test
  public void testSetVisibleSecondTime() {
    openPageInWizardDialog();

    page.patternText.setFocus();
    page.setVisible( true );

    assertThat( page.patternText.isFocusControl() ).isTrue();
  }

  @Test
  public void testCreateControl() {
    IWorkingSet workingSet = createWorkingSet( "name", "pattern" );
    projectsProvider.addProject( createProject( "project-name" ) );
    page.setSelection( workingSet );
    page.createControl( displayHelper.createShell() );

    assertThat( page.nameText.getText() ).isEqualTo( workingSet.getLabel() );
    assertThat( page.patternText.getText() ).isEqualTo( workingSet.getName() );
    assertThat( page.previewViewer.getTable().getItemCount() ).isEqualTo( 1 );
  }

  @Test
  public void testCreateControlDoesNotUpdateMessage() {
    IWorkingSet workingSet = createWorkingSet( "name", "pattern" );
    page.setSelection( workingSet );

    page.createControl( displayHelper.createShell() );

    assertThat( page.getMessage() ).isNull();
    assertThat( page.getErrorMessage() ).isNull();
  }

  @Test
  public void testFinish() {
    IWorkingSet workingSet = createWorkingSet();
    page.setSelection( workingSet );
    page.createControl( displayHelper.createShell() );
    page.nameText.setText( "name" );
    page.patternText.setText( "pattern" );

    page.finish();

    verify( workingSet ).setLabel( "name" );
    verify( workingSet ).setName( "pattern" );
  }

  @Test
  public void testEnterName() {
    page.setSelection( createWorkingSet() );
    page.createControl( displayHelper.createShell() );
    page.setVisible( true );

    page.nameText.setText( "name" );

    assertThat( page.getMessage() ).isNotEmpty();
    assertThat( page.getMessageType() ).isEqualTo( IMessageProvider.ERROR );
    assertThat( page.isPageComplete() ).isFalse();
  }

  @Test
  public void testEnterPattern() {
    page.setSelection( createWorkingSet() );
    page.createControl( displayHelper.createShell() );
    page.setVisible( true );

    page.patternText.setText( "pattern" );

    assertThat( page.getMessage() ).isNotEmpty();
    assertThat( page.getMessageType() ).isEqualTo( IMessageProvider.ERROR );
    assertThat( page.isPageComplete() ).isFalse();
  }

  @Test
  public void testChangeExistingName() {
    IWorkingSet workingSet = createWorkingSet( "name", "pattern" );
    projectsProvider.addProject( createProject( "project-name" ) );
    page.setSelection( workingSet );
    page.createControl( displayHelper.createShell() );
    page.setVisible( true );

    page.nameText.setText( "other name" );

    assertThat( page.isPageComplete() ).isTrue();
  }

  private static IWorkingSet createWorkingSet() {
    IWorkingSet result = mock( IWorkingSet.class );
    when( result.getLabel() ).thenReturn( "" );
    when( result.getName() ).thenReturn( "" );
    return result;
  }

  private static IWorkingSet createWorkingSet( String name, String pattern ) {
    IWorkingSet result = createWorkingSet();
    when( result.getLabel() ).thenReturn( name );
    when( result.getName() ).thenReturn( pattern );
    return result;
  }

  private static IProject createProject( String name ) {
    IProject result = mock( IProject.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }

  private void openPageInWizardDialog() {
    Wizard wizard = new Wizard() {
      @Override
      public boolean performFinish() {
        return true;
      }
    };
    WizardDialog wizardDialog = new WizardDialog( displayHelper.createShell(), wizard );
    wizard.setContainer( wizardDialog );
    wizard.addPage( page );
    page.setWizard( wizard );
    wizardDialog.setBlockOnOpen( false );
    wizardDialog.open();
    wizardDialog.showPage( page );
  }

  private static class TestableDynamicWorkingSetPage extends DynamicWorkingSetPage {
    TestableDynamicWorkingSetPage( TestProjectsProvider projectsProvider, JdtFeature jdtFeature ) {
      super( projectsProvider, jdtFeature );
    }

    @Override
    public Shell getShell() {
      Shell result = super.getShell();
      if( result == null ) {
        result = composite.getShell();
      }
      return result;
    }
  }

}
