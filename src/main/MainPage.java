/*Auteur de cette classe: Émile Lareau
contributeurs: 
*/

package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class MainPage extends JFrame {

	// Composantes de l'interface
	private PanelDrawnMap panelMap;
	private JPanel contentPane;
	private JLabel labelTitreTrajet;
	private JLabel labelDistance;
	private JLabel labelTemps;
	private JPanel BarreDeProportionDeTrajet;
	private JButton btnConfirmerSelection;
	private JRadioButton rdbtnVoiture;
	private JRadioButton rdbtnVelo;
	private JRadioButton rdbtnMarche;
	private JRadioButton rdbtnTransportEnCommun;
	private JToggleButton btnHeureDePointe;
	private JComboBox<String> comBoxDepartX;
	private JComboBox<String> comBoxDepartY;
	private JComboBox<String> comBoxDestinationX;
	private JComboBox<String> comBoxDestinationY;
	
	// Variables d'état
	private Carte carte;
	private Parametres params;
	private ModeTransport[] vehicules;
	private Requete requete;
	
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPage frame = new MainPage();
					frame.initialiseMap();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setParams(Parametres params) {
		if (params == null) {
			this.params = Parametres.defaultParams6x5;
		} else {
			this.params = params;
		}
		
		setVehicules();
		setCarte();
		panelMap.setCarte(carte);
		populateComboBoxes();
	}
	
	public void setCarte() {
		Carte carte = null;
		try {
			carte = new Carte(params.distanceRoutesVerticales, params.distanceRoutesHorizontales, params.typesRoutesVerticales, params.typesRoutesHorizontales);
		} catch (Carte.UnequalNumberOfRoadsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Circuit[] circuits = Circuit.parseCircuits(params.circuitsAutobus, vehicules[2]);
		
		carte.setCircuits(circuits);
		
		this.carte = carte;
	}
	
	public void setVehicules() {
		
		String[] inputVehicules =  params.vehicules.split(";");
		
		ModeTransport[] vehicules = new ModeTransport[] {     //public ModeTransport(String nom, typeTransport type, boolean transportHeureReguliere, boolean transportHeurePointe, boolean transportEnCommun, String input)
				new ModeTransport("Marche", typeTransport.marche, true, true, false, inputVehicules[0]),
				new ModeTransport("Vélo", typeTransport.velo, true, true, false, inputVehicules[1]),
				new ModeTransport("Autobus", typeTransport.autobus, true, true, true, inputVehicules[2]),
				new ModeTransport("Metro", typeTransport.metro, true, true, true, inputVehicules[3]),
				new ModeTransport("Voiture", typeTransport.voiture, true, false, false, inputVehicules[4]),
				new ModeTransport("Voiture", typeTransport.voiture, false, true, false, inputVehicules[5]),
			};
		
		this.vehicules = vehicules;
	}
	
	//existe simplement pour eviter un bug dans refreshAndShowTrajet() causé par la stupidité de java
	private void refresh(Itineraire itineraire) {
		
		//refresh components (set proper text values based on selection)
		labelDistance.setText("Distance totale: " + Handler.getDistancePourHumains(itineraire.getDistance()));
		labelTemps.setText("Temps total: " + Handler.getTempsPourHumains(itineraire.getTemps()));
		labelTitreTrajet.setText(itineraire.getNom() + " : ");
		
		//show components
		labelDistance.setVisible(true);
		labelTemps.setVisible(true);
		BarreDeProportionDeTrajet.setVisible(true);
		
		
		//set itineraire de la map
		panelMap.setItineraire(itineraire);
		
		//repaint la map
		panelMap.repaint();
	}
	
	public void clearTrajet() {
																																																												
		//hide components 
		labelDistance.setVisible(false);
		labelTemps.setVisible(false);
		BarreDeProportionDeTrajet.setVisible(false);
		
		//clear drawn trajet on panelMap
		panelMap.setItineraire(null);  //clear the default trajet on start
		
		//repaint map
		panelMap.repaint();
		
		//reset selected trajet inputs
		comBoxDepartX.setSelectedIndex(0);
		comBoxDepartY.setSelectedIndex(0);
		comBoxDestinationX.setSelectedIndex(0);
		comBoxDestinationY.setSelectedIndex(0);
		btnHeureDePointe.setSelected(false);
	}
	
	public void confirmSelection() {
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
				
			requete = requeteDeTrajet;
			//calcul des itineraires possibles pour chaque type de vehicules
			requeteDeTrajet.calculateItineraires(vehicules); 
			
			
			afficherModeTransport(typeTransport.voiture);
			
			//return type: ArrayList<Itineraire>    parameters: ModeTransport[] vehicules
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void nouvelleRequete() {
		
	}
	
	public void afficherModeTransport(typeTransport moyenTransport) {
		
		int indexRequete = -1;
		
		switch (moyenTransport) {
		case marche:
			indexRequete = 0;
			break;
		case velo: 
			indexRequete = 1;
			break;
			
		case voiture:
			indexRequete = 2;
			break;
			
		case autobus:
		case metro: 
			indexRequete = 3;
			break;
			
		default:
			System.out.println("Problème dans afficherModeTransport, moyenTransport a pas été identifié.");
			
			break;
		}
		
		refresh(requete.getItineraire(indexRequete));
		
	}
	
	public void comboBoxChanged() {
		if ((String)comBoxDepartX.getSelectedItem() == (String)comBoxDestinationX.getSelectedItem() && (String)comBoxDepartY.getSelectedItem() == (String)comBoxDestinationY.getSelectedItem() ) {
			btnConfirmerSelection.setEnabled(false);
		} else {
			btnConfirmerSelection.setEnabled(true);
		}
	}
	
	public void populateComboBoxes() {
		//debut custom jComboBox code
		
		//find the amount of our map horizontal and vertical segments
		int nbSegmentsHorizontaux = carte.getNbRoutesHorizontales();
		int nbSegmentsVerticaux = carte.getNbRoutesVerticales();
		
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
		
		
		DefaultComboBoxModel<String> comboModelDepartX = new DefaultComboBoxModel<String>(strArrayMapSegmentsHorizontales);
		comBoxDepartX.setModel(comboModelDepartX);

		
		DefaultComboBoxModel<String> comboModelDepartY = new DefaultComboBoxModel<String>(strArrayMapSegmentsVerticales);
		comBoxDepartY.setModel(comboModelDepartY);

		
		DefaultComboBoxModel<String> comboModelDesinationX = new DefaultComboBoxModel<String>(strArrayMapSegmentsHorizontales);
		comBoxDestinationX.setModel(comboModelDesinationX);

		
		DefaultComboBoxModel<String> comboModelDesinationY = new DefaultComboBoxModel<String>(strArrayMapSegmentsVerticales);
		comBoxDestinationY.setModel(comboModelDesinationY);

	}
	
	public void initialiseMap() {
		
		panelMap = new PanelDrawnMap();
		panelMap.setBounds(10, 11, 614, 504);
		contentPane.add(panelMap);
		panelMap.setLayout(new BorderLayout(0, 0));
		
		setParams(null);
	}
	
	public MainPage() {
		
		
		
		setResizable(false);
		setTitle("MAAS");
		setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 876, 838);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
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
		
		labelTitreTrajet = new JLabel("Transport en commun:");
		labelTitreTrajet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelTitreTrajet.setBounds(210, 39, 305, 47);
		panelDetailsTrajet.add(labelTitreTrajet);
		
		labelDistance = new JLabel("Distance totale: xxxxkm");
		labelDistance.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labelDistance.setBounds(510, 88, 150, 32);
		panelDetailsTrajet.add(labelDistance);
		
		labelTemps = new JLabel("Temps total: xxh xxmin");
		labelTemps.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labelTemps.setBounds(272, 88, 166, 32);
		panelDetailsTrajet.add(labelTemps);
		
		BarreDeProportionDeTrajet = new JPanel();
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
		
		
		comBoxDepartX = new JComboBox<String>();
		comBoxDepartX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxChanged();
			}
		});
		comBoxDepartX.setBounds(128, 109, 78, 22);
		panelSelectionTrajet.add(comBoxDepartX);
		
		comBoxDepartY = new JComboBox<String>();
		comBoxDepartY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxChanged();
			}
		});
		comBoxDepartY.setBounds(128, 142, 78, 22);
		panelSelectionTrajet.add(comBoxDepartY);
		
		comBoxDestinationX = new JComboBox<String>();
		comBoxDestinationX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxChanged();
			}
		});
		comBoxDestinationX.setBounds(128, 215, 78, 22);
		panelSelectionTrajet.add(comBoxDestinationX);
		
		comBoxDestinationY = new JComboBox<String>();
		comBoxDestinationY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxChanged();
			}
		});
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
		
		btnHeureDePointe = new JToggleButton("faux");
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
				clearTrajet();
			}
		});
		
		rdbtnVoiture = new JRadioButton("Voiture");
		rdbtnVoiture.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rdbtnMarche = new JRadioButton("Marche");
		rdbtnMarche.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rdbtnTransportEnCommun = new JRadioButton("Transport en commun");
		rdbtnTransportEnCommun.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rdbtnVelo = new JRadioButton("V\u00E9lo");
		rdbtnVelo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		btnConfirmerSelection = new JButton("Confirmer s\u00E9lection");
		btnConfirmerSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				confirmSelection();
				
			}
		});		
		btnConfirmerSelection.setBounds(10, 410, 196, 50);
		panelSelectionTrajet.add(btnConfirmerSelection);
		
		//radio buttons content declaration
		
		//JRadioButton rdbtnVelo
		rdbtnVelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afficherModeTransport(typeTransport.velo);
			}
		});
		rdbtnVelo.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnVelo.setBounds(7, 82, 180, 40);
		panelDetailsTrajet.add(rdbtnVelo);
		
		//JRadioButton rdbtnTransportEnCommun
		rdbtnTransportEnCommun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Autobus = transport en commun;
				afficherModeTransport(typeTransport.autobus);
			}
		});
		rdbtnTransportEnCommun.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnTransportEnCommun.setBounds(6, 168, 180, 40);
		panelDetailsTrajet.add(rdbtnTransportEnCommun);
		
		//JRadioButton rdbtnMarche
		rdbtnMarche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afficherModeTransport(typeTransport.marche);
			}
		});
		rdbtnMarche.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnMarche.setBounds(6, 125, 180, 40);
		panelDetailsTrajet.add(rdbtnMarche);
		
		//JRadioButton rdbtnVoiture
		rdbtnVoiture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afficherModeTransport(typeTransport.voiture);
			}
		});
		rdbtnVoiture.setSelected(true);
		rdbtnVoiture.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnVoiture.setBounds(7, 39, 180, 40);
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
		
		JButton btnExporterParams = new JButton("Exporter les param\u00E8tres...");
		btnExporterParams.setBounds(650, 765, 200, 23);
		contentPane.add(btnExporterParams);
		
		JButton btnImporterParams = new JButton("Importer des param\u00E8tres");
		btnImporterParams.setBounds(440, 765, 200, 23);
		contentPane.add(btnImporterParams);
		
		JButton btnModifierParams = new JButton("Modifier les param\u00E8tres");
		btnModifierParams.setBounds(230, 765, 200, 23);
		contentPane.add(btnModifierParams);
		
		comboBoxChanged();
		
		
		
	}
}
