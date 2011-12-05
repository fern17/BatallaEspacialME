package BatallaEspacial;
import javax.microedition.lcdui.game.Sprite;

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
	
	private int step = 1; //cuanto se mueve por frame
	
	public int dinero = 100;
	
	public int idMoneda 	= -1; 

	private int idAsesino = -1;
	//Atributos
	private int inicio_v 	= 5;
	private int inicio_e 	= 100;
	private int inicio_p 	= 10;
	private int inicio_c 	= 100;
	
	public DisparoJugador disparo = null;
	public int velocidad;
	public int escudo;
	public int potencia;
	public int cristales;
	
	public int vidas 		= 3;
	public int puntos 		= 0;
	
	public boolean estaVivo = false;
	public boolean puedeDisparar = true;
	//cuanto sale cada mejora
	private int mejoraVelocidad 	= 10;
	private int mejoraEscudo 		= 5;
	private int mejoraPotencia 		= 10;
	private int mejoraCristales 	= 1;
	
	public Juego juego;
	
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
	
	
	
	public void actualizar(int _x, int _y, int _dir){
		this.x = _x;
		this.y = _y;
		this.dir = _dir;
		cambiarFrame();
		
	}
	
	public void moverDisparo(){
		this.disparo.mover();
	}


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

	public void incrementarMoneda(int _valor){
		dinero = dinero + _valor;
	}
	
	public int decrementarVida(){
		vidas = vidas - 1;
		return vidas;
	}
	
	
	
	public void setDisparar(boolean _d){
		this.puedeDisparar = _d;
	}
	public void incrementarPuntos(){
		puntos = puntos + 1;
	}
	
	//funciones de interaccion
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
		boolean rollback = false;
		this.s_player.setPosition(x,y);
		cambiarFrame();
		if( this.s_player.collidesWith(juego.mapa.backgroundL2, false)){
			//si colisiona, hago rollback
			rollback = true;
		}
		else{ //si ya colisione, ni pruebo con otra cosa
			for(int i = 0; i < juego.broadcaster.cantidadJugadores; i++){
				Enemy e = (Enemy) juego.naves.elementAt(i);
				if(e.id == this.identificador) continue; //no debo colisionar conmigo mismo
				if(e.s_enemy.isVisible() & this.s_player.collidesWith(e.s_enemy,true)){ 
					rollback = true;
					break;
				}
			}
		}
		if(rollback == true){
			this.x = t_x;
			this.y = t_y;
			s_player.setPosition(t_x,t_y);
		}
	}
	
	public boolean disparar(){
		if(this.puedeDisparar){
			this.disparo.set();
			setDisparar(false); //ahora no puede disparar
			return true;
		}
		return false;
		
	}
	
	public boolean colisionar(DisparoEnemigo de){
		return (this.s_player.collidesWith(de.s_disparoenemigo,true));
	}
	
	public boolean colisionar(Moneda m){
		return (this.s_player.collidesWith(m.s_moneda, true));
	}
	
	//funciones que aumentan los stats
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
	
	public int costoVelocidad(){ return (mejoraVelocidad * inicio_e); 	}
	
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
	
	public int costoEscudo(){return (mejoraEscudo * inicio_e); }
	
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
	
	public int costoPotencia(){	return (mejoraPotencia * inicio_p);	}
	
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
	
	public int costoCristales(){ return (mejoraCristales * inicio_c);	}
	
}
