package com.codeaffine.extras.imageviewer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class ImageViewer {

  private final ScrolledComposite scrolledComposite;
  private final Label imageLabel;
  private ImageData[] imageDatas;
  private Image image;

  public ImageViewer( Composite parent ) {
    scrolledComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
    scrolledComposite.setBackground( getBackgroundColor() );
    imageLabel = new Label( scrolledComposite, SWT.NONE );
    imageLabel.setBackground( getBackgroundColor() );
    scrolledComposite.setContent( imageLabel );
    scrolledComposite.addListener( SWT.Dispose, this::handleDispose );
  }

  public Control getControl() {
    return scrolledComposite;
  }

  public void setImageDatas( ImageData... imageDatas ) {
    if( image != null ) {
      image.dispose();
      image = null;
    }
    this.imageDatas = imageDatas;
    if( imageDatas != null && imageDatas.length > 0 ) {
      image = new Image( scrolledComposite.getDisplay(), imageDatas[ 0 ] );
    }
    imageLabel.setImage( image );
    imageLabel.pack();
  }

  public ImageData[] getImageDatas() {
    return imageDatas;
  }

  @SuppressWarnings("unused")
  private void handleDispose( Event event ) {
    setImageDatas( ( ImageData[] )null );
  }

  private Color getBackgroundColor() {
    return scrolledComposite.getDisplay().getSystemColor( SWT.COLOR_LIST_BACKGROUND );
  }

}
