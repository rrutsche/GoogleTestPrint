package de.rutsche.mycloudprinter.filechooser;

import java.io.File;

import de.beuthhochschule.swp.cloudprinter.filechooser.R;

/**
 * Model CLass for {@code FileChooser} representing a directory
 * 
 * @author Frank Schmidt
 * 
 */
public class DirectoryNode implements IFileNode {
    private static final String ILLEGAL_ARGUMENT_FOR_FILE = "Given File is not a directory";
    private final File          file;
    private final String        name;

    /**
     * Creates a new {@code DirectoryNode}
     * 
     * @param file
     *            The {@code java.io.File} representation of the directory
     */
    public DirectoryNode(File file) {
        super();
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_FOR_FILE);
        }
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
        if (!(another instanceof DirectoryNode)) {
            return -1;
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
        return R.drawable.folder_temp;
    }
}
