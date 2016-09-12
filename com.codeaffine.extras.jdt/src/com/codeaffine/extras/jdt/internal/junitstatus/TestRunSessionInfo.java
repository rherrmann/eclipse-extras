package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.eclipse.jdt.junit.model.ITestElement.Result.UNDEFINED;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.ProgressState;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;


public class TestRunSessionInfo {

  private final ITestRunSession testRunSession;
  private boolean countersInitialized;
  private int totalTestCount;
  private int failedTestCount;
  private int executedTestCount;

  public TestRunSessionInfo( ITestElement testElement ) {
    this.testRunSession = testElement.getTestRunSession();
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
    initializeCounters();
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
    if( !UNDEFINED.equals( testResult ) ) {
      executedTestCount++;
    }
    if( isTestFailed( testResult ) ) {
      failedTestCount++;
    }
    totalTestCount++;
  }

  private static boolean isTestFailed( Result testResult ) {
    return Result.ERROR.equals( testResult ) || Result.FAILURE.equals( testResult );
  }

}
