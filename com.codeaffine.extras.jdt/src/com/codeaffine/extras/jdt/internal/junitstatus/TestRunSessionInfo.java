package com.codeaffine.extras.jdt.internal.junitstatus;

import static java.util.Objects.requireNonNull;
import static org.eclipse.jdt.junit.model.ITestElement.Result.UNDEFINED;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.ProgressState;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;


public class TestRunSessionInfo {

  public static boolean isTestFailed( Result testResult ) {
    return Result.ERROR.equals( testResult ) || Result.FAILURE.equals( testResult );
  }

  private final ITestRunSession testRunSession;
  private final Object lock;
  private boolean countersInitialized;
  private int totalTestCount;
  private int failedTestCount;
  private int executedTestCount;

  public TestRunSessionInfo( ITestElement testElement ) {
    requireNonNull( testElement, "testElement" );
    this.testRunSession = testElement.getTestRunSession();
    this.lock = new Object();
  }

  public String getName() {
    return testRunSession.getTestRunName();
  }

  public int getTotalTestCount() {
    synchronized( lock ) {
      initializeCounters();
      return totalTestCount;
    }
  }

  public int getFailedTestCount() {
    synchronized( lock ) {
      initializeCounters();
      return failedTestCount;
    }
  }

  public int getExecutedTestCount() {
    synchronized( lock ) {
      initializeCounters();
      return executedTestCount;
    }
  }

  public TestRunState getTestRunState() {
    TestRunState result;
    if( testRunSession.getProgressState() == ProgressState.STOPPED ) {
      result = TestRunState.STOPPED;
    } else {
      if( getFailedTestCount() > 0 ) {
        result = TestRunState.FAILED;
      } else {
        result = TestRunState.SUCCESS;
      }
    }
    return result;
  }

  public void incExecutedTestCount() {
    synchronized( lock ) {
      initializeCounters();
      executedTestCount++;
    }
  }

  public void incFailedTestCount() {
    synchronized( lock ) {
      initializeCounters();
      failedTestCount++;
    }
  }

  public boolean equalsSession( ITestElement testElement ) {
    return testElement != null && testElement.getTestRunSession().equals( testRunSession );
  }

  private void initializeCounters() {
    if( !countersInitialized ) {
      countersInitialized = true;
      collectCounters( testRunSession );
      if( executedTestCount > 0 ) {
        executedTestCount--;
      }
      if( failedTestCount > 0 ) {
        failedTestCount--;
      }
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

}
