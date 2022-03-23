package main;

public class Requete {
	private Itineraire[] itineraires;
	private String nom;
	private Intersection depart, destination;
	private int recommandation;
	
	public Itineraire[] getItineraires() {
		return itineraires;
	}
	
	public Itineraire getItineraire(int index) {
		return itineraires[index];
	}
	public String getNom() {
		return nom;
	}
	public Intersection getDepart() {
		return depart;
	}
	public Intersection getDestination() {
		return destination;
	}
	public int getRecommandation() {
		return recommandation;
	}
	
}
