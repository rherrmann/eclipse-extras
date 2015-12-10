package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.LaunchConfigSelectionHistory;
import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigSelectionHistoryPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private LaunchConfigSelectionHistory history;
  private ILaunchConfiguration launchConfig;

  @Before
  public void setUp() throws CoreException {
    launchConfig = launchConfigRule.createLaunchConfig().doSave();
    history = new LaunchConfigSelectionHistory();
  }

  @Test
  public void testRestoreFromMemento() {
    Object restoredItem = history.restoreItemFromMemento( XMLMemento.createWriteRoot( "foo" ) );

    assertThat( restoredItem ).isNull();
  }

  @Test
  public void testStoreItemToMemento() {
    IMemento memento = mock( IMemento.class );

    history.storeItemToMemento( new Object(), memento );

    verifyZeroInteractions( memento );
  }

  @Test
  public void testGetHistoryItems() {
    runLaunchConfig();

    Object[] historyItems = history.getHistoryItems();

    assertThat( historyItems ).containsOnly( launchConfig );
  }

  @Test
  public void testGetHistoryItemsTwice() {
    runLaunchConfig();

    Object[] historyItems1 = history.getHistoryItems();
    Object[] historyItems2 = history.getHistoryItems();

    assertThat( historyItems1 ).isNotSameAs( historyItems2 );
    assertThat( historyItems1[ 0 ] ).isEqualTo( historyItems2[ 0 ] );
  }

  @Test
  public void testGetHistoryItemsAfterRunAndDebugSameLaunchConfig() {
    runLaunchConfig();
    debugLaunchConfig();

    Object[] historyItems = history.getHistoryItems();

    assertThat( historyItems ).containsOnly( launchConfig );
  }

  @Test
  public void testGetHistoryItemsWhenNoHistory() {
    Object[] historyItems = history.getHistoryItems();

    assertThat( historyItems ).isEmpty();
  }

  @Test
  public void testIsEmpty() {
    runLaunchConfig();

    boolean empty = history.isEmpty();

    assertThat( empty ).isFalse();
  }

  @Test
  public void testIsEmptyWhenNoHistory() {
    boolean empty = history.isEmpty();

    assertThat( empty ).isTrue();
  }

  @Test
  public void testContains() {
    runLaunchConfig();

    boolean contains = history.contains( launchConfig );

    assertThat( contains ).isTrue();
  }

  @Test
  public void testContainsWhenNoHistory() {
    boolean contains = history.contains( launchConfig );

    assertThat( contains ).isFalse();
  }

  @Test
  public void testContainsWithNullArgument() {
    boolean contains = history.contains( null );

    assertThat( contains ).isFalse();
  }

  @Test
  public void testRemove() {
    runLaunchConfig();
    Object[] historyItems = history.getHistoryItems();

    history.remove( historyItems[ 0 ] );

    assertThat( history.getHistoryItems() ).extracting( "name" ).doesNotContain( launchConfig.getName() );
  }

  @Test
  public void testRemoveWithNonLaunchConfig() {
    boolean removed = history.remove( new Object() );

    assertThat( removed ).isFalse();
  }

  @Test
  public void testRemoveWithNonFavouriteLaunchConfig() {
    boolean removed = history.remove( launchConfig );

    assertThat( removed ).isFalse();
  }

  private void runLaunchConfig() {
    DebugUITools.launch( launchConfig, RUN_MODE );
  }

  private void debugLaunchConfig() {
    DebugUITools.launch( launchConfig, DEBUG_MODE );
  }
}
