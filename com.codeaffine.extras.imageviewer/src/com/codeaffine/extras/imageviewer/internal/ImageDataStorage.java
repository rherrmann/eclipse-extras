package com.codeaffine.extras.imageviewer.internal;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

class ImageDataStorage {

  private final ImageData[] imageDatas;

  ImageDataStorage(ImageData... imageDatas) {
    this.imageDatas = requireNonNull(imageDatas, "imageDatas");
  }

  void save(IFile file, IProgressMonitor monitor) throws CoreException {
    requireNonNull(file, "file");
    requireNonNull(monitor, "monitor");
    int imageFormat = determineImageFormat(file.getFullPath());
    byte[] bytes = getImageDataBytes(imageFormat);
    InputStream inputStream = new ByteArrayInputStream(bytes);
    if (file.exists()) {
      file.setContents(inputStream, IResource.NONE, monitor);
    } else {
      file.create(inputStream, IResource.NONE, monitor);
    }
  }

  private byte[] getImageDataBytes(int imageFormat) {
    ImageLoader imageLoader = new ImageLoader();
    imageLoader.data = imageDatas;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    imageLoader.save(outputStream, imageFormat);
    return outputStream.toByteArray();
  }

  private static int determineImageFormat(IPath path) {
    int result = SWT.IMAGE_PNG;
    String fileExtension = path.getFileExtension();
    if (fileExtension != null) {
      Map<String, Integer> imageFormatMap = new HashMap<>();
      imageFormatMap.put("png", Integer.valueOf(SWT.IMAGE_PNG));
      imageFormatMap.put("gif", Integer.valueOf(SWT.IMAGE_GIF));
      imageFormatMap.put("jpeg", Integer.valueOf(SWT.IMAGE_JPEG));
      imageFormatMap.put("jpg", Integer.valueOf(SWT.IMAGE_JPEG));
      imageFormatMap.put("ico", Integer.valueOf(SWT.IMAGE_ICO));
      imageFormatMap.put("bmp", Integer.valueOf(SWT.IMAGE_BMP));
      imageFormatMap.put("tiff", Integer.valueOf(SWT.IMAGE_TIFF));
      Integer format = imageFormatMap.get(fileExtension.toLowerCase());
      if (format != null) {
        result = format.intValue();
      }
    }
    return result;
  }

}
