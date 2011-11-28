//se encarga de armar un String con los atributos de un player
public class MessageFromPlayer {
	private String msg;
	MessageFromPlayer	(int _id, char[] _name,int _escudo, 
						int _x, int _y, int _dir, char _vm, 
						int _power,int _lx, int _ly, int _moneda, int _idAsesino, int _fm){
		String s_id 	= "" + (_id);
		String s_name 	= String.valueOf(_name);
		String s_escudo = "" + (_escudo);
		String s_x 		= "" + (_x);
		String s_y 		= "" + (_y);
		String s_dir 	= "" + (_dir);
		String s_vm;
		if(_vm == 'V'){ //si estoy vivo, mando V
			s_vm 	= "" + (_vm);
		} else { //si estoy muerto, mando el idasesino
			s_vm    = "" + (_idAsesino);
		}
		String s_power 	= "" + (_power);
		String s_lx 	= "" + (_lx);
		String s_ly 	= "" + (_ly);
		String s_moneda = "" + (_moneda);
		String s_fm 	= "" + (_fm);
				
		s_id 		= charFill(s_id,4,' ');
		s_name 		= charFill(s_name,10,' ');
		s_escudo 	= charFill(s_escudo,4,' ');
		s_x 		= charFill(s_x,4,' ');
		s_y 		= charFill(s_y,4,' ');
		
		s_power 	= charFill(s_power,3,' ');
		s_lx 		= charFill(s_lx,4,' ');
		s_ly 		= charFill(s_ly,4,' ');
		s_moneda 	= charFill(s_moneda,4,' ');
		s_fm 		= charFill(s_fm,3,' ');
		
		msg = s_id + s_name + s_escudo + s_x + s_y + s_dir + s_vm + s_power + s_lx + s_ly + s_moneda + s_fm;
		
	}
	
	//rellena la string con el caracter especificado, insertandolo al principio hasta que se alcance el tamaño deseado
	public static String charFill(String s, int n, char c){
		StringBuffer t = new StringBuffer(s);
		while(t.toString().length() < n){
			StringBuffer sb = new StringBuffer("");
			sb.append(c);
			sb.append(t);
			t = sb;
		}
		return t.toString();
	}
	public String getMsg(){
		return this.msg;
	}
}


