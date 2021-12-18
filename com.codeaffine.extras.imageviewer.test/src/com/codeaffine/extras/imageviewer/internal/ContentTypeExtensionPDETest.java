package com.codeaffine.extras.imageviewer.internal;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collection;
import java.util.stream.Stream;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ide.IDE;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class ContentTypeExtensionPDETest {

  @Parameters(name = "{0}")
  public static Collection<Object[]> parameters() {
    return Stream.of("gif", "jpg", "jpeg", "png", "bmp", "ico", "tiff")
        .map(param -> new Object[] {param})
        .collect(toList());
  }

  @Parameter
  public String fileExtension;
  private String fileName;

  @Before
  public void setUp() {
    fileName = "image." + fileExtension;
  }

  @Test
  public void testContentTypes() {
    IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(fileName);

    assertThat(contentType.getId()).isEqualTo(ImageViewerPlugin.IMAGE_CONTENT_TYPE_ID);
  }

  @Test
  public void testDefaultEditorBinding() throws Exception {
    IEditorDescriptor editorDescriptor = IDE.getEditorDescriptor(fileName, true, false);

    assertThat(editorDescriptor.getId()).isEqualTo(ImageViewerEditor.ID);
  }

}
