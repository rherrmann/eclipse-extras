package com.codeaffine.extras.launch.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.codeaffine.extras.launch.internal.LaunchExtrasPlugin;

public class LaunchExtrasPluginPDETest {

  @Test
  public void testPluginId() {
    Bundle bundle = Platform.getBundle( LaunchExtrasPlugin.PLUGIN_ID );

    assertThat( bundle ).isNotNull();
  }

  @Test
  public void testGetInstance() {
    LaunchExtrasPlugin instance = LaunchExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
