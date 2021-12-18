package com.codeaffine.extras.launch.internal;

import java.io.File;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.codeaffine.extras.launch.internal.cleanup.LaunchConfigCleaner;
import com.codeaffine.extras.launch.internal.cleanup.LaunchConfigCleanerState;


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
  public void start(BundleContext context) throws Exception {
    super.start(context);
    instance = this;
    installLaunchConfigCleaner();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    uninstallLaunchConfigCleaner();
    super.stop(context);
    instance = null;
  }

  @Override
  protected void initializeImageRegistry(ImageRegistry registry) {
    Images.registerImages(registry);
  }

  private void installLaunchConfigCleaner() {
    launchConfigCleaner.install();
    if (hasStateLocation()) {
      launchConfigCleaner.restoreState(getLaunchConfigCleanerStateFile());
    }
  }

  private void uninstallLaunchConfigCleaner() {
    if (hasStateLocation()) {
      launchConfigCleaner.saveState(getLaunchConfigCleanerStateFile());
    }
    launchConfigCleaner.uninstall();
  }

  private File getLaunchConfigCleanerStateFile() {
    return getStateLocation().append(LaunchConfigCleanerState.DEFAULT_FILE_NAME).toFile();
  }

  private boolean hasStateLocation() {
    try {
      return getLaunchConfigCleanerStateFile() != null;
    } catch (IllegalStateException noStateLocation) {
      return false;
    }
  }

}
