
public class GameState {
	public static final int DEFAULT = 0; //valor por defecto
	public static final int RUNNING = 1; //el juego esta en un bucle infinito, dibujando y actualizando
	public static final int PAUSED = 2; // el juego esta pausado
	public static final int READY = 3; //los jugadores estan en el lobby esperando por el server para iniciar, todos tienen id
	public static final int WAITING_PLAYERS = 4; //el server esta en esperando jugadores que se conecten
	public static final int WAITING = 5;//el jugador esta esperando que el server le devuelva su id
	public static final int STARTING = 6; //el server le envia la informacion a los jugadores para empezar. UNUSED
	public static final int GAMESTART = 7; //el server le enviara la cadena start
	public static final int RESPONSE = 8; //el server responde con una string pasada por parametro
	public static final int READYTOSTART = 9; //el server tiene todos los jugadores confirmados
	
	
}
