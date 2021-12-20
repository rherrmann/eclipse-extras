package com.codeaffine.extras.archive.internal.contenttype;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class ZipContentDescriber implements IContentDescriber {

  private static final QualifiedName[] NO_OPTIONS = new QualifiedName[0];
  private static final byte[] ZIP_SIGNATURE = {0x50, 0x4b, 0x03, 0x04,};

  @Override
  public int describe(InputStream contents, IContentDescription description) throws IOException {
    byte[] signature = readSignature(contents);
    int result;
    if (isValidZipSignature(signature)) {
      result = IContentDescriber.VALID;
    } else {
      result = IContentDescriber.INVALID;
    }
    return result;
  }

  @Override
  public QualifiedName[] getSupportedOptions() {
    return NO_OPTIONS;
  }

  private static byte[] readSignature(InputStream contents) throws IOException {
    byte[] result = new byte[ZIP_SIGNATURE.length];
    Arrays.fill(result, (byte) 0);
    contents.read(result);
    return result;
  }

  private static boolean isValidZipSignature(byte[] signature) {
    boolean result = true;
    for (int i = 0; result && i < ZIP_SIGNATURE.length; i++) {
      if (signature[i] != ZIP_SIGNATURE[i]) {
        result = false;
      }
    }
    return result;
  }
}
