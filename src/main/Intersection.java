package main;

import main.Carte.directions;

enum typesIntersection {
	PrincPrinc, PrincSec, SecSec, undefined;
}

public class Intersection {
	private typesIntersection type;
	private int x;
	private int y;
	
	public Intersection(int x, int y, typesIntersection type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public Intersection(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = typesIntersection.undefined;
	}
	
	public Intersection(String input) {
		String s = input.replace("(", "").replace(")", "").replace(" ", "");
		x = Integer.parseInt(s.substring(0,s.indexOf(',')));
		y = Integer.parseInt(s.substring(s.indexOf(',') + 1));
	}
	
	
	/**
	 * Bouge l'intersection actuelle par une certaine distance dans la direction sp�cifi�e.
	 * Utiliser la fonction movedFrom() pour obtenir une nouvelle intersection.
	 * @param direction La direction dans laquelle on d�place l'intersection
	 * @param distance Le nombre de pas
	 * @return Cette intersection.
	 */
	public Intersection move(directions direction, int distance) {
		
		if (direction == directions.est) x += distance;
		else if (direction == directions.ouest) x -= distance;
		else if (direction == directions.nord) y += distance;
		else if (direction == directions.sud) y -= distance;
		return this;
	}
	
	/**
	 * Retourne une nouvelle intersection ayant boug� un certain nombre de pas dans la direction indiqu�e depuis l'intersection actuelle.
	 * @param direction La direction dans laquelle on d�place l'intersection
	 * @param distance Le nombre de pas
	 * @return Une nouvelle intersection, ayant sa position modifi�e.
	 */
	public Intersection movedFrom(directions direction, int distance) {
		return this.clone().move(direction,  distance);
	}
	

	public int delta(Intersection intersection) {
		return Math.abs(intersection.getX() - x) + Math.abs(intersection.getY() - y);
	}
	
	public Intersection clone() {
		return new Intersection(x, y, type);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public typesIntersection getType() {
		return type;
	}
	
	public void setType(typesIntersection type) {
		this.type = type;
	}
	
	public String toString( ) {
		return "(" + x + "," + y +")";
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Intersection) {
			Intersection intersection = (Intersection) obj;
			if (intersection.x == x && intersection.y == y) return true;
		}
		return false;
	}
	
	/**
	 * Prends une chaine de caract�re repr�sentant une liste de points (comme fournis dans ParamMAAS) et le renvoie sous forme du tableau d'Intersections associ�.
	 * Le format accept� est "(x0,y0);(x1,y1);(x2,y2)", o� les diff�rents points sont s�par�s par des points-virgules. Des espaces peuvent �tre ajout�s
	 * n'importe o�.
	 * @param input La chaine de caract�re repr�sentant les points.
	 * @return Le tableau d'Intersections associ�s.
	 */
	public static Intersection[] parseIntersections(String input) {
		String[] coordonnees = input.split(";");
		Intersection[] tableau = new Intersection[coordonnees.length];
		for (int i = 0; i < coordonnees.length; i++) {
			String point = coordonnees[i];
			tableau[i] = new Intersection(point);
		}
		return tableau;
	}
}
