package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.google.common.collect.Iterables.getFirst;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class JUnitStatusContributionItemPDETest {

  private static final String LOCATION
    = "toolbar:org.eclipse.ui.trim.status";

  @Test
  public void testExtension() {
    Extension extension = new RegistryAdapter()
      .readExtension( "org.eclipse.ui.menus" )
      .thatMatches( new LocationUriPredicate() )
      .process();

    Extension toolbarElement = getToolBarElement( extension );
    Extension controlElement = getControlElement( extension );
    assertThat( extension.getChildren( "toolbar" ) ).hasSize( 1 );
    assertThat( toolbarElement.getChildren( "control" ) ).hasSize( 1 );
    assertThat( toolbarElement.getAttribute( "label" ) ).isEqualTo( "JUnit" );
    assertThat( controlElement.getAttribute( "id" ) ).isNotNull();
    assertThat( controlElement.createExecutableExtension( JUnitStatusContributionItem.class ) ).isNotNull();
  }

  private static Extension getControlElement( Extension extension ) {
    return getFirst( getToolBarElement( extension ).getChildren( "control" ), null );
  }

  private static Extension getToolBarElement( Extension extension ) {
    return getFirst( extension.getChildren( "toolbar" ), null );
  }

  private static class LocationUriPredicate implements Predicate {
    @Override
    public boolean apply( Extension input ) {
      String attribute = input.getAttribute( "locationURI" );
      return LOCATION.equals( attribute );
    }
  }
}
