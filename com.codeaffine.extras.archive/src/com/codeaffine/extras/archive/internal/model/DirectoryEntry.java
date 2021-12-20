package com.codeaffine.extras.archive.internal.model;

import java.util.Collection;
import java.util.LinkedList;

public class DirectoryEntry extends ArchiveEntry {
  private final String name;
  private final Archive archive;
  private final Collection<ArchiveEntry> children;

  public DirectoryEntry(Archive archive, String name) {
    this(archive, null, name);
  }

  public DirectoryEntry(ArchiveEntry parent, String name) {
    this(null, parent, name);
  }

  private DirectoryEntry(Archive archive, ArchiveEntry parent, String name) {
    super(parent);
    this.archive = archive;
    this.name = name;
    this.children = new LinkedList<ArchiveEntry>();
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  public ArchiveEntry[] getChildren() {
    return children.toArray(new ArchiveEntry[children.size()]);
  }

  @Override
  protected void addChild(ArchiveEntry archiveEntry) {
    children.add(archiveEntry);
  }

  @Override
  public Archive getArchive() {
    return archive == null ? parent.getArchive() : archive;
  }
}
