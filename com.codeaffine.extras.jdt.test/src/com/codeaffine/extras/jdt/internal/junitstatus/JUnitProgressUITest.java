package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.codeaffine.eclipse.swt.test.util.DisplayHelper.flushPendingEvents;
import static com.codeaffine.extras.test.util.ConcurrentHelper.runInThread;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;

public class JUnitProgressUITest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private TestableJUnitProgressBar progressBar;
  private JUnitProgressUI progressUI;

  @Test
  public void testUpdate() {
    String text = "text";
    int textAslignment = SWT.CENTER;
    Color color = progressBar.getDisplay().getSystemColor( SWT.COLOR_GREEN );
    int selection = 1;
    int maximum = 2;

    progressUI.update( text, textAslignment, color, selection, maximum );
    flushPendingEvents();

    assertThat( progressBar.getText() ).isEqualTo( text );
    assertThat( progressBar.getTextAlignment() ).isEqualTo( textAslignment );
    assertThat( progressBar.getBarColor() ).isEqualTo( color );
    assertThat( progressBar.getSelection() ).isEqualTo( selection );
    assertThat( progressBar.getMaximum() ).isEqualTo( maximum );
  }

  @Test
  public void testUpdateFromBackgroundThread() {
    final String text = "text";
    final int textAslignment = SWT.CENTER;
    final Color color = progressBar.getDisplay().getSystemColor( SWT.COLOR_GREEN );
    final int selection = 1;
    final int maximum = 2;

    runInThread( new Runnable() {
      @Override
      public void run() {
        progressUI.update( text, textAslignment, color, selection, maximum );
      }
    } );
    flushPendingEvents();

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
    flushPendingEvents();

    assertThat( progressBar.getToolTipText() ).isEqualTo( toolTipText );
  }

  @Test
  public void testSetToolTipTextFromBckgroundThread() {
    final String text = "text";

    runInThread( new Runnable() {
      @Override
      public void run() {
        progressUI.setToolTipText( text );
      }
    } );
    flushPendingEvents();

    assertThat( progressBar.getToolTipText() ).isEqualTo( text );
  }

  @Test
  public void testSetToolTipTextToSameValue() {
    progressUI.setToolTipText( "text" );

    progressUI.setToolTipText( "text" );
    flushPendingEvents();

    assertThat( progressBar.setToolTipTextInvocationCount ).isEqualTo( 1 );
  }

  @Test
  public void testSetToolTipTextToDifferentValue() {
    String otherText = "other text";
    progressUI.setToolTipText( "text" );

    progressUI.setToolTipText( otherText );
    flushPendingEvents();

    assertThat( progressBar.getToolTipText() ).isEqualTo( otherText );
  }

  @Before
  public void setUp() {
    progressBar = new TestableJUnitProgressBar( displayHelper.createShell() );
    progressUI = new JUnitProgressUI( progressBar );
  }

  private static class TestableJUnitProgressBar extends JUnitProgressBar {

    int setToolTipTextInvocationCount;

    TestableJUnitProgressBar( Composite parent ) {
      super( parent );
    }

    @Override
    public void setToolTipText( String string ) {
      setToolTipTextInvocationCount++;
      super.setToolTipText( string );
    }

  }
}
