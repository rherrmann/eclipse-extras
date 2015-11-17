package com.codeaffine.extras.workingset.internal;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.junit.Test;

import com.codeaffine.extras.workingset.internal.Images;

public class ImagesPDETest {

  @Test
  public void testRegisteredImages() throws IllegalAccessException {
    for( String constantValue : getConstantValues() ) {
      checkImageDescriptor( constantValue );
    }
  }

  private static void checkImageDescriptor( String constantValue ) {
    ImageDescriptor descriptor = Images.getImageDescriptor( constantValue );
    assertNotNull( "No image descriptor registered for: " + constantValue, descriptor );
    Image image = descriptor.createImage( false );
    assertNotNull( "Image descriptor does not return image: " + constantValue, image );
    image.dispose();
  }

  private static String[] getConstantValues() throws IllegalAccessException {
    List<String> constantValues = new LinkedList<>();
    Field[] declaredFields = Images.class.getDeclaredFields();
    for( Field constant : declaredFields ) {
      constantValues.add( ( String )constant.get( null ) );
    }
    return constantValues.toArray( new String[ constantValues.size() ] );
  }
}