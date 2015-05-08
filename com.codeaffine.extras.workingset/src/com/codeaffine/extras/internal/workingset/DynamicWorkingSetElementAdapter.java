package com.codeaffine.extras.internal.workingset;

import static com.google.common.collect.Iterables.toArray;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetElementAdapter;


public class DynamicWorkingSetElementAdapter implements IWorkingSetElementAdapter {

  @Override
  public IAdaptable[] adaptElements( IWorkingSet workingSet, IAdaptable[] elements ) {
    Collection<IAdaptable> adaptedElements = new ArrayList<IAdaptable>( elements.length + 1 );
    for( IAdaptable element : elements ) {
      IProject project = adaptElememt( element );
      if( project != null ) {
        adaptedElements.add( project );
      }
    }
    return toArray( adaptedElements, IAdaptable.class );
  }

  @Override
  public void dispose() {
  }

  private static IProject adaptElememt( IAdaptable element ) {
    Object adapter = element instanceof IProject ? element : element.getAdapter( IProject.class );
    return ( IProject )( adapter instanceof IProject ? adapter : null );
  }
}
