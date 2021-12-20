package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

public class ExtractEntryActionTest {

  private ExtractEntryAction extractEntryAction;

  @Test
  public void testEnablementWithEmptySelection() throws Exception {
    boolean enabled = extractEntryAction.updateSelection(new StructuredSelection());

    assertFalse(enabled);
  }

  @Test
  public void testEnablementWithObjectSelection() throws Exception {
    IStructuredSelection selection = new StructuredSelection(new Object());
    boolean enabled = extractEntryAction.updateSelection(selection);

    assertTrue(enabled);
  }

  @Before
  public void setUp() {
    extractEntryAction = new ExtractEntryAction(mock(Shell.class), "");
  }
}
