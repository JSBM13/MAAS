/*
 * Déplacement d'un point à un autre, avec différentes informations sur le temps et la distance nécessaire.
 * 
 */

package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trajet {
	
	private Carte carte;

	private ArrayList<Intersection> intersections;	// Liste des intersections par lesquels le déplacement passe.
	private int nbIntersections;					// Nombre d'intersections
	 
	private int temps;								// Temps nécessaire, en secondes.
	private int distance;							// Distance parcourue, en mètres.
	private int distancePrincipale;					// Distance parcourue sur des routes principales.
	private int distanceSecondaire;					// Distance parcourue sur des routes secondaires.
	private int[] intersectionsParType; 			// Tableau d'entiers représentant le nombre d'intersections croisées de chaque type.
	private ModeTransport vehicule;					// Le véhicule dans lequel ce trajet est parcouru.
	private directions direction = directions.undefined;	// Direction vers laquelle on fait face à la fin du parcours.
	
	private int distanceDepart = -1;				// Si c'est un trajet de transport à commun, distance, en segments, entre le début du trajet
													// et le point de départ de la requête.
	private int distanceArrivee = -1;				// Distance en segments entre la fin du trajet et le point de destination de la requête.
	
	/**
	 * Détermine le temps nécessaire pour parcourir le trajet. Retourne ce temps en seconde, et l'enregistre dans la variable temps.
	 * @return Le temps en secondes.
	 */
	public int calculateTemps() {
		this.temps = vehicule.calculateTempsDeplacement(distancePrincipale, distanceSecondaire, intersections.toArray(new Intersection[intersections.size()]));
		return this.temps;
	}
	
	/**
	 * Détermine la distance du circuit en mètre. Cette fonction mets à jour le champs distance de cet objet et retourne cette valeur.
	 * @return La distance, en mètres.
	 */
	public int calculateDistance() {
		if (nbIntersections > 1) {
			int distance = 0;
			int distancePrincipale = 0;
			int distanceSecondaire = 0;
			for (int i = 0; i < nbIntersections - 1; i++) {
				Intersection point1 = intersections.get(i);
				Intersection point2 = intersections.get(i + 1);
				directions direction = carte.trouverDirection(point1, point2);
				orientations orientation = null; 
				int segment = 0;
				int route = 0;
				int distanceSegment = 0;
				switch (direction) {
				case nord:
					orientation = orientations.verticale;
					segment = point1.getY();
					route = point1.getX();
					distanceSegment = carte.getSegmentsHorizontaux(segment);
					break;
				case est:
					orientation = orientations.horizontale;
					segment = point1.getX();
					route = point1.getY();
					distanceSegment = carte.getSegmentsVerticaux(segment);
					break;
				case sud:
					orientation = orientations.verticale;
					segment = point2.getY();
					route = point2.getX();
					distanceSegment = carte.getSegmentsHorizontaux(segment);
					break;
				case ouest:
					orientation = orientations.horizontale;
					segment = point2.getX();
					route = point2.getY();
					distanceSegment = carte.getSegmentsVerticaux(segment);
					break;
				case undefined:
					// Vraiment pas supposé...
					break;
				}
				distance += distanceSegment;
				if (carte.getTypeRoute(orientation, route) == typesRoute.principale) {
					distancePrincipale += distanceSegment;
				} else if (carte.getTypeRoute(orientation, route) == typesRoute.secondaire) {
					distanceSecondaire += distanceSegment;
				}
			}
			this.distance = distance;
			this.distancePrincipale = distancePrincipale;
			this.distanceSecondaire = distanceSecondaire;
		} else {
			this.distance = 0;
		}
		
		return this.distance;
	}
	
	/**
	 * Détermine la direction vers laquelle l'on fait face à la fin du trajet et la stocke dans la variable direction.
	 */
	public void findDirection() {
		try {
			if (nbIntersections > 1) {
				this.direction = carte.trouverDirection(intersections.get(nbIntersections - 2), intersections.get(nbIntersections - 1));
			} else {
				this.direction = directions.undefined;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			this.direction = directions.undefined;
		}
		
	}
	
	/**
	 * Ajoute un array d'intersections à la liste d'intersections de ce trajet.
	 * @param newIntersections Array d'intersections à ajouter.
	 */
	public void addIntersectionArrayList(List<Intersection> newIntersections) {
		if (newIntersections.size() > 0) {
			for (Intersection intersection : newIntersections) {
				addIntersection(intersection);
			}
		}
	}
	
	/**
	 * Ajoute une intersection à la fin de la liste d'intersections du trajet.
	 * @param intersection Intersection à ajouter.
	 */
	public void addIntersection(Intersection intersection) {
		if (intersection.getType() == typesIntersection.undefined) {
			intersection.setType(carte.getTypeIntersection(intersection.getX(), intersection.getY()));
		}
		intersections.add(intersection);
		nbIntersections = intersections.size();
		switch (intersection.getType()) {
		case PrincPrinc: 	intersectionsParType[0]++; break;
		case SecSec: 		intersectionsParType[1]++; break;
		case PrincSec: 		intersectionsParType[2]++; break;
		default:			// (rien)
		}
		calculateTemps();
		calculateDistance();
		findDirection();
	}
	
	/**
	 * Prend les points d'un second objet Trajet et les ajoute à la fin de la liste de cet objet.
	 * Cette fonction nécessite que ce trajet fini au même endroit que le second Trajet commence.
	 * @param t Trajet à intégrer.
	 * @throws Exception Si le trajet à combiner ne commence pas à la même intersection que ce trajet fini.
	 */
	public void combine(Trajet t) throws Exception {
		if (t.nbIntersections >= 1) {
			if (nbIntersections == 0) {
				addIntersectionArrayList(t.getIntersections());
			} else {
				if (getDestination().equals(t.getDepart())) {
					addIntersectionArrayList(t.getIntersections().subList(1, t.getNbIntersections()));
					nbIntersections = intersections.size();
				} else {
					throw new Exception("Le Trajet à combiner (" + t + ") ne commence pas à la même intersection que ce Trajet (" + this + ") fini.");
				}
			}
		}
	}
	
	public String toString() {
		String s = "[" + vehicule.getNom() + ": ";
		for (Intersection inters : intersections) {
			s += inters.toString() + " ";
		}
		s += "| ";
		s += UtilitaireString.getTempsPourHumains(temps) + ", ";
		s += distance + "m (" + distancePrincipale + "/" + distanceSecondaire + "), ";
		s += nbIntersections + " inters. (" + intersectionsParType[0] + "/" + intersectionsParType[1] + "/" + intersectionsParType[2] + ") ";
		
		return s + "]";
	}


	public Intersection getDepart() {
		return intersections.get(0);
	}

	public Intersection getDestination() {
		return intersections.get(nbIntersections - 1) ;
	}

	public ArrayList<Intersection> getIntersections() {
		return intersections;
	}

	public int getTemps() {
		return temps;
	}

	public void setTemps(int temps) {
		this.temps = temps;
	}

	public int getNbIntersections() {
		return nbIntersections;
	}

	public int getDistance() {
		return distance;
	}

	public ModeTransport getVehicule() {
		return vehicule;
	}

	public directions getDirection() {
		return direction;
	}
	
	public void setDirection(directions direction) {
		this.direction = direction;
	}
	
	public int getDistanceDepart() {
		return distanceDepart;
	}

	public void setDistanceDepart(int distanceDepart) {
		this.distanceDepart = distanceDepart;
	}

	public int getDistanceArrivee() {
		return distanceArrivee;
	}

	public int getDistancePrincipale() {
		return distancePrincipale;
	}

	public int getDistanceSecondaire() {
		return distanceSecondaire;
	}

	public void setDistanceArrivee(int distanceArrivee) {
		this.distanceArrivee = distanceArrivee;
	}
	
	/**
	 * Constructeur de Trajet.
	 * @param carte La carte sur laquelle le trajet est tracé.
	 * @param vehicule Le mode de déplacement utilisé.
	 * @param intersections Intersections du trajet. Peut être vide.
	 */
	public Trajet(Carte carte, ModeTransport vehicule, Intersection ... intersections) {
		super();
		this.carte = carte;
		this.nbIntersections = intersections.length;
		this.vehicule = vehicule;
		this.intersectionsParType = new int[3];
		this.intersections = new ArrayList<>();
		addIntersectionArrayList(new ArrayList<Intersection>(Arrays.asList(intersections)));
	}
	 
}
