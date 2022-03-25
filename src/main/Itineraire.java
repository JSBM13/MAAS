package main;

import java.util.ArrayList;
import main.typeTransport;

public class Itineraire {
	public ArrayList<typeTransport> vehicules;
	public ArrayList<Trajet> trajets;
	public String nom;
	public String typeTrajet;
	
	public Itineraire(String nom, String typeTrajet) {
		super();
		this.nom = nom;
		this.typeTrajet = typeTrajet;
		this.trajets = new ArrayList<Trajet>();
		this.vehicules = new ArrayList<typeTransport>();
	}
	
	public Itineraire addTrajet(Trajet trajet) {
		trajets.add(trajet);
		if (!vehicules.contains(trajet.getVehicule())) {
			vehicules.add(trajet.getVehicule());
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

	public String getTypeTrajet() {
		return typeTrajet;
	}
	
	public String toString() {
		String s = "Itineraire " + nom + " (" + typeTrajet + "): { ";
		for (Trajet trajet : trajets) {
			s += trajet.toString();
		}
		return s + "} ";
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	
}
