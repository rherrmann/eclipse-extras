package com.codeaffine.extras.archive.internal.contenttype;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.junit.Test;

public class ExtensionTest {

  @Test
  public void testZipFile() throws Exception {
    String fileName = "file.zip";

    IContentDescription contentDescription = getContentDescription(fileName);

    assertThat(ContentTypes.isZipContentType(contentDescription.getContentType())).isTrue();
  }

  @Test
  public void testWarFile() throws Exception {
    String fileName = "file.war";

    IContentDescription contentDescription = getContentDescription(fileName);

    assertThat(ContentTypes.isZipContentType(contentDescription.getContentType())).isTrue();
  }

  @Test
  public void testJarFile() throws Exception {
    String fileName = "file.jar";

    IContentDescription contentDescription = getContentDescription(fileName);

    assertThat(ContentTypes.isZipContentType(contentDescription.getContentType())).isTrue();
  }

  @Test
  public void testEarFile() throws Exception {
    String fileName = "file.ear";

    IContentDescription contentDescription = getContentDescription(fileName);

    assertThat(ContentTypes.isZipContentType(contentDescription.getContentType())).isTrue();
  }

  private IContentDescription getContentDescription(String fileName) throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
      return Platform.getContentTypeManager().getDescriptionFor(inputStream, fileName, IContentDescription.ALL);
    }
  }
}
