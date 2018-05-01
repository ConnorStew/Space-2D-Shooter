package network;

import com.esotericsoftware.kryonet.Connection;

/**
 * This class stores a message sent between the server & client along with the connection that sent it.
 */
public class Message {

    /** The message. */
    private Object message;

    /** The connection that sent the message. */
    private Connection connection;

    Message(Connection connection, Object message) {
        this.connection = connection;
        this.message = message;
    }

    public Connection getConnection() {
        return connection;
    }

    public Object getMessage() {
        return message;
    }
}
