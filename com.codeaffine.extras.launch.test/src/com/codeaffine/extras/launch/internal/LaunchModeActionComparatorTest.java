package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.launch.test.LaunchModeHelper.createLaunchMode;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.LaunchModeAction;
import com.codeaffine.extras.launch.internal.LaunchModeActionComparator;

public class LaunchModeActionComparatorTest {

  private List<LaunchModeAction> launchModes;
  private LaunchModeActionComparator comparator;

  @Test
  public void testRunBeforeDebugBeforeAny() {
    LaunchModeAction profile = addLaunchMode( ILaunchManager.PROFILE_MODE, "Profile" );
    LaunchModeAction debug = addLaunchMode( ILaunchManager.DEBUG_MODE, "Debug" );
    LaunchModeAction run = addLaunchMode( RUN_MODE, "Run" );

    Collections.sort( launchModes, comparator );

    assertThat( launchModes ).containsExactly( run, debug, profile );
  }

  @Test
  public void testAlphabeticalOrder() {
    LaunchModeAction foo = addLaunchMode( "foo", "Foo" );
    LaunchModeAction bar = addLaunchMode( "bar", "Bar" );

    Collections.sort( launchModes, comparator );

    assertThat( launchModes ).containsExactly( bar, foo );
  }

  @Before
  public void setUp() {
    launchModes = newArrayList();
    comparator = new LaunchModeActionComparator();
  }

  private LaunchModeAction addLaunchMode( String identifier, String label ) {
    ILaunchMode launchMode = createLaunchMode( identifier, label );
    LaunchModeAction launchModeAction = mock( LaunchModeAction.class );
    when( launchModeAction.getLaunchMode() ).thenReturn( launchMode );
    launchModes.add( launchModeAction );
    return launchModeAction;
  }
}
