package BatallaEspacial;
import javax.microedition.lcdui.game.Sprite;

/**
 * Clase DisparoJugador.
 * Representa el disparo hecho por el Jugador.
 * @see Player
 */
public class DisparoJugador {
	//posicion inicial
	public int xinicial;
	public int yinicial;
	//posicion
	public int x;
	public int y;
	//pendiente
	public int mx; 
	public int my;
	public int dir;
	//potencia del disparador
	public int potencia;
	//distancia que puede recorrer
	public int distancia;  
	//owner
	public Player jugador;
	public Sprite s_disparo;
	private boolean existe = false;
	private int step = 3;
	private Juego juego = null;
	public static final int POSICIONESPECIAL = -1;
	/**
	 * Constructor. Asigna el objeto Juego de la aplicación y el Jugador que es dueño de este disparo. Deja todo con valores por defecto. 
	 * @param _j : Objeto Juego
	 * @param _duenio : Objeto Player
	 * @see Player
	 * @see Juego
	 */
	public DisparoJugador(Juego _j, Player _duenio){ //constructor por defecto
		this.juego = _j;
		this.jugador = _duenio;
		this.xinicial = DisparoEnemigo.VALORESPECIAL;
		this.yinicial = DisparoEnemigo.VALORESPECIAL;
		this.x = DisparoEnemigo.VALORESPECIAL;
		this.y = DisparoEnemigo.VALORESPECIAL;
		
		this.potencia = DisparoEnemigo.VALORESPECIAL;
		s_disparo = new Sprite(juego.im.getImgDisparo());
		s_disparo.setVisible(false);
		s_disparo.setRefPixelPosition(16, 16);
	}
	
	/**
	 * Asigna toda la información importante al disparo, obtenida del objeto Player.
	 */
	public void set(){
		this.xinicial = jugador.x + 8;//correccion por sprite
		this.yinicial = jugador.y + 8;
		this.x = this.xinicial;
		this.y = this.yinicial;
		this.dir = jugador.dir;
		this.distancia = jugador.cristales;
		switch(dir){
			case Player.DIRN:{
				this.mx =  0;
				this.my = -1;
				break;
			}
			case Player.DIRNE:{
				this.mx =  1;
				this.my = -1;
				break;
			}
			case Player.DIRE:{
				this.mx =  1;
				this.my =  0;
				break;
			}
			case Player.DIRSE:{
				this.mx =  1;
				this.my =  1;
				break;
			}
			case Player.DIRS:{
				this.mx =  0;
				this.my =  1;
				break;
			}
			case Player.DIRSO:{
				this.mx = -1;
				this.my =  1;
				break;
			}
			case Player.DIRO:{
				this.mx = -1;
				this.my =  0;
				break;
			}
			case Player.DIRNO:{
				this.mx = -1;
				this.my = -1;
				break;
			}
		}
		s_disparo.setPosition(this.x,this.y);
		s_disparo.setVisible(true);
		this.potencia = jugador.potencia;
		this.existe   = true;
	}
	
	/**
	 * Mueve el disparo. La velocidad de movimiento es el doble que la del jugador.
	 * Calcula la distancia recorrida y si supero el limite, borra el disparo, llamando a unSet().
	 * @see unSet()
	 * @see dist()
	 */
	public void mover(){
		if(existe == true){
			step = jugador.velocidad*2;
			this.x = this.x + this.mx*step;
			this.y = this.y + this.my*step;
			double d = dist(this.xinicial,this.yinicial,this.x,this.y);
			s_disparo.setPosition(this.x,this.y);
			if(d >= this.distancia){
				this.unSet();
			}
		}
	}
	/**
	 * Pone toda la información del disparo en default.
	 */
	public void unSet(){
		this.existe = false;
		this.x = DisparoJugador.POSICIONESPECIAL;
		this.y = DisparoJugador.POSICIONESPECIAL;
		this.potencia = DisparoJugador.POSICIONESPECIAL;
		s_disparo.setVisible(false);
		jugador.setDisparar(true);
	}
	/**
	 * Calcula la distancia entre 2 puntos A y B
	 * @param x0 : Coordenada X del punto A
	 * @param y0 : Coordenada Y del punto A
	 * @param x1 : Coordenada X del punto B
	 * @param y1 : Coordenada Y del punto B
	 * @return : Distancia entre A y B
	 */
	public static double dist(int x0, int y0, int x1, int y1){
		return Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
	}
}
