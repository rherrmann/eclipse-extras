package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class ButtonsTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private Button button;

  @Before
  public void setUp() {
    button = new Button( displayHelper.createShell(), SWT.PUSH );
  }

  @Test
  public void testComputePreferredButtonWidth() {
    int width = Buttons.computePreferredButtonWidth( button );

    assertThat( width ).isGreaterThan( 0 );
  }

  @Test
  public void testComputePreferredButtonWidthForWideButton() {
    int emptyWidth = Buttons.computePreferredButtonWidth( button );
    button.setText( longText() );

    int width = Buttons.computePreferredButtonWidth( button );

    assertThat( width ).isGreaterThan( emptyWidth );
  }

  private static String longText() {
    return new String( new char[ 500 ] ).replace( "\0", "X" );
  }
}
