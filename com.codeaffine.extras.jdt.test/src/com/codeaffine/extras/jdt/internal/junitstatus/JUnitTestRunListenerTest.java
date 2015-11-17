package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.ERROR_RGB;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.STARTING;
import static com.codeaffine.extras.jdt.internal.junitstatus.JUnitTestRunListener.SUCCESS_RGB;
import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;
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
    verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
  }

  @Test
  public void testSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession );

    testRunListener.sessionStarted( testRunSession );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
  }

  @Test
  public void testStoppedSessionFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    ITestRunSession testRunSession = mockTestRunSession( OK, testCaseElement );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );

    when( testRunSession.getProgressState() ).thenReturn( STOPPED );
    testRunListener.sessionFinished( testCaseElement.getTestRunSession() );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
  }

  @Test
  public void testStoppedSessionFinishedWithFailingTest() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    ITestRunSession testRunSession = mockTestRunSession( OK, testCaseElement );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );
    setTestCaseResult( testCaseElement, ERROR );
    testRunListener.testCaseFinished( testCaseElement );

    when( testCaseElement.getTestRunSession().getProgressState() ).thenReturn( STOPPED );
    testRunListener.sessionFinished( testCaseElement.getTestRunSession() );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "1 / 1", SWT.CENTER, errorColor(), 1, 1 );
  }

  @Test
  public void testObsoleteSessionFinished() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mockTestCaseElement(), mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    setTestCaseResult( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    testRunListener.sessionFinished( testRunSession1 );
    setTestCaseResult( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );
    setTestCaseResult( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ] );

    testRunListener.sessionFinished( testRunSession2 );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "2 / 2", SWT.CENTER, successColor(), 2, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testLaunchTerminatedBeforeSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession );

    ILaunch launch = mockLaunch( testRunSession.getTestRunName() );
    fireLaunchTerminated( launch );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "", SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( "" );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testLaunchTerminatedAfterSessionStarted() {
    ITestRunSession testRunSession = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );

    ILaunch launch = mockLaunch( testRunSession.getTestRunName() );
    fireLaunchTerminated( launch );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testTestCaseStarted() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    mockTestRunSession( OK, testCaseElement );

    testRunListener.testCaseStarted( testCaseElement );

    verify( progressUI, never() )
      .update( anyString(), anyInt(), any( Color.class ), anyInt(), anyInt() );
  }

  @Test
  public void testTestCaseFinished() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    ITestRunSession testRunSession = mockTestRunSession( OK, testCaseElement );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );

    setTestCaseResult( testCaseElement, Result.OK );
    testRunListener.testCaseFinished( testCaseElement );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "1 / 1", SWT.CENTER, successColor(), 1, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
  }

  @Test
  public void testTestCaseFinishedWithFailure() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    ITestRunSession testRunSession = mockTestRunSession( FAILURE, testCaseElement );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );
    setTestCaseResult( testCaseElement, FAILURE );

    testRunListener.testCaseFinished( testCaseElement );

    verify( progressUI ).update( "1 / 1", SWT.CENTER, errorColor(), 1, 1 );
    verify( progressUI ).setToolTipText( getToolTipText( testRunSession, 1 ) );
  }

  @Test
  public void testTestCaseFinishedWithError() {
    ITestCaseElement testCaseElement = mockTestCaseElement();
    ITestRunSession testRunSession = mockTestRunSession( FAILURE, testCaseElement );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );
    setTestCaseResult( testCaseElement, ERROR );

    testRunListener.testCaseFinished( testCaseElement );

    verify( progressUI ).update( "1 / 1", SWT.CENTER, errorColor(), 1, 1 );
    verify( progressUI ).setToolTipText( getToolTipText( testRunSession, 1 ) );
  }

  @Test
  public void testSecondSessionLaunched() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mockTestCaseElement(), mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    setTestCaseResult( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    setTestCaseResult( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testRunTwoSessionsFromStartToEnd() {
    ITestRunSession testRunSession1 = mockTestRunSession( OK, mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession1 );
    testRunListener.sessionStarted( testRunSession1 );
    setTestCaseResult( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession1.getChildren()[ 0 ] );
    testRunListener.sessionFinished( testRunSession1 );
    ITestRunSession testRunSession2
      = mockTestRunSession( OK, mockTestCaseElement(), mockTestCaseElement() );
    testRunListener.sessionLaunched( testRunSession2 );
    testRunListener.sessionStarted( testRunSession2 );
    setTestCaseResult( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 0 ] );
    setTestCaseResult( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ], OK );
    testRunListener.testCaseFinished( ( ITestCaseElement )testRunSession2.getChildren()[ 1 ] );
    testRunListener.sessionFinished( testRunSession2 );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( "0 / 1", SWT.CENTER, successColor(), 0, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( "1 / 1", SWT.CENTER, successColor(), 1, 1 );
    order.verify( progressUI ).setToolTipText( testRunSession1.getTestRunName() );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verify( progressUI ).update( "2 / 2", SWT.CENTER, successColor(), 2, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession2.getTestRunName() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testOutOfOrderEvents() {
    ITestCaseElement testCaseElement1 = mockTestCaseElement();
    ITestCaseElement testCaseElement2 = mockTestCaseElement();
    ITestRunSession testRunSession
      = mockTestRunSession( OK, testCaseElement1, testCaseElement2 );
    testRunListener.sessionLaunched( testRunSession );
    testRunListener.sessionStarted( testRunSession );
    testRunListener.sessionFinished( testRunSession );
    setTestCaseResult( testCaseElement1, OK );
    testRunListener.testCaseFinished( testCaseElement1 );
    setTestCaseResult( testCaseElement2, OK );
    testRunListener.testCaseFinished( testCaseElement2 );
    testRunListener.sessionFinished( testRunSession );

    InOrder order = inOrder( progressUI );
    order.verify( progressUI ).update( STARTING, SWT.LEFT, null, 0, 0 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "0 / 2", SWT.CENTER, successColor(), 0, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "1 / 2", SWT.CENTER, successColor(), 1, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verify( progressUI ).update( "2 / 2", SWT.CENTER, successColor(), 2, 2 );
    order.verify( progressUI ).setToolTipText( testRunSession.getTestRunName() );
    order.verifyNoMoreInteractions();
  }

  @Before
  public void setUp() {
    launchesListeners = new ArrayList<>();
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

  private static ITestRunSession mockTestRunSession( Result testResult, ITestElement... children )
  {
    ITestRunSession result = mock( ITestRunSession.class );
    when( result.getTestRunSession() ).thenReturn( result );
    when( result.getTestRunName() ).thenReturn( "test-run-" + new Object().hashCode() );
    when( result.getTestResult( true ) ).thenReturn( testResult );
    when( result.getChildren() ).thenReturn( children );
    for( ITestElement child : children ) {
      when( child.getTestRunSession() ).thenReturn( result );
    }
    return result;
  }

  private static ITestCaseElement mockTestCaseElement() {
    return mockTestCaseElement( Result.UNDEFINED );
  }

  private static ITestCaseElement mockTestCaseElement( Result testResult ) {
    ITestCaseElement result = mock( ITestCaseElement.class );
    when( result.getTestResult( false ) ).thenReturn( testResult );
    return result;
  }

  private static void setTestCaseResult( ITestCaseElement testCaseElement, Result testResult ) {
    when( testCaseElement.getTestResult( false ) ).thenReturn( testResult );
    when( testCaseElement.getTestRunSession().getTestResult( true ) ).thenReturn( testResult );
  }

  private static ILaunch mockLaunch( String launchConfigName ) {
    ILaunch result = mock( ILaunch.class );
    ILaunchConfiguration launchConfig = mock( ILaunchConfiguration.class );
    when( launchConfig.getName() ).thenReturn( launchConfigName );
    when( result.getLaunchConfiguration() ).thenReturn( launchConfig );
    return result;
  }

  private void fireLaunchTerminated( ILaunch launch ) {
    for( ILaunchesListener2 launchesListener : launchesListeners ) {
      launchesListener.launchesTerminated( new ILaunch[] { launch } );
    }
  }

  private static String getToolTipText( ITestRunSession testRunSession, int failedTests ) {
    return format( "{0} ({1} failed)", testRunSession.getTestRunName(), valueOf( failedTests ) );
  }

  private Color successColor() {
    return resourceManager.createColor( SUCCESS_RGB );
  }

  private Color errorColor() {
    return resourceManager.createColor( ERROR_RGB );
  }
}
