/*
 * Représente un circuit que va suivre un véhicule de transport en commun.
 * Les circuits sont une liste d'intersections (x,y) traversée, en ordre. Les circuits son toujours une 
 * boucle, et le premier et dernier circuit traversé doivent être identique.
 * 
 */

package main;

public final class Circuit {
	
	public Intersection[] arrets;
	public ModeTransport vehicule;
	
	/**
	 * Constructeur. Prend une liste de points sous forme de chaine de caractères dans le format "(x1,y1)(x2,y2)", où les x et les y sont des arrays.
	 * Des espaces peuvent être insérés entre les parenthèses. Ce format est celui généré par ParamMAAS.
	 * @param vehicule
	 * @param pointsArray
	 */
	public Circuit(ModeTransport vehicule, String pointsArray) {
		parsePoints(pointsArray);
		this.vehicule = vehicule;
	}
	
	/**
	 * Prend la liste de points du constructeur et transforme le tout en un array d'Intersection, qui seront stockés dans la variable arrets.
	 * 
	 */
	private void parsePoints(String pointsArray) {
		String[] pointsText = pointsArray.replace(" ", "").split("\\)\\(");
		arrets = new Intersection[pointsText.length];
		for (int i = 0; i < pointsText.length; i++) {
			String s = pointsText[i].replace("(", "").replace(")", "");
			int x, y;
			x = Integer.parseInt(s.substring(0,s.indexOf(',')));
			y = Integer.parseInt(s.substring(s.indexOf(',') + 1));
			arrets[i] = new Intersection(x,y);
		}
		
	}
	
	public String toString() {
		String s = "Circuit: [ ";
		for (Intersection p : arrets) {
			s += p.toString() + " ";
		}
		return s + "]";
	}
	
	public Intersection getArret(int index) {
		return arrets[index % arrets.length];
	}
	
	public Intersection[] getArrets() {
		return arrets;
	}
	
	public int getNbArrets() {
		return arrets.length;
	}
	
	/**
	 * Prends une chaine de caractère représentant une liste de circuits (comme fournis dans ParamMAAS) et le renvoie sous forme du tableau de Circuits associé.
	 * Le format accepté d'un circuit est "(x0,y0)(x1,y1)(x2,y2)", où les différents circuits sont séparés par des points-virgules. Des espaces peuvent être ajoutés
	 * entre les parenthèses.
	 * @param input La chaine de caractère représentant les circuits.
	 * @param vehicule Le type de mode de transport à utiliser pour ces circuits.
	 * @return
	 */
	public static Circuit[] parseCircuits(String input, ModeTransport vehicule) {
		String[] circuits = input.split(";");
		Circuit[] tableau = new Circuit[circuits.length];
		for (int i = 0; i < circuits.length; i++) {
			String circuit = circuits[i];
			tableau[i] = new Circuit(vehicule, circuit);
		}
		return tableau;
	}
}
