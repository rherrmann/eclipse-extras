package com.codeaffine.extras.platform.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class PlatformExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.platform";

  public static PlatformExtrasPlugin getInstance() {
    return instance;
  }

  private static PlatformExtrasPlugin instance;

  @Override
  public void start( BundleContext context ) throws Exception {
    super.start( context );
    instance = this;
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    instance = null;
    super.stop( context );
  }
}
