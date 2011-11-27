//se encarga de armar un String con los atributos de un player
public class MessageFromPlayer {
	private String msg;
	MessageFromPlayer	(int _id, char[] _name,int _escudo, 
						int _x, int _y, int _dir, char _vm, 
						int _power,int _lx, int _ly, int _moneda, int _fm){
		String s_id 	= String.valueOf(_id);
		String s_name 	= String.valueOf(_name);
		String s_escudo;
		if(_escudo == -1){
			s_escudo = "-1"; 
		} else {
			s_escudo = String.valueOf(_escudo);
		}
		String s_x 		= String.valueOf(_x);
		String s_y 		= String.valueOf(_y);
		String s_dir 	= String.valueOf(_dir);
		String s_vm 	= String.valueOf(_vm);
		String s_power 	= String.valueOf(_power);
		if(_lx < 0) 	_lx = 0;
		if(_ly < 0) 	_ly = 0;
		String s_lx 	= String.valueOf(_lx);
		String s_ly 	= String.valueOf(_ly);
		String s_moneda = String.valueOf(_moneda);
		String s_fm 	= String.valueOf(_fm);
				
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


