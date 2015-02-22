package com.codeaffine.extras.ide.internal.launch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LaunchConfigSearchPatternTest {

  private LaunchConfigSearchPattern searchPattern;

  @Before
  public void setUp() {
    searchPattern = new LaunchConfigSearchPattern();
  }

  @Test
  public void testSetPatternToEmptyString() {
    searchPattern.setPattern( "" );

    assertThat( searchPattern.getPattern() ).isEmpty();
  }

  @Test
  public void testSetPatternToSpace() {
    searchPattern.setPattern( " " );

    assertThat( searchPattern.getPattern() ).isEqualTo( "*" );
  }

  @Test
  public void testSetPatternToNonEmptyString() {
    searchPattern.setPattern( "pattern" );

    assertThat( searchPattern.getPattern() ).isEqualTo( "*pattern*" );
  }

  @Test
  public void testSetPatternToStringStartingWithAsterisk() {
    searchPattern.setPattern( "*" );

    assertThat( searchPattern.getPattern() ).isEqualTo( "*" );
  }
}