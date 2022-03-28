package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TestPagePrincipal extends JFrame {

	
	private Parametres param;
	private JPanel contentPane;
	private JLabel labelParam;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestPagePrincipal frame = new TestPagePrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void updateParam() {
		if (param == null) {
			labelParam.setText("null");
		} else {
			labelParam.setText(param.toString());
		}
	}
	
	private void sauvegarder() {
		
		JFileChooser chooser = new JFileChooser();
		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getPath();
			try (PrintWriter out = new PrintWriter(path)) {
				out.println(param.toSingleString());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void openDialog() {
		ParamMAAS dialog = new ParamMAAS();
		param = dialog.showDialog(param);
		System.out.println(param);
		updateParam();
	}
	
	private void openParam() {
		JFileChooser chooser = new JFileChooser();
		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
		int option = chooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try (Scanner reader = new Scanner(file)) {
				String paramFile = "";
				
				while (reader.hasNextLine()) {
					paramFile += reader.nextLine() + "\n";
				}
				
				String[] lines = paramFile.split("\\R");
				param = new Parametres(
						lines[0],
						lines[1],
						lines[2],
						lines[3],
						lines[4],
						lines[5],
						lines[6],
						lines[7]
						);
						
						
						
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Create the frame.
	 */
	public TestPagePrincipal() {
		
		param = null;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Ouvrir param\u00E8tres");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDialog();
			}
		});
		btnNewButton.setBounds(10, 11, 414, 23);
		contentPane.add(btnNewButton);
		
		JButton btnSauverParamtres = new JButton("Sauver param\u00E8tres");
		btnSauverParamtres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sauvegarder();
			}
		});
		btnSauverParamtres.setBounds(10, 45, 414, 23);
		contentPane.add(btnSauverParamtres);
		
		JButton btnOuvrirParamtres = new JButton("Ouvrir param\u00E8tres");
		btnOuvrirParamtres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openParam();
			}
		});
		btnOuvrirParamtres.setBounds(10, 79, 414, 23);
		contentPane.add(btnOuvrirParamtres);
		
		labelParam = new JLabel("New label");
		labelParam.setVerticalAlignment(SwingConstants.BOTTOM);
		labelParam.setBounds(10, 113, 414, 137);
		contentPane.add(labelParam);
		
		updateParam();
	}
}
