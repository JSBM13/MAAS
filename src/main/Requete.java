/*
 * Repr�sente une requ�te pour se rendre d'un point A � un point B, stockant plusieurs itin�raires possibles.
 * Cette classe poss�de �galement diverses fonctions pour obtenir les itin�raires, choisir les meilleurs et fournir des recommendations.
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
	 * G�n�re les itin�raires standarts du programme.
	 * @param vehicules Un tableau des modes de transport � utiliser.
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Itineraire> calculateItineraires(ModeTransport[] vehicules) throws Exception {
		
		for (int i = 0; i < vehicules.length; i++) {
			ModeTransport vehicule = vehicules[i];
			
			// Si le v�hicule peut �tre utilis� � cet heure...
			if ((heureDePointe && vehicule.isTransportHeurePointe()) || (!heureDePointe && vehicule.isTransportHeureReguliere())) {
				
				// Et si ce n'est pas un v�hicule de Transport en commun (ceux-ci sont g�r�s ind�pendamment)
				if (!vehicule.isTransportEnCommun()) {
					
					// On cr�� un itin�raire avec ce v�hicule.
					calculateItineraireDirect(vehicule.getNom(), vehicule);
				}
			}
		}
		
		// On ajoute le meilleur circuit de transport en commun, avec la marche comme m�thode interm�diaire de se rendre aux arr�ts.
		calculateMeilleurTransportEnCommun(vehicules[0]);
		
		//calculateAllTransportEnCommun(vehicules[0]);
		
		return itineraires;
	}
	
	public int calculateRecommendation() {
		
		return recommandation;
	}
	
	/**
	 * Ajoute � la requ�te un itin�raire direct entre le point de d�part et celui d'arriv�e en utilisant le mode de transport indiqu�.
	 * @param nom Le nom de l'itin�raire
	 * @param type Le type d'itin�raire
	 * @param vehicule Le mode de transport � utiliser
	 * @throws Exception
	 */
	public void calculateItineraireDirect(String nom, ModeTransport vehicule) throws Exception {
		Itineraire itineraire = new Itineraire(nom);
		itineraire.addTrajet(carte.trouverTrajet(depart, destination, vehicule, directions.undefined));
		itineraires.add(itineraire);
	}
	
	/**
	 * D�termine le meilleur Itineraire possible empruntant un v�hicule de transport en commun et l'ajoute � la requ�te.
	 * @param vehiculeIntermediaire
	 * @throws Exception
	 */
	public void calculateMeilleurTransportEnCommun(ModeTransport vehiculeIntermediaire) throws Exception {
		
		Trajet[] choix = carte.trouverCircuits(depart, destination);
		
		// On s�lectionne le trajet qui minimise la distance � marcher depuis et vers les arr�ts.
		// S'il y a une �galit�, on prend celui qui demande le moins de temps pour le voyage.
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
		
		// G�n�ration des d�placements interm�diaires de l'itin�raire, pour aller � l'arr�t d'embarquement et ensuite entre
		// l'arr�t de d�barquement et la destination.
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
	 * Produit un itin�raire � partir d'un Trajet en transport en commun o� l'on rajoute les d�placements � faire pour se rendre
	 * � l'arr�t d'embarquement et depuis l'arr�t de d�barquement jusqu'� la destination.
	 * @param trajet Le trajet de transport en commun � utiliser.
	 * @param vehiculeIntermediaire Le moyen de transport � utiliser pour les d�placements interm�diaires.
	 * @return Un Itin�raire comprenant les d�placements n�cessaires pour se rendre vers et depuis les arr�ts.
	 */
	public Itineraire produireDeplacementIntermediaire(Trajet trajet, ModeTransport vehiculeIntermediaire) {
		
		Itineraire itin = new Itineraire(trajet.getVehicule().getNom() + " circuit " + trajet.getIndexCircuit());
		
		try {
			// Ajout � l'itin�raire de la marche entre l'intersection actuelle et celle du premier arr�t, si besoin.
			if (trajet.getDistanceDepart() > 0) {
				itin.addTrajet(carte.trouverTrajet(depart, trajet.getDepart(), vehiculeIntermediaire, directions.undefined));
			}
			// Ajout du trajet en transport en commun
			itin.addTrajet(trajet);
			// AJout de la marche entre le dernier arr�t et la destination, si besoin.
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
		String x = "Requ�te de " + depart.toString() + " � " + destination.toString() + ", "+ (heureDePointe ? "heure de pointe " : "heure r�guli�re ") + ":\n";
		
		for (int i = 0; i < itineraires.size(); i++) {
			x += (recommandation == i ? "*" : " ") + i + ") " + itineraires.get(i).toString() + "\n";
		}
		
		
		return x;
	}
	
}
