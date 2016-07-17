package com.codeaffine.extras.imageviewer.internal;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static com.codeaffine.extras.imageviewer.internal.ImageViewerPlugin.IMAGE_CONTENT_TYPE_ID;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.eclipse.core.runtime.test.util.ExtensionAssert;

public class ImageContentTypePDETest {

  @Test
  public void testExtension() {
    Extension actual = readEditorsExtension();

    ExtensionAssert.assertThat( actual )
      .hasAttributeValue( "name", "Image" )
      .hasAttributeValue( "priority", "normal" )
      .hasAttributeValue( "file-extensions", "gif,jpg,jpeg,png,bmp,ico,tiff" );
  }

  private static Extension readEditorsExtension() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.core.contenttype.contentTypes" )
      .thatMatches( attribute( "id", IMAGE_CONTENT_TYPE_ID ) )
      .process();
  }

}
