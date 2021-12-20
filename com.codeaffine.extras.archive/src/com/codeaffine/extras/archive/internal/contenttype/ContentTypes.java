package com.codeaffine.extras.archive.internal.contenttype;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public final class ContentTypes {

  static class NullContentDescription implements IContentDescription {
    @Override
    public boolean isRequested(QualifiedName key) {
      return false;
    }

    @Override
    public String getCharset() {
      return null;
    }

    @Override
    public IContentType getContentType() {
      return null;
    }

    @Override
    public Object getProperty(QualifiedName key) {
      return null;
    }

    @Override
    public void setProperty(QualifiedName key, Object value) {}
  }

  private static final String ZIP_CONTENT_TYPE_ID = "com.codeaffine.extras.archive.contenttype.ZipContentType";

  public static boolean isZipContentType(IContentType contentType) {
    return contentType != null && ZIP_CONTENT_TYPE_ID.equals(contentType.getId());
  }

  private ContentTypes() {
    // prevent instantiation
  }

  public static IContentDescription getContentDescription(FileEntry fileEntry) throws IOException {
    try (InputStream inputStream = fileEntry.open()) {
      return getContentDescription(fileEntry.getName(), inputStream);
    }
  }

  public static IContentDescription getContentDescription(IFile file) {
    IContentDescription result = null;
    try {
      result = file.getContentDescription();
    } catch (CoreException ignore) {
    }
    if (result == null) {
      result = new NullContentDescription();
    }
    return result;
  }

  private static IContentDescription getContentDescription(String name, InputStream content) throws IOException {
    IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
    IContentDescription result = contentTypeManager.getDescriptionFor(content, name, IContentDescription.ALL);
    if (result == null) {
      result = new NullContentDescription();
    }
    return result;
  }

  public static IContentType getContentType(String fileName) {
    IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
    return contentTypeManager.findContentTypeFor(fileName);
  }
}
