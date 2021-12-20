package com.codeaffine.extras.archive.internal.model;

public abstract class ArchiveEntry {

  protected final ArchiveEntry parent;

  public ArchiveEntry(ArchiveEntry parent) {
    this.parent = parent;
    if (parent != null) {
      parent.addChild(this);
    }
  }

  public abstract String getName();

  public abstract Archive getArchive();

  public ArchiveEntry getParent() {
    return parent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((getArchive() == null) ? 0 : getArchive().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object object) {
    boolean result = false;
    if (object instanceof ArchiveEntry) {
      ArchiveEntry archiveEntry = (ArchiveEntry) object;
      boolean fileEquals = archiveEntry.getArchive().equals(getArchive());
      boolean nameEquals = archiveEntry.getName().equals(getName());
      result = fileEquals && nameEquals;
    }
    return result;
  }

  protected abstract void addChild(ArchiveEntry archiveEntry);
}
