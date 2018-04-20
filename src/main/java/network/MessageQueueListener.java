package network;

/**
 * This class is used to receive messages from the MessageQueue.
 */
public interface MessageQueueListener {

    /**
     * Called when a message is received.
     * @param message the message received
     * @return whether to consume the message
     */
    boolean received(Message message);

}
