package com.codeaffine.extras.archive.internal.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveEntrySorter extends ViewerComparator {

  @Override
  public int compare(Viewer viewer, Object object1, Object object2) {
    int result;
    if (object1 instanceof ArchiveEntry && object2 instanceof ArchiveEntry) {
      result = compare((ArchiveEntry) object1, (ArchiveEntry) object2);
    } else {
      result = super.compare(viewer, object1, object2);
    }
    return result;
  }

  private static int compare(ArchiveEntry archiveEntry1, ArchiveEntry archiveEntry2) {
    int result;
    if (archiveEntry1 instanceof DirectoryEntry && archiveEntry2 instanceof FileEntry) {
      result = -1;
    } else if (archiveEntry1 instanceof FileEntry && archiveEntry2 instanceof DirectoryEntry) {
      result = 1;
    } else {
      result = archiveEntry1.getName().compareTo(archiveEntry2.getName());
    }
    return result;
  }
}
