package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.OpenLaunchDialogHander;

public class StartLaunchConfigMenuPDETest {

  private static final String MENU_ITEM_ID = "com.codeaffine.extras.launch.internal.StartLaunchConfigMenuItem";

  private MApplication application;

  @Before
  public void setUp() {
    application = getEclipseContext().get( MApplication.class );
  }

  @Test
  public void testStartLaunchConfigMenuContribution() {
    List<MMenuContribution> menuContributions = application.getMenuContributions();
    MHandledMenuItem element = menuContributions.stream()
      .flatMap( menuContribution -> menuContribution.getChildren().stream() )
      .filter( menuElement -> MENU_ITEM_ID.equals( menuElement.getElementId() ) )
      .findFirst()
      .map( MHandledMenuItem.class::cast )
      .orElse( null );

    assertThat( element.getIconURI() ).endsWith( Images.START_LAUNCH_CONFIGURATION );
    assertThat( element.getLabel() ).isNotEmpty();
    assertThat( element.getCommand().getElementId() ).isEqualTo( OpenLaunchDialogHander.COMMAND_ID );
  }

  @SuppressWarnings("cast")
  private static IEclipseContext getEclipseContext() {
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    return ( IEclipseContext )activeWorkbenchWindow.getService( IEclipseContext.class );
  }
}
