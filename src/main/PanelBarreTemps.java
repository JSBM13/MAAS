package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelBarreTemps extends JPanel{
	
	private Itineraire itineraire;
	
	public PanelBarreTemps() {
		super();
		itineraire = null;
	}
	
	//Fonction de base paint, c'est exécuté chaque fois que le JVM pense que c'est necessaire de paint, comme lors d'une minimisation-maximization
	public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g; //Cast Graphics g to a Graphics2D, Updated version more methods
        
        g2D.setPaint(Color.white); //Color
        g2D.fillRect(0, 0, getWidth(), getHeight()); //Create rectangle with fill
        
        if (itineraire != null) {
        	int temps = itineraire.getTemps();
        	double position = 0;
        	for (int i = 0; i < itineraire.getTrajets().size(); i++) {
            	Trajet trajet = itineraire.getTrajets(i);
            	double length = 1.0 * trajet.getTemps() / temps;
            	paintSection(g2D, position, position + length, trajet.getVehicule().getCouleur(), trajet.getVehicule().getNom(), Handler.getDistancePourHumains(trajet.getDistance()), Handler.getTempsPourHumains(trajet.getTemps()));
            	position = position + length;
            }
        }
        
	}
	
	public void paintSection(Graphics2D g2D, double p1, double p2, Color color, String titre, String temps, String distance) {
		
		int leftPoint = (int)( p1 * getWidth());
		int width = (int) ((p2 - p1) * getWidth());
		
		g2D.setPaint(color);
		g2D.fillRect(leftPoint, 0, width, getHeight());
		Font fontGras = new Font ("Arial", Font.BOLD, 12);
		Font fontNormal = new Font ("Arial", Font.PLAIN, 12);
		drawCenteredString(g2D, titre, new Rectangle(leftPoint, 0, width, (int) (getHeight() * 0.40)), fontGras);
		drawCenteredString(g2D, temps, new Rectangle(leftPoint, (int) (getHeight() * 0.40), width, (int) (getHeight() * 0.30)), fontNormal);
		drawCenteredString(g2D, distance, new Rectangle(leftPoint, (int) (getHeight() * 0.70), width, (int) (getHeight() * 0.30)), fontNormal);
	}
		
	/**
	 * Draw a String centered in the middle of a Rectangle.
	 * 
	 * Code trouvé ici: https://stackoverflow.com/a/27740330
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	public void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
	    
		// Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    g.setPaint(Color.black);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	public void setItineraire(Itineraire itineraire) {
		this.itineraire = itineraire;
	}
	
	public void clearItineraire() {
		this.itineraire = null;
	}
	
}
