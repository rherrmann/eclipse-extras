package com.codeaffine.extras.platform.internal.openwith;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.actions.QuickMenuCreator;


public class OpenWithQuickMenu {

  private final IWorkbenchPage workbenchPage;
  private final IFile file;

  public OpenWithQuickMenu( IWorkbenchPage workbenchPage, IFile file ) {
    this.workbenchPage = workbenchPage;
    this.file = file;
  }

  public void show() {
    Control focusControl = getFocusControl();
    if( focusControl != null && !focusControl.isDisposed() ) {
      Point location = computeMenuLocation( focusControl );
      if( location != null ) {
        show( focusControl, location );
      }
    }
  }

  private void show( Control focusControl, Point location ) {
    Menu quickMenu = new Menu( focusControl.getShell() );
    OpenWithMenu openWithMenu = new OpenWithMenu( workbenchPage, file );
    openWithMenu.fill( quickMenu, 0 );
    quickMenu.setLocation( location );
    quickMenu.addListener( SWT.Hide, new MenuCloseListener( openWithMenu ) );
    quickMenu.setVisible( true );
  }

  private Control getFocusControl() {
    Display display = workbenchPage.getWorkbenchWindow().getShell().getDisplay();
    Control focusControl = display.getFocusControl();
    return focusControl;
  }

  private static Point computeMenuLocation( Control focusControl ) {
    MenuLocationComputer menuLocationComputer = new MenuLocationComputer();
    return menuLocationComputer.computeMenuLocation( focusControl );
  }

  private static class MenuCloseListener implements Listener {
    private final OpenWithMenu openWithMenu;

    private MenuCloseListener( OpenWithMenu openWithMenu ) {
      this.openWithMenu = openWithMenu;
    }

    @Override
    public void handleEvent( Event event ) {
      event.display.asyncExec( new Runnable() {
        @Override
        public void run() {
          openWithMenu.dispose();
        }
      } );
    }
  }

  private static class MenuLocationComputer extends QuickMenuCreator {
    @Override
    protected void fillMenu( IMenuManager menu ) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Point computeMenuLocation( Control focusControl ) {
      return super.computeMenuLocation( focusControl );
    }
  }
}
