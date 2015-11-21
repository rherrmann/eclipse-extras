package com.codeaffine.extras.launch.internal.cleanup;

import static java.util.stream.Collectors.joining;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;


public class LaunchConfigTypeSerializer {

  private static final String DELIMITER = ",";
  private static final String ESCAPED_DELIMITER = "\\" + DELIMITER;

  private final ILaunchManager launchManager;

  public LaunchConfigTypeSerializer( ILaunchManager launchManager ) {
    this.launchManager = launchManager;
  }

  public String serialize( ILaunchConfigurationType... types ) {
    return Stream.of( types ).map( type -> type.getIdentifier() ).collect( joining( DELIMITER ) );
  }

  public ILaunchConfigurationType[] deserialize( String string ) {
    return Stream.of( string.split( ESCAPED_DELIMITER ) )
      .map( typeId -> findType( typeId ) )
      .filter( type -> type != null )
      .toArray( ILaunchConfigurationType[]::new );
  }

  private ILaunchConfigurationType findType( String typeId ) {
    return Stream.of( launchManager.getLaunchConfigurationTypes() )
      .filter( type -> Objects.equals( type.getIdentifier(), typeId ) )
      .findFirst()
      .orElse( null );
  }
}
