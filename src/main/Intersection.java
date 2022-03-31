/**
 * Classe utilitaire qui représente une intersection donnée dans la carte.
 * 
 */

package main;


enum typesIntersection {
	PrincPrinc, PrincSec, SecSec, undefined;
}

public class Intersection {
	
	private typesIntersection type;	// Le type d'intersection.
	private int x;					// L'index de la rue verticale de l'intersection.
	private int y;					// L'index de la rue horizontale de l'intersection.
	
	/**
	 * Bouge l'intersection actuelle par une certaine distance dans la direction spécifiée.
	 * Utiliser la fonction movedFrom() pour obtenir une nouvelle intersection.
	 * @param direction La direction dans laquelle on déplace l'intersection
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
	 * Retourne une nouvelle intersection ayant bougé un certain nombre de pas dans la direction indiquée depuis l'intersection actuelle.
	 * @param direction La direction dans laquelle on déplace l'intersection
	 * @param distance Le nombre de pas
	 * @return Une nouvelle intersection, ayant sa position modifiée.
	 */
	public Intersection movedFrom(directions direction, int distance) {
		return this.clone().move(direction,  distance);
	}
	
	/**
	 * Détermine le nombre de segments qui sépare cette intersection de celle indiquée.
	 * @param intersection Une certaine intersection à comparer.
	 */
	public int delta(Intersection intersection) {
		return Math.abs(intersection.getX() - x) + Math.abs(intersection.getY() - y);
	}
	
	public Intersection clone() {
		return new Intersection(x, y, type);
	}
	
	public String toString( ) {
		return "(" + x + "," + y +")";
	}
	
	/**
	 * Retourne vrai si les coordonnées x et y sont identiques. Ne prend pas en compte le type d'Intersection (donc ce n'est pas
	 * un problème si le type est à undefined).
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Intersection) {
			Intersection intersection = (Intersection) obj;
			if (intersection.x == x && intersection.y == y) return true;
		}
		return false;
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
	

	
	
	/**
	 * Constructeur d'Intersection où le type est directement fourni.
	 * 
	 */
	public Intersection(int x, int y, typesIntersection type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructeur d'Intersection sans le type. Préférable quand l'on est pas certain si une intersection existe vraiment.
	 */
	public Intersection(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = typesIntersection.undefined;
	}
	
	/**
	 * Constructeur d'Intersection où l'on fournit uniquement un String du format (x,y). Ne spécifie pas le type de l'intersection
	 * (il devra donc être setté plus tard, si besoin est.)
	 */
	public Intersection(String input) {
		String s = input.replace("(", "").replace(")", "").replace(" ", "");
		x = Integer.parseInt(s.substring(0,s.indexOf(',')));
		y = Integer.parseInt(s.substring(s.indexOf(',') + 1));
	}
	
	/**
	 * Prends une chaine de caractère représentant une liste de points (comme fournis dans ParamMAAS) et le renvoie sous forme du tableau d'Intersections associé.
	 * Le format accepté est "(x0,y0);(x1,y1);(x2,y2)", où les différents points sont séparés par des points-virgules. Des espaces peuvent être ajoutés
	 * n'importe où.
	 * @param input La chaine de caractère représentant les points.
	 * @return Le tableau d'Intersections associés.
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
