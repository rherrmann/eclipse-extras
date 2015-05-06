package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.prefs.WorkspaceScopePreferences.PREF_SHOW_JUNIT_STATUS_BAR;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.codeaffine.extras.jdt.internal.JDTExtrasPlugin;
import com.codeaffine.extras.jdt.internal.prefs.ExpressionEvaluator;


public class JUnitStatusBarPreferencePage
  extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{

  public static final String ID = "com.codeaffine.extras.jdt.internal.JUnitStatusBarPreferencePage";

  private IWorkbench workbench;

  public JUnitStatusBarPreferencePage() {
    super( "JUnit Status Bar", FieldEditorPreferencePage.GRID );
    noDefaultAndApplyButton();
  }

  @Override
  public void init( IWorkbench workbench ) {
    this.workbench = workbench;
  }

  @Override
  public boolean performOk() {
    boolean result = super.performOk();
    new ExpressionEvaluator( workbench ).evaluate();
    return result;
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return JDTExtrasPlugin.getInstance().getPreferenceStore();
  }

  @Override
  protected void createFieldEditors() {
    String label = "&Show JUnit progress in the status bar";
    createBooleanFieldEditor( getFieldEditorParent(), label, PREF_SHOW_JUNIT_STATUS_BAR );
  }

  private void createBooleanFieldEditor( Composite parent, String label, String preferenceName ) {
    FieldEditor editor = new BooleanFieldEditor( preferenceName, label, parent );
    addField( editor );
  }
}
