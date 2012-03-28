/*
 * @author Michael Cotterell <mepcotterell@gmail.com>
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A really simple caching class.
 * @author Michael Cotterell <mepcotterell@gmail.com>
 */
public class SimpleCache {
 
    /**
     * Singleton instance
     */
    private static SimpleCache instance = null;
    
    /**
     * @return the singleton SimpleCache instance
     */
    public static SimpleCache getInstance() {
        if (SimpleCache.instance == null) SimpleCache.instance = new SimpleCache();
        return SimpleCache.instance;
    } // getInstance
    
    /**
     * Constructs a SimpleCache object.
     */
    private SimpleCache() { 
        
        try {
            // create the temp directory
            this.tempDir = File.createTempFile("simpleCacheTemp", Long.toString(System.nanoTime()));
            boolean delete = this.tempDir.delete();
            boolean mkdir = this.tempDir.mkdir();
                 
        } catch (IOException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

    /**
     * The cache map
     */
    private Map<String, File> cacheMap = new HashMap<String, File>();
    
    /**
     * The temporary cache directory.
     */
    private File tempDir = null;
    
    /**
     * Gets the URI to a cached file. If the file isn't in the cache, then we go
     * ahead and cache it.
     * @param url the url of the file we want to get.
     * @return location of a cached file.
     */
    public File get(String url) {

        // if the file is already cached, then return its File
        if (this.cacheMap.containsKey(url)) {
//            Logger.getLogger(SimpleCache.class.getName()).log(Level.INFO, "Found {0} in the cache!", url);
            return this.cacheMap.get(url);
        }

        // create a URL for the remote file
        URL remote = null;
        try {
            remote = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, "Couldn't create a URL from the url string.", ex);
        }
        
        // create a local file to cache the file to
        File local = null;
        try {
            local = File.createTempFile("cached", Long.toString(System.nanoTime()), this.tempDir);
            local.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, "Couldn't create a local file to save to.", ex);
        }

        // create an NIO byte channel for data transfer
        ReadableByteChannel rbc = null;
        try {
            rbc = Channels.newChannel(remote.openStream());
        } catch (IOException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, "Couldn't create a channel to the remote file.", ex);
        }
        
        // create an output stream to facilitate writing to the local file.
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(local);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, "Couldn't create an ouput stream to the local file.", ex);
        }
        
        // save the remote file to the local file.
        try {
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        } catch (IOException ex) {
            Logger.getLogger(SimpleCache.class.getName()).log(Level.SEVERE, "Couldn't transfer the file.", ex);
        }
            
//        Logger.getLogger(SimpleCache.class.getName()).log(Level.INFO, "Retrieved and saved {0} to {1}", new Object[]{url, local.getAbsoluteFile()});

        // add the file to the cacheMap
        this.cacheMap.put(url, local);
//        Logger.getLogger(SimpleCache.class.getName()).log(Level.INFO, "Added {0} to the cache map.", url);

        // return the local File
        return local;
        
    } // get
   
    
}
