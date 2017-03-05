package com.codeaffine.extras.ide.internal.resourcefilter;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class NestedProjectFilterExtensionPDETest {

  @Test
  public void testFilterMatcherExtension() {
    Extension extension = readFilterMatchersExtension();

    assertThat( extension.createExecutableExtension( NestedProjectFilter.class ) ).isNotNull();
    assertThat( extension.getAttribute( "name" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "description" ) ).isNotEmpty();
    assertThat( extension.getAttribute( "argumentType" ) ).isEqualTo( "none" );
    assertThat( extension.getAttribute( "ordering" ) ).isEqualTo( "first" );
  }

  private static Extension readFilterMatchersExtension() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.core.resources.filterMatchers" )
      .thatMatches( attribute( "id", NestedProjectFilter.ID ) )
      .process();
  }
}
