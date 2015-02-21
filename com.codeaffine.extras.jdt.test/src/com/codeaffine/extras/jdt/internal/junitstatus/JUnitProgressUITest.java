package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class JUnitProgressUITest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();
  private JUnitProgressBar progressBar;
  private JUnitProgressUI progressUI;

  @Test
  public void testUpdate() {
    String text = "text";
    int textAslignment = SWT.CENTER;
    Color color = progressBar.getDisplay().getSystemColor( SWT.COLOR_GREEN );
    int selection = 1;
    int maximum = 2;

    progressUI.update( text, textAslignment, color, selection, maximum );

    assertThat( progressBar.getText() ).isEqualTo( text );
    assertThat( progressBar.getTextAlignment() ).isEqualTo( textAslignment );
    assertThat( progressBar.getBarColor() ).isEqualTo( color );
    assertThat( progressBar.getSelection() ).isEqualTo( selection );
    assertThat( progressBar.getMaximum() ).isEqualTo( maximum );
  }

  @Test
  public void testSetToolTipText() {
    String toolTipText = "tooltip-text";

    progressUI.setToolTipText( toolTipText );

    assertThat( progressBar.getToolTipText() ).isEqualTo( toolTipText );
  }

  @Test
  public void testGetWidget() {
    Widget widget = progressUI.getWidget();

    assertThat( widget ).isEqualTo( progressBar );
  }

  @Before
  public void setUp() {
    progressBar = new JUnitProgressBar( displayHelper.createShell() );
    progressUI = new JUnitProgressUI( progressBar );
  }
}
