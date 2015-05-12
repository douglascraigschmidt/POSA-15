package vandy.mooc.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * A thin wrapper around an Android Message that defines the schema of
 * a request from the Activity to the Service.
 */
public class RequestMessage extends RequestReplyMessageBase {
    /**
     * Constructor is private to ensure the makeRequestMessage()
     * factory method is used.
     */
    private RequestMessage(Message message) {
        super(message);
    }

    /**
     * Convert a Message into a RequestMessage.
     */
    public static RequestMessage makeRequestMessage(Message message) {
        // Make a copy of the message since it may be recycled.
        return new RequestMessage(Message.obtain(message));
    }

    /**
     * Factory method creates a RequestMessage to return to the
     * Activity with information necessary to download an image.
     */
    public static RequestMessage makeRequestMessage(int requestCode, 
                                                    Uri url,
                                                    String directoryPathname,
                                                    Messenger replyMessenger) {
        // TODO -- you fill in here, replacing "null" with the proper
        // code.

        // Create a RequestMessage that holds a reference to a Message
        // created via the Message.obtain() factory method.
        RequestMessage requestMessage =
            new RequestMessage(Message.obtain());

        // Create and put a Messenger as the replyTo field in the
        // Message.
        // TODO -- you fill in here.
        requestMessage.mMessage.replyTo = replyMessenger;

        // Create a new Bundle to handle the result.
        // TODO -- you fill in here.
        Bundle data = new Bundle();

        // Put the URL to the image file into the Bundle via the
        // IMAGE_URL key.
        // TODO -- you fill in here.
        data.putString(IMAGE_URL,
                       url.toString());

        // Put the pathname to the image file into the Bundle via the
        // IMAGE_URL key.
        // TODO -- you fill in here.
        data.putString(DIRECTORY_PATHNAME,
                       directoryPathname);

        // Put the request code into the Bundle via the REQUEST_CODE
        // key.
        // TODO -- you fill in here.
        data.putInt(REQUEST_CODE,
                    requestCode);

        // Set the Bundle as the "data" for the Message.
        // TODO -- you fill in here.
        requestMessage.mMessage.setData(data);

        // Return the message to the caller.
        return requestMessage;
    }
}
