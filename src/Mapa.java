import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;


public class Mapa {
	public static int MAPLENGTH = 400;
	public static int TILESIZE = 50;
	public static int MAPSIDE = 20;
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
	
	private int[] map2 = {	
			2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,
			7,0,15,5,5,0,0,0,0,0,0,0,0,0,0,0,10,15,0,9,
			7,0,20,0,0,0,0,5,6,5,0,0,8,8,0,0,0,20,0,9,
			7,0,0,0,0,0,0,5,0,0,0,1,0,0,0,0,0,0,0,9,
			7,0,0,5,5,0,0,0,0,0,0,0,0,8,0,0,0,10,0,9,
			7,0,0,0,5,0,0,0,0,0,0,0,8,8,0,0,0,10,0,9,
			7,0,0,0,0,0,0,0,11,18,0,0,8,8,0,0,0,0,0,9,
			7,0,0,0,0,0,11,19,17,19,0,0,0,0,0,0,0,0,0,9,
			7,10,6,0,0,0,19,11,19,17,0,0,0,0,0,11,18,0,0,9,
			7,6,1,0,0,0,19,19,0,0,0,8,8,0,0,16,17,10,0,9,
			7,0,0,0,0,0,16,17,0,8,0,8,6,0,0,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,8,8,8,0,0,0,0,0,10,0,9,
			7,0,0,0,0,0,0,0,8,8,8,8,0,10,10,0,6,10,0,9,
			7,0,10,10,0,0,0,8,8,0,0,0,0,10,10,0,0,0,0,9,
			7,0,10,10,0,0,0,0,0,0,0,0,6,10,0,0,0,0,0,9,
			7,0,0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
			7,0,0,0,0,0,0,0,11,19,19,19,18,10,10,0,0,0,0,9,
			7,0,15,0,0,0,0,0,16,19,19,19,17,0,0,0,0,15,0,9,
			7,0,20,10,10,0,0,0,0,0,0,0,0,0,0,0,10,20,0,9,
			12,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,14
		};
	
	private String mapa1EnString;
	private String mapa2EnString;
	private Juego juego;
	private Image i_bg1;
	private Image i_bg2;
	public TiledLayer backgroundL1;
	public TiledLayer backgroundL2;
	public Mapa(Juego _j){
		this.juego = _j;
		try {
			i_bg1 = Image.createImage("/tileset1NOALFA.png");
			i_bg2 = Image.createImage("/tileset2NOALFA.png");
		} catch (IOException e) { 
			System.out.println("No se pudo leer el tilemap");
		};
	}
	
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
	
	public void crearComoCliente(String _map){ //solo crea el mapa 2
		
		//esta parte es igual al server, reveer una forma de organizarlo mejor :S
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
	
	public String mapaEnString(int i){
		if(i == 1)
			return mapa1EnString;
		if(i == 2)
			return mapa2EnString;
		return "";
	}
	
	public boolean colisionPlayer(int index){
		if (index >= map2.length) return false;
		else 					 return (map2[index] != 0);
	}
	
}
