import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;




public class Broadcaster implements CommandListener{
	
	public static final int[] xiniciales = {10,200,10,50};
	public static final int[] yiniciales = {10,100,50,50};
	//Datos sobre cadenas del servidor
	public static final int dataPosFrameRate = 43;
	public static final int idx = 3; //indice donde empieza la data de jugadores
	public static final int dataStep = 38; //cuanto ocupa la data de cada jugador
	//datos sobre cadenas de jugadores
	//Especifican donde empiezan esas cadenas en la de un jugador
	public static final int dataPosId = 0;
	public static final int dataPosNombre = 4;
	public static final int dataPosEscudo = 14;
	public static final int dataPosX = 18;
	public static final int dataPosY = 22;
	public static final int dataPosDir = 26;
	public static final int dataPosVM = 27;
	public static final int dataPosLaser = 28;
	public static final int dataPosLX = 31;
	public static final int dataPosLY = 35;
	public static final int dataPosMoneda = 39;
	
	public static final int MIN_PLAYERS 	 = 1;
	public static final int MAX_PLAYERS 	 = 4;
	
	private LocalDevice local 		= null;
	private DiscoveryAgent agent 	= null;
	private L2CAPConnectionNotifier notifier;
	private L2CAPConnection server 	= null;
	private ServiceRecord service 	= null;
	private RemoteDevice rDevices[];
	private Vector clients = null;
	
	public static final String SERVICE_NAME = "BatallaEspacialME";
	public static final String SERVICE_UUID = "112233445566778899AABBCCDDEEFF";
	public static final int ATTRSET[] 		= {0x0100}; //returns service name attribute
	public static final UUID[] UUIDSET 		= {new UUID(SERVICE_UUID,false)};
	public static final String SERVICE_URL 	= "btl2cap://localhost:" + SERVICE_UUID + ";name=" + SERVICE_NAME;

	
	private int ultimoID = 0;
	public int cantidadJugadores = -1;
	private int jugadoresListos	= 0;
	
	private BEMIDlet midlet 	= null;

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
	
	public String estado = "";
	private Vector mensajeAJugadores = new Vector();
	
	public Broadcaster(BEMIDlet _m, Display _d){
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
	//llamada desde BEMIDlet para mostrar este form
	public void show(){
		display.setCurrent(form);
	}
	
	public void commandAction(Command cmd,Displayable disp) {
		 if(cmd == exit){ //si se quiere salir, libera todo
			 releaseResources();
			 midlet.notifyDestroyed();
			 return;
		 }
		 
		 else if(cmd == startServer ) { //inicia servidor
			 //borra comandos innecesarios
			 form.removeCommand(startServer);
			 form.removeCommand(connect);
			 //muestra un form para pedir la cantidad de jugadores
			 display.setCurrent(numPlayers);
		 }
		 
		 else if(cmd == startGame){ //el servidor selecciona empezar juego
			 if(midlet.juego.getGameState() == GameState.READYTOSTART) //se fija si esta listo para empezar
				 sendMessageFromServer(null); //mandara el mensaje: "start"
		 }
		 else if(cmd == addPlayers){ //toma la cantidad de jugadores e inicia el servidor
			 cantidadJugadores = Integer.parseInt(tf.getString());
			 if(	cantidadJugadores < Broadcaster.MIN_PLAYERS | 
					cantidadJugadores > Broadcaster.MAX_PLAYERS){ //la cantidad de jugadores es incorrecta
				 return;
			 }
			 
			 jugadoresListos = 1; //el servidor ya esta listo
			 //mensajeAJugadores.setSize(cantidadJugadores); //redimensiona y llena el vector de mensajes
			 for(int i = 0; i < cantidadJugadores; i++){
				 mensajeAJugadores.addElement(new String()); 
			 }

			 form.append("Juego para " + String.valueOf(cantidadJugadores) + " jugadores. Esperando jugadores.\n");
			 
			 midlet.juego.esServidor = true;
			 midlet.setState(GameState.WAITING_PLAYERS); //se queda esperando jugadores
			 
			 display.setCurrent(form);
			 new Thread() {
				 public void run(){
					 startServer();
				 }
			 }.start();
		 }
		 
		 else if(cmd == join ) { //si quieren unirse a alguna partida. deberia llamarse solo desde algun cliente
			 new Thread() {
		            public void run() {
		                if(server == null) sendMessageFromServer(null);
		                else	sendMessageFromClient(null);
		            }
		         }.start();
		 }
		 
		 else if(cmd == connect){ //si va a buscar servidores
			 form.removeCommand(connect);
			 form.removeCommand(startServer);
			 form.append("Buscando servidores...");
			 form.append(devices);
			 form.addCommand(selectServer);

			 //busca dispositivos bluetooth
			 DeviceDiscoverer discoverer = new DeviceDiscoverer(Broadcaster.this);
			 try {
				agent.startInquiry(DiscoveryAgent.GIAC,discoverer); //obtengo remote devices
			 } catch (BluetoothStateException e) {}
		 }
		 
		 else if(cmd == selectServer){ //si encontro un servidor
			 form.removeCommand(selectServer);
			 form.addCommand(join);
			 int index = devices.getSelectedIndex();
	         form.delete(form.size()-1);//deletes choiceGroup
	         
	         //busca el servicio de interes
	         ServiceDiscoverer sDiscoverer = new ServiceDiscoverer(Broadcaster.this); 
			 int attrset[] = {0x0100}; //returns service name attribute
			 UUID[] uuidset = {new UUID(SERVICE_UUID,false)};
			 try {
				agent.searchServices(attrset, uuidset, rDevices[index], sDiscoverer);
			 } catch (BluetoothStateException e) {}

		 }

	 } //fin commandAction
	 
	//abre el servidor y busca conexiones con clientes
	 private void startServer(){
		 midlet.juego.setDatosJugador(0, xiniciales[0], yiniciales[0]);//setea los datos del servidor
		 
	     clients = new Vector();
	     try {//busca clientes
			local.setDiscoverable(DiscoveryAgent.GIAC);
			notifier = (L2CAPConnectionNotifier) Connector.open(SERVICE_URL);
			ServiceRecord record = local.getRecord(notifier);
		    String conURL = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
		    new Thread() {
	        	 public void run() {
					startRecieverServer();
	        	 }
	         }.start();
			 
			 while(cantidadJugadores != jugadoresListos){	 
				 clients.addElement(notifier.acceptAndOpen());
		         try {
		              Thread.sleep(50);
		         } catch (InterruptedException ie) {}

			 }
		} catch (BluetoothStateException e1) {} 
		catch (IOException e1) {}

	 }//fin startServer
	 
	 //envia un mensaje desde el servidor
	public void sendMessageFromServer(String _response) {
		String message = "";
		int gs = midlet.getState();
		
		if(gs == GameState.READYTOSTART){ //envia la cadena start
			message = "start";
			midlet.juego.setGameState(GameState.RUNNING); //cambio a running, empieza el juego
			midlet.juego.start();
			
		}
		//not used
		else if(gs == GameState.RUNNING){
			for(int i = 0; i < mensajeAJugadores.size(); i++){ //evita mandar el mensaje
				if( ((String) mensajeAJugadores.elementAt(i)).length() == 0){
					return; 
				}
			}
			MessageFromServer mfs = new MessageFromServer(mensajeAJugadores, midlet.juego.monedas, midlet.juego.frameRate);
			this.estado = mfs.getMsg(); //tambien me guardo el mensaje para mi
       	 	message = mfs.getMsg();
       	 	
		}
		else if(gs == GameState.RESPONSE){//enviar una respuesta prefijada
			message = _response;
		}
		else{
			return;
		}
		Broadcast(-1, message); //envia el mensaje a todos
	 }//fin sendMessageFromServer

	public void enviarServer(){
		new Thread() {
       	 public void run() {
       		while(true){
	   			if(midlet.getState() == GameState.RUNNING){
	   				for(int i = 0; i < mensajeAJugadores.size(); i++){ //evita mandar el mensaje
	   					if( ((String) mensajeAJugadores.elementAt(i)).length() == 0){
	   						break; 
	   					}
	   				}
	   				estado = generarMensaje(); //tambien me guardo el mensaje para mi
	   				Broadcast(-1, estado); //envia el mensaje a todos
	   			}
	   			try {
					//Thread.sleep(midlet.juego.frameRate);
					Thread.sleep(midlet.juego.frameRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
       	 }
        }.start();
	}
	
	public String generarMensaje(){
		MessageFromServer mfs = new MessageFromServer(mensajeAJugadores, midlet.juego.monedas, midlet.juego.frameRate);
		return mfs.getMsg();
	}
	
	//envia un mensaje desde un cliente
	public void sendMessageFromClient(String _msg){
		try {
			String message = "";
			int gs = midlet.juego.getGameState();
			if(gs == GameState.DEFAULT){ //construir mensaje con mi informacion
				message = midlet.juego.nombreJugador;
				form.removeCommand(join); //ya me uni
				midlet.juego.setGameState(GameState.WAITING); //se queda en waiting por su id
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
			
			//envia el mensaje al servidor
			byte[] data = message.getBytes(); 
	        int transmitMTU = server.getTransmitMTU();
	        if(data.length <= transmitMTU & data.length > 0) {
	            server.send(data);
	        } else { }
		 } catch (IOException e) {}
	 }//fin sendMesssageFromClient()
	 
	//recibe mensajes de los clientes
	private void startRecieverServer(){
		while(true) {
			try {
				for(int i = 0; i < clients.size(); ++i){//por cada cliente
					//abre conexion
					L2CAPConnection connection = (L2CAPConnection) clients.elementAt(i);
	                if(connection.ready()) { //si esta lista
	                	//recibe mensaje
	                	int receiveMTU = connection.getReceiveMTU();
	                    byte[] data = new byte[receiveMTU];
	                    int length = connection.receive(data);
	                    String message = new String(data,0,length);
	                    
	                    int gs = midlet.getState();
	                    if(message.length() <= BEMIDlet.MAX_NAME_LENGTH 
	                    	& message.length() >= BEMIDlet.MIN_NAME_LENGTH){ //el mensaje contiene solo el nombre del jugador
	                    	//alguien se quiere unir a la partida
	                    	ultimoID++;
	                    	int i_id = ultimoID; //le genero un id
	                    	//le calculo sus datos iniciales
	                    	int i_x = 0;
	                    	int i_y = 0;
	                    	form.append(message + " se ha conectado. ID: " + String.valueOf(i_id));
	                    	i_x = xiniciales[i_id];
	                    	i_y = yiniciales[i_id];
	                    	
	                    	//le genero el mensaje de respuesta
	                    	String response = "";
	                    	String s_id = String.valueOf(i_id);
	                    	String s_x = String.valueOf(i_x);
	                    	String s_y = String.valueOf(i_y);
	                    	String s_cjugadores = String.valueOf(cantidadJugadores);
	                    	s_id = MessageFromPlayer.charFill(s_id, 4, ' '); //rellena el id con ceros
	                    	s_x = MessageFromPlayer.charFill(s_x, 4, ' ');
	                    	s_y = MessageFromPlayer.charFill(s_y, 4, ' ');
	                    	 
	                    	 //TODO FALTA TILEMAP
	                    	midlet.setState(GameState.RESPONSE);
	                    	response = s_cjugadores + s_id + s_x + s_y; //le devuelve el id, el mensaje, x e y
	                    	connection.send(response.getBytes()); //le responde con su informacion inicial
	                    	midlet.setState(GameState.WAITING_PLAYERS); //se queda esperando jugadores de nuevo
	                    	jugadoresListos++;
	                    	if(jugadoresListos == cantidadJugadores){
	                    		midlet.setState(GameState.READYTOSTART); //ahora si esta listo para jugar
	                    	 	form.addCommand(startGame); //shall we dance?
	                    	 }
	                     }
	                     if(message.length() != 0 & gs == GameState.RUNNING){ //recibe la informacion de algun jugador
	                    	 int i_id = Integer.parseInt(message.substring(Broadcaster.dataPosId,Broadcaster.dataPosNombre).trim()); //extrae el id
	                    	 int i_fr = Integer.parseInt(message.substring(Broadcaster.dataPosFrameRate).trim()); //recalcula framerate
	                    	 //TODO ver como hacer esto
	                    	 midlet.juego.frameRate = (int) ((midlet.juego.frameRate+i_fr) / 2);
	                    	 //midlet.juego.frameRate = Math.min(midlet.juego.frameRate,i_fr);
	                    	 mensajeAJugadores.setElementAt(message, i_id); //almaceno el mensaje
	                     }
	                     
	                }
	            }
	        } catch(IOException ioe4) {}
	         try {
	             Thread.sleep(10);
	         } catch (InterruptedException ie) {}
	     }
	 }
	 
	 private void startRecieverClient(){
		 while(true) {
			 try{
				if(server.ready()) { //si estoy conectado al servidor
					//recibe mensaje
					int receiveMTU = server.getReceiveMTU();
					byte[] data = new byte[receiveMTU];
					int length = server.receive(data);
					String message = new String(data,0,length);
					
					int gs = midlet.juego.getGameState();

					if(message.length() != 0 & message.equals("start")){
						midlet.juego.setGameState(GameState.RUNNING); //empieza el juego
						midlet.juego.start();
                    }
					
					if(message.length() != 0 & gs == GameState.WAITING){ //esta esperando su id y el mapa
						//TODO
						//parsear mapa
						//midlet.juego.crearMapa(message.substring(22));
						int i_cjugadores = Integer.parseInt(message.substring(0,1));
						int i_id = Integer.parseInt(message.substring(1,5).trim());
						int i_x = Integer.parseInt(message.substring(5,9).trim());
						int i_y = Integer.parseInt(message.substring(9,13).trim());
						midlet.juego.setDatosJugador(i_id,i_x,i_y);
						this.cantidadJugadores = i_cjugadores;
						form.append("ID recibida = " + String.valueOf(i_id));
						midlet.setState(GameState.READY);
					}
					else if(message.length() != 0 & gs == GameState.RUNNING){ //recibe la informacion de todos los jugadores por parte del servidor
						this.estado = message; //guardo lo recibido en una variable miembro, para luego leerla desde afuera
					}
				}  
			 } catch(IOException e) { }
		 } 
	 }
	 
	 public void setInfoServer(String message){
		 this.mensajeAJugadores.setElementAt(message, 0);//guarda mi info si es servidor
	 }
	 
	 public void printVector(Vector v){
		 for(int i = 0; i < v.size(); i++){
			 System.out.print((String) v.elementAt(i));
			 System.out.print("|||\n");
		 }
	 }
	 public void Broadcast(int sender, String message){
	    for(int i = 0; i < clients.size(); ++i){
	        try{
	            
	            L2CAPConnection connection = (L2CAPConnection) clients.elementAt(i);
	            byte[] data = message.getBytes();
	            int transmitMTU = connection.getTransmitMTU();
	            if(data.length <= transmitMTU) {
	                connection.send(data);
	            } else {
	              
	            }
	        } catch (IOException ioe5) {
	            clients.removeElementAt(i);
	            --i;
	        }
	    }
	 }

	 public void deviceInquiryFinished(RemoteDevice[] _rd){
		 rDevices = _rd;
		 String deviceNames[] = new String[rDevices.length];
	     for(int k = 0; k < rDevices.length; k++) {
	        try {
	            deviceNames[k] = rDevices[k].getFriendlyName(false);
	        } catch(IOException ioe) {}
	     }
	     for(int l = 0; l < deviceNames.length; l++) {
	        devices.append(deviceNames[l],null);
	     }
	 }
	 
	 public void serviceSearchFinished(ServiceRecord _s){
		 this.service = _s;
		 String uerel = "";
		 try {
			 uerel = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
		 } catch (IllegalArgumentException iae1) {}
		 try {
			 server = (L2CAPConnection) Connector.open(uerel);
		     new Thread() {
		        public void run() {
					startRecieverClient();
		        }
		     }.start();
		     
		 } catch(IOException ioe1) {}
	 }

	 //closes L2CAP connection
	 public void releaseResources() {
	     try {
	      	if(server != null)
	             server.close();
	         if(clients != null){
	             for(int i = 0; i < clients.size(); ++i){
	                 L2CAPConnection connection = (L2CAPConnection) clients.elementAt(i);
	                 connection.close();
	             }
	         }
	         if(notifier != null)
	             notifier.close();
	      } catch(IOException ioe6) {
	       
	      }
	 }
}

