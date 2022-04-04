package main;

import java.util.Arrays;
import java.util.Scanner;

public class Handler {

	final static Parametres defaultParams6x5 = new Parametres("400,300,400,300",
            "400,300,400,300,400",
            "PSPSPS",
            "SPSSP",
            "Marche,3,3,30,20,5,0,#FFFF3C;Vélo,6,7,40,30,15,0,#9BFF98;Autobus,35,25,15,20,10,300,#34FFFF;Métro,50,50,45,0,0,180,#BA78E5;Voiture (régulier),30,20,60,40,15,0,#FF7373;Voiture (heure de pointe),20,15,90,60,30,0,#FF7373",
            "(0,1);(0,4);(5,4)",
            "(0,4) (4,4) (4,3) (5,3) (5,0) (2,0) (0,0) (0,2) (0,4);(0,1) (2,1) (2,3) (3,4) (4,2) (3,1) (1,0) (0,1);(5,0) (5,2) (3,3) (1,3) (1,0) (3,0) (5,0)",
            "(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
            );
	
	final static Parametres defaultParams20x20= new Parametres("100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100",
					"100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100",
					"PSSSPSSSSPSSSSPSSPSP",
					"PSPSSSSPSSSSPSSSPSSP",
					"Marche,3,3,30,20,5,0;Vélo,6,7,40,30,15,0;Autobus,35,25,15,20,10,300;Métro,50,50,45,0,0,180;Voiture (régulier),30,20,60,40,15,0;Voiture (heure de pointe),20,15,90,60,30,0",
					"(0,1);(0,4);(5,4)",
					"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)",
					"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
					);
			
	final static Parametres defaultParams = defaultParams6x5;
	
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
	
	static public String getTempsPourHumains(int temps) {
		int heures = temps / 3600;
		int minutes = (temps - (heures * 3600)) / 60;
		int secondes = (temps - ((heures * 3600) + (minutes * 60)));
		return (heures != 0 ? heures + "h" : "") + 
				(minutes != 0 || heures != 0 ? (minutes < 10 ? "0" : "") + minutes + "m" : "") + 
				(secondes != 0 ? (secondes < 10 ? "0" : "") + secondes + "s" : "");
	}
}
