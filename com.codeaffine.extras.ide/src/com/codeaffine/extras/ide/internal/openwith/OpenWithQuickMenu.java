package com.codeaffine.extras.ide.internal.openwith;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.actions.QuickMenuCreator;

import com.codeaffine.eclipse.swt.util.UIThreadSynchronizer;


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
    quickMenu.addListener( SWT.Hide, createMenuCloseListener( openWithMenu ) );
    quickMenu.setVisible( true );
  }

  private MenuCloseListener createMenuCloseListener( OpenWithMenu openWithMenu ) {
    return new MenuCloseListener( workbenchPage.getWorkbenchWindow().getShell(), openWithMenu );
  }

  private Control getFocusControl() {
    return workbenchPage.getWorkbenchWindow().getShell().getDisplay().getFocusControl();
  }

  private static Point computeMenuLocation( Control focusControl ) {
    MenuLocationComputer menuLocationComputer = new MenuLocationComputer();
    return menuLocationComputer.computeMenuLocation( focusControl );
  }

  private static class MenuCloseListener implements Listener {
    private final Shell shell;
    private final OpenWithMenu openWithMenu;

    MenuCloseListener( Shell shell, OpenWithMenu openWithMenu ) {
      this.shell = shell;
      this.openWithMenu = openWithMenu;
    }

    @Override
    public void handleEvent( Event event ) {
      new UIThreadSynchronizer().asyncExec( shell, new Runnable() {
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
