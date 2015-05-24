package com.codeaffine.extras.workingset.internal;

import static org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT;
import static org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class PreviewLabelProvider extends LabelProvider implements IColorProvider {

  private final LocalResourceManager resourceManager;
  private ProjectPatternMatcher projectPatternMatcher;

  public PreviewLabelProvider( Display display ) {
    resourceManager = new LocalResourceManager( JFaceResources.getResources( display ) );
    projectPatternMatcher = new ProjectPatternMatcher( "" );
  }

  public void setPattern( String pattern ) {
    projectPatternMatcher = new ProjectPatternMatcher( pattern );
  }

  @Override
  public String getText( Object element ) {
    IProject project = ( IProject )element;
    return project.getName();
  }

  @Override
  public Image getImage( Object element ) {
    IProject project = ( IProject )element;
    return getSharedImage( project.isOpen() ? IMG_OBJ_PROJECT : IMG_OBJ_PROJECT_CLOSED );
  }

  @Override
  public Color getForeground( Object element ) {
    IProject project = ( IProject )element;
    Color result = null;
    if( !projectPatternMatcher.matches( project ) ) {
      result = JFaceResources.getColorRegistry().get( JFacePreferences.QUALIFIER_COLOR );
    }
    return result;
  }

  @Override
  public Color getBackground( Object element ) {
    return null;
  }

  @Override
  public void dispose() {
    super.dispose();
    resourceManager.dispose();
  }

  private Image getSharedImage( String imageName ) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    ImageDescriptor imageDescriptor = workbench.getSharedImages().getImageDescriptor( imageName );
    return resourceManager.createImage( imageDescriptor );
  }
}