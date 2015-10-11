package com.codeaffine.extras.jdt.internal.junitstatus;

import java.util.Objects;

import org.eclipse.swt.graphics.Color;

import com.codeaffine.eclipse.swt.util.UIThreadSynchronizer;

public class JUnitProgressUI implements ProgressUI {

  private final JUnitProgressBar progressBar;
  private final UIThreadSynchronizer uiThreadSynchronizer;
  private volatile String currentToolTipText;

  public JUnitProgressUI( JUnitProgressBar progressBar ) {
    this.progressBar = progressBar;
    this.uiThreadSynchronizer = new UIThreadSynchronizer();
    this.currentToolTipText = "";
  }

  @Override
  public void update( final String text,
                      final int textAlignment,
                      final Color barColor,
                      final int selection,
                      final int maximum )
  {
    uiThreadSynchronizer.asyncExec( progressBar, new Runnable() {
      @Override
      public void run() {
        progressBar.setValues( text, textAlignment, barColor, selection, maximum );
      }
    } );
  }

  @Override
  public void setToolTipText( String toolTipText ) {
    if( !Objects.equals( currentToolTipText, toolTipText ) ) {
      currentToolTipText = toolTipText;
      execSetToolTipText( toolTipText );
    }
  }

  private void execSetToolTipText( final String toolTipText ) {
    uiThreadSynchronizer.asyncExec( progressBar, new Runnable() {
      @Override
      public void run() {
        progressBar.setToolTipText( toolTipText );
      }
    } );
  }

}