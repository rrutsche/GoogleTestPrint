package de.rutsche.mycloudprinter.filechooser;

import java.io.File;

import de.beuthhochschule.swp.cloudprinter.filechooser.R;

/**
 * Model CLass for {@code FileChooser} representing a root directory
 * 
 * @author Frank Schmidt
 * 
 */
public class RootNode implements IFileNode {
    private final File   file;
    private final String name = "...";

    /**
     * Creates a new {@code RootNode}
     * 
     * @param file
     *            The {@code java.io.File} representation of the root directory
     */
    public RootNode(File file) {
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.beuthhochschule.swp.cloudprinter.view.file_chooser.IFileNode#getFile()
     */
    @Override
    public File getFile() {
        return file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.beuthhochschule.swp.cloudprinter.view.file_chooser.IFileNode#getName()
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
    public int compareTo(IFileNode arg0) {
        throw new UnsupportedOperationException();
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
