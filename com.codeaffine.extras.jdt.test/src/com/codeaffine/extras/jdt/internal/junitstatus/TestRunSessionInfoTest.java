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

  private static final int EXECUTED_TEST_COUNT = 123;

  private ITestRunSession testRunSession;
  private TestRunSessionInfo testRunSessionInfo;

  @Before
  public void setUp() {
    testRunSession = mockTestRunSession();
    testRunSessionInfo = new TestRunSessionInfo( testRunSession, EXECUTED_TEST_COUNT );
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
    setChildren( testRunSession, mockTestCase( Result.FAILURE ) );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetFailedTestCountWithErrorResult() {
    setChildren( testRunSession, mockTestCase( Result.ERROR ) );

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
    setChildren( nestedContainer, mockTestCase( Result.ERROR ) );
    setChildren( testRunSession, nestedContainer );

    int count = testRunSessionInfo.getFailedTestCount();

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testGetExecutedTestCount() {
    int count = testRunSessionInfo.getExecutedTestCount();

    assertThat( count ).isEqualTo( EXECUTED_TEST_COUNT );
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
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.FAILURE );

    TestRunState testRunState = testRunSessionInfo.getTestRunState();

    assertThat( testRunState ).isEqualTo( TestRunState.FAILED );
  }

  @Test
  public void testGetTestRunStateWhenErroredTests() {
    when( testRunSession.getTestResult( true ) ).thenReturn( Result.ERROR );

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

  private static ITestRunSession mockTestRunSession() {
    ITestRunSession result = mock( ITestRunSession.class );
    when( result.getTestRunSession() ).thenReturn( result );
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
