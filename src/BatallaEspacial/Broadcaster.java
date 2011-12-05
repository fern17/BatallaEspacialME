package BatallaEspacial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * Clase Broadcaster.
 * Maneja toda la lógica de envío de mensajes, ser Servidor y esperar por jugadores,
 * ser jugador y buscar servidores.
 */
public class Broadcaster implements CommandListener{
	//Datos sobre cadenas del servidor
	public static final int idx = 3; //indice donde empieza la data de jugadores
	public static final int dataStep = 36; //cuanto ocupa la data de cada jugador
	
	//Datos sobre cadenas de jugadores
	//Especifican donde empiezan esas cadenas en la de un jugador
	public static final int dataPosId = 0;
	public static final int dataPosNombre = 1;
	public static final int dataPosEscudo = 11;
	public static final int dataPosX = 15;
	public static final int dataPosY = 19;
	public static final int dataPosDir = 23;
	public static final int dataPosVM = 24;
	public static final int dataPosLaser = 25;
	public static final int dataPosLX = 28;
	public static final int dataPosLY = 32;
	public static final int dataPosMoneda = 36;
	public static final int dataPosFrameRate = 40;
	
	//Objetos para la conexión Bluetooth con SPP.
	private LocalDevice local 		= null;
	private DiscoveryAgent agent 	= null;
	private StreamConnectionNotifier notifier;
	private StreamConnection server 	= null;
	private ServiceRecord service 	= null;
	private RemoteDevice rDevices[];
	private Vector clients = null;
	private Vector dataInput = new Vector();
	private Vector dataOutput = new Vector();
	
	public String estado = "";
	public Vector mensajeAJugadores = new Vector();
	
	//Información para crear un servidor o unirse a uno.
	public static final String SERVICE_NAME = "Jueguito";
	public static final String SERVICE_UUID = "112233445566778899AABBCCDDEEFF";
	//public static final int ATTRSET[] 		= {0x0100};
	public static final int ATTRSET[] 		= null;
	public static final UUID[] UUIDSET 		= {new UUID(SERVICE_UUID,false)};
	public static final String SERVICE_URL 	= "btspp://localhost:" + UUIDSET[0].toString() + ";name=" + SERVICE_NAME + ";authorize=false";

	//Información sobre el control de los jugadores en juego.
	private int ultimoID = 0;
	public int cantidadJugadores = -1;
	private int jugadoresListos	= 0;
	public static final int MIN_PLAYERS 	 = 1; //Minima cantidad de jugadores
	public static final int MAX_PLAYERS 	 = 4; //Maxima cantidad de jugadores
	
	private BatallaEspacial midlet 	= null;
	public Juego juego 			= null;
	private Form 	form;
	private Command exit;
	private Command connect;
	private Command join;
	private Command startServer;
	private Command selectServer;
	private Command addPlayers;
	private Command startGame;
	private Display display;
	private ChoiceGroup devices = null;
	
	private Form numPlayers;
	private TextField tf;
	
	/**
	 * Constructor. Toma un Midlet y un Display. Construye todos los Form.
	 * Obtiene el DiscoveryAgent
	 * @param _m : Midlet
	 * @param _d : Display
	 * @see BatallaEspacial
	 */
	public Broadcaster(BatallaEspacial _m, Display _d){
		midlet 		= _m;
		display 	= _d;
		
		form 		= new Form("Lobby");
		exit 		= new Command("Salir",Command.EXIT,1);
		connect 	= new Command("Buscar juegos",Command.OK,2);
		selectServer= new Command("Conectarse",Command.OK,2);
		join 		= new Command("Unirse",Command.OK,3);
		startServer = new Command("Ser Servidor",Command.OK,1);
		addPlayers 	= new Command("Abrir Servidor",Command.OK,1);
		startGame 	= new Command("Iniciar Juego", Command.OK,1);
		devices 	= new ChoiceGroup(null,Choice.EXCLUSIVE);
		
		form.addCommand(exit);
		form.addCommand(connect);
		form.addCommand(startServer);
		form.setCommandListener(this);
		
		numPlayers 	= new Form("Cantidad de Jugadores");
		tf 			= new TextField("Cantidad de jugadores","",1,TextField.NUMERIC);
		numPlayers.addCommand(addPlayers);
		numPlayers.append(tf);
		numPlayers.setCommandListener(this);
		
		try { //lee dispositivo local
			local = LocalDevice.getLocalDevice();//obtengo dispositivo local
			agent = local.getDiscoveryAgent(); //obtengo el discovery agent
		} catch (BluetoothStateException e) { }

	}//fin constructor
	
	/**
	 * Muestra este form.
	 * @see BatallaEspacial
	 */
	public void show(){
		display.setCurrent(form);
	}
	
	/**
	 * Se encarga de toda la lógica de los botones. Llama a las 
	 * funciones correspondientes a cada evento.
	 * @see releaseResources()
	 * @see sendMessageFromServer()
	 * @see sendMessageFromClient()
	 * @see startServer()
	 * @see BatallaEspacial::setState()
	 * @see Juego
	 * @see DeviceDiscoverer
	 * @see ServiceDiscoverer
	 */
	public void commandAction(Command cmd,Displayable disp) {
		//si se quiere salir, libera todo
		 if(cmd == exit){ 
			 releaseResources();
			 midlet.notifyDestroyed();
			 return;
		 }
		//inicia servidor
		 else if(cmd == startServer ) { 
			 //borra comandos innecesarios
			 form.removeCommand(startServer);
			 form.removeCommand(connect);
			 //muestra un form para pedir la cantidad de jugadores
			 display.setCurrent(numPlayers);
		 }
		//el servidor selecciona empezar juego
		 else if(cmd == startGame){ 
			 if(midlet.juego.getGameState() == GameState.READYTOSTART) //se fija si esta listo para empezar
				 sendMessageFromServer(null); //mandara el mensaje: "start"
		 }
		//toma la cantidad de jugadores e inicia el servidor
		 else if(cmd == addPlayers){ 
			 cantidadJugadores = Integer.parseInt(tf.getString());
			 if(	cantidadJugadores < Broadcaster.MIN_PLAYERS | 
					cantidadJugadores > Broadcaster.MAX_PLAYERS){ //la cantidad de jugadores es incorrecta
				 return;
			 }
			 
			 jugadoresListos = 1; //el servidor ya esta listo
			 //Agrega mensajes vacíos a mensajeAJugadores
			 for(int i = 0; i < cantidadJugadores; i++){
				 mensajeAJugadores.addElement(new String()); 
			 }
			 
			 //Muestra un mensaje en el form
			 form.append("Juego para " + String.valueOf(cantidadJugadores) + " jugadores. Esperando jugadores.\n");
			 
			 //Configuraciones de servidor en Juego
			 midlet.juego.esServidor = true;
			 midlet.juego.crearMapa(null);
			 
			 midlet.setState(GameState.WAITING_PLAYERS); //se queda esperando jugadores
			 
			 display.setCurrent(form);
			 new Thread() {
				 public void run(){
					 startServer();
				 }
			 }.start();
		 }
		//si quieren unirse a alguna partida. deberia llamarse solo desde algun cliente
		 else if(cmd == join ) { 
			 new Thread() {
		            public void run() {
		                if(server == null) sendMessageFromServer(null); //si no tiene ningun servidor asociado, es servidor
		                else	sendMessageFromClient(null); //cliente
		            }
		         }.start();
		 }
		//si un cliente va a buscar servidores
		 else if(cmd == connect){ 
			 form.removeCommand(connect);
			 form.removeCommand(startServer);
			 form.append("Servidores Encontrados");
			 form.append(devices);
			 form.addCommand(selectServer);

			 //busca dispositivos bluetooth
			 DeviceDiscoverer discoverer = new DeviceDiscoverer(Broadcaster.this);
			 try {
				agent.startInquiry(DiscoveryAgent.GIAC,discoverer); //obtengo remote devices
			 } catch (BluetoothStateException e) {}
		 }
		//si encontro un servidor
		 else if(cmd == selectServer){ 
			 form.removeCommand(selectServer);
			 form.addCommand(join);
			 int index = devices.getSelectedIndex();
	         form.delete(form.size()-1);//deletes choiceGroup
	         
	         //busca el servicio de interes
	         ServiceDiscoverer sDiscoverer = new ServiceDiscoverer(Broadcaster.this); 
			 
			 try {
				agent.searchServices(ATTRSET, UUIDSET, rDevices[index], sDiscoverer);
			 } catch (BluetoothStateException e) {
			 }
		 }
	 } //fin commandAction
	 
	/**
	 * Inicia un servidor y busca conexiones con clientes
	 * @see startRecieverServer()
	 * @see Juego::setDatosJugador()
	 * @see BatallaEspacial::setState()
	 */
	 private void startServer(){
		 //setea los datos del servidor
		 midlet.juego.setDatosJugador(0);
		 
	     clients = new Vector();
	     //busca clientes
	     try {
	    	 //Se pone descubierto
	    	 local.setDiscoverable(DiscoveryAgent.GIAC);
			 //Empieza a recibir mensajes de conexión
			 startRecieverServer();
			 
			 //Espera a que estén todos conectados
			 while(cantidadJugadores != jugadoresListos){
				 //Se conecta un cliente, lo agrega a la lista de clientes
				 notifier = (StreamConnectionNotifier) Connector.open(SERVICE_URL);
				 StreamConnection sc = notifier.acceptAndOpen();
				 dataOutput.addElement(sc.openDataOutputStream());
				 dataInput.addElement(sc.openDataInputStream());
				 clients.addElement(sc);
				 notifier.close();
		         try {
		              Thread.sleep(50);
		         } catch (InterruptedException ie) {}
			 }
			 
			 //Ahora está listo para jugar, deja de buscar
         	 midlet.setState(GameState.READYTOSTART); //ahora si esta listo para jugar
         	 form.addCommand(startGame); //shall we dance?
		} catch (BluetoothStateException e1) {} 
		catch (IOException e1) {}
	 }//fin startServer
	 
	/**
	 * Envia un mensaje desde el servidor
	 * @param _response : Parámetro opcional sólo utilizado si GameState es RESPONSE
	 * @see MessageFromServer
	 * @see Broadcaster::Broadcast()
	 * @see BatallaEspacial::setState()
	 * @see BatallaEspacial::getState()
	 * @see Juego::start()
	 */
	 public void sendMessageFromServer(String _response) {
		 String message = "";
		 int gs = midlet.getState();
		
		 //envia la cadena start
		 if(gs == GameState.READYTOSTART){ 
			 message = "start";
			//cambio a running, empieza el juego
			 midlet.setState(GameState.RUNNING); 
			 midlet.juego.start();
		 }
		 //Sin uso
		 else if(gs == GameState.RUNNING){
			 for(int i = 0; i < mensajeAJugadores.size(); i++){ //evita mandar el mensaje
				 if( ((String) mensajeAJugadores.elementAt(i)).length() == 0){
					 return; 
				 }
			 }
			 MessageFromServer mfs = new MessageFromServer(mensajeAJugadores, midlet.juego.monedas, (int) 1000/midlet.juego.fpsDesdeServer);
			 this.estado = mfs.getMsg(); //tambien me guardo el mensaje para mi
			 message = mfs.getMsg();
		 }
		//enviar una respuesta prefijada pasada por parametro
		 else if(gs == GameState.RESPONSE){
			 message = _response;
		 }
		 //no envia nada
		 else {
			 return;
		 }
		 //envia el mensaje a todos
		 Broadcast(-1, message); 
	 }//fin sendMessageFromServer

    /**
     * El servidor envia un mensaje a todos.
     * @see Broadcast()
     * @see generarMensaje()
     */
	public void enviarServer(){
		if(midlet.getState() == GameState.RUNNING){
			for(int i = 0; i < mensajeAJugadores.size(); i++){ //evita mandar el mensaje
				if( ((String) mensajeAJugadores.elementAt(i)).length() == 0){
					break; 
				}
			}
			estado = generarMensaje(); //tambien me guardo el mensaje para mi
			Broadcast(-1, estado); //envia el mensaje a todos
		}
	}
	
	/**
	 * Genera un mensaje como servidor
	 * @return : Mensaje generado
	 * @see MessageFromServer
	 */
	public String generarMensaje(){
		MessageFromServer mfs = new MessageFromServer(mensajeAJugadores, midlet.juego.monedas, (int) 1000/midlet.juego.milisegundosEnDibujar);
		 return mfs.getMsg();
	}
	
	/**
	 * Recalcula el FrameRate, tomando el más lento de todos 
	 * @return : FrameRate a imponer a los jugadores
	 */
	public int recalcularFrameRate(){
		int i_fps = (int) 1000/midlet.juego.milisegundosEnDibujar;
		for(int i = 0; i < mensajeAJugadores.size(); i++){
			String maj = (String) mensajeAJugadores.elementAt(i);
			i_fps = Math.max(i_fps, Integer.parseInt(maj.substring(Broadcaster.dataPosFrameRate).trim())); //recalcula framerate
		}
		return i_fps;
	}
	
	/**
	 * Envia un mensaje desde un cliente
	 * @param _msg : Mensaje a enviar, en el caso de GameState sea RUNNING 
	 * @see GameState
	 * @see BatallaEspacial::getState()
	 */
	public void sendMessageFromClient(String _msg){
		try {
			String message = "";
			int gs = midlet.getState();
			if(gs == GameState.DEFAULT){ //construir mensaje con mi informacion
				message = midlet.juego.nombreJugador;
				form.removeCommand(join); //ya me uni
				midlet.setState(GameState.WAITING); //se queda en waiting por su id
			}
			else if(gs == GameState.WAITING){//se queda esperando su id y posiciones iniciales
				message = ""; //no envia nada
			}
			else if(gs == GameState.READY){ //esta esperando que le pasen start
				message = "";
			}
			else if(gs == GameState.RUNNING){//envia su informacion
				message = _msg;
			}
			//dataOutput
			DataOutputStream out = (DataOutputStream) dataOutput.elementAt(0); //tiene uno solo
			
			//Escribimos datos en el stream
			out.writeUTF(message);
			out.flush();

		 } catch (IOException e) {}
	 }//fin sendMesssageFromClient()
	 
	/**
	 * Recibe mensaje de los clientes
	 * @see BatallaEspacial::getState()
	 * @see BatallaEspacial::setState()
	 * @see Juego
	 * @see MessageFromPlayer
	 */
	private void startRecieverServer(){
		new Thread() {
	       	 public void run() {
				while(true) {
						for(int i = 0; i < clients.size(); i++){//por cada cliente
							try {
			                	//recibe mensaje
			    				DataInputStream in = (DataInputStream) dataInput.elementAt(i);
			    				int gs = midlet.getState();
			    				
		                    	if(in.available() != 0) {
		                    		if( gs == GameState.WAITING_PLAYERS){
				    					String message = in.readUTF();
				    					if(message.length() <= BatallaEspacial.MAX_NAME_LENGTH 
						                & message.length() >= BatallaEspacial.MIN_NAME_LENGTH){ //el mensaje contiene solo el nombre del jugador
					                    	//alguien se quiere unir a la partida
					                    	ultimoID++;
					                    	int i_id = ultimoID; //le genero un id
					                    	//le calculo sus datos iniciales
					                    	int i_x = 0;
					                    	int i_y = 0;
					                    	form.append(message + " se ha conectado. ID: " + String.valueOf(i_id));
					                    	i_x = Juego.xiniciales[i_id];
					                    	i_y = Juego.yiniciales[i_id];
					                    	
					                    	//le genero el mensaje de respuesta
					                    	String response = "";
					                    	String s_id = String.valueOf(i_id);
					                    	String s_x = String.valueOf(i_x);
					                    	String s_y = String.valueOf(i_y);
					                    	String s_cjugadores = String.valueOf(cantidadJugadores);
					                    	s_x = MessageFromPlayer.charFill(s_x, 4, ' ');
					                    	s_y = MessageFromPlayer.charFill(s_y, 4, ' ');
					                    	String s_mapa =  midlet.juego.mapa.mapaEnString(2);
					                    	
					                    	midlet.setState(GameState.RESPONSE);
					                    	//le devuelve el id, el mensaje, x e y, y el mapa y la cantidad de jugadores
					                    	response = s_cjugadores + s_id + s_x + s_y + s_mapa; //CODING HORROR! 
					                    	
					                    	DataOutputStream out = (DataOutputStream) dataOutput.elementAt(i);
					            			//Escribimos datos en el stream
					            			out.writeUTF(response);
					            			out.flush();
					                    	midlet.setState(GameState.WAITING_PLAYERS); //se queda esperando jugadores de nuevo
					                    	jugadoresListos++;
					                    	if(jugadoresListos == cantidadJugadores){
					                    		midlet.setState(GameState.READYTOSTART); //ahora si esta listo para jugar
					                    	 	form.addCommand(startGame); //shall we dance?
					                    	 }
				    					}
			                    	}
			                    }
			                    if(gs == GameState.RUNNING){ //recibe la informacion de algun jugador
			                    	String message = in.readUTF();
			                    	 int i_id = Integer.parseInt(message.substring(Broadcaster.dataPosId,Broadcaster.dataPosNombre).trim()); //extrae el id
			                    	 mensajeAJugadores.setElementAt(message, i_id); //almaceno el mensaje
			                     }
							} catch(IOException ioe4) {}
						}
			        
			         try {
			             Thread.sleep(10);
			         } catch (InterruptedException ie) {}
			         Thread.yield();
			     }
	       	 }
		}.start();
	 }
	 
	/**
	 * Recibe la informacíon del servidor
	 * @see Juego
	 * @see BatallaEspacial::getState()
	 * @see BatallaEspacial::setState()
	 * @see Juego::crearMapa()
	 * @see Juego::setDatosJugador()
	 */
	 private void startRecieverClient(){
		 new Thread() {
			 public void run() {
				 while(true) {
					 try{
						//recibe mensaje
						DataInputStream in = (DataInputStream) dataInput.elementAt(0); 
						
						//Leemos el mensaje y lo mostramos por pantalla
						String message = in.readUTF();
			
						int gs = midlet.getState();
			
						if(message.length() != 0 & message.equals("start")){
							midlet.setState(GameState.RUNNING); //empieza el juego
							midlet.juego.start();
			            }
						
						if(message.length() != 0 & gs == GameState.WAITING){ //esta esperando su id y el mapa
							int i_cjugadores = Integer.parseInt(message.substring(0,1));
							int i_id = Integer.parseInt(message.substring(1,2).trim());
							int i_x = Integer.parseInt(message.substring(2,6).trim());
							int i_y = Integer.parseInt(message.substring(6,10).trim());
							
							cantidadJugadores = i_cjugadores;
							midlet.juego.setDatosJugadorXY(i_id,i_x,i_y);
							
							midlet.juego.crearMapa(message.substring(10));
							
							form.append("ID recibida = " + String.valueOf(i_id));
							midlet.setState(GameState.READY);
						}
						else if(message.length() != 0 & gs == GameState.RUNNING){ //recibe la informacion de todos los jugadores por parte del servidor
							estado = message; //guardo lo recibido en una variable miembro, para luego leerla desde afuera
						}
						Thread.sleep(10);
					 	}
					 
					 catch(IOException e) { }
					 catch (InterruptedException e) {}
					 Thread.yield();
				 }
			 }
	     }.start();
	 }
	 
	 /**
	  * Actualiza la informacíon del servidor, guardándola en mensajeAJugadores 
	  * @param message : Información a almacenar
	  */
	 public void setInfoServer(String message){
		 this.mensajeAJugadores.setElementAt(message, 0);//guarda mi info si es servidor
	 }
	 
	 /**
	  * Envia un mensaje a todos los jugadores
	  * @param sender : Id del remitente
	  * @param message : Mensaje a enviar
	  */
	 public void Broadcast(int sender, String message){
	    for(int i = 0; i < clients.size(); ++i){
	        try{
	        	DataOutputStream out = (DataOutputStream) dataOutput.elementAt(i);
    			
    			//Escribimos datos en el stream
    			out.writeUTF(message);
    			out.flush();
	        } catch (IOException ioe5) {
	            clients.removeElementAt(i);
	            --i;
	        }
	    }
	 }
	 
	 /**
	  * Llamado cuando se terminó de buscar dispositivos
	  * @param _rd : Lista de RemoteDevice encontrados
	  * @see DeviceDiscoverer
	  */
	 public void deviceInquiryFinished(RemoteDevice[] _rd){
		 rDevices = _rd;
		 String deviceNames[] = new String[rDevices.length];
	     for(int k = 0; k < rDevices.length; k++) {
	        try {
	            deviceNames[k] = rDevices[k].getFriendlyName(false) + "(" + rDevices[k].getBluetoothAddress() + ")";
	        } catch(IOException ioe) {}
	     }
	     for(int l = 0; l < deviceNames.length; l++) {
	        devices.append(deviceNames[l],null);
	     }
	 }
	 
	 /**
	  * Llamado cuando se terminó de buscar servicios
	  * @param _s : Servicio del juego
	  * @see ServiceDiscoverer
	  * @see startRecieverClient()
	  */
	 public void serviceSearchFinished(ServiceRecord _s){
		 this.service = _s;
		 String uerel = "";
		 try {
			 uerel = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
		 } catch (IllegalArgumentException iae1) {
			 iae1.printStackTrace();
		 }
		 try {
			 server = (StreamConnection) Connector.open(uerel);
			 dataOutput.addElement(server.openDataOutputStream()); //se guarda input y output del server
			 dataInput.addElement(server.openDataInputStream());
			 startRecieverClient();
		 } catch(IOException ioe1) {}
	 }

	 /**
	  * Cierra conexión SPP
	  */
	 public void releaseResources() {
	     try {
	      	if(server != null)
	             server.close();
	         if(clients != null){
	             for(int i = 0; i < clients.size(); ++i){
	            	 StreamConnection connection = (StreamConnection) clients.elementAt(i);
	                 connection.close();
	             }
	         }
	         if(notifier != null)
	             notifier.close();
	      } catch(IOException ioe6) {
	       
	      }
	 }
}

