package vandy.mooc.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * Super class that defines common keys and methods used by the
 * RequestMessage and ReplyMessage subclasses.
 */
class RequestReplyMessageBase {
   /**
     * String constant used to extract the pathname to a downloaded
     * image from a Bundle.
     */
    protected static final String IMAGE_PATHNAME = "IMAGE_PATHNAME";

    /**
     * String constant used to extract the request code.
     */
    protected static final String REQUEST_CODE = "REQUEST_CODE";

    /**
     * String constant used to extract the URL to an image from a
     * Bundle.
     */
    protected static final String IMAGE_URL = "IMAGE_URL";

    /**
     * String constant used to extract the directory pathname to use
     * to store a downloaded image.
     */
    protected static final String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";
    
    /**
     * Message used to hold the information.
     */
    protected Message mMessage;

    /**
     * Constructor initializes the mMessage field.
     */
    protected RequestReplyMessageBase(Message message) {
        mMessage = message;
    }

    /**
     * Accessor method that returns the underlying Message.
     */
    public Message getMessage() {
        return mMessage;
    }

    /**
     * Accessor method that returns the Bundle that's part of the
     * underlying Message.
     */
    public Bundle getData() {
        return mMessage.getData();
    }

    /**
     * Accessor method that returns the result code of the message, which
     * can be used to check if the download succeeded.
     */
    public int getResultCode() {
      return mMessage.arg1;
    }

    /**
     * Accessor method that returns Messenger of the Message.
     */
    public Messenger getMessenger() {
      return mMessage.replyTo;
    }
    
    /**
     * Accessor method that returns the request code of the message.
     */
    public int getRequestCode() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the request code.
        return data.getInt(REQUEST_CODE);
    }

    /**
     * Helper method that returns the URL to the image file.
     */
    public static Uri getImageURL(Bundle data) {
        // Extract the path to the image file from the Bundle, which
        // should be stored using the IMAGE_URL key.
        return Uri.parse(data.getString(IMAGE_URL));
    }

    /**
     * Helper method that returns the URL to the image file.
     */
    public Uri getImageURL() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the path to the image file from the Bundle, which
        // should be stored using the IMAGE_URL key.
        return Uri.parse(data.getString(IMAGE_URL));
    }

    /**
     * Helper method that returns the path to the image file if it is
     * download successfully.
     */
    public static String getImagePathname(Bundle data) {
        // Extract the path to the image file from the Bundle, which
        // should be stored using the IMAGE_PATHNAME key.
        return data.getString(IMAGE_PATHNAME);
    }

    /**
     * Helper method that returns the path to the image file if it is
     * download successfully.
     */
    public String getImagePathname() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the path to the image file from the Bundle, which
        // should be stored using the IMAGE_PATHNAME key.
        return data.getString(IMAGE_PATHNAME);
    }

    /**
     * Helper method that returns the URI to the directory pathname.
     */
    public String getDirectoryPathname() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the directory pathname the Bundle, which should be
        // stored using the DIRECTORY_PATHNAME key.
        return data.getString(DIRECTORY_PATHNAME);
    }
}
