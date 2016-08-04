package com.codeaffine.extras.imageviewer.internal;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.eclipse.core.runtime.test.util.ExtensionAssert;

public class ImageViewerEditorExtensionPDETest {

  @Test
  public void testEditorExtension() {
    Extension actual = readEditorsExtension();

    ExtensionAssert.assertThat( actual )
      .isInstantiable( "class", ImageViewerEditor.class )
      .hasNoAttributeValueFor( "contributorClass" )
      .hasAttributeValue( "icon", "$nl$/icons/eview16/image-viewer-editor.gif" )
      .hasAttributeValue( "default", Boolean.TRUE.toString() )
      .hasNonEmptyAttributeValueFor( "name" )
      .hasNoAttributeValueFor( "matchingStrategy" )
      .hasChildWithAttributeValue( "contentTypeId", ImageViewerPlugin.IMAGE_CONTENT_TYPE_ID );
  }

  private static Extension readEditorsExtension() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.editors" )
      .thatMatches( attribute( "id", ImageViewerEditor.ID ) )
      .process();
  }

}
