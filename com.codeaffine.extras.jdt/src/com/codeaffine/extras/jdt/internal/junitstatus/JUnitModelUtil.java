package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;


class JUnitModelUtil {

  static int countTestCases( ITestElementContainer testElementContainer ) {
    int result = 0;
    ITestElement[] children = testElementContainer.getChildren();
    for( ITestElement child : children ) {
      if( child instanceof ITestElementContainer ) {
        ITestElementContainer container = ( ITestElementContainer )child;
        result += countTestCases( container );
      }
      if( child instanceof ITestCaseElement ) {
        result++;
      }
    }
    return result;
  }
}
