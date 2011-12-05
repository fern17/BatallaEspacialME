package BatallaEspacial;
import java.util.Vector;

/**
 * Clase MessageFromServer. 
 * Toma los mensajes de todos los jugadores y arma un único mensaje. 
 * Sólo debe usarlo el servidor
 */
public class MessageFromServer {
	private String msg;
	/**
	 * Constructor. Toma un vector de mensajes de los jugadores, 
	 * un vector de objetos Moneda y un FrameRate a imponer a los jugadores
	 * @param _playerString : Vector de mensajes de jugadores
	 * @param _monedas : Vector de Monedas
	 * @param FM : FrameRate a imponer a los jugadores, calculado como el mínimo de todos.
	 * @see Player
	 * @see Broadcaster
	 * @see Moneda
	 * @see Broadcaster::generarMensaje()
	 * @see Broadcaster::recalcularFrameRate()
	 * @see Moneda::generarMensaje
	 */
	public MessageFromServer(Vector _playerString, Vector _monedas, int FM){
		//Inserta el FrameRate primero
		msg = MessageFromPlayer.charFill(String.valueOf(FM),3,'0');
		
		//le saca los caracteres que no importan a los jugadores y los agrega a msg
		for(int i = 0; i < _playerString.size(); i++){
			String t = (String) _playerString.elementAt(i);
			if(t.length() == 0) continue;
			
			String t1 = t.substring(0,Broadcaster.dataPosVM);
			String t2 = t.substring(Broadcaster.dataPosVM+1,Broadcaster.dataPosMoneda);
			String t3 = t.substring(Broadcaster.dataPosVM,Broadcaster.dataPosVM+1);
			
			msg = msg + t1 + t2 + t3;
		}
		
		//Genera el mensaje de cada moneda y los agrega a msg
		for(int i = 0; i < _monedas.size(); i++){
			Moneda m = (Moneda) _monedas.elementAt(i);
			String t4 = m.generarMensaje();
			msg = msg + t4;
		}
	}
	
	/**
	 * @return Mensaje generado en el constructor
	 */
	public String getMsg(){
		return this.msg;
	}
}
