import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Enemy {
	
	public int id;
	private char[] nombre;
	private int x; 
	private int y;
	public Sprite s_enemy;
	private int dir;
	private Juego juego;
	
	public Enemy(Juego _j, int _id){
		this.juego = _j;
		this.id  = _id;
		//if(id == juego.jugador.identificador) drawable = false;
	}
	
	public Enemy(Juego _j, int _id, char[] _nombre, int _x, int _y, int _dir){
		this.juego  = _j;
		this.id 	= _id;
		this.nombre = _nombre;
		this.x		= _x;
		this.y 		= _y;
		this.dir	= _dir;
		
		try{
			Image img_en = Image.createImage("/nave_enemy.png");
			this.s_enemy = new Sprite(img_en, 48, 48);
			s_enemy.setPosition(this.x, this.y);
		}
		catch(IOException e){
			System.out.println("No se pudo leer la imagen del Enemigo");
		}
		cambiarFrame();
	}
	
	public void updateMsg(String msg){		//recibe el string completo y extrae lo que le sirve
		int idx 	= Broadcaster.idx; 		//los primeros 3 caracteres son framerate
		int step 	= Broadcaster.dataStep;	//cada mensaje sobre cada jugador ocupa esto
		int start	= 0;
		int end		= 0;
		for(int i = 0; i < juego.broadcaster.cantidadJugadores & end < msg.length(); i++){ 		//recorro los 4 jugadores
			start		= idx+i*step; 		//indice donde empieza la cadena para el jugador i
			end 		= start + step; 		//indice donde termina la cadena para el jugador i
			//TODO revisar
			if(end > msg.length()) return; //quedan caracteres pero son de monedas
			String t 	= msg.substring(start, end); //discrimino el mensaje del jugador i
			int t_id 	= Integer.parseInt(t.substring(Broadcaster.dataPosId,Broadcaster.dataPosNombre).trim());	//obtengo el id
			if(t_id == this.id){ 			//si es mi id
				this.x 		= Integer.parseInt(t.substring(Broadcaster.dataPosX,Broadcaster.dataPosY).trim());
				this.y 		= Integer.parseInt(t.substring(Broadcaster.dataPosY,Broadcaster.dataPosDir).trim());				
				this.dir 	= Integer.parseInt(t.substring(Broadcaster.dataPosDir,Broadcaster.dataPosVM).trim());
				break;//ya me actualice, no necesito seguir buscando
			}
		}
		s_enemy.setPosition(this.x, this.y);
		cambiarFrame();
	}
	
	public void dibujar(Graphics g){
		if(this.id != juego.jugador.identificador) this.s_enemy.paint(g); 
	}
	
	public boolean colisionar(Moneda m){
		return this.s_enemy.collidesWith(m.s_moneda, true);
	}
	
	private void cambiarFrame(){
		switch(this.dir){
		case Player.DIRN:{
			this.s_enemy.setFrame(0);
			break;
		}
		case Player.DIRNE:{
			this.s_enemy.setFrame(1);
			break;
		}
		case Player.DIRE:{
			this.s_enemy.setFrame(2);
			break;
		}
		case Player.DIRSE:{
			this.s_enemy.setFrame(3);
			break;
		}
		case Player.DIRS:{
			this.s_enemy.setFrame(4);
			break;
		}
		case Player.DIRSO:{
			this.s_enemy.setFrame(5);
			break;
		}
		case Player.DIRO:{
			this.s_enemy.setFrame(6);
			break;
		}
		case Player.DIRNO:{
			this.s_enemy.setFrame(7);
			break;
		}
	}
	}
}
