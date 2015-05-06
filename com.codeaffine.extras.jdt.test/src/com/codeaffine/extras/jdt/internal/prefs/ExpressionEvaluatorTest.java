package com.codeaffine.extras.jdt.internal.prefs;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.services.IEvaluationService;
import org.junit.Before;
import org.junit.Test;

public class ExpressionEvaluatorTest {

  private IWorkbench workbench;

  @Before
  public void setUp() {
    workbench = mock( IWorkbench.class );
  }

  @Test
  public void testEvaluate() {
    IEvaluationService evaluationService = mock( IEvaluationService.class );
    when( workbench.getService( IEvaluationService.class ) ).thenReturn( evaluationService );

    new ExpressionEvaluator( workbench ).evaluate();

    verify( evaluationService ).requestEvaluation( PreferencePropertyTester.PROP_IS_TRUE );
  }

  @Test
  public void testEvaluateWithNullworkbench() {
    try {
      new ExpressionEvaluator( null ).evaluate();
    } catch( RuntimeException notExpected ) {
      fail();
    }
  }

  @Test
  public void testEvaluateWithoutEvaluationService() {
    new ExpressionEvaluator( workbench ).evaluate();

    verify( workbench ).getService( IEvaluationService.class );
  }

}
