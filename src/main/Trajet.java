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
	 * Prend les points d'un second objet Trajet et les ajoute à la fin de la liste de cet objet.
	 * Cette fonction nécessite que ce trajet fini au même endroit que le second Trajet commence.
	 * @param t
	 * @throws Exception Si le trajet à combiner ne commence pas à la même intersection que ce trajet fini.
	 */
	public void combine(Trajet t) throws Exception {
		if (getDestination() == t.getDepart()) {
			intersections.addAll(t.getIntersections().subList(1, t.nbIntersections - 1));
			nbIntersections = intersections.size();
			makeReady();
		} else {
			throw new Exception("Le Trajet à combiner ne commence pas à la même intersection que ce Trajet fini.");
		}
	}
	

	public String toString() {
		String s = "Trajet: [ ";
		for (Intersection inters : intersections) {
			s += inters.toString() + " ";
		}
		return s + "]";
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
	
	public void findDirection() {
		this.direction = carte.findDirection(intersections.get(nbIntersections - 2), intersections.get(nbIntersections - 1));
	}
	
	public void makeReady() {
		findDirection();
	}
	 
	 
}
