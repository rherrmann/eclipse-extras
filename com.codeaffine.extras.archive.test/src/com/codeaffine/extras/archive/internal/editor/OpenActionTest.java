package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class OpenActionTest {

  private OpenAction openAction;

  @Before
  public void setUp() {
    openAction = new OpenAction(mock(IWorkbenchPage.class), "");
  }

  @Test
  public void testEnablementWithEmptySelection() throws Exception {
    IStructuredSelection selection = new StructuredSelection();

    boolean enabled = openAction.updateSelection(selection);

    assertFalse(enabled);
  }

  @Test
  public void testEnablementWithFileEntrySelection() throws Exception {
    IStructuredSelection selection = new StructuredSelection(mock(FileEntry.class));

    boolean enabled = openAction.updateSelection(selection);

    assertTrue(enabled);
  }

  @Test
  public void testEnablementWithFileEntryAndObjectSelection() throws Exception {
    Object[] elements = new Object[] {mock(FileEntry.class), new Object()};
    IStructuredSelection selection = new StructuredSelection(elements);

    boolean enabled = openAction.updateSelection(selection);

    assertTrue(enabled);
  }

  @Test
  public void testEnablementWithObjectAndFileEntrySelection() throws Exception {
    Object[] elements = new Object[] {new Object(), mock(FileEntry.class)};
    IStructuredSelection selection = new StructuredSelection(elements);

    boolean enabled = openAction.updateSelection(selection);

    assertTrue(enabled);
  }

  @Test
  public void testEnablementWithDirectoryEntrySelection() throws Exception {
    IStructuredSelection selection = new StructuredSelection(mock(DirectoryEntry.class));

    boolean enabled = openAction.updateSelection(selection);

    assertFalse(enabled);
  }

  @Test
  public void testEnablementWithObjectSelection() throws Exception {
    IStructuredSelection selection = new StructuredSelection(new Object());

    boolean enabled = openAction.updateSelection(selection);

    assertFalse(enabled);
  }

}
