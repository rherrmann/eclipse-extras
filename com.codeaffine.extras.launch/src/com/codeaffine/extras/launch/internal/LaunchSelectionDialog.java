package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static org.eclipse.debug.ui.DebugUITools.openLaunchConfigurationDialog;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.codeaffine.extras.launch.internal.LaunchConfigLabelProvider.LabelMode;


public class LaunchSelectionDialog extends FilteredItemsSelectionDialog {

  static final int EDIT_ID = IDialogConstants.CLIENT_ID + 2;

  private final ILaunchManager launchManager;

  public LaunchSelectionDialog( Shell shell ) {
    super( shell, true );
    this.launchManager = DebugPlugin.getDefault().getLaunchManager();
    setTitle( "Start Launch Configuration" );
    setMessage( "Enter a &name pattern (? = any character, * = any string, CamelCase)" );
    setListLabelProvider( createLaunchConfigLabelProvider( shell, LabelMode.LIST ) );
    setDetailsLabelProvider( createLaunchConfigLabelProvider( shell, LabelMode.DETAIL ) );
    setSelectionHistory( new LaunchConfigSelectionHistory() );
  }

  public ILaunchConfiguration[] getSelectedLaunchConfigurations() {
    Object[] selectedElements = getResult();
    ILaunchConfiguration[] result = new ILaunchConfiguration[ selectedElements.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = ( ILaunchConfiguration )selectedElements[ i ];
    }
    return result;
  }

  public String getLaunchModeId() {
    return new LaunchModeSetting( launchManager, getDialogSettings() ).getLaunchModeId();
  }

  public ILaunchMode getLaunchMode() {
    return new LaunchModeSetting( launchManager, getDialogSettings() ).getLaunchMode();
  }

  @Override
  public String getElementName( Object item ) {
    ILaunchConfiguration configuration = ( ILaunchConfiguration )item;
    return configuration.getName();
  }

  @Override
  protected void fillViewMenu( IMenuManager menuManager ) {
    super.fillViewMenu( menuManager );
    LaunchModeSetting launchModeSetting = new LaunchModeSetting( launchManager, getDialogSettings() );
    LaunchModeDropDownAction action = new LaunchModeDropDownAction( launchModeSetting );
    action.addPropertyChangeListener( new IPropertyChangeListener() {
      @Override
      public void propertyChange( PropertyChangeEvent event ) {
        updateOkButtonLabel();
        updateStatus();
      }
    } );
    menuManager.add( action );
  }

  @Override
  protected void createButtonsForButtonBar( Composite parent ) {
    GridLayout parentLayout = ( GridLayout )parent.getLayout();
    parentLayout.makeColumnsEqualWidth = false;
    Button editButton = createButton( parent, EDIT_ID, "&Edit...", false );
    setButtonLayoutData( editButton );
    new Label( parent, SWT.NONE ).setLayoutData( new GridData( 5, 0 ) );
    parentLayout.numColumns++;
    createButton( parent, OK_ID, OK_LABEL, true );
    createButton( parent, CANCEL_ID, CANCEL_LABEL, false );
    updateOkButtonLabel();
    updateEditButtonEnablement( StructuredSelection.EMPTY );
  }

  @Override
  protected void buttonPressed( int buttonId ) {
    if( buttonId == EDIT_ID ) {
      editSelectedLaunchConfig();
    } else {
      super.buttonPressed( buttonId );
    }
  }

  @Override
  protected Control createExtendedContentArea( Composite parent ) {
    return null;
  }

  @Override
  protected IDialogSettings getDialogSettings() {
    IDialogSettings dialogSettings = LaunchExtrasPlugin.getInstance().getDialogSettings();
    return DialogSettings.getOrCreateSection( dialogSettings, "LaunchSelectionDialog" );
  }

  @Override
  protected void handleSelected( StructuredSelection selection ) {
    super.handleSelected( selection );
    updateEditButtonEnablement( selection );
  }

  @Override
  protected IStatus validateItem( Object item ) {
    ILaunchMode preferredLaunchMode = getLaunchMode();
    ILaunchConfiguration launchConfig = ( ILaunchConfiguration )item;
    return new LaunchConfigValidator( launchConfig, preferredLaunchMode ).validate();
  }

  @Override
  protected ItemsFilter createFilter() {
    return new LaunchConfigItemsFilter();
  }

  @Override
  protected Comparator<?> getItemsComparator() {
    return new LaunchConfigComparator();
  }

  @Override
  protected void fillContentProvider( AbstractContentProvider contentProvider,
                                      ItemsFilter itemsFilter,
                                      IProgressMonitor progressMonitor )
    throws CoreException
  {
    ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations();
    for( ILaunchConfiguration configuration : configurations ) {
      contentProvider.add( configuration, itemsFilter );
    }
  }

  private LaunchConfigLabelProvider createLaunchConfigLabelProvider( Shell shell,
                                                                     LabelMode labelMode )
  {
    return new LaunchConfigLabelProvider( shell.getDisplay(), this, labelMode );
  }

  private void editSelectedLaunchConfig() {
    StructuredSelection selection = getSelectedItems();
    ILaunchConfiguration launchConfig = ( ILaunchConfiguration )selection.getFirstElement();
    if( editLaunchConfig( launchConfig ) ) {
      setReturnCode( Window.CANCEL );
      close();
    } else {
      refresh();
    }
  }

  private boolean editLaunchConfig( ILaunchConfiguration launchConfig ) {
    Shell shell = getShell();
    String launchGroup = getLaunchGroupIdentifier();
    return openLaunchConfigurationDialog( shell, launchConfig, launchGroup, null ) == Window.OK;
  }

  private String getLaunchGroupIdentifier() {
    String result = null;
    String launchModeId = getLaunchModeId();
    ILaunchGroup[] launchGroups = DebugUITools.getLaunchGroups();
    for( int i = 0; result == null && i < launchGroups.length; i++ ) {
      ILaunchGroup launchGroup = launchGroups[ i ];
      if( launchGroup.getMode().equals( launchModeId ) && launchGroup.getCategory() == null ) {
        result = launchGroup.getIdentifier();
      }
    }
    return result;
  }

  private void updateEditButtonEnablement( StructuredSelection selection ) {
    Button editButton = getButton( EDIT_ID );
    if( editButton != null ) {
      boolean enabled = selection.size() == 1 && selection.getFirstElement() instanceof ILaunchConfiguration;
      editButton.setEnabled( enabled );
    }
  }

  private void updateOkButtonLabel() {
    Button okButton = getButton( OK_ID );
    LaunchModeSetting launchModeSetting = new LaunchModeSetting( launchManager, getDialogSettings() );
    ILaunchMode launchMode = launchModeSetting.getLaunchMode();
    if( okButton != null && launchMode != null ) {
      okButton.setText( launchMode.getLabel() );
      okButton.getParent().layout();
    }
  }

  void updateStatus() {
    StructuredSelection selection = getSelectedItems();
    IStatus totalStatus = okStatus();
    for( Object item : selection.toArray() ) {
      IStatus itemStatus = validateItem( item );
      if( !itemStatus.isOK() ) {
        totalStatus = itemStatus;
      }
    }
    updateStatus( totalStatus );
  }

  private static Status okStatus() {
    return new Status( IStatus.OK, PLUGIN_ID, "" );
  }

  public abstract static class AccessibleSelectionHistory extends SelectionHistory {
  }

  private class LaunchConfigItemsFilter extends ItemsFilter {
    LaunchConfigItemsFilter() {
      super( new LaunchConfigSearchPattern() );
    }

    @Override
    public boolean matchItem( Object item ) {
      boolean result = false;
      if( item instanceof ILaunchConfiguration ) {
        ILaunchConfiguration configuration = ( ILaunchConfiguration )item;
        result = matches( configuration.getName() );
      }
      return result;
    }

    @Override
    public boolean isConsistentItem( Object item ) {
      return true;
    }
  }

}
