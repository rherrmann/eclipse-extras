package com.codeaffine.extras.launch.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.codeaffine.extras.launch.internal.cleanup.LaunchConfigCleaner;


public class LaunchExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.launch";

  public static LaunchExtrasPlugin getInstance() {
    return instance;
  }

  private static LaunchExtrasPlugin instance;

  private final LaunchConfigCleaner launchConfigCleaner;

  public LaunchExtrasPlugin() {
    launchConfigCleaner = new LaunchConfigCleaner();
  }

  @Override
  public void start( BundleContext context ) throws Exception {
    super.start( context );
    instance = this;
    launchConfigCleaner.install();
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    launchConfigCleaner.uninstall();
    super.stop( context );
    instance = null;
  }

}
