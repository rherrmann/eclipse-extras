package com.codeaffine.extras.launch.internal.dialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class ToggleTerminateBeforeRelaunchAction extends Action {

  public static final String ID = ToggleTerminateBeforeRelaunchAction.class.getName();

  private final DebugUIPreferences preferences;

  public ToggleTerminateBeforeRelaunchAction() {
    this( new DebugUIPreferences() );
  }

  public ToggleTerminateBeforeRelaunchAction( DebugUIPreferences preferences ) {
    super( "Terminate before Relaunch", IAction.AS_CHECK_BOX );
    this.preferences = preferences;
    setId( ID );
    setChecked( preferences.isTerminateBeforeRelaunch() );
  }

  @Override
  public void run() {
    preferences.setTerminateBeforeRelaunch( !preferences.isTerminateBeforeRelaunch() );
  }
}