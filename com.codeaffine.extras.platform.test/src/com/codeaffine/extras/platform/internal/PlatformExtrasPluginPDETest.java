package com.codeaffine.extras.platform.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PlatformExtrasPluginPDETest {

  @Test
  public void testGetInstance() {
    PlatformExtrasPlugin instance = PlatformExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
