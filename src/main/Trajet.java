package main;

import java.util.ArrayList;
import java.util.Arrays;

import main.Carte.directions;
import main.Intersection;

public class Trajet {
	
	private Carte carte;
	private Intersection depart;
	private Intersection destination;
	 
	private ArrayList<Intersection> intersections;
	private int nbIntersections;
	 
	private int temps;
	private int distance;
	private typeTransport vehicule;
	private directions direction;
	 


	public Trajet(Carte carte, Intersection ... intersections) {
		super();
		this.carte = carte;
		this.nbIntersections = intersections.length;
		this.depart = intersections[0];
		this.destination = intersections[nbIntersections];
		this.intersections = new ArrayList<>(Arrays.asList(intersections));
		this.vehicule = typeTransport.undefined;
	}
	
	public Trajet(Carte carte, typeTransport vehicule, Intersection ... intersections) {
		super();
		this.carte = carte;
		this.nbIntersections = intersections.length;
		this.depart = intersections[0];
		this.destination = intersections[nbIntersections - 1];
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
		return depart;
	}

	public Intersection getDestination() {
		return destination;
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

	public typeTransport getVehicule() {
		return vehicule;
	}

	public directions getDirection() {
		return direction;
	}
	
	public void setDirection(directions direction) {
		this.direction = direction;
	}
	 
	 
}
