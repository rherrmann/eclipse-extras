package com.codeaffine.extras.archive.internal.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class PersistableEditorInputFactory implements IElementFactory, IPersistableElement {

  private static final String ELEMENT_ID = "elementId";
  private static final String FACTORY_ID = "com.codeaffine.extras.archive.internal.editor.PersistableEditorInputFactory";

  private static final Map<Integer, IEditorInput> persistedEditorInputs = new HashMap<>();

  private IEditorInput editorInput;

  public static void clearPersistedEdiorInputs() {
    persistedEditorInputs.clear();
  }

  public PersistableEditorInputFactory() {
    // default constructor is used by extension registry
  }

  public PersistableEditorInputFactory(IEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  @Override
  public void saveState(IMemento memento) {
    int identifier = editorInput.hashCode();
    persistedEditorInputs.put(Integer.valueOf(identifier), editorInput);
    memento.putInteger(ELEMENT_ID, identifier);
  }

  @Override
  public String getFactoryId() {
    return FACTORY_ID;
  }

  @Override
  public IAdaptable createElement(IMemento memento) {
    return persistedEditorInputs.remove(memento.getInteger(ELEMENT_ID));
  }
}
