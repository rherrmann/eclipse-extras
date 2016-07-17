package com.codeaffine.extras.imageviewer.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class ImageViewerTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ImageViewer imageViewer;

  @Before
  public void setUp() {
    imageViewer = new ImageViewer( displayHelper.createShell() );
  }

  @Test
  public void testGetInitialImageDatas() {
    ImageData[] imageDatas = imageViewer.getImageDatas();

    assertThat( imageDatas ).isNull();
  }

  @Test
  public void testSetImageDatas() {
    Image image = new Image( displayHelper.getDisplay(), 2, 2 );
    ImageData imageData = image.getImageData();
    imageViewer.setImageDatas( imageData );

    assertThat( imageViewer.getImageDatas() ).containsOnly( imageData );
  }

  @Test
  public void testSetImageDatasToNull() {
    ImageData imageData = new Image( displayHelper.getDisplay(), 2, 2 ).getImageData();
    imageViewer.setImageDatas( imageData );
    Image previousImage = imageViewer.imageLabel.getImage();

    imageViewer.setImageDatas( ( ImageData[] )null );

    assertThat( previousImage.isDisposed() ).isTrue();
    assertThat( imageViewer.imageLabel.getImage() ).isNull();
  }

  @Test
  public void testDispose() {
    ImageData imageData = new Image( displayHelper.getDisplay(), 2, 2 ).getImageData();
    imageViewer.setImageDatas( imageData );
    Image currentImage = imageViewer.imageLabel.getImage();

    imageViewer.getControl().dispose();

    assertThat( currentImage.isDisposed() ).isTrue();
  }
}
