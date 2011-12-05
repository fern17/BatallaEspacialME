package BatallaEspacial;
import javax.microedition.lcdui.game.Sprite;

/**
 * Clase DisparoEnemigo.
 * Representa un disparo hecho por cualquiera de los enemigos. 
 * Los objetos de esta clase se crean y destruyen cada vez que 
 * dispara alguno de los enemigos. 
 * @see Enemy
 */
public class DisparoEnemigo {
	public int id;
	private int x;
	private int y;
	public Sprite s_disparoenemigo;
	public int potencia;
	public static final int VALORESPECIAL = -1;
	private Juego juego;
	
	/**
	 * Constructor. Asigna toda la información importante al disparo
	 * @param _j : Objeto Juego de la aplicación
	 * @param _id : Id del enemigo
	 * @param _x : Posición X del disparo
	 * @param _y : Posicion Y del disparo
	 * @param _p : Potencia del disparo
	 * @see Juego
	 * @see Enemy
	 * @see ImageManager
	 */
	public DisparoEnemigo(Juego _j, int _id, int _x, int _y, int _p){
		this.juego = _j;
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.potencia = _p;
		
		s_disparoenemigo = new Sprite(juego.im.getImgDisparo());
		s_disparoenemigo.setRefPixelPosition(16, 16);
		s_disparoenemigo.setPosition(this.x,this.y);
		if(this.x == DisparoEnemigo.VALORESPECIAL & this.y == DisparoEnemigo.VALORESPECIAL){
			s_disparoenemigo.setVisible(false);
		}
	}
}