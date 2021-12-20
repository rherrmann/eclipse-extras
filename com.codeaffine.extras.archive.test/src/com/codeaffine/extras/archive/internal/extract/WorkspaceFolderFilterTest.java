package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.extract.WorkspaceFolderSelectionDialog.WorkspaceFolderFilter;

public class WorkspaceFolderFilterTest {

  private Viewer viewer;
  private ViewerFilter filter;

  @Test
  public void testSelectWithObject() throws Exception {
    boolean select = filter.select(viewer, null, new Object());

    assertFalse(select);
  }

  @Test
  public void testSelectWithContainer() throws Exception {
    boolean select = filter.select(viewer, null, mock(IContainer.class));

    assertFalse(select);
  }

  @Test
  public void testSelectWithProject() throws Exception {
    boolean select = filter.select(viewer, null, mock(IProject.class));

    assertTrue(select);
  }

  @Test
  public void testSelectWithFolder() throws Exception {
    boolean select = filter.select(viewer, null, mock(IFolder.class));

    assertTrue(select);
  }

  @Before
  public void setUp() {
    filter = new WorkspaceFolderFilter();
    viewer = mock(Viewer.class);
  }
}
