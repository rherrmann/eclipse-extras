package com.codeaffine.extras.archive.internal.contenttype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.junit.Before;
import org.junit.Test;

public class ZipContentDescriberTest {

  private ZipContentDescriber contentDescriber;

  @Test
  public void testGetSupportedOptions() {
    QualifiedName[] supportedOptions = contentDescriber.getSupportedOptions();

    assertNotNull(supportedOptions);
    assertEquals(0, supportedOptions.length);
  }

  @Test
  public void testDescribeWithEmptyStream() throws IOException {
    int describe = contentDescriber.describe(createInputStream(new byte[0]), null);

    assertEquals(IContentDescriber.INVALID, describe);
  }

  @Test
  public void testDescribeNonZipStream() throws IOException {
    byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
    int describe = contentDescriber.describe(createInputStream(bytes), null);

    assertEquals(IContentDescriber.INVALID, describe);
  }

  @Test
  public void testDescribeZipStream() throws IOException {
    byte[] bytes = {0x50, 0x4b, 0x03, 0x04, 0x01, 0x02, 0x03};
    int describe = contentDescriber.describe(createInputStream(bytes), null);

    assertEquals(IContentDescriber.VALID, describe);
  }

  @Before
  public void setUp() {
    contentDescriber = new ZipContentDescriber();
  }

  private static InputStream createInputStream(byte[] bytes) {
    return new ByteArrayInputStream(bytes);
  }
}
