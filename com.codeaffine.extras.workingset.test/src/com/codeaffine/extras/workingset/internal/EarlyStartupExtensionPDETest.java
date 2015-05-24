package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IStartup;
import org.junit.Test;
import org.osgi.framework.BundleActivator;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.extras.workingset.internal.DynamicWorkingSetStartup;


public class EarlyStartupExtensionPDETest {

  @Test
  public void testStartupExtension() {
    Extension extension = getEarlyStartup();

    IStartup startup = extension.createExecutableExtension( "class", IStartup.class );
    assertThat( startup ).isInstanceOf( DynamicWorkingSetStartup.class );
    assertThat( startup ).isNotInstanceOf( BundleActivator.class );
  }

  private static Extension getEarlyStartup() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.startup" )
      .thatMatches( Predicates.attribute( "class", DynamicWorkingSetStartup.class.getName() ) )
      .process();
  }

}
