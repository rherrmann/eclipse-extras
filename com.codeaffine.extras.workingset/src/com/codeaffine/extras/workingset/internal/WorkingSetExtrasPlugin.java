package com.codeaffine.extras.workingset.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class WorkingSetExtrasPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.codeaffine.extras.workingset";

  public static WorkingSetExtrasPlugin getInstance() {
    return instance;
  }

  private static WorkingSetExtrasPlugin instance;

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
