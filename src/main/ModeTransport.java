/* 
 * Classe représentant un moyen de transport pour l'application. Permet de calculer le temps nécessaire pour un déplacement, stocker
 * les paramètres d'un certain mode de transport et vérifier si le véhicule est utilisé pour un certain moment de la journée.
 *  
 */

package main;

import java.awt.Color;

enum typeTransport {
	marche, velo, voiture, autobus, metro, undefined
}

public class ModeTransport {
	
	private String nom;							// Nom du mode de transport
	private typeTransport type;					// Le type de véhicule utilisé

	private int vitesseRoutePrincipale;			// Vitesse du véhicule sur les routes principales, en m/s
	private int vitesseRouteSecondaire;			// Vitesse du véhicule sur les routes secondaires, en m/s
	
	private int arretIntersectionPrincPrinc;	// Temps d'arrêt en seconde aux intersections principales-principales
	private int arretIntersectionPrincSec;		// Temps d'arrêt en seconde aux intersections principales-secondaires
	private int arretIntersectionSecSec;		// Temps d'arrêt aux intersections secondaires-secondaires.
	
	//private boolean[] routesPermises;			// Détermine si le véhicule peut aller sur un certain type de routes. Pas utilisé.
	private int delaiEmbarquement;				// Détermine le temps d'attente avant d'embarquer dans le moyen de transport. Utiliser 0 si aucun.
	
	private boolean transportHeureReguliere;	// Indique que le mode de transport peut être utilisé pour les déplacements en dehors des heures de pointe.
	private boolean transportHeurePointe;		// Indique que le mode de transport peut être utilisé pour les déplacement lors des heures de pointe.
	private boolean transportEnCommun;			// Indique que le mode de transport est du transport en commun (donc utilise des circuits).
	
	private Color couleur;
	
	


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
	
	/**
	 * Inutilisé, détermine si le véhicule peut prendre une route d'un certain type.
	 * @param route
	 * @return
	 */
	/*public boolean peutAllerSurRoute(typesRoute route) {
		return routesPermises[route.ordinal()];
	}*/
	
	
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
	
	public Color getCouleur() {
		return couleur;
	}
	
	public void setCouleur(Color couleur) {
		this.couleur = couleur;
	}
	
	/**
	 * Prend la liste de variables du véhicule et l'applique à ce mode de transport.
	 * @param input String représentant les variables du véhicule. Voir le constructeur pour le format.
	 */
	public void parseInput(String input) {
		String[] data = input.split(",");
		int[] values = new int[data.length];
		for (int i = 1; i < 7; i++) {
			values[i] = Integer.parseInt(data[i]);
		}

		this.vitesseRoutePrincipale = values[1];
		this.vitesseRouteSecondaire = values[2];
		this.arretIntersectionPrincPrinc = values[3];
		this.arretIntersectionPrincSec = values[4];
		this.arretIntersectionSecSec = values[5];
		this.delaiEmbarquement = values[6];
		this.couleur = Color.decode(data[7]);
		//this.debutTrajet = values[];
		//this.routesPermises = values[];
	}
	
	/**
	 * Constructeur utilisant une chaine de caractères pour les paramètres.
	 * Le format est "[inutilisé], vitesseRoutePrincipale, vitesseRouteSecondaire, tempsArretIntersectionPrincPrinc, tempsArretIntersectionPrincSec,
	 * 					tempsArretIntersectionSecSec, tempsAttenteAvantEmbarquement".
	 * @param nom Nom du moyen de transport
	 * @param type Type de véhicule
	 * @param transportHeureReguliere Vrai si le véhicule est utilisé en période régulière
	 * @param transportHeurePointe Vrai si le véhicule est utilisé en heure de pointe
	 * @param transportEnCommun Vrai si le véhicule est du transport en commun
	 * @param input La chaine de caractère représentant les variables
	 */
	public ModeTransport(String nom, typeTransport type, boolean transportHeureReguliere, boolean transportHeurePointe, boolean transportEnCommun, String input) {
		super();
		this.nom = nom;
		this.type = type;
		this.transportHeureReguliere = transportHeureReguliere;
		this.transportHeurePointe = transportHeurePointe;
		this.transportEnCommun = transportEnCommun;
		parseInput(input);	
	}
	
	/**
	 * Constructeur direct, sans parsing de String.
	 */
	public ModeTransport(String nom, typeTransport type, int vitesseRoutePrincipale, int vitesseRouteSecondaire,
			int arretIntersectionPrincPrinc, int arretIntersectionPrincSec, int arretIntersectionSecSec,
			/*typesIntersection debutTrajet,*/ /*boolean[] routesPermises,*/ int delaiEmbarquement, boolean transportHeureReguliere, boolean transportHeurePointe,
			boolean transportEnCommun, String couleur) {
		super();
		this.nom = nom;
		this.type = type;
		this.vitesseRoutePrincipale = vitesseRoutePrincipale;
		this.vitesseRouteSecondaire = vitesseRouteSecondaire;
		this.arretIntersectionPrincPrinc = arretIntersectionPrincPrinc;
		this.arretIntersectionPrincSec = arretIntersectionPrincSec;
		this.arretIntersectionSecSec = arretIntersectionSecSec;
		//this.debutTrajet = debutTrajet;
		//this.routesPermises = routesPermises;
		this.delaiEmbarquement = delaiEmbarquement;
		this.transportHeureReguliere = transportHeureReguliere;
		this.transportHeurePointe = transportHeurePointe;
		this.transportEnCommun = transportEnCommun;
		this.couleur = Color.decode(couleur);
	}
	
}
