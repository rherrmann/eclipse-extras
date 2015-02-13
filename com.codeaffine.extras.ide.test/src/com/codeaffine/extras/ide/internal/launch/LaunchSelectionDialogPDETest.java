package com.codeaffine.extras.ide.internal.launch;

import static com.codeaffine.extras.ide.test.LaunchManagerHelper.createLaunchConfig;
import static com.codeaffine.extras.ide.test.LaunchManagerHelper.getDebugModeLabel;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class LaunchSelectionDialogPDETest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private TestableLaunchSelectionDialog dialog;

  @Test
  public void testValidateItem() {
    IStatus status = dialog.validateItem( null );

    assertThat( status.isOK() ).isTrue();
  }

  @Test
  public void testGetLaunchModeId() {
    String launchModeId = dialog.getLaunchModeId();

    assertThat( launchModeId ).isEqualTo( ILaunchManager.DEBUG_MODE );
  }

  @Test
  public void testOkButtonLabel() {
    dialog.create();

    Button button = dialog.getButton( IDialogConstants.OK_ID );

    assertThat( button.getText() ).isEqualTo( getDebugModeLabel() );
  }

  @Test
  public void testCreateFilter() {
    Object filter = dialog.createFilter();

    assertThat( filter ).isNotNull();
  }

  @Test
  public void testGetItemsComparator() {
    Comparator<?> comparator = dialog.getItemsComparator();

    assertThat( comparator ).isInstanceOf( LaunchConfigComparator.class );
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

  @Before
  public void setUp() {
    dialog = new TestableLaunchSelectionDialog( displayHelper.createShell() );
    dialog.setBlockOnOpen( false );;
  }

  public static class TestableLaunchSelectionDialog extends LaunchSelectionDialog {
    public TestableLaunchSelectionDialog( Shell shell ) {
      super( shell );
    }

    @Override
    protected String getInitialPattern() {
      return super.getInitialPattern();
    }

    @Override
    protected Button getButton( int id ) {
      return super.getButton( id );
    }
  }
}
