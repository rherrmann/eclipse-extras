package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitModelUtil.countFailedTests;
import static com.google.common.base.Objects.equal;
import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;
import static org.eclipse.jdt.junit.model.ITestElement.ProgressState.STOPPED;
import static org.eclipse.jdt.junit.model.ITestElement.Result.ERROR;
import static org.eclipse.jdt.junit.model.ITestElement.Result.FAILURE;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class JUnitTestRunListener extends TestRunListener {

  static final RGB ERROR_RGB = new RGB( 159, 63, 63 );
  static final RGB SUCCESS_RGB = new RGB( 95, 191, 95 );
  static final RGB STOPPED_RGB = new RGB( 120, 120, 120 );
  static final String STARTING = "Starting...";

  private final LaunchesAdapter launchListener;
  private final ILaunchManager launchManager;
  private final ResourceManager resourceManager;
  private final ProgressUI progressUI;
  private volatile int currentSessionHashCode;
  private volatile String currentTestRunName;
  private volatile int currentTest;
  private volatile int totalTestCount;
  private volatile int failedTestCount;

  public JUnitTestRunListener( ResourceManager resourceManager, ProgressUI progressUI ) {
    this( DebugPlugin.getDefault().getLaunchManager(), resourceManager, progressUI );
  }

  public JUnitTestRunListener( ILaunchManager launchManager,
                               ResourceManager resourceManager,
                               ProgressUI progressUI )
  {
    this.launchListener = new LaunchTerminatedListener();
    this.resourceManager = resourceManager;
    this.progressUI = progressUI;
    this.launchManager = launchManager;
    this.launchManager.addLaunchListener( launchListener );
  }

  public void dispose() {
    launchManager.removeLaunchListener( launchListener );
  }

  @Override
  public void sessionLaunched( final ITestRunSession testRunSession ) {
    currentSessionHashCode = testRunSession.hashCode();
    currentTestRunName = testRunSession.getTestRunName();
    totalTestCount = 0;
    failedTestCount = 0;
    currentTest = 0;
    updateProgressUI( STARTING );
  }

  @Override
  public void sessionStarted( ITestRunSession testRunSession ) {
    if( belongsToCurrentSession( testRunSession ) ) {
      totalTestCount = JUnitModelUtil.countTestCases( testRunSession );
      failedTestCount = 0;
      currentTest = 0;
      updateProgressUI( testRunSession );
    }
  }

  @Override
  public void sessionFinished( ITestRunSession testRunSession ) {
    if( belongsToCurrentSession( testRunSession ) ) {
      finishSession();
    }
  }

  @Override
  public void testCaseFinished( ITestCaseElement testCaseElement ) {
    if( belongsToCurrentSession( testCaseElement ) ) {
      int temp = currentTest;
      temp++;
      currentTest = temp;
      failedTestCount = countFailedTests( testCaseElement.getTestRunSession() );
      updateProgressUI( testCaseElement.getTestRunSession() );
    }
  }

  private boolean belongsToCurrentSession( ITestElement testElement ) {
    return currentSessionHashCode == testElement.getTestRunSession().hashCode();
  }

  private void finishSession() {
    if( totalTestCount == 0 ) {
      updateProgressUI( "" );
    }
  }

  private void updateProgressUI( ITestRunSession testRunSession ) {
    String text = getProgressBarText();
    Color barColor = getProgressBarColor( testRunSession );
    progressUI.update( text, SWT.CENTER, barColor, currentTest, totalTestCount );
    progressUI.setToolTipText( getToolTipText() );
  }

  private void updateProgressUI( String text ) {
    progressUI.update( text, SWT.LEFT, null, 0, 0 );
    progressUI.setToolTipText( getToolTipText() );
  }

  private String getToolTipText() {
    String result;
    if( failedTestCount > 0 ) {
      result = format( "{0} ({1} failed)", currentTestRunName, valueOf( failedTestCount ) );
    } else {
      result = currentTestRunName;
    }
    return result;
  }

  private String getProgressBarText() {
    return format( "{0} / {1}", valueOf( currentTest ), valueOf( totalTestCount ) );
  }

  private Color getProgressBarColor( ITestRunSession testRunSession ) {
    Result testResult = testRunSession.getTestResult( true );
    RGB rgb;
    if( STOPPED.equals( testRunSession.getProgressState() ) ) {
      rgb = STOPPED_RGB;
    } else if( ERROR.equals( testResult ) || FAILURE.equals( testResult ) ) {
      rgb = ERROR_RGB;
    } else {
      rgb = SUCCESS_RGB;
    }
    return getColor( rgb );
  }

  private Color getColor( RGB rgb ) {
    return resourceManager.createColor( ColorDescriptor.createFrom( rgb ) );
  }

  private class LaunchTerminatedListener extends LaunchesAdapter {
    @Override
    public void launchesTerminated( ILaunch[] launches ) {
      for( ILaunch launch : launches ) {
        launchTerminated( launch );
      }
    }

    private void launchTerminated( ILaunch launch ) {
      if( matchesCurrentSession( launch.getLaunchConfiguration() ) ) {
        finishSession();
      }
    }

    private boolean matchesCurrentSession( ILaunchConfiguration launchConfig ) {
      return launchConfig != null && equal( launchConfig.getName(), currentTestRunName );
    }
  }

}