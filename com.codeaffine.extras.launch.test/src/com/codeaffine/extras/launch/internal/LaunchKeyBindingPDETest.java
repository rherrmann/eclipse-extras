package com.codeaffine.extras.launch.internal;

import static com.codeaffine.extras.test.util.KeyBindingInspector.DEFAULT_SCHEME_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.OpenLaunchDialogHander;
import com.codeaffine.extras.test.util.KeyBindingInfo;
import com.codeaffine.extras.test.util.KeyBindingInspector;


public class LaunchKeyBindingPDETest {

  private static final String KEY_SEQUENCE = "M2+F11";

  @Test
  public void testGeneralKeyBinding() {
    KeyBindingInfo keyBinding = KeyBindingInspector.keyBindingFor( KEY_SEQUENCE );

    assertThat( keyBinding.getSchemeId() ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( keyBinding.getCommandId() ).isEqualTo( OpenLaunchDialogHander.COMMAND_ID );
    assertThat( keyBinding.getContextId() ).isNull();
    assertThat( keyBinding.getPlatform() ).isNull();
    assertThat( keyBinding.getParameters() ).isEmpty();
  }

}
