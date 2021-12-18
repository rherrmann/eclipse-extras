package com.codeaffine.extras.jdt.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class JDTExtrasPluginPDETest {

  private Bundle bundle;

  @Before
  public void setUp() {
    bundle = FrameworkUtil.getBundle(JDTExtrasPlugin.class);
  }

  @Test
  public void testPluginId() {
    assertThat(JDTExtrasPlugin.PLUGIN_ID).isEqualTo(bundle.getSymbolicName());
  }

  @Test
  public void testGetInstance() {
    JDTExtrasPlugin instance = JDTExtrasPlugin.getInstance();

    assertThat(instance.getBundle()).isEqualTo(bundle);
  }
}
