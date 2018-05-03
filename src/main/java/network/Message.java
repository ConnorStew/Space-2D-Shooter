package network;

/**
 * This class stores a message sent between the server & client along with the connection that sent it.
 */
public class Message {

    /** The message. */
    private Object message;

    Message(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }
}
