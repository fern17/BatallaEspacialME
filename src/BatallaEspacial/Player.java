package BatallaEspacial;
import javax.microedition.lcdui.game.Sprite;

/**
 * Clase Player. Maneja toda la lógica pertinente a un jugador.
 * Mueve el sprite, lo cambia, actualiza los atributos, 
 * calcula colisiones, calcula puntos, y se encarga del disparo.
 *
 */
public class Player {
	//Direcciones
	public static final int DIRN 	= 0; //Norte
	public static final int DIRNE 	= 1; //NorEste
	public static final int DIRE 	= 2; //Este
	public static final int DIRSE 	= 3; //SurEste
	public static final int DIRS 	= 4; //Sur
	public static final int DIRSO 	= 5; //SurOeste
	public static final int DIRO 	= 6; //Oeste
	public static final int DIRNO 	= 7; //NorOeste
	
	//Constantes para sacudir
	public static final int DERECHA = 1;
	public static final int IZQUIERDA = 0;
	public static final int DELTA = 3;
	
	
	//identificador
	public int identificador ;
	public char[] nombre = new char[BatallaEspacial.MAX_NAME_LENGTH]; 
	
	public Sprite s_player;
	
	//Posicion
	private int xinicio;
	private int yinicio;
	public int x;
	public int y;
	public int width = 48;
	public int height = 48;
	public int dir; //señala donde esta mirando, para saber donde disparar
	
	private int step = 1; //cuánto se mueve por frame
	
	public int dinero = 100;
	public int vidas 		= 3;
	public int puntos 		= 0;
	
	public int idMoneda 	= -1; 
	private int idAsesino = -1;
	
	//Atributos
	private int inicio_v 	= 5;
	private int inicio_e 	= 100;
	private int inicio_p 	= 10;
	private int inicio_c 	= 100;
	
	public int velocidad;
	public int escudo;
	public int potencia;
	public int cristales;
	
	//cuanto sale cada mejora
	private int mejoraVelocidad 	= 10;
	private int mejoraEscudo 		= 5;
	private int mejoraPotencia 		= 10;
	private int mejoraCristales 	= 1;
	
	public boolean estaVivo = false;
	public boolean puedeDisparar = true;
	
	public Juego juego;
	public DisparoJugador disparo = null;
	
	/**
	 * Constructor. Construye el Sprite y le asigna los datos iniciales al jugador.
	 * @param _j : Objeto Juego de la aplicación
	 * @param _id : Id del jugador
	 * @param _xinicio : Posición X inicial
	 * @param _yinicio : Posición Y inicial
	 * @param _nombre : Nombre del jugador
	 * @see Juego
	 * @see DisparoJugador
	 * @see ImageManager
	 * @see Player::setInicio()
	 * @see Player::cambiarFrame()
	 */
	public Player(Juego _j, int _id, int _xinicio, int _yinicio, String _nombre){
		this.juego = _j;
		this.identificador = _id;
		//correcciones por setRefPixel
		this.xinicio = _xinicio;
		this.yinicio = _yinicio;
		this.disparo = new DisparoJugador(juego, this);
		_nombre.getChars(0, _nombre.length(), this.nombre, 0);
		
		s_player = new Sprite(juego.im.getImgNavePlayer(),48,48);
		s_player.setRefPixelPosition(24, 24); //centra las coordenadas
		
		setInicio(); //setea valores por defecto
		cambiarFrame();
		estaVivo = true;
	}
	
	/**
	 * Construye el mensaje de este jugador, con sus datos propios.
	 * Además, si esta función se llama luego de ser destruído, se llama a Player::setInicio()
	 * 
	 * @return Mensaje generado
	 * @see MessageFromPlayer
	 * @see Player::setInicio()
	 */
	public String generarMensaje(){
		char life;
		if(estaVivo | escudo == -1) 	life = 'V';
		else			life = 'M';
		
		MessageFromPlayer mfp = 
			new MessageFromPlayer(identificador, nombre, escudo, x, y, dir,
								  life, disparo.potencia, disparo.x, disparo.y, idMoneda, idAsesino, (int) 1000/juego.milisegundosEnDibujar);
		//lo revivo DESPUES de mandar el mensaje
		if(estaVivo == false){
			setInicio();
		}
		return mfp.getMsg();
	}
	
	/**
	 * Asigna los valores por defecto al jugador. Además, si se le acabaron las vidas, 
	 * le asigna los valores especiales para que el servidor se entere que está muerto.
	 */
	public void setInicio(){
		this.x 			= this.xinicio;
		this.y 			= this.yinicio;
		this.s_player.setPosition(this.x,this.y);
		this.s_player.setVisible(true);
		switch(this.identificador){
			case 0:{
				this.dir	    = Player.DIRSE;
				break;
			}
			case 1:{
				this.dir	    = Player.DIRNO;
				break;
			}
			case 2:{
				this.dir	    = Player.DIRSO;
				break;
			}
			case 3:{
				this.dir	    = Player.DIRNE;
				break;
			}
			default:{
				this.dir	    = Player.DIRN;
				break;
			}
		}
		this.dir	    = Player.DIRN;
		
		if(vidas > 0){
			this.escudo 	= this.inicio_e;
			this.estaVivo		= true;
			this.puedeDisparar = true;
		}
		else{
			this.escudo 	= -1;
			this.estaVivo = false;
			this.s_player.setVisible(false);
			//juego.lm.remove(this.s_player); //no lo dibujo mas
			this.puedeDisparar = false; //ya no puede disparar
		}
		this.velocidad 	= this.inicio_v;
		this.potencia 	= this.inicio_p;
		this.cristales 	= this.inicio_c;
		this.idMoneda = -1;
		this.idAsesino = -1;
	}
	
	/**
	 * Calcula si el disparo afectó al jugador. De ser así, le resta el valor de escudo.
	 * Si se le acabó el escudo, le resta una vida y lo llama a respawn en su base.
	 * @param _de : DisparoEnemigo con el que se va a probar colisión
	 * @return Cantidad de escudo restante
	 * @see DisparoEnemigo
	 * @see Player::decrementarVida()
	 */
	//retorna el escudo que le queda o -1 si lo destruyo y se regenero en otro lado
	public int recibirDisparo(DisparoEnemigo _de){
		int _potenciaDisparo = _de.potencia;
		escudo = escudo - _potenciaDisparo;
		if(escudo <= 0){ 			
			idAsesino = _de.id;
			estaVivo = false;
			decrementarVida();
			this.escudo = 0;
			return -1; 		//escudo destruido 
		}
		return escudo;		//escudo restante
	}
	
	/**
	 * Mueve un disparo. Llama a DisparoJugador::mover()
	 * @see DisparoJugador::mover()
	 */
	public void moverDisparo(){
		this.disparo.mover();
	}

	/**
	 * Cambia el frame del Sprite dependiendo de la dirección que esté mirando el jugador.
	 */
	private void cambiarFrame(){
		switch(this.dir){
		case Player.DIRN:{
			this.s_player.setFrame(0);
			break;
		}
		case Player.DIRNE:{
			this.s_player.setFrame(1);
			break;
		}
		case Player.DIRE:{
			this.s_player.setFrame(2);
			break;
		}
		case Player.DIRSE:{
			this.s_player.setFrame(3);
			break;
		}
		case Player.DIRS:{
			this.s_player.setFrame(4);
			break;
		}
		case Player.DIRSO:{
			this.s_player.setFrame(5);
			break;
		}
		case Player.DIRO:{
			this.s_player.setFrame(6);
			break;
		}
		case Player.DIRNO:{
			this.s_player.setFrame(7);
			break;
		}
		}
	}

	/**
	 * Aumenta el valor de dinero.
	 * @param _valor : Valor a aumentar
	 */
	public void incrementarMoneda(int _valor){
		dinero = dinero + _valor;
	}
	
	/**
	 * Resta una vida del jugador
	 * @return Vidas restantes
	 */
	public int decrementarVida(){
		vidas = vidas - 1;
		return vidas;
	}
	
	/**
	 * Cambia el valor de la variable Player::puedeDisparar.
	 * @param _d : Valor a imponer
	 */
	public void setDisparar(boolean _d){
		this.puedeDisparar = _d;
	}
	
	/**
	 * Incrementa un punto.
	 */
	public void incrementarPuntos(){
		puntos = puntos + 1;
	}
	
	/**
	 * Mueve al jugador. Luego de moverlo, se fija si colisiona con un enemigo o con
	 * el TiledLayer capa 2, de ser así, vuelve a la posición, haciendo nulo el movimiento. 
	 * @param _direccion : Dirección en la cual moverse
	 * @see Player::cambiarFrame()
	 * @see Player::confirmarMovimiento()
	 */
	public void mover(int _direccion){
		dir = _direccion;
		
		int t_x = this.x;
		int t_y = this.y;
		
		step = velocidad;
		switch(dir){
			case Player.DIRN:{
				this.y = this.y - step;
				break;
			}
			case Player.DIRNE:{
				this.x = this.x + step;
				this.y = this.y - step;
				break;
			}
			case Player.DIRE:{
				this.x = this.x + step;
				break;
			}
			case Player.DIRSE:{
				this.x = this.x + step;
				this.y = this.y + step;
				break;
			}
			case Player.DIRS:{
				this.y = this.y + step;
				break;
			}
			case Player.DIRSO:{
				this.x = this.x - step;
				this.y = this.y + step;
				break;
			}
			case Player.DIRO:{
				this.x = this.x - step;
				break;
			}
			case Player.DIRNO:{
				this.x = this.x - step;
				this.y = this.y - step;
				break;
			}
		}
		
		this.s_player.setPosition(x,y);
		cambiarFrame();
		boolean rollback = confirmarMovimiento();
		if(rollback == true) {
			this.x = t_x;
			this.y = t_y;
			s_player.setPosition(t_x,t_y);
		}
	}

	/**
	 * Es llamada luego de efectuar algún movimiento. Verifica que no se haya colisionado con nada
	 * @return  True si se pudo mover. False si no.
	 * @see Enemy
	 * @see Mapa
	 */
	private boolean confirmarMovimiento(){
		boolean rollback = false;
		if( this.s_player.collidesWith(juego.mapa.backgroundL2, true)){
			//si colisiona, hago rollback
			rollback = true;
		}
		else{ //si ya colisione, ni pruebo con otra cosa
			for(int i = 0; i < juego.broadcaster.cantidadJugadores; i++){
				Enemy e = (Enemy) juego.naves.elementAt(i);
				if(e.id == this.identificador) 
					continue; //no debo colisionar conmigo mismo
				if(e.s_enemy.isVisible()) {
					if(this.s_player.collidesWith(e.s_enemy,false)) {
							if (this.s_player.collidesWith(e.s_enemy,true)){ 
								rollback = true;
								break;
							}
					}
				}
			}
		}
		return rollback;
	}
	
	/**
	 * Se encarga de mover la nave unos pixeles al costado
	 * @param _dir : Direccion a donde moverse, izquierda o derecha. Siempre se toma como referencia la trompa de la nave, independientemente de su orientación.
	 * @see Player::confirmarMovimiento()
	 * @see Player::cambiarFrame()
	 */
	
	public void sacudir(int _dir){
		int t_x = this.x;
		int t_y = this.y;
		
		int sentidox = 0;
		int sentidoy = 0;
		
		switch(this.dir){
			case Player.DIRN:{
				sentidox = 1;
				break;
			}
			case Player.DIRNE:{
				sentidox = 1;
				sentidoy = 1;
				break;
			}
			case Player.DIRE:{
				sentidoy = 1;
				break;
			}
			case Player.DIRSE:{
				sentidox = -1;
				sentidoy = 1;
				break;
			}
			case Player.DIRS:{
				sentidox = -1;
				break;
			}
			case Player.DIRSO:{
				sentidox = -1;
				sentidoy = -1;
				break;
			}
			case Player.DIRO:{
				sentidoy = -1;
				break;
			}
			case Player.DIRNO:{
				sentidox = 1;
				sentidoy = -1;
				break;
			}
		}
		switch(_dir){
			case Player.DERECHA:{
				this.x = this.x + sentidox*Player.DELTA;
				this.y = this.y + sentidoy*Player.DELTA;
				break;
			}
			case Player.IZQUIERDA:{
				this.x = this.x - sentidox*Player.DELTA;
				this.y = this.y - sentidoy*Player.DELTA;
				break;
			}
		}
		
		this.s_player.setPosition(x,y);
		cambiarFrame();
		boolean rollback = confirmarMovimiento();
		if(rollback == true) {
			this.x = t_x;
			this.y = t_y;
			s_player.setPosition(t_x,t_y);
		}
	}
	
	/**
	 * Llamada cuando el jugador apreta el boton de disparar. Se fija si el jugador
	 * está en condiciones de disparar (basado en el contador de la distancia del disparo 
	 * anterior). Si puede disparar, llama a DisparoJugador::set() y a Player::setDisparar() con False.
	 * @return True si el disparo se efectuó con éxito, False si no.
	 * @see DisparoJugador::set()
	 * @see Player::setDisparar()
	 */
	public boolean disparar(){
		if(this.puedeDisparar){
			this.disparo.set();
			setDisparar(false); //ahora no puede disparar
			return true;
		}
		return false;
	}
	
	/**
	 * Prueba si el jugador colisiona con un DisparoEnemigo.
	 * @param de : DisparoEnemigo con el cual colisionar.
	 * @return True si colisiona, False si no.
	 * @see DisparoEnemigo
	 */
	public boolean colisionar(DisparoEnemigo de){
		if(this.s_player.collidesWith(de.s_disparoenemigo, false))
			return (this.s_player.collidesWith(de.s_disparoenemigo,true));
		else 
			return false;
	}
	
	/**
	 * Prueba si el jugador colisiona con una Moneda.
	 * @param m : Moneda con la cual colisionar
	 * @return : True si colisiona, False si no.
	 * @see Moneda
	 */
	public boolean colisionar(Moneda m){
		if(this.s_player.collidesWith(m.s_moneda, false))
			return (this.s_player.collidesWith(m.s_moneda, true));
		else
			return false;
	}
	
	//funciones que aumentan los stats
	/**
	 * Aumenta en un valor la velocidad y disminuye el dinero, si alcanza. 
	 * @return True si pudo aumentarlo, False si no.
	 * @see Player::costoVelocidad()
	 */
	public boolean aumentarVelocidad(){
		if (dinero-costoVelocidad() >= 0) {
			dinero 		= dinero - costoVelocidad();
			velocidad 	= velocidad + 2;
			inicio_v	= velocidad;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return Costo de mejorar la velocidad.
	 */
	public int costoVelocidad(){ return (mejoraVelocidad * inicio_e); 	}
	
	/**
	 * Aumenta en un valor el escudo y disminuye el dinero, si alcanza. 
	 * @return True si pudo aumentarlo, False si no.
	 * @see Player::costoEscudo()
	 */
	public boolean aumentarEscudo(){
		if (dinero-costoEscudo() >= 0) {
			dinero 		= dinero - costoEscudo();
			escudo		= (int) ( (double) escudo * 1.1);
			inicio_e	= (int) ( (double) inicio_e * 1.1);;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return Costo de mejorar el escudo
	 */
	public int costoEscudo(){return (mejoraEscudo * inicio_e); }
	
	/**
	 * Aumenta en un valor la potencia y disminuye el dinero, si alcanza. 
	 * @return True si pudo aumentarlo, False si no.
	 * @see Player::costoPotencia()
	 */
	public boolean aumentarPotencia(){
		if (dinero-costoPotencia() >= 0) {
			dinero 		= dinero - costoPotencia();
			potencia	= (int) ( (double) potencia * 1.1); //agrega 10%
			inicio_p	= potencia;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return Costo de mejorar la potencia
	 */
	public int costoPotencia(){	return (mejoraPotencia * inicio_p);	}
	
	/**
	 * Aumenta en un valor los cristales y disminuye el dinero, si alcanza. Si lo aumentó,
	 * llama a Juego::actualizarCristales() para que se actualice la máscara.
	 * @return True si pudo aumentarlo, False si no.
	 * @see Player::costoCristales()
	 * @see Juego::actualizarCristales()
	 */
	public boolean aumentarCristales(){
		if (dinero-costoCristales() >= 0) {
			dinero 		= dinero - costoCristales();
			cristales	= (int) ( (double) cristales * 1.2); //agrega 20%
			inicio_c	= cristales;
			juego.actualizarCristales();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return Costo de mejorar los cristales
	 */
	public int costoCristales(){ return (mejoraCristales * inicio_c);	}
	
}
