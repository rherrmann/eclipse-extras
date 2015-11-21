package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IStartup;
import org.junit.Test;
import org.osgi.framework.BundleActivator;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class LaunchExtrasStartupPDETest {


  @Test
  public void testStartupExtension() {
    Extension extension = getEarlyStartup();

    IStartup startup = extension.createExecutableExtension( "class", IStartup.class );
    assertThat( startup ).isInstanceOf( LaunchExtrasStartup.class );
    assertThat( startup ).isNotInstanceOf( BundleActivator.class );
  }

  private static Extension getEarlyStartup() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.startup" )
      .thatMatches( Predicates.attribute( "class", LaunchExtrasStartup.class.getName() ) )
      .process();
  }

}
