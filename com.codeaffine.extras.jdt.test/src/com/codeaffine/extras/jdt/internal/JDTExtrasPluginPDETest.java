package com.codeaffine.extras.jdt.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class JDTExtrasPluginPDETest {

  @Test
  public void testPluginId() {
    Bundle bundle = Platform.getBundle( JDTExtrasPlugin.PLUGIN_ID );

    assertThat( bundle ).isNotNull();
  }

  @Test
  public void testGetInstance() {
    JDTExtrasPlugin instance = JDTExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
