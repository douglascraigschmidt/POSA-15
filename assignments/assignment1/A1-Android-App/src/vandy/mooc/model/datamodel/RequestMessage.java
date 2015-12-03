package vandy.mooc.model.datamodel;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * A thin facade around an Android Message that defines the schema of
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
        // Make a copy of @a message since it may be recycled.
        return new RequestMessage(Message.obtain(message));
    }

    /**
     * Factory method creates a RequestMessage to return to the
     * Activity with information necessary to download an image.
     */
    public static RequestMessage makeRequestMessage(int requestCode,
                                                    Uri url,
                                                    Uri directoryPathname,
                                                    Messenger replyMessenger) {
        // Create a RequestMessage that holds a reference to a Message
        // created via the Message.obtain() factory method.
        RequestMessage requestMessage =
            new RequestMessage(Message.obtain());

        // Set replyMessenger into the Message's replyTo field.
        // TODO -- you fill in here.

        // Create a new Bundle and set it as the "data" for the
        // RequestMessage.
        // TODO -- you fill in here.

        // Set the URL to the image file into the Bundle.
        // TODO -- you fill in here.

        // Set the pathname to the directory into the Bundle.
        // TODO -- you fill in here.

        // Set the request code into the Bundle.
        // TODO -- you fill in here.

        // Return the message to the caller.
        return requestMessage;
    }
}
