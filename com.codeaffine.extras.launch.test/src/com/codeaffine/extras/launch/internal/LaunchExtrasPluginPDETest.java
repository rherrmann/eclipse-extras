package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class LaunchExtrasPluginPDETest {

  private Bundle bundle;

  @Before
  public void setUp() {
    bundle = FrameworkUtil.getBundle( LaunchExtrasPlugin.class );
  }

  @Test
  public void testPluginId() {
    assertThat( LaunchExtrasPlugin.PLUGIN_ID ).isEqualTo( bundle.getSymbolicName() );
  }

  @Test
  public void testGetInstance() {
    LaunchExtrasPlugin instance = LaunchExtrasPlugin.getInstance();

    assertThat( instance.getBundle() ).isEqualTo( bundle );
  }
}
