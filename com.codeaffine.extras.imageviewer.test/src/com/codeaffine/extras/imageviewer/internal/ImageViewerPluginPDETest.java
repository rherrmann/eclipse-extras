package com.codeaffine.extras.imageviewer.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageViewerPluginPDETest {

  @Test
  public void testID() {
    Bundle imageViewerBundle = FrameworkUtil.getBundle( ImageViewerPlugin.class );

    assertThat( ImageViewerPlugin.ID ).isEqualTo( imageViewerBundle.getSymbolicName() );
  }
}
