package com.codeaffine.extras.imageviewer.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.layout.FillLayout;
import org.junit.Test;

public class FillLayoutFactoryTest {

  @Test
  public void testNewFillLayout() {
    int margin = 2;

    FillLayout fillLayout = FillLayoutFactory.newFillLayout(margin);

    assertThat(fillLayout.marginHeight).isEqualTo(margin);
    assertThat(fillLayout.marginWidth).isEqualTo(margin);
  }
}
