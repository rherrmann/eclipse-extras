package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.extract.WorkspaceFolderSelectionDialog.WorkspaceFolderValidator;

public class WorkspaceFolderValidatorTest {

  private ISelectionStatusValidator validator;

  @Test
  public void testValidateWithObject() {
    IStatus status = validator.validate(new Object[] {new Object()});

    assertEquals(IStatus.ERROR, status.getSeverity());
  }

  @Test
  public void testValidateWithProject() {
    IStatus status = validator.validate(new Object[] {mock(IProject.class)});

    assertTrue(status.isOK());
    assertEquals("", status.getMessage());
  }

  @Test
  public void testValidateWithFolder() {
    IStatus status = validator.validate(new Object[] {mock(IFolder.class)});

    assertTrue(status.isOK());
    assertEquals("", status.getMessage());
  }

  @Test
  public void testValidateWithMultipleProjects() {
    Object[] selection = new Object[] {mock(IProject.class), mock(IProject.class)};

    IStatus status = validator.validate(selection);

    assertEquals(IStatus.ERROR, status.getSeverity());
  }

  @Test
  public void testValidateWithMultipleFolders() {
    Object[] selection = new Object[] {mock(IFolder.class), mock(IFolder.class)};

    IStatus status = validator.validate(selection);

    assertEquals(IStatus.ERROR, status.getSeverity());
  }

  @Before
  public void setUp() {
    validator = new WorkspaceFolderValidator();
  }
}
