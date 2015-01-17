package com.codeaffine.extras.platform.internal.launch;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.codeaffine.extras.platform.internal.PlatformExtrasPlugin;
import com.codeaffine.extras.platform.internal.launch.LaunchConfigLabelProvider.LabelMode;


public class LaunchSelectionDialog extends FilteredItemsSelectionDialog {

  private final ILaunchManager launchManager;

  public LaunchSelectionDialog( Shell shell ) {
    super( shell, true );
    this.launchManager = DebugPlugin.getDefault().getLaunchManager();
    setTitle( "Start Launch Configuration" );
    setMessage( "&Select a configuration to launch (? = any character, * = any string)" );
    setListLabelProvider( createLaunchConfigLabelProvider( shell, LabelMode.LIST ) );
    setDetailsLabelProvider( createLaunchConfigLabelProvider( shell, LabelMode.DETAIL ) );
  }

  public String getLaunchModeId() {
    return new LaunchModeSetting( launchManager, getDialogSettings() ).getLaunchModeId();
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
      }
    } );
    menuManager.add( action );
  }

  @Override
  protected void createButtonsForButtonBar( Composite parent ) {
    super.createButtonsForButtonBar( parent );
    updateOkButtonLabel();
  }

  @Override
  protected Control createExtendedContentArea( Composite parent ) {
    return null;
  }

  @Override
  protected IDialogSettings getDialogSettings() {
    IDialogSettings dialogSettings = PlatformExtrasPlugin.getInstance().getDialogSettings();
    return DialogSettings.getOrCreateSection( dialogSettings, "LaunchSelectionDialog" );
  }

  @Override
  protected IStatus validateItem( Object item ) {
    return Status.OK_STATUS;
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

  private void updateOkButtonLabel() {
    Button okButton = getButton( IDialogConstants.OK_ID );
    LaunchModeSetting launchModeSetting = new LaunchModeSetting( launchManager, getDialogSettings() );
    ILaunchMode launchMode = launchModeSetting.getLaunchMode();
    if( okButton != null && launchMode != null ) {
      okButton.setText( launchMode.getLabel() );
      okButton.getParent().layout();
    }
  }

  private class LaunchConfigItemsFilter extends ItemsFilter {
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
