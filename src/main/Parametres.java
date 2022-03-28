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
	
}