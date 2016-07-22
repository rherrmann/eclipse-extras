package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.services.IEvaluationService;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.jdt.internal.prefs.PreferencePropertyTester;
import com.codeaffine.extras.jdt.internal.prefs.WorkspaceScopePreferences;

public class CloseJUnitStatusActionTest {

  private IWorkbench workbench;
  private IEvaluationService evaluationService;
  private WorkspaceScopePreferences preferences;
  private CloseJUnitStatusAction action;

  @Before
  public void setUp() {
    evaluationService = mock( IEvaluationService.class );
    workbench = mock( IWorkbench.class );
    when( workbench.getService( IEvaluationService.class ) ).thenReturn( evaluationService );
    preferences = new WorkspaceScopePreferences( new PreferenceStore() );
    action = new CloseJUnitStatusAction( workbench, preferences );
  }

  @Test
  public void testGetText() {
    assertThat( action.getText() ).isNotEmpty();
  }

  @Test
  public void testRun() {
    preferences.setShowJUnitStatusBar( true );

    action.run();

    assertThat( preferences.isShowJUnitStatusBar() ).isFalse();
    verify( evaluationService ).requestEvaluation( PreferencePropertyTester.PROP_IS_TRUE );
  }

  @Test
  public void testRunWhileHidden() {
    preferences.setShowJUnitStatusBar( false );

    action.run();

    assertThat( preferences.isShowJUnitStatusBar() ).isFalse();
    verify( evaluationService ).requestEvaluation( PreferencePropertyTester.PROP_IS_TRUE );
  }
}
