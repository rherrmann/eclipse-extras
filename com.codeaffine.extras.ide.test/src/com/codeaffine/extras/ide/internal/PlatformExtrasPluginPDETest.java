package com.codeaffine.extras.ide.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.extras.ide.internal.IDEExtrasPlugin;

public class PlatformExtrasPluginPDETest {

  @Test
  public void testGetInstance() {
    IDEExtrasPlugin instance = IDEExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
