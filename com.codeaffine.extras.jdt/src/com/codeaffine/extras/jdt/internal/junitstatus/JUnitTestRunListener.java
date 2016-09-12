package com.codeaffine.extras.jdt.internal.junitstatus;

import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;

import java.util.Objects;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
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
    this.currentTestRunName = "";
  }

  public void dispose() {
    launchManager.removeLaunchListener( launchListener );
  }

  @Override
  public void sessionLaunched( ITestRunSession testRunSession ) {
    currentSessionHashCode = 0;
    currentTestRunName = testRunSession.getTestRunName();
    updateProgressUI( new TestRunSessionInfo( testRunSession ), STARTING );
  }

  @Override
  public void sessionStarted( ITestRunSession testRunSession ) {
    if( belongsToCurrentSession( testRunSession ) ) {
      initializeCurrentSession( testRunSession );
      updateProgressUI( new TestRunSessionInfo( testRunSession ) );
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
      initializeCurrentSession( testCaseElement );
      updateProgressUI( new TestRunSessionInfo( testCaseElement ) );
    }
  }

  private boolean belongsToCurrentSession( ITestElement testElement ) {
    return currentSessionHashCode == 0
        || currentSessionHashCode == testElement.getTestRunSession().hashCode();
  }

  private void initializeCurrentSession( ITestElement testElement ) {
    currentSessionHashCode = testElement.getTestRunSession().hashCode();
    currentTestRunName = testElement.getTestRunSession().getTestRunName();
  }

  private void finishSession() {
    if( currentSessionHashCode == 0 ) {
      currentTestRunName = "";
      updateProgressUI( null, "" );
    }
  }

  private void updateProgressUI( TestRunSessionInfo testRunSessionInfo ) {
    Color barColor = getProgressBarColor( testRunSessionInfo );
    String text = getProgressBarText( testRunSessionInfo );
    int executedTestCount = testRunSessionInfo.getExecutedTestCount();
    int totalTestCount = testRunSessionInfo.getTotalTestCount();
    progressUI.update( text, SWT.CENTER, barColor, executedTestCount, totalTestCount );
    progressUI.setToolTipText( getToolTipText( testRunSessionInfo ) );
  }

  private void updateProgressUI( TestRunSessionInfo testRunSessionInfo, String text ) {
    progressUI.update( text, SWT.LEFT, null, 0, 0 );
    progressUI.setToolTipText( getToolTipText( testRunSessionInfo ) );
  }

  private String getToolTipText( TestRunSessionInfo testRunSessionInfo ) {
    String result = currentTestRunName;
    if( testRunSessionInfo != null ) {
      int failedTestCount = testRunSessionInfo.getFailedTestCount();
      if( failedTestCount > 0 ) {
        result = format( "{0} ({1} failed)", currentTestRunName, valueOf( failedTestCount ) );
      }
    }
    return result;
  }

  private static String getProgressBarText( TestRunSessionInfo testRunSessionInfo ) {
    int executedTestCount = testRunSessionInfo.getExecutedTestCount();
    int totalTestCount = testRunSessionInfo.getTotalTestCount();
    return format( "{0} / {1}", valueOf( executedTestCount ), valueOf( totalTestCount ) );
  }

  private Color getProgressBarColor( TestRunSessionInfo testRunSessionInfo ) {
    RGB rgb;
    TestRunState testRunState = testRunSessionInfo.getTestRunState();
    switch( testRunState ) {
      case STOPPED:
        rgb = STOPPED_RGB;
      break;
      case FAILED:
        rgb = ERROR_RGB;
      break;
      case SUCCESS:
        rgb = SUCCESS_RGB;
      break;
      default:
        throw new UnsupportedOperationException( "Unsupported TestRunState: " + testRunState );
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
      return launchConfig != null && Objects.equals( launchConfig.getName(), currentTestRunName );
    }
  }

}