import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;


public class Mapa {
	public static int MAPLENGTH = 400;
	public static int TILESIZE = 50;
	public static int MAPSIDE = 20;
	private int[] map = {	
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
	private String mapaEnString;
	private Juego juego;
	private Image i_bg;
	public TiledLayer background;
	public Mapa(Juego _j){
		this.juego = _j;
		try {
			i_bg = Image.createImage("/tilemapsinalfa.png");
		} catch (IOException e) { 
			System.out.println("No se pudo leer el tilemap");
		};
		if(juego.esServidor){
			/*int [] map2 = 
			{	
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
			map = map2;
			*/
		}
		
	}
	
	public void crearComoServer(){
		background = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg,Mapa.TILESIZE,Mapa.TILESIZE);
		mapaEnString = "";
		
		for (int i = 0; i < map.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column)/Mapa.MAPSIDE;
			background.setCell(column, row, map[i]);
			mapaEnString = mapaEnString + MessageFromPlayer.charFill(""+map[i], 2, ' ');
		}
		background.setPosition(0,0);
		
	}
	
	public void crearComoCliente(String _map){
		int start = 0;
		int end = 0;
		map = new int [Mapa.MAPLENGTH];
		int j = 0;
		
		while(end < _map.length() & j < map.length){
			end = end + 2;
			if(j != map.length-1)
				map[j] = Integer.parseInt(_map.substring(start,end).trim());
			else
				map[j] = Integer.parseInt(_map.substring(start).trim());
			
			j++;
			start = end;
		}
		
		background = new TiledLayer(Mapa.MAPSIDE,Mapa.MAPSIDE,i_bg,Mapa.TILESIZE,Mapa.TILESIZE);
		
		for (int i = 0; i < map.length; i++) {
			int column = i % Mapa.MAPSIDE;
			int row = (i-column)/Mapa.MAPSIDE;
			background.setCell(column, row, map[i]);
		}
		background.setPosition(0,0);
	}
	
	public String mapaEnString(){
		return mapaEnString;
	}
	
}
