/**
 * Classe utilitaire qui repr?sente une intersection donn?e dans la carte.
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
	 * Bouge l'intersection actuelle par une certaine distance dans la direction sp?cifi?e.
	 * Attention, cette m?thode ne retournera pas forc?ment une intersection qui existe dans la Carte.
	 * Utiliser la fonction movedFrom() pour obtenir une nouvelle intersection.
	 * @param direction La direction dans laquelle on d?place l'intersection
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
	 * D?termine le nombre de segments qui s?pare cette intersection de celle indiqu?e.
	 * @param intersection Une certaine intersection ? comparer.
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
	 * Retourne vrai si les coordonn?es x et y sont identiques. Ne prend pas en compte le type d'Intersection (donc ce n'est pas
	 * un probl?me si le type est ? undefined).
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
	 * Constructeur d'Intersection o? le type est directement fourni.
	 * 
	 */
	public Intersection(int x, int y, typesIntersection type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructeur d'Intersection sans le type. Pr?f?rable quand l'on est pas certain si une intersection existe vraiment.
	 */
	public Intersection(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = typesIntersection.undefined;
	}
	
	/**
	 * Constructeur d'Intersection o? l'on fournit uniquement un String du format (x,y). Ne sp?cifie pas le type de l'intersection
	 * (il devra donc ?tre sett? plus tard, si besoin est.)
	 */
	public Intersection(String input) {
		String s = input.replace("(", "").replace(")", "").replace(" ", "");
		x = Integer.parseInt(s.substring(0,s.indexOf(',')));
		y = Integer.parseInt(s.substring(s.indexOf(',') + 1));
	}
	
	/**
	 * Constructeur o? l'on duplique les coordonn?es et propri?t?s d'une intersection existante.
	 */
	public Intersection(Intersection intersection) {
		this.x = intersection.getX();
		this.y = intersection.getY();
		this.type = intersection.getType();
	}
	
	/**
	 * Prends une chaine de caract?re repr?sentant une liste de points (comme fournis dans ParamMAAS) et le renvoie sous forme du tableau d'Intersections associ?.
	 * Le format accept? est "(x0,y0);(x1,y1);(x2,y2)", o? les diff?rents points sont s?par?s par des points-virgules. Des espaces peuvent ?tre ajout?s
	 * n'importe o?.
	 * @param input La chaine de caract?re repr?sentant les points.
	 * @return Le tableau d'Intersections associ?s.
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
