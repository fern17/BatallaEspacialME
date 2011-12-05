package BatallaEspacial;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

/**
 * Clase BatallaEspacial. 
 * MIDlet principal que es ejecutado al inicio y almacena el juego.
 */

public class BatallaEspacial extends MIDlet implements CommandListener {
	public Juego juego = null;
	public Broadcaster 	broadcaster = null;
	public static int MIN_NAME_LENGTH = 2; //Longitud mínima del nombre
	public static int MAX_NAME_LENGTH = 10; // Longitud máxima del nombre
	public Display display = null;
	private Form form = null;
	private TextField textField = null;
	private Command exit = null;
	private Command enter = null;
	public ImageItem imagetitle = null;
	
	/**
	 * Constructor de BatallaEspacial.
	 * Llama a los constructores de Juego y Broadcaster. Tambien los conecta entre sí por variables miembro. 
	 * Construye la pantalla principal.
	 * @see Juego
	 * @see Broadcaster
	 */
	public BatallaEspacial() {
		super();
	    display = Display.getDisplay(this);
	    
	    broadcaster = new Broadcaster(this,display);
	    juego = new Juego(this, broadcaster);
	    broadcaster.juego = this.juego;
	    
	    exit = new Command("Salir", Command.EXIT, 1);
		enter = new Command("Entrar", Command.OK, 1);
		
	    form = new Form("Batalla Espacial ME");
		textField = new TextField("Nombre","",10,TextField.ANY);
		imagetitle = new ImageItem(null, juego.im.getImgTitulo(), ImageItem.LAYOUT_NEWLINE_BEFORE
		          | ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_AFTER,"title" );
		
		form.append(imagetitle);
		form.append(textField);
		
		form.setCommandListener(this);
		form.addCommand(exit);
		form.addCommand(enter);
		juego.addCommand(exit);
		
		juego.setCommandListener(this);
	}//fin BEMIDlet constructor
	
	/**
	 * Cambia el estado de Juego.
	 * @param _state : Estado a imponer.
	 * @see GameState
	 * @see Juego::setGameState()
	 */
	public void setState(int _state){
		this.juego.setGameState(_state);
	}
	
	/**
	 * Retorna el estado actual del juego
	 * @return Estado actual del juego
	 * @see Juego::getGameState()
	 */
	public int getState(){
		return this.juego.getGameState();
	}
	
	/**
	 * Muestra en pantalla el formulario inicial.
	 */
	protected void startApp() throws MIDletStateChangeException {
		display.setCurrent(form);
	}
	
	protected void pauseApp() { }

	protected void destroyApp(boolean uncond) {	}
	
	/**
	 * Maneja el comando exit de todos los Displayable.
	 * Comprueba que el nombre esté en la longitud especificada.
	 * Si está, llama a Broadcaster.show().
	 * @see Broadcaster::show()
	 * @see MessageFromPlayer::charFill()
	 */
	public void commandAction(Command cmd,Displayable disp) {
	    if(cmd == exit) {
	    	destroyApp(true);
	    	notifyDestroyed();
	    } 
	    else if(cmd == enter){
	    	String name = textField.getString();
	    	if(name.length() < MIN_NAME_LENGTH | name.length() > MAX_NAME_LENGTH) return;
	    	else{
	    		this.juego.nombreJugador = MessageFromPlayer.charFill(name, 10, ' '); //completo con espacios
	        	broadcaster.show();
	    	}
	    }
	}//fin commandAction

} // Fin class
