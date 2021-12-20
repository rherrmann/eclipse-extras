package com.codeaffine.extras.archive.internal.model;

import org.eclipse.core.runtime.IPath;

interface ArchiveLocationProvider {
  String getArchiveName();

  IPath getLocation();
}
