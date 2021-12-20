package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.junit.Before;
import org.junit.Test;

public class OpenActionProviderTest {

  private OpenActionProvider actionProvider;

  @Test
  public void testSetContextWithNullArgument() {
    actionProvider.setContext(null);

    ActionContext context = actionProvider.getContext();

    assertNull(context);
  }

  @Test
  public void testSetContext() throws Exception {
    ActionContext context = mock(ActionContext.class);
    actionProvider.setContext(context);

    ActionContext returnedContext = actionProvider.getContext();

    assertEquals(context, returnedContext);
  }

  @Test
  public void testDisposeInitializedContext() {
    ICommonViewerWorkbenchSite workbenchSite = mock(ICommonViewerWorkbenchSite.class);
    ICommonActionExtensionSite viewerSite = mock(ICommonActionExtensionSite.class);
    when(viewerSite.getViewSite()).thenReturn(workbenchSite);
    actionProvider.init(viewerSite);

    actionProvider.dispose();

    assertNull(actionProvider.getContext());
  }

  @Before
  public void setUp() {
    actionProvider = new OpenActionProvider();
  }
}
