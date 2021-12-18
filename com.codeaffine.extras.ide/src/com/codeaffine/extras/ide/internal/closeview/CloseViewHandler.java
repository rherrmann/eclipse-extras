package com.codeaffine.extras.ide.internal.closeview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class CloseViewHandler extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.ide.internal.CloseViewCommand";

  @Override
  public Object execute(ExecutionEvent event) {
    IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
    closeView(activePart);
    return null;
  }

  @Override
  public void setEnabled(Object evaluationContext) {
    if (evaluationContext instanceof IEvaluationContext) {
      setBaseEnabled(isEnabled((IEvaluationContext) evaluationContext));
    } else {
      setBaseEnabled(false);
    }
  }

  private static boolean isEnabled(IEvaluationContext evaluationContext) {
    Object activePart = evaluationContext.getVariable(ISources.ACTIVE_PART_NAME);
    return activePart instanceof IViewPart;
  }

  private static void closeView(IWorkbenchPart activePart) {
    if (activePart instanceof IViewPart) {
      IViewPart viewPart = (IViewPart) activePart;
      activePart.getSite().getPage().hideView(viewPart);
    }
  }
}
