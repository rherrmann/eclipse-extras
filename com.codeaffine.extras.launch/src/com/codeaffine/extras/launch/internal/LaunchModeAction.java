package com.codeaffine.extras.launch.internal;

import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class LaunchModeAction extends Action {

  private final LaunchModeSetting launchModeSetting;
  private final ILaunchMode launchMode;

  public LaunchModeAction( LaunchModeSetting launchModeSetting, ILaunchMode launchMode ) {
    super( launchMode.getLabel(), IAction.AS_CHECK_BOX );
    this.launchModeSetting = launchModeSetting;
    this.launchMode = launchMode;
    update();
  }

  public ILaunchMode getLaunchMode() {
    return launchMode;
  }

  @Override
  public void run() {
    launchModeSetting.setLaunchModeId( launchMode.getIdentifier() );
    update();
  }

  public void update() {
    setChecked( launchMode.equals( launchModeSetting.getLaunchMode() ) );
  }
}