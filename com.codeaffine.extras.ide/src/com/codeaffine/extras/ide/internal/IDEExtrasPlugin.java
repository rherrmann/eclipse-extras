package com.codeaffine.extras.ide.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class IDEExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.ide";

  public static IDEExtrasPlugin getInstance() {
    return instance;
  }

  private static IDEExtrasPlugin instance;

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

  @Override
  protected void initializeImageRegistry( ImageRegistry registry ) {
    Images.registerImages( registry );
  }

}
