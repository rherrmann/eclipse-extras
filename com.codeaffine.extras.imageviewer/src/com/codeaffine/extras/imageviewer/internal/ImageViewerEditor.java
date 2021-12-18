package com.codeaffine.extras.imageviewer.internal;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;


public class ImageViewerEditor extends EditorPart implements IReusableEditor {

  public static final String ID = "com.codeaffine.extras.imageviewer.internal.ImageViewerEditor";

  ImageViewer imageCanvas;

  @Override
  public void init(IEditorSite site, IEditorInput input) {
    setSite(site);
    setInput(input);
  }

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(FillLayoutFactory.newFillLayout(0));
    imageCanvas = new ImageViewer(parent);
    addPropertyListener((source, propertyId) -> handlePropertyChangedEvent(propertyId));
    updateContent();
  }

  @Override
  public void setFocus() {
    imageCanvas.getControl().forceFocus();
  }

  @Override
  public void setInput(IEditorInput editorInput) {
    checkEditorInput(editorInput);
    setInputWithNotify(editorInput);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void doSaveAs() {
    IPath filePath = querySaveAsFilePath();
    if (filePath != null) {
      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
      try {
        new ImageDataStorage(imageCanvas.getImageDatas()).save(file, getProgressMonitor());
        setInput(new FileEditorInput(file));
      } catch (CoreException exception) {
        StatusManager.getManager().handle(exception, ImageViewerPlugin.ID);
        StatusManager.getManager().handle(exception.getStatus(), StatusManager.SHOW);
      }
    }
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return imageCanvas != null && imageCanvas.getImageDatas() != null;
  }

  private IPath querySaveAsFilePath() {
    SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());
    IEditorInput editorInput = getEditorInput();
    IFile originalFile = ResourceUtil.getFile(editorInput);
    if (originalFile != null) {
      dialog.setOriginalFile(originalFile);
    } else {
      dialog.setOriginalName(editorInput.getName());
    }
    int dialogResult = dialog.open();
    return dialogResult == Window.OK ? dialog.getResult() : null;
  }

  private IProgressMonitor getProgressMonitor() {
    IProgressMonitor monitor = null;
    IStatusLineManager manager = getEditorSite().getActionBars().getStatusLineManager();
    if (manager != null) {
      monitor = manager.getProgressMonitor();
    }
    return monitor != null ? monitor : new NullProgressMonitor();
  }

  private void handlePropertyChangedEvent(int propertyId) {
    if (propertyId == IWorkbenchPartConstants.PROP_INPUT) {
      updateContent();
    }
  }

  private void updateContent() {
    setPartName(getEditorInput().getName());
    try (InputStream inputStream = openEditorInput()) {
      ImageData[] imageDatas = new ImageLoader().load(inputStream);
      imageCanvas.setImageDatas(imageDatas);
    } catch (IOException ignoreCloseProblem) {
    }
  }

  private InputStream openEditorInput() {
    InputStream result = new ByteArrayInputStream(new byte[0]);
    if (getEditorInput() instanceof IStorageEditorInput) {
      IStorageEditorInput storageEditorInput = (IStorageEditorInput) getEditorInput();
      try {
        result = storageEditorInput.getStorage().getContents();
      } catch (CoreException ignore) {
      }
    } else if (getEditorInput() instanceof IPathEditorInput) {
      IPathEditorInput pathEditorInput = (IPathEditorInput) getEditorInput();
      try {
        result = new FileInputStream(pathEditorInput.getPath().toFile());
      } catch (FileNotFoundException ignore) {
      }
    } else if (getEditorInput() instanceof IURIEditorInput) {
      IURIEditorInput uriEditorInput = (IURIEditorInput) getEditorInput();
      try {
        result = uriEditorInput.getURI().toURL().openStream();
      } catch (IOException ignore) {
      }
    }
    return result;
  }

  private static void checkEditorInput(IEditorInput input) {
    if (!(input instanceof IStorageEditorInput) && !(input instanceof IPathEditorInput)
        && !(input instanceof IURIEditorInput)) {
      throw new IllegalArgumentException("Invalid input: " + input);
    }
  }
}
