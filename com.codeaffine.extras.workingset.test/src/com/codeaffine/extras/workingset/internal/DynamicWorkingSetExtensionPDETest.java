package com.codeaffine.extras.workingset.internal;

import static com.codeaffine.extras.workingset.internal.WorkingSetExtrasPlugin.PLUGIN_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.ui.IWorkingSetElementAdapter;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.extras.test.util.ImageAssert;


public class DynamicWorkingSetExtensionPDETest {

  @Test
  public void testWorkingSetExtension() {
    Extension extension = getDynamicWorkingSetExtension();

    assertThat( extension.getAttribute( "name" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "description" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "icon" ) ).isEqualTo( "$nl$/icons/obj16/working-set.png" );
    ImageAssert.assertThat( PLUGIN_ID, extension.getAttribute( "icon" ) ).exists();
    assertThat( getPageClass( extension ) ).isInstanceOf( DynamicWorkingSetPage.class );
    assertThat( getUpdaterClass( extension ) ).isInstanceOf( DynamicWorkingSetUpdater.class );
    assertThat( getAdapterClass( extension ) ).isInstanceOf( DynamicWorkingSetElementAdapter.class );
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

  private static IWorkingSetElementAdapter getAdapterClass( Extension extension ) {
    return extension.createExecutableExtension( "elementAdapterClass", IWorkingSetElementAdapter.class );
  }
}
