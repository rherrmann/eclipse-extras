package com.codeaffine.extras.platform.test;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.core.resources.IContainer.INCLUDE_HIDDEN;
import static org.eclipse.core.resources.IResource.ALWAYS_DELETE_PROJECT_CONTENT;
import static org.eclipse.core.resources.IResource.FORCE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.ide.undo.CreateProjectOperation;

import com.google.common.base.Charsets;

public class ProjectHelper {

  private static int uniqueId = 0;
  private static List<ProjectHelper> projects = new LinkedList<ProjectHelper>();

  public static void cleanWorkspace() throws CoreException {
    projects.clear();
    IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects( INCLUDE_HIDDEN );
    for( IProject project : allProjects ) {
      delete( project );
      project.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }
  }

  private final String projectName;
  private final IPath projectLocation;
  private IProject project;

  public ProjectHelper() {
    this.projectName = uniqueProjectName();
    this.projectLocation = null;
  }

  public ProjectHelper( File baseLocation ) {
    this.projectName = uniqueProjectName();
    this.projectLocation = baseLocation == null ? null : concat( baseLocation, projectName );
  }

  public ProjectHelper( IPath projectLocation ) {
    this( projectLocation, uniqueProjectName() );
  }

  public ProjectHelper( IPath projectLocation, String projectName ) {
    this.projectName = projectName;
    this.projectLocation = projectLocation;
  }

  public String getName() {
    initializeProject();
    return project.getName();
  }

  public IProject getProject() {
    initializeProject();
    return project;
  }

  public IFolder createFolder( String name ) throws CoreException {
    initializeProject();
    String[] segments = new Path( name ).segments();
    IContainer container = project;
    for( String segment : segments ) {
      IFolder newFolder = container.getFolder( new Path( segment ) );
      if( !newFolder.exists() ) {
        newFolder.create( true, true, newProgressMonitor() );
      }
      container = newFolder;
    }
    return project.getFolder( new Path( name ) );
  }

  public IFile createFile( String fileName, String content ) throws CoreException {
    return createFile( getProject(), fileName, content );
  }

  public IFile createFile( IContainer parent, String fileName, String content )
    throws CoreException
  {
    initializeProject();
    IFile result = parent.getFile( new Path( fileName ) );
    InputStream stream = new ByteArrayInputStream( content.getBytes( Charsets.UTF_8 ) );
    if( !result.exists() ) {
      result.create( stream, true, newProgressMonitor() );
    } else {
      result.setContents( stream, false, false, newProgressMonitor() );
    }
    return result;
  }

  public void dispose() throws CoreException {
    if( isProjectCreated() ) {
      projects.remove( this );
      delete( project );
    }
  }

  private boolean isProjectCreated() {
    return project != null;
  }

  private void initializeProject() {
    if( !isProjectCreated() ) {
      IProjectDescription projectDescription = createProjectDescription();
      project = createProject( projectDescription );
      projects.add( this );
    }
  }

  private IProject createProject( IProjectDescription projectDescription ) {
    String label = "Create project " + projectName;
    try {
      new CreateProjectOperation( projectDescription, label ).execute( newProgressMonitor(), null );
    } catch( ExecutionException ee ) {
      throw new RuntimeException( ee );
    }
    return ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
  }

  private IProjectDescription createProjectDescription() {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProjectDescription result = workspace.newProjectDescription( projectName );
    result.setLocation( projectLocation );
    return result;
  }

  private static IProgressMonitor newProgressMonitor() {
    return new NullProgressMonitor();
  }

  private static String uniqueProjectName() {
    String result = "test.project." + uniqueId;
    uniqueId++;
    return result;
  }

  private static IPath concat( File baseLocation, String projectName ) {
    return new Path( baseLocation.getAbsolutePath() ).append( projectName );
  }

  private static void delete( IResource resource ) throws CoreException {
    int numAttempts = 0;
    boolean success = false;
    while( !success ) {
      try {
        resource.delete( FORCE | ALWAYS_DELETE_PROJECT_CONTENT, new NullProgressMonitor() );
        success = true;
        numAttempts++;
      } catch( CoreException ce ) {
        if( numAttempts > 4 ) {
          throw ce;
        }
        System.gc();
        sleepUninterruptibly( 500, MILLISECONDS );
        System.gc();
      }
    }
  }

}
