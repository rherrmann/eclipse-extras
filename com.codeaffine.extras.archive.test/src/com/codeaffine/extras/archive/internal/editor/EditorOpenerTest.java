package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.Archive;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class EditorOpenerTest {

  private IWorkbenchPage workbenchPage;

  @Test
  public void testOpenKnownContentType() throws Exception {
    FileEntry fileEntry = createTextFileEntry();

    EditorOpener editorOpener = new EditorOpener(workbenchPage, fileEntry);
    editorOpener.open();

    IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
    assertEquals(1, editorReferences.length);
    IEditorReference editorReference = editorReferences[0];
    assertEquals(ArchiveEntryEditorInput.class, editorReference.getEditorInput().getClass());
  }

  @Before
  public void setUp() {
    workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    closeEditors();
  }

  @After
  public void tearDown() {
    closeEditors();
  }

  private void closeEditors() {
    IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
    for (IEditorReference editorReference : editorReferences) {
      IEditorPart editor = editorReference.getEditor(false);
      if (editor != null) {
        editor.dispose();
      }
    }
  }

  private static FileEntry createTextFileEntry() throws IOException {
    Archive archive = new Archive("archive-name", new Path("/path/to/archive.zip"));
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn("document.txt");
    when(result.getArchive()).thenReturn(archive);
    when(result.open()).thenReturn(new ByteArrayInputStream(new byte[0]));
    return result;
  }
}
