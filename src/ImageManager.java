import java.io.IOException;
import javax.microedition.lcdui.Image;


public class ImageManager {
	private Image img_disparo = null;
	private Image img_monedaNormal = null;
	private Image img_monedaEspecial = null;
	private Image img_nave_enemy = null;
	private Image img_nave_player = null;
	private Image img_mascara = null;
	
	public ImageManager(){
		try{
			img_disparo = Image.createImage("/disparo2.png");
			img_monedaNormal = Image.createImage("/coin_yellowSINALFA.png");
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
	
	public Image crearCirculo(int x, int y, int r, int w, int h){
		int [] data = new int[w*h];
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				if(dist(x, y, i, j) <= r){
					data[i*w+j] = 1;
				} else {
					data[i*w+j] = 0;
				}
			}
		}
		return Image.createRGBImage(data, w, h, true);
	}
	
	public Image crearRectangulo(int x, int y, int w, int h){
		int [] data = new int[w*h];
		for(int i = 0; i < data.length; i++)
			data[i] = 255;
		/*for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				data[i*w+j] = 1;
			}
		}*/
		return Image.createRGBImage(data, w, h, true);
	}
	
	public static double dist(int x0, int y0, int x1, int y1){
		return Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
	}
	
	public void generarMascara(int x0, int y0, int w, int h, int xc, int yc, int r){
		Image rect = crearRectangulo(x0, y0, w, h);
		Image circle = crearCirculo(xc, yc, r, w, h);
		img_mascara = maskImage(rect, circle);
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