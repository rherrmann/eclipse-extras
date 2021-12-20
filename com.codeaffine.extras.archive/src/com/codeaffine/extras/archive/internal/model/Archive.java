package com.codeaffine.extras.archive.internal.model;

import org.eclipse.core.runtime.IPath;

public class Archive {
  private final String name;
  private final IPath location;
  private final DirectoryEntry rootEntry;

  public Archive(String name, IPath location) {
    this.name = name;
    this.location = location;
    this.rootEntry = new DirectoryEntry(this, "/");
  }

  public String getName() {
    return name;
  }

  public IPath getLocation() {
    return location;
  }

  public DirectoryEntry getRootEntry() {
    return rootEntry;
  }
}
