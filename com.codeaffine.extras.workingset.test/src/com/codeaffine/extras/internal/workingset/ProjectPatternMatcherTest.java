package com.codeaffine.extras.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.internal.workingset.ProjectPatternMatcher;



public class ProjectPatternMatcherTest {

  private IProject project;

  @Before
  public void setUp() {
    project = mock( IProject.class );
  }

  @Test
  public void testMatchesWithEmptyPattern() {
    setProjectName( "com.codeaffine.foo" );

    boolean matches = matchesProjectName( "" );

    assertThat( matches ).isFalse();
  }

  @Test
  public void testMatchesWithMatchingPattern() {
    setProjectName( "com.codeaffine.foo" );

    boolean matches = matchesProjectName( "com.*" );

    assertThat( matches ).isTrue();
  }

  @Test
  public void testMatchesWithNonMatchingPattern() {
    setProjectName( "com.codeaffine.foo" );

    boolean matches = matchesProjectName( "bar.*" );

    assertThat( matches ).isFalse();
  }

  @Test
  public void testMatchesWithInvalidPattern() {
    setProjectName( "com.codeaffine.foo" );

    boolean matches = matchesProjectName( "*code" );

    assertThat( matches ).isFalse();
  }

  @Test
  public void testIsPatternValidWithInvalidPattern() {
    ProjectPatternMatcher patternMatcher = new ProjectPatternMatcher( "*code" );

    boolean validPattern = patternMatcher.isPatternValid();

    assertThat( validPattern ).isFalse();
  }

  @Test
  public void testIsPatternValidWithValidPattern() {
    ProjectPatternMatcher patternMatcher = new ProjectPatternMatcher( ".*" );

    boolean validPattern = patternMatcher.isPatternValid();

    assertThat( validPattern ).isTrue();
  }

  private boolean matchesProjectName( String pattern ) {
    return new ProjectPatternMatcher( pattern ).matches( project );
  }

  private void setProjectName( String name ) {
    when( project.getName() ).thenReturn( name );
  }
}
