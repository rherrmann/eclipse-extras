package com.codeaffine.extras.imageviewer.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class ImageViewerTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ImageViewer imageCanvas;

  @Before
  public void setUp() {
    imageCanvas = new ImageViewer( displayHelper.createShell() );
  }

  @Test
  public void testGetInitialImageDatas() {
    ImageData[] imageDatas = imageCanvas.getImageDatas();

    assertThat( imageDatas ).isNull();
  }

  @Test
  public void testSetImageDatas() {
    Image image = new Image( displayHelper.getDisplay(), 2, 2 );
    imageCanvas.setImageDatas( image.getImageData() );

    fail();
  }
}
