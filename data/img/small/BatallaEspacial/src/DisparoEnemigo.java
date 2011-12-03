import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class DisparoEnemigo {
	private int id;
	private int x;
	private int y;
	public Sprite s_disparoenemigo;
	public int potencia;
	public static final int VALORESPECIAL = 0;
	
	public DisparoEnemigo(int _id, int _x, int _y, int _p){
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.potencia = _p;
		try{
			Image img_pl = Image.createImage("/disparo2.png");
			s_disparoenemigo = new Sprite(img_pl);
		}
		catch(IOException e){
			System.out.println("No se pudo leer la imagen del disparo enemigo");
		}
	}
	
	public void dibujar(Graphics g){
		if(this.potencia == DisparoEnemigo.VALORESPECIAL) return; //no dibujar
		s_disparoenemigo.setPosition(this.x,this.y);
		s_disparoenemigo.paint(g);
	}
}
