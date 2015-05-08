package com.codeaffine.extras.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.internal.workingset.PreviewComparator;

public class PreviewComparatorTest {

  private PreviewComparator comparator;

  @Before
  public void setUp() {
    comparator = new PreviewComparator( "foo.*" );
  }

  @Test
  public void testCategory() {
    int matchingCategory = comparator.category( createProject( "foo-bar" ) );
    int nonMatchingCategory = comparator.category( createProject( "bar-bar" ) );

    assertThat( matchingCategory ).isLessThan( nonMatchingCategory );
  }

  @Test(expected=ClassCastException.class)
  public void testCategoryForNonProject() {
    comparator.category( new Object() );
  }

  private static IProject createProject( String name ) {
    IProject result = mock( IProject.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }
}
