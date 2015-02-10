package com.codeaffine.extras.jdt.internal.junitstatus;

import static java.lang.Integer.valueOf;
import static org.eclipse.jdt.junit.model.ITestElement.ProgressState.STOPPED;
import static org.eclipse.jdt.junit.model.ITestElement.Result.ERROR;
import static org.eclipse.jdt.junit.model.ITestElement.Result.FAILURE;

import java.text.MessageFormat;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.codeaffine.eclipse.swt.util.UIThreadSynchronizer;

public class JUnitTestRunListener extends TestRunListener {

  static final RGB ERROR_RGB = new RGB( 159, 63, 63 );
  static final RGB SUCCESS_RGB = new RGB( 95, 191, 95 );
  static final RGB STOPPED_RGB = new RGB( 120, 120, 120 );
  static final String STARTING = "Starting...";

  private final ResourceManager resourceManager;
  private final ProgressUI progressUI;
  private volatile ITestRunSession currentSession;
  private int testCount;
  private int currentTest;

  public JUnitTestRunListener( ResourceManager resourceManager, ProgressUI progressUI ) {
    this.resourceManager = resourceManager;
    this.progressUI = progressUI;
  }

  @Override
  public void sessionLaunched( final ITestRunSession testRunSession ) {
    currentSession = testRunSession;
    asyncExec( new Runnable() {
      @Override
      public void run() {
        testCount = 0;
        currentTest = 0;
        updateProgressUI( STARTING );
      }
    } );
  }

  @Override
  public void sessionStarted( final ITestRunSession testRunSession ) {
    if( currentSession != testRunSession ) {
      return;
    }
    asyncExec( new Runnable() {
      @Override
      public void run() {
        testCount = JUnitModelUtil.countTestCases( testRunSession );
        currentTest = 0;
        updateProgressUI( testRunSession, currentTest, testCount );
      }
    } );
  }

  @Override
  public void sessionFinished( final ITestRunSession testRunSession ) {
    if( currentSession != testRunSession ) {
      return;
    }
    currentSession = null;
    asyncExec( new Runnable() {
      @Override
      public void run() {
        updateProgressUI( testRunSession, currentTest, testCount );
      }
    } );
  }

  @Override
  public void testCaseFinished( final ITestCaseElement testCaseElement ) {
    if( currentSession != testCaseElement.getTestRunSession() ) {
      return;
    }
    asyncExec( new Runnable() {
      @Override
      public void run() {
        currentTest++;
        updateProgressUI( testCaseElement.getTestRunSession(), currentTest, testCount );
      }
    } );
  }

  private void asyncExec( Runnable runnable ) {
    new UIThreadSynchronizer().asyncExec( progressUI.getWidget(), runnable );
  }

  private void updateProgressUI( ITestRunSession testRunSession, int currentTest, int testCount ) {
    String text = MessageFormat.format( "{0} / {1}", valueOf( currentTest ), valueOf( testCount ) );
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
    return resourceManager.createColor( ColorDescriptor.createFrom( rgb ) );
  }

}