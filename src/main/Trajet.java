package main;

import java.util.ArrayList;
import java.util.Arrays;

import main.Carte.directions;
import main.Intersection;

public class Trajet {
	
	private Carte carte;

	 
	private ArrayList<Intersection> intersections;
	private int nbIntersections;
	 
	private int temps;
	private int distance;
	private typeTransport vehicule;
	private directions direction = directions.undefined;
	
	private int distanceDepart = -1;
	private int distanceArrivee = -1;

	public Trajet(Carte carte, Intersection ... intersections) {
		super();
		this.carte = carte;
		this.nbIntersections = intersections.length;
		this.intersections = new ArrayList<>(Arrays.asList(intersections));
		this.vehicule = typeTransport.undefined;
	}
	
	public Trajet(Carte carte, typeTransport vehicule, Intersection ... intersections) {
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
	 * Prend les points d'un second objet Trajet et les ajoute � la fin de la liste de cet objet.
	 * Cette fonction n�cessite que ce trajet fini au m�me endroit que le second Trajet commence.
	 * @param t
	 * @throws Exception Si le trajet � combiner ne commence pas � la m�me intersection que ce trajet fini.
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
					throw new Exception("Le Trajet � combiner (" + t + ") ne commence pas � la m�me intersection que ce Trajet (" + this + ") fini.");
				}
				
			}
		}
	}
	
	public int calculateDistance() {
		if (nbIntersections > 1) {
			int distance = 0;
			for (int i = 0; i < nbIntersections - 1; i++) {
				Intersection point1 = intersections.get(i);
				Intersection point2 = intersections.get(i + 1);
				directions direction = carte.findDirection(point1, point2);
				int distanceSegment = 0;
				switch (direction) {
				case nord:
					distanceSegment = carte.getSegmentsVerticaux(point1.getY());
					break;
				case est:
					distanceSegment = carte.getSegmentsHorizontaux(point1.getX());
					break;
				case sud:
					distanceSegment = carte.getSegmentsVerticaux(point2.getY());
					break;
				case ouest:
					distanceSegment = carte.getSegmentsHorizontaux(point2.getX());
					break;
				case undefined:
					// Vraiment pas suppos�...
					break;
				}
				distance += distanceSegment;
			}
			this.distance = distance;
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

	public void setVehicule(typeTransport vehicule) {
		this.vehicule = vehicule;
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

	public typeTransport getVehicule() {
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
