package com.codeaffine.extras.workingset.internal;

import static com.codeaffine.extras.workingset.internal.RegexPatterns.ANYTHING;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.MoveResourcesOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.test.util.ProjectHelper;
import com.codeaffine.extras.workingset.internal.DynamicWorkingSet;

public class DynamicWorkingSetUpdaterPDETest {

  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();

  private Collection<IProject> renamedProjects;
  private IWorkingSetManager workingSetManager;
  private IWorkingSet workingSet;

  @Before
  public void setUp() {
    renamedProjects = new LinkedList<IProject>();
    workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    workingSet = workingSetManager.createWorkingSet( "", new IAdaptable[ 0 ] );
    workingSet.setId( DynamicWorkingSet.ID );
  }

  @After
  public void tearDown() {
    workingSetManager.removeWorkingSet( workingSet );
    for( IProject project : renamedProjects ) {
      ProjectHelper.delete( project );
    }
  }

  @Test
  public void testCreateMatchingProject() {
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );

    IProject project = projectHelper.getProject();

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testCreateNonMatchingProject() {
    setWorkingSetPattern( "very-special-projects-*" );
    workingSetManager.addWorkingSet( workingSet );

    projectHelper.getProject();

    assertThat( workingSet.getElements() ).isEmpty();
  }

  @Test
  public void testDeleteMatchingProject() {
    IProject project = projectHelper.getProject();
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );

    ProjectHelper.delete( project );

    assertThat( workingSet.getElements() ).isEmpty();
  }

  @Test
  public void testRenameProjectToMatchingName() throws ExecutionException {
    IProject project = projectHelper.getProject();
    setWorkingSetPattern( "foo*" );
    workingSetManager.addWorkingSet( workingSet );

    IProject renamedProject = renameProjectTo( project, "foo" );

    assertThat( workingSet.getElements() ).containsOnly( renamedProject );
  }

  @Test
  public void testRenameProjectToNonMatchingName() throws ExecutionException {
    IProject project = projectHelper.getProject();
    setWorkingSetPattern( project.getName() );
    workingSetManager.addWorkingSet( workingSet );

    renameProjectTo( project, "foo" );

    assertThat( workingSet.getElements() ).isEmpty();
  }

  @Test
  public void testCloseMatchingProject() throws CoreException {
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );
    IProject project = projectHelper.getProject();

    project.close( new NullProgressMonitor() );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testAddWorkingSet() {
    IProject project = projectHelper.getProject();

    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testAddWorkingSetWithoutMatchingProjects() {
    projectHelper.getProject();

    setWorkingSetPattern( "very-special-projects-*" );
    workingSetManager.addWorkingSet( workingSet );

    assertThat( workingSet.getElements() ).isEmpty();
  }

  @Test
  public void testAddWorkingSetWithClosedProject() throws CoreException {
    IProject project = projectHelper.getProject();
    project.close( new NullProgressMonitor() );

    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testChangeWorkingSetToExcludeProject() {
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );
    projectHelper.getProject();

    setWorkingSetPattern( "very-special-projects-.*" );

    assertThat( workingSet.getElements() ).isEmpty();
  }

  @Test
  public void testChangeWorkingSetToIncludeProject() {
    setWorkingSetPattern( "very-special-projects-.*" );
    workingSetManager.addWorkingSet( workingSet );
    IProject project = projectHelper.getProject();

    setWorkingSetPattern( ANYTHING );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  /*
   * Ensure that it is possible to manually add non-matching projects. Otherwise dragging a project
   * from the 'Other Projects' WS to a dynamic WS may cause the project to vanish: it is removed
   * from the 'Other Projects' but never added to the dynamic WS if its name does not match the
   * pattern.
   * Therefore it is safer to accept the _manual_ addition even though the project will be removed
   * when _something else_ (e.g. view re-open, workbench restart, pattern change) causes the dynamic
   * WS to be updated.
   */
  @Test
  public void testAddNonMatchingProjectToWorkingSet() {
    setWorkingSetPattern( "very-special-projects-.*" );
    workingSetManager.addWorkingSet( workingSet );
    IProject project = projectHelper.getProject();

    workingSet.setElements( new IAdaptable[] { project } );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testAddMatchingProjectToWorkingSet() {
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );
    IProject project = projectHelper.getProject();

    workingSet.setElements( new IAdaptable[] { project } );

    assertThat( workingSet.getElements() ).containsOnly( project );
  }

  @Test
  public void testRemoveMatchingProjectFromWorkingSet() {
    setWorkingSetPattern( ANYTHING );
    workingSetManager.addWorkingSet( workingSet );
    projectHelper.getProject();

    workingSet.setElements( new IAdaptable[ 0 ] );

    assertThat( workingSet.getElements() ).isEmpty();
  }

  private void setWorkingSetPattern( String pattern ) {
    workingSet.setName( pattern );
  }

  private IProject renameProjectTo( IProject project, String name ) throws ExecutionException {
    IProject result = project.getWorkspace().getRoot().getProject( name );
    IPath path = result.getFullPath();
    AbstractOperation operation = new MoveResourcesOperation( project, path, "Rename project" );
    operation.execute( new NullProgressMonitor(), null );
    renamedProjects.add( result );
    return result;
  }


}
