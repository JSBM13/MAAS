package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import main.Carte.orientations;
import main.Carte.typesRoute;

@SuppressWarnings("serial")
public class Map extends JPanel {
	
	final static Parametres defaultParams = new Parametres("400,300,400,300",
			"400,300,400,300,400",
			"PSPSPS",
			"SPSSP",
			"Marche,1,1,30,20,5,0;Vélo,6,7,40,30,15,0;Autobus,20,14,15,20,10,300;Métro,25,25,45,0,0,180;Voiture (régulier),20,14,60,40,10,0;Voiture (heure de pointe),8,10,90,60,45,0",
			"(0,1);(0,4);(5,4)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (2,0) (0,0) (0,2) (0,4);(0,1) (2,1) (2,3) (3,4) (4,2) (3,1) (1,0) (0,1);(5,0) (5,2) (3,3) (1,3) (1,0) (3,0) (5,0)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
			);
	
	int mapHeight;
	int mapWidth;
	int verticalMargin;
	int horizontalMargin;
	int scale;
	
	int[] positionRoutesHorizontales;
	int[] positionRoutesVerticales;
	
	Carte carte;
	
	public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g; //Cast Graphics g to a Graphics2D, Updated version more methods
        
        
        g2D.setPaint(Color.white); //Color
        g2D.fillRect(0, 0, getWidth(), getHeight()); //Create rectangle with fill
        paintCarte(g2D);
	}

	private void paintCarte(Graphics2D g2D) {
		
		mapHeight = sumArray(carte.getSegmentsHorizontaux());
		mapWidth = sumArray(carte.getSegmentsVerticaux());
		
		positionRoutesHorizontales = sumFromBefore(carte.getSegmentsVerticaux());
		positionRoutesVerticales = sumFromBefore(carte.getSegmentsHorizontaux());
		
		setScale();
		
		for (int i = 0; i < carte.getNbRoutesVerticales(); i++) {
			paintRoute(g2D, positionRoutesVerticales[i], 0, positionRoutesVerticales[i], mapWidth, carte.getTypeRoute(orientations.verticale, i) == typesRoute.principale);
		}
		
		for (int i = 0; i < carte.getNbRoutesHorizontales(); i++) {
			paintRoute(g2D, 0, positionRoutesHorizontales[i], mapHeight, positionRoutesHorizontales[i], carte.getTypeRoute(orientations.horizontale, i) == typesRoute.principale);
		}
	}
	
	private int sumArray(int[] array) {
		int x = 0;
		for (int i = 0; i < array.length; i++) {
			x += array[i];
		}
		return x;
	}
	
	private int[] sumFromBefore(int[] array) {
		int[] a = new int[array.length + 1];
		int x = 0;
		a[0] = 0;
		for (int i = 0; i < array.length; i++) {
			x += array[i];
			a[i + 1] = x;
		}
		return a;
	}
	
	private void paintRoute(Graphics2D g2D, int x1, int y1, int x2, int y2, boolean principal) {
		if (principal) {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.black, 6);
		} else {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.black, 4);
		}
		
	}
	
	private void paintLine(Graphics2D g2D, PointPixel p1, PointPixel p2, Color color, int size) {
		g2D.setPaint(color);
		g2D.setStroke(new BasicStroke(size));
		g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	private PointPixel scale(int x, int y) {
		return new PointPixel((x / scale) + horizontalMargin, (y / scale) + verticalMargin);
	}

	public void setScale() {
		int width = this.getWidth() - 40;
		int height = this.getHeight() - 40;
		this.scale = (int) Math.max(Math.ceil(1.0 * mapHeight / height), Math.ceil(1.0 * mapWidth / width));
		this.verticalMargin = (height - (mapHeight / scale)) / 2;
		this.horizontalMargin = (width - (mapWidth / scale)) / 2;
	}

	public Map() {
		super();
		try {
			carte = new Carte(defaultParams.distanceRoutesVerticales, defaultParams.distanceRoutesHorizontales, defaultParams.typesRoutesVerticales, defaultParams.typesRoutesHorizontales);
		} catch (Exception e) {
			
		}
	}
	
	public int getMapHeight() {
		return mapHeight;
		
		
	}

	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getScale() {
		return scale;
	}
	
	
	private class PointPixel {
		int x;
		int y;
		PointPixel(int x, int y ) {
			this.x = x;
			this.y = y;
		}
	}
	
	
	
	
	
	
	
	
}
