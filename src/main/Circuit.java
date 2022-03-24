package main;

public final class Circuit {
	public Intersection[] arrets;
	public ModeTransport vehicule;
	
	public Circuit(ModeTransport vehicule, String pointsArray) {
		parsePoints(pointsArray);
		this.vehicule = vehicule;
		
	}
	
	private void parsePoints(String pointsArray) {
		String[] pointsText = pointsArray.split(" ");
		arrets = new Intersection[pointsText.length];
		for (int i = 0; i < pointsText.length; i++) {
			String s = pointsText[i];
			int x, y;
			x = Integer.parseInt(s.substring(1,s.indexOf(',')));
			y = Integer.parseInt(s.substring(s.indexOf(',') + 1, s.length() - 1));
			arrets[i] = new Intersection(x,y);
		}
		
	}
	
	public String toString() {
		String s = "Circuit: [ ";
		for (Intersection p : arrets) {
			s += p.toString() + " ";
		}
		return s + "]";
	}
	
	public Intersection getArret(int index) {
		return arrets[index % arrets.length];
	}
	
	public Intersection[] getArrets() {
		return arrets;
	}
	
	public int getNbArrets() {
		return arrets.length;
	}
}
