import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Moneda {
	public int id;
	public int x;
	public int y;
	public int valor;
	public Sprite s_moneda;
	public static final int NORMAL = 0;
	public static final int ESPECIAL = 1;
	public static final int valorMonedaNormal = 5;
	public static final int valorMonedaEspecial = 20;
	public Moneda(int _id, int _x, int _y, int _valor){
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.valor = _valor;
		Image img_mo = null;
		try{
			if(this.valor == Moneda.NORMAL){
				img_mo = Image.createImage("/coin_yellow.png");
			} else {
				img_mo = Image.createImage("/coin_red.png");
			}
		}
		catch(IOException e){
			System.out.println("No se pudo leer la imagen del Enemigo");
		}
		this.s_moneda = new Sprite(img_mo);
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
		s_valor = MessageFromPlayer.charFill(s_valor, 4, ' ');
		s_x = MessageFromPlayer.charFill(s_x, 4, ' ');
		s_y = MessageFromPlayer.charFill(s_y, 4, ' ');
		
		String msg = s_id + s_valor + s_x + s_y;
		return msg;
	}
	
	
}
