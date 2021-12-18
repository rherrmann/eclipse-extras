package com.codeaffine.extras.launch.internal.dialog;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;

public class LaunchConfigComparator implements Comparator<ILaunchConfiguration> {
  private final List<Object> launchConfigHistory;
  private final ILaunchMode launchMode;

  public LaunchConfigComparator(LaunchConfigSelectionHistory launchConfigHistory, ILaunchMode launchMode) {
    requireNonNull(launchConfigHistory);
    this.launchConfigHistory = asList(launchConfigHistory.getHistoryItems());
    this.launchMode = launchMode;
  }

  @Override
  public int compare(ILaunchConfiguration launchConfig1, ILaunchConfiguration launchConfig2) {
    requireNonNull(launchConfig1, "launchConfig1");
    requireNonNull(launchConfig2, "launchConfig2");
    int result;
    if (isHistoryElement(launchConfig1) && isHistoryElement(launchConfig2)) {
      result = compareHistoryElements(launchConfig1, launchConfig2);
    } else {
      result = launchConfig1.getName().compareTo(launchConfig2.getName());
    }
    return result;
  }

  private int compareHistoryElements(ILaunchConfiguration launchConfig1, ILaunchConfiguration launchConfig2) {
    int result;
    if (isLastLaunchedConfig(launchConfig1)) {
      result = -1;
    } else if (isLastLaunchedConfig(launchConfig2)) {
      result = 1;
    } else {
      result = launchConfigHistory.indexOf(launchConfig1) - launchConfigHistory.indexOf(launchConfig2);
    }
    return result;
  }

  private boolean isLastLaunchedConfig(ILaunchConfiguration launchConfig) {
    ILaunchConfiguration lastLaunchedConfig = getLastLaunchConfig(launchConfig);
    return launchConfig.equals(lastLaunchedConfig);
  }

  private ILaunchConfiguration getLastLaunchConfig(ILaunchConfiguration launchConfig) {
    ILaunchConfiguration result = null;
    if (launchMode != null) {
      ILaunchGroup launchGroup = DebugUITools.getLaunchGroup(launchConfig, launchMode.getIdentifier());
      if (launchGroup != null) {
        result = DebugUITools.getLastLaunch(launchGroup.getIdentifier());
      }
    }
    return result;
  }

  private boolean isHistoryElement(ILaunchConfiguration launchConfig) {
    return launchConfigHistory.contains(launchConfig);
  }
}
