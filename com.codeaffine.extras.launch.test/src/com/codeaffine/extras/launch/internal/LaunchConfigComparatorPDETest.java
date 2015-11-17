package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchManagerHelper;

public class LaunchConfigComparatorPDETest {

  private ILaunchConfigurationWorkingCopy launchConfig1;
  private ILaunchConfigurationWorkingCopy launchConfig2;
  private LaunchConfigSelectionHistory launchConfigHistory;
  private List<ILaunchConfiguration> historyItems;

  @Before
  public void setUp() throws CoreException {
    launchConfig1 = LaunchManagerHelper.createLaunchConfig();
    launchConfig2 = LaunchManagerHelper.createLaunchConfig();
    historyItems = new LinkedList<>();
    launchConfigHistory = mock( LaunchConfigSelectionHistory.class );
    when( launchConfigHistory.getHistoryItems() ).thenReturn( historyItems.toArray() );
  }

  @Test
  public void testCompareWhenBothLaunchConfigsInHistory() {
    launchConfig1.rename( "z" + launchConfig1.getName() );
    launchConfig2.rename( "a" + launchConfig2.getName() );
    addToHistory( launchConfig1 );
    addToHistory( launchConfig2 );

    int compareResult = compare( launchConfig1, launchConfig2 );

    assertThat( compareResult ).isEqualTo( -1 );
  }

  @Test
  public void testCompareWhenOneLaunchConfigInHistory() {
    launchConfig1.rename( "z" + launchConfig1.getName() );
    launchConfig2.rename( "a" + launchConfig2.getName() );
    addToHistory( launchConfig1 );

    int compareResult = compare( launchConfig1, launchConfig2 );

    assertThat( compareResult ).isGreaterThan( 0 );
  }

  @Test
  public void testCompareWhenBothLaunchConfigsNotInHistory() {
    launchConfig1.rename( "z" + launchConfig1.getName() );
    launchConfig2.rename( "a" + launchConfig2.getName() );

    int compareResult = compare( launchConfig1, launchConfig2 );

    assertThat( compareResult ).isGreaterThan( 0 );
  }

  private void addToHistory( ILaunchConfigurationWorkingCopy launchConfig ) {
    historyItems.add( launchConfig );
    when( launchConfigHistory.contains( launchConfig ) ).thenReturn( true );
    when( launchConfigHistory.getHistoryItems() ).thenReturn( historyItems.toArray() );
  }

  private int compare( ILaunchConfigurationWorkingCopy launchConfig1,
                       ILaunchConfigurationWorkingCopy launchConfig2 )
  {
    LaunchConfigComparator comparator = new LaunchConfigComparator( launchConfigHistory );
    return comparator.compare( launchConfig1, launchConfig2 );
  }
}
