package vandy.mooc.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * Super class that defines common keys and methods used by the
 * RequestMessage and ReplyMessage subclasses.
 */
public class RequestReplyMessageBase {
   /**
     * String constant used to extract the pathname to a downloaded
     * image from a Bundle.
     */
    public static final String IMAGE_PATHNAME = "IMAGE_PATHNAME";

    /**
     * String constant used to extract the request code.
     */
    public static final String REQUEST_CODE = "REQUEST_CODE";

    /**
     * String constant used to extract the URL to an image from a
     * Bundle.
     */
    public static final String IMAGE_URL = "IMAGE_URL";

    /**
     * String constant used to extract the directory pathname to use
     * to store a downloaded image.
     */
    public static final String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";
    
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
     * Sets provided Bundle as the data of the underlying Message
     * @param data - the Bundle to set
     */
    public void setData(Bundle data) {
        mMessage.setData(data);
    }

    /**
     * Accessor method that returns the result code of the message, which
     * can be used to check if the download succeeded.
     */
    public int getResultCode() {
      return mMessage.arg1;
    }

    /**
     * Accessor method that sets the result code
     * @param resultCode - the code too set
     */
    public void setResultCode(int resultCode) {
        mMessage.arg1 = resultCode;
    }

    /**
     * Accessor method that returns Messenger of the Message.
     */
    public Messenger getMessenger() {
      return mMessage.replyTo;
    }

    /**
     * Accessor method that sets Messenger of the Message
     * @param messenger
     */
    public void setMessenger(Messenger messenger) {
        mMessage.replyTo = messenger;
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
     * Accessor method that sets the request code of the message
     * @param requestCode
     */
    public void setRequestCode(int requestCode) {
        mMessage.getData().putInt(REQUEST_CODE,requestCode);
    }

    /**
     * Helper method that sets the request code of the message to the provided Bundle
     * @param data
     * @param requestCode
     */
    public static void setRequestCode(Bundle data, int requestCode) {
        data.putInt(REQUEST_CODE,requestCode);
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
     *  Helper method that sets the URL to the image file into provided Bundle
     * @param data - the Bundle to store the URL
     * @param url - URL to the image file
     */
    public static void setImageURL(Bundle data, Uri url) {
        data.putString(IMAGE_URL,
                       url.toString());
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
     * Helper method that sets the URL to the image file
     * @param url
     */
    public void setImageURL(Uri url) {
        mMessage.getData().putString(IMAGE_URL,
                                     url.toString());
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
     * Helper method that sets the path to the image file into provided Bundle
     * @param data - the Bundle to store the path
     * @param imagePathname - the path to the image file
     */
    public static void setImagePathname(Bundle data, String imagePathname) {
        data.putString(IMAGE_PATHNAME,
                       imagePathname);
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
     * Helper method that sets the path to the image file
     * @param imagePathname - the path to the image file
     */
    public void setImagePathname(String imagePathname) {
        mMessage.getData().putString(IMAGE_PATHNAME,
                                     imagePathname);
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

    /**
     * Helper method that sets the URI to the directory pathname
     * @param directoryPathname
     */
    public void setDirectoryPathname(String directoryPathname) {
        mMessage.getData().putString(DIRECTORY_PATHNAME,
                                     directoryPathname);
    }

    /**
     * Helper method that sets the URI to the directory pathname to provided Bundle
     * @param data
     * @param directoryPathname
     */
    public static void setDirectoryPathname(Bundle data, String directoryPathname) {
        data.putString(DIRECTORY_PATHNAME,directoryPathname);
    }
}
