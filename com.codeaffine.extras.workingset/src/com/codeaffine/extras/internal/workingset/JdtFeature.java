package com.codeaffine.extras.internal.workingset;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;


public class JdtFeature {

  private Boolean installed;

  public boolean isInstalled() {
    if( installed == null ) {
      findJdtFeature();
    }
    return installed.booleanValue();
  }

  private void findJdtFeature() {
    installed = FALSE;
    Bundle[] bundles = getBundles();
    for( int i = 0; installed == FALSE && i < bundles.length; i++ ) {
      if( "org.eclipse.jdt.ui".equals( bundles[ i ].getSymbolicName() ) ) {
        installed = TRUE;
      }
    }
  }

  private Bundle[] getBundles() {
    BundleContext bundleContext = FrameworkUtil.getBundle( getClass() ).getBundleContext();
    return bundleContext.getBundles();
  }
}
