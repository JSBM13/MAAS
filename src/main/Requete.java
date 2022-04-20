/*
 * Représente une requête pour se rendre d'un point A à un point B, stockant plusieurs itinéraires possibles.
 * Cette classe possède également diverses fonctions pour obtenir les itinéraires, choisir les meilleurs et fournir des recommendations.
 * 
 */

package main;

import java.util.ArrayList;

public class Requete {
	private Carte carte;
	private ArrayList<Itineraire> itineraires;
	private Intersection depart, destination;
	private boolean heureDePointe;
	private int recommandation;
	
	/**
	 * Génère les itinéraires standarts du programme.
	 * @param vehicules Un tableau des modes de transport à utiliser.
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Itineraire> calculateItineraires(ModeTransport[] vehicules) throws Exception {
		
		for (int i = 0; i < vehicules.length; i++) {
			ModeTransport vehicule = vehicules[i];
			
			// Si le véhicule peut être utilisé à cet heure...
			if ((heureDePointe && vehicule.isTransportHeurePointe()) || (!heureDePointe && vehicule.isTransportHeureReguliere())) {
				
				// Et si ce n'est pas un véhicule de Transport en commun (ceux-ci sont gérés indépendamment)
				if (!vehicule.isTransportEnCommun()) {
					
					// On créé un itinéraire avec ce véhicule.
					calculateItineraireDirect(vehicule.getNom(), vehicule);
				}
			}
		}
		
		// On ajoute le meilleur circuit de transport en commun, avec la marche comme méthode intermédiaire de se rendre aux arrêts.
		calculateMeilleurTransportEnCommun(vehicules[0]);
		
		//calculateAllTransportEnCommun(vehicules[0]);
		
		return itineraires;
	}
	
	public int calculateRecommendation() {
		
		return recommandation;
	}
	
	/**
	 * Ajoute à la requête un itinéraire direct entre le point de départ et celui d'arrivée en utilisant le mode de transport indiqué.
	 * @param nom Le nom de l'itinéraire
	 * @param type Le type d'itinéraire
	 * @param vehicule Le mode de transport à utiliser
	 * @throws Exception
	 */
	public void calculateItineraireDirect(String nom, ModeTransport vehicule) throws Exception {
		Itineraire itineraire = new Itineraire(nom);
		itineraire.addTrajet(carte.trouverTrajet(depart, destination, vehicule, directions.undefined));
		itineraires.add(itineraire);
	}
	
	/**
	 * Détermine le meilleur Itineraire possible empruntant un véhicule de transport en commun et l'ajoute à la requête.
	 * @param vehiculeIntermediaire
	 * @throws Exception
	 */
	public void calculateMeilleurTransportEnCommun(ModeTransport vehiculeIntermediaire) throws Exception {
		
		Trajet[] choix = carte.trouverCircuits(depart, destination);
		
		// On sélectionne le trajet qui minimise la distance à marcher depuis et vers les arrêts.
		// S'il y a une égalité, on prend celui qui demande le moins de temps pour le voyage.
		int minimumDistanceArrets = -1;
		int minimumTemps = -1;
		int indexMeilleur = -1;
		
		for (int i = 0; i < choix.length; i++) {
			int distanceDepart = choix[i].getDistanceDepart();
			int distanceArrivee = choix[i].getDistanceArrivee();
			int tempsTrajet = choix[i].calculateTemps();
			//System.out.println("Trajet %d: distanceDepart=%d, distanceArrivee=%d, tempsTrajet=%d; %s".formatted(
			//		i, distanceDepart, distanceArrivee, tempsTrajet, choix[i]));
			
			if (distanceDepart + distanceArrivee < minimumDistanceArrets || minimumDistanceArrets == -1) {
				minimumDistanceArrets = distanceDepart + distanceArrivee;
				minimumTemps = tempsTrajet;
				indexMeilleur = i;
				
			} else if (distanceDepart + distanceArrivee == minimumDistanceArrets && tempsTrajet < minimumTemps) {
				minimumDistanceArrets = distanceDepart + distanceArrivee;
				minimumTemps = tempsTrajet;
				indexMeilleur = i;
			}
		}
		
		// Génération des déplacements intermédiaires de l'itinéraire, pour aller à l'arrêt d'embarquement et ensuite entre
		// l'arrêt de débarquement et la destination.
		Itineraire itin = produireDeplacementIntermediaire(choix[indexMeilleur], vehiculeIntermediaire);
		
		addItineraire(itin);
	}
	
	public void calculateAllTransportEnCommun(ModeTransport vehiculeIntermediaire) throws Exception {
		Trajet[] choix = carte.trouverCircuits(depart, destination);
		
		for (int i = 0; i < choix.length; i++) {
			addItineraire(produireDeplacementIntermediaire(choix[i], vehiculeIntermediaire));
		}
	}
	
	/**
	 * Produit un itinéraire à partir d'un Trajet en transport en commun où l'on rajoute les déplacements à faire pour se rendre
	 * à l'arrêt d'embarquement et depuis l'arrêt de débarquement jusqu'à la destination.
	 * @param trajet Le trajet de transport en commun à utiliser.
	 * @param vehiculeIntermediaire Le moyen de transport à utiliser pour les déplacements intermédiaires.
	 * @return Un Itinéraire comprenant les déplacements nécessaires pour se rendre vers et depuis les arrêts.
	 */
	public Itineraire produireDeplacementIntermediaire(Trajet trajet, ModeTransport vehiculeIntermediaire) {
		
		Itineraire itin = new Itineraire(trajet.getVehicule().getNom() + " circuit " + trajet.getIndexCircuit());
		
		try {
			// Ajout à l'itinéraire de la marche entre l'intersection actuelle et celle du premier arrêt, si besoin.
			if (trajet.getDistanceDepart() > 0) {
				itin.addTrajet(carte.trouverTrajet(depart, trajet.getDepart(), vehiculeIntermediaire, directions.undefined));
			}
			// Ajout du trajet en transport en commun
			itin.addTrajet(trajet);
			// AJout de la marche entre le dernier arrêt et la destination, si besoin.
			if (trajet.getDistanceArrivee() > 0) {
				itin.addTrajet(carte.trouverTrajet(trajet.getDestination(), destination, vehiculeIntermediaire, directions.undefined));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return itin;
	}
	
	public void addItineraire(Itineraire itineraire) {
		itineraires.add(itineraire);
	}
	
	public Requete(Carte carte, Intersection depart, Intersection destination, boolean heureDePointe) throws Exception {
		super();
		this.depart = depart;
		this.destination = destination;
		this.itineraires = new ArrayList<>();
		this.carte = carte;
		this.heureDePointe = heureDePointe;
		this.recommandation = -1;
	}
	
	public ArrayList<Itineraire> getItineraires() {
		return itineraires;
	}

	public Itineraire getItineraire(int index) {
		return itineraires.get(index);
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
	
	@Override
	public String toString() {
		String x = "Requête de " + depart.toString() + " à " + destination.toString() + ", "+ (heureDePointe ? "heure de pointe " : "heure régulière ") + ":\n";
		
		for (int i = 0; i < itineraires.size(); i++) {
			x += (recommandation == i ? "*" : " ") + i + ") " + itineraires.get(i).toString() + "\n";
		}
		
		
		return x;
	}
	
}
