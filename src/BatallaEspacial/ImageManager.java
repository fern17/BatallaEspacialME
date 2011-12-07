package BatallaEspacial;
import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/*
 * Clase ImageManager
 * Se encarga de cargar en memoria las imágenes en uso y pasarselas al resto de las clases cuando las necesitan.
 * @see Player
 * @see Enemy
 * @see Moneda
 * @see DisparoEnemigo
 * @see DisparoJugador
 * @see BatallaEspacial
 */
public class ImageManager {
	private Image img_disparo = null;
	private Image img_monedaNormal = null;
	private Image img_monedaEspecial = null;
	private Image img_nave_enemy = null;
	private Image img_nave_player = null;
	private Image img_mascara = null;
	private Image img_titulo = null;
	/**
	 * Constructor. Carga todas las imagenes nememoria.
	 */
	public ImageManager(){
		try{
			img_titulo = Image.createImage("/title.png");
			img_disparo = Image.createImage("/disparo2.png");
			img_monedaNormal = Image.createImage("/coin_yellow.png");
			img_monedaEspecial = Image.createImage("/coin_red.png");
			img_nave_player = Image.createImage("/nave_player.png");
			img_nave_enemy = Image.createImage("/nave_enemy.png");
			
		}
		catch(IOException e){ 
			e.printStackTrace();
		}
	}
	
	/**
	* maskImage: crea una nueva imagen con una máscara aplicada a lo que se debe dibujar.
	*
	* @param source: imagen origen sobre la que se aplica la mascara
	* @param mask: mascara a aplicar.
	* @return La máscara hecha.
	*/
	public Image maskImage(Image source, Image mask) {
		int[] data, maskdata;
		data 		= new int[source.getHeight()*source.getWidth()];
		maskdata 	= new int[source.getHeight()*source.getWidth()];
		source.getRGB(data, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
		mask.getRGB(maskdata, 0, mask.getWidth(), 0, 0, mask.getWidth(), mask.getHeight());
		
		for(int i = 0; i < data.length; i++){
			if (maskdata[i] != -1)
				data[i] = 0;
		}
		return Image.createRGBImage(data, source.getWidth(), source.getHeight(), true);
	}
	/**
	 * Calcula la distancia entre dos puntos A y B
	 * @param x0 : X del punto A
	 * @param y0 : Y del punto A
	 * @param x1 : X del punto B 
	 * @param y1 : Y del punto B
	 * @return : Distancia de A a B
	 */
	public static double dist(int x0, int y0, int x1, int y1){
		return Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
	}
	
	/**
	 * Construye un rectángulo y un círculo y genera una máscara a partir de ellos. Se guarda en img_mascara.
	 * @param x0 : X del rectángulo
	 * @param y0 : Y del rectángulo
	 * @param w : Ancho del rectángulo
	 * @param h : Alto del rectángulo
	 * @param xc : Centro X del círculo
	 * @param yc : Centro Y del círculo
	 * @param r : Radio del círculo
	 * @see maskImage
	 */
	public void generarMascara(int x0, int y0, int w, int h, int xc, int yc, int r){
		Image rect = Image.createImage(w, h);
		Graphics g = rect.getGraphics();
		g.setColor(0x000000);
		g.fillRect(0, 0, w, h);

		Image circle = Image.createImage(w, h);
		g = circle.getGraphics();
		g.setColor(0);
		g.fillArc(w/2 - r, h/2 - r, 2*r, 2*r, 0, 360);
		img_mascara = maskImage(rect, circle);
	}
	/**
	 * 
	 * @return Imagen para la pantalla principal
	 */
	public Image getImgTitulo(){
		return img_titulo;
	}
	/**
	 * 
	 * @return Imagen de la máscara de un círculo sobre un rectángulo
	 */
	public Image getImgMascara(){
		return img_mascara;
	}
	/**
	 * 
	 * @return Imagen del disparo
	 */
	public Image getImgDisparo(){
		return img_disparo;
	}
	/**
	 * 
	 * @return Imagen de moneda normal
	 */
	public Image getImgMonedaNormal(){
		return img_monedaNormal;
	}
	
	/**
	 * 
	 * @return Imagen de moneda especial
	 */
	public Image getImgMonedaEspecial(){
		return img_monedaEspecial;
	}
	/**
	 * 
	 * @return Imagen de la nave del jugador
	 */
	public Image getImgNavePlayer(){
		return img_nave_player;
	}
	
	/**
	 * 
	 * @return Imagen de la nave de los enemigos.
	 */
	public Image getImgNaveEnemy(){
		return img_nave_enemy;
	}
}