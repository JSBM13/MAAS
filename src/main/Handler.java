package main;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

import main.Carte.directions;
import main.Carte.typesRoute;

public class Handler {

	public static void main(String[] args) throws Exception {
		
		Scanner scanner = new Scanner(System.in);
		
		int[] segVert = {400,300,400,300};
		int[] segHor = {400,300,400,300,400};
		Carte.typesRoute[] typeVert = {typesRoute.principale, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire};
		Carte.typesRoute[] typeHor = {typesRoute.principale, typesRoute.secondaire, typesRoute.secondaire, typesRoute.principale, typesRoute.secondaire};
		
		Carte carte = null;
		try {
			carte = new Carte(6, 5, segVert, segHor, typeVert, typeHor);
		} catch (Carte.UnequalNumberOfRoadsException e) {
			System.out.println(e.getMessage() + " " + e.orientation);
		}
		
		ModeTransport vehicule = new ModeTransport(null, null, null, null, null);
		Circuit[] circuits = new Circuit[3];
		circuits[0] = new Circuit(vehicule, "(3,5) (5,5) (5,2) (3,2) (3,5)");
		circuits[1] = new Circuit(vehicule, "(3,5) (5,5) (5,2) (3,2) (3,5)");
		circuits[2] = new Circuit(vehicule, "(3,5) (5,5) (5,2) (3,2) (3,5)");
		
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
			System.out.println(carte.trouverCircuits(new Intersection(scanner.nextInt(), scanner.nextInt()), new Intersection(scanner.nextInt(), scanner.nextInt()), vehicule));
		}
		

	}

}
