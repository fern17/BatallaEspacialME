package BatallaEspacial;
import javax.microedition.lcdui.game.Sprite;

/**
 * Clase Moneda
 * Representa una moneda en el juego, que puede ser NORMAL, con valor 5, o ESPECIAL, con valor 20.
 *
 */
public class Moneda {
	private Juego juego = null;
	public int id;
	public int x;
	public int y;
	public int valor;
	public Sprite s_moneda;
	public static final int NORMAL = 0;
	public static final int ESPECIAL = 1;
	public static final int valorMonedaNormal = 5;
	public static final int valorMonedaEspecial = 20;
	/**
	 * Constructor. Recibe todos los datos inherentes a una Moneda y crea el objeto.
	 * @param _j : Objeto Juego de la aplicacion
	 * @param _id : Id de la moneda
	 * @param _x : Posición X inicial
	 * @param _y : Posición Y inicial
	 * @param _valor : Valor. Si es Moneda.NORMAL, se asigna 5. Si es Moneda.ESPECIAL, se asigna 20.
	 * @see Juego
	 * @see ImageManager
	 */
	public Moneda(Juego _j, int _id, int _x, int _y, int _valor){
		this.juego = _j;
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.valor = _valor;
		if(this.valor == Moneda.NORMAL){
			this.s_moneda = new Sprite(juego.im.getImgMonedaNormal());
		} else {
			this.s_moneda = new Sprite(juego.im.getImgMonedaEspecial());
		}
		
		s_moneda.setRefPixelPosition(12,12);
		s_moneda.setPosition(this.x, this.y);
	}
	
	/**
	 * Construye el mensaje sobre los datos de la moneda, para que se distribuya en los mensajes desde el servidor
	 * @return : String representando los datos de la moneda.
	 */
	public String generarMensaje(){
		String s_id = String.valueOf(this.id);
		String s_valor;
		if(this.valor == Moneda.NORMAL){
			s_valor = String.valueOf(Moneda.valorMonedaNormal);
		} else {
			s_valor = String.valueOf(Moneda.valorMonedaEspecial);
		}
		String s_x = String.valueOf(this.x);
		String s_y = String.valueOf(this.y);
		
		s_id = MessageFromPlayer.charFill(s_id, 4, ' ');
		s_x = MessageFromPlayer.charFill(s_x, 4, ' ');
		s_y = MessageFromPlayer.charFill(s_y, 4, ' ');
		s_valor = MessageFromPlayer.charFill(s_valor, 2, ' ');
		
		String msg = s_id + s_x + s_y + s_valor;
		return msg;
	}
}
