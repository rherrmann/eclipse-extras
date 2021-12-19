package com.codeaffine.extras.launch.internal.dialog;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.ui.ISources.ACTIVE_WORKBENCH_WINDOW_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;
import com.codeaffine.extras.test.util.ServiceHelper;

public class OpenLaunchDialogHanderPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private IWorkbenchWindow workbenchWindow;
  private ILaunchMode launchMode;
  private OpenLaunchDialogHander handler;

  @Before
  public void setUp() {
    workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    launchMode = DebugPlugin.getDefault().getLaunchManager().getLaunchMode(DEBUG_MODE);
    handler = spy(new OpenLaunchDialogHander());
  }

  @Test
  public void testExecuteSingleLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    LaunchSelectionDialog dialog = mockLaunchSelectionDialog(launchConfig);
    doReturn(dialog).when(handler).createDialog(any());

    executeHandler();

    ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
    assertThat(launches).extracting("launchConfiguration").containsOnly(launchConfig);
    assertThat(launches).extracting("launchMode").containsOnly(launchMode.getIdentifier());
  }

  @Test
  public void testExecuteMultipleLaunchConfigs() throws CoreException {
    ILaunchConfiguration launchConfig1 = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunchConfiguration launchConfig2 = launchConfigRule.createPublicLaunchConfig().doSave();
    LaunchSelectionDialog dialog = mockLaunchSelectionDialog(launchConfig1, launchConfig2);
    doReturn(dialog).when(handler).createDialog(any());

    executeHandler();

    ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
    assertThat(launches).extracting("launchConfiguration").containsOnly(launchConfig1, launchConfig2);
    assertThat(launches).extracting("launchMode").containsOnly(launchMode.getIdentifier());
  }

  private void executeHandler() {
    Command command = getOpenLaunchDialogCommand();
    IEvaluationContext evaluationContext = createEvaluationContext();
    handler.execute(new ExecutionEvent(command, emptyMap(), null, evaluationContext));
  }

  private LaunchSelectionDialog mockLaunchSelectionDialog(ILaunchConfiguration... launchConfigs) {
    LaunchSelectionDialog dialog = mock(LaunchSelectionDialog.class);
    when(dialog.open()).thenReturn(Window.OK);
    when(dialog.getSelectedLaunchConfigurations()).thenReturn(launchConfigs);
    when(dialog.getLaunchMode()).thenReturn(launchMode);
    return dialog;
  }

  private IEvaluationContext createEvaluationContext() {
    IEvaluationContext result = new EvaluationContext(null, new Object());
    result.addVariable(ACTIVE_WORKBENCH_WINDOW_NAME, workbenchWindow);
    return result;
  }

  private Command getOpenLaunchDialogCommand() {
    ICommandService commandService = ServiceHelper.getService(workbenchWindow, ICommandService.class);
    return commandService.getCommand(OpenLaunchDialogHander.COMMAND_ID);
  }
}
