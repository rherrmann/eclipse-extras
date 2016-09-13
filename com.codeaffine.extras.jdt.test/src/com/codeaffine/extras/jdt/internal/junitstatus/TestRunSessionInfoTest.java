package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.ProgressState;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.junit.Before;
import org.junit.Test;

public class TestRunSessionInfoTest {

  private ITestRunSession testRunSession;
  private TestRunSessionInfo testRunSessionInfo;

  @Before
  public void setUp() {
    testRunSession = mockTestRunSession();
    testRunSessionInfo = new TestRunSessionInfo( testRunSession );
  }

  @Test(expected=NullPointerException.class)
  public void testConstructorWithNullArgument() {
    new TestRunSessionInfo( null );
  }

  @Test
  public void testGetName() {
    when( testRunSession.getTestRunName() ).thenReturn( "test-run-name" );

    String name = testRunSessionInfo.getName();

    assertThat( name ).isEqualTo( testRunSession.getTestRunName() );
  }

  @Test
  public void testGetTotalTestCountWithEmptyContainer() {
    setChildren( testRunSession, new ITestElement[ 0 ] );

    int count = testRunSessionInfo.getTotalTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetTotalTestCountWithTestCaseInChildren() {
    setChildren( testRunSession, mock( ITestCaseElement.class ) );

    int count = testRunSessionInfo.getTotalTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetTotalTestCountWithNonTestCaseInChilren() {
    setChildren( testRunSession, mock( ITestElement.class ) );

    int count = testRunSessionInfo.getTotalTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetTotalTestCountWithTestCaseInNestedChilren() {
    ITestElementContainer nestedContainer = mock( ITestElementContainer.class );
    setChildren( nestedContainer, mock( ITestCaseElement.class ) );
    setChildren( testRunSession, nestedContainer );

    int count = testRunSessionInfo.getTotalTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetFailedTestCount() {
    setChildren( testRunSession, new ITestElement[ 0 ] );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetFailedTestCountWithFailureResult() {
    setChildren( testRunSession, mockTestCase( Result.FAILURE ), mockTestCase( Result.FAILURE ) );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetFailedTestCountWithErrorResult() {
    setChildren( testRunSession, mockTestCase( Result.ERROR ), mockTestCase( Result.ERROR ) );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetFailedTestCountWithOkResult() {
    setChildren( testRunSession, mockTestCase( Result.OK ) );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetFailedTestCountWithUndefinedResult() {
    setChildren( testRunSession, mockTestCase( Result.UNDEFINED ) );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetFailedTestCountWithErrorInNestedChilren() {
    ITestElementContainer nestedContainer = mock( ITestElementContainer.class );
    setChildren( nestedContainer, mockTestCase( Result.ERROR ), mockTestCase( Result.ERROR ) );
    setChildren( testRunSession, nestedContainer );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCount() {
    setChildren( testRunSession, mockTestCase( Result.UNDEFINED ) );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetExecutedTestCountWithEmptyContainer() {
    setChildren( testRunSession, new ITestElement[ 0 ] );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testGetExecutedTestCountWithErroredTest() {
    setChildren( testRunSession, mockTestCase( Result.ERROR ), mockTestCase( Result.ERROR ) );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCountWithFailedTest() {
    setChildren( testRunSession, mockTestCase( Result.FAILURE ), mockTestCase( Result.FAILURE ) );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCountWithIgnoredTest() {
    setChildren( testRunSession, mockTestCase( Result.IGNORED ), mockTestCase( Result.IGNORED ) );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCountWithSucceededTest() {
    setChildren( testRunSession, mockTestCase( Result.OK ), mockTestCase( Result.OK ) );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCountWithNestedChilren() {
    ITestElementContainer nestedContainer1 = mock( ITestElementContainer.class );
    ITestElementContainer nestedContainer2 = mock( ITestElementContainer.class );
    setChildren( nestedContainer1, mockTestCase( Result.OK ), mockTestCase( Result.OK ) );
    setChildren( nestedContainer2, mockTestCase( Result.FAILURE ) );
    setChildren( testRunSession, nestedContainer1, nestedContainer2 );

    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( 2 );
  }

  @Test
  public void testGetTestRunStateWhenStopped() {
    when( testRunSession.getProgressState() ).thenReturn( ProgressState.STOPPED );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.STOPPED );
  }

  @Test
  public void testGetTestRunStateWhenStoppedAndFailedTests() {
    when( testRunSession.getProgressState() ).thenReturn( ProgressState.STOPPED );
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.FAILURE );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.STOPPED );
  }

  @Test
  public void testGetTestRunStateWhenFailedTests() {
    setChildren( testRunSession, mockTestCase( Result.FAILURE ), mockTestCase( Result.FAILURE ) );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.FAILED );
  }

  @Test
  public void testGetTestRunStateWhenErroredTests() {
    setChildren( testRunSession, mockTestCase( Result.ERROR ), mockTestCase( Result.ERROR ) );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.FAILED );
  }

  @Test
  public void testGetTestRunStateAfterFailedTestCountIncreased() {
    testRunSessionInfo.incFailedTestCount();

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.FAILED );
  }

  @Test
  public void testGetTestRunStateWhenIgnoredTests() {
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.IGNORED );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.SUCCESS );
  }

  @Test
  public void testGetTestRunStateWhenSucceededTests() {
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.OK );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.SUCCESS );
  }

  @Test
  public void testGetTestRunStateWhenUndefinedSessionResult() {
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.UNDEFINED );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.SUCCESS );
  }

  @Test
  public void testGetTestRunStateWithNullProgressStateAndTestResult() {
    when( testRunSession.getProgressState() ).thenReturn( null );
    when( testRunSession.getTestResult( true ) ).thenReturn( null );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.SUCCESS );
  }

  @Test
  public void testEqualsSessionWithDifferentSession() {
    boolean equalsSession = testRunSessionInfo.equalsSession( mockTestRunSession() );

    assertThat( equalsSession ).isFalse();
  }

  @Test
  public void testEqualsSessionWithDifferentSame() {
    boolean equalsSession = testRunSessionInfo.equalsSession( testRunSession );

    assertThat( equalsSession ).isTrue();
  }

  @Test
  public void testtestEqualsSessionWithNullArgument() {
    boolean equalsSession = testRunSessionInfo.equalsSession( null );

    assertThat( equalsSession ).isFalse();
  }

  @Test
  public void testIncExecutedTestCount() {
    testRunSessionInfo.incExecutedTestCount();

    assertThat( testRunSessionInfo.getExecutedTestCount() ).isEqualTo( 1 );
  }

  @Test
  public void testIncFailedTestCount() {
    testRunSessionInfo.incFailedTestCount();

    assertThat( testRunSessionInfo.getFailedTestCount() ).isEqualTo( 1 );
  }

  private static ITestRunSession mockTestRunSession() {
    ITestRunSession result = mock( ITestRunSession.class );
    when( result.getTestRunSession() ).thenReturn( result );
    setChildren( result, new ITestElement[ 0 ] );
    return result;
  }

  private static ITestCaseElement mockTestCase( Result testResult ) {
    ITestCaseElement result = mock( ITestCaseElement.class );
    when( result.getTestResult( false ) ).thenReturn( testResult );
    return result;
  }

  private static void setChildren( ITestElementContainer container, ITestElement... children ) {
    when( container.getChildren() ).thenReturn( children );
  }
}
