package com.codeaffine.extras.jdt.internal.junitstatus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.eclipse.core.runtime.Extension;
import com.codeaffine.eclipse.core.runtime.Predicate;
import com.codeaffine.eclipse.core.runtime.RegistryAdapter;

public class JUnitStatusContributionItemPDETest {

  private static final String LOCATION = "toolbar:org.eclipse.ui.trim.status";
  private static final String TOOL_BAR_ID = "com.codeaffine.extras.jdt.internal.JUnitStatusToolBar";

  private Extension extension;

  @Before
  public void setUp() {
    extension = new RegistryAdapter()
      .readExtension( "org.eclipse.ui.menus" )
      .thatMatches( new JUnitStatusBarPredicate() )
      .process();
  }

  @Test
  public void testExtension() {
    assertThat( extension ).isNotNull();
    Extension toolbarElement = getToolBarElement();
    Extension controlElement = getControlElement();
    VisibleWhenElement visibleWhen = getControlVisibleWhenElement();
    assertThat( extension.getChildren( "toolbar" ) ).hasSize( 1 );
    assertThat( toolbarElement.getChildren( "control" ) ).hasSize( 1 );
    assertThat( toolbarElement.getAttribute( "label" ) ).isEqualTo( "JUnit" );
    assertThat( controlElement.getAttribute( "id" ) ).isNotNull();
    assertThat( controlElement.createExecutableExtension( JUnitStatusContributionItem.class ) ).isNotNull();
    assertThat( visibleWhen.isCheckEnabled() ).isFalse();
    assertThat( visibleWhen.referencedExpression() ).isEqualTo( "com.codeaffine.extras.jdt.internal.JUnitStatusVisibleExpression" );
  }

  @Test
  public void testShowJUnitViewCommand() {
    assertThat( extension ).isNotNull();
    Extension command = getCommandElement();
    Extension parameter = getCommandParameterElement();
    VisibleWhenElement visibleWhen = getCommandVisibleWhenElement();
    assertThat( command.getAttribute( "id" ) ).isEqualTo( "com.codeaffine.extras.jdt.internal.OpenJUnitViewToolItem" );
    assertThat( command.getAttribute( "commandId" ) ).isEqualTo( "org.eclipse.ui.views.showView" );
    assertThat( command.getAttribute( "icon" ) ).isEqualTo( "icons/etool16/junit.gif" );
    assertThat( command.getAttribute( "style" ) ).isEqualTo( "push" );
    assertThat( parameter.getAttribute( "name" ) ).isEqualTo( "org.eclipse.ui.views.showView.viewId" );
    assertThat( parameter.getAttribute( "value" ) ).isEqualTo( "org.eclipse.jdt.junit.ResultView" );
    assertThat( visibleWhen.isCheckEnabled() ).isFalse();
    assertThat( visibleWhen.referencedExpression() ).isEqualTo( "com.codeaffine.extras.jdt.internal.JUnitStatusVisibleExpression" );
  }

  private Extension getCommandElement() {
    return getFirst( getToolBarElement().getChildren( "command" ) );
  }

  private Extension getCommandParameterElement() {
    return getFirst( getCommandElement().getChildren( "parameter" ) );
  }

  private VisibleWhenElement getCommandVisibleWhenElement() {
    return new VisibleWhenElement( getVisibleWhenElement( getCommandElement() ) );
  }

  private Extension getControlElement() {
    return getFirst( getToolBarElement().getChildren( "control" ) );
  }

  private Extension getToolBarElement() {
    return getFirst( extension.getChildren( "toolbar" ) );
  }

  private VisibleWhenElement getControlVisibleWhenElement() {
    return new VisibleWhenElement( getVisibleWhenElement( getControlElement() ) );
  }

  private static Extension getVisibleWhenElement( Extension extension ) {
    return getFirst( extension.getChildren( "visibleWhen" ) );
  }

  private static <T> T getFirst( Collection<T> collection ) {
    return collection.stream().findFirst().orElse( null );
  }

  private static class VisibleWhenElement {

    private final Extension extension2;

    VisibleWhenElement( Extension extension ) {
      extension2 = extension;
    }

    boolean isCheckEnabled() {
      return "true".equals( extension2.getAttribute( "checkEnabled" ) );
    }

    String referencedExpression() {
      return getFirst( extension2.getChildren( "reference" ) ).getAttribute( "definitionId" );
    }
  }

  private static class JUnitStatusBarPredicate implements Predicate {
    @Override
    public boolean apply( Extension input ) {
      return isToolBarContribution( input ) && containsJUnitSttausBar( input );
    }

    private static boolean isToolBarContribution( Extension extension ) {
      return LOCATION.equals( extension.getAttribute( "locationURI" ) );
    }

    private static boolean containsJUnitSttausBar( Extension extension ) {
      Collection<Extension> toolBars = extension.getChildren( "toolbar" );
      return toolBars.stream().anyMatch( element -> TOOL_BAR_ID.equals( getId( element ) ) );
    }

    private static String getId( Extension element ) {
      return element.getAttribute( "id" );
    }
  }

}
