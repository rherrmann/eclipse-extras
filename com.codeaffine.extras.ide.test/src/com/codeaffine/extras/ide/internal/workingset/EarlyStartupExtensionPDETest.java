package com.codeaffine.extras.ide.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IStartup;
import org.junit.Test;
import org.osgi.framework.BundleActivator;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;


public class EarlyStartupExtensionPDETest {

  @Test
  public void testKeywordExtension() {
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
