package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dialog.ModalityType;

public class ParamMAAS extends JDialog {

	private JPanel contentPane;
	private JTable tableRoutesVerticales;
	private JTable tableRoutesHorizontales;
	private JTable tableVehicules;
	private JTable tablePoints;
	private JTable tableAutobus;
	private JTable tableMetro;

	private Parametres resultat;
	
	public void cancel() {
		dispose();
	}
	
	public void ok() {
		String distanceRoutesVerticales = extractRouteDistanceData(tableRoutesVerticales);
		String distanceRoutesHorizontales = extractRouteDistanceData(tableRoutesHorizontales);
		String typesRoutesVerticales = extractRouteTypeData(tableRoutesVerticales);;
		String typesRoutesHorizontales = extractRouteTypeData(tableRoutesHorizontales);
		String vehicules = extractVehiculesData();
		String points = extractPointData();
		String circuitsAutobus = extractCircuitsData(tableAutobus);
		String circuitsMetro = extractCircuitsData(tableMetro);
		resultat = new Parametres(distanceRoutesVerticales, distanceRoutesHorizontales, typesRoutesVerticales, typesRoutesHorizontales, vehicules, points, circuitsAutobus, circuitsMetro);
		
		dispose();
	}
	

	public Parametres showDialog(Parametres initial) {
		populate(initial);
		setVisible(true);
		return resultat;
	}
	
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
	
	public String extractRouteTypeData(JTable table) {
		TableModel model = table.getModel();
		String routes = "";
		for (int i = 0; i < model.getRowCount(); i++) {
			routes += ((boolean)model.getValueAt(i, 1) ? "P" : "S" );
		}
		return routes;
	}
	
	public void addRow(JTable table, Object[] obj) {
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		tableModel.addRow(obj);
		table.setModel(tableModel);
	}
	
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
	
	public void populate(Parametres initial) {
		Parametres paramInitiaux = initial;
		if (initial == null) {
			paramInitiaux = new Parametres(
					"30,50,100",
					"60,90,150",
					"SPSS",
					"SSPS",
					"Marche,3,3,30,20,5,0;V�lo,6,7,40,30,15,0;Autobus,35,25,15,20,10,300;M�tro,50,50,45,0,0,180;Voiture (r�gulier),30,20,60,40,15,0;Voiture (heure de pointe),20,15,90,60,30,0",
					"(0,1);(0,4);(5,4)",
					"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(2,1) (2,0) (4,0) (5,1) (4,2) (4,4) (2,4) (2,1);(1,4) (1,2) (3,0) (3,2) (4,3) (5,3) (3,4) (1,4)",
					"(0,4) (4,4) (4,3) (5,3) (5,0) (0,0) (0,4);(2,1) (2,0) (4,0) (5,1) (4,2) (4,4) (2,4) (2,1)"
					);
		}
		populateRoutes(tableRoutesVerticales, paramInitiaux.distanceRoutesVerticales, paramInitiaux.typesRoutesVerticales );
		populateRoutes(tableRoutesHorizontales, paramInitiaux.distanceRoutesHorizontales, paramInitiaux.typesRoutesHorizontales);
		populateVehicules(paramInitiaux.vehicules);
		populatePoints(paramInitiaux.points);
		populateCircuits(paramInitiaux.circuitsAutobus, tableAutobus);
		populateCircuits(paramInitiaux.circuitsMetro, tableMetro);
	}
	
	public void populateRoutes(JTable table, String paramsDistance, String paramsType) {
		Route[] routes = parseRoutes(paramsDistance, paramsType);
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		for (int i = 0; i < routes.length; i++) {
			Route route = routes[i];
			tableModel.addRow(new Object[]{i, route.principale, route.distance});
		}
		table.setModel(tableModel);
	}
	
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
	
	public void populateVehicules(String params) {
		TableModel table = tableVehicules.getModel();
		String[] vehicules = params.split(";");
		for (int i = 0; i < vehicules.length; i++) {
			String[] val = vehicules[i].split(",");
			table.setValueAt(val[0], i, 0);
			for (int j = 1 ; j < val.length; j++) {
				table.setValueAt(Integer.parseInt(val[j].trim()), i, j);
			}
		}
		tableVehicules.setModel(table);
	}
	
	public String extractVehiculesData() {
		TableModel model = tableVehicules.getModel();
		String[] vehicules = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			String[] val = new String[model.getColumnCount()];
			val[0] = (String)model.getValueAt(i, 0);
			for (int j = 1; j < model.getColumnCount(); j++) {
				val[j] = (Integer.toString((Integer)model.getValueAt(i, j)));
			}
			vehicules[i] = String.join(",", val);
		}
		return String.join(";", vehicules);
	}
	
	public void populatePoints(String params) {
		DefaultTableModel table = (DefaultTableModel)tablePoints.getModel();
		String[] points = params.replace("(", "").replace(")", "").split(";");
		for (int i = 0; i < points.length; i++) {
			String[] val = points[i].split(",");
			table.addRow(new Object[]{i, val[0], val[1]});
		}
		tablePoints.setModel(table);
	}
	
	public String extractPointData() {
		TableModel model = tablePoints.getModel();
		String[] points = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			points[i] = "(" + (String)model.getValueAt(i,1) + "," + (String)model.getValueAt(i,2) + ")";
		}
		return String.join(";", points);
	}
	
	public void populateCircuits(String params, JTable table) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		String[] circuits = params.split(";");
		for (int i = 0; i < circuits.length; i++) {
			model.addRow(new Object[]{i, circuits[0]});
		}
		table.setModel(model);
	}
	
	public String extractCircuitsData(JTable table) {
		TableModel model = table.getModel();
		String[] circuits = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			circuits[i] = (String)model.getValueAt(i,1);
		}
		return String.join(";", circuits);
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("serial")
	public ParamMAAS() {
		setModalityType(ModalityType.DOCUMENT_MODAL);
		
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
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Routes verticales", null, panel, null);
		panel.setLayout(null);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(430, 0, 511, 505);
		panel.add(panel_5);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 429, 460);
		panel.add(scrollPane);
		
		tableRoutesVerticales = new JTable();
		scrollPane.setViewportView(tableRoutesVerticales);
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
		
		JButton btnNewButton_1 = new JButton("Ajouter");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableRoutesVerticales, new Object[]{tableRoutesVerticales.getModel().getRowCount(), false, 100});
			}
		});
		btnNewButton_1.setBounds(310, 471, 110, 23);
		panel.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Supprimer");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableRoutesVerticales);
			}
			
		});
		btnNewButton_2.setBounds(190, 471, 110, 23);
		panel.add(btnNewButton_2);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Routes horizontales", null, panel_1, null);
		panel_1.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 0, 429, 460);
		panel_1.add(scrollPane_1);
		
		tableRoutesHorizontales = new JTable();
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
		scrollPane_1.setViewportView(tableRoutesHorizontales);
		
		JButton btnNewButton_2_1 = new JButton("Supprimer");
		btnNewButton_2_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableRoutesHorizontales);
			}
		});
		btnNewButton_2_1.setBounds(190, 471, 110, 23);
		panel_1.add(btnNewButton_2_1);
		
		JButton btnNewButton_1_1 = new JButton("Ajouter");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableRoutesHorizontales, new Object[]{tableRoutesHorizontales.getModel().getRowCount(), false, 100});
			}
		});
		btnNewButton_1_1.setBounds(310, 471, 110, 23);
		panel_1.add(btnNewButton_1_1);
		
		JPanel panel_5_1 = new JPanel();
		panel_5_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5_1.setBackground(Color.WHITE);
		panel_5_1.setBounds(430, 0, 511, 505);
		panel_1.add(panel_5_1);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("V\u00E9hicules", null, panel_2, null);
		panel_2.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(0, 0, 941, 505);
		panel_2.add(scrollPane_2);
		
		tableVehicules = new JTable();
		scrollPane_2.setViewportView(tableVehicules);
		tableVehicules.setModel(new DefaultTableModel(
			new Object[][] {
				{"Marche", null, null, null, null, null, null},
				{"V\u00E9lo", null, null, null, null, null, null},
				{"Autobus", null, null, null, null, null, null},
				{"M\u00E9tro", null, null, null, null, null, null},
				{"Voiture (r\u00E9gulier)", null, null, null, null, null, null},
				{"Voiture (heure de pointe)", null, null, null, null, null, null},
			},
			new String[] {
				"Nom", "Vit. (principale)", "Vit. (secondaire)", "Arr\u00EAt (princ-princ)", "Arr\u00EAt (princ-sec)", "Arr\u00EAt (sec-sec)", "Attente"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
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
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Points", null, panel_3, null);
		panel_3.setLayout(null);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(0, 0, 941, 460);
		panel_3.add(scrollPane_3);
		
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
		scrollPane_3.setViewportView(tablePoints);
		
		JButton btnNewButton_5 = new JButton("Ajouter");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tablePoints, new Object[]{tablePoints.getModel().getRowCount(), 0, 0});
			}
		});
		btnNewButton_5.setBounds(109, 471, 89, 23);
		panel_3.add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("Supprimer");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tablePoints);
			}
		});
		btnNewButton_6.setBounds(10, 471, 89, 23);
		panel_3.add(btnNewButton_6);
		
		JPanel tableCircuitsAutobus = new JPanel();
		tabbedPane.addTab("Circuits autobus", null, tableCircuitsAutobus, null);
		tableCircuitsAutobus.setLayout(null);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_6.setBackground(Color.WHITE);
		panel_6.setBounds(469, 0, 472, 505);
		tableCircuitsAutobus.add(panel_6);
		
		JButton btnNewButton_5_1 = new JButton("Ajouter");
		btnNewButton_5_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableAutobus, new Object[]{tableAutobus.getModel().getRowCount(), "(0,0) (0,5) (5,5) (5,0) (0,0)"});
			}
		});
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(0, 0, 466, 460);
		tableCircuitsAutobus.add(scrollPane_4);
		
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
		scrollPane_4.setViewportView(tableAutobus);
		btnNewButton_5_1.setBounds(359, 471, 100, 23);
		tableCircuitsAutobus.add(btnNewButton_5_1);
		
		JButton btnNewButton_6_1 = new JButton("Supprimer");
		btnNewButton_6_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableAutobus);
			}
		});
		btnNewButton_6_1.setBounds(249, 471, 100, 23);
		tableCircuitsAutobus.add(btnNewButton_6_1);
		
		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		tabbedPane.addTab("Circuits m\u00E9tro", null, panel_7, null);
		
		JPanel panel_6_1 = new JPanel();
		panel_6_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_6_1.setBackground(Color.WHITE);
		panel_6_1.setBounds(469, 0, 472, 505);
		panel_7.add(panel_6_1);
		
		JButton btnNewButton_3_1 = new JButton("Ajouter");
		btnNewButton_3_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow(tableMetro, new Object[]{tableMetro.getModel().getRowCount(), "(0,0) (0,5) (5,5) (5,0) (0,0)"});
			}
		});
		btnNewButton_3_1.setBounds(359, 471, 100, 23);
		panel_7.add(btnNewButton_3_1);
		
		JScrollPane scrollPane_4_1 = new JScrollPane();
		scrollPane_4_1.setBounds(0, 0, 468, 460);
		panel_7.add(scrollPane_4_1);
		
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
		scrollPane_4_1.setViewportView(tableMetro);
		
		JButton btnNewButton_4_1_1 = new JButton("Supprimer");
		btnNewButton_4_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(tableMetro);
			}
		});
		btnNewButton_4_1_1.setBounds(249, 471, 100, 23);
		panel_7.add(btnNewButton_4_1_1);
		
		JButton btnNewButton = new JButton("Ok");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		btnNewButton.setBounds(847, 544, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnAnnuler.setBounds(748, 544, 89, 23);
		contentPane.add(btnAnnuler);
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
