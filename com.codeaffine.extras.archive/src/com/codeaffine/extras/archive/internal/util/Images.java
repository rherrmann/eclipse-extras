package com.codeaffine.extras.archive.internal.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public final class Images {

  private static final String ICONS_PATH = "$nl$/icons/"; //$NON-NLS-1$
  private static final String PATH_OBJ = ICONS_PATH + "obj16/"; //$NON-NLS-1$
  private static final String PATH_LOCAL = ICONS_PATH + "elcl16/"; //$NON-NLS-1$

  public static final ImageDescriptor ARCHIVE_FOLDER = create(PATH_OBJ + "zip_folder.png"); //$NON-NLS-1$
  public static final ImageDescriptor ARCHIVE_FILE = create(PATH_OBJ + "zip_file.png"); //$NON-NLS-1$
  public static final ImageDescriptor EXTRACT_ACTION = create(PATH_LOCAL + "extract_action.png"); //$NON-NLS-1$

  private static ImageDescriptor create(String name) {
    Bundle bundle = FrameworkUtil.getBundle(Images.class);
    URL imageUrl = FileLocator.find(bundle, new Path(name), null);
    return ImageDescriptor.createFromURL(imageUrl);
  }

  private Images() {
    // prevent instantiation
  }
}
