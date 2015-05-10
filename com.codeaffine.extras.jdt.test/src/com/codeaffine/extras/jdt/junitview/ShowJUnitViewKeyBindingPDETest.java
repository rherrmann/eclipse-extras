package com.codeaffine.extras.jdt.junitview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jface.util.Util.WS_CARBON;

import org.junit.Test;

import com.codeaffine.extras.test.util.KeyBindingInfo;
import com.codeaffine.extras.test.util.KeyBindingInspector;


public class ShowJUnitViewKeyBindingPDETest {

  private static final String KEY_SEQUENCE = "M2+M3+Q U";
  private static final String VIEW_PARAMETER_NAME = "org.eclipse.ui.views.showView.viewId";
  private static final String JUNIT_VIEW_ID = "org.eclipse.jdt.junit.ResultView";
  private static final String SHOW_VIEW_COMMAND_ID = "org.eclipse.ui.views.showView";

  @Test
  public void testGeneralKeyBinding() {
    KeyBindingInfo keyBinding = KeyBindingInspector.keyBindingFor( KEY_SEQUENCE );

    assertThat( keyBinding.getSchemeId() ).isEqualTo( KeyBindingInspector.DEFAULT_SCHEME_ID );
    assertThat( keyBinding.getCommandId() ).isEqualTo( SHOW_VIEW_COMMAND_ID );
    assertThat( keyBinding.getContextId() ).isNull();
    assertThat( keyBinding.getPlatform() ).isNull();
    assertThat( keyBinding.getParameters() ).hasSize( 1 );
    assertThat( keyBinding.getParameters()[ 0 ].getId() ).isEqualTo( VIEW_PARAMETER_NAME );
    assertThat( keyBinding.getParameters()[ 0 ].getValue() ).isEqualTo( JUNIT_VIEW_ID );
  }

  @Test
  public void testCarbonKeyBindingOverride() {
    KeyBindingInfo keyBinding = KeyBindingInspector.keyBindingFor( KEY_SEQUENCE, WS_CARBON );

    assertThat( keyBinding.getSchemeId() ).isEqualTo( KeyBindingInspector.DEFAULT_SCHEME_ID );
    assertThat( keyBinding.getCommandId() ).isNull();
    assertThat( keyBinding.getContextId() ).isNull();
    assertThat( keyBinding.getParameters() ).isEmpty();
  }

  @Test
  public void testCarbonKeyBinding() {
    KeyBindingInfo keyBinding = KeyBindingInspector.keyBindingFor( "COMMAND+ALT+Q U", WS_CARBON );

    assertThat( keyBinding.getSchemeId() ).isEqualTo( KeyBindingInspector.DEFAULT_SCHEME_ID );
    assertThat( keyBinding.getCommandId() ).isEqualTo( SHOW_VIEW_COMMAND_ID );
    assertThat( keyBinding.getContextId() ).isNull();
    assertThat( keyBinding.getPlatform() ).isEqualTo( WS_CARBON );
    assertThat( keyBinding.getParameters() ).hasSize( 1 );
    assertThat( keyBinding.getParameters()[ 0 ].getId() ).isEqualTo( VIEW_PARAMETER_NAME );
    assertThat( keyBinding.getParameters()[ 0 ].getValue() ).isEqualTo( JUNIT_VIEW_ID );
  }

}
