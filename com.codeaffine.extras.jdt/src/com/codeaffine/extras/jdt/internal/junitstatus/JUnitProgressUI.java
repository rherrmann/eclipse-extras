package com.codeaffine.extras.jdt.internal.junitstatus;

import java.util.Objects;

import org.eclipse.swt.graphics.Color;

import com.codeaffine.eclipse.swt.util.UIThreadSynchronizer;

public class JUnitProgressUI implements ProgressUI {

  private final JUnitProgressBar progressBar;
  private final UIThreadSynchronizer uiThreadSynchronizer;
  private volatile String currentToolTipText;

  public JUnitProgressUI(JUnitProgressBar progressBar) {
    this.progressBar = progressBar;
    this.uiThreadSynchronizer = new UIThreadSynchronizer();
    this.currentToolTipText = "";
  }

  @Override
  public void update(String text, int textAlignment, Color barColor, int selection, int maximum) {
    uiThreadSynchronizer.asyncExec(
        progressBar,
        () -> progressBar.setValues(text, textAlignment, barColor, selection, maximum));
  }

  @Override
  public void setToolTipText(String toolTipText) {
    if (!Objects.equals(currentToolTipText, toolTipText)) {
      currentToolTipText = toolTipText;
      execSetToolTipText(toolTipText);
    }
  }

  private void execSetToolTipText(String toolTipText) {
    uiThreadSynchronizer.asyncExec(progressBar, () -> progressBar.setToolTipText(toolTipText));
  }

}
