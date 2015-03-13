package com.codeaffine.extras.ide.internal.workingset;

import static com.codeaffine.extras.ide.internal.workingset.RegexPatterns.ANYTHING;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.junit.Before;
import org.junit.Test;

public class WorkingSetContentUpdaterTest {

  private IWorkingSet workingSet;
  private TestProjectsProvider projectsProvider;

  @Before
  public void setUp() {
    workingSet = mock( IWorkingSet.class );
    projectsProvider = new TestProjectsProvider();
  }

  @Test
  public void testCreateProject() {
    IProject project = createProject( "foo" );
    setWorkingSetPattern( ANYTHING );
    setWorkingSetElements( new IAdaptable[ 0 ] );

    updateElements();

    verify( workingSet ).setElements( new IAdaptable[] { project } );
  }

  @Test
  public void testCreateNonMatchingProject() {
    createProject( "foo" );
    setWorkingSetPattern( "bar.*" );
    setWorkingSetElements( new IAdaptable[ 0 ] );

    updateElements();

    verify( workingSet, never() ).setElements( any( IAdaptable[].class ) );
  }

  @Test
  public void testDeleteProject() {
    setWorkingSetPattern( ANYTHING );
    IProject project = createProject( "foo" );
    setWorkingSetElements( project );
    deleteProject( project );

    updateElements();

    verify( workingSet ).setElements( new IAdaptable[ 0 ] );
  }

  @Test
  public void testDeleteNonMatchingProject() {
    setWorkingSetPattern( "bar.*" );
    IProject project = createProject( "foo" );
    setWorkingSetElements( project );
    deleteProject( project );

    updateElements();

    verify( workingSet ).setElements( new IAdaptable[ 0 ] );
  }

  @Test
  public void testChangePatternToExcludeProject() {
    IProject project = createProject( "foo" );
    setWorkingSetElements( project );
    setWorkingSetPattern( "bar.*" );

    updateElements();

    verify( workingSet ).setElements( new IAdaptable[ 0 ] );
  }

  @Test
  public void testChangePatternToIncludeProject() {
    IProject project = createProject( "foo-bar" );
    setWorkingSetElements( new IAdaptable[ 0 ] );
    setWorkingSetPattern( "foo.*" );

    updateElements();

    verify( workingSet ).setElements( new IAdaptable[] { project } );
  }

  @Test
  public void testChangePatternToIncludeAndExcludeProject() {
    IProject fooProject = createProject( "foo" );
    IProject barProject = createProject( "bar" );
    setWorkingSetPattern( "foo.*" );
    setWorkingSetElements( fooProject );

    setWorkingSetPattern( "bar.*" );
    updateElements();

    verify( workingSet ).setElements( new IAdaptable[] { barProject } );
  }

  private IProject createProject( String name ) {
    IProject result = mock( IProject.class );
    when( result.getName() ).thenReturn( name );
    projectsProvider.addProject( result );
    return result;
  }

  private void deleteProject( IProject project ) {
    projectsProvider.removeProject( project );
  }

  private void updateElements() {
    new WorkingSetContentUpdater( workingSet, projectsProvider ).updateElements();
  }

  private void setWorkingSetPattern( String pattern ) {
    when( workingSet.getName() ).thenReturn( pattern );
  }

  private void setWorkingSetElements( IAdaptable... elements ) {
    when( workingSet.getElements() ).thenReturn( elements );
  }
}
