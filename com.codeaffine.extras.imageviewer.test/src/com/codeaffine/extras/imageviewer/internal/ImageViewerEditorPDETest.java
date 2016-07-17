package com.codeaffine.extras.imageviewer.internal;

import static com.codeaffine.extras.imageviewer.internal.ImageViewerEditor.ID;
import static com.codeaffine.extras.imageviewer.internal.Images.GIF_IMAGE;
import static com.codeaffine.extras.imageviewer.internal.Images.PNG_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.core.resources.IResource.NONE;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.codeaffine.extras.test.util.ProjectHelper;

public class ImageViewerEditorPDETest {

  @Rule
  public final ProjectHelper projectHelper = new ProjectHelper();
  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  private IWorkbenchPage activePage;

  @Before
  public void setUp() {
    activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }

  @Test
  public void testOpenFromJaredImage() throws PartInitException {
    ImageViewerEditor editor = openImageViewerEditor( new ImageEditorInput() );

    assertThat( editor.imageCanvas.getImageDatas() ).isNotEmpty();
  }

  @Test
  public void testOpenFromWorkspaceImage() throws CoreException {
    IFile file = projectHelper.getProject().getFile( "image.gif" );
    file.create( getClass().getResourceAsStream( GIF_IMAGE ), NONE, null );

    IEditorInput editorInput = new FileEditorInput( file );
    ImageViewerEditor editor = openImageViewerEditor( editorInput );

    assertThat( editor.imageCanvas.getImageDatas() ).isNotEmpty();
  }

  @Test
  public void testOpenFromFilesystemFile() throws Exception {
    Path file = new File( tempFolder.getRoot(), "image.gif" ).toPath();
    Files.copy( getClass().getResourceAsStream( GIF_IMAGE ), file );

    URI uri = file.toUri();
    ImageViewerEditor editor = openImageViewerEditor( uri );

    assertThat( editor.imageCanvas.getImageDatas() ).isNotEmpty();
  }

  @Test
  public void testOpenWithUnsupportedEditorInput() throws PartInitException {
    IEditorInput editorInput = new UnsupportedEditorInput();

    IEditorPart editor = IDE.openEditor( activePage, editorInput, ID, true );

    assertThat( editor ).isNotInstanceOf( ImageViewerEditor.class );
  }

  @Test
  public void testReopen() throws CoreException {
    IFile file1 = projectHelper.getProject().getFile( "image.gif" );
    file1.create( getClass().getResourceAsStream( GIF_IMAGE ), NONE, null );
    IFile file2 = projectHelper.getProject().getFile( "image.png" );
    file2.create( getClass().getResourceAsStream( PNG_IMAGE ), NONE, null );

    ImageViewerEditor editor = openImageViewerEditor( new FileEditorInput( file1 ) );
    ImageData[] imageDatas1 = editor.imageCanvas.getImageDatas();
    editor.getSite().getPage().reuseEditor( editor, new FileEditorInput( file2 ) );
    ImageData[] imageDatas2 = editor.imageCanvas.getImageDatas();

    assertThat( imageDatas1 ).isNotEqualTo( imageDatas2 );
  }

  private ImageViewerEditor openImageViewerEditor( URI uri ) throws PartInitException {
    return ( ImageViewerEditor )IDE.openEditor( activePage, uri, ID, true );
  }

  private ImageViewerEditor openImageViewerEditor( IEditorInput editorInput )
    throws PartInitException
  {
    return ( ImageViewerEditor )IDE.openEditor( activePage, editorInput, ID, true );
  }

  private static class UnsupportedEditorInput implements IEditorInput {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter( Class adapter ) {
      return null;
    }

    @Override
    public String getToolTipText() {
      return "";
    }

    @Override
    public IPersistableElement getPersistable() {
      return null;
    }

    @Override
    public String getName() {
      return "Unsuported";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return null;
    }

    @Override
    public boolean exists() {
      return false;
    }
  }

  private static class ImageEditorInput implements IStorageEditorInput {
    @Override
    public boolean exists() {
      return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return null;
    }

    @Override
    public String getName() {
      return Images.GIF_IMAGE;
    }

    @Override
    public IPersistableElement getPersistable() {
      return null;
    }

    @Override
    public String getToolTipText() {
      return "";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter( Class adapter ) {
      return Platform.getAdapterManager().getAdapter( this, adapter );
    }

    @Override
    public IStorage getStorage() throws CoreException {
      return new ImageStorage();
    }

  }

  private static class ImageStorage implements IStorage {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter( Class adapter ) {
      return Platform.getAdapterManager().getAdapter( this, adapter );
    }

    @Override
    public boolean isReadOnly() {
      return true;
    }

    @Override
    public String getName() {
      return Images.GIF_IMAGE;
    }

    @Override
    public IPath getFullPath() {
      return null;
    }

    @Override
    public InputStream getContents() {
      return getClass().getResourceAsStream( Images.GIF_IMAGE );
    }
  }
}
