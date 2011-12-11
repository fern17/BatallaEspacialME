package BatallaEspacial;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

/**
 * Clase Juego.
 * Se encarga de toda la lógica de la aplicación una vez que se está
 * jugando. Captura el teclado, calcula colisiones, actualiza posiciones, 
 * arma mensajes, envia y recibe mensajes y sincroniza los jugadores.
 */
public class Juego extends GameCanvas implements Runnable {
	//Posiciones iniciales de todos los jugadores
	public static final int[] xiniciales = {50,900,900,50};
	public static final int[] yiniciales = {50,900,50,900};
	public int fpsDesdeServer = 30;
	public int milisegundosEnDibujar = 33;
	public static final int FRAMESAT30 = 33;
	public static final int FPSLIMITE = 30;
	
	private BatallaEspacial midlet;
	public Broadcaster broadcaster;
	private int gameState = GameState.DEFAULT;
	
	public LayerManager lm;
	public LayerManager lm2;
	public ImageManager im;
	public Mapa mapa;
	public String nombreJugador = "";
	public Sprite s_mascara = null;
	
	public Player jugador = null;
	public Vector naves = new Vector(); //guarda la info de las 4 naves
	public Vector monedas = new Vector();
	public Vector disparos = new Vector();
	
	public boolean esServidor = false;
	//usado para evitar que se generen monedas en lugares donde no se pueden agarrar
	public boolean monedaGenerada = false; 
	public int idMonedaNueva = 0; // identificador de las monedas
	public int tiempoMonedas = 30000; //cuanto tiempo tardan en generarse las monedas
	private static final int  MAX_MONEDAS = 25; 
	public Font fuenteJugadores = null;
	public Font fuenteInterfaz = null;
	private int tiempoParaCompra = 0;
	private int RETARDOCOMPRA = 1000;
	/**
	 * Constructor. Construyte todos los objetos básicos para el desarrollo del juego.
	 * @param _m : Midlet de la aplicación
	 * @param _bc : Objeto encargado de enviar y recibir los mensajes
	 * @see BatallaEspacial
	 * @see Broadcaster
	 * @see ImageManager
	 * @see Enemy
	 * @see Mapa
	 */
	public Juego(BatallaEspacial _m, Broadcaster _bc){
		super(false);
		setFullScreenMode(true);
		this.midlet = _m;
		this.broadcaster = _bc;
		
		im = new ImageManager();
		lm = new LayerManager();
		lm2 = new LayerManager();
		
		fuenteInterfaz = Font.getFont (Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
		fuenteJugadores = Font.getFont (Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_SMALL);
		
		char[] l_n = {'s','i','n',' ','n','o','m','b','r','e'};
		for(int i = 0; i < 4; i++){
			Enemy e = new Enemy(this,i,l_n,
					xiniciales[i],yiniciales[i],0);
			naves.addElement(e);
		}
		mapa = new Mapa();
	}
	
	/**
	 * Función principal. Aquí se desarrolla todo el juego.
	 * Consiste en unas configuraciones al inicio de la partida y
	 * luego entra en un bucle infinito.
	 * Sensa el teclado, calcula colisiones, actualiza el estado general del juego,
	 * manda a entregar los mensajes, sincroniza los jugadores y llama al dibujado.
	 * @see Player
	 * @see DisparoJugador
	 * @see DisparoEnemigo
	 * @see Enemy
	 * @see Broadcaster
	 * @see Moneda
	 * @see Mapa
	 * @see Juego::input()
	 * @see Juego::render()
	 * @see Juego::generarMensaje()
	 * @see Juego::actualizarCristales()
	 * @see Juego::actualizarEstado()
	 * @see Juego::colisionar()
	 * @see Broadcaster::setInfoServer()
	 * @see Broadcaster::sendMessageFromClient()
	 * @see Broadcaster::generarMensaje()
	 * @see Broadcaster::enviarServer()
	 * @see Broadcaster::recalcularFrameRate()
	 * @see Player::moverDisparo()
	 */
	public void run(){
		//agrega las layer al LayerManager
		lm.append(jugador.disparo.s_disparo);
		if(esServidor == false){
			for(int i = 0; i < broadcaster.cantidadJugadores; i++){
				Enemy e = (Enemy) naves.elementAt(i);
				if(e.id != jugador.identificador){
					lm.append(e.s_enemy);
				}
			}
		}
		
		lm.append(jugador.s_player);
		lm.append(mapa.backgroundL2);
		lm.append(mapa.backgroundL1);
		
		//se agregan a lm2
		actualizarCristales();
		
		Graphics g = getGraphics();
		long start,end;
		
		//leer la info que me manda el server y crear 4 enemigos, uno voy a ser yo
		String mensaje = generarMensaje();
		if(esServidor == true){
			broadcaster.setInfoServer(mensaje);
		} else {
			broadcaster.sendMessageFromClient(mensaje);
		}
		
		//configuraciones iniciales
		if(esServidor == true){
			try { //espera a que llegue el mensaje de todos los players
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
			
			String estadoInicial = broadcaster.generarMensaje();
			
			int idx 	= Broadcaster.idx; 					//los primeros 3 caracteres son framerate
			int step 	= Broadcaster.dataStep; 					//cada mensaje sobre cada jugador ocupa esto
			int l_start	= 0;
			int l_end	= 0;
			
			for(int i = 0; i < broadcaster.cantidadJugadores & l_end < estadoInicial.length(); i++){ 		//recorro los 4 jugadores
				l_start		= idx+i*step; 		//indice donde empieza la cadena para el jugador i
				l_end 		= l_start + step; 		//indice donde termina la cadena para el jugador i
				String t 	= estadoInicial.substring(l_start, l_end); //discrimino el mensaje del jugador i
				int l_id 	= Integer.parseInt(t.substring(Broadcaster.dataPosId,Broadcaster.dataPosNombre).trim());	//obtengo el id
				String l_name = t.substring(Broadcaster.dataPosNombre,Broadcaster.dataPosEscudo);
				int l_x 		= Integer.parseInt(t.substring(Broadcaster.dataPosX,Broadcaster.dataPosY).trim());
				int l_y 		= Integer.parseInt(t.substring(Broadcaster.dataPosY,Broadcaster.dataPosDir).trim());
				int l_dir 		= Integer.parseInt(t.substring(Broadcaster.dataPosDir,Broadcaster.dataPosVM));
				char[] l_char =  new char[10];
				l_name.getChars(0, l_name.length(), l_char, 0);
				Enemy e = new Enemy(this, l_id, l_char, l_x, l_y, l_dir);
				naves.setElementAt(e,i);
				if(e.id != jugador.identificador) 
					lm.insert(e.s_enemy,0);
			}
			 //envia datos
			broadcaster.enviarServer();
		} else {
			//cliente se queda esperando un poco
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		
		int t_monedas = 0; //tiempo de generacion de monedas
		
		//game loop
		while(true){
			start = System.currentTimeMillis();
			
			//sensar teclado y mover jugador
			input(); 
			
			//mueve el disparo, if any
			jugador.moverDisparo();
			
			//comprobar colisiones
			colisionar();
			
			//enviar/almacenar estado propio
			mensaje = generarMensaje();
			if (esServidor == true) 	broadcaster.setInfoServer(mensaje);			//server se la guarda	
			else 						broadcaster.sendMessageFromClient(mensaje); //jugador envia su info
			
			//recibe el estado de todos
			actualizarEstado(broadcaster.estado);
			
			//dibujar
			render(g);
			
			//generar moneda
			if(esServidor == true){
				if(monedas.size() < Juego.MAX_MONEDAS & 
						t_monedas >= tiempoMonedas){
					while(monedaGenerada == false){
						Random r = new Random();
						int l_x = 0+r.nextInt(1000);
						int l_y = 0+r.nextInt(1000);
						if(generarMoneda(l_x,l_y,Moneda.NORMAL) == true){
							t_monedas = 0;
							monedaGenerada = true;
						}
					}
					monedaGenerada = false;
				} else {
					t_monedas = t_monedas + 1000/fpsDesdeServer;
				}
			}
			
			//sincronizacion
			end = System.currentTimeMillis();
			milisegundosEnDibujar = (int)(end - start);			
			
			if(esServidor == true){//recalcular fr
				fpsDesdeServer = Math.max(broadcaster.recalcularFrameRate(),1);
			}
			
			if(tiempoParaCompra > 0){
				tiempoParaCompra = tiempoParaCompra - milisegundosEnDibujar;
			}
				
			if(tiempoParaCompra < 0) tiempoParaCompra = 0;
			
			int fpsActual = (int) 1000/milisegundosEnDibujar;
			//si soy mas rapido que el limite, espero hasta el limite
			if(fpsActual > Juego.FPSLIMITE){ 
				try {
					Thread.sleep(Juego.FRAMESAT30 - milisegundosEnDibujar);
				} 
				catch (InterruptedException ie) {break;}
			}
			//si demoro menos de lo que me pidio el server
			else if (fpsActual > fpsDesdeServer) { 
				try {
					int milisegundosDelServer = 1000 / fpsDesdeServer;
					Thread.sleep(milisegundosDelServer - milisegundosEnDibujar);
				} 
				catch (InterruptedException ie) {break;}
			}
			//else Thread.yield();
			
			if(esServidor) broadcaster.enviarServer();
			//se duerme para evitar errores de censado de teclado
			try{
				Thread.sleep(10);
			}
			catch(InterruptedException ie){ 
			}
		}
	}
	
	/**
	 * Funcion encargada de dibujar en pantalla.
	 * Borra la pantalla (la pone todo en negro). Dibuja jugadores, mapa, monedas, disparos
	 * e interfaz.
	 * @param g : Objeto gráfico
	 */
	public void render(Graphics g){
		// limpia pantalla
		g.setColor(0x000000);
		int l_w = getWidth();
		int l_h = getHeight();
		g.fillRect(0, 0, l_w, l_h);
		
		//dibuja
		lm.setViewWindow(jugador.x - l_w/2 + 16,jugador.y - l_h/2 + 16,l_w,l_h);
		lm.paint(g,0,0);
		//Dibuja el nombre del jugador
		/*
		g.setFont(fuenteJugadores);
		g.setColor(0x000088);
		for(int i = 0; i < broadcaster.cantidadJugadores; i++){
			Enemy e = (Enemy) naves.elementAt(i);
			if(enPantalla(e.s_enemy.getX(), e.s_enemy.getY(),
					jugador.x-l_w/2, jugador.y-l_h/2, l_w, l_h))
				if(e.id != jugador.identificador)
					g.drawString (new String(e.nombre).trim(), e.x, e.y,Graphics.LEFT | Graphics.TOP);
		}*/

		//dibujo la interfaz solo si esta vivo
		if(jugador.estaVivo){
			
			lm2.setViewWindow(jugador.x-l_w/2,jugador.y-l_h/2,l_w,l_h);
			s_mascara.setPosition(jugador.x - l_w/2, jugador.y - l_h/2);
			lm2.paint(g,0,0);
			
			g.setFont(fuenteInterfaz);
			g.setColor(0x990000);
			g.drawString ("$: " + jugador.dinero, 0, 0, Graphics.LEFT | Graphics.TOP);
			g.drawString ("VEL: " + jugador.velocidad + " [9] ($" + jugador.costoVelocidad() + ")", l_w, l_h, Graphics.RIGHT | Graphics.BOTTOM);
			g.drawString ("POT: " + jugador.potencia + " [3] ($" + jugador.costoPotencia() + ")", l_w, l_h-40, Graphics.RIGHT | Graphics.BOTTOM);
			g.drawString ("Puntos: " + jugador.puntos, l_w, 0, Graphics.RIGHT | Graphics.TOP);
			g.drawString ("ESC: " + jugador.escudo + " [1] ($" + jugador.costoEscudo() + ")", 0, l_h-60, Graphics.LEFT | Graphics.BOTTOM);
			g.drawString ("CRI: " + jugador.cristales + " [7] ($" + jugador.costoCristales() + ")", 0, l_h-20, Graphics.LEFT | Graphics.BOTTOM);
			g.drawString ("Vidas: " + jugador.vidas, l_w/2, 20, Graphics.TOP | Graphics.HCENTER);
		}
		flushGraphics();
	}
	
	/**
	 * Ante un cambio del valor de jugador.cristales, calcula la máscara de nuevo y la cambia.
	 * @see Player
	 * @see ImageManager
	 */
	public void actualizarCristales(){
		im.generarMascara(0, 0, getWidth(), getHeight(), jugador.x, jugador.y, jugador.cristales);
		s_mascara = new Sprite(im.getImgMascara());
		lm2 = null;
		lm2 = new LayerManager();
		lm2.append(s_mascara);
	}
	
	/**
	 * Recibe un mensaje del servidor y actualiza el estado de todo el juego.
	 * Calcula las nuevas posiciones de los enemigos. Cambia las posiciones de los disparos.
	 * Agrega las monedas en el mapa.
	 * @param msg : Mensaje recibido desde el servidor que incluye la informacíon de todo el juego en ese frame.
	 * @see Broadcaster
	 * @see Player
	 * @see Enemy
	 * @see Moneda
	 * @see DisparoEnemigo
	 * @see DisparoJugador
	 * @see Juego::crearMonedasPorMuertos()
	 * @see Juego::borrarMonedasComoServidor()
	 */
	public void actualizarEstado(String msg){
		if (msg.length() <= 0) return;
		fpsDesdeServer = Integer.parseInt(msg.substring(0,Broadcaster.idx).trim()); //actualiza framerate que le paso el server
		
		for(int i = 0; i < naves.size(); i++){
			if(i != jugador.identificador)
				((Enemy) naves.elementAt(i)).updateMsg(msg); //actualiza enemigos
		}
		
		int idx 	= Broadcaster.idx; 					//los primeros 3 caracteres son framerate
		int step 	= Broadcaster.dataStep; 					//cada mensaje sobre cada jugador ocupa esto
		int start 	= 0;
		int end 	= 0;
		
		//busqueda de disparos
		for(int i = 0; i < disparos.size() ; i++){
			DisparoEnemigo de = (DisparoEnemigo) disparos.elementAt(i);
			lm.remove(de.s_disparoenemigo);
		}
		disparos.removeAllElements();
		
		for(int i = 0; i < broadcaster.cantidadJugadores & end < msg.length(); i++){ 		//recorro los 4 jugadores
			start		= idx + i*step; 		//indice donde empieza la cadena para el jugador i
			end 		= start + step; 		//indice donde termina la cadena para el jugador i
			if(end > msg.length()) return; //no sigo buscando porque me voy del rango
			String t 	= msg.substring(start, end); //discrimino el mensaje del jugador i
			int t_id 	= Integer.parseInt(t.substring(0,Broadcaster.dataPosNombre).trim());	//obtengo el id
			char t_asesino = t.charAt(t.length()-1); //es el ultimo caracter
			if(t_asesino != 'V'){ //si no esta vivo
				int t_as = Integer.parseInt("" + t_asesino);
				if(t_as == this.jugador.identificador) //si mate a alguien en el turno anterior
					this.jugador.incrementarPuntos();
			}
			if(esServidor)
				crearMonedasPorMuertos(t);
			if(t_id == this.jugador.identificador){ 			//si es mi id
				continue; //no busco mi disparo aqui
			}
			//se resta 1 porque el server le quita un caracter al enviarlo, y las posiciones
			// son del mensaje de los clientes
			int i_laser = Integer.parseInt(t.substring(Broadcaster.dataPosLaser-1,Broadcaster.dataPosLX-1).trim());
			int i_lx	= Integer.parseInt(t.substring(Broadcaster.dataPosLX-1,Broadcaster.dataPosLY-1).trim());
			int i_ly	= Integer.parseInt(t.substring(Broadcaster.dataPosLY-1, Broadcaster.dataPosLY-1+4).trim());
			DisparoEnemigo de = new DisparoEnemigo(this,t_id,i_lx,i_ly,i_laser);
			if(i_lx != DisparoEnemigo.VALORESPECIAL 
					& i_ly != DisparoEnemigo.VALORESPECIAL){
				disparos.addElement(de);//agrega un disparo
				lm.insert(de.s_disparoenemigo, 0);
			}
			
		}
		if(esServidor == true){
			borrarMonedasComoServidor();
		}
		else{
			for(int i = 0; i < monedas.size(); i++){
				Moneda m = (Moneda) monedas.elementAt(i);
				lm.remove(m.s_moneda);
			}
			monedas.removeAllElements();
			String l_monedas = msg.substring(end);
			
			int l_start = 0;
			int l_end = 0;
			while(l_end < l_monedas.length()){
				l_end = l_start + 14;
				String sm = l_monedas.substring(l_start,l_end);
				int l_idM = Integer.parseInt(sm.substring(0,4).trim());
				int l_monx = Integer.parseInt(sm.substring(4,8).trim());
				int l_mony = Integer.parseInt(sm.substring(8,12).trim());
				int l_monval = Integer.parseInt(sm.substring(12,14).trim());
				
				if(l_monval == Moneda.valorMonedaNormal) l_monval = Moneda.NORMAL;
				else l_monval = Moneda.ESPECIAL;
				
				Moneda m = new Moneda(this, l_idM,l_monx,l_mony,l_monval);
				monedas.addElement(m);
				lm.insert(m.s_moneda,0);
				
				l_start = l_end;
			}
		}
	}
	
	/**
	 * Siendo servidor, inspecciona los mensajes de los jugadores en busca de colisiones
	 * con monedas. Si colisionan, las borra.
	 * @see Broadcaster
	 * @see Moneda
	 */
	public void borrarMonedasComoServidor(){
		for (int i = 0; i < broadcaster.mensajeAJugadores.size(); i++) { 		//recorro los 4 jugadores
			String t 	= (String) broadcaster.mensajeAJugadores.elementAt(i); 
			int t_id	= Integer.parseInt(t.substring(Broadcaster.dataPosMoneda,Broadcaster.dataPosFrameRate).trim());
			if (t_id != -1) {
				for (int j = 0; j < monedas.size(); j++) {
					Moneda m = (Moneda) monedas.elementAt(j);
					
					if (m.id == t_id) {
						lm.remove(m.s_moneda);
						monedas.removeElement(m);
					}
				}
			}
		}
	}
	
	/**
	 * Crea monedas en los lugares donde hay naves destruidas
	 * @param msg : Mensaje que se recibió con la información de todos los jugadores
	 * @see Broadcaster
	 * @see Moneda
	 * @see Juego::generarMoneda
	 */
	public void crearMonedasPorMuertos(String msg){
		int step 	= Broadcaster.dataStep; 					//cada mensaje sobre cada jugador ocupa esto
		int start 	= 0;
		int end 	= 0;
		end 		= start + step; 		//indice donde termina la cadena para el jugador i
		String t 	= msg.substring(start, end); //discrimino el mensaje del jugador i
		int t_x 	= Integer.parseInt(t.substring(Broadcaster.dataPosX,Broadcaster.dataPosY).trim());
		int t_y 	= Integer.parseInt(t.substring(Broadcaster.dataPosY,Broadcaster.dataPosDir).trim());
		char t_asesino = t.charAt(t.length()-1);
		if(t_asesino != 'V'){ //si no esta vivo
			generarMoneda(t_x,t_y,Moneda.ESPECIAL);
		}
	}
	
	/**
	 * Censa el teclado en busca de teclas apretadas. De ser así, realiza las funciones 
	 * descriptas por cada tecla.
	 * @see Player
	 * @see Juego::nuevoDisparo()
	 * @see Player::mover()
	 * @see Player::aumentarEscudo()
	 * @see Player::aumentarCristales()
	 * @see Player::aumentarVelocidad()
	 * @see Player::aumentarPotencia
	 */
	public void input() {
		int ks = getKeyStates();
		if 		((ks & UP_PRESSED) != 0 & (ks & RIGHT_PRESSED) != 0){
			jugador.mover(Player.DIRNE);
		}
		else if ((ks & DOWN_PRESSED) != 0 & (ks & RIGHT_PRESSED) != 0){
			jugador.mover(Player.DIRSE);
		}
		else if ((ks & DOWN_PRESSED) != 0 & (ks & LEFT_PRESSED) != 0){
			jugador.mover(Player.DIRSO);
		}
		else if ((ks & UP_PRESSED) != 0 & (ks & LEFT_PRESSED) != 0){
			jugador.mover(Player.DIRNO);
		}
		else if ((ks & UP_PRESSED) != 0 ){
			jugador.mover(Player.DIRN);
		}
		else if ((ks & RIGHT_PRESSED) != 0){
			jugador.mover(Player.DIRE);
		}
		else if ((ks & DOWN_PRESSED) != 0){
			jugador.mover(Player.DIRS);
		}
		else if ((ks & LEFT_PRESSED) != 0){
			jugador.mover(Player.DIRO);
		}
		else if((ks & FIRE_PRESSED) != 0){
			nuevoDisparo();
		}
		if(tiempoParaCompra > 0) return;
		if((ks & GAME_A_PRESSED) != 0){
			if(jugador.aumentarEscudo())
				tiempoParaCompra = RETARDOCOMPRA;
		}
		else if((ks & GAME_B_PRESSED) != 0){
			if(jugador.aumentarPotencia())
				tiempoParaCompra = RETARDOCOMPRA;
		}
		else if((ks & GAME_C_PRESSED) != 0){
			if(jugador.aumentarCristales())
				tiempoParaCompra = RETARDOCOMPRA;
		}
		else if((ks & GAME_D_PRESSED) != 0){
			if(jugador.aumentarVelocidad())
				tiempoParaCompra = RETARDOCOMPRA;
		}

		getKeyStates();
	}
	
	
	protected void keyPressed(int keyCode) {
		switch(keyCode){
			case Canvas.KEY_POUND: {
				jugador.sacudir(Player.DERECHA);
				break; 
			}
			case Canvas.KEY_STAR: { 
				jugador.sacudir(Player.IZQUIERDA);
				break;
			}
		}
	}
	
	/**
	 * Prueba colisiones con monedas y disparos.
	 * @see DisparoEnemigo
	 * @see Moneda
	 * @see Player::colisionar()
	 * @see Player::incrementarMoneda()
	 */
	public void colisionar(){
		for(int i = 0; i < disparos.size(); i++){
			DisparoEnemigo de = (DisparoEnemigo) disparos.elementAt(i);
			if(jugador.colisionar(de)){
				jugador.recibirDisparo(de);
			}
		}
		
		for(int i = 0; i < monedas.size(); i++){
			Moneda m = (Moneda) monedas.elementAt(i);
			if(jugador.colisionar(m)){
				if(m.id == jugador.idMoneda) 
					continue; //ya la agarre, no vuelvo a colisionar
				if(m.valor == Moneda.ESPECIAL){
					jugador.incrementarMoneda(Moneda.valorMonedaEspecial);
				} else {
					jugador.incrementarMoneda(Moneda.valorMonedaNormal);
				}
				jugador.idMoneda = m.id;
				monedas.removeElement(m);
				lm.remove(m.s_moneda);
			}
		}
	}
	
	/**
	 * Inicia el juego
	 * @see BatallaEspacial
	 */
	public void start(){
		midlet.display.setCurrent(this);
		new Thread(this).start();
	}
	
	/**
	 * Asigna los datos del jugador, creando el objeto jugador
	 * @param _id : Id a asignarle al jugador
	 * @see Player
	 */
	public void setDatosJugador(int _id){
		jugador = new Player(this,_id, xiniciales[_id], yiniciales[_id], this.nombreJugador);
	}	
	
	/**
	 * Asigna los datos del jugador, creando el objeto jugador e imponiendole una X e Y iniciales.
	 * @param _id : Id del jugador
	 * @param _x : Posición X inicial
	 * @param _y : Posición Y inicial
	 * @see Player
	 */
	public void setDatosJugadorXY(int _id, int _x, int _y){
		jugador = new Player(this,_id, _x, _y, this.nombreJugador);
	}
	
	/**
	 * Llama a las funciones de creación de mapa. Si es servidor, llama a Mapa::crearComoServer().
	 * Si es cliente, llama a Mapa::crearComoCliente()
	 * @param _mapa : Mapa en formato String
	 * @see Mapa
	 * @see Mapa::crearComoServer()
	 * @see Mapa::crearComoCliente()
	 */
	public void crearMapa(String _mapa){
		if(esServidor == true)
			mapa.crearComoServer();
		else
			mapa.crearComoCliente(_mapa);
	}
	
	/**
	 * Cambia el estado del juego
	 * @param _state : Nuevo estado
	 */
	public void setGameState(int _state){
		this.gameState = _state;
	}
	
	/**
	 * Obtiene el estado del juego
	 * @return Estado actual del juego
	 */
	public int getGameState(){
		return this.gameState;
	}
	
	/**
	 * Cambia el nombre del jugador.
	 * @param _nombre : Nombre a asignar
	 * @see Player
	 */
	public void setNombreJugador(String _nombre){
		_nombre.getChars(0, _nombre.length(), this.jugador.nombre, 0);
	}
	
	/**
	 * Llama a Player::generarMensaje()
	 * @return Mensaje generado
	 */
	public String generarMensaje(){
		return jugador.generarMensaje();
	}
	
	/**
	 * Genera una moneda en la posición indicada y con el valor indicado.
	 * @param _x : Posición X
	 * @param _y : Posición Y
	 * @param _valor : Valor. 
	 * @return True si se pudo crear, False si no.
	 * @see Moneda
	 */
	public boolean generarMoneda(int _x, int _y, int _valor){ // pone una moneda en la posicion x,y
		Moneda m = new Moneda(this, idMonedaNueva,_x,_y,_valor);
		if(m.s_moneda.collidesWith(mapa.backgroundL2, true)) 
				return false;
		monedas.addElement(m);
		lm.insert(m.s_moneda,0);
		idMonedaNueva++;
		return true;
	}
	
	/**
	 * Llamada cuando el jugador presiona el botón de disparar.
	 * Llama a Player::disparar()
	 * @see Player::disparar()
	 */
	public void nuevoDisparo(){//el jugador QUIERE disparar
		jugador.disparar();
	}
}
