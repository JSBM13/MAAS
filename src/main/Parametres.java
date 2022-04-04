/**
 * Représente un ensemble de paramètres, sous forme de String, à utiliser par l'application.
 * 
 */

package main;


public final class Parametres {
	String distanceRoutesVerticales;
	String distanceRoutesHorizontales;
	String typesRoutesVerticales;
	String typesRoutesHorizontales;
	String vehicules;
	String points;
	String circuitsAutobus;
	String circuitsMetro;
	
	public Parametres(String distanceRoutesVerticales, String distanceRoutesHorizontales, String typesRoutesVerticales, String typesRoutesHorizontales, String vehicules, String points,
			String circuitsAutobus, String circuitsMetro) {
		super();
		this.distanceRoutesVerticales = distanceRoutesVerticales;
		this.distanceRoutesHorizontales = distanceRoutesHorizontales;
		this.typesRoutesVerticales = typesRoutesVerticales;
		this.typesRoutesHorizontales = typesRoutesHorizontales;
		this.vehicules = vehicules;
		this.points = points;
		this.circuitsAutobus = circuitsAutobus;
		this.circuitsMetro = circuitsMetro;
	}
	
	public String toSingleString() {
		return 	distanceRoutesVerticales + "\n" +
				distanceRoutesHorizontales + "\n" +
				typesRoutesVerticales + "\n" +
				typesRoutesHorizontales + "\n" +
				vehicules + "\n" +
				points + "\n" +
				circuitsAutobus + "\n" +
				circuitsMetro;
	}
	
	public String toString() {
		return 	"Distances Routes Verticales: " + distanceRoutesVerticales + "\n" +
				"Distances Routes Horizontales: " + distanceRoutesHorizontales + "\n" +
				"Types Routes Verticales: " + typesRoutesVerticales + "\n" +
				"Types Routes Horizontales: " + typesRoutesHorizontales + "\n" +
				"Vehicules: " + vehicules + "\n" +
				"Points: " + points + "\n" +
				"Circuits Autobus: " + circuitsAutobus + "\n" +
				"Circuits Métro: " + circuitsMetro;
	}
	
	final static Parametres defaultParams6x5 = new Parametres("400,300,400,300",
            "400,300,400,300,400",
            "PSPSPS",
            "SPSSP",
            "Marche,3,3,30,20,5,0,#FFFF3C;Vélo,6,7,40,30,15,0,#9BFF98;Autobus,35,25,15,20,10,300,#34FFFF;Métro,50,50,45,0,0,180,#BA78E5;Voiture (régulier),30,20,60,40,15,0,#FF7373;Voiture (heure de pointe),20,15,90,60,30,0,#FF7373",
            "(0,1);(0,4);(5,4)",
            "(0,4) (4,4) (4,3) (5,3) (5,0) (2,0) (0,0) (0,2) (0,4);(0,1) (2,1) (2,3) (3,4) (4,2) (3,1) (1,0) (0,1);(5,0) (5,2) (3,3) (1,3) (1,0) (3,0) (5,0)",
            "(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4)"
            );
	
}