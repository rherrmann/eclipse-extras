package com.codeaffine.extras.launch.internal;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.google.common.collect.Iterables;

public class LaunchModeDropDownAction extends Action implements IMenuCreator {

  private final LaunchModeSetting launchModeSetting;
  private final LaunchModeAction[] launchModeActions;
  private Menu menu;

  public LaunchModeDropDownAction( LaunchModeSetting launchModeSetting ) {
    super( "&Default Launch Mode", IAction.AS_DROP_DOWN_MENU );
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
    List<LaunchModeAction> launchModeActions = newLinkedList();
    ILaunchMode[] launchModes = launchModeSetting.getLaunchManager().getLaunchModes();
    for( ILaunchMode launchMode : launchModes ) {
      LaunchModeAction action = new LaunchModeAction( launchModeSetting, launchMode );
      action.addPropertyChangeListener( new LaunchActionPropertyChangeListner() );
      launchModeActions.add( action );
    }
    Collections.sort( launchModeActions, new LaunchModeActionComparator() );
    return Iterables.toArray( launchModeActions, LaunchModeAction.class );
  }

  private class LaunchActionPropertyChangeListner implements IPropertyChangeListener {
    @Override
    public void propertyChange( PropertyChangeEvent event ) {
      if( IAction.CHECKED.equals( event.getProperty() ) ) {
        for( LaunchModeAction launchModeAction : launchModeActions ) {
          launchModeAction.update();
        }
      }
      firePropertyChange( event );
    }
  }

}