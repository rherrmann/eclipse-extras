package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class StatusComposite extends Composite {
  private int defaultTrimHeight;

  public StatusComposite( Composite parent, int style ) {
    super( parent, style );
  }

  @Override
  public Point computeSize( int wHint, int hHint, boolean changed ) {
    Point textSize = measureText( "MMMMMMMMMM" );
    int height = Math.max( getDefaultTrimHeight(), textSize.y + 4 );
    int width = textSize.x + 15;
    return new Point( width, height );
  }

  private int getDefaultTrimHeight() {
    if( defaultTrimHeight == 0 ) {
      defaultTrimHeight = computeDefaultTrimHeight();
    }
    return defaultTrimHeight;
  }

  private int computeDefaultTrimHeight() {
    int result;
    Shell shell = new Shell( getDisplay(), SWT.NONE );
    try {
      ToolBar toolBar = new ToolBar( shell, SWT.NONE );
      ToolItem toolItem = new ToolItem( toolBar, SWT.PUSH );
      toolItem.setImage( JFaceResources.getImageRegistry().get( Dialog.DLG_IMG_MESSAGE_INFO ) );
      int toolItemHeight = toolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
      result = Math.max( toolItemHeight, measureText( "Wg" ).y );
    } finally {
      shell.dispose();
    }
    return result;
  }

  private Point measureText( String string ) {
    GC gc = new GC( this );
    Point result = gc.textExtent( string );
    gc.dispose();
    return result;
  }
}