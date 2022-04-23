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
	
	/* Les paramètres réguliers de l'application. */
	final static Parametres defaultParams6x5 = new Parametres("400,300,400,300,400",
			"400,300,400,300",
            "PSPSPS",
            "SPSSP",
            "Marche,3,3,30,20,5,0,#FFFF3C;Vélo,4,4,60,40,20,0,#9BFF98;Autobus,30,25,20,20,20,180,#34FFFF;Métro,35,35,15,0,0,120,#BA78E5;Voiture (régulier),17,12,60,40,20,0,#FF7373;Voiture (heure de pointe),14,10,120,75,45,0,#FF7373",
            "(0,1);(0,4);(5,4)",
            "(0,1) (0,3) (2,4) (4,4) (4,2) (2,2) (2,0) (0,1);" + 
            		"(1,1) (3,1) (5,1) (5,3) (4,4) (2,4) (1,3) (1,1);" + 
            		"(4,0) (3,1) (2,2) (1,0) (0,1) (0,3) (1,4) (3,4) (4,3) (5,2) (4,0);" +
            		"(0,0) (0,2) (1,3) (2,2) (3,1) (4,2) (5,0) (3,0) (0,0)",
            "(0,1) (0,4) (2,4) (4,4) (4,1) (2,1) (0,1);(2,0) (2,1) (2,4) (4,4) (4,1) (4,0) (2,0)"
            );
	
	/* Paramètres de tests pour une carte plus grande (20x20). */
	final static Parametres defaultParams20x20= new Parametres("150,150,150,100,180,120,160,140,140,160,200,120,180,160,170,130,140,180,120",
			"150,170,120,150,140,120,160,180,100,110,140,170,130,150,180,120,110,170,130",
			"PSSSPSSSSPSSPSSPSSSP",
			"PSSPSSSSPSSSPSSSPSSP",
			"Marche,3,3,30,20,5,0,#FFFF3C;Vélo,4,4,60,40,20,0,#9BFF98;Autobus,30,25,20,20,20,180,#34FFFF;Métro,35,15,45,0,0,120,#BA78E5;Voiture (régulier),17,12,60,40,20,0,#FF7373;Voiture (heure de pointe),14,10,120,75,45,0,#FF7373",
			"(0,1);(0,4);(5,4)",
			"(5,0) (5,3) (5,5) (7,5) (7,8) (7,11) (7,14) (4,14) (0,14) (0,16) (3,16) (5,16) (8,16) (10,16) (12,16) (12,13) (12,10) (12,8) (12,5) (15,5) (15,3) (15,0) (13,0) (10,0) (8,0) (5,0);" +
					"(0,19) (0,17) (0,14) (0,12) (0,9) (3,9) (5,9) (8,9) (11,9) (14,9) (14,6) (17,6) (17,9) (19,9) (19,12) (19,15) (19,17) (19,19) (17,19) (15,19) (15,16) (12,16) (12,19) (10,19) (7,19) (4,19) (2,19) (0,19);" +
					"(9,8) (12,8) (15,8) (17,8) (19,8) (19,10) (19,12) (19,15) (19,17) (16,17) (13,17) (10,17) (10,14) (8,14) (5,14) (5,11) (5,8) (7,8) (9,8);" +
					"(19,0) (19,3) (19,6) (19,8) (19, 11) (16,11) (16,8) (16,6) (16,3) (13,3) (10,3) (7,3) (4,3) (4,6) (4,9) (2,9) (2,6) (2,3) (2,0) (4,0) (7,0) (10,0) (12,0) (15,0) (17,0) (19,0)",
			"(0,0) (0,3) (0,8) (0,12) (0,16) (0,19) (4,19) (9,19) (12,19) (15,19) (19,19) (19,16) (19,12) (19,8) (19,3) (19,0) (15,0) (12,0) (9,0) (4,0) (0,0);" +
					"(4,3) (4,8) (4,12) (4,16) (9,16) (12,16) (15,16) (15,12) (15,8) (15,3) (12,3) (9,3) (4,3);(0,8) (4,8) (9,8) (12,8) (15,8) (19,8) (19,12) (19,16) (15,16) (12,16) (9,16) (4,16) (0,16) (0,12) (0,8);" +
					"(9,0) (12,0) (15,0) (15,3) (15,8) (15,12) (15,16) (15,19) (12,19) (9,19) (9,16) (9,12) (9,8) (9,3) (9,0)"
			);

}