package network;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * This class stores messages that are not consumed by listeners.
 * @author Connor Stewart
 */
public class MessageQueue extends Listener {

    /** Messages that have been received. */
    private Array<Message> messages = new Array<Message>();

    /** Listeners who want to receive messages. */
    private Array<MessageQueueListener> listeners = new Array<MessageQueueListener>();

    @Override
    public void received(Connection connection, Object object) {
        Message received = new Message(object);

        for (MessageQueueListener listener : listeners) {
            if (listener.received(received)) {
                break;
            } else {
                messages.add(received);
            }
        }
    }

    /**
     * Checks if this MessageQueue has received a message of the desired class.
     * @param messageClass the messages class
     * @return whether a message of the desired class have been received
     */
    public boolean haveReceived(Class messageClass) {


        for (Message message : messages) {
            System.out.println(messageClass + " = " + message.getClass());
            if (message.getMessage().getClass().isAssignableFrom(messageClass))
                return true;
        }


        return false;
    }

    /**
     * Gets messages that matches the given class and destroys the copy in the MessageQueue if destroy is true.
     * @param messageClass the class of message to get
     * @param destroy whether to destroy found messages
     * @return an Array of messages that match the given class
     */
    public Array<Message> getMessages(Class messageClass, boolean destroy) {
        Array<Message> validMessages = new Array<Message>();

        for (Message message : messages) {
            if (message.getMessage().getClass().equals(messageClass)) {
                validMessages.add(message);
                if (destroy) {
                    messages.removeValue(message, false);
                }
            }
        }

        return validMessages;
    }

    /**
     * Adds a listener to this MessageQueue.
     * @param toAdd the listener to add
     */
    public void addListener(MessageQueueListener toAdd) {
        listeners.add(toAdd);
    }

}