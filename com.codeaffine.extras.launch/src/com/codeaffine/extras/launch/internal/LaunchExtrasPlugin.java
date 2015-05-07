package com.codeaffine.extras.launch.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class LaunchExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.launch";

  public static LaunchExtrasPlugin getInstance() {
    return instance;
  }

  private static LaunchExtrasPlugin instance;

  @Override
  public void start( BundleContext context ) throws Exception {
    super.start( context );
    instance = this;
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    super.stop( context );
    instance = null;
  }

}
