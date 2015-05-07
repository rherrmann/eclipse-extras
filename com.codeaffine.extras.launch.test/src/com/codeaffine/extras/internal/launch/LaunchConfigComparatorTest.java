package com.codeaffine.extras.internal.launch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Test;

import com.codeaffine.extras.internal.launch.LaunchConfigComparator;

public class LaunchConfigComparatorTest {

  @Test
  public void testCompare() {
    ILaunchConfiguration launchConfig1 = createLaunchConfiguration( "z" );
    ILaunchConfiguration launchConfig2 = createLaunchConfiguration( "a" );

    LaunchConfigComparator comparator = new LaunchConfigComparator();
    int compareResult = comparator.compare( launchConfig1, launchConfig2 );

    assertThat( compareResult ).isGreaterThan( 0 );
  }

  private static ILaunchConfiguration createLaunchConfiguration( String name ) {
    ILaunchConfiguration result = mock( ILaunchConfiguration.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }
}
