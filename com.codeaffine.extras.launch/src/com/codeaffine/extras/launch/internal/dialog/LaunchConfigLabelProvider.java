package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.DETAIL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;


public class LaunchConfigLabelProvider extends LabelProvider implements IStyledLabelProvider {

  public enum LabelMode {
    LIST,
    DETAIL
  }

  private final FilteredItemsSelectionDialog selectionDialog;
  private final LabelMode labelMode;
  private final IDebugModelPresentation debugModelPresentation;

  public LaunchConfigLabelProvider( FilteredItemsSelectionDialog selectionDialog, LabelMode labelMode  ) {
    this.selectionDialog = selectionDialog;
    this.labelMode = labelMode;
    this.debugModelPresentation = DebugUITools.newDebugModelPresentation();
  }

  @Override
  public Image getImage( Object element ) {
    return debugModelPresentation.getImage( element );
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

  private StyledString getStyledString( ILaunchConfiguration launchConfig ) {
    StyledString result;
    result = new StyledString( debugModelPresentation.getText( launchConfig ) );
    if( labelMode == DETAIL || selectionDialog.isDuplicateElement( launchConfig ) ) {
      result.append( " - ", StyledString.QUALIFIER_STYLER );
      result.append( getTypeName( launchConfig ), StyledString.QUALIFIER_STYLER );
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
}
