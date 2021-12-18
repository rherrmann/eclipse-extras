package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.junitstatus.ActivateJUnitViewOnFailureAction.PREF_SHOW_ON_ERROR_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.internal.junit.JUnitPreferencesConstants;
import org.eclipse.jface.action.IAction;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class ActivateJUnitViewOnFailureActionTest {

  private IEclipsePreferences preferences;
  private ActivateJUnitViewOnFailureAction action;

  @Before
  public void setUp() {
    preferences = mock(IEclipsePreferences.class);
    when(preferences.getBoolean(PREF_SHOW_ON_ERROR_ONLY, false)).thenReturn(true);
    action = new ActivateJUnitViewOnFailureAction(preferences);
  }

  @Test
  public void testPreferenceConstant() {
    assertThat(PREF_SHOW_ON_ERROR_ONLY).isEqualTo(JUnitPreferencesConstants.SHOW_ON_ERROR_ONLY);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullArgument() {
    new ActivateJUnitViewOnFailureAction(null);
  }

  @Test
  public void testGetStyle() {
    int style = action.getStyle();

    assertThat(style).isEqualTo(IAction.AS_CHECK_BOX);
  }

  @Test
  public void testGetText() {
    String text = action.getText();

    assertThat(text).isNotEmpty();
  }

  @Test
  public void testInitialCheckState() {
    boolean checked = action.isChecked();

    assertThat(checked).isTrue();
  }

  @Test
  public void testRun() {
    action.run();

    verify(preferences).putBoolean(PREF_SHOW_ON_ERROR_ONLY, false);
  }
}
