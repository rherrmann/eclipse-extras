package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.junit.Before;
import org.junit.Test;

public class ArchiveEntryEditorInputTest {

  private ArchiveEntryStorage storage;
  private ArchiveEntryEditorInput editorInput;

  @Test
  public void testGetSTorage() throws Exception {
    assertSame(storage, editorInput.getStorage());
  }

  @Test
  public void testGetName() throws Exception {
    String name = editorInput.getName();

    assertEquals(name, storage.getName());
  }

  @Test
  public void testGetImageDescriptor() throws Exception {
    ArchiveEntryStorage storage = mock(ArchiveEntryStorage.class);
    ArchiveEntryEditorInput editorInput = new ArchiveEntryEditorInput(storage);

    ImageDescriptor imageDescriptor = editorInput.getImageDescriptor();

    assertNotNull(imageDescriptor);
  }

  @Test
  public void testGetPersistable() throws Exception {
    IPersistableElement persistable = editorInput.getPersistable();

    assertTrue(persistable instanceof PersistableEditorInputFactory);
  }

  @Test
  public void testGetToolTipText() throws Exception {
    String toolTipText = editorInput.getToolTipText();

    assertNotNull(toolTipText);
  }

  @Test
  public void testEqualsWithSameStorage() throws Exception {
    ArchiveEntryEditorInput otherEditorInput = new ArchiveEntryEditorInput(storage);

    boolean equals = editorInput.equals(otherEditorInput);

    assertTrue(equals);
  }

  @Test
  public void testEqualsWithDifferentStorage() throws Exception {
    ArchiveEntryStorage otherStorage = mock(ArchiveEntryStorage.class);
    ArchiveEntryEditorInput otherEditorInput = new ArchiveEntryEditorInput(otherStorage);

    boolean equals = editorInput.equals(otherEditorInput);

    assertFalse(equals);
  }

  @Test
  public void testEqualsWithObject() throws Exception {
    Object object = new Object();

    boolean equals = editorInput.equals(object);

    assertFalse(equals);
  }

  @Test
  public void testHashCodeWithSameStorage() throws Exception {
    ArchiveEntryEditorInput otherEditorInput = new ArchiveEntryEditorInput(storage);

    int hashCode = editorInput.hashCode();
    int otherHashCode = otherEditorInput.hashCode();

    assertEquals(hashCode, otherHashCode);
  }

  @Test
  public void testHashCodeWithDifferentStorage() throws Exception {
    ArchiveEntryStorage otherStorage = mock(ArchiveEntryStorage.class);
    ArchiveEntryEditorInput otherEditorInput = new ArchiveEntryEditorInput(otherStorage);

    int hashCode = editorInput.hashCode();
    int otherHashCode = otherEditorInput.hashCode();

    assertTrue(hashCode != otherHashCode);
  }

  @Test
  public void testExists() throws Exception {
    assertFalse(editorInput.exists());
  }

  @Before
  public void setUp() {
    storage = mock(ArchiveEntryStorage.class);
    when(storage.getName()).thenReturn("name");
    editorInput = new ArchiveEntryEditorInput(storage);
  }

}
