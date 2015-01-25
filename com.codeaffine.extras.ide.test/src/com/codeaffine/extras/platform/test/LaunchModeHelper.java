package com.codeaffine.extras.platform.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.debug.core.ILaunchMode;


public class LaunchModeHelper {

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
