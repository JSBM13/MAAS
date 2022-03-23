package main;

import main.typeTransport;

public class Itineraire {
	public typeTransport[] vehicules;
	public Trajet[] trajets;
	public String nom;
	public String typeTrajet;
	
	public Itineraire(typeTransport[] vehicules, Trajet[] trajets, String nom, String typeTrajet) {
		super();
		this.vehicules = vehicules;
		this.trajets = trajets;
		this.nom = nom;
		this.typeTrajet = typeTrajet;
	}

	public typeTransport[] getVehicules() {
		return vehicules;
	}

	public Trajet[] getTrajets() {
		return trajets;
	}

	public String getNom() {
		return nom;
	}

	public String getTypeTrajet() {
		return typeTrajet;
	}
	
	
}
