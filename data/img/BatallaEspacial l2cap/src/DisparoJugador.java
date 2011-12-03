import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class DisparoJugador {
	//posicion inicial
	public int xinicial;
	public int yinicial;
	//posicion
	public int x;
	public int y;
	//pendiente
	public int mx; 
	public int my;
	public int dir;
	//potencia del disparador
	public int potencia;
	//distancia que puede recorrer
	public int distancia;  
	//owner
	public Player jugador;
	private Sprite s_disparo;
	private boolean existe = false;
	private int step = 1;
	
	public DisparoJugador(Player _duenio){ //constructor por defecto
		this.jugador = _duenio;
		this.x = 0;
		this.y = 0;
		
		this.potencia = DisparoEnemigo.VALORESPECIAL;
		try{
			Image img_pl = Image.createImage("/disparo2.png");
			s_disparo = new Sprite(img_pl);
		}
		catch(IOException e){
			System.out.println("No se pudo leer la imagen del disparo del jugador");
		}
		s_disparo.setVisible(false);
	}
	
	
	public void set(int _x, int _y, int _dir, int _potencia, int _distancia){
		this.xinicial = _x+5;//correccion por sprite
		this.yinicial = _y+5;
		this.x = _x+5; 
		this.y = _y+5; 
		this.dir = _dir;
		this.distancia = _distancia;
		switch(dir){
			case Player.DIRN:{
				this.mx =  0;
				this.my = -1;
				break;
			}
			case Player.DIRNE:{
				this.mx =  1;
				this.my = -1;
				break;
			}
			case Player.DIRE:{
				this.mx =  1;
				this.my =  0;
				break;
			}
			case Player.DIRSE:{
				this.mx =  1;
				this.my =  1;
				break;
			}
			case Player.DIRS:{
				this.mx =  0;
				this.my =  1;
				break;
			}
			case Player.DIRSO:{
				this.mx = -1;
				this.my =  1;
				break;
			}
			case Player.DIRO:{
				this.mx = -1;
				this.my =  0;
				break;
			}
			case Player.DIRNO:{
				this.mx = -1;
				this.my = -1;
				break;
			}
		}
		s_disparo.setPosition(this.x,this.y);
		s_disparo.setVisible(true);
		this.potencia = _potencia;
		this.existe   = true;
		
	}
	
	public void mover(){
		if(existe == true){
			this.x = this.x + this.mx*step;
			this.y = this.y + this.my*step;
			double d = dist(this.xinicial,this.yinicial,this.x,this.y);
			if(d >= this.distancia){
				this.unSet();
			}
		}
	}
	
	public void unSet(){
		this.existe = false;
		this.x = -1;
		this.y = -1;
		this.potencia = -1;
		s_disparo.setVisible(false);
		jugador.setDisparar(true);
	}
	
	public static double dist(int x0, int y0, int x1, int y1){
		return Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
	}
	
	public void dibujar(Graphics g){
		if(existe == true){
			s_disparo.setPosition(this.x,this.y);
			s_disparo.paint(g);
		}	
	}
}
