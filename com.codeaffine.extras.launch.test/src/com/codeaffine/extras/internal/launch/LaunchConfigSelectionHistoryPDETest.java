package com.codeaffine.extras.internal.launch;

import static com.codeaffine.extras.launch.test.LaunchManagerHelper.createLaunchConfig;
import static com.codeaffine.extras.launch.test.LaunchManagerHelper.deleteLaunchConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LaunchConfigSelectionHistoryPDETest {

  private LaunchConfigSelectionHistory history;
  private ILaunchConfiguration launchConfig;

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
    addLaunchConfigToHistory();

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
    addLaunchConfigToHistory();

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
    addLaunchConfigToHistory();

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

  @Before
  public void setUp() throws Exception {
    launchConfig = createLaunchConfig().doSave();
    history = new LaunchConfigSelectionHistory();
  }

  @After
  public void tearDown() throws CoreException {
    deleteLaunchConfig( launchConfig.getName() );
  }

  private void addLaunchConfigToHistory() {
    DebugUITools.launch( launchConfig, ILaunchManager.RUN_MODE );
  }
}
