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
    int style = ( maximum > 0 ? SWT.BORDER : SWT.NONE ) | textAlignment;
    String text = textAnimation.getAnimatedText();
    new Painter( gc, getClientArea(), text, barColor, getBarWidth(), style  ).paint();
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

  private static class Painter {
    private final GC gc;
    private final Rectangle clientArea;
    private final String text;
    private final Color barColor;
    private final int barWidth;
    private final int style;

    Painter( GC gc, Rectangle clientArea, String text, Color barColor, int barWidth, int style ) {
      this.gc = gc;
      this.clientArea = clientArea;
      this.text = text;
      this.barColor = barColor;
      this.barWidth = barWidth;
      this.style = style;
    }

    void paint() {
      prepareGC();
      drawBorder();
      drawBar();
      drawText();
    }

    private void prepareGC() {
      if( gc.getAdvanced() ) {
        gc.setTextAntialias( SWT.ON );
      }
      gc.fillRectangle( clientArea );
    }

    private void drawBorder() {
      if( ( style & SWT.BORDER ) != 0 ) {
        int x = clientArea.x;
        int y = clientArea.y;
        int width = clientArea.width - 1;
        int height = clientArea.height - 1 - 1;
        gc.setAlpha( 255 );
        gc.setForeground( getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ) );
        gc.drawRoundRectangle( x, y, width, height, ARC_SIZE, ARC_SIZE );
      }
    }

    private void drawBar() {
      if( barColor != null ) {
        gc.setAlpha( 200 );
        gc.setBackground( barColor );
        gc.fillRoundRectangle( 1, 1, barWidth, clientArea.height - 2 - 1, ARC_SIZE, ARC_SIZE );
      }
    }

    private void drawText() {
      gc.setAlpha( 255 );
      Rectangle rect = insideBorderArea();
      Point textLocation = getTextLocation( rect );

      Color defaultForeground = getSystemColor( SWT.COLOR_WIDGET_FOREGROUND );
      drawText( textLocation, defaultForeground, null );
      Color barForeground = getBarForegroundColor();
      if( !equals( defaultForeground, barForeground ) ) {
        Rectangle clipping = new Rectangle( rect.x, rect.y, barWidth, rect.height );
        drawText( textLocation, barForeground, clipping );
      }
    }

    private void drawText( Point location, Color foreground, Rectangle clipping ) {
      Rectangle previousClipping = gc.getClipping();
      if( clipping != null ) {
        gc.setClipping( clipping );
      }
      gc.setForeground( foreground );
      gc.drawText( text, location.x, location.y, SWT.DRAW_TRANSPARENT );
      if( clipping != null ) {
        gc.setClipping( previousClipping );
      }
    }

    private Point getTextLocation( Rectangle clientArea ) {
      Point textSize = gc.textExtent( text );
      int x = 3;
      if( ( style & SWT.CENTER ) != 0 ) {
        x = ( clientArea.width - textSize.x ) / 2;
      }
      int y = ( clientArea.height - textSize.y ) / 2 + 1;
      return new Point( x, y );
    }

    private Color getBarForegroundColor() {
      Color result = getSystemColor( SWT.COLOR_WIDGET_FOREGROUND );
      if( barColor != null ) {
        int red = barColor.getRed();
        int green = barColor.getGreen();
        int blue = barColor.getBlue();
        double y = ( 299 * red + 587 * green + 114 * blue ) / 1000;
        result = y >= 128 ? getSystemColor( SWT.COLOR_BLACK ) : getSystemColor( SWT.COLOR_WHITE );
      }
      return result;
    }

    private Color getSystemColor( int id ) {
      return gc.getDevice().getSystemColor( id );
    }

    private Rectangle insideBorderArea() {
      return new Rectangle( clientArea.x + 1, clientArea.y + 1, clientArea.width - 2, clientArea.height - 2 );
    }

    private static boolean equals( Color color1, Color color2 ) {
      return color1.getRGB().equals( color2.getRGB() );
    }
  }

}
