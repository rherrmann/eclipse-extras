package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.junit.Before;
import org.junit.Test;

public class JUnitModelUtilTest {

  private ITestElementContainer container;

  @Test
  public void testCountTestCasesWithEmptyContainer() {
    setChildren( container, new ITestElement[ 0 ] );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testCountTestCasesWithTestCaseInChildren() {
    setChildren( container, mock( ITestCaseElement.class ) );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testCountTestCasesWithNonTestCaseInChilren() {
    setChildren( container, mock( ITestElement.class ) );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testCountTestCasesWithTestCaseInNestedChilren() {
    ITestElementContainer nestedContainer = mock( ITestElementContainer.class );
    setChildren( nestedContainer, mock( ITestCaseElement.class ) );
    setChildren( container, nestedContainer );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testCountFailedTests() {
    setChildren( container, new ITestElement[ 0 ] );

    int count = JUnitModelUtil.countFailedTests( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testCountFailedTestsWithFailureResult() {
    setChildren( container, createTestCase( Result.FAILURE ) );

    int count = JUnitModelUtil.countFailedTests( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testCountFailedTestsWithErrorResult() {
    setChildren( container, createTestCase( Result.ERROR ) );

    int count = JUnitModelUtil.countFailedTests( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testCountFailedTestsWithOkResult() {
    setChildren( container, createTestCase( Result.OK ) );

    int count = JUnitModelUtil.countFailedTests( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testCountFailedTestsWithUndefinedResult() {
    setChildren( container, createTestCase( Result.UNDEFINED ) );

    int count = JUnitModelUtil.countFailedTests( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testCountFailedTestsWithErrorInNestedChilren() {
    ITestElementContainer nestedContainer = mock( ITestElementContainer.class );
    setChildren( nestedContainer, createTestCase( Result.ERROR ) );
    setChildren( container, nestedContainer );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Before
  public void setUp() {
    container = mock( ITestElementContainer.class );
  }

  private static ITestCaseElement createTestCase( Result testResult ) {
    ITestCaseElement result = mock( ITestCaseElement.class );
    when( result.getTestResult( false ) ).thenReturn( testResult );
    return result;
  }

  private static void setChildren( ITestElementContainer container, ITestElement... children ) {
    when( container.getChildren() ).thenReturn( children );
  }
}
