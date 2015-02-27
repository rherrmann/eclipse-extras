package com.codeaffine.extras.jdt.internal.junitstatus;

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
  private volatile int testCount;
  private volatile int currentTest;

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
    testCount = 0;
    currentTest = 0;
    updateProgressUI( STARTING );
    progressUI.setToolTipText( testRunSession.getTestRunName() );
  }

  @Override
  public void sessionStarted( ITestRunSession testRunSession ) {
    if( belongsToCurrentSession( testRunSession ) ) {
      testCount = JUnitModelUtil.countTestCases( testRunSession );
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
      updateProgressUI( testCaseElement.getTestRunSession() );
    }
  }

  private boolean belongsToCurrentSession( ITestElement testElement ) {
    return currentSessionHashCode == testElement.getTestRunSession().hashCode();
  }

  private void finishSession() {
    if( testCount == 0 ) {
      updateProgressUI( "" );
    }
  }

  private void updateProgressUI( ITestRunSession testRunSession  ) {
    String text = format( "{0} / {1}", valueOf( currentTest ), valueOf( testCount ) );
    Color barColor = getProgressBarColor( testRunSession );
    progressUI.update( text, SWT.CENTER, barColor, currentTest, testCount );
  }

  private  void updateProgressUI( String text ) {
    progressUI.update( text, SWT.LEFT, null, 0, 0 );
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