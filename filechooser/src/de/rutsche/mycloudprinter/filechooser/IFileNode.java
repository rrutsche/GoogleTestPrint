package de.rutsche.mycloudprinter.filechooser;

import java.io.File;

/**
 * Interface for input objects of the {@code FileChooser}
 * 
 * @author Frank Schmidt
 * 
 */
public interface IFileNode extends Comparable<IFileNode> {

    /**
     * @return The {@code java.io.File} representation of the node
     */
    File getFile();

    /**
     * @return The name of the node
     */
    String getName();

    /**
     * @return The image id of the node
     */
    int getImageId();

}