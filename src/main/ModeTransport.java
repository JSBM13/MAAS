/* 
 * Classe repr�sentant un moyen de transport pour l'application. Permet de calculer le temps n�cessaire pour un d�placement, stocker
 * les param�tres d'un certain mode de transport et v�rifier si le v�hicule est utilis� pour un certain moment de la journ�e.
 *  
 */

package main;

import java.awt.Color;

enum typeTransport {
	marche, velo, voiture, autobus, metro, undefined
}

public class ModeTransport {
	
	private String nom;							// Nom du mode de transport
	private typeTransport type;					// Le type de v�hicule utilis�

	private int vitesseRoutePrincipale;			// Vitesse du v�hicule sur les routes principales, en m/s
	private int vitesseRouteSecondaire;			// Vitesse du v�hicule sur les routes secondaires, en m/s
	
	private int arretIntersectionPrincPrinc;	// Temps d'arr�t en seconde aux intersections principales-principales
	private int arretIntersectionPrincSec;		// Temps d'arr�t en seconde aux intersections principales-secondaires
	private int arretIntersectionSecSec;		// Temps d'arr�t aux intersections secondaires-secondaires.
	
	//private boolean[] routesPermises;			// D�termine si le v�hicule peut aller sur un certain type de routes. Pas utilis�.
	private int delaiEmbarquement;				// D�termine le temps d'attente avant d'embarquer dans le moyen de transport. Utiliser 0 si aucun.
	
	private boolean transportHeureReguliere;	// Indique que le mode de transport peut �tre utilis� pour les d�placements en dehors des heures de pointe.
	private boolean transportHeurePointe;		// Indique que le mode de transport peut �tre utilis� pour les d�placement lors des heures de pointe.
	private boolean transportEnCommun;			// Indique que le mode de transport est du transport en commun (donc utilise des circuits).
	
	private Color couleur;
	
	


	/**
	 * Calcule le temps n�cessaire pour parcourir un trajet. Cette m�thode prends en compte la distance parcourue et le nombre d'arr�t.
	 * @param distanceRoutePrincipale Distance parcourue sur une route principale, en m�tres.
	 * @param distanceRouteSecondaire Distance parcourue sur une route secondaire, en m�tres.
	 * @param intersections Array contenant la liste des intersections par lequel le v�hicule a pass�.
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
	 * Retourne le temps d'attente � une intersection de ce mode de transport selon le type d'intersection.
	 * @param type Le type d'intersection.
	 * @return Le temps d'arr�t � une intersection.
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
	 * Inutilis�, d�termine si le v�hicule peut prendre une route d'un certain type.
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
	 * Prend la liste de variables du v�hicule et l'applique � ce mode de transport.
	 * @param input String repr�sentant les variables du v�hicule. Voir le constructeur pour le format.
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
	 * Constructeur utilisant une chaine de caract�res pour les param�tres.
	 * Le format est "[inutilis�], vitesseRoutePrincipale, vitesseRouteSecondaire, tempsArretIntersectionPrincPrinc, tempsArretIntersectionPrincSec,
	 * 					tempsArretIntersectionSecSec, tempsAttenteAvantEmbarquement".
	 * @param nom Nom du moyen de transport
	 * @param type Type de v�hicule
	 * @param transportHeureReguliere Vrai si le v�hicule est utilis� en p�riode r�guli�re
	 * @param transportHeurePointe Vrai si le v�hicule est utilis� en heure de pointe
	 * @param transportEnCommun Vrai si le v�hicule est du transport en commun
	 * @param input La chaine de caract�re repr�sentant les variables
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
