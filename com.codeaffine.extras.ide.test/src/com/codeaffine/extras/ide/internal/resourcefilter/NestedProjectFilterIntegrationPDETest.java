package com.codeaffine.extras.ide.internal.resourcefilter;

import static com.codeaffine.extras.ide.internal.resourcefilter.NestedProjectFilter.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.core.resources.IProjectDescription.DESCRIPTION_FILE_NAME;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.core.resources.IResourceFilterDescription.EXCLUDE_ALL;
import static org.eclipse.core.resources.IResourceFilterDescription.FOLDERS;
import static org.eclipse.core.resources.IResourceFilterDescription.INHERITABLE;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.test.util.ProjectHelper;

public class NestedProjectFilterIntegrationPDETest {

  private static final int FILTER_TYPE = FOLDERS | EXCLUDE_ALL | INHERITABLE;

  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  @Test
  public void testMembersWithFilter() throws CoreException {
    IProject project = projectHelper.getProject();
    createNestedProjectFilter();
    createNestedProject();

    assertThat( project.members() ).containsOnly( project.getFile( DESCRIPTION_FILE_NAME ) );
  }

  @Test
  public void testMembersWithoutFilter() throws CoreException {
    IProject project = projectHelper.getProject();
    IProject nestedProject = createNestedProject();

    IResource projectDescription = project.getFile( DESCRIPTION_FILE_NAME );
    IResource nestedProjectFolder = project.getFolder( nestedProject.getName() );
    assertThat( project.members() ).containsOnly( projectDescription, nestedProjectFolder );
  }

  @Test
  public void testMembersWithoutNestedProject() throws CoreException {
    IProject project = projectHelper.getProject();
    createNestedProjectFilter();

    assertThat( project.members() ).containsOnly( project.getFile( DESCRIPTION_FILE_NAME ) );
  }

  private void createNestedProjectFilter() throws CoreException {
    IProject project = projectHelper.getProject();
    FileInfoMatcherDescription matcherDescription = new FileInfoMatcherDescription( ID, null );
    project.createFilter( FILTER_TYPE, matcherDescription, NONE, new NullProgressMonitor() );
  }

  private IProject createNestedProject() throws CoreException {
    IProject result = projectHelper.createNestedProject().getProject();
    projectHelper.getProject().refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
    return result;
  }

}
