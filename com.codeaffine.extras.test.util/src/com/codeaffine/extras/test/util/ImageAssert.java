package com.codeaffine.extras.test.util;

import java.net.URL;

import org.assertj.core.api.AbstractAssert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;


public class ImageAssert extends AbstractAssert<ImageAssert, ImageInfo> {

  private ImageAssert(ImageInfo actual) {
    super(actual, ImageAssert.class);
  }

  public static ImageAssert assertThat(String pluginId, String imagePath) {
    return new ImageAssert(new ImageInfo(pluginId, imagePath));
  }

  public ImageAssert exists() {
    Bundle bundle = Platform.getBundle(actual.pluginId);
    URL location = FileLocator.find(bundle, new Path(actual.imagePath), null);
    ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(location);
    Image image = imageDescriptor.createImage(false);
    if (image == null) {
      failWithMessage("Image <%s> not found in plugin <%s>", actual.imagePath, actual.pluginId);
    }
    image.dispose();
    return this;
  }
}
