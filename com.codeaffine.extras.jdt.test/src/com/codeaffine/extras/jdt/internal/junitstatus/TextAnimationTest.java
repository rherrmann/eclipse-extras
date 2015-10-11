package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.jdt.internal.junitstatus.TextAnimation.TextAnimationPainter;



public class TextAnimationTest {

  private static final String TEXT_WITH_DOTS = "Running...";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private TextAnimationPainter textAnimationPainter;
  private Shell widget;
  private TextAnimation textAnimation;

  @Test
  public void testSetTextWithDots() {
    textAnimation.setText( TEXT_WITH_DOTS );

    waitFor( 300 );

    verify( textAnimationPainter, atLeast( 3 ) ).drawText( textAnimation );
  }

  @Test
  public void testSetTextWithoutDots() {
    textAnimation.setText( "foo" );

    waitFor( 300 );

    verify( textAnimationPainter, never() ).drawText( any( TextAnimation.class ) );
  }

  @Test
  public void testGetTextWidthDots() {
    String text = TEXT_WITH_DOTS;
    textAnimation.setText( text );

    String returnedText = textAnimation.getText();

    assertThat( returnedText ).isEqualTo( text );
  }

  @Test
  public void testGetAnimatedText() {
    textAnimation.setText( "Running..." );

    String animatedText = textAnimation.getAnimatedText();

    assertThat( animatedText ).isEqualTo( "Running" );
  }

  @Test
  public void testAnimationCycle() {
    textAnimation.setText( "Running..." );

    String animatedText1 = triggerAnimationCycle();
    String animatedText2 = triggerAnimationCycle();
    String animatedText3 = triggerAnimationCycle();
    String animatedText4 = triggerAnimationCycle();
    String animatedText5 = triggerAnimationCycle();

    assertThat( animatedText1 ).isEqualTo( "Running." );
    assertThat( animatedText2 ).isEqualTo( "Running.." );
    assertThat( animatedText3 ).isEqualTo( "Running..." );
    assertThat( animatedText4 ).isEqualTo( "Running" );
    assertThat( animatedText5 ).isEqualTo( "Running." );
  }

  @Test
  public void testDisable() {
    textAnimation.setText( TEXT_WITH_DOTS );

    textAnimation.disable();
    waitFor( 300 );

    verify( textAnimationPainter, never() ).drawText( any( TextAnimation.class ) );
  }

  @Test
  public void testDisposeWidgetDisablesAnimation() {
    textAnimation.setText( TEXT_WITH_DOTS );

    widget.dispose();
    waitFor( 300 );

    verify( textAnimationPainter, never() ).drawText( any( TextAnimation.class ) );
  }

  @Before
  public void setUp() {
    textAnimationPainter = mock( TextAnimationPainter.class );
    widget = displayHelper.createShell();
    textAnimation = new TextAnimation( widget, textAnimationPainter, 1 );
  }

  private static void waitFor( long milliseconds ) {
    long start = System.currentTimeMillis();
    while( System.currentTimeMillis() - start <= milliseconds ) {
      try {
        Thread.sleep( 1 );
      } catch( InterruptedException ignore ) {
        Thread.interrupted();
      }
      DisplayHelper.flushPendingEvents();
    }
  }

  private String triggerAnimationCycle() {
    textAnimation.run();
    return textAnimation.getAnimatedText();
  }
}
