package com.codeaffine.extras.launch.internal.cleanup;

import java.util.stream.Stream;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.model.WorkbenchViewerComparator;


public class CleanupPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  public static final String ID = "com.codeaffine.extras.launch.internal.cleanup.CleanupPreferencePage";

  private final ILaunchManager launchManager;
  private final LaunchPreferences launchPreferences;
  Button cleanupButton;
  Label cleanupTypesLabel;
  CheckboxTableViewer cleanupTypesViewer;
  Button selectAllButton;
  Button deselectAllButton;
  private Label notelabel;

  public CleanupPreferencePage() {
    this( LaunchPreferences.getPluginPreferenceStore() );
  }

  public CleanupPreferencePage( IPreferenceStore preferenceStore ) {
    super( "Clean Up" );
    setPreferenceStore( preferenceStore );
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    launchPreferences = new LaunchPreferences( getPreferenceStore() );
  }

  @Override
  public void init( IWorkbench workbench ) {
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    createPageControls( composite );
    layoutPageControls( composite );
    initPageControls();
    return composite;
  }

  @Override
  protected void performDefaults() {
    cleanupButton.setSelection( false );
    cleanupTypesViewer.setAllChecked( false );
    super.performDefaults();
  }

  @Override
  public boolean performOk() {
    ILaunchConfigurationType[] types = getCheckedLaunchConfigTypes();
    String serializedTypeIds = new LaunchConfigTypeSerializer( launchManager ).serialize( types );
    launchPreferences.setCleanupGenerateLaunchConfigTypes( serializedTypeIds );
    return super.performOk();
  }

  private void createPageControls( Composite parent ) {
    cleanupButton = new Button( parent, SWT.CHECK );
    cleanupButton.setText( "Remove on-the-fly generated launch configurations when no longer needed" );
    cleanupButton.addListener( SWT.Selection, this::cleanupButtonSelected );
    cleanupTypesLabel = new Label( parent, SWT.NONE );
    cleanupTypesLabel.setText( "Select the launch configuration types to clean up" );
    cleanupTypesViewer = CheckboxTableViewer.newCheckList( parent, SWT.BORDER );
    cleanupTypesViewer.setLabelProvider( DebugUITools.newDebugModelPresentation() );
    cleanupTypesViewer.setContentProvider( ArrayContentProvider.getInstance() );
    cleanupTypesViewer.setComparator( new WorkbenchViewerComparator() );
    cleanupTypesViewer.addFilter( new LaunchConfigTypeFilter() );
    cleanupTypesViewer.setInput( launchManager.getLaunchConfigurationTypes() );
    selectAllButton = new Button( parent, SWT.PUSH );
    selectAllButton.addListener( SWT.Selection, event -> cleanupTypesViewer.setAllChecked( true ) );
    selectAllButton.setText( "&Select All" );
    deselectAllButton = new Button( parent, SWT.PUSH );
    deselectAllButton.setText( "&Deselect All" );
    deselectAllButton.addListener( SWT.Selection, event -> cleanupTypesViewer.setAllChecked( false ) );
    notelabel = new Label( parent, SWT.WRAP );
    String text
      = "Note: Launch configurations are considered as on-the-fly generated if "
      + "they were created outside the Run Configurations dialog without further "
      + "manual changes. For example with Run As > JUnit Test";
    notelabel.setText( text );
  }

  private void layoutPageControls( Composite parent ) {
    parent.setLayout( new GridLayout( 2, false ) );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .grab( true, false )
      .applyTo( parent );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .indent( 20, 0 )
      .span( 2, 1 )
      .applyTo( cleanupTypesLabel );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .indent( 20, 0 )
      .hint( SWT.DEFAULT, cleanupTypesViewer.getTable().getItemHeight() * 14 )
      .grab( true, false )
      .span( 1, 2 )
      .applyTo( cleanupTypesViewer.getControl() );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .hint( computePreferredButtonWidth( selectAllButton ), SWT.DEFAULT )
      .applyTo( selectAllButton );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .hint( computePreferredButtonWidth( deselectAllButton ), SWT.DEFAULT )
      .applyTo( deselectAllButton );
    GridDataFactory.swtDefaults()
      .align( SWT.FILL, SWT.TOP )
      .hint( getTextWidth( cleanupButton.getText() ), SWT.DEFAULT )
      .span( 2, 1 )
      .applyTo( notelabel );
  }

  private static int computePreferredButtonWidth( Button button ) {
    int defaultButtonWidth = getDefaultButtonWidth();
    Point minSize = button.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
    return Math.max( defaultButtonWidth, minSize.x );
  }

  private static int getDefaultButtonWidth() {
    return LayoutConstants.getMinButtonSize().x;
  }

  private void initPageControls() {
    cleanupButton.setSelection( launchPreferences.isCleanupGeneratedLaunchConfigs() );
    cleanupTypesViewer.setCheckedElements( getCleanupLaunchConfigTypes() );
    updateEnablement();
  }

  private void updateEnablement() {
    boolean enabled = launchPreferences.isCleanupGeneratedLaunchConfigs();
    cleanupTypesLabel.setEnabled( enabled );
    cleanupTypesViewer.getControl().setEnabled( launchPreferences.isCleanupGeneratedLaunchConfigs() );
    selectAllButton.setEnabled( launchPreferences.isCleanupGeneratedLaunchConfigs() );
    deselectAllButton.setEnabled( launchPreferences.isCleanupGeneratedLaunchConfigs() );
  }

  @SuppressWarnings("unused")
  private void cleanupButtonSelected( Event event ) {
    launchPreferences.setCleanupGeneratedLaunchConfigs( cleanupButton.getSelection() );
    updateEnablement();
  }

  private ILaunchConfigurationType[] getCheckedLaunchConfigTypes() {
    return Stream.of( cleanupTypesViewer.getCheckedElements() )
      .map( element -> ( ILaunchConfigurationType )element )
      .toArray( ILaunchConfigurationType[]::new );
  }

  private ILaunchConfigurationType[] getCleanupLaunchConfigTypes() {
    String typeIds = launchPreferences.getCleanupGenerateLaunchConfigTypes();
    return new LaunchConfigTypeSerializer( launchManager ).deserialize( typeIds );
  }

  private int getTextWidth( String text ) {
    GC gc = new GC( cleanupTypesViewer.getControl() );
    try {
      return gc.textExtent( text ).x;
    } finally {
      gc.dispose();
    }
  }

}
