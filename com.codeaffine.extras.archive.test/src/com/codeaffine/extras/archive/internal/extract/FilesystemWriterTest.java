package com.codeaffine.extras.archive.internal.extract;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesystemWriterTest {
  private static final byte[] CONTENT = new byte[] {1, 2, 3};

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private File directory;

  @Before
  public void setUp() {
    directory = tempFolder.getRoot();
  }

  @Test
  public void testWriteFile() throws Exception {
    IPath destinationFileName = new Path("file.txt");
    FilesystemWriter writer = new FilesystemWriter(directory);

    writer.write(destinationFileName, new ByteArrayInputStream(CONTENT));

    File destinationFile = new File(directory, destinationFileName.toPortableString());
    assertThat(destinationFile).hasBinaryContent(CONTENT);
  }

  @Test
  public void testWriteFileInFolder() throws Exception {
    IPath destinationFileName = new Path("folder/file.txt");
    FilesystemWriter writer = new FilesystemWriter(directory);

    writer.write(destinationFileName, new ByteArrayInputStream(CONTENT));

    File destinationFile = new File(directory, destinationFileName.toPortableString());
    assertThat(destinationFile).hasBinaryContent(CONTENT);
  }

}
