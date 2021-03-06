package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelDrawnMap extends JPanel{
	
	private int mapHeight;
	private int mapWidth;
	private int verticalMargin;
	private int horizontalMargin;
	private double scale;

	private int[] positionRoutesHorizontales;
	private int[] positionRoutesVerticales;
	
	private Itineraire itineraire;
	private ArrayList<Intersection> points;

	private Carte carte;
	
	//Image image;
	public PanelDrawnMap(Carte carte) {
		//image = new ImageIcon("mapMaas.jpg").getImage();
				//this.setPreferredSize(new Dimension(250,250)); //Size of panel with pack
		super();
		this.points = new ArrayList<Intersection>();
		try {
			this.carte = carte;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PanelDrawnMap() {
		super();
		this.points = new ArrayList<Intersection>();
		Parametres params = Parametres.defaultParams6x5;
		try {
			carte = new Carte(params.distanceRoutesVerticales, params.distanceRoutesHorizontales, params.typesRoutesVerticales, params.typesRoutesHorizontales);
		} catch (Carte.UnequalNumberOfRoadsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Fonction de base paint, c'est ex?cut? chaque fois que le JVM pense que c'est necessaire de paint, comme lors d'une minimisation-maximization
	public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g; //Cast Graphics g to a Graphics2D, Updated version more methods
        
        
        g2D.setPaint(Color.white); //Color
        g2D.fillRect(0, 0, getWidth(), getHeight()); //Create rectangle with fill
        paintCarte(g2D);
        
	}
	
	//Fonction qui recueille toutes les fonctions
	private void paintCarte(Graphics2D g2D) {
		
		mapWidth = sumArray(carte.getSegmentsVerticaux()); //Fait la somme des segements horizontaux et le met dans mapWidth
		mapHeight = sumArray(carte.getSegmentsHorizontaux()); //Fait la somme des segements verticaux et le met dans mapHeight
		
		positionRoutesHorizontales = sumFromBefore(carte.getSegmentsHorizontaux()); // Cr?e un array de la longeur des segements
		//verticaux ? partir du point 0,0, cela permet de trouver de la position de chacune des routes, c'est le x
		positionRoutesVerticales = sumFromBefore(carte.getSegmentsVerticaux()); // Cr?e un array de la longeur des segements
		//horizontaux ? partir du point 0,0, cela permet de trouver de la position de chacune des routes, c'est le y
		
		
		setScale();
		paintRoutes(g2D); //Appelle paintRoute, pour paint toutes les routes
		paintNombresCarte(g2D); //Appelle paintNombresCarte, pour paint tous les nombres sur la carte
		
		if (itineraire != null){		
			Intersection depart = itineraire.getTrajets(0).getDepart();
			PointPixel pointDepart = new PointPixel(positionRoutesVerticales[depart.getX()], positionRoutesHorizontales[depart.getY()]);
			Intersection arrivee = itineraire.getTrajets(itineraire.getTrajets().size() - 1).getDestination();
			PointPixel pointArrivee = new PointPixel(positionRoutesVerticales[arrivee.getX()], positionRoutesHorizontales[arrivee.getY()]);
			
			paintItineraire(g2D);
			paintPoint(g2D, pointDepart);
			paintPoint(g2D, pointArrivee);
		}
		
		if (points.size() > 0) {
			for (int i = 0; i < points.size(); i++) {
				Intersection pointDessiner = points.get(i);
				PointPixel point = new PointPixel(positionRoutesVerticales[pointDessiner.getX()], positionRoutesHorizontales[pointDessiner.getY()]);
				paintPoint(g2D, point);
			}
		}
		
		
	}
	
	
	private PointPixel intersectionToPointPixel(Intersection intersection) {
		PointPixel point = scale(positionRoutesVerticales[intersection.getX()], positionRoutesHorizontales[intersection.getY()]);
		return point;
	}
	
	//Paint toutes les routes
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
	//Fait la somme d'un array
	private int sumArray(int[] array) {
		int x = 0;
		for (int i = 0; i < array.length; i++) {
			x += array[i];
		}
		return x;
	}
	
	//Fait la somme de toutes les valeurs avant jusqu'? la valeur desir?e et cr?e un tableau pour stocker toutes les valeurs
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
	
	//Paint les nombres des x et y sur la carte
	private void paintNombresCarte(Graphics2D g2D) {
		
		int segmentAvantX = 0;
		int segmentAvantY = 0;
		
		//Offset pour les x sur position y
		int offsetY = -160;
		
		//Offset pour les y sur position x
		int offsetX = -140;
		
		//Offset centrer les chiffres avec les routes
		int offsetCentrer = 40;
		
		//Taille des chiffres
		int tailleChiffres = 40;
		
		if (carte.getNbRoutesHorizontales() > 7 || carte.getNbRoutesVerticales() > 7) tailleChiffres = 12;
		
		//Couleur des chiffres
		Color color = Color.black;
		
		
		//Paint tous les num?ros des X
		for (int i = 0; i < carte.getNbRoutesVerticales(); i++) {

				String stringNb = Integer.toString(i);
				paintString(g2D, scale(segmentAvantX - offsetCentrer, offsetY), stringNb, color,tailleChiffres);
				
				if (i < carte.getNbRoutesVerticales() - 1 ) segmentAvantX  += carte.getSegmentsVerticaux(i);
		}	

		
		
		//Paint tous les num?ros des Y
		for (int i = 0; i < carte.getNbRoutesHorizontales(); i++) {
		 
			 String stringNb = Integer.toString(i);
			 paintString(g2D, scale(offsetX, segmentAvantY - offsetCentrer), stringNb, color,tailleChiffres);
		 
			 if (i < carte.getNbRoutesHorizontales() - 1 ) segmentAvantY += carte.getSegmentsHorizontaux(i);
		  
		  
		 }
		
		//RePaint en blanc sur le 0 des Y pour l'enlever
		String stringNb = Integer.toString(0);
		paintString(g2D, scale(offsetX, - offsetCentrer), stringNb, Color.white,tailleChiffres);
		 
	
	}
	
	
	
	//Paint un itin?raire en segements, les segments sont de couleurs bas? sur le type de v?hicule
	public void paintItineraire(Graphics2D g2D) {
		//Pour chaque trajets de l'itin?raire...
		for(int i = 0; i < itineraire.getTrajets().size(); i++) {
			
			Trajet trajet = itineraire.getTrajets().get(i);
			
			if (trajet.getNbIntersections() > 1) {
				
				// Pour chaque points dans la liste d'intersections du trajet, ? l'exception du dernier
				for (int j = 0; j < trajet.getNbIntersections() - 1; j++) {
					
					Intersection pointA = trajet.getIntersections().get(j);
					Intersection pointB = trajet.getIntersections().get(j + 1);
					
					paintLine(g2D, intersectionToPointPixel(pointA), intersectionToPointPixel(pointB), trajet.getVehicule().getCouleur(), getRouteSize());
				}
			}
		}
	}
		
	//Paint le point de d?part et d'arriv?e
	private void paintPoint(Graphics2D g2D, PointPixel point) {
		
		paintCircle(g2D, scale(point.x, point.y), scale(point.x, point.y), Color.decode("#00FF00"));

	}

	private int getRouteSize() {
		if (carte.getNbRoutesHorizontales() > 8 || carte.getNbRoutesVerticales() > 8) {
			return 5;
		} else {
			return 8;
		}
	}


	//Paint une ligne bas? sur l'emplacement d'une route, donc paint une route
	private void paintRoute(Graphics2D g2D, int x1, int y1, int x2, int y2, boolean principal) {
		if (principal) {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.decode("#202020"), getRouteSize() );
		} else {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.decode("#A0A0A0"), getRouteSize() );
		}
		
	}
	
	//Paint une ligne
	private void paintLine(Graphics2D g2D, PointPixel p1, PointPixel p2, Color color, int size) {
		g2D.setPaint(color);
		g2D.setStroke(new BasicStroke(size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		g2D.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	//Paint un String
	private void paintString(Graphics2D g2D, PointPixel p1, String string, Color color, int size) {
		g2D.setFont(new Font ("Arial", Font.BOLD, size));
		g2D.setPaint(color);
		g2D.drawString(string , p1.x, p1.y);
	}
	
	//Paint un cercle
	private void paintCircle(Graphics2D g2D, PointPixel p1, PointPixel p2, Color color) {
		g2D.setPaint(color);
		g2D.fillOval(p1.x -10, p1.y-10, 20, 20);
	}
	
	
	//Cr?e un objet PointPixel
	private PointPixel scale(int x, int y) {
		return new PointPixel((x * scale) + verticalMargin, getHeight() - ((y * scale) + horizontalMargin));
	}
	

	//Convertit les donn?es dans les arrays en pixels avec un ratio d?termin? dans cette m?thode
	public void setScale() {
		
		int padding = 100;
		
		int width = this.getWidth()- padding;
		int height = this.getHeight() - padding;
		
		//Retourne la plus petite valeur entre la height / par mapHeight et width / par mapWidth 
		this.scale = Math.min(1.0 * height / mapHeight, 1.0 * width / mapWidth);
		
		this.verticalMargin = ((int)(width - (mapWidth * scale)) / 2) + padding/2;
		this.horizontalMargin = ((int)(height - (mapHeight * scale)) / 2) + padding/2;
	}

	
	
	//Getters et setters
	
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
	
	public Carte getCarte() {
		return carte;
	}
	
	public double getScale() {
		return scale;
	}
	
	public void setItineraire(Itineraire itineraire) {
		this.itineraire = itineraire;
	}
	
	public void setCarte(Carte carte) {
		this.carte = carte;
		repaint();
	}
	
	public ArrayList<Intersection> getPoints() {
		return this.points;
	}
	
	public void setPoints(ArrayList<Intersection> points) {
		this.points = points; 
	}
	
	public void clearPoints() {
		this.points.clear();
	}
	
	public void addPoint(Intersection point) {
		this.points.add(point);
	}
	
	//Pour cr?er un objet pixel avec coordonnees x et y
	private class PointPixel {
		int x;
		int y;
		PointPixel(double x, double y ) {
			this.x = (int) Math.round(x);
			this.y = (int) Math.round(y);
		}
	}
	
	

}
