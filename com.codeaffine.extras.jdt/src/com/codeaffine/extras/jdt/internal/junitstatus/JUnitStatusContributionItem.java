package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;


public class JUnitStatusContributionItem extends WorkbenchWindowControlContribution {

  private ResourceManager resourceManager;
  private JUnitTestRunListener testRunListener;
  private JUnitProgressBar progressBar;

  @Override
  protected Control createControl(Composite parent) {
    resourceManager = new LocalResourceManager(JFaceResources.getResources(parent.getDisplay()));
    Composite result = createControls(parent);
    layoutControls(result);
    attachContextMenu();
    JUnitCore.addTestRunListener(testRunListener);
    return result;
  }

  @Override
  public void dispose() {
    detachTestRunListener();
    disposeResourceManager();
    super.dispose();
  }

  private Composite createControls(Composite parent) {
    Composite result = new StatusComposite(parent, SWT.NONE);
    progressBar = new JUnitProgressBar(result);
    testRunListener = new JUnitTestRunListener(resourceManager, new JUnitProgressUI(progressBar));
    return result;
  }

  private void layoutControls(Composite composite) {
    composite.setLayout(GridLayoutFactory.fillDefaults().margins(3, 0).create());
    progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  private void attachContextMenu() {
    attachContextMenu(progressBar);
  }

  private void attachContextMenu(Control control) {
    MenuManager menuManager = new MenuManager();
    menuManager.add(new ActivateJUnitViewOnFailureAction());
    menuManager.add(new Separator());
    menuManager.add(new CloseJUnitStatusAction(getWorkbenchWindow().getWorkbench()));
    Menu contextMenu = menuManager.createContextMenu(control);
    control.setMenu(contextMenu);
  }

  private void detachTestRunListener() {
    if (testRunListener != null) {
      JUnitCore.removeTestRunListener(testRunListener);
      testRunListener.dispose();
      testRunListener = null;
    }
  }

  private void disposeResourceManager() {
    if (resourceManager != null) {
      resourceManager.dispose();
      resourceManager = null;
    }
  }
}
