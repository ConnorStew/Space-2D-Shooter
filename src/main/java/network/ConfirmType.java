package network;

/**
 * Thees are messages types used by {@link network.Network.ConfirmationMessage} to indicate a successful communication.
 * @author Connor Stewart
 */
public enum ConfirmType {

    /** A score has been added to the database. */
    ScoreAdded(),
    /** The name sent is valid. */
    ValidName()
}
