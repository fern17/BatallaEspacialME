package BatallaEspacial;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;


public class DisparoEnemigo {
	public int id;
	private int x;
	private int y;
	public Sprite s_disparoenemigo;
	public int potencia;
	public static final int VALORESPECIAL = 0;
	private Juego juego;
	
	public DisparoEnemigo(Juego _j, int _id, int _x, int _y, int _p){
		this.juego = _j;
		this.id = _id;
		this.x = _x;
		this.y = _y;
		this.potencia = _p;
		s_disparoenemigo = new Sprite(juego.im.getImgDisparo());
		s_disparoenemigo.setRefPixelPosition(16, 16);
		s_disparoenemigo.setPosition(this.x,this.y);
	}
	
	public void dibujar(Graphics g){
		if(this.potencia == DisparoEnemigo.VALORESPECIAL) return; //no dibujar
		s_disparoenemigo.setPosition(this.x,this.y);
		s_disparoenemigo.paint(g);
	}
}
