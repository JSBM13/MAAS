package main;

public class UtilitaireString {
	static public String getTempsPourHumains(int temps) {
		int heures = temps / 3600;
		int minutes = (temps - (heures * 3600)) / 60;
		int secondes = (temps - ((heures * 3600) + (minutes * 60)));
		return (heures != 0 ? heures + "h" : "") + 
				(minutes != 0 || heures != 0 ? (minutes < 10 ? "0" : "") + minutes + "m" : "") + 
				(secondes != 0 ? (secondes < 10 ? "0" : "") + secondes + "s" : "");
	}
	
	static public String getDistancePourHumains(int distance) {
		if (distance < 1000) return distance + " m";
		else return "%.1f km".formatted(distance / 1000.0);
	}
}
