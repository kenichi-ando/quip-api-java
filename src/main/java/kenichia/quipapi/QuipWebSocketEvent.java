package kenichia.quipapi;

public interface QuipWebSocketEvent {

	public default void onMessage(QuipMessage message, QuipUser user, QuipThread thread) {};

	public default void onHeartbeat() {};

	public default void onAlive(String message) {};

	public default void onError(String debug) {};
}