package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.prefs.PreferencePropertyTester.PROP_IS_TRUE;
import static com.codeaffine.extras.jdt.internal.prefs.WorkspaceScopePreferences.PREF_SHOW_JUNIT_STATUS_BAR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class JUnitStatusContributionItemPDETest {

  private static final String LOCATION = "toolbar:org.eclipse.ui.trim.status";
  private static final String TOOL_BAR_ID = "com.codeaffine.extras.jdt.internal.JUnitStatusToolBar";

  @Test
  public void testExtension() {
    Extension extension = new RegistryAdapter()
      .readExtension( "org.eclipse.ui.menus" )
      .thatMatches( new JUnitStatusBarPredicate() )
      .process();

    Extension toolbarElement = getToolBarElement( extension );
    Extension controlElement = getControlElement( extension );
    Extension visibleWhenElement = getVisibleWhenElement( extension );
    Extension visibleTestElement = getVisibleTestElement( extension );
    assertThat( extension.getChildren( "toolbar" ) ).hasSize( 1 );
    assertThat( toolbarElement.getChildren( "control" ) ).hasSize( 1 );
    assertThat( toolbarElement.getAttribute( "label" ) ).isEqualTo( "JUnit" );
    assertThat( controlElement.getAttribute( "id" ) ).isNotNull();
    assertThat( controlElement.createExecutableExtension( JUnitStatusContributionItem.class ) ).isNotNull();
    assertThat( visibleWhenElement.getAttribute( "checkEnabled" ) ).isEqualTo( "false" );
    assertThat( visibleTestElement.getAttribute( "args" ) ).isEqualTo( PREF_SHOW_JUNIT_STATUS_BAR );
    assertThat( visibleTestElement.getAttribute( "forcePluginActivation" ) ).isEqualTo( "true" );
    assertThat( visibleTestElement.getAttribute( "property" ) ).isEqualTo( PROP_IS_TRUE );
    assertThat( visibleTestElement.getAttribute( "value" ) ).isEqualTo( "true" );
  }

  private static Extension getControlElement( Extension extension ) {
    return getFirst( getToolBarElement( extension ).getChildren( "control" ) );
  }

  private static Extension getToolBarElement( Extension extension ) {
    return getFirst( extension.getChildren( "toolbar" ) );
  }

  private static Extension getVisibleWhenElement( Extension extension ) {
    return getFirst( getControlElement( extension ).getChildren( "visibleWhen" ) );
  }

  private static Extension getVisibleTestElement( Extension extension ) {
    Extension withElement = getFirst( getVisibleWhenElement( extension ).getChildren( "with" ) );
    return getFirst( withElement.getChildren( "test" ) );
  }

  private static <T> T getFirst( Collection<T> collection ) {
    return collection.stream().findFirst().orElse( null );
  }

  private static class JUnitStatusBarPredicate implements Predicate {
    @Override
    public boolean apply( Extension input ) {
      return isToolBarContribution( input ) && containsJUnitSttausBar( input );
    }

    private static boolean isToolBarContribution( Extension extension ) {
      return LOCATION.equals( extension.getAttribute( "locationURI" ) );
    }

    private static boolean containsJUnitSttausBar( Extension extension ) {
      Collection<Extension> toolBars = extension.getChildren( "toolbar" );
      return toolBars.stream().anyMatch( element -> TOOL_BAR_ID.equals( getId( element ) ) );
    }

    private static String getId( Extension element ) {
      return element.getAttribute( "id" );
    }
  }

}
