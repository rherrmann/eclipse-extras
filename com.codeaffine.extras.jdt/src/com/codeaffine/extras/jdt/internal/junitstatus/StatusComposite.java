package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
    GC gc = new GC( this );
    Point textSize = gc.textExtent( "MMMMMMMMMM" );
    gc.dispose();
    int height = Math.max( getDefaultTrimHeight(), textSize.y + 4 );
    int width = textSize.x + 15;
    return new Point( width, height );
  }

  private int getDefaultTrimHeight() {
    if( defaultTrimHeight == 0 ) {
      Shell shell = new Shell( getDisplay(), SWT.NONE );
      shell.setLayout( new GridLayout() );
      ToolBar toolBar = new ToolBar( shell, SWT.NONE );
      toolBar.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
      ToolItem toolItem = new ToolItem( toolBar, SWT.PUSH );
      toolItem.setImage( JFaceResources.getImageRegistry().get( Dialog.DLG_IMG_MESSAGE_INFO ) );
      shell.layout();
      int toolItemHeight = toolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
      GC gc = new GC( shell );
      Point fontSize = gc.textExtent( "Wg" );
      gc.dispose();
      defaultTrimHeight = Math.max( toolItemHeight, fontSize.y );
      shell.dispose();
    }
    return defaultTrimHeight;
  }
}