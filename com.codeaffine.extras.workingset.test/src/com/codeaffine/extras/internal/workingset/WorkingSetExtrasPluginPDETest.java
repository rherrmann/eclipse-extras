package com.codeaffine.extras.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.codeaffine.extras.internal.workingset.WorkingSetExtrasPlugin;

public class WorkingSetExtrasPluginPDETest {

  @Test
  public void testPluginId() {
    Bundle bundle = Platform.getBundle( WorkingSetExtrasPlugin.PLUGIN_ID );

    assertThat( bundle ).isNotNull();
  }

  @Test
  public void testGetInstance() {
    WorkingSetExtrasPlugin instance = WorkingSetExtrasPlugin.getInstance();

    assertThat( instance ).isNotNull();
  }
}
