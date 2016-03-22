package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class WorkingSetExtrasPluginPDETest {

  private Bundle bundle;

  @Before
  public void setUp() {
    bundle = FrameworkUtil.getBundle( WorkingSetExtrasPlugin.class );
  }

  @Test
  public void testPluginId() {
    assertThat( WorkingSetExtrasPlugin.PLUGIN_ID ).isEqualTo( bundle.getSymbolicName() );
  }

  @Test
  public void testGetInstance() {
    WorkingSetExtrasPlugin instance = WorkingSetExtrasPlugin.getInstance();

    assertThat( instance.getBundle() ).isEqualTo( bundle );
  }
}
