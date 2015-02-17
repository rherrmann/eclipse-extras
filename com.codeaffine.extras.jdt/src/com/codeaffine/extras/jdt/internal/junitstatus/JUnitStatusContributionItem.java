package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;


public class JUnitStatusContributionItem extends WorkbenchWindowControlContribution {

  private static final String JUNIT_VIEW_ID = "org.eclipse.jdt.junit.ResultView";

  private ResourceManager resourceManager;
  private JUnitTestRunListener testRunListener;
  private ToolItem junitViewButton;
  private JUnitProgressBar progressBar;
  private Label spacingLabel;

  @Override
  protected Control createControl( Composite parent ) {
    resourceManager = new LocalResourceManager( JFaceResources.getResources( parent.getDisplay() ) );
    Composite result = createControls( parent );
    layoutControls( result );
    attachListeners();
    JUnitCore.addTestRunListener( testRunListener );
    return result;
  }

  @Override
  public void dispose() {
    detachTestRunListener();
    disposeResourceManager();
    super.dispose();
  }

  private Composite createControls( Composite parent ) {
    Composite result = new StatusComposite( parent, SWT.NONE );
    ToolBar toolBar = new ToolBar( result, SWT.NONE );
    junitViewButton = new ToolItem( toolBar, SWT.PUSH );
    junitViewButton.setToolTipText( "Show JUnit View" );
    junitViewButton.setImage( getJUnitImage() );
    progressBar = new JUnitProgressBar( result );
    spacingLabel = new Label( result, SWT.NONE );
    testRunListener = new JUnitTestRunListener( resourceManager, new JUnitProgressUI( progressBar ) );
    return result;
  }

  private void layoutControls( Composite composite ) {
    composite.setLayout( GridLayoutFactory.fillDefaults().numColumns( 3 ).create() );
    junitViewButton.getParent().setLayoutData( new GridData( SWT.BEGINNING, SWT.FILL, false, false ) );
    progressBar.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    spacingLabel.setLayoutData( new GridData() );
  }

  private void attachListeners() {
    junitViewButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showJUnitView();
      }
    } );
  }

  private Image getJUnitImage() {
    Image result = null;
    IViewRegistry viewRegistry = getWorkbenchWindow().getWorkbench().getViewRegistry();
    IViewDescriptor viewDescriptor = viewRegistry.find( JUNIT_VIEW_ID );
    if( viewDescriptor != null ) {
      result = resourceManager.createImage( viewDescriptor.getImageDescriptor() );
    }
    return result;
  }

  private void showJUnitView() {
    try {
      getWorkbenchWindow().getActivePage().showView( JUNIT_VIEW_ID );
    } catch( PartInitException ignore ) {
    }
  }

  private void detachTestRunListener() {
    if( testRunListener != null ) {
      JUnitCore.removeTestRunListener( testRunListener );
      testRunListener.dispose();
      testRunListener = null;
    }
  }

  private void disposeResourceManager() {
    if( resourceManager != null ) {
      resourceManager.dispose();
      resourceManager = null;
    }
  }
}
