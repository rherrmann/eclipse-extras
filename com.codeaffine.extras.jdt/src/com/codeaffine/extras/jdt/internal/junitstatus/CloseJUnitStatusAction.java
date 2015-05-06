package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;

import com.codeaffine.extras.jdt.internal.prefs.ExpressionEvaluator;
import com.codeaffine.extras.jdt.internal.prefs.WorkspaceScopePreferences;

public class CloseJUnitStatusAction extends Action {

  private final IWorkbench workbench;
  private final WorkspaceScopePreferences preferences;

  public CloseJUnitStatusAction( IWorkbench workbench ) {
    this( workbench, new WorkspaceScopePreferences() );
  }

  public CloseJUnitStatusAction( IWorkbench workbench, WorkspaceScopePreferences preferences ) {
    super( "Close" );
    this.workbench = workbench;
    this.preferences = preferences;
  }

  @Override
  public void run() {
    closeJUnitStatusBar();
    updateWorkbench();
  }

  private void closeJUnitStatusBar() {
    preferences.setShowJUnitStatusBar( false );
  }

  private void updateWorkbench() {
    new ExpressionEvaluator( workbench ).evaluate();
  }
}