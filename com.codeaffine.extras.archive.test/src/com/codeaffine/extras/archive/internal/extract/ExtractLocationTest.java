package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.codeaffine.extras.test.util.ProjectHelper;

public class ExtractLocationTest {

  @Rule
  public ProjectHelper projectHelper = new ProjectHelper();
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testGetPath() {
    ExtractLocation extractLocation = new ExtractLocation("path/to/folder");

    IPath path = extractLocation.getPath();

    assertEquals("path/to/folder", path.toPortableString());
  }

  @Test
  public void testIsWorkspaceRelativeWithExistingWorkspaceFolder() throws Exception {
    IFolder folder = projectHelper.createFolder("foo");
    String path = folder.getFullPath().toPortableString();
    ExtractLocation extractLocation = new ExtractLocation(path);

    boolean workspaceRelative = extractLocation.isWorkspaceRelative();

    assertTrue(workspaceRelative);
  }

  @Test
  public void testIsWorkspaceRelativeWithNonExistingFolder() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation("foo");

    boolean workspaceRelative = extractLocation.isWorkspaceRelative();

    assertFalse(workspaceRelative);
  }

  @Test
  public void testValidateWithEmptyPath() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation("");

    IStatus status = extractLocation.validate();

    assertEquals(IStatus.ERROR, status.getSeverity());
  }

  @Test
  public void testValidateWithExistingFilesystemFolder() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation(tempFolder.getRoot().getCanonicalPath());

    IStatus status = extractLocation.validate();

    assertTrue(status.isOK());
  }

  @Test
  public void testValidateWithExistingWorkspaceFolder() throws Exception {
    IFolder folder = projectHelper.createFolder("foo");
    String path = folder.getFullPath().toPortableString();
    ExtractLocation extractLocation = new ExtractLocation(path);

    IStatus status = extractLocation.validate();

    assertTrue(status.isOK());
  }

  @Test
  public void testValidateWithNonExistingFolder() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation("/path/to/workspace/folder");

    IStatus status = extractLocation.validate();

    assertEquals(IStatus.ERROR, status.getSeverity());
  }

  @Test
  public void testValidateWithNonEmptyFilesystemFolder() throws Exception {
    File file = new File(tempFolder.getRoot(), "file.txt");
    file.createNewFile();
    ExtractLocation extractLocation = new ExtractLocation(tempFolder.getRoot().getCanonicalPath());

    IStatus status = extractLocation.validate();

    assertEquals(IStatus.WARNING, status.getSeverity());
  }

  @Test
  public void testValidateWithNonEmptyWorkspaceFolder() throws Exception {
    IFolder folder = projectHelper.createFolder("foo");
    projectHelper.createFile(folder, "file.txt", "");
    String path = folder.getFullPath().toPortableString();
    ExtractLocation extractLocation = new ExtractLocation(path);

    IStatus status = extractLocation.validate();

    assertEquals(IStatus.WARNING, status.getSeverity());
  }

}
