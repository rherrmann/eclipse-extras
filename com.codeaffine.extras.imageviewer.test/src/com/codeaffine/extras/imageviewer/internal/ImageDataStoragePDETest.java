package com.codeaffine.extras.imageviewer.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.codeaffine.extras.test.util.ProjectHelper;

public class ImageDataStoragePDETest {

  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private ImageData imageData;

  @Before
  public void setUp() throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream(Images.GIF_IMAGE)) {
      imageData = new ImageLoader().load(inputStream)[0];
    }
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullImageDatas() {
    new ImageDataStorage((ImageData[]) null);
  }

  @Test
  public void testLoadWithNullFile() {
    ImageDataStorage storage = new ImageDataStorage(imageData);
    NullProgressMonitor monitor = new NullProgressMonitor();

    Throwable throwable = catchThrowable(() -> storage.save(null, monitor));

    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testLoadWithNullMonitor() {
    ImageDataStorage storage = new ImageDataStorage(imageData);
    IFile file = projectHelper.getProject().getFile("file");

    Throwable throwable = catchThrowable(() -> storage.save(file, null));

    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testSaveWithExistingFile() throws CoreException {
    ImageDataStorage storage = new ImageDataStorage(imageData);
    IFile file = projectHelper.createFile("image.gif", "");

    storage.save(file, new NullProgressMonitor());

    assertThat(file.getLocation().toFile().length()).isGreaterThan(0);
  }

  @Test
  public void testSaveWithNonExistingFile() throws CoreException {
    ImageDataStorage storage = new ImageDataStorage(imageData);
    IFile file = projectHelper.getProject().getFile("image.gif");

    storage.save(file, new NullProgressMonitor());

    assertThat(file.getLocation().toFile().length()).isGreaterThan(0);
  }
}
