package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.launch.internal.LaunchSelectionDialog.EDIT_BUTTON_ID;
import static com.codeaffine.extras.launch.test.LaunchManagerHelper.createLaunchConfig;
import static com.codeaffine.extras.launch.test.LaunchManagerHelper.getDebugModeLabel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.core.runtime.IStatus.INFO;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.launch.test.LaunchManagerHelper;
import com.codeaffine.extras.launch.test.LaunchModeHelper;

public class LaunchSelectionDialogPDETest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private TestableLaunchSelectionDialog dialog;

  @Test
  public void testValidateItemWithSupportedLaunchMode() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = LaunchManagerHelper.createLaunchConfig();

    IStatus status = dialog.validateItem( launchConfig );

    assertThat( status.isOK() ).isTrue();
  }

  @Test
  public void testValidateItemWithUnsupportedLaunchMode() throws CoreException {
    LaunchModeSetting launchModeSetting = getLaunchModeSettings();
    launchModeSetting.setLaunchModeId( LaunchModeHelper.TEST_LAUNCH_MODE );
    ILaunchConfigurationWorkingCopy launchConfig = LaunchManagerHelper.createLaunchConfig();

    IStatus status = dialog.validateItem( launchConfig );

    assertThat( status.getSeverity() ).isEqualTo( INFO );
    assertThat( status.getCode() ).isEqualTo( INFO );
    assertThat( status.getMessage() ).contains( "Debug" );
  }

  @Test
  public void testGetLaunchModeId() {
    String launchModeId = dialog.getLaunchModeId();

    assertThat( launchModeId ).isEqualTo( ILaunchManager.DEBUG_MODE );
  }

  @Test
  public void testOkButtonLabel() {
    dialog.create();

    Button button = dialog.getButton( OK_ID );

    assertThat( button.getText() ).isEqualTo( getDebugModeLabel() );
  }

  @Test
  public void testEditButtonLabel() {
    dialog.create();

    Button button = dialog.getButton( EDIT_BUTTON_ID );

    assertThat( button.getText() ).isNotEmpty();
  }

  @Test
  public void testInitialEditButtonEnablement() {
    dialog.open();

    assertThat( dialog.getButton( EDIT_BUTTON_ID ).getEnabled() ).isFalse();
  }

  @Test
  public void testCreateFilter() {
    Object filter = dialog.createFilter();

    assertThat( filter ).isNotNull();
  }

  @Test
  public void testGetItemsComparator() {
    Comparator<Object> comparator = dialog.getItemsComparator();

    int comparisonResult = comparator.compare( new Object(), new Object() );

    assertThat( comparisonResult ).isZero();
  }

  @Test
  public void testGetDialogSettings() {
    IDialogSettings dialogSettings = dialog.getDialogSettings();

    assertThat( dialogSettings ).isNotNull();
  }

  @Test
  public void testGetElementName() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = createLaunchConfig();

    String elementName = dialog.getElementName( launchConfig );

    assertThat( elementName ).isEqualTo( launchConfig.getName() );
  }

  @Test
  public void testGetInitialPattern() {
    String initialPattern = dialog.getInitialPattern();

    assertThat( initialPattern ).isNull();
  }

  @Test
  public void testTitle() {
    dialog.create();

    String text = dialog.getShell().getText();

    assertThat( text ).isNotEmpty();
  }

  @Test
  public void testUpdateStatus() {
    dialog.create();

    dialog.updateStatus();

    assertThat( dialog.lastStatus.isOK() ).isTrue();
    assertThat( dialog.lastStatus.getMessage() ).isEmpty();
  }

  @Test
  public void testFillContextMenu() {
    MenuManager menuManager = new MenuManager();

    dialog.fillContextMenu( menuManager );

    assertThat( menuManager.getSize() ).isEqualTo( 1 );
    assertThat( menuManager.getItems()[ 0 ].getId() ).isEqualTo( EditLaunchConfigAction.ID );
  }

  @Before
  public void setUp() {
    dialog = new TestableLaunchSelectionDialog( displayHelper.createShell() );
    dialog.setBlockOnOpen( false );;
  }

  private LaunchModeSetting getLaunchModeSettings() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    IDialogSettings dialogSettings = dialog.getDialogSettings();
    return new LaunchModeSetting( launchManager, dialogSettings );
  }

  public static class TestableLaunchSelectionDialog extends LaunchSelectionDialog {

    private final DialogSettings dialogSettings;
    private IStatus lastStatus;

    public TestableLaunchSelectionDialog( Shell shell ) {
      super( shell );
      dialogSettings = new DialogSettings( "TestableLaunchSelectionDialog" );
    }

    @Override
    protected void fillContextMenu( IMenuManager menuManager ) {
      super.fillContextMenu( menuManager );
    }

    @Override
    protected void updateStatus( IStatus status ) {
      lastStatus = status;
      super.updateStatus( status );
    }

    @Override
    protected String getInitialPattern() {
      return super.getInitialPattern();
    }

    @Override
    protected Button getButton( int id ) {
      return super.getButton( id );
    }

    @Override
    protected IDialogSettings getDialogSettings() {
      return dialogSettings;
    }
  }
}
