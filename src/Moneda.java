import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;


public class Moneda {
	private Juego juego = null;
	public int id;
	public int x;
	public int y;
	public int valor;
	public Sprite s_moneda;
	public static final int NORMAL = 0;
	public static final int ESPECIAL = 1;
	public static final int valorMonedaNormal = 5;
	public static final int valorMonedaEspecial = 20;
	public Moneda(Juego _j, int _id, int _x, int _y, int _valor){
		this.juego = _j;
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.valor = _valor;
		if(this.valor == Moneda.NORMAL){
			this.s_moneda = new Sprite(juego.im.getImgMonedaNormal());
		} else {
			this.s_moneda = new Sprite(juego.im.getImgMonedaEspecial());
		}
		
		
		
		s_moneda.setPosition(this.x, this.y);
	}
	
	public void dibujar(Graphics g){
		s_moneda.paint(g);
	}
	
	public String generarMensaje(){
		String s_id = String.valueOf(this.id);
		String s_valor;
		if(this.valor == Moneda.NORMAL){
			s_valor = String.valueOf(Moneda.valorMonedaNormal);
		} else {
			s_valor = String.valueOf(Moneda.valorMonedaEspecial);
		}
		String s_x = String.valueOf(this.x);
		String s_y = String.valueOf(this.y);
		
		s_id = MessageFromPlayer.charFill(s_id, 4, ' ');
		s_x = MessageFromPlayer.charFill(s_x, 4, ' ');
		s_y = MessageFromPlayer.charFill(s_y, 4, ' ');
		s_valor = MessageFromPlayer.charFill(s_valor, 2, ' ');
		
		String msg = s_id + s_x + s_y + s_valor;
		return msg;
	}
	
	
}
