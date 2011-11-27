import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;

//TODO agregar punto cada vez que mato uno
public class Juego extends GameCanvas implements Runnable {
	public int frameRate = 0;
	public int currentFrameRate = 0;
	
	private BEMIDlet midlet;
	public Broadcaster broadcaster;
	private int gameState = GameState.DEFAULT;
	
	public LayerManager lm;
	public ImageManager im;
	public Mapa mapa;
	public String nombreJugador = "";
	
	public Player jugador = null;
	private Vector naves = new Vector(); //guarda la info de las 4 naves
	public Vector monedas = new Vector();
	public Vector disparos = new Vector();
	public boolean esServidor = false;
	public int idMonedaNueva = 0; // identificador de las monedas
	//TODO pasar a 30000 y 25
	public int tiempoMonedas = 100; //cuanto tiempo tardan en generarse las monedas
	private static final int  MAX_MONEDAS = 0; 
	
	public Juego(BEMIDlet _m, Broadcaster _bc){
		super(true);
		this.midlet = _m;
		this.broadcaster = _bc;
		
		im = new ImageManager();
		char[] l_n = {'s','i','n',' ','n','o','m','b','r','e'};
		for(int i = 0; i < 4; i++){
			Enemy e = new Enemy(this,i,l_n,
					Broadcaster.xiniciales[i],Broadcaster.yiniciales[i],0);
			naves.addElement(e);
		}
		mapa = new Mapa(this);
		
	}
	
	public void run(){
		lm = new LayerManager();
		lm.append(jugador.disparo.s_disparo);
		if(esServidor == false){
			for(int i = 0; i < broadcaster.cantidadJugadores; i++){
				Enemy e = (Enemy) naves.elementAt(i);
				if(e.id != jugador.identificador) 
					lm.append(e.s_enemy);
			}
		}
		
		lm.append(jugador.s_player);
		lm.append(mapa.background);
		
		
		Graphics g = getGraphics();
		long start,end;
		
		//leer la info que me manda el server y crear 4 enemigos, uno voy a ser yo
		String mensaje = generarMensaje();
		if(esServidor == true){
			broadcaster.setInfoServer(mensaje);
		} else {
			broadcaster.sendMessageFromClient(mensaje);
		}
		
		if(esServidor == true){
			try { //espera a que llegue el mensaje de todos los players
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
			
			String estadoInicial = broadcaster.generarMensaje();
			
			int idx 	= 3; 					//los primeros 3 caracteres son framerate
			int step 	= 38; 					//cada mensaje sobre cada jugador ocupa esto
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
			
			broadcaster.enviarServer(); //empieza a enviar datos
		} else {
			
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
				if(monedas.size() < Juego.MAX_MONEDAS & t_monedas >= tiempoMonedas){
					Random r = new Random();
					//TODO agregar un 0
					int l_x = 0+r.nextInt(100);
					int l_y = 0+r.nextInt(100);
					generarMoneda(l_x,l_y,Moneda.NORMAL);
					t_monedas = 0;
				} else {
					t_monedas = t_monedas + frameRate;
				}
			}
			//sync
			end = System.currentTimeMillis();

			currentFrameRate = (int)(end - start);			
			
			if(esServidor == true){//recalcular fr
				frameRate = broadcaster.recalcularFrameRate();
			}
			
			if (currentFrameRate < frameRate) { //si demoro menos de lo que me pidio el server
				try {
					Thread.sleep(frameRate - currentFrameRate);
				} 
				catch (InterruptedException ie) {break;}
			}
			
		}
	}
	
	public void render(Graphics g){
		// clean screen
		g.setColor(0x000000);
		int l_w = getWidth();
		int l_h = getHeight();
		g.fillRect(0, 0, l_w, l_h);
		
		//draw
		lm.setViewWindow(jugador.x-l_w/2,jugador.y-l_h/2,l_w,l_h);
		lm.paint(g,0,0);
		
		flushGraphics();
	}
	
	public void actualizarEstado(String msg){
		if (msg.length() <= 0) return;
		frameRate = Integer.parseInt(msg.substring(0,3).trim()); //actualiza framerate que le paso el server
		
		for(int i = 0; i < naves.size(); i++){
			if(i != jugador.identificador)
				((Enemy) naves.elementAt(i)).updateMsg(msg); //actualiza enemigos
		}
		int idx 	= Broadcaster.idx; 					//los primeros 3 caracteres son framerate
		int step 	= Broadcaster.dataStep; 					//cada mensaje sobre cada jugador ocupa esto
		int start 	= 0;
		int end 	= 0;
		
		//busqueda de disparos
		disparos.removeAllElements();
		for(int i = 0; i < broadcaster.cantidadJugadores & end < msg.length(); i++){ 		//recorro los 4 jugadores
			start		= idx + i*step; 		//indice donde empieza la cadena para el jugador i
			end 		= start + step; 		//indice donde termina la cadena para el jugador i
			if(end > msg.length()) return; //no sigo buscando porque me voy del rango
			String t 	= msg.substring(start, end); //discrimino el mensaje del jugador i
			int t_id 	= Integer.parseInt(t.substring(0,4).trim());	//obtengo el id
			
			if(t_id == this.jugador.identificador){ 			//si es mi id
				continue; //no busco mi disparo aqui
			}
			int i_laser = Integer.parseInt(t.substring(Broadcaster.dataPosLaser,Broadcaster.dataPosLX).trim());
			int i_lx	= Integer.parseInt(t.substring(Broadcaster.dataPosLX,Broadcaster.dataPosLY).trim());
			int i_ly	= Integer.parseInt(t.substring(Broadcaster.dataPosLY).trim());
			DisparoEnemigo de = new DisparoEnemigo(this,t_id,i_lx,i_ly,i_laser);
			if(i_laser == DisparoEnemigo.VALORESPECIAL){
				de.s_disparoenemigo.setVisible(false);
			}
			disparos.addElement(de);//agrega un disparo
			lm.insert(de.s_disparoenemigo, 0);
		}
		
		if(esServidor == false){
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
				//TODO revisar
				lm.insert(m.s_moneda,0);
				l_start = l_end;
			}
		}
		 
	}
	
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
	}
	
	public void colisionar(){
		for(int i = 0; i < disparos.size(); i++){
			DisparoEnemigo de = (DisparoEnemigo) disparos.elementAt(i);
			if(jugador.colisionar(de)){
				jugador.recibirDisparo(de.potencia);
			}
		}
		
		for(int i = 0; i < monedas.size(); i++){
			Moneda m = (Moneda) monedas.elementAt(i);
			if(jugador.colisionar(m)){
				if(m.valor == Moneda.ESPECIAL){
					jugador.incrementarMoneda(Moneda.valorMonedaEspecial);
				} else {
					jugador.incrementarMoneda(Moneda.valorMonedaNormal);
				}
				monedas.removeElement(m);
				lm.remove(m.s_moneda);
			}
		}
		
		if(esServidor) colisionarServidor();
	}
	//tarea extra que hace un server
	
	public void colisionarServidor(){
		//se fija si algun enemigo colisiona con alguna moneda, entonces la borra
		for(int j = 0; j < naves.size(); j++){
			for(int i = 0; i < monedas.size(); i++){
				Moneda m = (Moneda) monedas.elementAt(i);
				Enemy e = (Enemy) naves.elementAt(j);
				if(e.colisionar(m)){
					monedas.removeElement(m);
				}
			}
		}
	}
	
	public void start(){
		midlet.display.setCurrent(this);
		new Thread(this).start();
	}
	public void setDatosJugador(int _id, int _x, int _y){
		jugador = new Player(this,_id,_x,_y, this.nombreJugador);
	}	
	
	
	public void crearMapa(String _mapa){
		if(esServidor == true)
			mapa.crearComoServer();
		else
			mapa.crearComoCliente(_mapa);
	}
	public void setGameState(int _state){
		this.gameState = _state;
	}
	public int getGameState(){
		return this.gameState;
	}
	public void setNombreJugador(String _nombre){
		_nombre.getChars(0, _nombre.length(), this.jugador.nombre, 0);
	}
	public String generarMensaje(){
		return jugador.generarMensaje();
	}
	public void generarMoneda(int _x, int _y, int _valor){ // pone una moneda en la posicion x,y
		Moneda m = new Moneda(this, idMonedaNueva,_x,_y,_valor);
		monedas.addElement(m);
		lm.insert(m.s_moneda,0);
		
		idMonedaNueva++;
	}
	public void borrarJugador(Player _player){ //saca un jugador del juego porque tiene 0 vidas
		
	}
	public void nuevoDisparo(){//el jugador QUIERE disparar
		jugador.disparar();
	}
}
