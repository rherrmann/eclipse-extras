package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.junit.Before;
import org.junit.Test;



public class JUnitModelUtilTest {

  private ITestElementContainer container;

  @Test
  public void testEmptyContainer() {
    setChildren( container, new ITestElement[ 0 ] );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testTestCaseInChildren() {
    setChildren( container, mock( ITestCaseElement.class ) );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 1 );
  }

  @Test
  public void testNonTestCaseInChilren() {
    setChildren( container, mock( ITestElement.class ) );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 0 );
  }

  @Test
  public void testTestCaseInNestedChilren() {
    ITestElementContainer nestedContainer = mock( ITestElementContainer.class );
    setChildren( nestedContainer, mock( ITestCaseElement.class ) );
    setChildren( container, nestedContainer );

    int count = JUnitModelUtil.countTestCases( container );

    assertThat( count ).isEqualTo( 1 );
  }

  private static void setChildren( ITestElementContainer container, ITestElement... children ) {
    when( container.getChildren() ).thenReturn( children );
  }

  @Before
  public void setUp() {
    container = mock( ITestElementContainer.class );
  }
}
