import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class ImageManager {
	private Image img_disparo = null;
	private Image img_monedaNormal = null;
	private Image img_monedaEspecial = null;
	private Image img_nave_enemy = null;
	private Image img_nave_player = null;
	private Image img_mascara = null;
	private Image img_titulo = null;
	
	public ImageManager(){
		try{
			img_titulo = Image.createImage("/title.png");
			img_disparo = Image.createImage("/disparo2.png");
			img_monedaNormal = Image.createImage("/coin_yellow.png");
			img_monedaEspecial = Image.createImage("/coin_red.png");
			img_nave_player = Image.createImage("/nave_player.png");
			img_nave_enemy = Image.createImage("/nave_enemy.png");
			
		}
		catch(IOException e){ }
	}
	
	/**
	* maskImage: crea una nueva imagen con una máscara aplicada a lo que se debe dibujar.
	*
	* @param source: imagen origen sobre la que se aplica la mascara
	* @param mask: mascara a aplicar.
	* @return
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
	
	public static double dist(int x0, int y0, int x1, int y1){
		return Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
	}
	
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
	public Image getImgTitulo(){
		return img_titulo;
	}
	
	public Image getImgMascara(){
		return img_mascara;
	}

	public Image getImgDisparo(){
		return img_disparo;
	}
	
	public Image getImgMonedaNormal(){
		return img_monedaNormal;
	}
	
	public Image getImgMonedaEspecial(){
		return img_monedaEspecial;
	}
	
	public Image getImgNavePlayer(){
		return img_nave_player;
	}
	
	public Image getImgNaveEnemy(){
		return img_nave_enemy;
	}
}