package vandy.mooc.model.datamodel;

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
        mMessage.getData().putInt(REQUEST_CODE,
                                  requestCode);
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
        return data.getParcelable(IMAGE_URL);
    }

    /**
     * Helper method that sets the URL to the image file
     * @param url
     */
    public void setImageURL(Uri url) {
        mMessage.getData().putParcelable(IMAGE_URL,
                                         url);
    }

    /**
     * Helper method that returns the path to the image file if it is
     * download successfully.
     */
    public Uri getImagePathname() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the path to the image file from the Bundle, which
        // should be stored using the IMAGE_PATHNAME key.
        return data.getParcelable(IMAGE_PATHNAME);
    }

    /**
     * Helper method that sets the path to the image file
     * @param imagePathname - the path to the image file
     */
    public void setImagePathname(Uri imagePathname) {
        mMessage.getData().putParcelable(IMAGE_PATHNAME,
                                         imagePathname);
    }

    /**
     * Helper method that returns the URI to the directory pathname.
     */
    public Uri getDirectoryPathname() {
        // Extract the data from Message, which is in the form of a
        // Bundle that can be passed across processes.
        Bundle data = mMessage.getData();

        // Extract the directory pathname the Bundle, which should be
        // stored using the DIRECTORY_PATHNAME key.
        return data.getParcelable(DIRECTORY_PATHNAME);
    }

    /**
     * Helper method that sets the URI to the directory pathname
     * @param directoryPathname
     */
    public void setDirectoryPathname(Uri directoryPathname) {
        mMessage.getData().putParcelable(DIRECTORY_PATHNAME,
                                         directoryPathname);
    }
}
