package com.codeaffine.extras.launch.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchMode;


public class LaunchModeHelper {

  public static final String TEST_LAUNCH_MODE = "com.codeaffine.extras.ide.test.TestLaunchMode";

  public static ILaunchMode createLaunchMode( String identifier ) {
    return createLaunchMode( identifier, "Label of " + identifier );
  }

  public static ILaunchMode createLaunchMode( String identifier, String label ) {
    ILaunchMode result = mock( ILaunchMode.class );
    when( result.getIdentifier() ).thenReturn( identifier );
    when( result.getLabel() ).thenReturn( label );
    return result;
  }

}
