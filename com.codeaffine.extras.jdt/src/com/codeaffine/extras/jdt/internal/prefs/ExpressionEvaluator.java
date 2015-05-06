package com.codeaffine.extras.jdt.internal.prefs;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.services.IEvaluationService;


public class ExpressionEvaluator {

  private final IWorkbench workbench;

  public ExpressionEvaluator( IWorkbench workbench ) {
    this.workbench = workbench;
  }

  public void evaluate() {
    IEvaluationService evaluationService = getEvaluationService();
    if( evaluationService != null ) {
      evaluationService.requestEvaluation( PreferencePropertyTester.PROP_IS_TRUE );
    }
  }

  private IEvaluationService getEvaluationService() {
    return workbench == null ? null : ( IEvaluationService )workbench.getService( IEvaluationService.class );
  }

}
