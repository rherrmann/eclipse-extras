package com.codeaffine.extras.jdt.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class JDTExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.jdt";

  public static JDTExtrasPlugin getInstance() {
    return instance;
  }

  private static JDTExtrasPlugin instance;

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    instance = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    instance = null;
    super.stop(context);
  }
}
