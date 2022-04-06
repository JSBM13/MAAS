/*
 * Classe Carte
 * 
 * Contient les informations à propos de la carte sur lequel le programme doit travailler, et les méthodes
 * relatives à ces informations.
 * 
 * 
 */

package main;

// Indique l'orientation d'une route.
enum orientations {
	verticale, horizontale
}

// Permet de distinguer les routes principales des routes secondaires.
enum typesRoute {
	principale, secondaire
}

// Indique un point cardinal dans l'espace.
enum directions {
	nord, est, sud, ouest, undefined
}

public class Carte {
	
	private int nbRoutesVerticales;						// Nombre de routes verticales (donc leur index est représenté par x) de la carte.
	private int nbRoutesHorizontales;					// Nombre de routes horizontales (donc leur index est représenté par y) de la carte.
	
	private int[] segmentsVerticaux;					// Longueur des segments (morceau de route entre deux intersections) verticaux.
	private int[] segmentsHorizontaux;					// Longueur des segments (morceau de route entre deux intersections) horizontaux.
	
	private typesRoute[] typeRoutesVerticales;			// Défini le type (principale ou secondaire) pour chacune des routes verticales.
	private typesRoute[] typeRoutesHorizontales;		// Défini le type (principale ou secondaire) pour chacune des routes horizontales.
	
	private Circuit[] circuits;							// L'ensemble des circuits de transport en commun de la carte.
	
	/**
	 * Détermine, pour chaque circuit de la carte, le trajet optimal pour embarquer aussi près que possible d'un
	 * point de départ et débarquer aussi près que possible d'un point d'arrivée.
	 * @param depart L'intersection de départ.
	 * @param arrivee L'intersection d'arrivée.
	 * @return Un array de Trajets, un pour chaque circuit de la carte.
	 */
	public Trajet[] trouverCircuits(Intersection depart, Intersection arrivee) {
		
		Trajet[] choix = new Trajet[circuits.length];
		
		// Pour chaque circuit...
		for (int i = 0; i < circuits.length; i++) {
			Circuit circuit = circuits[i];
			
			int distanceDepart = -1;
			int pointDepart = -1;
			int distanceArrivee = -1;
			int pointArrivee = -1;
			
			// Pour chacun des arrêts du circuit, on vérifie s'il est le plus proche du point de départ et du point d'arrivée.
			// À la fin, on obtient donc l'index de l'arrêt où on embarquera et celui où l'on débarquera.
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
			
			// On génère le trajet du véhicule qui commence à l'arrêt d'index pointDepart et qui termine à l'arrêt d'index pointArrivee.
			Trajet t = produireTrajetCircuit(i, pointDepart, pointArrivee);
			
			// On spécifie la distance entre notre point de départ et l'arrêt d'embarquement, et la distance entre notre point
			// d'arrivée et le point de débarquement. Ces informations permetteront de déterminer le meilleur trajet à prendre
			// plus tard, sans avoir besoin de le recalculer.
			t.setDistanceDepart(distanceDepart);
			t.setDistanceArrivee(distanceArrivee);
			
			// Maintenant qu'on a trouvé le trajet associé au circuit d'index i, on l'ajoute à notre Array qui sera retourné.
			choix[i] = t;
		}
		
		
		return choix;
	}
	
	/**
	 * Produit le trajet qu'emprunte le véhicule de transport en commun en commençant au points de départ et terminant au
	 * point d'arrivée spécifé, en passant par chacun des points intermédiaire du circuit.
	 * @param indexCircuit L'index du circuit.
	 * @param arretDepart L'index de l'arrêt du circuit où débute le trajet.
	 * @param arretArrivee L'index de l'arrêt du circuit où termine le trajet.
	 * @return Le trajet associé.
	 */
	public Trajet produireTrajetCircuit(int indexCircuit, int arretDepart, int arretArrivee) {
		
		Circuit circuit = circuits[indexCircuit];
		
		// Créé un circuit vide.
		Trajet t = new Trajet(this, circuit.vehicule);
		
		// Si le point de départ est le même que celui d'arrivé, on créé un trajet avec un seul point, pas besoin d'en faire plus.
		if (arretDepart == arretArrivee) {
			t.addIntersection(circuit.getArret(arretDepart));
			return t;
		}
		
		try {
			
			// Puisque le circuit est une boucle, il est possible que la valeur d'arretDepart soit plus grande qu'arretArrivee. Le
			// nombre de points à traverser est donc plus compliqué à trouver.
			int length = 0;
			if (arretArrivee < arretDepart) {
				length = (circuit.getNbArrets() - arretDepart + arretArrivee);
			} else {
				length = arretArrivee - arretDepart;
			}
			
			// Pour chaque arrêt entre le départ (inclut) et l'arrivée (exclu)
			for (int i = arretDepart; i < length + arretDepart; i++) {
				Intersection point1 = circuit.getArret(i);
				Intersection point2 = circuit.getArret(i + 1);
				
				// On vérifie si l'arrêt est directement après celui précédant. Si oui, on peut directement ajouter le prochain arrêt 
				// sans avoir à calculer le trajet à prendre pour y arriver. Autrement, on calcule un chemin entre les deux arrêts et on
				// l'ajoute à notre Trajet t.
				if (point1.delta(point2) == 1) {
					
					// Si le trajet t est vide, il faut ajouter le point de départ, qui n'est pas déjà inclus.
					if (t.getNbIntersections() == 0) t.addIntersection(point1);
					t.addIntersection(point2);
				} else {
					Trajet segment = trouverTrajet(point1, point2, circuit.vehicule, t.getDirection());
					t.combine(segment);
				}
				
			}
		} catch (Exception e) {
			// Ce bloc permet de rassurer Java, qui voit que combine() de Trajet peut produire une exception on tente de combiner deux
			// trajets qui ne commencent et finissent pas au même endroit. Ce n'est pas le cas ici.
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
			
		return t;
	}
	
	
	/**
	 * Détermine le trajet à prendre entre deux points.
	 * @param depart L'intersection où l'on débute
	 * @param arrivee L'intersection où l'on souhaite arriver
	 * @param vehicule Le type de véhicule à utiliser
	 * @param direction La direction initiale vers laquelle on navigue
	 * @return Un objet Trajet avec la liste des Intersections qu'on doit traverser pour se rendre au point.
	 * @throws Exception
	 */
	public Trajet trouverTrajet(Intersection depart, Intersection arrivee, ModeTransport vehicule, directions direction ) throws Exception {
		
		// On vérifie si les intersections demandées existent.
		if (!intersectionExiste(depart)) throw new Exception("L'intersection de départ n'existe pas. " + depart);
		if (!intersectionExiste(arrivee)) throw new Exception("L'intersection d'arrivée n'existe pas. " + arrivee);
		
		// On calcule la distance entre les deux points pour les deux axes.
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		Trajet t = new Trajet(this, vehicule, depart);
		
		// S'il n'y a aucun déplacement à faire, on va simplement renvoyer un trajet avec un seul point.
		if (deltaX == 0 && deltaY == 0) {
			return t;
		}
		
		directions directionActuelle = direction;
		Intersection positionActuelle = depart.clone();
		
		// Si on a pas de direction initiale, on va déterminer la direction optimale à prendre.
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
		
		// Permet de vérifier qu'on est pas dans une boucle infinie, ce qu'on espère n'arrivera pas!
		int loop = 0;
		
		// Tant qu'on est pas rendu au point final, on roule cette boucle. Donc, à chaque intersections... 
		while ((positionActuelle.getX() != arrivee.getX() || positionActuelle.getY() != arrivee.getY()) && loop++ < 100) {
			
			// On essaie, en ordre, aller tout droit, puis à droite, puis à gauche, puis finalement de reculer sur nos pas.
			// Chaque fois, on vérifie si ce déplacement nous rapprocherais de notre but. Si oui, on utilise cette direction. Autrement, on essaie la prochaine.
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
		
		if (loop >= 100) throw new Exception("Boucle infinie pour trouver trajet! Départ: " + depart + "; arrivée: " + arrivee);
		
		t.setDirection(directionActuelle);
		return t;
	}
	
	/**
	 * Détermine la direction dans laquelle on a voyagé. Ne s'applique que si les deux points sont sur une même ligne,
	 * ne devrait pas être utilisé pour déterminer s'il y a eu plus qu'un déplacement en ligne droite.
	 * @param depart Intersection initale
	 * @param arrivee Intersection finale
	 * @return La direction dans laquelle on a voyagé.
	 */
	public directions trouverDirection(Intersection depart, Intersection arrivee) {
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
	 * Détermine si l'Intersection aux coordonnées fournies existe sur notre Carte.
	 * @param x La coordonnée x de la route
	 * @param y La coordonnée y de la route
	 * @return Vrai si l'intersection existe dans notre carte.
	 */
	public boolean intersectionExiste(int x, int y) {
		return (x < nbRoutesVerticales && y < nbRoutesHorizontales && x >= 0 && y >= 0);
	}
	
	/**
	 * Détermine si l'Intersection fournie correspond à une véritable intersection dans notre Carte.
	 * @param intersection L'Intersection à vérifier.
	 * @return Vrai si l'intersection existe dans notre carte.
	 */
	public boolean intersectionExiste(Intersection intersection) {
		return intersectionExiste(intersection.getX(), intersection.getY());
	}

	/**
	 * Fournie une direction qui est le résultat d'une rotation vers la droite/sens horaire d'un certain nombre de pas.
	 * @param initial La direction originale
	 * @param rotation Le nombre de rotation à effectuer
	 * @return La nouvelle direction
	 */
	public directions rotateDirection(directions initial, int rotation) {
		return (directionFromInt(Math.abs(initial.ordinal() + rotation) % 4));
		
	}
	
	/**
	 * Fournie une direction selon un nombre entier, où le Nord équivaut à 0, est à 1, sud à 2 et ouest à 3.
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
	
	// =================================================
	// ================= Getters/Setters ===============
	// =================================================
	
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
	
	public Circuit[] getCircuits() {
		return circuits;
	}

	public void setCircuits(Circuit[] circuits) {
		this.circuits = circuits;
	}
	
	
	// ===============================================
	// ================ Constructeurs ================
	// ===============================================
	
	
	/**
	 * Construit une carte avec les paramètres individuels.
	 * Chaque route de la carte possède un type parmi l'enum typesRoute. Les routes de même directions partagent la longueur de leurs segments.
	 * @param nbRoutesVerticales Nombre de routes verticales dans la carte
	 * @param nbRoutesHorizontales Nombre de routes horizontales dans la carte
	 * @param segmentsVerticaux Array d'entiers représentant, en mètres, la longueur des segments de chaques routes verticales. Doit être de longueur (nbRoutesHorizontales - 1).
	 * @param segmentsHorizontaux Array d'entiers représentant, en mètres, la longueur des segments de chaques routes horizontales. Doit être de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesVerticales Array de typesRoute représentant le type de chaque route horizontale de la carte. Doit être de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesHorizontales Array de typesRoute représentant le type de chaque route verticales de la carte. Doit être de longueur (nbRoutesHorizontales - 1).
	 * @throws UnequalNumberOfRoadsException Si le nombre de routes fournis est inconstant entre les différents paramètres. Contient un attribut Orientation pour savoir quelle orientation est incorrecte. 
	 */
	public Carte(int nbRoutesVerticales, int nbRoutesHorizontales, 
					int[] segmentsVerticaux, int[] segmentsHorizontaux,
					typesRoute[] typeRoutesVerticales, typesRoute[] typeRoutesHorizontales) throws UnequalNumberOfRoadsException {
		
		// Vérifie si le nombre de routes verticales est correct.
		if (segmentsVerticaux.length != nbRoutesHorizontales - 1 || typeRoutesVerticales.length != nbRoutesVerticales) {
			throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de routes ne correspond pas au nombre de segments ou de types fournis.");
		}
		
		// Vérifie si le nombre de routes horizontales est correct.
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
	 * Construit une carte à partir de quatres chaines de caractères, représentant respectivement les distances des routes verticales et horizontales et le type des routes verticales et horizontales.
	 * Le format est celui généré par la fenêtre ParamMAAS.
	 * Le format accepté pour les distances est "50,50,50", soit une liste d'entiers séparés par une virgule. Les distances son en mètres.
	 * Le format accepté pour les types de routes est "PSPS", où P représente une route principale et S une route secondaire.
	 * @throws Exception
	 * @throws UnequalNumberOfRoadsException Si le nombre de routes fournis est inconstant entre les différents paramètres. Contient un attribut Orientation pour savoir quelle orientation est incorrecte. 
	 */
	public Carte(String inputDistancesVerticales, String inputDistancesHorizontales, String inputTypesVerticaux, String inputTypesHorizontaux) throws Exception, UnequalNumberOfRoadsException{
		String[] distancesVerticales = inputDistancesVerticales.split(",");
		String[] distancesHorizontales = inputDistancesHorizontales.split(",");
		
		// Si le nombre de routes est correct...
		if (distancesVerticales.length + 1 == inputTypesVerticaux.length()) {
			if (distancesHorizontales.length + 1 == inputTypesHorizontaux.length()) {
				
				// ... Construit la carte.
				this.nbRoutesVerticales = inputTypesVerticaux.length();
				this.nbRoutesHorizontales = inputTypesHorizontaux.length();
				this.segmentsVerticaux = parseIntArray(distancesVerticales);
				this.segmentsHorizontaux = parseIntArray(distancesHorizontales);
				this.typeRoutesVerticales = parseTypesRoute(inputTypesVerticaux);
				this.typeRoutesHorizontales = parseTypesRoute(inputTypesHorizontaux);
				
			} else {
				throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de données présentes dans le tableau des distances horizontales et celui des types verticaux ne correspondent pas.");
			}
		} else {
			throw new UnequalNumberOfRoadsException(orientations.horizontale, "Le nombre de données présentes dans le tableau des distances verticales et celui des types horizontaux ne correspondent pas.");
		}
		
	}
	
	/**
	 * Fonction utilitaire pour parseRoutes(); permet de transformer un tableau de String représentant des entiers en un tableau d'entiers.
	 * @param inputs Le tableau de chaînes de caractères à transformer
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
	 * Fonction utilitaire pour parseRoutes(). Prend une chaine de caractères représentant des routes, sous la forme de caractères 'P' pour principales et 'S' pour secondaires, et renvoie
	 * le tableau de types de route correspondant.
	 * @param input La chaine de caractère représentant le types de routes
	 * @return Le tableau de typesRoute correspondant.
	 * @throws Exception Si une lettre invalide est insérée dans l'entrée.
	 */
	public typesRoute[] parseTypesRoute(String input) throws Exception {
		typesRoute[] values = new typesRoute[input.length()];
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == 'P') values[i] = typesRoute.principale;
			else if (input.charAt(i) == 'S') values[i] = typesRoute.secondaire;
			else throw new Exception("Entrée invalide, la lettre '" + input.charAt(i) + "' n'est pas un type de routes.");
		}
		return values;
	}
	
	/**
	 * Exception générée par le constructeur de Carte si le nombre de routes fournis par les constructeurs ne correspondent pas.
	 * Possède l'attribut orientation pour savoir quelle des deux liste de routes a fait défaut.
	 * @author JS
	 */
	@SuppressWarnings("serial")
	class UnequalNumberOfRoadsException extends Exception {
		orientations orientation;
		UnequalNumberOfRoadsException(orientations o, String msg) {
			super(msg);
			this.orientation = o;
		}
	}
}
