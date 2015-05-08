package com.codeaffine.extras.internal.workingset;

import static com.codeaffine.extras.internal.workingset.WorkingSetExtrasPlugin.PLUGIN_ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;


public class Images {

  public static final String WORKING_SET_WIZBAN = "icons/wizban/working-set.png";

  public static ImageDescriptor getImageDescriptor( String name ) {
    ImageDescriptor result = null;
    ImageRegistry imageRegistry = getImageRegistry();
    if( imageRegistry != null ) {
      result = imageRegistry.getDescriptor( name );
    }
    return result;
  }

  static void registerImages( ImageRegistry registry ) {
    Field[] declaredFields = Images.class.getDeclaredFields();
    for( Field field : declaredFields ) {
      if( isStringConstant( field ) ) {
        registerImage( registry, getStringValue( field ) );
      }
    }
  }

  private static void registerImage( ImageRegistry registry, String imageName ) {
    ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin( PLUGIN_ID, imageName );
    registry.put( imageName, imageDescriptor );
  }

  private static ImageRegistry getImageRegistry() {
    ImageRegistry result = null;
    WorkingSetExtrasPlugin pluginInstance = WorkingSetExtrasPlugin.getInstance();
    if( pluginInstance != null && pluginInstance.getBundle().getState() == Bundle.ACTIVE ) {
      result = pluginInstance.getImageRegistry();
    }
    return result;
  }

  private static String getStringValue( Field field ) {
    try {
      return ( String )field.get( null );
    } catch( IllegalAccessException iae ) {
      throw new RuntimeException( iae );
    }
  }

  private static boolean isStringConstant( Field field ) {
    int modifiers = field.getModifiers();
    Class<?> type = field.getType();
    return Modifier.isFinal( modifiers ) && Modifier.isStatic( modifiers ) && type == String.class;
  }

  private Images() { }

}
