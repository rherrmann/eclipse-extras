package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigTypeFilterPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private LaunchConfigTypeFilter filter;

  @Before
  public void setUp() {
    filter = new LaunchConfigTypeFilter();
  }

  @Test
  public void testSelectForRegularType() {
    boolean select = filter.select(null, null, launchConfigRule.getPublicTestLaunchConfigType());

    assertThat(select).isTrue();
  }

  @Test
  public void testSelectForPrivateType() {
    boolean select = filter.select(null, null, launchConfigRule.getPrivateTestLaunchConfigType());

    assertThat(select).isFalse();
  }

}
