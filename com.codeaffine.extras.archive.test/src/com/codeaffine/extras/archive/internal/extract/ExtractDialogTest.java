package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.codeaffine.extras.test.util.DisplayHelper;

public class ExtractDialogTest {

  @Rule
  public DisplayHelper displayHelper = new DisplayHelper();

  private TestExtractDialog dialog;

  @Before
  public void setUp() {
    dialog = new TestExtractDialog(displayHelper.createShell());
    dialog.setBlockOnOpen(false);
  }

  @After
  public void tearDown() {
    dialog.close();
  }

  @Test
  public void testGetLocationAfterDialogWasClosed() throws Exception {
    dialog.open();
    dialog.close();

    ExtractLocation location = dialog.getLocation();

    assertNotNull(location);
    assertTrue(location.getPath().isEmpty());
  }

  @Test
  public void testOKButtonIsInitiallyDisabled() throws Exception {
    dialog.open();
    Button okButton = dialog.getButton(IDialogConstants.OK_ID);

    assertFalse(okButton.getEnabled());
  }

  private static class TestExtractDialog extends ExtractDialog {
    TestExtractDialog(Shell parent) {
      super(parent);
    }

    @Override
    public Button getButton(int id) {
      return super.getButton(id);
    }
  }

}
