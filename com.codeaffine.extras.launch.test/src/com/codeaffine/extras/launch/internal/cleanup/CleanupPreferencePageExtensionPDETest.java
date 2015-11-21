package com.codeaffine.extras.launch.internal.cleanup;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static com.codeaffine.extras.launch.internal.cleanup.CleanupPreferencePage.ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.eclipse.core.runtime.test.util.ExtensionAssert;

public class CleanupPreferencePageExtensionPDETest {

  private static final String LAUNCHING_KEYWORD_ID = "org.eclipse.debug.ui.launching";
  private static final String CONTEXT_LAUNCHING_KEYWORD_ID = "org.eclipse.debug.ui.contextLaunching";

  @Test
  public void testExtension() {
    Extension extension = readPreferencePageExtension();

    ExtensionAssert.assertThat( extension )
      .hasAttributeValue( "category", "org.eclipse.debug.ui.LaunchingPreferencePage" )
      .hasAttributeValue( "class", CleanupPreferencePage.class.getName() )
      .hasChildWithAttributeValue( "id", LAUNCHING_KEYWORD_ID )
      .hasChildWithAttributeValue( "id", CONTEXT_LAUNCHING_KEYWORD_ID )
      .hasNonEmptyAttributeValueFor( "name" )
      .isInstantiable( CleanupPreferencePage.class );
  }

  @Test
  public void testNameEqulsTitle() {
    Extension extension = readPreferencePageExtension();

    String name = extension.getAttribute( "name" );
    String title = new CleanupPreferencePage().getTitle();

    assertThat( name ).isEqualTo( title );
  }

  @Test
  public void testLaunchingKeywordReferences() {
    Extension extension = readExtension( "org.eclipse.ui.keywords", attribute( "id", LAUNCHING_KEYWORD_ID ) );

    assertThat( extension ).isNotNull();
  }

  @Test
  public void testContextLaunchingKeywordReferences() {
    Extension extension = readExtension( "org.eclipse.ui.keywords", attribute( "id", CONTEXT_LAUNCHING_KEYWORD_ID ) );

    assertThat( extension ).isNotNull();
  }

  private static Extension readPreferencePageExtension() {
    return readExtension( "org.eclipse.ui.preferencePages", attribute( "id", ID ) );
  }

  private static Extension readExtension( String extensionPoint, Predicate<Extension> predicate ) {
    return new RegistryAdapter().readExtension( extensionPoint ).thatMatches( predicate ).process();
  }

}
