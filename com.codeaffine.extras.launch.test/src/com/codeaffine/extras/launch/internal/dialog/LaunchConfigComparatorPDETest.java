package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchMode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigComparatorPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private ILaunchConfiguration launchConfig1;
  private ILaunchConfiguration launchConfig2;
  private LaunchConfigSelectionHistory launchConfigHistory;
  private List<ILaunchConfiguration> historyItems;

  @Before
  public void setUp() throws CoreException {
    launchConfig1 = launchConfigRule.createPublicLaunchConfig().doSave();
    launchConfig2 = launchConfigRule.createPublicLaunchConfig().doSave();
    historyItems = new LinkedList<>();
    launchConfigHistory = mock(LaunchConfigSelectionHistory.class);
    when(launchConfigHistory.getHistoryItems()).thenReturn(historyItems.toArray());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullLaunchConfigHistory() {
    new LaunchConfigComparator(null, null);
  }

  @Test
  public void testCompareWhenBothLaunchConfigsInHistory() throws CoreException {
    renameLaunchConfig1("z" + launchConfig1.getName());
    renameLaunchConfig2("a" + launchConfig2.getName());
    addToHistory(launchConfig1);
    addToHistory(launchConfig2);

    int compareResult = compare(launchConfig1, launchConfig2);

    assertThat(compareResult).isEqualTo(-1);
  }

  @Test
  public void testCompareWhenOneLaunchConfigInHistory() throws CoreException {
    renameLaunchConfig1("z" + launchConfig1.getName());
    renameLaunchConfig2("a" + launchConfig2.getName());
    addToHistory(launchConfig1);

    int compareResult = compare(launchConfig1, launchConfig2);

    assertThat(compareResult).isGreaterThan(0);
  }

  @Test
  public void testCompareWhenBothLaunchConfigsNotInHistory() throws CoreException {
    renameLaunchConfig1("z" + launchConfig1.getName());
    renameLaunchConfig2("a" + launchConfig2.getName());

    int compareResult = compare(launchConfig1, launchConfig2);

    assertThat(compareResult).isGreaterThan(0);
  }

  @Test
  public void testCompareWithLastLaunched() throws CoreException {
    addToHistory(launchConfig2);
    addToHistory(launchConfig1);
    launchConfig1.launch(DEBUG_MODE, null);

    int compareResult = compare(launchConfig1, launchConfig2);

    assertThat(compareResult).isLessThan(0);
  }

  @Test
  public void testCompareWithLastLaunchedInDifferentLaunchMode() throws CoreException {
    addToHistory(launchConfig2);
    addToHistory(launchConfig1);
    launchConfig1.launch(DEBUG_MODE, null);

    int compareResult = compare(launchConfig1, launchConfig2, RUN_MODE);

    assertThat(compareResult).isLessThan(0);
  }

  @Test
  public void testCompareWithNullLaunchMode() {
    addToHistory(launchConfig1);
    addToHistory(launchConfig2);

    LaunchConfigComparator comparator = new LaunchConfigComparator(launchConfigHistory, null);
    int compareResult = comparator.compare(launchConfig1, launchConfig2);

    assertThat(compareResult).isLessThan(0);
  }

  @Test
  public void testCompareWithNullLaunchConfigs() {
    Throwable throwable = catchThrowable(() -> compare(null, null));

    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  private void renameLaunchConfig1(String name) throws CoreException {
    ILaunchConfigurationWorkingCopy workingCopy = launchConfig1.getWorkingCopy();
    workingCopy.rename(name);
    launchConfig1 = workingCopy.doSave();
  }

  private void renameLaunchConfig2(String name) throws CoreException {
    ILaunchConfigurationWorkingCopy workingCopy = launchConfig2.getWorkingCopy();
    workingCopy.rename(name);
    launchConfig2 = workingCopy.doSave();
  }

  private void addToHistory(ILaunchConfiguration launchConfig) {
    historyItems.add(launchConfig);
    when(launchConfigHistory.contains(launchConfig)).thenReturn(true);
    when(launchConfigHistory.getHistoryItems()).thenReturn(historyItems.toArray());
  }

  private int compare(ILaunchConfiguration launchConfig1, ILaunchConfiguration launchConfig2) {
    return compare(launchConfig1, launchConfig2, DEBUG_MODE);
  }

  private int compare(ILaunchConfiguration launchConfig1, ILaunchConfiguration launchConfig2, String mode) {
    ILaunchMode launchMode = DebugPlugin.getDefault().getLaunchManager().getLaunchMode(mode);
    LaunchConfigComparator comparator = new LaunchConfigComparator(launchConfigHistory, launchMode);
    return comparator.compare(launchConfig1, launchConfig2);
  }

}
