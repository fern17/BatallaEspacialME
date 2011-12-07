package BatallaEspacial;

import javax.microedition.lcdui.game.Sprite;

/**
 * Clase Enemigo
 * Representa un enemigo en el juego. 
 */

public class Enemy {
	public int id;
	public char[] nombre;
	public int x; 
	public int y;
	public Sprite s_enemy;
	private int dir;
	private Juego juego;
	
	/**
	 * Constructor por defecto, solo le asigna una id.
	 * @param _j : Objeto Juego de la aplicacion
	 * @param _id : Id a asignarle
	 */
	public Enemy(Juego _j, int _id){
		this.juego = _j;
		this.id  = _id;
	}
	
	/**
	 * Constructor completo. Le asigna todos los atributos
	 * @param _j : Objeto Juego de la aplicacion
	 * @param _id : Id del enemigo
	 * @param _nombre : Nombre del enemigo
	 * @param _x : Posición X inicial
	 * @param _y : Posición Y inicial
	 * @param _dir : Dirección inicial
	 */
	public Enemy(Juego _j, int _id, char[] _nombre, int _x, int _y, int _dir){
		this.juego  = _j;
		this.id 	= _id;
		this.nombre = _nombre;
		this.x		= _x;
		this.y 		= _y;
		this.dir	= _dir;
		this.s_enemy = new Sprite(juego.im.getImgNaveEnemy(), 48, 48);
		s_enemy.setRefPixelPosition(24, 24);
		s_enemy.setPosition(this.x, this.y);
		
		cambiarFrame();
	}
	/**
	 * Recibe un String con el estado actual de todos y edita el enemigo con la nueva información
	 * @param msg : String recibida desde el servidor con toda la información de todos los jugadores
	 */
	
	public void updateMsg(String msg){		//recibe el string completo y extrae lo que le sirve
		int idx 	= Broadcaster.idx; 		//los primeros 3 caracteres son framerate
		int step 	= Broadcaster.dataStep;	//cada mensaje sobre cada jugador ocupa esto
		int start	= 0;
		int end		= 0;
		for(int i = 0; i < juego.broadcaster.cantidadJugadores & end < msg.length(); i++){ 		//recorro los 4 jugadores
			start		= idx+i*step; 		//indice donde empieza la cadena para el jugador i
			end 		= start + step; 		//indice donde termina la cadena para el jugador i
			if(end > msg.length()) return; //quedan caracteres pero son de monedas
			String t 	= msg.substring(start, end); //discrimino el mensaje del jugador i
			int t_id 	= Integer.parseInt(t.substring(Broadcaster.dataPosId,Broadcaster.dataPosNombre).trim());	//obtengo el id
			if(t_id == this.id){ 			//si es mi id
				int t_escudo = Integer.parseInt(t.substring(Broadcaster.dataPosEscudo, Broadcaster.dataPosX).trim());
				if(t_escudo == -1){
					juego.lm.remove(this.s_enemy);
					this.s_enemy.setVisible(false);
				}
					
				this.x 		= Integer.parseInt(t.substring(Broadcaster.dataPosX,Broadcaster.dataPosY).trim());
				this.y 		= Integer.parseInt(t.substring(Broadcaster.dataPosY,Broadcaster.dataPosDir).trim());				
				this.dir 	= Integer.parseInt(t.substring(Broadcaster.dataPosDir,Broadcaster.dataPosVM).trim());
				break;//ya me actualice, no necesito seguir buscando
			}
		}
		s_enemy.setPosition(this.x, this.y);
		cambiarFrame();
	}
	
	/**
	 * Cambia el frame actual del sprite del enemigo, conforme a su dirección actual.
	 */
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
