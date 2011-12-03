import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;


public class Compra implements CommandListener{
	private Form form;
	private Command buy;
	private Command back;
	private Display display;
	private Juego juego;
	private List list;
	
	public Compra(Display _d, Juego _j){
		this.display = _d;
		this.juego = _j;
		
		
		buy = new Command("Comprar",Command.OK,1);
		back = new Command("Volver",Command.BACK,1);
		
	}
	
	public void show(){
		list = new List("Dinero: " + juego.jugador.dinero,List.IMPLICIT);
		list.append("Velocidad: " + juego.jugador.costoVelocidad(),null);
		list.append("Escudo: " + juego.jugador.costoEscudo(),null);
		list.append("Potencia: " + juego.jugador.costoPotencia(),null);
		list.append("Cristales: " + juego.jugador.costoCristales(),null);
		list.addCommand(buy);
		list.addCommand(back);
		list.setCommandListener(this);
		display.setCurrent(list);
	}
	
	public void commandAction(Command c, Displayable d) {
		if(c == back){
			display.setCurrent(juego);
		} else if (c == buy){
			int idx = list.getSelectedIndex();
			switch(idx){
				case 0: {
					if(juego.jugador.aumentarVelocidad() == true){
						list.setTitle("Dinero: " + juego.jugador.dinero);
						list.delete(0);
						list.insert(0, "Velocidad: " + juego.jugador.costoVelocidad(), null);
					}
					break;
				}
				case 1: {
					if(juego.jugador.aumentarEscudo() == true){
						list.setTitle("Dinero: " + juego.jugador.dinero);
						list.delete(1);
						list.insert(1, "Escudo: " + juego.jugador.costoEscudo(), null);
					}
					break;
				}
				case 2: {
					if(juego.jugador.aumentarPotencia() == true){
						list.setTitle("Dinero: " + juego.jugador.dinero);
						list.delete(2);
						list.insert(2, "Potencia: " + juego.jugador.costoPotencia(), null);
					}
					break;
				}
				case 3: {
					if(juego.jugador.aumentarCristales() == true){
						list.setTitle("Dinero: " + juego.jugador.dinero);
						list.delete(3);
						list.insert(3, "Cristales: " + juego.jugador.costoCristales(), null);
					}
					break;
				}
				default: {
					break;
				}
			}
		}
	}
	
}
