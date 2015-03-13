package com.codeaffine.extras.ide.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;


public class DynamicWorkingSetExtensionPDETest {

  @Test
  public void testWorkingSetExtension() {
    Extension extension = getDynamicWorkingSetExtension();

    assertThat( extension.getAttribute( "name" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "description" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "icon" ) ).isEqualTo( "icons/obj16/working-set.png" );
    assertThat( getPageClass( extension ) ).isInstanceOf( DynamicWorkingSetPage.class );
    assertThat( getUpdaterClass( extension ) ).isInstanceOf( DynamicWorkingSetUpdater.class );
    assertThat( extension.getAttribute( "elementAdapterClass" ) ).isNull();
  }

  private static Extension getDynamicWorkingSetExtension() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.workingSets" )
      .thatMatches( Predicates.attribute( "id", DynamicWorkingSet.ID ) )
      .process();
  }

  private static IWorkingSetPage getPageClass( Extension extension ) {
    return extension.createExecutableExtension( "pageClass", IWorkingSetPage.class );
  }

  private static IWorkingSetUpdater getUpdaterClass( Extension extension ) {
    return extension.createExecutableExtension( "updaterClass", IWorkingSetUpdater.class );
  }
}
