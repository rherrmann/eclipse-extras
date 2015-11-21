package com.codeaffine.extras.launch.internal.cleanup;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class LaunchConfigTypeFilter extends ViewerFilter {

  static final String EXTERNALTOOLS_BUILDER_TYPE_ID = "org.eclipse.ui.externaltools.builder";

  @Override
  public boolean select( Viewer viewer, Object parentElement, Object element ) {
    return select( ( ILaunchConfigurationType )element );
  }

  private static boolean select( ILaunchConfigurationType type ) {
    if( !type.isPublic() ) {
      return false;
    }
    if( EXTERNALTOOLS_BUILDER_TYPE_ID.equals( type.getCategory() ) ) {
      return false;
    }
    return true;
  }
}