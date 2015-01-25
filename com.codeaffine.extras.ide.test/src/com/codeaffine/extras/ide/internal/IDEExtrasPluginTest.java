package com.codeaffine.extras.ide.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.codeaffine.extras.ide.internal.IDEExtrasPlugin;

public class IDEExtrasPluginTest {

  @Test
  public void testPluginId() {
    Bundle bundle = Platform.getBundle( IDEExtrasPlugin.PLUGIN_ID );

    assertThat( bundle ).isNotNull();
  }

  @Test
  public void testGetInstance() {
    IDEExtrasPlugin instance = IDEExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
