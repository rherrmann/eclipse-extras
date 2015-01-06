package com.codeaffine.extras.platform.junitview;

import static com.google.common.collect.Iterables.getFirst;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;


public class ShowJUnitViewPDETest {

  private static final String BINDINGS_EP = "org.eclipse.ui.bindings";
  private static final String ID = "id";
  private static final String VALUE = "value";
  private static final String SCHEME_ID = "schemeId";
  private static final String PARAMETER = "parameter";
  private static final String VIEW_PARAMETER_NAME = "org.eclipse.ui.views.showView.viewId";
  private static final String JUNIT_VIEW_ID = "org.eclipse.jdt.junit.ResultView";
  private static final String DEFAULT_SCHEME_ID = "org.eclipse.ui.defaultAcceleratorConfiguration";

  private static final String PLATFORM = "platform";
  private static final String SEQUENCE = "sequence";
  private static final String KEY_SEQUENCE = "M2+M3+Q U";

  @Test
  public void testGeneralKeyBinding() {
    Extension extension = readKeyBindingExtension(new GeneralKeyBindingPredicate());

    assertThat( extension.getAttribute( SCHEME_ID ) ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( extension.getChildren( PARAMETER ) ).hasSize( 1 );
    Extension parameter = getFirst( extension.getChildren( PARAMETER ), null );
    assertThat( parameter.getAttribute( ID ) ).isEqualTo( VIEW_PARAMETER_NAME );
    assertThat( parameter.getAttribute( VALUE ) ).isEqualTo( JUNIT_VIEW_ID );
  }

  @Test
  public void testCarbonKeyBinding() {
    Extension extension = readKeyBindingExtension( new CarbonKeyBindingPredicate() );

    assertThat( extension.getAttribute( SCHEME_ID ) ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( extension.getChildren( PARAMETER ) ).isEmpty();
  }

  private static Extension readKeyBindingExtension( Predicate predicate ) {
    return new RegistryAdapter().readExtension( BINDINGS_EP ).thatMatches( predicate ).process();
  }

  private static class GeneralKeyBindingPredicate implements Predicate {
    @Override
    public boolean apply( Extension input ) {
      String sequence = input.getAttribute( SEQUENCE );
      String platform = input.getAttribute( PLATFORM );
      return KEY_SEQUENCE.equals( sequence ) && platform == null;
    }
  }

  private static class CarbonKeyBindingPredicate implements Predicate {
    @Override
    public boolean apply( Extension input ) {
      String sequence = input.getAttribute( SEQUENCE );
      String platform = input.getAttribute( PLATFORM );
      return KEY_SEQUENCE.equals( sequence ) && "carbon".equals( platform );
    }
  }
}
