package main;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class ChessPiece {
	private BufferedImage image;
	private int width, height;
	private int positionX = 0, positionY = 0;
	private int type;
	public ChessPiece(int type, int width, int height, int positionX, int positionY) {
		BufferedImage originalImage = null;
		this.width = width;
		this.height = height;
		this.positionX = positionX;
		this.positionY = positionY;
		this.type = type;
		int img_x = 0, img_y = 0, img_width = 0, img_height = 0;
		switch(type) {
		case 0:
			img_x = 1510;
			img_y = 435;
			img_width = 290;
			img_height = 290;
			break;
		case 1:
			img_x = 8;
			img_y = 430;
			img_width = 300;
			img_height = 300;
			break;
		case 2:
			img_x = 1210;
			img_y = 435;
			img_width = 300;
			img_height = 300;
			break;
		case 3:
			img_x = 310;
			img_y = 423;
			img_width = 310;
			img_height = 310;
			break;
		case 4:
			img_x = 615;
			img_y = 435;
			img_width = 295;
			img_height = 295;
			break;
		case 5:
			img_x = 910;
			img_y = 428;
			img_width = 300;
			img_height = 300;
			break;
		case 6:
			img_x = 1510;
			img_y = 65;
			img_width = 290;
			img_height = 290;
			break;
		case 7:
			img_x = 2;
			img_y = 50;
			img_width = 310;
			img_height = 310;
			break;
		case 8:
			img_x = 1210;
			img_y = 60;
			img_width = 300;
			img_height = 300;
			break;
		case 9:
			img_x = 302;
			img_y = 60;
			img_width = 310;
			img_height = 310;
			break;
		case 10:
			img_x = 608;
			img_y = 60;
			img_width = 310;
			img_height = 310;
			break;
		case 11:
			img_x = 900;
			img_y = 55;
			img_width = 320;
			img_height = 320;
			break;
			
			
			
		default:
			img_width = 350;
			img_height = 350;
		}
		
		String currentDir = System.getProperty("user.dir");
		try {
			originalImage = ImageIO.read(new File(currentDir + "\\src\\ChessArt.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		BufferedImage croppedImage = originalImage.getSubimage(img_x, img_y, img_width, img_height);
		image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(croppedImage, 0, 0, this.width, this.height, null);
		g2d.dispose();
	}
	
	public int getPosX() {
		return this.positionX;
	}
	
	public int getPosY() {
		return this.positionY;
	}
	
	public int getType() {
		return this.type;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setPosX(int posX) {
		this.positionX = posX;
	}
	
	public void setPosY(int posY) {
		this.positionY = posY;
	}
}
