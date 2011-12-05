package BatallaEspacial;

/**
 * Clase GameState
 * Define constantes sobre el estado del juego.
 * 
 */
public class GameState {
	public static final int DEFAULT = 0; //valor por defecto
	public static final int RUNNING = 1; //el juego esta en un bucle infinito, dibujando y actualizando
	public static final int READY = 2; //los jugadores estan en el lobby esperando por el server para iniciar, todos tienen id
	public static final int WAITING_PLAYERS = 3; //el server esta en esperando jugadores que se conecten
	public static final int WAITING = 4;//el jugador esta esperando que el server le devuelva su id
	public static final int RESPONSE = 5; //el server responde con una string pasada por parametro
	public static final int READYTOSTART = 6; //el server tiene todos los jugadores confirmados
}
