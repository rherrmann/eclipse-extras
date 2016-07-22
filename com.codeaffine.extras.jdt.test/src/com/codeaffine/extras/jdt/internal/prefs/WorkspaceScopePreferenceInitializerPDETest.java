package com.codeaffine.extras.jdt.internal.prefs;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class WorkspaceScopePreferenceInitializerPDETest {

  private static final String EXTENION_POINT_ID = "org.eclipse.core.runtime.preferences";

  @Test
  public void testExtension() {
    AbstractPreferenceInitializer actual = createExtension();

    assertThat( actual )
      .isInstanceOf( WorkspaceScopePreferenceInitializer.class );
  }

  private static AbstractPreferenceInitializer createExtension() {
    return new RegistryAdapter()
      .createExecutableExtension( EXTENION_POINT_ID, AbstractPreferenceInitializer.class )
      .thatMatches( attribute( "class", WorkspaceScopePreferenceInitializer.class.getName() ) )
      .process();
  }
}
