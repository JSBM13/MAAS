package main;

import main.typesIntersection;
import main.Carte.typesRoute;

enum typeTransport {
	marche, velo, voiture, autobus, metro, undefined
}

public class ModeTransport {
	
	
	private String nom;
	private typeTransport type;

	private int[] vitesses;
	private int[] tempsArret;
	private typesIntersection debutTrajet;
	private boolean[] routesPermises;
	private int delaiEmbarquement;
	private boolean varieSelonHeure;
	private boolean transportEnCommun;
	
	
	public ModeTransport(String nom, int[] vitesses, int[] tempsArret, typesIntersection debutTrajet,
			boolean[] routesPermises, int delaiEmbarquement) {
		super();
		this.nom = nom;
		this.vitesses = vitesses;
		this.tempsArret = tempsArret;
		this.debutTrajet = debutTrajet;
		this.routesPermises = routesPermises;
		this.delaiEmbarquement = delaiEmbarquement;
		this.transportEnCommun = true;
	}
	
	public ModeTransport(String nom, int[] vitesses, int[] tempsArret, typesIntersection debutTrajet,
			boolean[] routesPermises) {
		super();
		this.nom = nom;
		this.vitesses = vitesses;
		this.tempsArret = tempsArret;
		this.debutTrajet = debutTrajet;
		this.routesPermises = routesPermises;
		this.transportEnCommun = false;
	}
	
	/**
	 * Calcule le temps nécessaire pour parcourir un trajet. Cette méthode ne prend en compte que la distance parcourue et le nombre d'arrêt, et ne prend pas en compte le temps de la journée.
	 * @param distanceRoutePrincipale Distance parcourue sur une route principale, en mètres.
	 * @param distanceRouteSecondaire Distance parcourue sur une route secondaire, en mètres.
	 * @param intersections Array contenant la liste des intersections par lequel le véhicule a passé.
	 * @return Le temps en secondes.
	 */
	public int calculateTempsDeplacement(int distanceRoutePrincipale, int distanceRouteSecondaire, Intersection[] intersections) {
		return calculateTempsDeplacement(distanceRoutePrincipale, distanceRouteSecondaire, intersections, false);
	}
	
	/**
	 * Calcule le temps nécessaire pour parcourir un trajet. Cette méthode ne prend en compte que la distance parcourue et le nombre d'arrêt, ainsi que le moment de la journée.
	 * @param distanceRoutePrincipale Distance parcourue sur une route principale, en mètres.
	 * @param distanceRouteSecondaire Distance parcourue sur une route secondaire, en mètres.
	 * @param intersections Array contenant la liste des intersections par lequel le véhicule a passé.
	 * @param heureDePointe Un boolean indiquant si la fonction doit considérer plutôt les valeurs de l'heure de pointe.
	 * @return
	 */
	public int calculateTempsDeplacement(int distanceRoutePrincipale, int distanceRouteSecondaire, Intersection[] intersections, boolean heureDePointe) {
		int t = 0;
		t += Math.round(distanceRoutePrincipale * vitesses[(heureDePointe ? 2 : 0)]);
		t += Math.round(distanceRouteSecondaire * vitesses[(heureDePointe ? 3 : 1)]);
		for (Intersection intersection : intersections) {
			t += tempsArret[intersection.getType().ordinal() + (heureDePointe? 3 : 0)];
		}
		return t;
	}
	
	public typeTransport getType() {
		return type;
	}
	
	
	public boolean peutAllerSurRoute(typesRoute route) {
		return routesPermises[route.ordinal()];
	}
	
}
