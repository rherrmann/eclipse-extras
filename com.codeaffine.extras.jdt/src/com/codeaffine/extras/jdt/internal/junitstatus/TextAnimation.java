package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.google.common.base.Strings.repeat;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

public class TextAnimation implements Runnable {

  private static final String DOTS = "...";

  public interface TextAnimationPainter {
    void drawText( TextAnimation textAnimation );
  }

  private static final int DEFAULT_ANIMATION_INTERVAL = 400;

  private final int animationInterval;
  private final TextAnimationPainter textAnimationPainter;
  private final Widget widget;
  private String text;
  private int dotCount;

  public TextAnimation( Widget widget, TextAnimationPainter textAnimationPainter  ) {
    this( widget, textAnimationPainter, DEFAULT_ANIMATION_INTERVAL );
  }

  public TextAnimation( Widget widget , TextAnimationPainter textAnimationPainter , int animationInterval  ) {
    this.animationInterval = animationInterval;
    this.textAnimationPainter = textAnimationPainter;
    this.widget = widget;
    this.text = "";
    this.widget.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        disable();
      }
    } );
  }

  public void disable() {
    widget.getDisplay().timerExec( -1, this );
  }

  public void setText( String text ) {
    if( !this.text.equals( text ) ) {
      disable();
      this.text = text;
      updateAnimation();
    }
  }

  public String getText() {
    return text;
  }

  public String getAnimatedText() {
    return getUnanimatedText() + repeat( ".", dotCount );
  }

  private void updateAnimation() {
    dotCount = 0;
    if( text.endsWith( DOTS ) ) {
      widget.getDisplay().timerExec( animationInterval, this );
    }
  }

  private String getUnanimatedText() {
    String result = text;
    if( result.endsWith( DOTS ) ) {
      result = result.substring( 0, text.length() - 3 );
    }
    return result;
  }

  @Override
  public void run() {
    if( !widget.isDisposed() ) {
      increaseDotCount();
      textAnimationPainter.drawText( this );
      widget.getDisplay().timerExec( animationInterval, this );
    }
  }

  private void increaseDotCount() {
    dotCount++;
    if( dotCount > 3 ) {
      dotCount = 0;
    }
  }

}