package main;

import main.typesIntersection;
import main.Carte.typesRoute;

enum typeTransport {
	marche, velo, voiture, autobus, metro, undefined
}

public class ModeTransport {
	
	
	private String nom;
	private typeTransport type;

	private int vitesseRoutePrincipale;
	private int vitesseRouteSecondaire;
	private int arretIntersectionPrincPrinc;
	private int arretIntersectionPrincSec;
	private int arretIntersectionSecSec;
	//private typesIntersection debutTrajet;
	private boolean[] routesPermises;
	private int delaiEmbarquement;
	private boolean transportHeureReguliere;
	private boolean transportHeurePointe;
	private boolean transportEnCommun;
	
	
	public ModeTransport(String nom, typeTransport type, boolean transportHeureReguliere, boolean transportHeurePointe, boolean transportEnCommun, String input) {
		super();
		this.nom = nom;
		this.type = type;
		this.transportHeureReguliere = transportHeureReguliere;
		this.transportHeurePointe = transportHeurePointe;
		this.transportEnCommun = transportEnCommun;
		parseInput(input);	
	}
	
	public ModeTransport(String nom, typeTransport type, int vitesseRoutePrincipale, int vitesseRouteSecondaire,
			int arretIntersectionPrincPrinc, int arretIntersectionPrincSec, int arretIntersectionSecSec,
			typesIntersection debutTrajet, boolean[] routesPermises, int delaiEmbarquement, boolean transportHeureReguliere, boolean transportHeurePointe,
			boolean transportEnCommun) {
		super();
		this.nom = nom;
		this.type = type;
		this.vitesseRoutePrincipale = vitesseRoutePrincipale;
		this.vitesseRouteSecondaire = vitesseRouteSecondaire;
		this.arretIntersectionPrincPrinc = arretIntersectionPrincPrinc;
		this.arretIntersectionPrincSec = arretIntersectionPrincSec;
		this.arretIntersectionSecSec = arretIntersectionSecSec;
		//this.debutTrajet = debutTrajet;
		this.routesPermises = routesPermises;
		this.delaiEmbarquement = delaiEmbarquement;
		this.transportHeureReguliere = transportHeureReguliere;
		this.transportHeurePointe = transportHeurePointe;
		this.transportEnCommun = transportEnCommun;
	}

	/**
	 * Calcule le temps nécessaire pour parcourir un trajet. Cette méthode prends en compte la distance parcourue et le nombre d'arrêt.
	 * @param distanceRoutePrincipale Distance parcourue sur une route principale, en mètres.
	 * @param distanceRouteSecondaire Distance parcourue sur une route secondaire, en mètres.
	 * @param intersections Array contenant la liste des intersections par lequel le véhicule a passé.
	 * @return Le temps, en secondes.
	 */
	public int calculateTempsDeplacement(int distanceRoutePrincipale, int distanceRouteSecondaire, Intersection[] intersections) {
		int t = 0;
		t += Math.round(distanceRoutePrincipale / getVitesse(typesRoute.principale));
		t += Math.round(distanceRouteSecondaire / getVitesse(typesRoute.secondaire));
		for (Intersection intersection : intersections) {
			t += getTempsIntersection(intersection.getType());
		}
		return delaiEmbarquement + t;
	}
	
	public typeTransport getType() {
		return type;
	}
	
	/**
	 * Retourne le temps d'attente à une intersection de ce mode de transport selon le type d'intersection.
	 * @param type Le type d'intersection.
	 * @return Le temps d'arrêt à une intersection.
	 */
	public int getTempsIntersection(typesIntersection type) {
		switch (type) {
		case PrincPrinc:
			return arretIntersectionPrincPrinc;
		case PrincSec:
			return arretIntersectionPrincSec;
		case SecSec:
			return arretIntersectionSecSec;
		case undefined:
		default:
			return -1;
		}
	}
	
	/**
	 * Retourne la vitesse de ce mode de transport selon le type de route.
	 * @param type Le type de route.
	 * @return La vitesse.
	 */
	public int getVitesse(typesRoute type) {
		switch (type) {
		case principale:
			return vitesseRoutePrincipale;
		case secondaire:
			return vitesseRouteSecondaire;
		default:
			return -1;
		}
	}
	
	
	public boolean peutAllerSurRoute(typesRoute route) {
		return routesPermises[route.ordinal()];
	}
	
	
	
	public String getNom() {
		return nom;
	}

	public int getDelaiEmbarquement() {
		return delaiEmbarquement;
	}

	public boolean isTransportHeureReguliere() {
		return transportHeureReguliere;
	}

	public boolean isTransportHeurePointe() {
		return transportHeurePointe;
	}

	public boolean isTransportEnCommun() {
		return transportEnCommun;
	}

	public void parseInput(String input) {
		String[] data = input.split(",");
		int[] values = new int[data.length];
		for (int i = 1; i < data.length; i++) {
			values[i] = Integer.parseInt(data[i]);
		}

		this.vitesseRoutePrincipale = values[1];
		this.vitesseRouteSecondaire = values[2];
		this.arretIntersectionPrincPrinc = values[3];
		this.arretIntersectionPrincSec = values[4];
		this.arretIntersectionSecSec = values[5];
		this.delaiEmbarquement = values[6];
		//this.debutTrajet = values[];
		//this.routesPermises = values[];
	}
	
}
