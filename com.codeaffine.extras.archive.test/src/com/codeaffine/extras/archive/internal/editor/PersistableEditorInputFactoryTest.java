package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistableEditorInputFactoryTest {

  private IEditorInput editorInput;
  private PersistableEditorInputFactory editorInputFactory;
  private IMemento memento;

  @Before
  public void setUp() {
    editorInput = mock(IEditorInput.class);
    editorInputFactory = new PersistableEditorInputFactory(editorInput);
    memento = XMLMemento.createWriteRoot("testNode");
  }

  @After
  public void tearDown() {
    PersistableEditorInputFactory.clearPersistedEdiorInputs();
  }

  @Test
  public void testExtensionPoint() throws Exception {
    String factoryId = editorInputFactory.getFactoryId();

    IWorkbench workbench = PlatformUI.getWorkbench();
    IElementFactory elementFactory = workbench.getElementFactory(factoryId);

    assertNotNull(elementFactory);
    assertEquals(PersistableEditorInputFactory.class, elementFactory.getClass());
  }

  @Test
  public void testGetFactoryId() throws Exception {
    String factoryId = editorInputFactory.getFactoryId();

    assertNotNull(factoryId);
  }

  @Test
  public void testSaveState() throws Exception {
    editorInputFactory.saveState(memento);

    IAdaptable createdElement = editorInputFactory.createElement(memento);

    assertEquals(editorInput, createdElement);
  }

  @Test
  public void testCreateElementRemovesSavedState() throws Exception {
    editorInputFactory.saveState(memento);

    editorInputFactory.createElement(memento);
    IAdaptable createdElement = editorInputFactory.createElement(memento);

    assertNull(createdElement);
  }

  @Test
  public void testClearPersisteedEditorInputs() throws Exception {
    editorInputFactory.saveState(memento);

    PersistableEditorInputFactory.clearPersistedEdiorInputs();
    IAdaptable createdElement = editorInputFactory.createElement(memento);

    assertNull(createdElement);
  }

}
