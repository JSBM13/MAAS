package main;

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
		while (true) {
			System.out.println("Entrez un trajet à trouver, dans la forme suivante: ");
			System.out.println("x1 y1 x2 y2 direction");
			System.out.println(carte.trouverTrajet(new Intersection(scanner.nextInt(), scanner.nextInt()), new Intersection(scanner.nextInt(), scanner.nextInt()), new ModeTransport(null, null, null, null, null), carte.rotateDirection(directions.nord, scanner.nextInt())));
			
		}
		

	}

}
