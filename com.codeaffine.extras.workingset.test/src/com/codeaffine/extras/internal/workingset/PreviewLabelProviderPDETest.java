package com.codeaffine.extras.internal.workingset;

import static com.codeaffine.extras.internal.workingset.RegexPatterns.ANYTHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.eclipse.swt.test.util.DisplayHelper;
import com.codeaffine.extras.internal.workingset.PreviewLabelProvider;

public class PreviewLabelProviderPDETest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private PreviewLabelProvider labelProvider;

  @Before
  public void setUp() {
    labelProvider = new PreviewLabelProvider( displayHelper.getDisplay() );
  }

  @Test
  public void testGetText() {
    IProject project = createProject( "name" );

    String text = labelProvider.getText( project );

    assertThat( text ).isEqualTo( project.getName() );
  }

  @Test(expected=ClassCastException.class)
  public void testGetTextForNonProject() {
    labelProvider.getText( new Object() );
  }

  @Test
  public void testGetImage() {
    IProject openedProject = createProject( "opened" );
    when( openedProject.isOpen() ).thenReturn( true );
    IProject closedProject = createProject( "closed" );

    Image openedImage = labelProvider.getImage( openedProject );
    Image closedImage = labelProvider.getImage( closedProject );

    assertThat( openedImage ).isNotNull();
    assertThat( closedImage ).isNotNull();
    assertThat( openedImage ).isNotEqualTo( closedImage );
  }

  @Test
  public void testGetImageTwice() {
    IProject project = createProject( "name" );

    Image image1 = labelProvider.getImage( project );
    Image image2 = labelProvider.getImage( project );

    assertThat( image1 ).isSameAs( image2 );
  }

  @Test(expected=ClassCastException.class)
  public void testGetImageForNonProject() {
    labelProvider.getImage( new Object() );
  }

  @Test
  public void testGetForeground() {
    IProject project = createProject( "bar" );

    labelProvider.setPattern( ANYTHING );
    Color matchingForeground = labelProvider.getForeground( project );
    labelProvider.setPattern( "foo*" );
    Color nonMatchingForeground = labelProvider.getForeground( project );

    assertThat( matchingForeground ).isNull();
    assertThat( nonMatchingForeground ).isNotNull();
  }

  @Test
  public void testGetForegroundTwice() {
    IProject project = createProject( "name" );

    Color foreground1 = labelProvider.getForeground( project );
    Color foreground2 = labelProvider.getForeground( project );

    assertThat( foreground1 ).isSameAs( foreground2 );
  }

  @Test(expected=ClassCastException.class)
  public void testGetForegroundForNonProject() {
    labelProvider.getForeground( new Object() );
  }

  private static IProject createProject( String name ) {
    IProject result = mock( IProject.class );
    when( result.getName() ).thenReturn( name );
    return result;
  }

}
