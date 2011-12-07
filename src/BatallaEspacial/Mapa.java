package BatallaEspacial;
import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * Clase Mapa. 
 * Se encarga de construir las 2 capas de mapa para el jugador y el servidor.
 *
 */
public class Mapa {
	/**
	 * Longitud del mapa, en cantidad de tiles uno tras del otro. 
	 */
	public static int MAPLENGTH = 400; 
	/**
	 * Tamaño en píxeles de cada tile.
	 */
	public static int TILESIZE = 50;
	/**
	 * Cantidad de tiles por lado del mapa.
	 */
	public static int MAPSIDE = 20;
	/**
	 * Mapa nivel 1.
	 */
	private int[] map1 = {	
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
		};
	/**
	 * Mapa nivel 2 con el que se colisiona.
	 */
	private int[] map2 = {	
			2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,
			7,0,0,0,0,0,0,5,6,5,0,0,0,0,0,0,0,0,0,9,
			7,0,0,0,15,0,0,5,0,0,0,0,8,8,0,15,0,0,0,9,
			7,0,0,0,20,0,0,0,0,0,0,0,0,0,0,20,10,0,0,9,
			7,0,0,5,5,0,0,0,0,5,0,0,0,0,0,0,0,0,0,9,
			7,0,0,0,5,0,0,0,0,0,0,0,8,8,0,0,8,8,0,9,
			7,0,0,0,0,0,0,0,11,18,0,0,8,8,0,0,0,0,0,9,
			7,0,5,0,0,0,11,19,17,19,0,0,0,0,0,0,0,0,0,9,
			7,5,6,0,0,0,19,11,19,17,0,0,0,0,0,11,18,0,10,9,
			7,5,1,0,0,0,19,19,0,0,0,0,8,0,0,16,17,0,1,9,
			7,0,0,0,0,0,16,17,0,0,0,8,6,0,0,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,0,0,9,
			7,0,0,10,0,0,0,0,8,8,8,0,0,0,0,0,6,10,0,9,
			7,0,10,10,0,0,0,8,8,0,0,0,0,10,0,0,10,10,0,9,
			7,0,10,10,0,0,0,0,0,0,0,0,6,10,0,0,0,10,0,9,
			7,0,0,0,10,0,0,0,0,10,10,0,0,0,0,0,0,0,0,9,
			7,0,0,0,15,0,0,0,0,0,0,0,0,0,0,15,0,0,0,9,
			7,0,0,0,20,0,0,0,11,19,19,18,0,0,0,20,0,0,0,9,
			7,0,0,0,0,0,0,0,16,19,19,17,0,0,0,0,0,0,0,9,
			12,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,14
		};
	
	private String mapa1EnString;
	private String mapa2EnString;
	private Image i_bg1;
	private Image i_bg2;
	public TiledLayer backgroundL1;
	public TiledLayer backgroundL2;
	/**
	 * Constructor.
	 * Carga las imágenes de cada tilemap.
	 */
	public Mapa(){
		try {
			i_bg1 = Image.createImage("/tileset1solo.png");
			i_bg2 = Image.createImage("/tileset2.png");
		} catch (IOException e) { 
			System.out.println("No se pudo leer el tilemap");
		};
	}
	/**
	 * Construye los dos mapas a partir de los tileset y genera las cadenas en string de cada uno.
	 */
	public void crearComoServer(){
		backgroundL1 = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg1,Mapa.TILESIZE,Mapa.TILESIZE);
		mapa1EnString = "";
		
		for (int i = 0; i < map1.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column) / Mapa.MAPSIDE;
			backgroundL1.setCell(column, row, map1[i]);
			mapa1EnString = mapa1EnString + MessageFromPlayer.charFill(""+map1[i], 2, ' ');
		}
		backgroundL1.setPosition(0,0);
		
		backgroundL2 = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg2,Mapa.TILESIZE,Mapa.TILESIZE);
		mapa2EnString = "";
		for (int i = 0; i < map2.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column)/Mapa.MAPSIDE;
			backgroundL2.setCell(column, row, map2[i]);
			mapa2EnString = mapa2EnString + MessageFromPlayer.charFill(""+map2[i], 2, ' ');
		}
		backgroundL2.setPosition(0,0);
	}
	
	/**
	 * Se le pasa un mapa nivel 2 en String, y crea un TiledLayer a partir de allí.
	 * @param _map : Mapa en formato String
	 */
	public void crearComoCliente(String _map){ //solo crea el mapa 2
		
		
		backgroundL1 = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg1,Mapa.TILESIZE,Mapa.TILESIZE);
		mapa1EnString = "";
		
		for (int i = 0; i < map1.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column)/Mapa.MAPSIDE;
			backgroundL1.setCell(column, row, map1[i]);
			mapa1EnString = mapa1EnString + MessageFromPlayer.charFill(""+map1[i], 2, ' ');
		}
		backgroundL1.setPosition(0,0);

		int start = 0;
		int end = 0;
		map2 = new int [Mapa.MAPLENGTH];
		int j = 0;
		
		while(end < _map.length() & j < map2.length){
			end = end + 2;
			if(j != map2.length-1)
				map2[j] = Integer.parseInt(_map.substring(start,end).trim());
			else
				map2[j] = Integer.parseInt(_map.substring(start).trim());
			j++;
			start = end;
		}
		
		backgroundL2 = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg2,Mapa.TILESIZE,Mapa.TILESIZE);
		
		for (int i = 0; i < map2.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column)/Mapa.MAPSIDE;
			backgroundL2.setCell(column, row, map2[i]);
		}
		backgroundL2.setPosition(0,0);
	}
	/**
	 * Retorna el mapa en formato String indicado por el parámetro
	 * @param i Si es 1, devuelve el mapa nivel 1. Si es 2, devuelve el mapa nivel 2. Si es otro número, devuelve una cadena vacía.
	 * @return La cadena correspondiente al argumento. 
	 */
	public String mapaEnString(int i){
		if(i == 1)
			return mapa1EnString;
		if(i == 2)
			return mapa2EnString;
		return "";
	}
}
