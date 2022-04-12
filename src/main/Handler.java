/**
 * 
 * À ignorer, cette classe fut autrefois la façon de tester le code avant que l'on aille une interface graphique.
 * Gardée uniquement comme référence pour comment utiliser les différents objets, et si on a besoin de tester avec une interface texte.
 * 
 */

package main;

import java.util.Arrays;
import java.util.Scanner;

public class Handler {

			
	final static Parametres defaultParams = Parametres.defaultParams6x5;
	
	static String[] inputVehicules =  defaultParams.vehicules.split(";");
	
	static ModeTransport[] vehicules = new ModeTransport[] {     //public ModeTransport(String nom, typeTransport type, boolean transportHeureReguliere, boolean transportHeurePointe, boolean transportEnCommun, String input)
		new ModeTransport("Marche", typeTransport.marche, true, true, false, inputVehicules[0]),
		new ModeTransport("Vélo", typeTransport.velo, true, true, false, inputVehicules[1]),
		new ModeTransport("Autobus", typeTransport.autobus, true, true, true, inputVehicules[2]),
		new ModeTransport("Metro", typeTransport.metro, true, true, true, inputVehicules[3]),
		new ModeTransport("Voiture", typeTransport.voiture, true, false, false, inputVehicules[4]),
		new ModeTransport("Voiture", typeTransport.voiture, false, true, false, inputVehicules[5]),
	};
	
	
	
	public static void main(String[] args) throws Exception {
		
		Scanner scanner = new Scanner(System.in);
		
		/*int[] segVert = {400,300,400,300};
		int[] segHor = {400,300,400,300,400};
		Carte.typesRoute[] typeVert = {typesRoute.principale, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire};
		Carte.typesRoute[] typeHor = {typesRoute.principale, typesRoute.secondaire, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire};*/
		
		Carte carte = null;
		try {
			carte = new Carte(defaultParams.distanceRoutesVerticales, defaultParams.distanceRoutesHorizontales, defaultParams.typesRoutesVerticales, defaultParams.typesRoutesHorizontales);
		} catch (Carte.UnequalNumberOfRoadsException e) {
			e.printStackTrace();
			System.out.println(e.getMessage() + " " + e.orientation);
		}
		
		//old position of ModeTransport vehicules[] = new ModeTransport[6];     and    String[] inputVehicules = new String[6];
		
		Circuit[] circuits = Circuit.parseCircuits(defaultParams.circuitsAutobus, vehicules[2]);
		
		carte.setCircuits(circuits);
		System.out.println(Arrays.toString(carte.getCircuits()));
		/*while (true) {
			System.out.println("Entrez un trajet à trouver, dans la forme suivante: ");
			System.out.println("x1 y1 x2 y2 direction");
			System.out.println(carte.trouverTrajet( new Intersection(scanner.nextInt(), scanner.nextInt()), new Intersection(scanner.nextInt(), scanner.nextInt()), new ModeTransport(null, null, null, null, null), carte.directionFromInt(scanner.nextInt())));
			
		}*/
		while (true) {
			System.out.println("Entrez un point de départ et d'arrivée, dans la forme suivante: ");
			System.out.println("x1 y1 x2 y2");
			int v1 = scanner.nextInt();
			int v2 = scanner.nextInt();
			int v3 = scanner.nextInt();
			int v4 = scanner.nextInt();
			Requete req = new Requete(carte, new Intersection(v1, v2), new Intersection(v3, v4), false);
			req.calculateItineraires(vehicules);
			System.out.println(req);
			System.out.println();
		}
		
		//generateAllRequetes(carte, vehicules);

	}
	
	static void generateAllRequetes(Carte carte, ModeTransport[] vehicules) throws Exception {
		for (int v1 = 0; v1 < carte.getNbRoutesVerticales(); v1++) {
			for (int v2 = 0; v2 < carte.getNbRoutesHorizontales(); v2++) {
				for (int v3 = 0; v3 < carte.getNbRoutesVerticales(); v3++) {
					for (int v4 = 0; v4 < carte.getNbRoutesHorizontales(); v4++) {
						Requete req = new Requete(carte, new Intersection(v1, v2), new Intersection(v3, v4), false);
						req.calculateItineraires(vehicules);
						System.out.println(req);
						System.out.println();
					}
				}
			}
		}
		
	}
}
