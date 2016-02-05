package com.codeaffine.extras.launch.internal.dialog;

import java.util.stream.Stream;

import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class LaunchModeDropDownAction extends Action implements IMenuCreator {

  private final LaunchModeSetting launchModeSetting;
  private final LaunchModeAction[] launchModeActions;
  private Menu menu;

  public LaunchModeDropDownAction( LaunchModeSetting launchModeSetting ) {
    super( "&Launch Mode", AS_DROP_DOWN_MENU );
    this.launchModeSetting = launchModeSetting;
    this.launchModeActions = createLaunchModeActions();
    setMenuCreator( this );
  }

  @Override
  public Menu getMenu( Menu parent ) {
    dispose();
    menu = new Menu( parent );
    for( LaunchModeAction launchModeAction : launchModeActions ) {
      new ActionContributionItem( launchModeAction ).fill( menu, -1 );
    }
    return menu;
  }

  @Override
  public Menu getMenu( Control parent ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispose() {
    if( menu != null ) {
      menu.dispose();
      menu = null;
    }
  }

  private LaunchModeAction[] createLaunchModeActions() {
    return Stream.of( launchModeSetting.getLaunchManager().getLaunchModes() )
      .map( launchMode -> createLaunchModeAction( launchMode ) )
      .sorted( new LaunchModeActionComparator() )
      .toArray( LaunchModeAction[]::new );
  }

  private LaunchModeAction createLaunchModeAction( ILaunchMode launchMode ) {
    LaunchModeAction result = new LaunchModeAction( launchModeSetting, launchMode );
    result.addPropertyChangeListener( this::launchModeActionPropertyChanged );
    return result;
  }

  private void launchModeActionPropertyChanged( PropertyChangeEvent event ) {
    if( IAction.CHECKED.equals( event.getProperty() ) ) {
      Stream.of( launchModeActions ).forEach( LaunchModeAction::update );
    }
    firePropertyChange( event );
  }

}