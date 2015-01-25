package com.codeaffine.extras.ide.internal.launch;

import static com.google.common.collect.Maps.newHashMap;

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class LaunchConfigImageProvider {
  private static final String LAUNCH_CONFIGURATION_TYPE_IMAGES_EP
    = "org.eclipse.debug.ui.launchConfigurationTypeImages";

  private final ILaunchManager launchManager;
  private final Map<ILaunchConfigurationType,ImageDescriptor> launchConfigurationTypeImages;
  private final ResourceManager resourceManager;
  private boolean initialized;

  public LaunchConfigImageProvider( Display display ) {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    resourceManager = new LocalResourceManager( JFaceResources.getResources( display ) );
    launchConfigurationTypeImages = newHashMap();
  }

  public Image getImage( ILaunchConfiguration configuration ) {
    ensureInitialized();
    Image result = null;
    ILaunchConfigurationType type = getType( configuration );
    if( type != null ) {
      ImageDescriptor imageDescriptor = launchConfigurationTypeImages.get( type );
      if( imageDescriptor != null ) {
        result = resourceManager.createImage( imageDescriptor );
      }
    }
    return result;
  }

  public void dispose() {
    resourceManager.dispose();
  }

  private void ensureInitialized() {
    if( !initialized ) {
      initialized = true;
      initialize();
    }
  }

  private void initialize() {
    IConfigurationElement[] elements = getLaunchConfigTypeImages();
    for( IConfigurationElement element : elements ) {
      ILaunchConfigurationType type = getLaunchConfigType( element );
      ImageDescriptor imageDescriptor = createImageDescriptor( element );
      launchConfigurationTypeImages.put( type, imageDescriptor );
    }
  }

  private ILaunchConfigurationType getLaunchConfigType( IConfigurationElement element ) {
    return launchManager.getLaunchConfigurationType( element.getAttribute( "configTypeID" ) );
  }

  private static IConfigurationElement[] getLaunchConfigTypeImages() {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    return extensionRegistry.getConfigurationElementsFor( LAUNCH_CONFIGURATION_TYPE_IMAGES_EP );
  }

  private static ImageDescriptor createImageDescriptor( IConfigurationElement element ) {
    String icon = element.getAttribute( "icon" );
    String bundleName = element.getContributor().getName();
    URL url = FileLocator.find( Platform.getBundle( bundleName ), new Path( icon ), null );
    return ImageDescriptor.createFromURL( url );
  }

  private static ILaunchConfigurationType getType( ILaunchConfiguration configuration ) {
    ILaunchConfigurationType type = null;
    try {
      type = configuration.getType();
    } catch( CoreException ignore ) {
    }
    return type;
  }
}
