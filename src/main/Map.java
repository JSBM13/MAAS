package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


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
	double scale;
	
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
		
		mapWidth = sumArray(carte.getSegmentsHorizontaux());
		mapHeight = sumArray(carte.getSegmentsVerticaux());
		
		positionRoutesHorizontales = sumFromBefore(carte.getSegmentsVerticaux());
		positionRoutesVerticales = sumFromBefore(carte.getSegmentsHorizontaux());
		
		setScale();
		paintRoutes(g2D);
		
	}
	
	private void paintRoutes(Graphics2D g2D) {
		for (int i = 0; i < carte.getNbRoutesVerticales(); i++) {
			if (carte.getTypeRoute(orientations.verticale, i) == typesRoute.secondaire) {
				paintRoute(g2D, positionRoutesVerticales[i], 0, positionRoutesVerticales[i], mapHeight, carte.getTypeRoute(orientations.verticale, i) == typesRoute.principale);
			}
			
		}
		
		for (int i = 0; i < carte.getNbRoutesHorizontales(); i++) {
			if (carte.getTypeRoute(orientations.horizontale, i) == typesRoute.secondaire) {
				paintRoute(g2D, 0, positionRoutesHorizontales[i], mapWidth, positionRoutesHorizontales[i], carte.getTypeRoute(orientations.horizontale, i) == typesRoute.principale);
			}
			
		}
		for (int i = 0; i < carte.getNbRoutesVerticales(); i++) {
			if (carte.getTypeRoute(orientations.verticale, i) == typesRoute.principale) {
				paintRoute(g2D, positionRoutesVerticales[i], 0, positionRoutesVerticales[i], mapHeight, carte.getTypeRoute(orientations.verticale, i) == typesRoute.principale);
			}
			
		}
		
		for (int i = 0; i < carte.getNbRoutesHorizontales(); i++) {
			if (carte.getTypeRoute(orientations.horizontale, i) == typesRoute.principale) {
				paintRoute(g2D, 0, positionRoutesHorizontales[i], mapWidth, positionRoutesHorizontales[i], carte.getTypeRoute(orientations.horizontale, i) == typesRoute.principale);
			}
			
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
			paintLine(g2D, scale(x1 - 1,y1 - 1), scale(x2 - 1,y2 - 1), Color.decode("#202020"), 8);
		} else {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.decode("#A0A0A0"), 6);
		}
		
	}
	
	private void paintLine(Graphics2D g2D, PointPixel p1, PointPixel p2, Color color, int size) {
		g2D.setPaint(color);
		g2D.setStroke(new BasicStroke(size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	private PointPixel scale(int x, int y) {
		return new PointPixel((x * scale) + verticalMargin, getHeight() - ((y * scale) + horizontalMargin));
	}

	public void setScale() {
		int width = this.getWidth()- 16;
		int height = this.getHeight() - 16;
		this.scale = Math.min(1.0 * height / mapHeight, 1.0 * width / mapWidth);
		this.verticalMargin = ((int)(width - (mapWidth * scale)) / 2) + 8;
		this.horizontalMargin = ((int)(height - (mapHeight * scale)) / 2) + 8;
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

	public double getScale() {
		return scale;
	}
	
	
	private class PointPixel {
		int x;
		int y;
		PointPixel(double x, double y ) {
			this.x = (int) Math.round(x);
			this.y = (int) Math.round(y);
		}
	}
	
	
	
	
	
	
	
	
}
