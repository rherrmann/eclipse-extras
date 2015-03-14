package com.codeaffine.extras.ide.internal.workingset;

import static com.codeaffine.extras.ide.internal.Images.WORKING_SET_WIZBAN;
import static org.eclipse.jface.dialogs.Dialog.applyDialogFont;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;
import static org.eclipse.jface.viewers.StructuredSelection.EMPTY;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.FindReplaceDocumentAdapterContentProposalProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

import com.codeaffine.extras.ide.internal.Images;
import com.codeaffine.extras.ide.internal.workingset.ValidationStatus.Severity;


public class DynamicWorkingSetPage extends WizardPage implements  IWorkingSetPage {

  static final String TITLE = "New Dynamic Project Working Set";

  private final ProjectsProvider projectsProvider;
  private final Validator validator;
  private boolean firstTimeShown;
  private IWorkingSet workingSet;
  Composite composite;
  Text nameText;
  Text patternText;
  Label previewLabel;
  TableViewer previewViewer;
  PreviewLabelProvider previewLabelProvider;

  public DynamicWorkingSetPage() {
    this( new WorkspaceProjectsProvider(), new JdtFeature() );
  }

  public DynamicWorkingSetPage( ProjectsProvider projectsProvider, JdtFeature jdtFeature ) {
    super( "DynamicWorkingSetWizardPage" );
    this.projectsProvider = projectsProvider;
    this.validator = new Validator( projectsProvider, jdtFeature );
    this.firstTimeShown = true;
    setTitle( TITLE );
    setDescription( "Enter a pattern to include matching projects in this working set" );
    setImageDescriptor( Images.getDescriptor( WORKING_SET_WIZBAN ) );
  }

  @Override
  public void createControl( Composite parent ) {
    createControls( parent );
    defineTabOrder();
    attachListeners();
    layoutControls();
    setControl( composite );
    applyDialogFont( composite );
    applySelection();
  }

  @Override
  public void setVisible( boolean visible ) {
    super.setVisible( visible );
    if( visible ) {
      if( firstTimeShown ) {
        firstTimeShown = false;
        nameText.setFocus();
      }
      updateStatusMessage( new ValidationStatus( Severity.NONE, "" ) );
      setPageComplete( validate() == null );
    }
  }

  @Override
  public void finish() {
    workingSet.setName( getPatternText() );
    workingSet.setLabel( getNameText() );
  }

  @Override
  public IWorkingSet getSelection() {
    return workingSet;
  }

  @Override
  public void setSelection( IWorkingSet workingSet ) {
    this.workingSet = workingSet;
  }

  private void createControls( Composite parent ) {
    composite = new Composite( parent, SWT.NONE );
    Label nameLabel = new Label( composite, SWT.NONE );
    nameLabel.setText( "&Name" );
    nameText = new Text( composite, SWT.BORDER );
    Label patternLabel = new Label( composite, SWT.NONE );
    patternLabel.setText( "&Pattern" );
    patternText = new Text( composite, SWT.BORDER );
    installPatternContentAssist();
    previewLabel = new Label( composite, SWT.NONE );
    previewLabel.setText( "Preview" );
    previewViewer = new TableViewer( composite, SWT.SINGLE | SWT.FULL_SELECTION );
    previewViewer.getControl().setBackground( getWidgetBackgroundColor() );
    previewViewer.getControl().addListener( SWT.FocusOut, new PreviewFocusOutListener() );
    previewLabelProvider = new PreviewLabelProvider( getShell().getDisplay() );
    previewViewer.setLabelProvider( previewLabelProvider );
    previewViewer.setContentProvider( ArrayContentProvider.getInstance() );
    previewViewer.setInput( projectsProvider.getProjects() );
    previewViewer.setComparator( new PreviewComparator( "" ) );
  }

  private void layoutControls() {
    composite.setLayout( GridLayoutFactory.swtDefaults().numColumns( 2 ).spacing( 10, 5 ).create() );
    swtDefaults().align( SWT.FILL, SWT.TOP ).grab( true, false ).applyTo( composite );
    swtDefaults().align( SWT.FILL, SWT.CENTER ).grab( true, false ).applyTo( nameText );
    swtDefaults().align( SWT.FILL, SWT.CENTER ).grab( true, false ).applyTo( patternText );
    swtDefaults().align( SWT.FILL, SWT.TOP ).grab( false, false ).indent( 0, 10 ).applyTo( previewLabel );
    swtDefaults().align( SWT.FILL, SWT.FILL ).grab( true, true ).indent( 0, 10 ).applyTo( previewViewer.getControl() );
  }

  private void installPatternContentAssist() {
    ContentProposalAdapter contentAssist = new ContentAssistCommandAdapter(
      patternText,
      new TextContentAdapter(),
      new FindReplaceDocumentAdapterContentProposalProvider( true ),
      CONTENT_ASSIST_PROPOSALS,
      new char[]{ '\\', '[', '(' },
      true );
    contentAssist.setEnabled( true );
  }

  private void defineTabOrder() {
    composite.setTabList( new Control[] { nameText, patternText } );
  }

  private void attachListeners() {
    nameText.addListener( SWT.Modify, new NameModifyListener() );
    patternText.addListener( SWT.Modify, new PatternModifyListener() );
  }

  private void applySelection() {
    if( workingSet != null ) {
      nameText.setText( workingSet.getLabel() );
      patternText.setText( workingSet.getName() );
    }
  }

  private ValidationStatus validate() {
    return validator.validate( workingSet, getNameText(), getPatternText() );
  }

  @SuppressWarnings("incomplete-switch")
  private void updateStatusMessage( ValidationStatus validationStatus ) {
    int messageType = IMessageProvider.NONE;
    String message = null;
    switch( validationStatus.getSeverity() ) {
      case ERROR:
        messageType = IMessageProvider.ERROR;
        message = validationStatus.getMessage();
      break;
      case WARNING:
        messageType = IMessageProvider.WARNING;
        message = validationStatus.getMessage();
      break;
    }
    setMessage( message, messageType );
    setPageComplete( validationStatus.getSeverity() != Severity.ERROR );
  }

  private void updatePreview() {
    String pattern = getPatternText();
    previewLabelProvider.setPattern( pattern );
    previewViewer.setComparator( new PreviewComparator( pattern ) );
  }

  private String getPatternText() {
    return patternText.getText().trim();
  }

  private String getNameText() {
    return nameText.getText().trim();
  }

  private Color getWidgetBackgroundColor() {
    return getShell().getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );
  }

  private class NameModifyListener implements Listener {
    @Override
    public void handleEvent( Event event ) {
      updateStatusMessage( validate() );
    }
  }

  private class PatternModifyListener implements Listener {
    @Override
    public void handleEvent( Event event ) {
      updateStatusMessage( validate() );
      updatePreview();
    }
  }

  private class PreviewFocusOutListener implements Listener {
    @Override
    public void handleEvent( Event event ) {
      previewViewer.setSelection( EMPTY );
    }
  }
}
