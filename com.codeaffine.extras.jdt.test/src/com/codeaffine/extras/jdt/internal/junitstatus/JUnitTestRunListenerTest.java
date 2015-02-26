package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.ERROR_RGB;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.STARTING;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.STOPPED_RGB;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.SUCCESS_RGB;
import static org.eclipse.jdt.junit.model.ITestElement.ProgressState.STOPPED;
import static org.eclipse.jdt.junit.model.ITestElement.Result.ERROR;
import static org.eclipse.jdt.junit.model.ITestElement.Result.FAILURE;
import static org.eclipse.jdt.junit.model.ITestElement.Result.OK;
import static org.eclipse.jface.resource.JFaceResources.getResources;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class JUnitTestRunListenerTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ResourceManager resourceManager;
  private ILaunchManager launchManager;
  private List<ILaunchesListener2> launchesListeners;
  private ProgressUI progressUI;
  private JUnitTestRunListener testRunListener;

  @Test
  public void testSessionLaunched() {
    ITestRunSession testRunSession = mockTestRunSession( OK );

    testRunListener.sessionLaunched( testRunSession );

    verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
  }

  @Test
  public void testSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession );

    testRunListener.sessionStarted( testRunSession );

    verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
  }

  @Test
  public void testStoppedSessionFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement( OK );
    testRunListener.sessionLaunched( testCaseElement.getTestRunSession() );
    testRunListener.sessionStarted( testCaseElement.getTestRunSession() );
    when( testCaseElement.getTestRunSession().getProgressState() ).thenReturn( STOPPED );

    testRunListener.sessionFinished( testCaseElement.getTestRunSession() );

    verify( progressUI ).update( "0 / 1", SWT.CENTER, stoppedColor(), 0, 1 );
  }

  @Test
  public void testStoppedSessionFinishedWithFailingTest() {
    ITestCaseElement testCaseElement = mockTestCaseElement( ERROR );
    testRunListener.sessionLaunched( testCaseElement.getTestRunSession() );
    testRunListener.sessionStarted( testCaseElement.getTestRunSession() );
    when( testCaseElement.getTestRunSession().getProgressState() ).thenReturn( STOPPED );

    testRunListener.sessionFinished( testCaseElement.getTestRunSession() );

    verify( progressUI ).update( "0 / 1", SWT.CENTER, stoppedColor(), 0, 1 );
  }

  @Test
  public void testObsoleteSessionFinished() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mock( ITestCaseElement.class ), mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    testRunListener.sessionFinished( testRunSession1 );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ] );

    testRunListener.sessionFinished( testRunSession2 );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI, times( 2 ) ).update( "2 / 2", SWT.CENTER, successColor(), 2, 2 );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testLaunchTerminatedBeforeSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession );

    ILaunch launch = mockLaunch( testRunSession.getTestRunName() );
    fireLaunchTerminated( launch );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "", SWT.LEFT, null, 0, 0 );
  }

  @Test
  public void testLaunchTerminatedAfterSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );

    ILaunch launch = mockLaunch( testRunSession.getTestRunName() );
    fireLaunchTerminated( launch );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI, times( 2 ) ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
  }

  @Test
  public void testTestCaseStarted() {
    ITestCaseElement testCaseElement = mock( ITestCaseElement.class );
    mockTestRunSession( OK, testCaseElement );

    testRunListener.testCaseStarted( testCaseElement );

    verify( progressUI, never() )
      .update( anyString(), anyInt(), any( Color.class ), anyInt(), anyInt() );
  }

  @Test
  public void testTestCaseFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement( OK );
    testRunListener.sessionLaunched( testCaseElement.getTestRunSession() );
    testRunListener.sessionStarted( testCaseElement.getTestRunSession() );

    testRunListener.testCaseFinished( testCaseElement );

    verify( progressUI ).update( "1 / 1", SWT.CENTER, successColor(), 1, 1 );
  }

  @Test
  public void testFailedTestCaseFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement( FAILURE );
    testRunListener.sessionLaunched( testCaseElement.getTestRunSession() );
    testRunListener.sessionStarted( testCaseElement.getTestRunSession() );

    testRunListener.testCaseFinished( testCaseElement );

    verify( progressUI ).update( "1 / 1", SWT.CENTER, errorColor(), 1, 1 );
  }

  @Test
  public void testErroredTestCaseFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement( ERROR );
    testRunListener.sessionLaunched( testCaseElement.getTestRunSession() );
    testRunListener.sessionStarted( testCaseElement.getTestRunSession() );

    testRunListener.testCaseFinished( testCaseElement );

    verify( progressUI ).update( "1 / 1", SWT.CENTER, errorColor(), 1, 1 );
  }

  @Test
  public void testSecondSessionLaunched() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mock( ITestCaseElement.class ), mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testRunTwoSessionsFromStartToEnd() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    testRunListener.sessionFinished( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mock( ITestCaseElement.class ), mock( ITestCaseElement.class ) );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ] );
    testRunListener.sessionFinished( testRunSession2 );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI, times( 2 ) ).update( "1 / 1", SWT.CENTER, successColor(), 1, 1 );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI, times( 2 ) ).update( "2 / 2", SWT.CENTER, successColor(), 2, 2 );
    order.verifyNoMoreInteractions();
  }

  @Before
  public void setUp() {
    launchesListeners = new ArrayList<ILaunchesListener2>();
    launchManager = mockLaunchManager();
    resourceManager = new LocalResourceManager( getResources( displayHelper.getDisplay() ) );
    progressUI = mock( ProgressUI.class );
    testRunListener = new JUnitTestRunListener( launchManager, resourceManager, progressUI );
  }

  @After
  public void tearDown() {
    resourceManager.dispose();
  }

  private ILaunchManager mockLaunchManager() {
    ILaunchManager result = mock( ILaunchManager.class );
    doAnswer( new Answer<Object>() {
      @Override
      public Object answer( InvocationOnMock invocation ) {
        launchesListeners.add( ( ILaunchesListener2 )invocation.getArguments()[ 0 ] );
        return null;
      }
    } ).when( result ).addLaunchListener( any( ILaunchesListener2.class ) );
    return result;
  }

  private static ILaunch mockLaunch( String launchConfigName ) {
    ILaunch result = mock( ILaunch.class );
    ILaunchConfiguration launchConfig = mock( ILaunchConfiguration.class );
    when( launchConfig.getName() ).thenReturn( launchConfigName );
    when( result.getLaunchConfiguration() ).thenReturn( launchConfig );
    return result;
  }

  private static ITestRunSession mockTestRunSession( Result testResult, ITestElement... children )
  {
    ITestRunSession result = mock( ITestRunSession.class );
    when( result.getTestRunSession() ).thenReturn( result );
    when( result.getTestRunName() ).thenReturn( "test-run-name" );
    when( result.getTestResult( true ) ).thenReturn( testResult );
    when( result.getChildren() ).thenReturn( children );
    for( ITestElement child : children ) {
      when( child.getTestRunSession() ).thenReturn( result );
    }
    return result;
  }

  private static ITestCaseElement mockTestCaseElement( Result testResult ) {
    ITestCaseElement result = mock( ITestCaseElement.class );
    ITestRunSession testRunSession = mockTestRunSession( testResult, result );
    when( result.getTestRunSession() ).thenReturn( testRunSession );
    return result;
  }

  private void fireLaunchTerminated( ILaunch launch ) {
    for( ILaunchesListener2 launchesListener : launchesListeners ) {
      launchesListener.launchesTerminated( new ILaunch[] { launch } );
    }
  }

  private Color successColor() {
    return resourceManager.createColor( SUCCESS_RGB );
  }

  private Color errorColor() {
    return resourceManager.createColor( ERROR_RGB );
  }

  private Color stoppedColor() {
    return resourceManager.createColor( STOPPED_RGB );
  }

}
