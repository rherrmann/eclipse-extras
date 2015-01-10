package com.codeaffine.extras.platform.internal.openwith;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.extras.platform.internal.openwith.OpenWithQuickMenuHandler;


public class OpenWithQuickMenuKeyBindingPDETest {

  private static final String BINDINGS_EP = "org.eclipse.ui.bindings";
  private static final String SCHEME_ID = "schemeId";
  private static final String COMMAND_ID = "commandId";
  private static final String CONTEXT_ID = "contextId";
  private static final String DEFAULT_SCHEME_ID = "org.eclipse.ui.defaultAcceleratorConfiguration";

  private static final String PLATFORM = "platform";
  private static final String SEQUENCE = "sequence";
  private static final String KEY_SEQUENCE = "M2+F3";
  private static final String CARBON = "carbon";

  @Test
  public void testGeneralKeyBinding() {
    Extension extension = readKeyBindingExtension( new GeneralKeyBindingPredicate() );

    assertThat( extension.getAttribute( SCHEME_ID ) ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( extension.getAttribute( COMMAND_ID ) ).isEqualTo( OpenWithQuickMenuHandler.COMMAND_ID );
    assertThat( extension.getAttribute( CONTEXT_ID ) ).isNull();
    assertThat( extension.getAttribute( PLATFORM ) ).isNull();
  }

  @Test
  public void testCarbonKeyBinding() {
    Extension extension = readKeyBindingExtension( new CarbonKeyBindingPredicate() );

    assertThat( extension.getAttribute( SCHEME_ID ) ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( extension.getAttribute( COMMAND_ID ) ).isNull();
    assertThat( extension.getAttribute( CONTEXT_ID ) ).isNull();
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
      return KEY_SEQUENCE.equals( sequence ) && CARBON.equals( platform );
    }
  }
}
