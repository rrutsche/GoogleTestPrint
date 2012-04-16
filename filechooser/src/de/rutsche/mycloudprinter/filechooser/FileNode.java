package de.rutsche.mycloudprinter.filechooser;

import java.io.File;

import de.beuthhochschule.swp.cloudprinter.filechooser.R;

/**
 * Model CLass for {@code FileChooser} representing a file
 * 
 * @author Frank Schmidt
 * 
 */
public class FileNode implements IFileNode {
    private final File   file;
    private final String name;

    /**
     * Creates a new {@code FileNode}
     * 
     * @param file
     *            The {@code java.io.File} representation of the file
     */
    public FileNode(File file) {
        super();
        this.file = file;
        this.name = file.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.beuthhochschule.swp.cloudprinter.view.file_chooser.IFileChooserEntry
     * #getFile()
     */
    @Override
    public File getFile() {
        return file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.beuthhochschule.swp.cloudprinter.view.file_chooser.IFileChooserEntry
     * #getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IFileNode another) {
        if (!(another instanceof FileNode)) {
            return 1;
        } else {
            return this.getName().compareTo(another.getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.beuthhochschule.swp.cloudprinter.view.file_chooser.IFileNode#getImageId
     * ()
     */
    @Override
    public int getImageId() {
        return R.drawable.file_temp;
    }
}
