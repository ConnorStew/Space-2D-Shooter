package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import network.Message;
import network.Network;
import network.client.ClientHandler;

/**
 * This class displays users who are waiting for a game to start.
 */
public class LobbyScreen extends UIScreen {

    /** This clients connection to the server. */
    private ClientHandler client;

    /** The button that allows the leader to start the room. */
    private TextButton btnStart;

    /** The button that allows this client to leave the room. */
    private TextButton btnLeave;

    /** List to display the players in thr room. */
    private List<String> playerList;

    /** Whether this client is the leader of the lobby. */
    private boolean isLeader;


    public LobbyScreen(ClientHandler client, boolean leader) {
        this.client = client;
        this.isLeader = leader;
    }

    @Override
    public void show() {
        super.show();

        //make background
        Image background = new Image(new Texture(Gdx.files.internal("backgrounds/hubble.jpg")));
        background.setFillParent(true);
        background.setPosition(0, 0);

        btnLeave = new TextButton("Leave", buttonStyle);
        btnLeave.setPosition(Gdx.graphics.getWidth() - btnLeave.getWidth() - 10,10);

        HorizontalGroup lists = new HorizontalGroup();

        //initialising room and player lists
        playerList = new List<String>(lstStyle);

        //check if a list of players is waiting in the queue
        if (client.getQueue().haveReceived(Network.LobbyPlayers.class)) {
            Array<Message> messages = client.getQueue().getMessages(Network.LobbyPlayers.class, true);
            //add the latest LobbyPlayers message
            String[] playerNames = ((Network.LobbyPlayers) messages.get(messages.size - 1).getMessage()).players;
            populatePlayers(playerNames);
        }

        lists.addActor(playerList);
        lists.space(50f);

        //initialising the scroll pane
        ScrollPane pnlScroll = new ScrollPane(lists, scrStyle);
        pnlScroll.setBounds(20, 100, 850, 500);

        stage.addActor(background);
        stage.addActor(pnlScroll);
        stage.addActor(btnLeave);

        if (isLeader) {
            btnStart = new TextButton("Start Game", buttonStyle);
            btnStart.setPosition(10,10);

            stage.addActor(btnStart);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (isLeader && btnStart.isPressed() && validateButtonPress())
            client.getKyroClient().sendTCP(new Network.RequestGameStart());

        if (btnLeave.isPressed() && validateButtonPress()) {
            client.getKyroClient().sendTCP(new Network.LeaveLobby());
            ControlGame.getInstance().setScreen(MultiplayerScreen.getInstance());
        }

    }

    public void populatePlayers(String[] players) {
        playerList.setItems(players);
    }
}
