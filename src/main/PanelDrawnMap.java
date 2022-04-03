package main;

import java.awt.*;

import javax.swing.*;



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

	private Carte carte;
	
	//Image image;
	public PanelDrawnMap(Carte carte) {
		//image = new ImageIcon("mapMaas.jpg").getImage();
				//this.setPreferredSize(new Dimension(250,250)); //Size of panel with pack
		super();
		try {
			this.carte = carte;
		} catch (Exception e) {
			
		}
	}

	
	final static Parametres defaultParams = new Parametres("400,300,400,300",
			"400,300,400,300,400",
			"PSPSPS",
			"SPSSP",
			"Marche,1,1,30,20,5,0;Vélo,6,7,40,30,15,0;Autobus,20,14,15,20,10,300;Métro,25,25,45,0,0,180;Voiture (régulier),20,14,60,40,10,0;Voiture (heure de pointe),8,10,90,60,45,0",
			"(0,1);(0,4);(5,4)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (2,0) (0,0) (0,2) (0,4);(0,1) (2,1) (2,3) (3,4) (4,2) (3,1) (1,0) (0,1);(5,0) (5,2) (3,3) (1,3) (1,0) (3,0) (5,0)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
			);
	
	//Fonction de base paint, c'est exécuté chaque fois que le JVM pense que c'est necessaire de paint, comme lors d'une minimisation-maximization
	public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g; //Cast Graphics g to a Graphics2D, Updated version more methods
        
        
        g2D.setPaint(Color.white); //Color
        g2D.fillRect(0, 0, getWidth(), getHeight()); //Create rectangle with fill
        paintCarte(g2D);
        
	}
	
	//Fonction qui recueille toutes les fonctions
	private void paintCarte(Graphics2D g2D) {
		
		mapWidth = sumArray(carte.getSegmentsHorizontaux()); //Fait la somme des segements horizontaux et le met dans mapWidth
		mapHeight = sumArray(carte.getSegmentsVerticaux()); //Fait la somme des segements verticaux et le met dans mapHeight
		
		positionRoutesHorizontales = sumFromBefore(carte.getSegmentsVerticaux()); // Crée un array de la longeur des segements
		//verticaux à partir du point 0,0, cela permet de trouver de la position de chacune des routes, c'est le x
		positionRoutesVerticales = sumFromBefore(carte.getSegmentsHorizontaux()); // Crée un array de la longeur des segements
		//horizontaux à partir du point 0,0, cela permet de trouver de la position de chacune des routes, c'est le y
		
		
		setScale();
		paintRoutes(g2D); //Appelle paintRoute, pour paint toutes les routes
		paintNombresCarte(g2D); //Appelle paintNombresCarte, pour paint tous les nombres sur la carte
		
		if (itineraire != null){		
			Intersection depart = itineraire.getTrajets(0).getDepart();
			PointPixel pointDepart = new PointPixel(positionRoutesVerticales[depart.getX()], positionRoutesHorizontales[depart.getY()]);
			Intersection arrivee = itineraire.getTrajets(itineraire.getTrajets().size() - 1).getDestination();
			PointPixel pointArrivee = new PointPixel(positionRoutesVerticales[arrivee.getX()], positionRoutesHorizontales[arrivee.getY()]);
			
			
			paintPoint(g2D, pointDepart);
			paintPoint(g2D, pointArrivee);
			paintItineraire(g2D);
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
	
	//Fait la somme de toutes les valeurs avant jusqu'à la valeur desirée et crée un tableau pour stocker toutes les valeurs
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
		
		int dernierX = 5;
		int dernierY = 4;
		
		//Offset pour les x sur position y
		int offsetY = -160;
		
		//Offset pour les y sur position x
		int offsetX = -140;
		
		//Offset centrer les chiffres avec les routes
		int offsetCentrer = 40;
		
		//Taille des chiffres
		int tailleChiffres = 40;
		
		//Couleur des chiffres
		Color color = Color.black;
		
		
		//Paint tous les numéros des X sauf le dernier
		for (int i = 0; i < carte.getNbRoutesHorizontales(); i++) {

				String stringNb = Integer.toString(i);
				paintString(g2D, scale(segmentAvantX - offsetCentrer, offsetY), stringNb, color,tailleChiffres);
				
				segmentAvantX  += carte.getSegmentsHorizontaux(i);
	
			
		}	
		
		//Paint le dernier X
		String stringX = Integer.toString(dernierX);
		paintString(g2D, scale(segmentAvantX - offsetCentrer, offsetY), stringX, color,tailleChiffres);
		
		segmentAvantX  += carte.getSegmentsHorizontaux(dernierX-1);
		
		
		//Paint tous les numéros des Y sauf le dernier 
		for (int i = 0; i < carte.getNbRoutesVerticales()-2; i++) {
		 
			 String stringNb = Integer.toString(i);
			 paintString(g2D, scale(offsetX, segmentAvantY - offsetCentrer), stringNb, color,tailleChiffres);
		 
			 segmentAvantY += carte.getSegmentsVerticaux(i);
		  
		  
		 }
		
		//RePaint en blanc sur le 0 des Y pour l'enlever
		String stringNb = Integer.toString(0);
		paintString(g2D, scale(offsetX, - offsetCentrer), stringNb, Color.white,tailleChiffres);
		  
		//Paint le dernier Y
		String stringY = Integer.toString(dernierY);
		paintString(g2D, scale(offsetX, segmentAvantY - offsetCentrer), stringY, color,tailleChiffres);
		
		segmentAvantX  += carte.getSegmentsVerticaux(dernierY-1);
		 
	
	}
	
	
	
	//Paint un itinéraire en segements, les segments sont de couleurs basé sur le type de véhicule
	public void paintItineraire(Graphics2D g2D) {
			
		//Pour chaque trajets de l'itinéraire...
		for(int i = 0; i < itineraire.getTrajets().size(); i++) {
			
			Trajet trajet = itineraire.getTrajets().get(i);
			
			if (trajet.getNbIntersections() > 1) {
				
				// Pour chaque points dans la liste d'intersections du trajet, à l'exception du dernier
				for (int j = 0; j < trajet.getNbIntersections() - 1; j++) {
					
					Intersection pointA = trajet.getIntersections().get(j);
					Intersection pointB = trajet.getIntersections().get(j + 1);
					
					paintLine(g2D, intersectionToPointPixel(pointA), intersectionToPointPixel(pointB), trajet.getVehicule().getCouleur(), 8);
					
				}
			}
		}
	}
		
	//Paint le point de départ et d'arrivée
	private void paintPoint(Graphics2D g2D, PointPixel point) {
		
		paintCircle(g2D, scale(point.x, point.y), scale(point.x, point.y), Color.decode("#00FF00"));

	}

		


	//Paint une ligne basé sur l'emplacement d'une route, donc paint une route
	private void paintRoute(Graphics2D g2D, int x1, int y1, int x2, int y2, boolean principal) {
		if (principal) {
			paintLine(g2D, scale(x1 - 1,y1 - 1), scale(x2 - 1,y2 - 1), Color.decode("#202020"), 8);
		} else {
			paintLine(g2D, scale(x1,y1), scale(x2,y2), Color.decode("#A0A0A0"), 8);
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
	
	
	//Crée un objet PointPixel
	private PointPixel scale(int x, int y) {
		return new PointPixel((x * scale) + verticalMargin, getHeight() - ((y * scale) + horizontalMargin));
	}
	

	//Convertit les données dans les arrays en pixels avec un ratio déterminé dans cette méthode
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

	public double getScale() {
		return scale;
	}
	
	public void setItineraire(Itineraire itineraire) {
		this.itineraire = itineraire;
	}
	
	//Pour créer un objet pixel avec coordonnees x et y
	private class PointPixel {
		int x;
		int y;
		PointPixel(double x, double y ) {
			this.x = (int) Math.round(x);
			this.y = (int) Math.round(y);
		}
	}
	
	

}
