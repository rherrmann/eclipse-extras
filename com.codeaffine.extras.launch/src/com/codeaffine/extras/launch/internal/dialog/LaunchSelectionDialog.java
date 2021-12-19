package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.DETAIL;
import static com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode.LIST;
import static java.util.Arrays.stream;
import static org.eclipse.jface.action.IAction.ENABLED;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import java.util.Comparator;
import java.util.stream.Stream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import com.codeaffine.extras.launch.internal.LaunchExtrasPlugin;
import com.codeaffine.extras.launch.internal.dialog.LaunchConfigLabelProvider.LabelMode;
import com.codeaffine.extras.launch.internal.util.LaunchAdapter;
import com.codeaffine.extras.util.swt.UIThreadSynchronizer;


public class LaunchSelectionDialog extends FilteredItemsSelectionDialog implements DuplicatesDetector {

  static final int EDIT_BUTTON_ID = IDialogConstants.CLIENT_ID + 2;

  private final ILaunchManager launchManager;
  private final EditLaunchConfigAction editLaunchConfigAction;
  private final TerminateLaunchesAction terminateLaunchesAction;
  private final LaunchConfigSelectionHistory launchConfigHistory;

  public LaunchSelectionDialog(Shell shell) {
    super(shell, true);
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    editLaunchConfigAction = new EditLaunchConfigAction(this);
    editLaunchConfigAction.addPropertyChangeListener(new EditLaunchConfigActionListener());
    terminateLaunchesAction = new TerminateLaunchesAction();
    launchConfigHistory = new LaunchConfigSelectionHistory();
    setTitle("Start Launch Configuration");
    setMessage("Enter a &name pattern (? = any character, * = any string, CamelCase)");
    setListLabelProvider(createLaunchConfigLabelProvider(shell.getDisplay(), LIST));
    setDetailsLabelProvider(createLaunchConfigLabelProvider(shell.getDisplay(), DETAIL));
    setSelectionHistory(launchConfigHistory);
    setSeparatorLabel("Launch Configuration matches");
  }

  public ILaunchConfiguration[] getSelectedLaunchConfigurations() {
    return Stream.of(getResult()).map(element -> (ILaunchConfiguration) element).toArray(ILaunchConfiguration[]::new);
  }

  public String getLaunchModeId() {
    return new LaunchModeSetting(launchManager, getDialogSettings()).getLaunchModeId();
  }

  public ILaunchMode getLaunchMode() {
    return new LaunchModeSetting(launchManager, getDialogSettings()).getLaunchMode();
  }

  @Override
  public String getElementName(Object item) {
    ILaunchConfiguration configuration = (ILaunchConfiguration) item;
    return configuration.getName();
  }

  @Override
  public void create() {
    super.create();
    registerLaunchConfigDecorationUpdater();
  }

  public void close(int returnCode) {
    setReturnCode(returnCode);
    close();
  }

  @Override
  protected void fillViewMenu(IMenuManager menuManager) {
    super.fillViewMenu(menuManager);
    menuManager.add(new Separator());
    menuManager.add(new ToggleTerminateBeforeRelaunchAction());
    menuManager.add(createLaunchModeDropDownAction());
  }

  @Override
  protected void fillContextMenu(IMenuManager menuManager) {
    menuManager.add(editLaunchConfigAction);
    menuManager.add(new Separator());
    menuManager.add(terminateLaunchesAction);
    menuManager.add(new Separator());
    super.fillContextMenu(menuManager);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    GridLayout parentLayout = (GridLayout) parent.getLayout();
    parentLayout.makeColumnsEqualWidth = false;
    parentLayout.numColumns++;
    Button editButton = createButton(parent, EDIT_BUTTON_ID, "&Edit...", false);
    editButton.setEnabled(false);
    setButtonLayoutData(editButton);
    new Label(parent, SWT.NONE).setLayoutData(new GridData(5, 0));
    createButton(parent, OK_ID, OK_LABEL, true);
    createButton(parent, CANCEL_ID, CANCEL_LABEL, false);
    updateOkButtonLabel();
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == EDIT_BUTTON_ID) {
      editLaunchConfigAction.run();
    } else {
      super.buttonPressed(buttonId);
    }
  }

  @Override
  protected Control createExtendedContentArea(Composite parent) {
    return null;
  }

  @Override
  protected IDialogSettings getDialogSettings() {
    IDialogSettings dialogSettings = LaunchExtrasPlugin.getInstance().getDialogSettings();
    return DialogSettings.getOrCreateSection(dialogSettings, "LaunchSelectionDialog");
  }

  @Override
  protected void handleSelected(StructuredSelection selection) {
    super.handleSelected(selection);
    editLaunchConfigAction.setSelection(selection);
    terminateLaunchesAction.setSelection(selection);
  }

  @Override
  protected IStatus validateItem(Object item) {
    ILaunchMode preferredLaunchMode = getLaunchMode();
    ILaunchConfiguration launchConfig = (ILaunchConfiguration) item;
    return new LaunchConfigValidator(launchConfig, preferredLaunchMode).validate();
  }

  @Override
  protected ItemsFilter createFilter() {
    return new LaunchConfigItemsFilter();
  }

  @Override
  protected Comparator<ILaunchConfiguration> getItemsComparator() {
    return new LaunchConfigComparator(launchConfigHistory, getLaunchMode());
  }

  @Override
  protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
      IProgressMonitor progressMonitor) throws CoreException {
    LaunchConfigProvider launchConfigProvider = new LaunchConfigProvider(launchManager);
    stream(launchConfigProvider.getLaunchConfigurations())
        .forEach(launchConfig -> contentProvider.add(launchConfig, itemsFilter));
  }

  private void registerLaunchConfigDecorationUpdater() {
    LaunchAdapter launchListener = new LaunchConfigDecorationUpdater();
    launchManager.addLaunchListener(launchListener);
    getShell().addListener(SWT.Dispose, event -> launchManager.removeLaunchListener(launchListener));
  }

  private ILabelProvider createLaunchConfigLabelProvider(Display display, LabelMode labelMode) {
    return new LaunchConfigLabelProvider(display, this, labelMode);
  }

  private LaunchModeDropDownAction createLaunchModeDropDownAction() {
    LaunchModeSetting launchModeSetting = new LaunchModeSetting(launchManager, getDialogSettings());
    LaunchModeDropDownAction result = new LaunchModeDropDownAction(launchModeSetting);
    result.addPropertyChangeListener(event -> {
      updateOkButtonLabel();
      updateStatus();
    });
    return result;
  }

  private void updateOkButtonLabel() {
    Button okButton = getButton(OK_ID);
    LaunchModeSetting launchModeSetting = new LaunchModeSetting(launchManager, getDialogSettings());
    ILaunchMode launchMode = launchModeSetting.getLaunchMode();
    if (okButton != null && launchMode != null) {
      okButton.setText(launchMode.getLabel());
      okButton.getParent().layout();
    }
  }

  void updateStatus() {
    IStatus totalStatus = (IStatus) getSelectedItems().toList().stream().map(item -> validateItem(item))
        .filter(status -> !((IStatus) status).isOK()).findFirst().orElse(okStatus());
    updateStatus(totalStatus);
  }

  private static Status okStatus() {
    return new Status(OK, PLUGIN_ID, "");
  }

  public abstract static class AccessibleSelectionHistory extends SelectionHistory {
  }

  private class LaunchConfigItemsFilter extends ItemsFilter {
    LaunchConfigItemsFilter() {
      super(new LaunchConfigSearchPattern());
    }

    @Override
    public boolean matchItem(Object item) {
      boolean result = false;
      if (item instanceof ILaunchConfiguration) {
        result = matchLaunchConfig((ILaunchConfiguration) item);
      }
      return result;
    }

    @Override
    public boolean isConsistentItem(Object item) {
      boolean result = true;
      if (item instanceof ILaunchConfiguration && !((ILaunchConfiguration) item).exists()) {
        result = false;
      }
      return result;
    }

    private boolean matchLaunchConfig(ILaunchConfiguration launchConfig) {
      return matches(launchConfig.getName());
    }
  }

  private class LaunchConfigDecorationUpdater extends LaunchAdapter {
    @Override
    public void launchesAdded(ILaunch[] launches) {
      update();
    }

    @Override
    public void launchesTerminated(ILaunch[] launches) {
      update();
    }

    private void update() {
      new UIThreadSynchronizer().asyncExec(getShell(), LaunchSelectionDialog.this::refresh);
    }
  }

  private class EditLaunchConfigActionListener implements IPropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (ENABLED.equals(event.getProperty())) {
        updateEditButtonEnablement((IAction) event.getSource());
      }
    }

    private void updateEditButtonEnablement(IAction action) {
      Button button = getButton(EDIT_BUTTON_ID);
      if (button != null && !button.isDisposed()) {
        button.setEnabled(action.isEnabled());
      }
    }
  }

}
