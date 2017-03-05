package com.codeaffine.extras.ide.internal.resourcefilter;

import static java.util.Collections.synchronizedSet;

import java.util.HashSet;
import java.util.Set;

class RecursionGuard {

  private final Set<Object> projects;

  RecursionGuard() {
    projects = synchronizedSet( new HashSet<>() );
  }

  boolean isInUse( Object resource ) {
    return projects.contains( resource );
  }

  void enter( Object resource ) {
    projects.add( resource );
  }

  void leave( Object resource ) {
    projects.remove( resource );
  }
}
