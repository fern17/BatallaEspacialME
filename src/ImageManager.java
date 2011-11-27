import java.io.IOException;
import javax.microedition.lcdui.Image;


public class ImageManager {
	private Image img_disparo;
	private Image img_monedaNormal;
	private Image img_monedaEspecial;
	private Image img_nave_enemy;
	private Image img_nave_player;
	
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