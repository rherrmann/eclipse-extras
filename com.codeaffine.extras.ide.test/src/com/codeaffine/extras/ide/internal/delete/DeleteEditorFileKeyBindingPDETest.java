package com.codeaffine.extras.ide.internal.delete;

import static com.codeaffine.extras.test.util.KeyBindingInspector.DEFAULT_SCHEME_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.extras.test.util.KeyBindingInfo;
import com.codeaffine.extras.test.util.KeyBindingInspector;


public class DeleteEditorFileKeyBindingPDETest {

  private static final String KEY_SEQUENCE = "M3+Del";

  @Test
  public void testGeneralKeyBinding() {
    KeyBindingInfo keyBinding = KeyBindingInspector.keyBindingFor( KEY_SEQUENCE );

    assertThat( keyBinding.getSchemeId() ).isEqualTo( DEFAULT_SCHEME_ID );
    assertThat( keyBinding.getCommandId() ).isEqualTo( DeleteEditorFileHandler.COMMAND_ID );
    assertThat( keyBinding.getContextId() ).isNull();
    assertThat( keyBinding.getPlatform() ).isNull();
    assertThat( keyBinding.getParameters() ).isEmpty();
  }

}
