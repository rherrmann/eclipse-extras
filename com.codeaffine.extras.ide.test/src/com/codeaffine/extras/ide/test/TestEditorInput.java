package com.codeaffine.extras.ide.test;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class TestEditorInput implements IEditorInput {

  @SuppressWarnings("rawtypes")
  @Override
  public Object getAdapter( Class adapter ) {
    return null;
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  @Override
  public String getName() {
    return "Test Editor Input";
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "";
  }
}
