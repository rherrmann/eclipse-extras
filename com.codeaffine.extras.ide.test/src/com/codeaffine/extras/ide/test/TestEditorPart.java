package com.codeaffine.extras.ide.test;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


public class TestEditorPart extends EditorPart {

  public static final String ID = "com.codeaffine.extras.platform.test.internal.TestEditorPart";

  @Override
  public void createPartControl( Composite parent ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setFocus() {
    // TODO Auto-generated method stub

  }

  @Override
  public void doSave( IProgressMonitor monitor ) {
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public void init( IEditorSite site, IEditorInput input ) throws PartInitException {
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

}
