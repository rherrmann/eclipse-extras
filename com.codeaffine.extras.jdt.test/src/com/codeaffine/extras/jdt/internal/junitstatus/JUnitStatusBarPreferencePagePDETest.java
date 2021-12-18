package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.eclipse.core.runtime.Predicates.attribute;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitStatusBarPreferencePage.ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;
import com.codeaffine.eclipse.core.runtime.test.util.ExtensionAssert;
import com.codeaffine.extras.jdt.internal.JDTExtrasPlugin;

public class JUnitStatusBarPreferencePagePDETest {

  private static final String JUNIT_KEYWORD_ID = "org.eclipse.jdt.junit.JUnit";

  private JUnitStatusBarPreferencePage preferencePage;

  @Before
  public void setUp() {
    preferencePage = new JUnitStatusBarPreferencePage();
  }

  @After
  public void tearDown() {
    preferencePage.dispose();
  }

  @Test
  public void testExtension() {
    Extension actual = readPreferencePageExtension();

    ExtensionAssert.assertThat(actual).hasAttributeValue("category", "org.eclipse.jdt.junit.preferences")
        .hasAttributeValue("class", JUnitStatusBarPreferencePage.class.getName())
        .hasChildWithAttributeValue("id", JUNIT_KEYWORD_ID).hasNonEmptyAttributeValueFor("name")
        .isInstantiable(JUnitStatusBarPreferencePage.class);
  }

  @Test
  public void testExtensionKeywordReferences() {
    Extension extension = readExtension("org.eclipse.ui.keywords", attribute("id", JUNIT_KEYWORD_ID));

    assertThat(extension).isNotNull();
  }

  @Test
  public void testDoGetPreferenceStore() {
    IPreferenceStore preferenceStore = preferencePage.doGetPreferenceStore();

    assertThat(preferenceStore).isEqualTo(JDTExtrasPlugin.getInstance().getPreferenceStore());
  }

  @Test
  public void testGetTitle() {
    String title = preferencePage.getTitle();

    assertThat(title).isNotEmpty();
  }

  private static Extension readPreferencePageExtension() {
    return readExtension("org.eclipse.ui.preferencePages", attribute("id", ID));
  }

  private static Extension readExtension(String extensionPoint, Predicate<Extension> predicate) {
    return new RegistryAdapter().readExtension(extensionPoint).thatMatches(predicate).process();
  }
}
