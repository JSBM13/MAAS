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
			System.out.println(i);
			Trajet t = new Trajet(this, vehicule.getType() );
			
			Circuit circuit = circuits[i];
			int distanceDepart = -1;
			int pointDepart = -1;
			int distanceArrivee = -1;
			int pointArrivee = -1;
			
			for (int j = 0; j < circuit.arrets.length; j++ ) {
				System.out.println(i);
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
			t.setIntersections(produireTrajetCircuit(i, pointDepart, pointArrivee).getIntersections());
			t.makeReady();
			choix[i] = t;
		}
		
		
		return choix;
	}
	
	public Trajet produireTrajetCircuit(int indexCircuit, int arretDepart, int arretArrivee) {
		
		Circuit circuit = circuits[indexCircuit];
		Trajet t = new Trajet(this, circuit.vehicule.getType());
		
		try {
			
			int length = 0;
			if (arretArrivee < arretDepart) {
				length = (circuit.getNbArrets() - arretDepart + arretArrivee);
			} else {
				length = arretArrivee - arretDepart;
			}
			
			for (int i = arretDepart; i <= length + arretDepart; i++) {
				t.combine(trouverTrajet(circuit.getArret(i), circuit.getArret(i + 1), circuit.vehicule, t.getDirection()));
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
		
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		Trajet t = new Trajet(this, vehicule.getType(), depart);
		
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
		
		// Tant qu'on est pas rendu au point final, on roule cette boucle.
		while ((positionActuelle.getX() != arrivee.getX() || positionActuelle.getY() != arrivee.getY())) {
			
			// On essaie, en ordre, aller tout droit, puis � droite, puis � gauche.
			// Chaque fois, on v�rifie si ce d�placement nous rapprocherais de notre but. Si oui, on utilise cette direction. Autrement, on essaie la prochaine.
			for (int i = 0; i < 3; i++) {
				int[] rotation = {0,1,3};
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
		t.makeReady();
		t.setDirection(directionActuelle);
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
			if (deltaY > 0) return directions.sud;
			if (deltaY < 0) return directions.nord;
			
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
	
	@SuppressWarnings("serial")
	class UnequalNumberOfRoadsException extends Exception {
		Carte.orientations orientation;
		UnequalNumberOfRoadsException(Carte.orientations o, String msg) {
			super(msg);
			this.orientation = o;
		}
	}
}
