package main;

import java.util.ArrayList;
import java.util.Arrays;

import main.Carte.directions;
import main.Carte.orientations;
import main.Carte.typesRoute;
import main.Intersection;

public class Trajet {
	
	private Carte carte;

	 
	private ArrayList<Intersection> intersections;
	private int nbIntersections;
	 
	private int temps;
	private int distance;
	private int distancePrincipale;
	private int distanceSecondaire;
	private ModeTransport vehicule;
	private directions direction = directions.undefined;
	
	private int distanceDepart = -1;
	private int distanceArrivee = -1;
	
	public Trajet(Carte carte, ModeTransport vehicule, Intersection ... intersections) {
		super();
		this.carte = carte;
		this.nbIntersections = intersections.length;
		this.intersections = new ArrayList<>(Arrays.asList(intersections));
		this.vehicule = vehicule;
	}
	
	public void addIntersection(Intersection intersection) {
		if (intersection.getType() == typesIntersection.undefined) {
			intersection.setType(carte.getTypeIntersection(intersection.getX(), intersection.getY()));
		}
		intersections.add(intersection);
		nbIntersections = intersections.size();
	}
	
	/**
	 * Prend les points d'un second objet Trajet et les ajoute à la fin de la liste de cet objet.
	 * Cette fonction nécessite que ce trajet fini au même endroit que le second Trajet commence.
	 * @param t
	 * @throws Exception Si le trajet à combiner ne commence pas à la même intersection que ce trajet fini.
	 */
	public void combine(Trajet t) throws Exception {
		if (t.nbIntersections >= 1) {
			if (nbIntersections == 0) {
				setIntersections(t.getIntersections());
			} else {
				if (getDestination().equals(t.getDepart())) {
					intersections.addAll(t.getIntersections().subList(1, t.getNbIntersections()));
					nbIntersections = intersections.size();
					makeReady();
				} else {
					throw new Exception("Le Trajet à combiner (" + t + ") ne commence pas à la même intersection que ce Trajet (" + this + ") fini.");
				}
				
			}
		}
	}
	
	public int calculateTemps() {
		this.temps = vehicule.calculateTempsDeplacement(distancePrincipale, distanceSecondaire, intersections.toArray(new Intersection[intersections.size()]));
		return this.temps;
	}
	
	public int calculateDistance() {
		if (nbIntersections > 1) {
			int distance = 0;
			int distancePrincipale = 0;
			int distanceSecondaire = 0;
			for (int i = 0; i < nbIntersections - 1; i++) {
				Intersection point1 = intersections.get(i);
				Intersection point2 = intersections.get(i + 1);
				directions direction = carte.findDirection(point1, point2);
				orientations orientation = null; 
				int segment = 0;
				int distanceSegment = 0;
				switch (direction) {
				case nord:
					orientation = orientations.verticale;
					segment = point1.getY();
					distanceSegment = carte.getSegmentsVerticaux(segment);
					break;
				case est:
					orientation = orientations.horizontale;
					segment = point1.getX();
					distanceSegment = carte.getSegmentsHorizontaux(segment);
					break;
				case sud:
					orientation = orientations.verticale;
					segment = point2.getY();
					distanceSegment = carte.getSegmentsVerticaux(segment);
					break;
				case ouest:
					orientation = orientations.horizontale;
					segment = point2.getX();
					distanceSegment = carte.getSegmentsHorizontaux(segment);
					break;
				case undefined:
					// Vraiment pas supposé...
					break;
				}
				distance += distanceSegment;
				if (carte.getTypeRoute(orientation, segment) == typesRoute.principale) {
					distancePrincipale += distanceSegment;
				} else if (carte.getTypeRoute(orientation, segment) == typesRoute.secondaire) {
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
	

	public String toString() {
		String s = "Trajet: [ ";
		for (Intersection inters : intersections) {
			s += inters.toString() + " ";
		}
		String autres = "";
		autres = (distance != -1 ? "Distance=" + distance + "m" : "");
		s += "] " + (autres != "" ? "( " + autres + " ) " : "");
		return s;
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
	
	public void setIntersections(ArrayList<Intersection> intersections) {
		this.intersections = intersections;
		nbIntersections = intersections.size();
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
	
	public void findDirection() {
		try {
			if (nbIntersections > 1) {
				this.direction = carte.findDirection(intersections.get(nbIntersections - 2), intersections.get(nbIntersections - 1));
			} else {
				this.direction = directions.undefined;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			this.direction = directions.undefined;
		}
		
	}
	
	public void makeReady() {
		findDirection();
		calculateDistance();
	}
	 
	 
}
