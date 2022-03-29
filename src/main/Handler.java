package main;

import java.util.Arrays;
import java.util.Scanner;
import main.Carte.typesRoute;

public class Handler {

	final static Parametres defaultParams = new Parametres("400,300,400,300",
			"400,300,400,300,400",
			"PSPSPS",
			"SPSSP",
			"Marche,1,1,30,20,5,0;V�lo,6,7,40,30,15,0;Autobus,20,14,15,20,10,300;M�tro,25,25,45,0,0,180;Voiture (r�gulier),20,14,60,40,10,0;Voiture (heure de pointe),8,10,90,60,45,0",
			"(0,1);(0,4);(5,4)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (2,0) (0,0) (0,2) (0,4);(0,1) (2,1) (2,3) (3,4) (4,2) (3,1) (1,0) (0,1);(5,0) (5,2) (3,3) (1,3) (1,0) (3,0) (5,0)",
			"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
			);
	
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
		
		ModeTransport vehicules[] = new ModeTransport[6];
		String[] inputVehicules = new String[6];
		inputVehicules = defaultParams.vehicules.split(";");
		vehicules[0] = new ModeTransport("V�hicule", typeTransport.marche, true, true, false, inputVehicules[0]);
		vehicules[1] = new ModeTransport("V�hicule", typeTransport.velo, true, true, false, inputVehicules[1]);
		vehicules[2] = new ModeTransport("V�hicule", typeTransport.autobus, true, true, true, inputVehicules[2]);
		vehicules[3] = new ModeTransport("V�hicule", typeTransport.metro, true, true, true, inputVehicules[3]);
		vehicules[4] = new ModeTransport("V�hicule", typeTransport.voiture, true, false, false, inputVehicules[4]);
		vehicules[5] = new ModeTransport("V�hicule", typeTransport.voiture, false, true, false, inputVehicules[5]);
		Circuit[] circuits = Circuit.parseCircuits(defaultParams.circuitsAutobus, vehicules[2]);
		
		carte.setCircuits(circuits);
		System.out.println(Arrays.toString(carte.getCircuits()));
		/*while (true) {
			System.out.println("Entrez un trajet � trouver, dans la forme suivante: ");
			System.out.println("x1 y1 x2 y2 direction");
			System.out.println(carte.trouverTrajet( new Intersection(scanner.nextInt(), scanner.nextInt()), new Intersection(scanner.nextInt(), scanner.nextInt()), new ModeTransport(null, null, null, null, null), carte.directionFromInt(scanner.nextInt())));
			
		}*/
		while (true) {
			System.out.println("Entrez un point de d�part et d'arriv�e, dans la forme suivante: ");
			System.out.println("x1 y1 x2 y2");
			System.out.println((carte.choisirTrajets(new Intersection(scanner.nextInt(), scanner.nextInt()), new Intersection(scanner.nextInt(), scanner.nextInt()), vehicules[0])));
		}
		

	}
	
	

}
