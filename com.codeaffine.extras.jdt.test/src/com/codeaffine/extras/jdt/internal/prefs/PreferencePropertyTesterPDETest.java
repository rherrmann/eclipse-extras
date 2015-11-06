package com.codeaffine.extras.jdt.internal.prefs;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static com.codeaffine.extras.jdt.internal.prefs.PreferencePropertyTester.ID;
import static com.codeaffine.extras.jdt.internal.prefs.PreferencePropertyTester.IS_TRUE;
import static com.codeaffine.extras.jdt.internal.prefs.PreferencePropertyTester.NAMESPACE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.function.Predicate;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.services.IServiceLocator;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.eclipse.core.runtime.test.util.ExtensionAssert;

public class PreferencePropertyTesterPDETest {

  private static final String PREF_NAME = "preference_name";

  private IServiceLocator serviceLocator;
  private PreferenceStore preferenceStore;
  private PreferencePropertyTester propertyTester;

  @Before
  public void setUp() {
    serviceLocator = mock( IServiceLocator.class );
    preferenceStore = new PreferenceStore();
    propertyTester = new PreferencePropertyTester( preferenceStore );
  }

  @Test
  public void testExtension() {
    Extension actual = readPropertyTesterExtension();

    ExtensionAssert.assertThat( actual )
      .hasAttributeValue( "namespace", NAMESPACE )
      .hasAttributeValue( "properties", IS_TRUE )
      .isInstantiable( "class", PreferencePropertyTester.class );
  }

  @Test
  public void testPropertyWhenTrue() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new String[] { PREF_NAME }, TRUE );

    assertThat( test ).isTrue();
  }

  @Test
  public void testPropertyWhenFalse() {
    preferenceStore.setValue( PREF_NAME, false );

    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new String[] { PREF_NAME }, FALSE );

    assertThat( test ).isTrue();
  }

  @Test
  public void testPropertyWhenNotSet() {
    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new String[] { PREF_NAME }, FALSE );

    assertThat( test ).isTrue();
  }

  @Test
  public void testTestWithNonServiceLocatorReceiver() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( new Object(), IS_TRUE, new String[] { PREF_NAME }, TRUE );

    assertThat( test ).isFalse();
  }

  @Test
  public void testTestWithUnknownProperty() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( serviceLocator, "foo", new String[] { PREF_NAME }, TRUE );

    assertThat( test ).isFalse();
  }

  @Test(expected=NullPointerException.class)
  public void testTestWithWithArgumentsNull() {
    propertyTester.test( serviceLocator, IS_TRUE, null, TRUE );
  }

  @Test
  public void testTestWithArgumentsEmpty() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new String[ 0 ], TRUE );

    assertThat( test ).isFalse();
  }

  @Test
  public void testTestWithArgumentsNotString() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new Object[] { new Object() }, TRUE );

    assertThat( test ).isFalse();
  }

  @Test
  public void testTestWithExpectedValueNull() {
    preferenceStore.setValue( PREF_NAME, true );

    boolean test = propertyTester.test( serviceLocator, IS_TRUE, new Object[] { PREF_NAME }, null );

    assertThat( test ).isFalse();
  }

  private static Extension readPropertyTesterExtension() {
    return readExtension( "org.eclipse.core.expressions.propertyTesters", attribute( "id", ID ) );
  }

  private static Extension readExtension( String extensionPoint, Predicate<Extension> predicate ) {
    return new RegistryAdapter().readExtension( extensionPoint ).thatMatches( predicate ).process();
  }
}
