package com.codeaffine.extras.archive.internal.editor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveEntryEditorInput extends PlatformObject implements IStorageEditorInput {
  private ArchiveEntryStorage storage;

  public ArchiveEntryEditorInput(FileEntry fileEntry, IContentType contentType) {
    this(new ArchiveEntryStorage(fileEntry, contentType));
  }

  ArchiveEntryEditorInput(ArchiveEntryStorage storage) {
    this.storage = storage;
  }

  @Override
  public IStorage getStorage() {
    return storage;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
    return editorRegistry.getImageDescriptor(storage.getName(), storage.getContentType());
  }

  @Override
  public String getName() {
    return storage.getName();
  }

  @Override
  public IPersistableElement getPersistable() {
    return new PersistableEditorInputFactory(this);
  }

  @Override
  public String getToolTipText() {
    return storage.getArchiveName() + "::" + storage.getName();
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public boolean equals(Object object) {
    boolean result = false;
    if (object instanceof ArchiveEntryEditorInput) {
      ArchiveEntryEditorInput archiveEntryEditorInput = (ArchiveEntryEditorInput) object;
      result = getStorage().equals(archiveEntryEditorInput.getStorage());
    }
    return result;
  }

  @Override
  public int hashCode() {
    return getStorage().hashCode();
  }
}
