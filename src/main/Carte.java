/*
 * Classe Carte
 * 
 * Contient les informations � propos de la carte sur lequel le programme doit travailler, et les m�thodes
 * relatives � ces informations.
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
	
	private int nbRoutesVerticales;						// Nombre de routes verticales (donc leur index est repr�sent� par x) de la carte.
	private int nbRoutesHorizontales;					// Nombre de routes horizontales (donc leur index est repr�sent� par y) de la carte.
	
	private int[] segmentsVerticaux;					// Longueur des segments (morceau de route entre deux intersections) verticaux.
	private int[] segmentsHorizontaux;					// Longueur des segments (morceau de route entre deux intersections) horizontaux.
	
	private typesRoute[] typeRoutesVerticales;			// D�fini le type (principale ou secondaire) pour chacune des routes verticales.
	private typesRoute[] typeRoutesHorizontales;		// D�fini le type (principale ou secondaire) pour chacune des routes horizontales.
	
	private Circuit[] circuits;							// L'ensemble des circuits de transport en commun de la carte.
	
	/**
	 * D�termine, pour chaque circuit de la carte, le trajet optimal pour embarquer aussi pr�s que possible d'un
	 * point de d�part et d�barquer aussi pr�s que possible d'un point d'arriv�e.
	 * @param depart L'intersection de d�part.
	 * @param arrivee L'intersection d'arriv�e.
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
			
			// Pour chacun des arr�ts du circuit, on v�rifie s'il est le plus proche du point de d�part et du point d'arriv�e.
			// � la fin, on obtient donc l'index de l'arr�t o� on embarquera et celui o� l'on d�barquera.
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
			
			// On g�n�re le trajet du v�hicule qui commence � l'arr�t d'index pointDepart et qui termine � l'arr�t d'index pointArrivee.
			Trajet t = produireTrajetCircuit(i, pointDepart, pointArrivee);
			
			// On sp�cifie la distance entre notre point de d�part et l'arr�t d'embarquement, et la distance entre notre point
			// d'arriv�e et le point de d�barquement. Ces informations permetteront de d�terminer le meilleur trajet � prendre
			// plus tard, sans avoir besoin de le recalculer.
			t.setDistanceDepart(distanceDepart);
			t.setDistanceArrivee(distanceArrivee);
			
			// Maintenant qu'on a trouv� le trajet associ� au circuit d'index i, on l'ajoute � notre Array qui sera retourn�.
			choix[i] = t;
		}
		
		
		return choix;
	}
	
	/**
	 * Produit le trajet qu'emprunte le v�hicule de transport en commun en commen�ant au points de d�part et terminant au
	 * point d'arriv�e sp�cif�, en passant par chacun des points interm�diaire du circuit.
	 * @param indexCircuit L'index du circuit.
	 * @param arretDepart L'index de l'arr�t du circuit o� d�bute le trajet.
	 * @param arretArrivee L'index de l'arr�t du circuit o� termine le trajet.
	 * @return Le trajet associ�.
	 */
	public Trajet produireTrajetCircuit(int indexCircuit, int arretDepart, int arretArrivee) {
		
		Circuit circuit = circuits[indexCircuit];
		
		// Cr�� un circuit vide.
		Trajet t = new Trajet(this, circuit.vehicule);
		
		// Si le point de d�part est le m�me que celui d'arriv�, on cr�� un trajet avec un seul point, pas besoin d'en faire plus.
		if (arretDepart == arretArrivee) {
			t.addIntersection(circuit.getArret(arretDepart));
			return t;
		}
		
		try {
			
			// Puisque le circuit est une boucle, il est possible que la valeur d'arretDepart soit plus grande qu'arretArrivee. Le
			// nombre de points � traverser est donc plus compliqu� � trouver.
			int length = 0;
			if (arretArrivee < arretDepart) {
				length = (circuit.getNbArrets() - arretDepart + arretArrivee);
			} else {
				length = arretArrivee - arretDepart;
			}
			
			// Pour chaque arr�t entre le d�part (inclut) et l'arriv�e (exclu)
			for (int i = arretDepart; i < length + arretDepart; i++) {
				Intersection point1 = circuit.getArret(i);
				Intersection point2 = circuit.getArret(i + 1);
				
				// On v�rifie si l'arr�t est directement apr�s celui pr�c�dant. Si oui, on peut directement ajouter le prochain arr�t 
				// sans avoir � calculer le trajet � prendre pour y arriver. Autrement, on calcule un chemin entre les deux arr�ts et on
				// l'ajoute � notre Trajet t.
				if (point1.delta(point2) == 1) {
					
					// Si le trajet t est vide, il faut ajouter le point de d�part, qui n'est pas d�j� inclus.
					if (t.getNbIntersections() == 0) t.addIntersection(point1);
					t.addIntersection(point2);
				} else {
					Trajet segment = trouverTrajet(point1, point2, circuit.vehicule, t.getDirection());
					t.combine(segment);
				}
				
			}
		} catch (Exception e) {
			// Ce bloc permet de rassurer Java, qui voit que combine() de Trajet peut produire une exception on tente de combiner deux
			// trajets qui ne commencent et finissent pas au m�me endroit. Ce n'est pas le cas ici.
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
		
		// On v�rifie si les intersections demand�es existent.
		if (!intersectionExiste(depart)) throw new Exception("L'intersection de d�part n'existe pas. " + depart);
		if (!intersectionExiste(arrivee)) throw new Exception("L'intersection d'arriv�e n'existe pas. " + arrivee);
		
		// On calcule la distance entre les deux points pour les deux axes.
		int deltaX = arrivee.getX() - depart.getX();
		int deltaY = arrivee.getY() - depart.getY();
		
		Trajet t = new Trajet(this, vehicule, depart);
		
		// S'il n'y a aucun d�placement � faire, on va simplement renvoyer un trajet avec un seul point.
		if (deltaX == 0 && deltaY == 0) {
			return t;
		}
		
		directions directionActuelle = direction;
		Intersection positionActuelle = depart.clone();
		
		// Si on a pas de direction initiale, on va d�terminer la direction optimale � prendre.
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
		
		// Permet de v�rifier qu'on est pas dans une boucle infinie, ce qu'on esp�re n'arrivera pas!
		int loop = 0;
		
		// Tant qu'on est pas rendu au point final, on roule cette boucle. Donc, � chaque intersections... 
		while ((positionActuelle.getX() != arrivee.getX() || positionActuelle.getY() != arrivee.getY()) && loop++ < 100) {
			
			// On essaie, en ordre, aller tout droit, puis � droite, puis � gauche, puis finalement de reculer sur nos pas.
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
	 * Construit une carte avec les param�tres individuels.
	 * Chaque route de la carte poss�de un type parmi l'enum typesRoute. Les routes de m�me directions partagent la longueur de leurs segments.
	 * @param nbRoutesVerticales Nombre de routes verticales dans la carte
	 * @param nbRoutesHorizontales Nombre de routes horizontales dans la carte
	 * @param segmentsVerticaux Array d'entiers repr�sentant, en m�tres, la longueur des segments de chaques routes verticales. Doit �tre de longueur (nbRoutesHorizontales - 1).
	 * @param segmentsHorizontaux Array d'entiers repr�sentant, en m�tres, la longueur des segments de chaques routes horizontales. Doit �tre de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesVerticales Array de typesRoute repr�sentant le type de chaque route horizontale de la carte. Doit �tre de longueur (nbRoutesVerticales - 1)
	 * @param typeRoutesHorizontales Array de typesRoute repr�sentant le type de chaque route verticales de la carte. Doit �tre de longueur (nbRoutesHorizontales - 1).
	 * @throws UnequalNumberOfRoadsException Si le nombre de routes fournis est inconstant entre les diff�rents param�tres. Contient un attribut Orientation pour savoir quelle orientation est incorrecte. 
	 */
	public Carte(int nbRoutesVerticales, int nbRoutesHorizontales, 
					int[] segmentsVerticaux, int[] segmentsHorizontaux,
					typesRoute[] typeRoutesVerticales, typesRoute[] typeRoutesHorizontales) throws UnequalNumberOfRoadsException {
		
		// V�rifie si le nombre de routes verticales est correct.
		if (segmentsVerticaux.length != nbRoutesHorizontales - 1 || typeRoutesVerticales.length != nbRoutesVerticales) {
			throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de routes ne correspond pas au nombre de segments ou de types fournis.");
		}
		
		// V�rifie si le nombre de routes horizontales est correct.
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
	 * Construit une carte � partir de quatres chaines de caract�res, repr�sentant respectivement les distances des routes verticales et horizontales et le type des routes verticales et horizontales.
	 * Le format est celui g�n�r� par la fen�tre ParamMAAS.
	 * Le format accept� pour les distances est "50,50,50", soit une liste d'entiers s�par�s par une virgule. Les distances son en m�tres.
	 * Le format accept� pour les types de routes est "PSPS", o� P repr�sente une route principale et S une route secondaire.
	 * @throws Exception
	 * @throws UnequalNumberOfRoadsException Si le nombre de routes fournis est inconstant entre les diff�rents param�tres. Contient un attribut Orientation pour savoir quelle orientation est incorrecte. 
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
				throw new UnequalNumberOfRoadsException(orientations.verticale, "Le nombre de donn�es pr�sentes dans le tableau des distances horizontales et celui des types verticaux ne correspondent pas.");
			}
		} else {
			throw new UnequalNumberOfRoadsException(orientations.horizontale, "Le nombre de donn�es pr�sentes dans le tableau des distances verticales et celui des types horizontaux ne correspondent pas.");
		}
		
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
	
	/**
	 * Exception g�n�r�e par le constructeur de Carte si le nombre de routes fournis par les constructeurs ne correspondent pas.
	 * Poss�de l'attribut orientation pour savoir quelle des deux liste de routes a fait d�faut.
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
