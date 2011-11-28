import java.util.Vector;

//se encarga de armar el string que el server debe enviar a los jugadores
public class MessageFromServer {
	private String msg;
	
	public MessageFromServer(Vector _playerString, Vector _monedas, int FM){
		msg = MessageFromPlayer.charFill(String.valueOf(FM),3,'0');
		//le saca los caracteres que no importan a los jugadores y los agrega a msg
		for(int i = 0; i < _playerString.size(); i++){
			String t = (String) _playerString.elementAt(i);
			if(t.length() == 0) continue;
			
			String t1 = t.substring(0,Broadcaster.dataPosVM);
			String t2 = t.substring(Broadcaster.dataPosVM+1,Broadcaster.dataPosMoneda);
			String t3 = t.substring(Broadcaster.dataPosVM,Broadcaster.dataPosVM+1);
			
			msg = msg + t1 + t2 + t3;
		}

		for(int i = 0; i < _monedas.size(); i++){
			Moneda m = (Moneda) _monedas.elementAt(i);
			String t4 = m.generarMensaje();
			msg = msg + t4;
		}
		//System.out.println(msg);
		
	}
	
	public String getMsg(){
		return this.msg;
	}
}
