package main;

import java.util.ArrayList;

public class Itineraire {
	private ArrayList<typeTransport> vehicules;
	private ArrayList<Trajet> trajets;
	private String nom;
	private int temps;
	private int distance;
	
	public Itineraire(String nom) {
		super();
		this.nom = nom;
		this.trajets = new ArrayList<Trajet>();
		this.vehicules = new ArrayList<typeTransport>();
		this.temps = 0;
		this.distance = 0;
	}
	
	public Itineraire addTrajet(Trajet trajet) {
		if (trajet.getNbIntersections() > 1) {
			trajets.add(trajet);
			temps += trajet.calculateTemps();
			distance += trajet.calculateDistance();
			if (!vehicules.contains(trajet.getVehicule().getType())) {
				vehicules.add(trajet.getVehicule().getType());
			}
		}
		return this;
	}

	public ArrayList<typeTransport> getVehicules() {
		return vehicules;
	}

	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}

	public String getNom() {
		return nom;
	}

	
	public String toString() {
		String s = nom + " (" + Handler.getTempsPourHumains(temps) + ", " + distance + "m) " +": { \n";
		for (Trajet trajet : trajets) {
			s += "    " + trajet.toString() + "\n";
		}
		return s + " } ";
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public int getTemps() {
		return temps;
	}
	
	public int getDistance() {
		return distance;
	}
	
	
}
