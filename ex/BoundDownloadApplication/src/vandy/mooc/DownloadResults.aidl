package vandy.mooc;

/**
 * @class
 *
 * @brief An AIDL Interface used for receiving results from a call to
 *        DownloadRequest.downloadImage()
 *
 *	  This file generates a java interface in /gen
 */
interface DownloadResults {
    /**
     * Send the location of a downloaded file on the file system back
     * to the caller.
     */
    oneway void sendPath(in String filePath); 
}
