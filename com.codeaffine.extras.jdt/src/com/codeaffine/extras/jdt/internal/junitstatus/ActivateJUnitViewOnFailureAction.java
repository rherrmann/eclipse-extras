package com.codeaffine.extras.jdt.internal.junitstatus;

import static java.util.Objects.requireNonNull;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class ActivateJUnitViewOnFailureAction extends Action {

  static final String JUNIT_CORE_PLUGIN_ID = "org.eclipse.jdt.junit";
  static final String PREF_SHOW_ON_ERROR_ONLY = JUNIT_CORE_PLUGIN_ID + ".show_on_error";

  private final IEclipsePreferences junitPreferences;

  public ActivateJUnitViewOnFailureAction() {
    this( InstanceScope.INSTANCE.getNode( JUNIT_CORE_PLUGIN_ID ) );
  }

  public ActivateJUnitViewOnFailureAction( IEclipsePreferences junitPreferences ) {
    super( "Activate JUnit View on Failure Only", IAction.AS_CHECK_BOX );
    this.junitPreferences = requireNonNull( junitPreferences );
    setChecked( junitPreferences.getBoolean( PREF_SHOW_ON_ERROR_ONLY, false ) );
  }

  @Override
  public void run() {
    boolean checked = junitPreferences.getBoolean( PREF_SHOW_ON_ERROR_ONLY, false );
    junitPreferences.putBoolean( PREF_SHOW_ON_ERROR_ONLY, !checked );
  }
}
