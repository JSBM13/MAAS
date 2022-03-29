package main;

import main.typesIntersection;
import main.ModeTransport;

public class Carte {

	enum orientations {
		verticale, horizontale
	}
	
	enum typesRoute {
		principale, secondaire
	}
	
	enum directions {
		nord, est, sud, ouest, undefined
	}
	
	
	private int nbRoutesVerticales;
	private int nbRoutesHorizontales;
	
	private int[] segmentsVerticaux;
	private int[] segmentsHorizontaux;
	
	private typesRoute[] typeRoutesHorizontales;
	private typesRoute[] typeRoutesVerticales;
	
	private Circuit[] circuits;
	
	/**
	 * Constructeur de la carte.
	 * Chaque route de la carte poss�de un type parmi l'enum typesRoute. Les routes de m�me directions partagent la longueur de leurs segments.
	 * @param nbRoutesVerticales Nombre de routes verticales dans la carte
	 * @param nbRoutesHorizontales Nombre de routes horizontales dans la carte
	 * @param segmentsVerticaux Array d'entiers repr�sentant, en m�tres, la longueur des segments de chaques routes verticales. Doit �tre de longueur (nbRoutesHorizontales - 1).
	 * @param segmentsHorizontaux Array d'entiers repr�sentant, en m�tres, la longueur des segments de chaques routes horizontales. Doit �tre de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesVerticales Array de typesRoute repr�sentant le type de chaque route horizontale de la carte. Doit �tre de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesHorizontales Array de typesRoute repr�sentant le type de chaque route verticales de la carte. Doit �tre de longueur (nbRoutesHorizontales - 1).
	 * @throws UnequalNumberOfRoadsException R�cup�rer l'orientation de l'erreur avec le param�tre orientation de l'exception.
	 */
	public Carte(int nbRoutesVerticales, int nbRoutesHorizontales, 
					int[] segmentsVerticaux, int[] segmentsHorizontaux,
					typesRoute[] typeRoutesVerticales, typesRoute[] typeRoutesHorizontales) throws UnequalNumberOfRoadsException {
		
		if (segmentsVerticaux.length != nbRoutesHorizontales - 1 || typeRoutesVerticales.length != nbRoutesVerticales) {
			throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de routes ne correspond pas au nombre de segments ou de types fournis.");
		}
		if (segmentsHorizontaux.length != nbRoutesVerticales - 1 || typeRoutesHorizontales.length != nbRoutesHorizontales) {
			throw new UnequalNumberOfRoadsException(orientations.horizontale, "Le nombre de routes ne correspond pas au nombre de segments ou de types fournis.");
		}
		
		this.nbRoutesVerticales = nbRoutesVerticales;
		this.nbRoutesHorizontales = nbRoutesHorizontales;
		this.segmentsVerticaux = segmentsVerticaux;
		this.segmentsHorizontaux = segmentsHorizontaux;
		this.typeRoutesVerticales = typeRoutesVerticales;
		this.typeRoutesHorizontales = typeRoutesHorizontales;
	}
	
	/**
	 * Cr��e une carte � partir de quatres chaines de caract�res, repr�sentant respectivement les distances des routes verticales et horizontales et le type des routes verticales et horizontales.
	 * Le format accept� pour les distances est "50,50,50", soit une liste d'entiers s�par�s par une virgule. Les distances son en m�tres.
	 * Le format accept� pour les types de routes est "PSPS", o� P repr�sente une route principale et S une route secondaire.
	 */
	public Carte(String inputDistancesVerticales, String inputDistancesHorizontales, String inputTypesVerticaux, String inputTypesHorizontaux) throws Exception, UnequalNumberOfRoadsException{
		String[] distancesVerticales = inputDistancesVerticales.split(",");
		String[] distancesHorizontales = inputDistancesHorizontales.split(",");
		
		// Si le nombre de donn�es est correct...
		if (distancesVerticales.length + 1 == inputTypesHorizontaux.length()) {
			if (distancesHorizontales.length + 1 == inputTypesVerticaux.length()) {
				
				this.nbRoutesVerticales = inputTypesVerticaux.length();
				this.nbRoutesHorizontales = inputTypesHorizontaux.length();
				this.segmentsVerticaux = parseIntArray(distancesVerticales);
				this.segmentsHorizontaux = parseIntArray(distancesHorizontales);
				this.typeRoutesVerticales = parseTypesRoute(inputTypesVerticaux);
				this.typeRoutesHorizontales = parseTypesRoute(inputTypesHorizontaux);
				
			} else {
				throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de donn�es pr�sentes dans le tableau des distances horizontales et celui des types verticaux ne correspondent pas.");
			}
		} else {
			throw new UnequalNumberOfRoadsException(orientations.horizontale, "Le nombre de donn�es pr�sentes dans le tableau des distances verticales et celui des types horizontaux ne correspondent pas.");
		}
		
	}
	
	public Itineraire choisirTrajets(Intersection depart, Intersection arrivee, ModeTransport vehicule) throws Exception {
		Itineraire itin = new Itineraire("Autobus", "autobus");
		
		Trajet[] choix = trouverCircuits(depart, arrivee, vehicule);
		
		// On s�lectionne le trajet qui minimise la distance � marcher depuis et vers les arr�ts.
		// S'il y a une �galit�, on prend celui qui demande le moins de temps pour le voyage.
		int minimumDistanceArrets = -1;
		int minimumDistanceTrajet = -1;
		int indexMeilleur = -1;
		
		for (int i = 0; i < choix.length; i++) {
			int distanceDepart = choix[i].getDistanceDepart();
			int distanceArrivee = choix[i].getDistanceArrivee();
			int tempsTrajet = choix[i].calculateTemps();
			System.out.println("Trajet %d: distanceDepart=%d, distanceArrivee=%d, tempsTrajet=%d; %s".formatted(
					i, distanceDepart, distanceArrivee, tempsTrajet, choix[i]));
			
			if (distanceDepart + distanceArrivee < minimumDistanceArrets || minimumDistanceArrets == -1) {
				minimumDistanceArrets = distanceDepart + distanceArrivee;
				minimumDistanceTrajet = tempsTrajet;
				indexMeilleur = i;
				
			} else if (distanceDepart + distanceArrivee == minimumDistanceArrets && tempsTrajet < minimumDistanceTrajet) {
				minimumDistanceArrets = distanceDepart + distanceArrivee;
				minimumDistanceTrajet = tempsTrajet;
				indexMeilleur = i;
			}
		}
		
		// Ajout � l'itin�raire de la marche entre l'intersection actuelle et celle du premier arr�t, si besoin.
		if (choix[indexMeilleur].getDistanceDepart() > 0) {
			itin.addTrajet(trouverTrajet(depart, choix[indexMeilleur].getDepart(), vehicule, directions.undefined));
		}
		// Ajout du trajet en transport en commun
		itin.addTrajet(choix[indexMeilleur]);
		// AJout de la marche entre le dernier arr�t et la destination, si besoin.
		if (choix[indexMeilleur].getDistanceArrivee() > 0) {
			itin.addTrajet(trouverTrajet(choix[indexMeilleur].getDestination(), arrivee, vehicule, directions.undefined));
		}
		
		itin.setNom("Autobus circuit " + indexMeilleur);
		return itin;
	}
	
	/**
	 * D�termine, pour chaque circuit de la carte, le trajet optimal pour embarquer aussi pr�s que possible d'un
	 * point de d�part et d�barquer aussi pr�s que possible d'un point d'arriv�e.
	 * @param depart L'intersection de d�part.
	 * @param arrivee L'intersection d'arriv�e.
	 * @param vehicule Le type de v�hicule utilis�.
	 * @return Un array de Trajets, un pour chaque circuits de la carte.
	 */
	public Trajet[] trouverCircuits(Intersection depart, Intersection arrivee, ModeTransport vehicule) {
		
		Trajet[] choix = new Trajet[circuits.length];
		
		for (int i = 0; i < circuits.length; i++) {
			Trajet t = new Trajet(this, vehicule);
			
			Circuit circuit = circuits[i];
			int distanceDepart = -1;
			int pointDepart = -1;
			int distanceArrivee = -1;
			int pointArrivee = -1;
			
			for (int j = 0; j < circuit.arrets.length; j++ ) {
				Intersection arret = circuit.arrets[j];
				int arretDistanceDepart = depart.delta(arret);
				int arretDistanceArrivee = arrivee.delta(arret);
				if (arretDistanceDepart < distanceDepart || distanceDepart == -1) {
					distanceDepart = arretDistanceDepart;
					pointDepart = j;
				}
				if (arretDistanceArrivee < distanceArrivee || distanceArrivee == -1) {
					distanceArrivee = arretDistanceArrivee;
					pointArrivee = j;
				}
			}
			System.out.println("Pour circuit " + i + ": " + pointDepart + ", " + pointArrivee);
			t.setIntersections(produireTrajetCircuit(i, pointDepart, pointArrivee).getIntersections());
			t.makeReady();
			t.setDistanceDepart(distanceDepart);
			t.setDistanceArrivee(distanceArrivee);
			choix[i] = t;
		}
		
		
		return choix;
	}
	
	public Trajet produireTrajetCircuit(int indexCircuit, int arretDepart, int arretArrivee) {
		
		Circuit circuit = circuits[indexCircuit];
		Trajet t = new Trajet(this, circuit.vehicule);
		
		try {
			
			int length = 0;
			if (arretArrivee < arretDepart) {
				length = (circuit.getNbArrets() - arretDepart + arretArrivee);
			} else {
				length = arretArrivee - arretDepart;
			}
			for (int i = arretDepart; i < length + arretDepart; i++) {
				Intersection point1 = circuit.getArret(i);
				Intersection point2 = circuit.getArret(i + 1);
				if (point1.delta(point2) == 1) {
					if (t.getNbIntersections() == 0) t.addIntersection(point1);
					t.addIntersection(point2);
				} else {
					Trajet segment = trouverTrajet(point1, point2, circuit.vehicule, t.getDirection());
					t.combine(segment);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
			
		return t;
	}
	
	
	
	/**
	 * D�termine le trajet � prendre entre deux points.
	 * @param depart L'intersection o� l'on d�bute
	 * @param arrivee L'intersection o� l'on souhaite arriver
	 * @param vehicule Le type de v�hicule � utiliser
	 * @param direction La direction initiale vers laquelle on navigue
	 * @return Un objet Trajet avec la liste des Intersections qu'on doit traverser pour se rendre au point.
	 * @throws Exception
	 */
	public Trajet trouverTrajet(Intersection depart, Intersection arrivee, ModeTransport vehicule, directions direction ) throws Exception {
		
		if (!intersectionExiste(depart)) throw new Exception("L'intersection de d�part n'existe pas. " + depart);
		if (!intersectionExiste(arrivee)) throw new Exception("L'intersection d'arriv�e n'existe pas. " + arrivee);
		
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		Trajet t = new Trajet(this, vehicule, depart);
		
		// S'il n'y a aucun d�placement � faire, on va simplement renvoyer un trajet avec un seul point.
		if (deltaX == 0 && deltaY == 0) {
			return t;
		}
		
		directions directionActuelle = direction;
		Intersection positionActuelle = depart.clone();
		
		// Si on a pas de direction initiale, on va d�terminer la direction optimale.
		if (directionActuelle == directions.undefined) {
			if (Math.abs(deltaX) >= Math.abs(deltaY)) {
				if (deltaX > 0) {
					directionActuelle = directions.est;
				} else {
					directionActuelle = directions.ouest;
				}
			} else {
				if (deltaY > 0) {
					directionActuelle = directions.sud;
				} else {
					directionActuelle = directions.nord;
				}
			}
		}
		
		int loop = 0;
		
		// Tant qu'on est pas rendu au point final, on roule cette boucle.
		while ((positionActuelle.getX() != arrivee.getX() || positionActuelle.getY() != arrivee.getY()) && loop++ < 100) {
			
			// On essaie, en ordre, aller tout droit, puis � droite, puis � gauche.
			// Chaque fois, on v�rifie si ce d�placement nous rapprocherais de notre but. Si oui, on utilise cette direction. Autrement, on essaie la prochaine.
			for (int i = 0; i < 4; i++) {
				int[] rotation = {0,1,3,2};
				directions testDirection = rotateDirection(directionActuelle, rotation[i]);
				Intersection testProchaineInter = positionActuelle.movedFrom(testDirection, 1);
				if (intersectionExiste(testProchaineInter) && (testProchaineInter.delta(arrivee) < positionActuelle.delta(arrivee))) {
					positionActuelle = testProchaineInter;
					directionActuelle = testDirection;
					t.addIntersection(positionActuelle);
					break;
				}
			}
		}
		if (loop >= 100) throw new Exception("Boucle infinie pour trouver trajet! D�part: " + depart + "; arriv�e: " + arrivee);
		t.makeReady();
		t.setDirection(directionActuelle);
		//System.out.println("Trajet trouv� entre " + depart + " et " + arrivee + ": " + t);
		return t;
	}
	
	/**
	 * D�termine la direction dans laquelle on a voyag�. Ne s'applique que si les deux points sont sur une m�me ligne,
	 * ne devrait pas �tre utilis� pour d�terminer s'il y a eu plus qu'un d�placement en ligne droite.
	 * @param depart Intersection initale
	 * @param arrivee Intersection finale
	 * @return La direction dans laquelle on a voyag�.
	 */
	public directions findDirection(Intersection depart, Intersection arrivee) {
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		if (deltaX == 0) {
			if (deltaY < 0) return directions.sud;
			if (deltaY > 0) return directions.nord;
			
		} else if (deltaY == 0) {
			if (deltaX > 0) return directions.est;
			if (deltaX < 0) return directions.ouest;
		}
		return directions.undefined;
	}
	
	/**
	 * D�termine si l'Intersection aux coordonn�es fournies existe sur notre Carte.
	 * @param x La coordonn�e x de la route
	 * @param y La coordonn�e y de la route
	 * @return Vrai si l'intersection existe dans notre carte.
	 */
	public boolean intersectionExiste(int x, int y) {
		return (x < nbRoutesVerticales && y < nbRoutesHorizontales && x >= 0 && y >= 0);
	}
	
	/**
	 * D�termine si l'Intersection fournie correspond � une v�ritable intersection dans notre Carte.
	 * @param intersection L'Intersection � v�rifier.
	 * @return Vrai si l'intersection existe dans notre carte.
	 */
	public boolean intersectionExiste(Intersection intersection) {
		return intersectionExiste(intersection.getX(), intersection.getY());
	}
	
	public Circuit[] getCircuits() {
		return circuits;
	}

	public void setCircuits(Circuit[] circuits) {
		this.circuits = circuits;
	}

	/**
	 * Fournie une direction qui est le r�sultat d'une rotation vers la droite/sens horaire d'un certain nombre de pas.
	 * @param initial La direction originale
	 * @param rotation Le nombre de rotation � effectuer
	 * @return La nouvelle direction
	 */
	public directions rotateDirection(directions initial, int rotation) {
		return (directionFromInt(Math.abs(initial.ordinal() + rotation) % 4));
		
	}
	
	/**
	 * Fournie une direction selon un nombre entier, o� le Nord �quivaut � 0, est � 1, sud � 2 et ouest � 3.
	 * Toutes les autres valeurs retourneront la direction "undefined".
	 */
	public directions directionFromInt(int i) {
		switch (i) {
		case 0: return directions.nord;
		case 1: return directions.est;
		case 2: return directions.sud;
		case 3: return directions.ouest;
		default: return directions.undefined;
		}
	}
	
	public int getNbRoutesVerticales() {
		return this.nbRoutesVerticales;
	}
	
	public int getNbRoutesHorizontales() {
		return this.nbRoutesHorizontales;
	}
	
	public int[] getSegmentsVerticaux() {
		return segmentsVerticaux;
	}
	
	public int[] getSegmentsHorizontaux() {
		return segmentsHorizontaux;
	}
	
	public int getSegmentsVerticaux(int i) {
		return segmentsVerticaux[i];
	}
	
	public int getSegmentsHorizontaux(int i) {
		return segmentsHorizontaux[i];
	} 
	
	public typesRoute[] getTypeRoute(orientations orientation) {
		if (orientation == orientations.verticale) {
			return typeRoutesVerticales;
		} else {
			return typeRoutesHorizontales;
		}
	}
	
	public typesRoute getTypeRoute(orientations orientation, int index) {
		if (orientation == orientations.verticale) {
			return typeRoutesVerticales[index];
		} else {
			return typeRoutesHorizontales[index];
		}
	}
	
	public typesIntersection getTypeIntersection(int routeVerticale, int routeHorizonale) {
		if (typeRoutesVerticales[routeVerticale] == typesRoute.principale &&  typeRoutesHorizontales[routeHorizonale] == typesRoute.principale) {
			return typesIntersection.PrincPrinc;
		} else if (typeRoutesVerticales[routeVerticale] == typesRoute.secondaire &&  typeRoutesHorizontales[routeHorizonale] == typesRoute.secondaire) {
			return typesIntersection.SecSec;
		} else {
			return typesIntersection.PrincSec;
		}
	}
	
	public typesIntersection getTypeIntersection(Intersection intersection) {
		return getTypeIntersection(intersection.getX(), intersection.getY());
	}
	

	
	/**
	 * Fonction utilitaire pour parseRoutes(); permet de transformer un tableau de String repr�sentant des entiers en un tableau d'entiers.
	 * @param inputs Le tableau de cha�nes de caract�res � transformer
	 * @return Le tableau d'entier correspondant.
	 */
	public int[] parseIntArray(String[] inputs) {
		int[] values = new int[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			values[i] = Integer.parseInt(inputs[i]);
		}
		return values;
	}
	
	/**
	 * Fonction utilitaire pour parseRoutes(). Prend une chaine de caract�res repr�sentant des routes, sous la forme de caract�res 'P' pour principales et 'S' pour secondaires, et renvoie
	 * le tableau de types de route correspondant.
	 * @param input La chaine de caract�re repr�sentant le types de routes
	 * @return Le tableau de typesRoute correspondant.
	 * @throws Exception Si une lettre invalide est ins�r�e dans l'entr�e.
	 */
	public typesRoute[] parseTypesRoute(String input) throws Exception {
		typesRoute[] values = new typesRoute[input.length()];
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == 'P') values[i] = typesRoute.principale;
			else if (input.charAt(i) == 'S') values[i] = typesRoute.secondaire;
			else throw new Exception("Entr�e invalide, la lettre '" + input.charAt(i) + "' n'est pas un type de routes.");
		}
		return values;
	}
	
	@SuppressWarnings("serial")
	class UnequalNumberOfRoadsException extends Exception {
		Carte.orientations orientation;
		UnequalNumberOfRoadsException(Carte.orientations o, String msg) {
			super(msg);
			this.orientation = o;
		}
	}
}
