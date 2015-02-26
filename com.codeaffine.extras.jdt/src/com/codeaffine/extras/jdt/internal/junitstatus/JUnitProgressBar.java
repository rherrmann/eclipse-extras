package com.codeaffine.extras.jdt.internal.junitstatus;

import static com.google.common.base.Objects.equal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.extras.jdt.internal.junitstatus.TextAnimation.TextAnimationPainter;

public class JUnitProgressBar extends Canvas implements TextAnimationPainter {
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
    addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        redraw();
      }
    } );
    addPaintListener( new PaintListener() {
      @Override
      public void paintControl( PaintEvent event ) {
        paint( event.gc );
      }
    } );
  }

  private void paint( GC gc ) {
    gc.fillRectangle( getClientArea() );
    drawBevelRect( gc );
    drawBar( gc );
    drawText( gc );
  }

  private void drawBevelRect( GC gc ) {
    Rectangle clientArea = getClientArea();
    int x = clientArea.x;
    int y = clientArea.y;
    int width = clientArea.width - 1;
    int height = clientArea.height - 1;
    gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ) );
    gc.drawLine( x, y, x + width - 1, y );
    gc.drawLine( x, y, x, y + height - 1 );
    gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW ) );
    gc.drawLine( x + width, y, x + width, y + height );
    gc.drawLine( x, y + height, x + width, y + height );
  }

  private void drawBar( GC gc ) {
    gc.setAlpha( 200 );
    Rectangle rect = getClientArea();
    if( barColor != null ) {
      gc.setBackground( barColor );
    }
    int barWidth = Math.min( rect.width - 2, getBarWidth() );
    gc.fillRectangle( 1, 1, barWidth, rect.height - 2 );
  }

  private void drawText( GC gc ) {
    gc.setAlpha( 255 );
    Rectangle rect = getClientArea();
    Rectangle clientArea = new Rectangle( 1, 1, rect.width - 2, rect.height - 2 );
    Point textSize = gc.textExtent( text );
    int x = 3;
    if( textAlignment == SWT.CENTER ) {
      x = ( clientArea.width - textSize.x ) / 2;
    }
    int y = ( clientArea.height - textSize.y ) / 2 + 1;
    gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_FOREGROUND ) );
    gc.drawText( textAnimation.getAnimatedText(), x, y, SWT.DRAW_TRANSPARENT );
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

  private boolean valuesChanged( String text,
                                 int textAlignment,
                                 Color barColor,
                                 int selection,
                                 int maximum )
  {
    return !equal( this.text, text )
        || this.textAlignment != textAlignment
        || !equal( this.barColor, barColor )
        || this.selection != selection
        || this.maximum != maximum;
  }

}
