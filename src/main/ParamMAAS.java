package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.BorderLayout;


@SuppressWarnings("serial")
public class ParamMAAS extends JDialog {

	// Composantes de l'interface
	private JPanel contentPane;
	private JPanel panelMapRoutesVerticales;
	private JPanel panelMapRoutesHorizontales;
	private JPanel panelMapCircuitsAutobus;
	private JPanel panelMapCircuitsMetro;
	private JTable tableRoutesVerticales;
	private JTable tableRoutesHorizontales;
	private JTable tableVehicules;
	private JTable tablePoints;
	private JTable tableAutobus;
	private JTable tableMetro;

	// Cartes dynamiques
	private PanelDrawnMap mapRoutesVerticales;
	private PanelDrawnMap mapRoutesHorizontales;
	private PanelDrawnMap mapCircuitsAutobus;
	private PanelDrawnMap mapCircuitsMetro;
	
	// Paramètres à retourner, si l'utilisateur clique Okay.
	private Parametres resultat;
	
	// Tourne à vrai quand l'interface a fini de s'initialiser, pour éviter 
	// que l'on détecte les changements dans les JTable avant que ça soit voulu.
	private boolean ready = false;
	
	/**
	 * Quand on annule, simplement fermer la fenêtre sans rien renvoyer.
	 */
	public void cancel() {
		dispose();
	}
	
	/**
	 * Quand on clique sur okay, on récupère les données entrées par l'utilisateur et on le renvoie comme objet Paramètres.
	 */
	public void ok() {
		String distanceRoutesVerticales = extractRouteDistanceData(tableRoutesVerticales);
		String distanceRoutesHorizontales = extractRouteDistanceData(tableRoutesHorizontales);
		String typesRoutesVerticales = extractRouteTypeData(tableRoutesVerticales);
		String typesRoutesHorizontales = extractRouteTypeData(tableRoutesHorizontales);
		String vehicules = extractVehiculesData();
		String points = extractPointData();
		String circuitsAutobus = extractCircuitsData(tableAutobus);
		String circuitsMetro = extractCircuitsData(tableMetro);
		resultat = new Parametres(distanceRoutesVerticales, distanceRoutesHorizontales, typesRoutesVerticales, typesRoutesHorizontales, vehicules, points, circuitsAutobus, circuitsMetro);
		
		dispose();
	}
	
	/**
	 * Permet d'afficher la fenêtre et de renvoyer les informations entrées si l'utilisateur clique sur Okay.
	 * @param initial Les paramètres initiaux à utiliser pour remplir les formulaires.
	 * @return Les paramètres entrés par l'utilisateur s'il clique sur okay, ou null s'il annule.
	 */
	public Parametres showDialog(Parametres initial) {
		populate(initial);
		setVisible(true);
		return resultat;
	}
	
	/**
	 * Récupère les données entrées de distance dans un des formulaires de routes.
	 * @param table La table contenant les données à récupérer.
	 * @return Les paramètres sous forme de String parsable par Carte.
	 */
	public String extractRouteDistanceData(JTable table) {
		TableModel model = table.getModel();
		if (table.getRowCount() > 1) {
			String[] routes = new String[table.getRowCount() - 1];
			for (int i = 1; i < model.getRowCount(); i++) {
				routes[i - 1] = model.getValueAt(i, 2).toString();
			}
			return String.join(",", routes);
		}
		return "";
	}
	
	/**
	 * Récupère les données de type de routes entrées dans un formulaire de route.
	 * @param table La table contenant les données à récupérer.
	 * @return Les paramètres de types de route, sous la forme d'un String parsable par Carte.
	 */
	public String extractRouteTypeData(JTable table) {
		TableModel model = table.getModel();
		String routes = "";
		for (int i = 0; i < model.getRowCount(); i++) {
			routes += ((boolean)model.getValueAt(i, 1) ? "P" : "S" );
		}
		return routes;
	}
	
	/**
	 * Ajoute une ligne à la table donnée, contenant les données fournies.
	 * @param table La table qu'on souhaite ajouter une ligne.
	 * @param obj L'objet contenant les données à ajouter.
	 */
	public void addRow(JTable table, Object[] obj) {
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		tableModel.addRow(obj);
		table.setModel(tableModel);
	}
	
	/**
	 * Retire la ligne sélectionnée par l'utilisateur.
	 * @param table La table que l'on souhaite retirer une ligne.
	 */
	public void deleteRow(JTable table) {
		if (table.getSelectedRowCount() > 0) {
			DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
			tableModel.removeRow(table.getSelectedRow());
			for (int i = 0 ; i < tableModel.getRowCount(); i++) {
				tableModel.setValueAt(i, i, 0);
			}
			table.setModel(tableModel);
		}
	}
	
	/**
	 * Remplis la fenêtre avec des paramètres données.
	 * @param initial L'objet Paramètres contenant les données à insérer.
	 */
	public void populate(Parametres initial) {
		Parametres paramInitiaux = initial;
		if (initial == null) {
			paramInitiaux = Parametres.defaultParams6x5;
		}
		populateRoutes(tableRoutesVerticales, paramInitiaux.distanceRoutesVerticales, paramInitiaux.typesRoutesVerticales );
		populateRoutes(tableRoutesHorizontales, paramInitiaux.distanceRoutesHorizontales, paramInitiaux.typesRoutesHorizontales);
		populateVehicules(paramInitiaux.vehicules);
		populatePoints(paramInitiaux.points);
		populateCircuits(tableAutobus, paramInitiaux.circuitsAutobus);
		populateCircuits(tableMetro, paramInitiaux.circuitsMetro);
	}
	
	/**
	 * Permet de remplir une table de routes avec les données fournies.
	 * @param table La table à remplir
	 * @param paramsDistance Les paramètres de distances, sous la forme de String parsable par Carte.
	 * @param paramsType Les paramètres de routes, sous la forme de String parsable par Carte.
	 */
	public void populateRoutes(JTable table, String paramsDistance, String paramsType) {
		Route[] routes = parseRoutes(paramsDistance, paramsType);
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		for (int i = 0; i < routes.length; i++) {
			Route route = routes[i];
			tableModel.addRow(new Object[]{i, route.principale, route.distance});
		}
		table.setModel(tableModel);
	}
	
	/**
	 * Prend des paramètres de type et de distances de routes et retourne les données contenues sous forme d'array d'objets Route.
	 * @param paramsDistance Les données de distance, sous forme de String parsable par Carte.
	 * @param paramType Les données de types d eroutes, sous forme de String parsable par Carte.
	 * @return Le tableau de routes associés.
	 */
	public Route[] parseRoutes(String paramsDistance, String paramType) {
		String[] distances = paramsDistance.split(",");
		Route[] output = new Route[paramType.length()];
		
		output[0] = new Route(0, (paramType.charAt(0) == 'P'));
		for (int i = 1; i < paramType.length(); i++) {
			String route = distances[i - 1];
			output[i] = new Route(Integer.parseInt(route), (paramType.charAt(i) == 'P'));
		}
		
		return output;
	}
	
	/**
	 * Remplie le tableau des véhicules avec les paramètres fournis.
	 * @param params Un String contenant les données, dans le même format que prend Carte.
	 */
	public void populateVehicules(String params) {
		TableModel table = tableVehicules.getModel();
		String[] vehicules = params.split(";");
		for (int i = 0; i < vehicules.length; i++) {
			String[] val = vehicules[i].split(",");
			table.setValueAt(val[0], i, 0);
			for (int j = 1 ; j < 7; j++) {
				table.setValueAt(Integer.parseInt(val[j].trim()), i, j);
			}
			table.setValueAt(val[7], i, 7);
		}
		tableVehicules.setModel(table);
	}
	
	/**
	 * Récupère les données entrées dans le tableau des véhicules.
	 * @return Les paramètres de véhicules.
	 */
	public String extractVehiculesData() {
		TableModel model = tableVehicules.getModel();
		String[] vehicules = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			String[] val = new String[model.getColumnCount()];
			val[0] = (String)model.getValueAt(i, 0);
			for (int j = 1; j < 7; j++) {
				val[j] = (Integer.toString((Integer)model.getValueAt(i, j)));
			}
			val[7] = (String)model.getValueAt(i, 7);
			vehicules[i] = String.join(",", val);
		}
		return String.join(";", vehicules);
	}
	
	/**
	 * Remplis le formulaire des points avec la liste de points fournis.
	 * @param params La liste de points à utiliser.
	 */
	public void populatePoints(String params) {
		DefaultTableModel table = (DefaultTableModel)tablePoints.getModel();
		String[] points = params.replace("(", "").replace(")", "").split(";");
		for (int i = 0; i < points.length; i++) {
			String[] val = points[i].split(",");
			table.addRow(new Object[]{i, val[0], val[1]});
		}
		tablePoints.setModel(table);
	}
	
	/**
	 * Récupère la liste des points entrés dans le formulaire.
	 * @return La liste de points, sous forme d'un String qui peut être parser par Intersection.parseIntersections().
	 */
	public String extractPointData() {
		TableModel model = tablePoints.getModel();
		String[] points = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			points[i] = "(" + (String)model.getValueAt(i,1) + "," + (String)model.getValueAt(i,2) + ")";
		}
		return String.join(";", points);
	}
	
	/**
	 * Remplis le formulaire de circuits fournis avec les paramètres donnés.
	 * @param params La liste de circuits à insérer dans le tableau.
	 * @param table La table à remplir.
	 */
	public void populateCircuits(JTable table, String params) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		String[] circuits = params.split(";");
		for (int i = 0; i < circuits.length; i++) {
			model.addRow(new Object[]{i, circuits[0]});
		}
		table.setModel(model);
	}
	
	/**
	 * Récupère les données de circuits entrés par l'utilisateur.
	 * @param table Le tableau à récupérer.
	 * @return Les données de circuits, sous forme d'un String parsable par Circuit.
	 */
	public String extractCircuitsData(JTable table) {
		TableModel model = table.getModel();
		String[] circuits = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			circuits[i] = (String)model.getValueAt(i,1);
		}
		return String.join(";", circuits);
	}
	
	public Carte parseRoutesAsCartes() {
		String distanceRoutesVerticales = extractRouteDistanceData(tableRoutesVerticales);
		String distanceRoutesHorizontales = extractRouteDistanceData(tableRoutesHorizontales);
		String typesRoutesVerticales = extractRouteTypeData(tableRoutesVerticales);
		String typesRoutesHorizontales = extractRouteTypeData(tableRoutesHorizontales);
		Carte newCarte = null;
		try {
			newCarte = new Carte(distanceRoutesVerticales, distanceRoutesHorizontales, typesRoutesVerticales, typesRoutesHorizontales);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newCarte;
	}
	
	public void updateMapsCarte() {
		if (ready) {
			Carte newCarte = parseRoutesAsCartes();
			if (newCarte != null) {
				mapRoutesVerticales.setCarte(newCarte);
				mapRoutesHorizontales.setCarte(newCarte);
				mapCircuitsAutobus.setCarte(newCarte);
				mapCircuitsMetro.setCarte(newCarte);
			}
		}
	}
	
	public void tableRoutesVerticalesEdited() {
		updateMapsCarte();
	}
	
	public void tableRoutesHorizontalesEdited() {
		updateMapsCarte();
	}
	
	public boolean isCircuitValid(Carte carte, String circuit) {
		try {
			if (circuit != "") {
				String[] pointsText = circuit.replace(" ", "").split("\\)\\(");
				for (int i = 0; i < pointsText.length; i++) {
					String s = pointsText[i].replace("(", "").replace(")", "");
					int x, y;
					x = Integer.parseInt(s.substring(0,s.indexOf(',')));
					y = Integer.parseInt(s.substring(s.indexOf(',') + 1));
					if (!carte.intersectionExiste(x, y)) {
						return false;
					}
				}
				return true;
					
			}
			return false;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public void tableCircuitsEdited(JTable table, PanelDrawnMap map, Color couleur) {
		
		if (table.getSelectedRow() != -1) {
			Carte tempCarte = parseRoutesAsCartes();
			try {
				Carte circuitCarte = parseRoutesAsCartes();
				Circuit[] circuits = new Circuit[1];
				String[] inputVehicules = extractVehiculesData().split(";");
				ModeTransport autobus = new ModeTransport("Autobus", typeTransport.autobus, true, true, true, inputVehicules[2]);
				autobus.setCouleur(couleur);
				String circuitData = (String)table.getModel().getValueAt( table.getSelectedRow(), 1);
				if (isCircuitValid(circuitCarte, circuitData)) {
					circuits[0] = new Circuit(autobus,circuitData);
					circuitCarte.setCircuits(circuits);
					map.setCarte(circuitCarte);
					Trajet t = map.getCarte().produireTrajetCircuit(0, 0, circuits[0].getNbArrets());
					Itineraire itin = new Itineraire("Essai circuit");
					itin.addTrajet(t);
					map.setItineraire(itin);
					map.clearPoints();
					for (Intersection pt : circuits[0].getArrets()) {
						map.addPoint(pt);
					}
				} else {
					map.setCarte(tempCarte);
					map.clearPoints();
					map.setItineraire(null);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				map.setCarte(tempCarte);
				map.clearPoints();
				map.setItineraire(null);
			}
			
		}		
	}

	/**
	 * Le code pour faire la fenêtre. Pas grands choses d'interressant ici.
	 */
	public ParamMAAS() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		resultat = null;
		
		setTitle("Param\u00E8tres");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 962, 617);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 946, 533);
		contentPane.add(tabbedPane);
		
		JPanel tabRoutesVerticales = new JPanel();
		tabbedPane.addTab("Routes verticales", null, tabRoutesVerticales, null);
		tabRoutesVerticales.setLayout(null);
		
		panelMapRoutesVerticales = new JPanel();
		panelMapRoutesVerticales.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMapRoutesVerticales.setBackground(Color.WHITE);
		panelMapRoutesVerticales.setBounds(430, 0, 511, 505);
		tabRoutesVerticales.add(panelMapRoutesVerticales);
		panelMapRoutesVerticales.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneTableRoutesVerticales = new JScrollPane();
		scrollPaneTableRoutesVerticales.setBounds(0, 0, 429, 460);
		tabRoutesVerticales.add(scrollPaneTableRoutesVerticales);
		
		tableRoutesVerticales = new JTable();
		tableRoutesVerticales.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				tableRoutesVerticalesEdited();
			}
		});
		scrollPaneTableRoutesVerticales.setViewportView(tableRoutesVerticales);
		tableRoutesVerticales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRoutesVerticales.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Routes", "Principale?", "Distance"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Boolean.class, Integer.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableRoutesVerticales.getColumnModel().getColumn(0).setResizable(false);
		tableRoutesVerticales.getColumnModel().getColumn(1).setResizable(false);
		tableRoutesVerticales.getColumnModel().getColumn(2).setResizable(false);
		
		JButton btnAjouterRouteVerticale = new JButton("Ajouter");
		btnAjouterRouteVerticale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableRoutesVerticales, new Object[]{tableRoutesVerticales.getModel().getRowCount(), false, 100});
				tableRoutesVerticalesEdited();
			}
		});
		btnAjouterRouteVerticale.setBounds(310, 471, 110, 23);
		tabRoutesVerticales.add(btnAjouterRouteVerticale);
		
		JButton btnSupprimerRouteVerticale = new JButton("Supprimer");
		btnSupprimerRouteVerticale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableRoutesVerticales);
				tableRoutesVerticalesEdited();
			}
			
		});
		btnSupprimerRouteVerticale.setBounds(190, 471, 110, 23);
		tabRoutesVerticales.add(btnSupprimerRouteVerticale);
		
		JPanel tabRoutesHorizontales = new JPanel();
		tabbedPane.addTab("Routes horizontales", null, tabRoutesHorizontales, null);
		tabRoutesHorizontales.setLayout(null);
		
		JScrollPane scrollPaneRoutesHorizontales = new JScrollPane();
		scrollPaneRoutesHorizontales.setBounds(0, 0, 429, 460);
		tabRoutesHorizontales.add(scrollPaneRoutesHorizontales);
		
		tableRoutesHorizontales = new JTable();
		tableRoutesHorizontales.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				tableRoutesHorizontalesEdited();
			}
		});
		tableRoutesHorizontales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRoutesHorizontales.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Routes", "Principale?", "Distance"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Boolean.class, Integer.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableRoutesHorizontales.getColumnModel().getColumn(0).setResizable(false);
		tableRoutesHorizontales.getColumnModel().getColumn(1).setResizable(false);
		tableRoutesHorizontales.getColumnModel().getColumn(2).setResizable(false);
		//tableRoutesHorizontales.getColumnModel().getColumn(0).set
		tableRoutesHorizontales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneRoutesHorizontales.setViewportView(tableRoutesHorizontales);
		
		JButton btnSupprimerRouteHorizontale = new JButton("Supprimer");
		btnSupprimerRouteHorizontale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableRoutesHorizontales);
				tableRoutesHorizontalesEdited();
			}
		});
		btnSupprimerRouteHorizontale.setBounds(190, 471, 110, 23);
		tabRoutesHorizontales.add(btnSupprimerRouteHorizontale);
		
		JButton btnAjouterRouteHorizontale = new JButton("Ajouter");
		btnAjouterRouteHorizontale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableRoutesHorizontales, new Object[]{tableRoutesHorizontales.getModel().getRowCount(), false, 100});
				tableRoutesHorizontalesEdited();
			}
		});
		btnAjouterRouteHorizontale.setBounds(310, 471, 110, 23);
		tabRoutesHorizontales.add(btnAjouterRouteHorizontale);
		
		panelMapRoutesHorizontales = new JPanel();
		panelMapRoutesHorizontales.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMapRoutesHorizontales.setBackground(Color.WHITE);
		panelMapRoutesHorizontales.setBounds(430, 0, 511, 505);
		tabRoutesHorizontales.add(panelMapRoutesHorizontales);
		
		JPanel tabVehicules = new JPanel();
		tabbedPane.addTab("V\u00E9hicules", null, tabVehicules, null);
		tabVehicules.setLayout(null);
		
		JScrollPane scrollPaneVehicules = new JScrollPane();
		scrollPaneVehicules.setBounds(0, 0, 941, 505);
		tabVehicules.add(scrollPaneVehicules);
		
		tableVehicules = new JTable();
		scrollPaneVehicules.setViewportView(tableVehicules);
		tableVehicules.setModel(new DefaultTableModel(
			new Object[][] {
				{"Marche", null, null, null, null, null, null, null},
				{"V\u00E9lo", null, null, null, null, null, null, null},
				{"Autobus", null, null, null, null, null, null, null},
				{"M\u00E9tro", null, null, null, null, null, null, null},
				{"Voiture (r\u00E9gulier)", null, null, null, null, null, null, null},
				{"Voiture (heure de pointe)", null, null, null, null, null, null, null},
			},
			new String[] {
				"Nom", "Vit. (principale)", "Vit. (secondaire)", "Arr\u00EAt (princ-princ)", "Arr\u00EAt (princ-sec)", "Arr\u00EAt (sec-sec)", "Attente", "Couleur"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableVehicules.getColumnModel().getColumn(0).setResizable(false);
		tableVehicules.getColumnModel().getColumn(0).setPreferredWidth(162);
		tableVehicules.getColumnModel().getColumn(1).setResizable(false);
		tableVehicules.getColumnModel().getColumn(1).setPreferredWidth(91);
		tableVehicules.getColumnModel().getColumn(2).setResizable(false);
		tableVehicules.getColumnModel().getColumn(2).setPreferredWidth(98);
		tableVehicules.getColumnModel().getColumn(3).setResizable(false);
		tableVehicules.getColumnModel().getColumn(3).setPreferredWidth(102);
		tableVehicules.getColumnModel().getColumn(4).setResizable(false);
		tableVehicules.getColumnModel().getColumn(4).setPreferredWidth(92);
		tableVehicules.getColumnModel().getColumn(5).setResizable(false);
		tableVehicules.getColumnModel().getColumn(5).setPreferredWidth(90);
		tableVehicules.getColumnModel().getColumn(6).setResizable(false);
		tableVehicules.getColumnModel().getColumn(7).setResizable(false);
		
		JPanel tabPoints = new JPanel();
		tabbedPane.addTab("Points", null, tabPoints, null);
		tabPoints.setLayout(null);
		
		JScrollPane scrollPanePoints = new JScrollPane();
		scrollPanePoints.setBounds(0, 0, 941, 460);
		tabPoints.add(scrollPanePoints);
		
		tablePoints = new JTable();
		tablePoints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePoints.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"#", "x", "y"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, Integer.class, Integer.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		scrollPanePoints.setViewportView(tablePoints);
		
		JButton btnAjouterPoint = new JButton("Ajouter");
		btnAjouterPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tablePoints, new Object[]{tablePoints.getModel().getRowCount(), 0, 0});
			}
		});
		btnAjouterPoint.setBounds(109, 471, 89, 23);
		tabPoints.add(btnAjouterPoint);
		
		JButton btnSupprimerPoint = new JButton("Supprimer");
		btnSupprimerPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tablePoints);
			}
		});
		btnSupprimerPoint.setBounds(10, 471, 89, 23);
		tabPoints.add(btnSupprimerPoint);
		
		JPanel tabAutobus = new JPanel();
		tabbedPane.addTab("Circuits autobus", null, tabAutobus, null);
		tabAutobus.setLayout(null);
		
		panelMapCircuitsAutobus = new JPanel();
		panelMapCircuitsAutobus.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMapCircuitsAutobus.setBackground(Color.WHITE);
		panelMapCircuitsAutobus.setBounds(469, 0, 472, 505);
		tabAutobus.add(panelMapCircuitsAutobus);
		panelMapCircuitsAutobus.setLayout(new BorderLayout(0, 0));
		
		JButton btnAjouterAutobus = new JButton("Ajouter");
		btnAjouterAutobus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableAutobus, new Object[]{tableAutobus.getModel().getRowCount(), "(0,0) (0,5) (5,5) (5,0) (0,0)"});
			}
		});
		
		JScrollPane scrollPaneAutobus = new JScrollPane();
		scrollPaneAutobus.setBounds(0, 0, 466, 460);
		tabAutobus.add(scrollPaneAutobus);
		
		tableAutobus = new JTable();
		tableAutobus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAutobus.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"#", "Circuit"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableAutobus.getColumnModel().getColumn(0).setResizable(false);
		tableAutobus.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableAutobus.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableAutobus.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

		    public void valueChanged(ListSelectionEvent lse) {
		        if (!lse.getValueIsAdjusting()) {
		        	tableCircuitsEdited(tableAutobus, mapCircuitsAutobus, Color.BLUE);
		        }
		    }
		});
		tableAutobus.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				// TODO Auto-generated method stub
				tableCircuitsEdited(tableAutobus, mapCircuitsAutobus, Color.BLUE);
			}
		});
		scrollPaneAutobus.setViewportView(tableAutobus);
		btnAjouterAutobus.setBounds(359, 471, 100, 23);
		tabAutobus.add(btnAjouterAutobus);
		
		JButton btnSupprimerAutobus = new JButton("Supprimer");
		btnSupprimerAutobus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableAutobus);
			}
		});
		btnSupprimerAutobus.setBounds(249, 471, 100, 23);
		tabAutobus.add(btnSupprimerAutobus);
		
		JPanel tabMetro = new JPanel();
		tabMetro.setLayout(null);
		tabbedPane.addTab("Circuits m\u00E9tro", null, tabMetro, null);
		
		panelMapCircuitsMetro = new JPanel();
		panelMapCircuitsMetro.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMapCircuitsMetro.setBackground(Color.WHITE);
		panelMapCircuitsMetro.setBounds(469, 0, 472, 505);
		tabMetro.add(panelMapCircuitsMetro);
		panelMapCircuitsMetro.setLayout(new BorderLayout(0, 0));
		
		JButton btnAjouterMetro = new JButton("Ajouter");
		btnAjouterMetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableMetro, new Object[]{tableMetro.getModel().getRowCount(), "(0,0) (0,5) (5,5) (5,0) (0,0)"});
			}
		});
		btnAjouterMetro.setBounds(359, 471, 100, 23);
		tabMetro.add(btnAjouterMetro);
		
		JScrollPane scrollPaneMetro = new JScrollPane();
		scrollPaneMetro.setBounds(0, 0, 468, 460);
		tabMetro.add(scrollPaneMetro);
		
		tableMetro = new JTable();
		tableMetro.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableMetro.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"#", "Circuit"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableMetro.getColumnModel().getColumn(0).setResizable(false);
		tableMetro.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableMetro.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableMetro.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

		    public void valueChanged(ListSelectionEvent lse) {
		        if (!lse.getValueIsAdjusting()) {
		        	tableCircuitsEdited(tableMetro, mapCircuitsMetro, Color.BLUE);
		        }
		    }
		});
		tableMetro.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				// TODO Auto-generated method stub
				tableCircuitsEdited(tableMetro, mapCircuitsMetro, Color.BLUE);
			}
		});
		scrollPaneMetro.setViewportView(tableMetro);
		
		JButton btnSupprimerMetro = new JButton("Supprimer");
		btnSupprimerMetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableMetro);
			}
		});
		btnSupprimerMetro.setBounds(249, 471, 100, 23);
		tabMetro.add(btnSupprimerMetro);
		
		JButton btnOK = new JButton("Ok");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		btnOK.setBounds(847, 544, 89, 23);
		contentPane.add(btnOK);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnAnnuler.setBounds(748, 544, 89, 23);
		contentPane.add(btnAnnuler);

		mapRoutesVerticales = new PanelDrawnMap();
		panelMapRoutesVerticales.add(mapRoutesVerticales, BorderLayout.CENTER);
		panelMapRoutesHorizontales.setLayout(new BorderLayout(0, 0));
		
		mapRoutesHorizontales = new PanelDrawnMap();
		panelMapRoutesHorizontales.add(mapRoutesHorizontales, BorderLayout.CENTER);
		
		mapCircuitsAutobus = new PanelDrawnMap();
		panelMapCircuitsAutobus.add(mapCircuitsAutobus, BorderLayout.CENTER);
		
		mapCircuitsMetro = new PanelDrawnMap();
		panelMapCircuitsMetro.add(mapCircuitsMetro, BorderLayout.CENTER);
		
		ready = true;
	}
	
	class Route {
		int distance;
		boolean principale;
		public Route(int distance, boolean principale) {
			super();
			this.distance = distance;
			this.principale = principale;
		}
		
	}
}
