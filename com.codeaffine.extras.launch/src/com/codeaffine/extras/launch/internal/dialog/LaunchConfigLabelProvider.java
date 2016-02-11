package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.Images.RUNNING;
import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.LIST;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_RIGHT;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.codeaffine.extras.launch.internal.Images;


public class LaunchConfigLabelProvider extends LabelProvider implements IStyledLabelProvider {

  public enum LabelMode {
    LIST,
    DETAIL
  }

  private static final Styler ADDITIONAL_INFO_STYLER = StyledString.QUALIFIER_STYLER;

  private final LocalResourceManager resourceManager;
  private final DuplicatesDetector duplicatesDetector;
  private final LabelMode labelMode;
  private final IDebugModelPresentation debugModelPresentation;

  public LaunchConfigLabelProvider( Display display, DuplicatesDetector selectionDialog, LabelMode labelMode ) {
    this.resourceManager = new LocalResourceManager( JFaceResources.getResources( display ) );
    this.duplicatesDetector = selectionDialog;
    this.labelMode = labelMode;
    this.debugModelPresentation = DebugUITools.newDebugModelPresentation();
  }

  @Override
  public Image getImage( Object element ) {
    Image result = debugModelPresentation.getImage( element );
    if( isRunning( element ) ) {
      result = decorateImage( result );
    }
    return result;
  }

  @Override
  public String getText( Object element ) {
    return getStyledText( element ).toString();
  }

  @Override
  public StyledString getStyledText( Object element ) {
    StyledString result;
    if( element instanceof ILaunchConfiguration ) {
      result = getStyledString( ( ILaunchConfiguration )element );
    } else {
      result = new StyledString( String.valueOf( element ) );
    }
    return result;
  }

  @Override
  public void dispose() {
    debugModelPresentation.dispose();
    super.dispose();
  }

  protected boolean isRunning( Object element ) {
    boolean result = false;
    if( element instanceof ILaunchConfiguration ) {
      result = LaunchConfigs.isRunning( ( ILaunchConfiguration )element );
    }
    return result;
  }

  private Image decorateImage( Image image ) {
    Image result = null;
    if( image != null ) {
      ImageDescriptor imageDescriptor = Images.getImageDescriptor( RUNNING );
      DecorationOverlayIcon overlay = new DecorationOverlayIcon( image, imageDescriptor, BOTTOM_RIGHT );
      result = resourceManager.createImage( overlay );
    }
    return result;
  }

  private StyledString getStyledString( ILaunchConfiguration launchConfig ) {
    StyledString result;
    if( labelMode == LIST ) {
      result = getListStyledString( launchConfig );
    } else {
      result = getDetailStyledString( launchConfig );
    }
    return result;
  }

  private StyledString getListStyledString( ILaunchConfiguration launchConfig ) {
    StyledString result;
    result = new StyledString( debugModelPresentation.getText( launchConfig ) );
    if( duplicatesDetector.isDuplicateElement( launchConfig ) ) {
      result.append( " - ", ADDITIONAL_INFO_STYLER );
      if( launchConfig.getFile() != null ) {
        result.append( getContainerName( launchConfig ), ADDITIONAL_INFO_STYLER );
      } else {
        result.append( getTypeName( launchConfig ), ADDITIONAL_INFO_STYLER );
      }
    }
    return result;
  }

  private StyledString getDetailStyledString( ILaunchConfiguration launchConfig ) {
    StyledString result;
    result = new StyledString( debugModelPresentation.getText( launchConfig ) );
    result.append( " - ", ADDITIONAL_INFO_STYLER );
    result.append( getTypeName( launchConfig ), ADDITIONAL_INFO_STYLER );
    if( launchConfig.getFile() != null ) {
      result.append( " (", ADDITIONAL_INFO_STYLER );
      result.append( getContainerName( launchConfig ), ADDITIONAL_INFO_STYLER );
      result.append( ")", ADDITIONAL_INFO_STYLER );
    }
    return result;
  }

  private static String getTypeName( ILaunchConfiguration configuration ) {
    String result = "Unknown Launch Configuration Type";
    try {
      result = configuration.getType().getName();
    } catch( CoreException ignore ) {
    }
    return result;
  }

  private static String getContainerName( ILaunchConfiguration launchConfig ) {
    return launchConfig.getFile().getParent().getFullPath().makeRelative().toPortableString();
  }
}
