package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.launch.internal.LaunchConfigLabelProvider.LabelMode.DETAIL;
import static com.codeaffine.extras.launch.internal.LaunchConfigLabelProvider.LabelMode.LIST;
import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static org.eclipse.jface.action.IAction.ENABLED;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

  static final int EDIT_BUTTON_ID = IDialogConstants.CLIENT_ID + 2;

  private final ILaunchManager launchManager;
  private final EditLaunchConfigAction editLaunchConfigAction;
  private final LaunchConfigSelectionHistory launchConfigHistory;

  public LaunchSelectionDialog( Shell shell ) {
    super( shell, true );
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    editLaunchConfigAction = new EditLaunchConfigAction( this );
    editLaunchConfigAction.setText( "&Edit..." );
    editLaunchConfigAction.addPropertyChangeListener( new EditLaunchConfigActionListener() );
    launchConfigHistory = new LaunchConfigSelectionHistory();
    setTitle( "Start Launch Configuration" );
    setMessage( "Enter a &name pattern (? = any character, * = any string, CamelCase)" );
    setListLabelProvider( createLaunchConfigLabelProvider( shell, LIST ) );
    setDetailsLabelProvider( createLaunchConfigLabelProvider( shell, DETAIL ) );
    setSelectionHistory( launchConfigHistory );
    setSeparatorLabel( "Launch Configuration matches" );
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

  public void close( int returnCode ) {
    setReturnCode( returnCode );
    close();
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
  protected void fillContextMenu( IMenuManager menuManager ) {
    menuManager.add( editLaunchConfigAction );
  }

  @Override
  protected void createButtonsForButtonBar( Composite parent ) {
    GridLayout parentLayout = ( GridLayout )parent.getLayout();
    parentLayout.makeColumnsEqualWidth = false;
    parentLayout.numColumns++;
    Button editButton = createButton( parent, EDIT_BUTTON_ID, "&Edit...", false );
    editButton.setEnabled( false );
    setButtonLayoutData( editButton );
    new Label( parent, SWT.NONE ).setLayoutData( new GridData( 5, 0 ) );
    createButton( parent, OK_ID, OK_LABEL, true );
    createButton( parent, CANCEL_ID, CANCEL_LABEL, false );
    updateOkButtonLabel();
  }

  @Override
  protected void buttonPressed( int buttonId ) {
    if( buttonId == EDIT_BUTTON_ID ) {
      editLaunchConfigAction.run();
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
    editLaunchConfigAction.setSelection( selection );
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
  protected Comparator<ILaunchConfiguration> getItemsComparator() {
    return new LaunchConfigComparator( launchConfigHistory );
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

  private class EditLaunchConfigActionListener implements IPropertyChangeListener {
    @Override
    public void propertyChange( PropertyChangeEvent event ) {
      if( ENABLED.equals( event.getProperty() ) ) {
        updateEditButtonEnablement( ( IAction )event.getSource() );
      }
    }

    private void updateEditButtonEnablement( IAction action ) {
      Button button = getButton( EDIT_BUTTON_ID );
      if( button != null && !button.isDisposed() ) {
        button.setEnabled( action.isEnabled() );
      }
    }
  }

}
