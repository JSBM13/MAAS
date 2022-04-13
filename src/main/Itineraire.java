/*
 * Classe qui représente une série de trajet utilisant un ou plusieurs moyens de transport pour se déplacer d'un point à un autre.
 */

package main;

import java.util.ArrayList;

public class Itineraire {
	private ArrayList<typeTransport> vehicules;	// La liste des moyens de transports utilisés
	private ArrayList<Trajet> trajets;			// La liste de trajets empruntés
	private String nom;							// Le nom de cet Itinéraire, généralement le nom du moyen de transport principal.
	private int temps;							// Temps, en secondes, nécessaire pour faire le déplacement.
	private int distance;						// Distance, en mètres, parcourue dans cet itinéraire.
	
	public Itineraire addTrajet(Trajet trajet) {
		if (trajet.getNbIntersections() > 1) {
			trajets.add(trajet);
			temps += trajet.getTemps();
			distance += trajet.getDistance();
			if (!vehicules.contains(trajet.getVehicule().getType())) {
				vehicules.add(trajet.getVehicule().getType());
			}
		}
		return this;
	}

	public ArrayList<typeTransport> getVehicules() {
		return vehicules;
	}
	
	public boolean isVehiculeUsed(typeTransport vehicule) {
		return vehicules.contains(vehicule);
	}
	
	public Trajet getTrajets(int index) {
		return trajets.get(index);
	}
	
	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
	
	public int getNbTrajets() {
		return trajets.size();
	}

	public String getNom() {
		return nom;
	}
	
	public String toString() {
		String s = nom + " (" + UtilitaireString.getTempsPourHumains(temps) + ", " + distance + "m) " +": { \n";
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
	
	/**
	 * Constructeur. Ne prend comme paramètre que le nom.
	 * @param nom Nom de l'itinéraire.
	 */
	public Itineraire(String nom) {
		super();
		this.nom = nom;
		this.trajets = new ArrayList<Trajet>();
		this.vehicules = new ArrayList<typeTransport>();
		this.temps = 0;
		this.distance = 0;
	}
	
	
}
