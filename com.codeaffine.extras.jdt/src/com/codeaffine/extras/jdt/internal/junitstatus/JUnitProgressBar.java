package com.codeaffine.extras.jdt.internal.junitstatus;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.extras.jdt.internal.junitstatus.TextAnimation.TextAnimationPainter;

public class JUnitProgressBar extends Canvas implements TextAnimationPainter {

  private static final int ARC_SIZE = 3;
  private static final int DEFAULT_WIDTH = 160;
  private static final int DEFAULT_HEIGHT = 18;

  private final TextAnimation textAnimation;
  private String text;
  private int textAlignment;
  private int maximum;
  private int selection;
  private Color barColor;

  public JUnitProgressBar( Composite parent ) {
    super( parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS );
    text = "";
    textAlignment = SWT.LEFT;
    textAnimation = new TextAnimation( this, this );
    registerListeners();
  }

  public void setValues( String text, int textAlignment, Color barColor, int selection, int maximum ) {
    if( valuesChanged( text, textAlignment, barColor, selection, maximum ) ) {
      this.text = text;
      this.textAnimation.setText( text );
      this.textAlignment = textAlignment;
      this.barColor = barColor;
      this.selection = selection;
      this.maximum = maximum;
      redraw();
    }
  }

  public void setMaximum( int maximum ) {
    setValues( text, textAlignment, barColor, selection, maximum );
  }

  public int getMaximum() {
    return maximum;
  }

  public void setSelection( int selection ) {
    setValues( text, textAlignment, barColor, selection, maximum );
  }

  public int getSelection() {
    return selection;
  }

  public void setBarColor( Color barColor ) {
    setValues( text, textAlignment, barColor, selection, maximum );
  }

  public Color getBarColor() {
    return barColor;
  }

  public void setText( String text ) {
    setValues( text, textAlignment, barColor, selection, maximum );
  }

  public String getText() {
    return text;
  }

  public void setTextAlignment( int textAlignment ) {
    setValues( text, textAlignment, barColor, selection, maximum );
  }

  public int getTextAlignment() {
    return textAlignment;
  }

  @Override
  public void drawText( TextAnimation textAnimation ) {
    redraw();
  }

  @Override
  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    Point result = new Point( DEFAULT_WIDTH, DEFAULT_HEIGHT );
    if( wHint != SWT.DEFAULT ) {
      result.x = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      result.y = hHint;
    }
    return result;
  }

  private void registerListeners() {
    addListener( SWT.Resize, event -> redraw() );
    addListener( SWT.Paint, event -> paint( event.gc ) );
  }

  private void paint( GC gc ) {
    prepareGC( gc );
    drawBorder( gc );
    drawBar( gc );
    drawText( gc );
  }

  private void prepareGC( GC gc ) {
    if( gc.getAdvanced() ) {
      gc.setTextAntialias( SWT.ON );
    }
    gc.fillRectangle( getClientArea() );
  }

  private void drawBorder( GC gc ) {
    if( maximum > 0 ) {
      Rectangle clientArea = getClientArea();
      int x = clientArea.x;
      int y = clientArea.y;
      int width = clientArea.width - 1;
      int height = clientArea.height - 1 - 1;
      gc.setAlpha( 255 );
      gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ) );
      gc.drawRoundRectangle( x, y, width, height, ARC_SIZE, ARC_SIZE );
    }
  }

  private void drawBar( GC gc ) {
    gc.setAlpha( 200 );
    if( barColor != null ) {
      gc.setBackground( barColor );
    }
    Rectangle clientArea = getClientArea();
    int barWidth = Math.min( clientArea.width - 2, getBarWidth() );
    gc.fillRoundRectangle( 1, 1, barWidth, clientArea.height - 2 - 1, ARC_SIZE, ARC_SIZE );
  }

  private void drawText( GC gc ) {
    gc.setAlpha( 255 );
    Rectangle rect = getClientArea();
    Rectangle clientArea = new Rectangle( rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2 );
    Point textSize = gc.textExtent( text );
    int x = 3;
    if( textAlignment == SWT.CENTER ) {
      x = ( clientArea.width - textSize.x ) / 2;
    }
    int y = ( clientArea.height - textSize.y ) / 2 + 1;
    String text = textAnimation.getAnimatedText();
    Color color = getDisplay().getSystemColor( SWT.COLOR_WIDGET_FOREGROUND );
    drawText( gc, text, x, y, color, null );
    Color contrastColor = getBarForegroundColor();
    if( !color.getRGB().equals( contrastColor.getRGB() ) ) {
      int barWidth = Math.min( clientArea.width - 2, getBarWidth() );
      Rectangle clipping = new Rectangle( clientArea.x, clientArea.y, barWidth, clientArea.height );
      drawText( gc, text, x, y, contrastColor, clipping );
    }
  }

  private static void drawText( GC gc, String text, int x, int y, Color foreground, Rectangle clip ) {
    Rectangle previousClipping = gc.getClipping();
    if( clip != null ) {
      gc.setClipping( clip );
    }
    gc.setForeground( foreground );
    gc.drawText( text, x, y, SWT.DRAW_TRANSPARENT );
    if( clip != null ) {
      gc.setClipping( previousClipping );
    }
  }

  private Color getBarForegroundColor() {
    Color result = getDisplay().getSystemColor( SWT.COLOR_WIDGET_FOREGROUND );
    if( barColor != null ) {
      double y
        = ( 299 * barColor.getRed() + 587 * barColor.getGreen() + 114 * barColor.getBlue() ) / 1000;
      result = y >= 128
        ? getDisplay().getSystemColor( SWT.COLOR_BLACK )
        : getDisplay().getSystemColor( SWT.COLOR_WHITE );
    }
    return result;
  }

  int getBarWidth() {
    int result = selection;
    Rectangle clientArea = getClientArea();
    if( maximum > 0 ) {
      if( clientArea.width != 0 ) {
        result = Math.max( 0, selection * ( clientArea.width - 2 ) / maximum );
      }
    } else {
      result = 0;
    }
    return Math.min( clientArea.width - 2, result );
  }

  private boolean valuesChanged( String text, int textAlignment, Color barColor, int selection, int maximum ) {
    return !Objects.equals( this.text, text )
        || this.textAlignment != textAlignment
        || !Objects.equals( this.barColor, barColor )
        || this.selection != selection
        || this.maximum != maximum;
  }

}
