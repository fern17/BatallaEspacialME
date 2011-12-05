package BatallaEspacial;

/**
 * Clase MessageFromPlayer.
 * Se encarga de armar un mensaje String con los datos de un Jugador.
 * @see Player
 */
public class MessageFromPlayer {
	private String msg;
	
	/**
	 * Constructor. Toma toda la información de un jugador y 
	 *  construye el mensaje, guardándolo en msg
	 * @param _id : Id
	 * @param _name : Nombre
	 * @param _escudo : Valor de escudo actual
	 * @param _x : Posición X actual
	 * @param _y : Posición Y actual
	 * @param _dir : Dirección actual
	 * @param _vm : Valor del byte VM. 'V' si está vivo, M si está muerto
	 * @param _power : Valor de potencia. Si no está disparando, es DisparoEnemigo.VALORESPECIAL.
	 * @param _lx : Posición X actual del laser. Si no está disparando, es DisparoEnemigo.VALORESPECIAL.
	 * @param _ly : Posicion Y actual del laser. Si no está disparando, es DisparoEnemigo.VALORESPECIAL.
	 * @param _moneda : Id de la moneda que agarró antes de enviar el mensaje. -1 si no agarró ninguna.
	 * @param _idAsesino : Número entre 0 y 3 del id enemigo que lo destruyó, o -1 si nadie lo destruyó.
	 * @param _fm : Framerate del jugador.
	 * @see Juego
	 * @see Player
	 * @see DisparoJugador
	 * 
	 */
	MessageFromPlayer	(int _id, char[] _name,int _escudo, 
						int _x, int _y, int _dir, char _vm, 
						int _power,int _lx, int _ly, int _moneda, int _idAsesino, int _fm){
		if(_fm < 1)
			_fm = 1;
		String s_id 	= "" + (_id);
		String s_name 	= String.valueOf(_name);
		String s_escudo = "" + (_escudo);
		String s_x 		= "" + (_x);
		String s_y 		= "" + (_y);
		String s_dir 	= "" + (_dir);
		String s_vm;
		if(_vm == 'V'){ //si estoy vivo, mando V
			s_vm 	= "" + (_vm);
		} else { //si estoy muerto, mando el idasesino
			s_vm    = "" + (_idAsesino);
		}
		String s_power 	= "" + (_power);
		String s_lx 	= "" + (_lx);
		String s_ly 	= "" + (_ly);
		String s_moneda = "" + (_moneda);
		String s_fm 	= "" + (_fm);
				
		s_name 		= charFill(s_name,10,' ');
		s_escudo 	= charFill(s_escudo,4,' ');
		s_x 		= charFill(s_x,4,' ');
		s_y 		= charFill(s_y,4,' ');
		s_power 	= charFill(s_power,3,' ');
		s_lx 		= charFill(s_lx,4,' ');
		s_ly 		= charFill(s_ly,4,' ');
		s_moneda 	= charFill(s_moneda,4,' ');
		s_fm 		= charFill(s_fm,3,' ');
		
		msg = s_id + s_name + s_escudo + s_x + s_y + s_dir + s_vm + s_power + s_lx + s_ly + s_moneda + s_fm;
	}
	
	/**
	 * Rellena una String s con el caracter especificado c, 
	 * insertandolo al principio hasta que se alcance el tamaño deseado n 
	 * @param s : String origen
	 * @param n : Longitud final deseada
	 * @param c : Caracter con el que rellenar al principio
	 * @return : String s rellenada con el caracter especificado.
	 */
	public static String charFill(String s, int n, char c){
		StringBuffer t = new StringBuffer(s);
		while(t.toString().length() < n){
			StringBuffer sb = new StringBuffer("");
			sb.append(c);
			sb.append(t);
			t = sb;
		}
		return t.toString();
	}
	
	/**
	 * @return Mensaje generado en el constructor.
	 */
	public String getMsg(){
		return this.msg;
	}
}


