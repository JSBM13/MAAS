package main;

import main.Carte.directions;

enum typesIntersection {
	PrincPrinc, PrincSec, SecSec, undefined;
}

public class Intersection {
	private int x;
	private int y;
	private typesIntersection type;
	
	public Intersection(int x, int y, typesIntersection type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public Intersection(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = typesIntersection.undefined;
	}
	
	
	/**
	 * Bouge l'intersection actuelle par une certaine distance dans la direction spécifiée.
	 * Utiliser la fonction movedFrom() pour obtenir une nouvelle intersection.
	 * @param direction La direction dans laquelle on déplace l'intersection
	 * @param distance Le nombre de pas
	 * @return Cette intersection.
	 */
	public Intersection move(directions direction, int distance) {
		
		if (direction == directions.est) x += distance;
		else if (direction == directions.ouest) x -= distance;
		else if (direction == directions.nord) y += distance;
		else if (direction == directions.sud) y -= distance;
		return this;
	}
	
	/**
	 * Retourne une nouvelle intersection ayant bougé un certain nombre de pas dans la direction indiquée depuis l'intersection actuelle.
	 * À noter que le type d'Intersection sera toujours réinitialisé à undefined.
	 * @param direction La direction dans laquelle on déplace l'intersection
	 * @param distance Le nombre de pas
	 * @return Une nouvelle intersection, ayant sa position modifiée.
	 */
	public Intersection movedFrom(directions direction, int distance) {
		return this.clone().move(direction,  distance);
	}
	

	public int delta(Intersection intersection) throws Exception {
		return Math.abs(intersection.getX() - x) + Math.abs(intersection.getY() - y);
	}
	
	public Intersection clone() {
		return new Intersection(x, y, type);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public typesIntersection getType() {
		return type;
	}
	
	public void setType(typesIntersection type) {
		this.type = type;
	}
	
	public String toString( ) {
		return "(" + x + "," + y +")";
	}
}
