import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public class BEMIDlet extends MIDlet implements CommandListener {
	public Juego juego;
	public Broadcaster broadcaster = null;
	public Compra compra = null;
	public static int MIN_NAME_LENGTH = 2;
	public static int MAX_NAME_LENGTH = 10;
	public Display display = null;
	private Form form;
	private TextField textField;
	private Command exit = null;
	private Command enter = null;
	private Command shop = null;
	//TODO
	//agregar mapa
	public BEMIDlet() {
		super();
	    display = Display.getDisplay(this);
	    broadcaster = new Broadcaster(this,display);
	    juego = new Juego(this, broadcaster);
	    compra = new Compra(display,juego);
	    
	    exit = new Command("Salir", Command.EXIT, 1);
		enter = new Command("Entrar", Command.OK, 1);
		shop = new Command("Shop", Command.OK, 1);
		
	    form = new Form("Batalla Espacial ME");
		textField = new TextField("Nombre","",10,TextField.ANY);
		form.append(textField);
		
		form.setCommandListener(this);
		form.addCommand(exit);
		form.addCommand(enter);
		
		juego.addCommand(exit);
		juego.addCommand(shop);
		
		juego.setCommandListener(this);
		
	}//fin BEMIDlet constructor
	
	public void setState(int _state){
		this.juego.setGameState(_state);
	}//fin setState
	
	public int getState(){
		return this.juego.getGameState();
	}
	protected void startApp() throws MIDletStateChangeException {
		display.setCurrent(form);
	}//fin startApp

	protected void pauseApp() {
   
	}//fin pauseApp

	protected void destroyApp(boolean uncond) {

	}//fin destroyApp

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
	    else if(cmd == shop){
	    	compra.show();
	    }
	}//fin commandAction

} // Fin class
