package vandy.mooc.model.datamodel;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

/**
 * A thin facade around an Android Message that defines the schema of
 * a reply from the Service back to the Activity.
 */
public class ReplyMessage extends RequestReplyMessageBase {
    /**
     * Constructor is private to ensure the makeReplyMessage() factory
     * method is used.
     */
    private ReplyMessage(Message message) {
        super(message);
    }

    /**
     * Convert a Message into a ReplyMessage.
     */
    public static ReplyMessage makeReplyMessage(Message message) {
        // Make a copy of @a message since it may be recycled.
        return new ReplyMessage(Message.obtain(message));
    }

    /**
     * A factory method that creates a reply message to return to the
     * Activity with the pathname of the downloaded image.
     */
    public static ReplyMessage makeReplyMessage(Uri pathToImageFile,
                                                Uri url,
                                                int requestCode) {
        // Create a ReplyMessage that holds a reference to a Message
        // created via the Message.obtain() factory method.
        ReplyMessage replyMessage =
            new ReplyMessage(Message.obtain());

        // Create a new Bundle and set it as the "data" for the
        // ReplyMessage.
        // TODO -- you fill in here.

        // Set the URL to the image file into the Bundle.
        // TODO -- you fill in here.

        // Set the request code into the Bundle.
        // TODO -- you fill in here.

        // Set the resultCode in the Message to indicate whether the
        // download succeeded or failed.
        // TODO -- you fill in here.

        // Put the path to the image file into the Bundle via the
        // IMAGE_PATHNAME key only if the download succeeded.
        // TODO -- you fill in here.

        return replyMessage;
    }
}
