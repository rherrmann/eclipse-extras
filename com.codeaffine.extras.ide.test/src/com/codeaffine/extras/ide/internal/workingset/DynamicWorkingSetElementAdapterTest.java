package com.codeaffine.extras.ide.internal.workingset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.junit.Before;
import org.junit.Test;

public class DynamicWorkingSetElementAdapterTest {

  private DynamicWorkingSetElementAdapter elementAdapter;

  @Before
  public void setUp() {
    elementAdapter = new DynamicWorkingSetElementAdapter();
  }

  @Test
  public void testAdaptArbitraryElement() {
    IAdaptable[] adaptElements = adaptElements( mock( IAdaptable.class ) );

    assertThat( adaptElements ).isEmpty();
  }

  @Test
  public void testAdaptProject() {
    IProject project = mock( IProject.class );

    IAdaptable[] adaptElements = adaptElements( project );

    assertThat( adaptElements ).containsOnly( project );
  }

  @Test
  public void testAdaptProjectAdapter() {
    IProject project = mock( IProject.class );
    IAdaptable element = mock( IAdaptable.class );
    when( element.getAdapter( IProject.class ) ).thenReturn( project );

    IAdaptable[] adaptElements = adaptElements( element );

    assertThat( adaptElements ).containsOnly( project );
  }

  private IAdaptable[] adaptElements( IAdaptable... elements ) {
    return elementAdapter.adaptElements( null, elements );
  }
}
