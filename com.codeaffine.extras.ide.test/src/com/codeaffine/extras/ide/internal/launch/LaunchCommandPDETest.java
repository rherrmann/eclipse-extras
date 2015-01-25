package com.codeaffine.extras.ide.internal.launch;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.commands.IHandler;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.extras.ide.internal.launch.LaunchHandler;


public class LaunchCommandPDETest {

  @Test
  public void testExtension() {
    Extension extension = readCommandExtension();

    assertThat( extension.getAttribute( "name" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "description" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "categoryId" ) ).isEqualTo( "org.eclipse.debug.ui.category.run" );
    IHandler handler = extension.createExecutableExtension( "defaultHandler", IHandler.class );
    assertThat( handler ).isInstanceOf( LaunchHandler.class );
  }

  private static Extension readCommandExtension() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.commands" )
      .thatMatches( Predicates.attribute( "id", LaunchHandler.COMMAND_ID ) )
      .process();
  }

}
