package com.codeaffine.extras.jdt.internal.prefs;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.services.IServiceLocator;

import com.codeaffine.extras.jdt.internal.JDTExtrasPlugin;


public class PreferencePropertyTester extends PropertyTester {

  public static final String ID = "com.codeaffine.extras.jdt.internal.PreferencePropertyTester";
  public static final String NAMESPACE = "com.codeaffine.extras.jdt.internal.PreferenceStore";
  public static final String IS_TRUE = "isTrue";
  public static final String PROP_IS_TRUE = NAMESPACE + "." + IS_TRUE;

  private final IPreferenceStore preferenceStore;

  public PreferencePropertyTester() {
    this(JDTExtrasPlugin.getInstance().getPreferenceStore());
  }

  public PreferencePropertyTester(IPreferenceStore preferenceStore) {
    this.preferenceStore = preferenceStore;
  }

  @Override
  public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
    boolean result = false;
    if (argumentsValid(receiver, property, args)) {
      result = test(args, expectedValue);
    }
    return result;
  }

  private boolean test(Object[] args, Object expectedValue) {
    return evaluateResult(expectedValue, getPreferenceValue(args));
  }

  private static boolean argumentsValid(Object receiver, String property, Object[] args) {
    return receiver instanceof IServiceLocator && IS_TRUE.equals(property) && args.length == 1
        && args[0] instanceof String;
  }

  private boolean getPreferenceValue(Object[] args) {
    return preferenceStore.getBoolean((String) args[0]);
  }

  private static boolean evaluateResult(Object expectedValue, boolean value) {
    return expectedValue instanceof Boolean && ((Boolean) expectedValue).booleanValue() == value;
  }
}
