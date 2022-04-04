/*Auteur de cette classe: Émile Lareau
contributeurs: 
*/

package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.awt.event.ActionEvent;

public class MainPage extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPage frame = new MainPage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void refreshAndShowTrajet(JPanel panelMap, Requete requete, JLabel labelTitreTrajet, JLabel labelDistance, JLabel labelTemps, JPanel BarreDeProportionDeTrajet, JRadioButton rdbtnVoiture, JRadioButton rdbtnVelo, JRadioButton rdbtnMarche, JRadioButton rdbtnTransportEnCommun) {
		//find the selected vehicle option by user
		typeTransport vehicleSelected; //defaults to this in case of error
		
		if (rdbtnVoiture.isSelected()) {
			vehicleSelected = typeTransport.voiture;
			
		} else if (rdbtnVelo.isSelected()){
			vehicleSelected = typeTransport.velo;
			
		} else if (rdbtnMarche.isSelected()){
			vehicleSelected = typeTransport.marche;
			
		} else if (rdbtnTransportEnCommun.isSelected()){
			vehicleSelected = typeTransport.autobus;
			
		}else{
			System.out.println("Aucune selection de vehicule faite. refresh failed");
			labelTitreTrajet.setText("Choississez un moyen de transport!");
			return;
		}
		
		//get itineraires de la requete
		ArrayList<Itineraire> itineraires =  requete.getItineraires();
		
		int itineraireToDrawIndex = 0;

		for(int i = 0; i<itineraires.size() ;i++){
			if (itineraires.get(i).isVehiculeUsed(vehicleSelected)){
				if (vehicleSelected == typeTransport.marche && itineraires.get(i).getVehicules().size() == 1) {  //marche is used by both buses rides and walks. we differentiate the two by knowing bus rides use multiple  types of vehicle
					//then we know its a walk
					itineraireToDrawIndex = i;
					refresh(panelMap, itineraires , itineraireToDrawIndex, vehicleSelected,  labelTitreTrajet,labelDistance,  labelTemps, BarreDeProportionDeTrajet);
					break;
				}
				itineraireToDrawIndex = i;
				refresh(panelMap, itineraires , itineraireToDrawIndex, vehicleSelected,  labelTitreTrajet,labelDistance,  labelTemps, BarreDeProportionDeTrajet);
				break;
			}
		}
		
		//because java is a terrible language full of stupid intricacies, you cannot simply put the content of the refresh() function here, because the code KEEPS EXECUTING WITHOUT WAITING FOR THE FOR LOOP TO FINISH,
		//resulting in an incorrect value for itineraireToDrawIndex. I've never had such a strong disdain for a programming language until now.
		
	}
	//existe simplement pour eviter un bug dans refreshAndShowTrajet() causé par la stupidité de java
	private void refresh(JPanel panelMap, ArrayList<Itineraire> itineraires , int itineraireToDrawIndex, typeTransport vehicleSelected, JLabel labelTitreTrajet, JLabel labelDistance, JLabel labelTemps, JPanel BarreDeProportionDeTrajet) {
		
		//refresh components (set proper text values based on selection)
		labelDistance.setText("Distance totale: " + itineraires.get(itineraireToDrawIndex).getDistance());
		labelTemps.setText("Temps total: " + Handler.getTempsPourHumains(itineraires.get(itineraireToDrawIndex).getTemps()));
		labelTitreTrajet.setText(vehicleSelected + " : ");
		
		//show components
		labelDistance.setVisible(true);
		labelTemps.setVisible(true);
		BarreDeProportionDeTrajet.setVisible(true);
		
		
		//set itineraire de la map
		System.out.println(itineraireToDrawIndex + "!!!EWGNGW");
		((PanelDrawnMap) panelMap).setItineraire(itineraires.get(itineraireToDrawIndex));
		
		//repaint la map
		((PanelDrawnMap) panelMap).repaint();
	}
	
	public void clearTrajet(JPanel panelMap, JLabel labelTitreTrajet, JLabel labelDistance, JLabel labelTemps, JPanel BarreDeProportionDeTrajet,JComboBox comBoxDepartX,JComboBox comBoxDepartY , JComboBox comBoxDestinationX,JComboBox comBoxDestinationY, JToggleButton btnHeureDePointe) {
																																																												
		//hide components 
		labelDistance.setVisible(false);
		labelTemps.setVisible(false);
		BarreDeProportionDeTrajet.setVisible(false);
		
		//clear drawn trajet on panelMap
		((PanelDrawnMap) panelMap).setItineraire(null);  //clear the default trajet on start
		
		//repaint map
		((PanelDrawnMap) panelMap).repaint();
		
		//reset selected trajet inputs
		comBoxDepartX.setSelectedIndex(0);
		comBoxDepartY.setSelectedIndex(0);
		comBoxDestinationX.setSelectedIndex(0);
		comBoxDestinationY.setSelectedIndex(0);
		btnHeureDePointe.setSelected(false);
	}
	
	public MainPage() {
		setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 876, 804);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelMap = new PanelDrawnMap();
		panelMap.setBounds(10, 11, 614, 504);
		contentPane.add(panelMap);
		panelMap.setLayout(new BorderLayout(0, 0));
		
		JPanel panelDetailsTrajet = new JPanel();
		panelDetailsTrajet.setBounds(10, 526, 840, 228);
		contentPane.add(panelDetailsTrajet);
		panelDetailsTrajet.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("Moyen de transport");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(0, 0, 194, 32);
		panelDetailsTrajet.add(lblNewLabel_4);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 35, 176, 13);
		panelDetailsTrajet.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		separator_2.setBounds(193, 11, 13, 206);
		panelDetailsTrajet.add(separator_2);
		
		JSeparator separator_1_1 = new JSeparator();
		separator_1_1.setBounds(200, 35, 630, 13);
		panelDetailsTrajet.add(separator_1_1);
		
		JLabel TitreDetailTrajet = new JLabel("D\u00E9tails du trajet");
		TitreDetailTrajet.setHorizontalAlignment(SwingConstants.CENTER);
		TitreDetailTrajet.setFont(new Font("Tahoma", Font.PLAIN, 20));
		TitreDetailTrajet.setBounds(204, 0, 626, 32);
		panelDetailsTrajet.add(TitreDetailTrajet);
		
		JLabel lblNewLabel_5 = new JLabel(" Voiture");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_5.setBounds(10, 39, 183, 47);
		panelDetailsTrajet.add(lblNewLabel_5);
		
		JLabel lblNewLabel_5_1 = new JLabel(" V\u00E9lo");
		lblNewLabel_5_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_5_1.setBounds(10, 88, 150, 47);
		panelDetailsTrajet.add(lblNewLabel_5_1);
		
		JLabel lblNewLabel_5_2 = new JLabel(" Marche");
		lblNewLabel_5_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_5_2.setBounds(11, 136, 150, 47);
		panelDetailsTrajet.add(lblNewLabel_5_2);
		
		JLabel lblNewLabel_5_3 = new JLabel(" Transport en commun");
		lblNewLabel_5_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_5_3.setBounds(11, 181, 150, 47);
		panelDetailsTrajet.add(lblNewLabel_5_3);
		
		JLabel labelTitreTrajet = new JLabel("Transport en commun:");
		labelTitreTrajet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelTitreTrajet.setBounds(210, 39, 305, 47);
		panelDetailsTrajet.add(labelTitreTrajet);
		
		JLabel labelDistance = new JLabel("Distance totale: xxxxkm");
		labelDistance.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labelDistance.setBounds(510, 88, 150, 32);
		panelDetailsTrajet.add(labelDistance);
		
		JLabel labelTemps = new JLabel("Temps total: xxh xxmin");
		labelTemps.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labelTemps.setBounds(272, 88, 166, 32);
		panelDetailsTrajet.add(labelTemps);
		
		JPanel BarreDeProportionDeTrajet = new JPanel();
		BarreDeProportionDeTrajet.setBackground(Color.LIGHT_GRAY);
		BarreDeProportionDeTrajet.setBounds(200, 146, 630, 71);
		panelDetailsTrajet.add(BarreDeProportionDeTrajet);
		BarreDeProportionDeTrajet.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.GREEN);
		panel_4.setBounds(10, 11, 140, 49);
		BarreDeProportionDeTrajet.add(panel_4);
		panel_4.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel_8_1 = new JLabel("Marche");
		lblNewLabel_8_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_8_1);
		
		JLabel lblNewLabel_8_2 = new JLabel("4 min");
		lblNewLabel_8_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_8_2);
		
		JLabel lblNewLabel_8 = new JLabel("0.2 km");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_8);
		
		JPanel panel_4_1 = new JPanel();
		panel_4_1.setBackground(Color.ORANGE);
		panel_4_1.setBounds(150, 11, 208, 49);
		BarreDeProportionDeTrajet.add(panel_4_1);
		panel_4_1.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel_8_1_1 = new JLabel("Autobus");
		lblNewLabel_8_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1.add(lblNewLabel_8_1_1);
		
		JLabel lblNewLabel_8_2_1 = new JLabel("14 min");
		lblNewLabel_8_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1.add(lblNewLabel_8_2_1);
		
		JLabel lblNewLabel_8_3 = new JLabel("2 km");
		lblNewLabel_8_3.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1.add(lblNewLabel_8_3);
		
		JPanel panel_4_1_1 = new JPanel();
		panel_4_1_1.setBackground(Color.YELLOW);
		panel_4_1_1.setBounds(358, 11, 182, 49);
		BarreDeProportionDeTrajet.add(panel_4_1_1);
		panel_4_1_1.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel_8_1_1_1 = new JLabel("Autobus");
		lblNewLabel_8_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1_1.add(lblNewLabel_8_1_1_1);
		
		JLabel lblNewLabel_8_2_1_1 = new JLabel("14 min");
		lblNewLabel_8_2_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1_1.add(lblNewLabel_8_2_1_1);
		
		JLabel lblNewLabel_8_3_1 = new JLabel("2 km");
		lblNewLabel_8_3_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_1_1.add(lblNewLabel_8_3_1);
		
		JPanel panel_4_2 = new JPanel();
		panel_4_2.setBackground(Color.GREEN);
		panel_4_2.setBounds(540, 11, 80, 49);
		BarreDeProportionDeTrajet.add(panel_4_2);
		panel_4_2.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel_8_1_2 = new JLabel("Marche");
		lblNewLabel_8_1_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_2.add(lblNewLabel_8_1_2);
		
		JLabel lblNewLabel_8_2_2 = new JLabel("3 min");
		lblNewLabel_8_2_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_2.add(lblNewLabel_8_2_2);
		
		JLabel lblNewLabel_8_4 = new JLabel("0.15 km");
		lblNewLabel_8_4.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4_2.add(lblNewLabel_8_4);
		
		JPanel panelSelectionTrajet = new JPanel();
		panelSelectionTrajet.setBounds(634, 11, 216, 504);
		contentPane.add(panelSelectionTrajet);
		panelSelectionTrajet.setLayout(null);
		
		JLabel TitreSelectionTrajet = new JLabel("S\u00E9lection du trajet");
		TitreSelectionTrajet.setFont(new Font("Tahoma", Font.PLAIN, 20));
		TitreSelectionTrajet.setHorizontalAlignment(SwingConstants.CENTER);
		TitreSelectionTrajet.setBounds(10, 11, 196, 54);
		panelSelectionTrajet.add(TitreSelectionTrajet);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 76, 196, 22);
		panelSelectionTrajet.add(separator);
		
		
		//debut custom jComboBox code
		
		//find the amount of our map horizontal and vertical segments
		int nbSegmentsHorizontaux = ((PanelDrawnMap) panelMap).getCarte().getNbRoutesHorizontales();
		int nbSegmentsVerticaux = ((PanelDrawnMap) panelMap).getCarte().getNbRoutesVerticales();
		
		//make arrays of size 0-amount of horizontal and vertical segments on our drawn map like so: arrayMapSegmentsHorizontaux = {0,1,2,3,4,5}
		int[] intArrayMapSegmentsHorizontaux = IntStream.rangeClosed(0, nbSegmentsHorizontaux ).toArray();  //makes an array of range 0- nb segments horizontaux dans map. type casting required because panelMap is type JPanel not PanelDrawnMap
		int[] intArrayMapSegmentsVerticaux = IntStream.rangeClosed(0, nbSegmentsVerticaux ).toArray();  //type casting required because panelMap is type JPanel not Map
		String[] strArrayMapSegmentsHorizontales = new String[nbSegmentsVerticaux]; //j'ai pas pris le temps de penser pourquoi il faut les inverser mais c'est nécessaire
		String[] strArrayMapSegmentsVerticales = new String[nbSegmentsHorizontaux];
		
		//transform the above arrays into arrays of strings[], because JComboBoxes require that as input
		for (int i = 0; i < nbSegmentsHorizontaux ; i++ ) {
			strArrayMapSegmentsVerticales[i] = Integer.toString(intArrayMapSegmentsHorizontaux[i]);
		}
		for (int i = 0; i < nbSegmentsVerticaux ; i++ ) {
			strArrayMapSegmentsHorizontales[i] = Integer.toString(intArrayMapSegmentsVerticaux[i]);
		}
		
		
		JComboBox comBoxDepartX = new JComboBox(strArrayMapSegmentsHorizontales);
		comBoxDepartX.setBounds(128, 109, 78, 22);
		panelSelectionTrajet.add(comBoxDepartX);
		
		JComboBox comBoxDepartY = new JComboBox(strArrayMapSegmentsVerticales);
		comBoxDepartY.setBounds(128, 142, 78, 22);
		panelSelectionTrajet.add(comBoxDepartY);
		
		JComboBox comBoxDestinationX = new JComboBox(strArrayMapSegmentsHorizontales);
		comBoxDestinationX.setBounds(128, 215, 78, 22);
		panelSelectionTrajet.add(comBoxDestinationX);
		
		JComboBox comBoxDestinationY = new JComboBox(strArrayMapSegmentsVerticales);
		comBoxDestinationY.setBounds(128, 248, 78, 22);
		panelSelectionTrajet.add(comBoxDestinationY);
		
		//fin custom code jComboBox
		
		
		JLabel pointDepartX = new JLabel("Point de d\u00E9part X:");
		pointDepartX.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pointDepartX.setBounds(10, 112, 108, 14);
		panelSelectionTrajet.add(pointDepartX);
		
		JLabel lblNewLabel_3 = new JLabel("Heure de pointe");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_3.setBounds(10, 347, 108, 14);
		panelSelectionTrajet.add(lblNewLabel_3);
		
		JToggleButton btnHeureDePointe = new JToggleButton("faux");
		btnHeureDePointe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnHeureDePointe.isSelected()) {
					btnHeureDePointe.setText("Vrai");
				} else {
					btnHeureDePointe.setText("Faux");
				}
			}
		});
		btnHeureDePointe.setBounds(128, 325, 78, 60);
		panelSelectionTrajet.add(btnHeureDePointe);
	
		JButton btnCancellerTrajet = new JButton("Canceller Trajet");
		btnCancellerTrajet.setBounds(40, 471, 136, 22);
		panelSelectionTrajet.add(btnCancellerTrajet);
		btnCancellerTrajet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTrajet(panelMap, labelTitreTrajet, labelDistance, labelTemps, BarreDeProportionDeTrajet, comBoxDepartX,comBoxDepartY ,comBoxDestinationX, comBoxDestinationY, btnHeureDePointe);
			}
		});
		
		JRadioButton rdbtnVoiture = new JRadioButton("");
		JRadioButton rdbtnMarche = new JRadioButton("");
		JRadioButton rdbtnTransportEnCommun = new JRadioButton("");
		JRadioButton rdbtnVelo = new JRadioButton("");
		
		JButton btnConfirmerSelection = new JButton("Confirmer s\u00E9lection");
		btnConfirmerSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Carte carte = ((PanelDrawnMap) panelMap).getCarte();
				Intersection depart = new Intersection(Integer.parseInt((String)comBoxDepartX.getSelectedItem()), Integer.parseInt((String)comBoxDepartY.getSelectedItem()));
				Intersection destination = new Intersection(Integer.parseInt((String)comBoxDestinationX.getSelectedItem()), Integer.parseInt((String)comBoxDestinationY.getSelectedItem()));
				Boolean heureDePointe;
				if (btnHeureDePointe.isSelected()){
					heureDePointe = true;
				} else {
					heureDePointe = false;
				}

				try {
						
					Requete requeteDeTrajet = new Requete(carte, depart, destination, heureDePointe); //Carte carte, Intersection depart, Intersection destination, boolean heureDePointe
						
					//calcul des itineraires possibles pour chaque type de vehicules
					requeteDeTrajet.calculateItineraires(Handler.vehicules); 
					
					
					refreshAndShowTrajet(panelMap, requeteDeTrajet,labelTitreTrajet, labelDistance, labelTemps, BarreDeProportionDeTrajet, rdbtnVoiture, rdbtnVelo, rdbtnMarche, rdbtnTransportEnCommun);
					
					
					//OLD ATTEMPT AT IMPLEMENTATION
					/* types de vehicule
					vehicules[0] = new ModeTransport("Marche", typeTransport.marche, true, true, false, inputVehicules[0]);
					vehicules[1] = new ModeTransport("Vélo", typeTransport.velo, true, true, false, inputVehicules[1]);
					vehicules[2] = new ModeTransport("Autobus", typeTransport.autobus, true, true, true, inputVehicules[2]);
					vehicules[3] = new ModeTransport("Metro", typeTransport.metro, true, true, true, inputVehicules[3]);
					vehicules[4] = new ModeTransport("Voiture", typeTransport.voiture, true, false, false, inputVehicules[4]);
					*/
					
					
					//return type: ArrayList<Itineraire>    parameters: ModeTransport[] vehicules
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});		
		btnConfirmerSelection.setBounds(10, 410, 196, 50);
		panelSelectionTrajet.add(btnConfirmerSelection);
		
		//radio buttons content declaration
		
		//JRadioButton rdbtnVelo
		rdbtnVelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(ActionListener a: btnConfirmerSelection.getActionListeners()) {
				    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
				}
			}
		});
		rdbtnVelo.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnVelo.setBounds(166, 88, 27, 47);
		panelDetailsTrajet.add(rdbtnVelo);
		
		//JRadioButton rdbtnTransportEnCommun
		rdbtnTransportEnCommun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//call le action event du confirm button directement
				for(ActionListener a: btnConfirmerSelection.getActionListeners()) {
				    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
				}
			}
		});
		rdbtnTransportEnCommun.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnTransportEnCommun.setBounds(167, 181, 27, 47);
		panelDetailsTrajet.add(rdbtnTransportEnCommun);
		
		//JRadioButton rdbtnMarche
		rdbtnMarche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//call le action event du confirm button directement
				for(ActionListener a: btnConfirmerSelection.getActionListeners()) {
				    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
				}
			}
		});
		rdbtnMarche.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnMarche.setBounds(167, 136, 27, 47);
		panelDetailsTrajet.add(rdbtnMarche);
		
		//JRadioButton rdbtnVoiture
		rdbtnVoiture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//call le action event du confirm button directement
				for(ActionListener a: btnConfirmerSelection.getActionListeners()) {
				    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
				}
			}
		});
		rdbtnVoiture.setSelected(true);
		rdbtnVoiture.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnVoiture.setBounds(166, 39, 27, 47);
		panelDetailsTrajet.add(rdbtnVoiture);
		
		JLabel pointDepartY = new JLabel("Point de d\u00E9part Y:");
		pointDepartY.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pointDepartY.setBounds(10, 145, 108, 14);
		panelSelectionTrajet.add(pointDepartY);
		
		JLabel DestinationX = new JLabel("Destination X:");
		DestinationX.setFont(new Font("Tahoma", Font.PLAIN, 13));
		DestinationX.setBounds(10, 218, 108, 14);
		panelSelectionTrajet.add(DestinationX);
		
		JLabel lblDestinationY = new JLabel("Destination Y:");
		lblDestinationY.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblDestinationY.setBounds(10, 251, 108, 14);
		panelSelectionTrajet.add(lblDestinationY);
		
		//custom code 
		ButtonGroup selectionTransport = new javax.swing.ButtonGroup();
		selectionTransport.add(rdbtnVoiture);
		selectionTransport.add(rdbtnVelo);
		selectionTransport.add(rdbtnMarche);
		selectionTransport.add(rdbtnTransportEnCommun);
		
	}
}
