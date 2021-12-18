package com.codeaffine.extras.test.util;

import org.eclipse.ui.services.IServiceLocator;

public class ServiceHelper {

  public static <T> T getService(IServiceLocator serviceLocator, Class<T> api) {
    return api.cast(serviceLocator.getService(api));
  }

  private ServiceHelper() {}
}
