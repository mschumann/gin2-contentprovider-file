package net.sf.iqser.plugin.filesystem;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * File Scanner.
 * 
 * @author Magnus, Christian
 */
public class FileScanner
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger( FileScanner.class );

    /**
     * Folder Filter.
     */
    private FileFilter pathFilter;

    /**
     * Folders.
     */
    private List folders;

    /**
     * Constructor.
     * 
     * @param roots
     *            A Collection of Pathnames (String)
     * @param pathFilter
     *            A AcceptedPathFilter
     */
    public FileScanner( Collection roots, FileFilter pathFilter )
    {
        folders = new ArrayList();

        this.pathFilter = pathFilter;

        if ( roots != null )
        {
            for ( Iterator iter = roots.iterator(); iter.hasNext(); )
            {
                File root = new File( (String) iter.next() );
                folders.addAll( scanFolder( root ) );
            }
        }
    }

    /**
     * Constructor.
     */
    public FileScanner()
    {
        folders = new ArrayList();
    }

    /**
     * Scan for accepted Files in a folder.
     * 
     * @param filter
     *            FileFilter.
     * 
     * @return A List of Filenames (String). Guaranteed not null.
     */
    public Collection scanFiles( FileFilter filter )
    {
        ArrayList list = new ArrayList();

        for ( Iterator iter = folders.iterator(); iter.hasNext(); )
        {
            String s = (String) iter.next();
            File folder = new File( s );

            if ( folder.exists() )
            {
                File[] subs = folder.listFiles( filter );

                if ( subs != null )
                {
                    for ( int i = 0; i < subs.length; i++ )
                    {
                        list.add( subs[i].getAbsolutePath() );
                    }
                }
            }
        }

        return list;
    }

    /**
     * Scan a Folder for accepted Sub-Folders.
     * 
     * @param parent
     *            Folder to scan.
     * 
     * @return A Collection of Foldernames (String).
     */
    private Collection scanFolder( File parent )
    {
        logger.debug( "scanFolder(File parent=" + parent + ") - start" );

        ArrayList list = new ArrayList();
        list.add( parent.getAbsolutePath() );

        if ( ( parent != null ) && parent.isDirectory() )
        {
            File[] subs = parent.listFiles( pathFilter );

            if ( subs != null )
            {
                for ( int i = 0; i < subs.length; i++ )
                {
                    list.addAll( scanFolder( subs[i] ) );
                }

                // list.add( parent.getAbsolutePath() );
            }
        }

        logger.debug( "scanFolder(File parent=" + parent + ") - end - return value=" + list );
        return list;
    }

    /**
     * Returns the folders.
     * 
     * @return A List of the folders
     */
    public List getFolders()
    {
        logger.debug( "getFolders() - start" );

        logger.debug( "getFolders() - end - return value=" + folders );
        return folders;
    }

    /**
     * Sets the folders.
     * 
     * @param folders
     *            The folders to set.
     */
    public void setFolders( List folders )
    {
        logger.debug( "setFolders(List folders=" + folders + ") - start" );

        this.folders = folders;

        logger.debug( "setFolders(List folders=" + folders + ") - end" );
    }

    /**
     * Returns the pathFilter.
     * 
     * @return AcceptedPathFilter
     */
    public FileFilter getPathFilter()
    {
        logger.debug( "getPathFilter() - start" );

        logger.debug( "getPathFilter() - end - return value=" + pathFilter );
        return pathFilter;
    }

    /**
     * Sets the pathFilter.
     * 
     * @param pathFilter
     *            The pathFilter to set.
     */
    public void setPathFilter( FileFilter pathFilter )
    {
        logger.debug( "setPathFilter(AcceptedPathFilter pathFilter=" + pathFilter + ") - start" );

        this.pathFilter = pathFilter;

        logger.debug( "setPathFilter(AcceptedPathFilter pathFilter=" + pathFilter + ") - end" );
    }
}
