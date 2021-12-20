package com.codeaffine.extras.archive.internal.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.util.Images;

public class ArchiveLabelProvider extends LabelProvider {
  private Image folderImage;
  private Image fileImage;

  @Override
  public Image getImage(Object element) {
    Image result = null;
    if (element instanceof ArchiveEntry) {
      result = getArchiveEntryImage((ArchiveEntry) element);
    }
    return result;
  }

  @Override
  public String getText(Object element) {
    String result = null;
    if (element instanceof ArchiveEntry) {
      result = ((ArchiveEntry) element).getName();
    }
    return result;
  }

  @Override
  public void dispose() {
    if (folderImage != null) {
      folderImage.dispose();
      folderImage = null;
    }
    if (fileImage != null) {
      fileImage.dispose();
      fileImage = null;
    }
  };

  private Image getArchiveEntryImage(ArchiveEntry archiveEntry) {
    Image result;
    if (archiveEntry instanceof DirectoryEntry) {
      result = getFolderEntryImage();
    } else {
      result = getFileEntryImage();
    }
    return result;
  }

  private Image getFolderEntryImage() {
    if (folderImage == null) {
      folderImage = Images.ARCHIVE_FOLDER.createImage(true, Display.getCurrent());
    }
    return folderImage;
  }

  private Image getFileEntryImage() {
    if (fileImage == null) {
      fileImage = Images.ARCHIVE_FILE.createImage(true, Display.getCurrent());
    }
    return fileImage;
  }
}
