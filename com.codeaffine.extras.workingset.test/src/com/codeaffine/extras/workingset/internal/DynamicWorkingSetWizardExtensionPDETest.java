package com.codeaffine.extras.workingset.internal;

import static com.google.common.collect.Iterables.getFirst;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.ui.INewWizard;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicates;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.extras.workingset.internal.DynamicWorkingSetWizard;


public class DynamicWorkingSetWizardExtensionPDETest {

  private static final String KEYWORD_ID = "com.codeaffine.extras.ide.internal.NewWorkingSetWizardKeyword";

  @Test
  public void testWizardExtension() {
    Extension extension = getDynamicWorkingSetWizard();

    assertThat( extension.getAttribute( "name" ) ).isNotEmpty();
    assertThat( getWizardClass( extension ) ).isInstanceOf( DynamicWorkingSetWizard.class );
    assertThat( extension.getAttribute( "icon" ) ).isEqualTo( "icons/etool16/new-working-set.gif" );
    assertThat( extension.getAttribute( "category" ) ).isEqualTo( "org.eclipse.ui.Basic" );
    assertThat( extension.getAttribute( "canFinishEarly" ) ).isEqualTo( "false" );
    assertThat( extension.getAttribute( "hasPages" ) ).isEqualTo( "true" );
    assertThat( getDescriptions( extension ).size() ).isEqualTo( 1 );
    assertThat( getFirst( getDescriptions( extension ), null ).getValue() ).isNotEmpty();
    assertThat( getKeywordReferences( extension ).size() ).isEqualTo( 1 );
    assertThat( getFirstKeywordReference( extension ).getAttribute( "id" ) ).isEqualTo( KEYWORD_ID );
  }

  @Test
  public void testKeywordExtension() {
    Extension extension = getWizardKeyword();

    assertThat( extension.getAttribute( "label" ) ).isEqualTo( "Working Set" );
  }

  private static Collection<Extension> getDescriptions( Extension extension ) {
    return extension.getChildren( "description" );
  }

  private static Collection<Extension> getKeywordReferences( Extension extension ) {
    return extension.getChildren( "keywordReference" );
  }

  private static Extension getFirstKeywordReference( Extension extension ) {
    return getFirst( getKeywordReferences( extension ), null );
  }

  private static INewWizard getWizardClass( Extension extension ) {
    return extension.createExecutableExtension( "class", INewWizard.class );
  }

  private static Extension getDynamicWorkingSetWizard() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.newWizards" )
      .thatMatches( Predicates.attribute( "id", DynamicWorkingSetWizard.ID ) )
      .process();
  }

  private static Extension getWizardKeyword() {
    return new RegistryAdapter()
      .readExtension( "org.eclipse.ui.keywords" )
      .thatMatches( Predicates.attribute( "id", KEYWORD_ID ) )
      .process();
  }

}
