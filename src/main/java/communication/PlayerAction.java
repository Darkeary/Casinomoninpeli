package communication;

public class PlayerAction {

    public static final int STAY = 0;
    public static final int HIT = 1;
    public static final int DOUBLE = 2;
    public static final int SPLIT = 3;
    public static final int SURRENDER = 4;
    public static final int PLAY = 5;
    public static final int QUIT = 6;
    public static final int DISCONNECT = 6;

    final int actionId;
    final long playerId;

    public PlayerAction(int actionId, long playerId) {
        this.actionId = actionId;
        this.playerId = playerId;
    }

    public int getActionId() {
        return actionId;
    }

    public long getPlayerId() {
        return playerId;
    }
}
