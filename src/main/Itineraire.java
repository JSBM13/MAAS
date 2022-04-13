/*
 * Classe qui repr�sente une s�rie de trajet utilisant un ou plusieurs moyens de transport pour se d�placer d'un point � un autre.
 */

package main;

import java.util.ArrayList;

public class Itineraire {
	private ArrayList<typeTransport> vehicules;	// La liste des moyens de transports utilis�s
	private ArrayList<Trajet> trajets;			// La liste de trajets emprunt�s
	private String nom;							// Le nom de cet Itin�raire, g�n�ralement le nom du moyen de transport principal.
	private int temps;							// Temps, en secondes, n�cessaire pour faire le d�placement.
	private int distance;						// Distance, en m�tres, parcourue dans cet itin�raire.
	
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
	 * Constructeur. Ne prend comme param�tre que le nom.
	 * @param nom Nom de l'itin�raire.
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
