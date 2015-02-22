package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.eclipse.swt.test.util.SWTIgnoreConditions.NonWindowsPlatform;
import com.codeaffine.test.util.junit.ConditionalIgnoreRule;
import com.codeaffine.test.util.junit.ConditionalIgnoreRule.ConditionalIgnore;


public class JUnitProgressBarTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();
  public final ConditionalIgnoreRule ignoreRule = new ConditionalIgnoreRule();

  private JUnitProgressBar progressBar;

  @Test
  public void testInitialValues() {
    assertThat( progressBar.getText() ).isEmpty();
    assertThat( progressBar.getTextAlignment() ).isEqualTo( SWT.LEFT );
    assertThat( progressBar.getBarColor() ).isNull();
    assertThat( progressBar.getSelection() ).isEqualTo( 0 );
    assertThat( progressBar.getMaximum() ).isEqualTo( 0 );
  }

  @ConditionalIgnore(condition=NonWindowsPlatform.class)
  @Test
  public void testStyleNoBackground() {
    assertThat( progressBar.getStyle() & SWT.NO_BACKGROUND ).isNotZero();
  }

  @Test
  public void testStyleDoubleBuffered() {
    assertThat( progressBar.getStyle() & SWT.DOUBLE_BUFFERED ).isNotZero();
  }

  @Test
  public void testStyleNoFocus() {
    assertThat( progressBar.getStyle() & SWT.NO_FOCUS ).isNotZero();
  }

  @Test
  public void testSetText() {
    String expected = "text";
    progressBar.setText( expected );

    String text = progressBar.getText();

    assertThat( text ).isEqualTo( expected );
  }

  @Test
  public void testSetTextAlignment() {
    int expected = SWT.CENTER;
    progressBar.setTextAlignment( expected );

    assertThat( progressBar.getTextAlignment() ).isEqualTo( expected );
  }

  @Test
  public void testSetBarColor() {
    Color expected = displayHelper.getDisplay().getSystemColor( SWT.COLOR_RED );
    progressBar.setBarColor( expected );

    assertThat( progressBar.getBarColor() ).isEqualTo( expected );
  }

  @Test
  public void testSetSelection() {
    int expected = 2;
    progressBar.setSelection( expected );

    assertThat( progressBar.getSelection() ).isEqualTo( expected );
  }

  @Test
  public void testSetMaximum() {
    int expected = 2;
    progressBar.setMaximum( expected );

    assertThat( progressBar.getMaximum() ).isEqualTo( expected );
  }

  @Test
  public void testSetValue() {
    String text = "text";
    int textAlignment = SWT.CENTER;
    Color barColor = displayHelper.getDisplay().getSystemColor( SWT.COLOR_RED );
    int selection = 1;
    int maximum = 100;

    progressBar.setValues( text, textAlignment, barColor, selection, maximum );

    assertThat( progressBar.getText() ).isEqualTo( text );
    assertThat( progressBar.getTextAlignment() ).isEqualTo( textAlignment );
    assertThat( progressBar.getBarColor() ).isEqualTo( barColor );
    assertThat( progressBar.getSelection() ).isEqualTo( selection );
    assertThat( progressBar.getMaximum() ).isEqualTo( maximum );
  }

  @Test
  public void testGetBarWidth() {
    setProgressBarWidth( 100 );
    progressBar.setMaximum( 100 );

    progressBar.setSelection( 50 );

    assertThat( progressBar.getBarWidth() ).isEqualTo( 50 );
  }

  @Test
  public void testGetBarWidthForSelectionLargetThanWidth() {
    setProgressBarWidth( 100 );
    progressBar.setMaximum( 200 );

    progressBar.setSelection( 100 );

    assertThat( progressBar.getBarWidth() ).isEqualTo( 50 );
  }

  @Test
  public void testGetBarWidthForSelectionExceedingMaximum() {
    setProgressBarWidth( 100 );
    progressBar.setMaximum( 100 );

    progressBar.setSelection( 200 );

    assertThat( progressBar.getBarWidth() ).isEqualTo( 100 );
  }

  @Test
  public void testGetBarWidthForZeroSelection() {
    setProgressBarWidth( 100 );
    progressBar.setMaximum( 100 );

    progressBar.setSelection( 0 );

    assertThat( progressBar.getBarWidth() ).isEqualTo( 0 );
  }

  @Test
  public void testGetBarWidthForZeroMaximum() {
    setProgressBarWidth( 100 );
    progressBar.setMaximum( 0 );

    progressBar.setSelection( 10 );

    assertThat( progressBar.getBarWidth() ).isEqualTo( 0 );
  }

  @Before
  public void setUp() {
    progressBar = new JUnitProgressBar( displayHelper.createShell() );
  }

  private void setProgressBarWidth( int width ) {
    Rectangle trim = progressBar.computeTrim( 0, 0, width, 10 );
    progressBar.setSize( trim.width + 2, 10 );
  }

}
