package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.ProgressState;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;


public class TestRunSessionInfo {

  private final ITestRunSession testRunSession;
  private final int executedTestCount;
  private boolean countersInitialized;
  private int totalTestCount;
  private int failedTestCount;

  /*
   * [rh] The number of executed tests cannot be determined from evaluating the test result of
   * each tests within a session. In certain cases, the test result is UNDEFINED even though the
   * test was executed.
   *
   * Therefore the number of executed tests is increased in JUnitTestRunListener::testCaseFinished
   * and passed as an argument to this constructor.
   *
   * See issue #49 [JUnit StatusBar] Show wrong progress information when the same test is executed multiple times
   * https://github.com/rherrmann/eclipse-extras/issues/49
   */
  public TestRunSessionInfo( ITestElement testElement, int executedTestCount ) {
    this.testRunSession = testElement.getTestRunSession();
    this.executedTestCount = executedTestCount;
  }

  public int getTotalTestCount() {
    initializeCounters();
    return totalTestCount;
  }

  public int getFailedTestCount() {
    initializeCounters();
    return failedTestCount;
  }

  public int getExecutedTestCount() {
    return executedTestCount;
  }

  public TestRunState getTestRunState() {
    TestRunState result;
    if( testRunSession.getProgressState() == ProgressState.STOPPED ) {
      result = TestRunState.STOPPED;
    } else {
      if( isTestFailed( testRunSession.getTestResult( true ) ) ) {
        result = TestRunState.FAILED;
      } else {
        result = TestRunState.SUCCESS;
      }
    }
    return result;
  }

  private void initializeCounters() {
    if( !countersInitialized ) {
      countersInitialized = true;
      collectCounters( testRunSession );
    }
  }

  private void collectCounters( ITestElementContainer testElementContainer ) {
    ITestElement[] children = testElementContainer.getChildren();
    for( ITestElement child : children ) {
      if( child instanceof ITestElementContainer ) {
        collectCounters( ( ITestElementContainer )child );
      }
      if( child instanceof ITestCaseElement ) {
        updateCounters( ( ITestCaseElement )child );
      }
    }
  }

  private void updateCounters( ITestCaseElement testElement ) {
    Result testResult = testElement.getTestResult( false );
    if( isTestFailed( testResult ) ) {
      failedTestCount++;
    }
    totalTestCount++;
  }

  private static boolean isTestFailed( Result testResult ) {
    return Result.ERROR.equals( testResult ) || Result.FAILURE.equals( testResult );
  }

}
