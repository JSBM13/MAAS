package main;


public final class Parametres {
	String routesHorizontales;
	String routesVerticales;
	String vehicules;
	String points;
	String circuitsAutobus;
	String circuitsMetro;
	
	public Parametres(String routesHorizontales, String routesVerticales, String vehicules, String points,
			String circuitsAutobus, String circuitsMetro) {
		super();
		this.routesHorizontales = routesHorizontales;
		this.routesVerticales = routesVerticales;
		this.vehicules = vehicules;
		this.points = points;
		this.circuitsAutobus = circuitsAutobus;
		this.circuitsMetro = circuitsMetro;
	}
	
	public String toSingleString() {
		return 	routesHorizontales + "\n" +
				routesVerticales + "\n" +
				vehicules + "\n" +
				points + "\n" +
				circuitsAutobus + "\n" +
				circuitsMetro;
	}
	
	public String toString() {
		return 	"Routes Horizontales: " + routesHorizontales + "\n" +
				"Routes Verticales: " + routesVerticales + "\n" +
				"Vehicules: " + vehicules + "\n" +
				"Points: " + points + "\n" +
				"Circuits Autobus: " + circuitsAutobus + "\n" +
				"Circuits Métro: " + circuitsMetro;
	}
	
}