package com.codeaffine.extras.ide.internal.resourcefilter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RecursionGuardTest {

  private RecursionGuard recursionGuard;

  @Before
  public void setUp() {
    recursionGuard = new RecursionGuard();
  }

  @Test
  public void testIsInUseBeforeEnter() {
    Object resource = new Object();

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isFalse();
  }

  @Test
  public void testIsInUseAfterEnter() {
    Object resource = new Object();
    recursionGuard.enter( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isTrue();
  }

  @Test
  public void testIsInUseAfterLeave() {
    Object resource = new Object();
    recursionGuard.enter( resource );
    recursionGuard.leave( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isFalse();
  }

  @Test
  public void testLeaveWithoutEnter() {
    Object resource = new Object();
    recursionGuard.leave( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isFalse();
  }

  @Test
  public void testEnterTwice() {
    Object resource = new Object();
    recursionGuard.enter( resource );
    recursionGuard.enter( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isTrue();
  }

  @Test
  public void testLeaveTwice() {
    Object resource = new Object();
    recursionGuard.enter( resource );
    recursionGuard.leave( resource );
    recursionGuard.leave( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isFalse();
  }

  @Test
  public void testLeaveAfterDuplicateEnter() {
    Object resource = new Object();
    recursionGuard.enter( resource );
    recursionGuard.enter( resource );
    recursionGuard.leave( resource );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isFalse();
  }

  @Test
  public void testLeaveDifferentResource() {
    Object resource = new Object();
    recursionGuard.enter( resource );
    recursionGuard.leave( new Object() );

    boolean inUse = recursionGuard.isInUse( resource );

    assertThat( inUse ).isTrue();
  }

}
