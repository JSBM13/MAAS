package main;

import main.typesIntersection;

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
	
	private Circuit circuits;
	
	/**
	 * Constructeur de la carte.
	 * Chaque route de la carte possède un type parmi l'enum typesRoute. Les routes de même directions partagent la longueur de leurs segments.
	 * @param nbRoutesVerticales Nombre de routes verticales dans la carte
	 * @param nbRoutesHorizontales Nombre de routes horizontales dans la carte
	 * @param segmentsVerticaux Array d'entiers représentant, en mètres, la longueur des segments de chaques routes verticales. Doit être de longueur (nbRoutesHorizontales - 1).
	 * @param segmentsHorizontaux Array d'entiers représentant, en mètres, la longueur des segments de chaques routes horizontales. Doit être de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesVerticales Array de typesRoute représentant le type de chaque route horizontale de la carte. Doit être de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesHorizontales Array de typesRoute représentant le type de chaque route verticales de la carte. Doit être de longueur (nbRoutesHorizontales - 1).
	 * @throws UnequalNumberOfRoadsException Récupérer l'orientation de l'erreur avec le paramètre orientation de l'exception.
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
	
	public Trajet trouverTrajet(Intersection depart, Intersection arrivee, ModeTransport vehicule, directions direction ) throws Exception {
		
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		Trajet t = new Trajet(vehicule.getType(), depart);
		
		// S'il n'y a aucun déplacement à faire, on va simplement renvoyer un trajet avec un seul point.
		if (deltaX == 0 && deltaY == 0) {
			return t;
		}
		
		directions directionActuelle = direction;
		Intersection positionActuelle = depart.clone();
		
		// Si on a pas de direction initiale, on va déterminer la direction optimale.
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
		
		System.out.println(directionActuelle);
		System.out.println(positionActuelle);
		
		// Tant qu'on est pas rendu au point final, on roule cette boucle.
		while ((positionActuelle.getX() != arrivee.getX() || positionActuelle.getY() != arrivee.getY()) && ++loop < 100) {
			
			// On essaie, en ordre, aller tout droit, puis à droite, puis à gauche.
			// Chaque fois, on vérifie si ce déplacement nous rapprocherais de notre but. Si oui, on utilise cette direction. Autrement, on essaie la prochaine.
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
		
		if (loop == 100) throw new Exception("boucle infinie!");

		
		
		return t;
	}
	
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
	
	public boolean routeExiste(int x, int y) {
		return (x < nbRoutesVerticales && y < nbRoutesHorizontales && x >= 0 && y >= 0);
	}
	
	public boolean intersectionExiste(Intersection intersection) {
		return routeExiste(intersection.getX(), intersection.getY());
	}
	
	public directions rotateDirection(directions initial, int rotation) throws Exception {
		switch (Math.abs(initial.ordinal() + rotation) % 4) {
		case 0: return directions.nord;
		case 1: return directions.est;
		case 2: return directions.sud;
		case 3: return directions.ouest;
		default: throw new Exception("rotateDirection a donné une valeur autre que 0 à 3, JS, t'as mal codé ton affaire!");
		}
	}
	
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
	
	@SuppressWarnings("serial")
	class UnequalNumberOfRoadsException extends Exception {
		Carte.orientations orientation;
		UnequalNumberOfRoadsException(Carte.orientations o, String msg) {
			super(msg);
			this.orientation = o;
		}
	}
	
}
