package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestElement.Result;


class JUnitModelUtil {

  static int countTestCases( ITestElementContainer testElementContainer ) {
    int result = 0;
    ITestElement[] children = testElementContainer.getChildren();
    for( ITestElement child : children ) {
      if( child instanceof ITestElementContainer ) {
        result += countTestCases( ( ITestElementContainer )child );
      }
      if( child instanceof ITestCaseElement ) {
        result++;
      }
    }
    return result;
  }

  static int countFailedTests( ITestElementContainer testElementContainer ) {
    int result = 0;
    ITestElement[] children = testElementContainer.getChildren();
    for( ITestElement child : children ) {
      if( child instanceof ITestElementContainer ) {
        result += countFailedTests( ( ITestElementContainer )child );
      }
      if( child instanceof ITestCaseElement && hasFailed( child ) ) {
        result++;
      }
    }
    return result;
  }

  private static boolean hasFailed( ITestElement testElement ) {
    Result testResult = testElement.getTestResult( false );
    return testResult == Result.ERROR || testResult == Result.FAILURE;
  }
}
