package src.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import src.data.Attribute;
import src.data.Dataset;
import src.data.Distances;
import src.data.NumericMethods;
import src.data.Ontologia;
import src.data.Record;
import src.data.SemanticMethods;
import src.data.SnomedBD;
import src.data.WordNetAccess;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants; 

public class IfrMaskData extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public Dataset dataset;
	public Attribute att;
	public int method = 1; //por defecto se usa Individual Ranking
	public ArrayList<Integer> numAtts = new ArrayList<Integer>();
	public JComboBox comboBox; //selector para algoritmo
	public JButton btnMask, btnFind;
	JLabel lblNombreDelFichero;
	JScrollPane scrollPane;
	JTable table;
	boolean recarga = false;
	JLabel lblIntroduceK;
	JTextArea txtrRecommendedValuesOf;
	CheckCombo comboAttributes;
	JLabel lblSelectSemanticDistances;
	JButton buttonDistances;
	public static String dominio;
	Distances distances;
	JProgressBar progressBar;
	JButton btnSaveDataset;
	JLabel lblNumOfRecords;
	JRadioButton rdbtnSelectFileWith, rdbtnGenerateInitialCalculations;
	JButton btnGenerate;
	JLabel lblActualViewOf;
	JCheckBox chckbxSeeLemmas;
	String m;
	private JTextField txtPrivacyValue;
	public static JLabel labelDistances;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel lblInitialPresets;
	private JPanel panel_2;
	
	/**
	 * Create the frame.
	 */
	public IfrMaskData() {
		
		numAtts.add(1);
		setTitle("Data Anonymizer - Anonymize Dataset");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, -28, 634, 628);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		setLocationRelativeTo(FrmMain.frmDataAnonimator);
		
		table = new JTable();
		
		btnMask = new JButton("Anonymize");
		btnMask.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnMask.setBounds(259, 534, 103, 29);
		btnMask.setVisible(false);
		contentPane.add(btnMask);
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnBack.setBounds(37, 534, 103, 29);
		contentPane.add(btnBack);
		
		
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				FrmMain.frmDataAnonimator.setVisible(true);
				IfrMaskData.this.setEnabled(false);
				IfrMaskData.this.dispose();
				//setVisible(false);
			}
		});
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBar.setBounds(0, 574, 618, 14);
		contentPane.add(progressBar);
		
		btnSaveDataset = new JButton("Save Anonymized Dataset");
		btnSaveDataset.setEnabled(false);
		btnSaveDataset.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSaveDataset.setBounds(443, 534, 175, 29);
		btnSaveDataset.setVisible(false);
		contentPane.add(btnSaveDataset);
		
		panel = new JPanel();
		panel.setBorder(new LineBorder(SystemColor.activeCaption));
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		panel.setBounds(32, 380, 586, 143);
		contentPane.add(panel);
		panel.setLayout(null);
		
		lblSelectSemanticDistances = new JLabel("Initial calculations of semantic domain");
		lblSelectSemanticDistances.setBounds(20, 11, 260, 17);
		panel.add(lblSelectSemanticDistances);
		lblSelectSemanticDistances.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		rdbtnGenerateInitialCalculations = new JRadioButton("Generate initial calculations:");
		rdbtnGenerateInitialCalculations.setBackground(SystemColor.inactiveCaptionBorder);
		rdbtnGenerateInitialCalculations.setBounds(42, 40, 159, 23);
		panel.add(rdbtnGenerateInitialCalculations);
		rdbtnGenerateInitialCalculations.setSelected(true);
		rdbtnGenerateInitialCalculations.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		
		btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(231, 39, 114, 25);
		panel.add(btnGenerate);
		btnGenerate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		rdbtnSelectFileWith = new JRadioButton("Select file with initial calculations:");
		rdbtnSelectFileWith.setBackground(SystemColor.inactiveCaptionBorder);
		rdbtnSelectFileWith.setBounds(42, 78, 183, 23);
		panel.add(rdbtnSelectFileWith);
		rdbtnSelectFileWith.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		buttonDistances = new JButton("Search File");
		buttonDistances.setBounds(231, 78, 114, 23);
		panel.add(buttonDistances);
		buttonDistances.setEnabled(false);
		buttonDistances.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		
		labelDistances = new JLabel("");
		labelDistances.setBounds(237, 107, 230, 14);
		panel.add(labelDistances);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(SystemColor.activeCaption));
		panel_1.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.setBounds(32, 22, 300, 347);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		txtrRecommendedValuesOf = new JTextArea();
		txtrRecommendedValuesOf.setBounds(22, 261, 247, 64);
		panel_1.add(txtrRecommendedValuesOf);
		txtrRecommendedValuesOf.setWrapStyleWord(true);
		txtrRecommendedValuesOf.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtrRecommendedValuesOf.setText("Allowed values of k are between 1 and the number of records of the dataset.");
		txtrRecommendedValuesOf.setLineWrap(true);
		txtrRecommendedValuesOf.setBackground(Color.LIGHT_GRAY);
		txtrRecommendedValuesOf.setBorder(BorderFactory.createCompoundBorder(border, 
		      BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		JLabel lblSearchDataset = new JLabel("Select Dataset:");
		lblSearchDataset.setBounds(22, 35, 82, 29);
		panel_1.add(lblSearchDataset);
		lblSearchDataset.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFind = new JButton("Search File");
		btnFind.setBounds(124, 39, 145, 20);
		panel_1.add(btnFind);
		btnFind.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblNombreDelFichero = new JLabel("");
		lblNombreDelFichero.setBounds(124, 71, 166, 14);
		panel_1.add(lblNombreDelFichero);
		
		JLabel lblSelectAttribute = new JLabel("Select Attributes:");
		lblSelectAttribute.setBounds(22, 92, 92, 29);
		panel_1.add(lblSelectAttribute);
		lblSelectAttribute.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		//comboAttributes = new CheckCombo();
		//comboAttributes.setBounds(124, 96, 145, 20);
		//panel_1.add(comboAttributes);
		//comboAttributes.setEnabled(true);
		
		JLabel lblSelectMethod = new JLabel("Select Method:");
		lblSelectMethod.setBounds(22, 181, 82, 29);
		panel_1.add(lblSelectMethod);
		lblSelectMethod.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		comboBox = new JComboBox();
		comboBox.setBounds(124, 185, 145, 20);
		panel_1.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Individual Ranking", "Noise Addition", "Rank Swapping"}));
		
		
		lblIntroduceK = new JLabel("Introduce k:");
		lblIntroduceK.setBounds(22, 221, 82, 29);
		panel_1.add(lblIntroduceK);
		lblIntroduceK.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		txtPrivacyValue = new JTextField();
		txtPrivacyValue.setBounds(124, 225, 145, 20);
		panel_1.add(txtPrivacyValue);
		
		lblInitialPresets = new JLabel("Initial settings");
		lblInitialPresets.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblInitialPresets.setBounds(10, 0, 213, 29);
		panel_1.add(lblInitialPresets);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(SystemColor.activeCaption));
		panel_2.setBackground(SystemColor.inactiveCaptionBorder);
		panel_2.setBounds(342, 22, 276, 347);
		panel_2.setVisible(false);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		lblNumOfRecords = new JLabel("Num. of records:");
		lblNumOfRecords.setBounds(25, 304, 119, 20);
		panel_2.add(lblNumOfRecords);
		lblNumOfRecords.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblActualViewOf = new JLabel("Actual view of the dataset");
		lblActualViewOf.setBounds(10, 0, 172, 31);
		panel_2.add(lblActualViewOf);
		lblActualViewOf.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		chckbxSeeLemmas = new JCheckBox("See lemmas");
		chckbxSeeLemmas.setBackground(SystemColor.inactiveCaptionBorder);
		chckbxSeeLemmas.setBounds(152, 303, 97, 23);
		panel_2.add(chckbxSeeLemmas);
		lblActualViewOf.setVisible(false);
		lblNumOfRecords.setVisible(false);
		
		chckbxSeeLemmas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
			            progressBar.setVisible(true);
			            return null;
			        }
			    }.execute();
				
			    new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
			           
			        	chckbxSeeLemmas.setEnabled(false);
			        	if(chckbxSeeLemmas.isSelected())
						{
							if(FrmMain.firstTime)
							{
								Ontologia wordnet = new WordNetAccess();
								Ontologia snomed = new SnomedBD();
								FrmMain.firstTime = false;
							}
							
							for(Attribute attribute : dataset.getDataset())
							{
								boolean isWordNet = true;
								if(attribute.getTipo() == 1) //es nominal
								{
									if(isWordNet)
									{
										try {
											for(Record r : attribute.get())
											{
												Long offset = null;
												offset = r.getSemanticValue();
										
												String lema = "";
												lema = WordNetAccess.getSynset(offset).getWords()[0].getLemma();
												table.setValueAt(lema, r.getId() - 1, attribute.getColumn() - 1);
											}
										}
										catch(Exception e2) {isWordNet = false;}
									}
								
									if(!isWordNet)
									{
										try
										{
											for(Record r : attribute.get())
											{
												Long offset = r.getSemanticValue();									
												String lema = "";
												lema = SnomedBD.obtainConcept(offset);
												table.setValueAt(lema, r.getId() - 1, attribute.getColumn() - 1);
											}
										}
										catch(Exception e2) {
											progressBar.setVisible(false);
											JOptionPane.showMessageDialog(null,
												"Check that the concepts of the attribute are exclusively from WordNet or from SNOMED.", 
												"Error obtaining lemmas", JOptionPane.ERROR_MESSAGE);
											return null;
										}
									}
								}
							}	
						}
						else
						{
							
							for(Attribute attribute : dataset.getDataset())
							{
								if(attribute.getTipo() == 1) //es nominal
								{
									for(Record r : attribute.get())
									{
										Long offset = r.getSemanticValue();
										table.setValueAt(offset, r.getId() - 1, attribute.getColumn() - 1);
									}
								}
							}
						}
			        	chckbxSeeLemmas.setEnabled(true);
			        	progressBar.setVisible(false);
			            return null;
			        }
			    }.execute();
			    
			}
		});
		
		
		comboBox.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   method = comboBox.getSelectedIndex() + 1;
				   
				   switch(method)
				   {
				   		case 1: lblIntroduceK.setText("Introduce k:");
				   				txtrRecommendedValuesOf.setText("Allowed values of k are between 1 and the number of records of the dataset.");
				   				break;
				   		case 2: lblIntroduceK.setText("Introduce alpha:");
				   				txtrRecommendedValuesOf.setText("Recommended values of alpha are between 0.1 and 0.5.");	
				   				break;
				   		case 3: lblIntroduceK.setText("Introduce k:");
				   				txtrRecommendedValuesOf.setText("Allowed values of k are between 1 and the number of records of the dataset.");
				   				break;
				   }
				   
			   }
			});
		
		
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrMaskData.this);
				try
				{
					if (result == JFileChooser.APPROVE_OPTION)
					{
						File selectedFile = fileChooser.getSelectedFile();
						dataset = new Dataset(selectedFile.getAbsolutePath());
						lblNombreDelFichero.setText(selectedFile.getName());
						btnMask.setVisible(true);
						btnSaveDataset.setVisible(true);
						
						ArrayList<Attribute> datos = dataset.getDataset();
						String[] encabezados = new String[datos.size()];
						
						comboAttributes = new CheckCombo();
						comboAttributes.setBounds(124, 96, 145, 20);
						panel_1.add(comboAttributes);
						comboAttributes.setEnabled(true);
						
						encabezados[0] = datos.get(0).getNombre();
						comboAttributes.addItem(new CheckComboStore(encabezados[0],true));
						
						for(int i = 1; i < datos.size(); i++)
						{	
							encabezados[i] = datos.get(i).getNombre();
							comboAttributes.addItem(new CheckComboStore(encabezados[i],false));
						}
						
						comboAttributes.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0)
							{	
								int index = alreadySelectedAttribute(numAtts, comboAttributes.getSelectedIndex() + 1);
								
								if(index != -1)
								{
									if(numAtts.size() > 1)
									{
										CheckComboStore store = (CheckComboStore) comboAttributes.getSelectedItem();
										store.state = !store.state;
										numAtts.remove(index);
										repaint();
									}	
								}
								else
								{	
									numAtts.add(comboAttributes.getSelectedIndex() + 1);
									CheckComboStore store = (CheckComboStore) comboAttributes.getSelectedItem();
									store.state = !store.state;
									repaint();
								}
									
								Collections.sort(numAtts); //ordenar de forma creciente
							
						
								if(dataset.getAttribute(numAtts.get(0)).getTipo() == 1) //es nominal
								{
									lblSelectSemanticDistances.setVisible(true);
									panel.setVisible(true);
									buttonDistances.setVisible(true);
									labelDistances.setVisible(true);
									rdbtnSelectFileWith.setVisible(true);
									rdbtnGenerateInitialCalculations.setVisible(true);
									btnGenerate.setVisible(true);
								}
								else
								{
									lblSelectSemanticDistances.setVisible(false);
									panel.setVisible(false);
									buttonDistances.setVisible(false);
									labelDistances.setVisible(false);
									rdbtnSelectFileWith.setVisible(false);
									rdbtnGenerateInitialCalculations.setVisible(false);
									btnGenerate.setVisible(false);
								}
							}
						});
						
						
						
						if(dataset.getAttribute(1).getTipo() == 1)
						{
							lblSelectSemanticDistances.setVisible(true);
							panel.setVisible(true);
							buttonDistances.setVisible(true);
							rdbtnSelectFileWith.setVisible(true);
							rdbtnGenerateInitialCalculations.setVisible(true);
							btnGenerate.setVisible(true);
						}
						else
						{
							lblSelectSemanticDistances.setVisible(false);
							buttonDistances.setVisible(false);
							panel.setVisible(false);
							rdbtnSelectFileWith.setVisible(false);
							rdbtnGenerateInitialCalculations.setVisible(false);
							btnGenerate.setVisible(false);
						}
						
						//numAt = 1;
							
						Object[][] datosTabla = new Object[datos.get(0).get().size()][datos.size()];
						
						for(int i = 0; i < datos.size(); i++)
						{
							ArrayList<Record> at = datos.get(i).get();
							int tipo = datos.get(i).getTipo();
							
							for(int j = 0; j < at.size(); j++)
							{
								Record r = at.get(j);
								if(tipo == 1) //nominal
								{
									datosTabla[j][i] = r.getSemanticValue();
								}
								else
								{
									datosTabla[j][i] = r.getNumericValue();
								}
							}
						}
						
						DefaultTableModel tableModel = new DefaultTableModel(datosTabla, encabezados);
						
						if(recarga)
						{	
							tableModel = (DefaultTableModel) table.getModel();
							tableModel.setRowCount(0);
							tableModel.setDataVector(datosTabla, encabezados);
						}							

						lblNumOfRecords.setText("Num. of records: " + dataset.getAttribute(1).get().size());
						lblNumOfRecords.setVisible(true);
						lblActualViewOf.setVisible(true);
						chckbxSeeLemmas.setVisible(true);
						chckbxSeeLemmas.setSelected(false);
						panel_2.setVisible(true);
						
						table = new JTable(tableModel);
						recarga = true;
						table.setEnabled(false);
						scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						table.setFillsViewportHeight(true);
						table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						scrollPane.setBounds(10, 42, 256, 245);
						panel_2.add(scrollPane);
					}
				}
				catch(Exception wrongFile)
				{
					JOptionPane.showMessageDialog(null,
							"You must choose a file with the dataset format of this application.", 
							"Error selecting dataset", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		buttonDistances.setVisible(false);
		
		buttonDistances.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrMaskData.this);
				
				if(result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					dominio = selectedFile.getAbsolutePath();
					labelDistances.setText(selectedFile.getName());		
				}
			}
		});
		rdbtnSelectFileWith.setVisible(false);
		
		rdbtnSelectFileWith.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					rdbtnSelectFileWith.setSelected(true);
					rdbtnGenerateInitialCalculations.setSelected(false);
					btnGenerate.setEnabled(false);
					buttonDistances.setEnabled(true);
					labelDistances.setEnabled(true); 
			}
		});
		btnGenerate.setVisible(false);
		
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				IfrGenerateDistances ifrGenerateDistances = null;
				try {
					IfrCompareData.generarCompare = false;
					ifrGenerateDistances = new IfrGenerateDistances();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"The window can't be opened.", 
							"Error loading Generate semantic domain initial calculations", JOptionPane.ERROR_MESSAGE);
				}
				IfrMaskData.this.setVisible(false);
				ifrGenerateDistances.setVisible(true);
			}
		});
		rdbtnGenerateInitialCalculations.setVisible(false);
		
		
		rdbtnGenerateInitialCalculations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					rdbtnGenerateInitialCalculations.setSelected(true);
					rdbtnSelectFileWith.setSelected(false);
					btnGenerate.setEnabled(true);
					buttonDistances.setEnabled(false);
					labelDistances.setEnabled(false);
			}			
		});
		lblSelectSemanticDistances.setVisible(false);
		panel.setVisible(false);
		
		
		btnSaveDataset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");
				int result = fileChooser.showSaveDialog(IfrMaskData.this);
					
				if(result == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();
					dataset.save(fileToSave.getAbsolutePath());
				}
			}			
		});
		
		btnMask.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				
				new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
			            progressBar.setVisible(true);
			            return null;
			        }
			    }.execute();
				
				
				new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
				
			        	btnFind.setEnabled(false);
			        	comboAttributes.setEnabled(false);
			        	btnMask.setEnabled(false);
				
			        	int ipv = 5;
			        	float fpv = 1;
			        	String pv = txtPrivacyValue.getText();
			        	
			        	switch(method) //según el método, un valor de privacidad:
			        	{
			        		case 1:
			        		try {
			        			ipv = Integer.parseInt(pv);
			        		}
			        		catch(NumberFormatException ek) {
			        			
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Check that the value of k is an integer.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
						
			        		if(ipv < 1 || ipv > dataset.getAttribute(1).get().size())
			        		{
			        			
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Verify that the value of k is greater than 0 and less than the total rows of the dataset.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
			        		break;
						
			        		case 2:
						
			        		try {
			        			fpv = Float.parseFloat(pv);
			        		}
			        		catch(NumberFormatException ealpha) {
			        			
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Check that the alpha value is a real value.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
						
			        		if(fpv <= 0)
			        		{
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Check that the alpha value is a positive real value.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
			        		break;
						
			        		case 3:
			        		
			        		try {
			        			ipv = Integer.parseInt(pv);
			        		}	
			        		catch(NumberFormatException ek) {
			        			
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Check that the value of k is an integer.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}

			        		if(ipv < 1 || ipv > dataset.getAttribute(1).get().size())
			        		{
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"Verify that the value of k is greater than 0 and less than the total rows of the dataset.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
			        		break;
			        	}
			        	
			        	if(numAtts.size() > 1) //si hay más de un atributo seleccionado, multivariados
			        	{
			        		ArrayList<Attribute> anonimizeAttributes = new ArrayList<Attribute>();
			        			
			        		if(allAttributesSameType(numAtts, dataset))
			        		{
			        			
			        			int[] atts = new int[numAtts.size()];
			        			for(int i = 0; i < numAtts.size(); i++)
			        				atts[i] = numAtts.get(i);
			
			        			if(dataset.getAttribute(numAtts.get(0)).getTipo() == 1) //nominal
			        			{
			        				try
					        		{
					        			distances = new Distances(dominio);
					        		}
					        		catch(Exception distancesException)
					        		{
					        			btnFind.setEnabled(true);
							        	comboAttributes.setEnabled(true);
							        	btnMask.setEnabled(true);
					        			
					        			progressBar.setVisible(false);
					        			JOptionPane.showMessageDialog(null,
					        					"You must select a file with the initial calculations of the semantic domain, or generate it.", 
					        					"Error loading initial calculations file", JOptionPane.ERROR_MESSAGE);
					        			return null;
					        		}
			        				
			        				long begin = System.currentTimeMillis();
			        				
			        				try
					        		{
					        			switch(method)
					        			{
											case 1: anonimizeAttributes = SemanticMethods.semanticIndividualRankingMultivariate(dataset, atts, ipv, distances); break;
											case 2: anonimizeAttributes = SemanticMethods.semanticAddNoiseMultivariate(dataset, atts, fpv, distances); break;
											case 3: anonimizeAttributes = SemanticMethods.semanticRankSwappingMultivariate(dataset, atts, ipv, distances); break;
					        			}
					        		}
					        		catch(Exception MethodException)
					        		{
					        			btnFind.setEnabled(true);
							        	comboAttributes.setEnabled(true);
							        	btnMask.setEnabled(true);
					        			
					        			progressBar.setVisible(false);
					        			MethodException.printStackTrace();
					        			JOptionPane.showMessageDialog(null,
					        					"Check that the domain used belongs to the ontology of the dataset to anonymize.", 
					        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
					        			return null;
					        		}
			        				
			        				long end = System.currentTimeMillis();
					        		System.out.println("Tiempo milisegundos: " + (end-begin));
			        				
			        			}
			        			else //numérico
			        			{
			        				switch(method)
			        				{
			        					case 1: anonimizeAttributes = NumericMethods.individualRankingMultivariate(dataset, atts, ipv); break;
			        					case 2: anonimizeAttributes = NumericMethods.addNoiseMultivariate(dataset, atts, fpv); break;
			        					case 3: anonimizeAttributes = NumericMethods.rankSwappingMultivariate(dataset, atts, ipv); break;
			        				}
			        			}
			        			
			        			
			        			/*for(int i = 0; i < atts.length; i++)
			        			{
			        				dataset.setAttribute(anonimizeAttributes.get(i), atts[i]);
			        			}*/
			        			
			        		}
			        		else
			        		{
			        			btnFind.setEnabled(true);
					        	comboAttributes.setEnabled(true);
					        	btnMask.setEnabled(true);
			        			progressBar.setVisible(false);
			        			JOptionPane.showMessageDialog(null,
			        					"All selected attributes must be the same type.", 
			        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
			        			return null;
			        		}
			        	}
			        	else //si no, univariado
			        	{
			        		Attribute atributo = dataset.getAttribute(numAtts.get(0));
			        		for(Record r : atributo.get()) //inicializamos el atributo anonymized, por si se desea volver a anonimizar
					        	r.setAnonymized(false);
			        		ArrayList<Record> anonimizeAttribute = null;
			        		
			        		if(atributo.getTipo() == 1) //nominal
				        	{
				        		try
				        		{
				        			distances = new Distances(dominio);
				        		}
				        		catch(Exception distancesException)
				        		{
				        			btnFind.setEnabled(true);
						        	comboAttributes.setEnabled(true);
						        	btnMask.setEnabled(true);
				        			
				        			progressBar.setVisible(false);
				        			JOptionPane.showMessageDialog(null,
				        					"You must select a file with the initial calculations of the semantic domain, or generate it.", 
				        					"Error loading initial calculations file", JOptionPane.ERROR_MESSAGE);
				        			return null;
				        		}
						
				        		long begin = System.currentTimeMillis();
				        		
				        		try
				        		{
				        			switch(method)
				        			{
										case 1: anonimizeAttribute = SemanticMethods.individualRanking(atributo, ipv, distances); break;
										case 2: anonimizeAttribute = SemanticMethods.semanticAddNoise(atributo, fpv, distances); break;
										case 3: anonimizeAttribute = SemanticMethods.semanticRankSwapping(atributo, ipv, distances); break;
				        			}	
				        		}
				        		catch(Exception MethodException)
				        		{
				        			btnFind.setEnabled(true);
						        	comboAttributes.setEnabled(true);
						        	btnMask.setEnabled(true);
				        			
				        			progressBar.setVisible(false);
				        			MethodException.printStackTrace();
				        			JOptionPane.showMessageDialog(null,
				        					"Check that the domain used belongs to the ontology of the dataset to anonymize.", 
				        					"Anonymize error", JOptionPane.ERROR_MESSAGE);
				        			return null;
				        		}
				        		
				        		long end = System.currentTimeMillis();
				        		System.out.println("Tiempo milisegundos: " + (end-begin));
						
				        	}
				        	else //numérico
				        	{
				        		switch(method)
				        		{
				        			case 1: anonimizeAttribute = NumericMethods.individualRanking(atributo, ipv); break;
				        			case 2: anonimizeAttribute = NumericMethods.addNoise(atributo, fpv); break;
				        			case 3: anonimizeAttribute = NumericMethods.rankSwapping(atributo, ipv); break;
				        		}
				        	}
				        	
				        	atributo.set(anonimizeAttribute);
				        	atributo.setMasked(true);
				        	dataset.setAttribute(atributo, atributo.getColumn());
			        	}
			        	
			        	
			        	progressBar.setVisible(false);
			        	
			        	if(IfrMaskData.this.isEnabled())
			        	{
			        		JOptionPane.showMessageDialog(null, "Successfully anonymized.",
			        			"Completed anonymization", JOptionPane.INFORMATION_MESSAGE);
			        	}
			        	
			        	btnFind.setEnabled(true);
			        	comboAttributes.setEnabled(true);
			        	btnMask.setEnabled(true);
			        	
			        	ArrayList<Attribute> datos = dataset.getDataset();
			        	String[] encabezados = new String[datos.size()];
						
						for(int i = 1; i <= datos.size(); i++)
							encabezados[i-1] = datos.get(i-1).getNombre();
						
				
			        	Object[][] datosTabla = new Object[datos.get(0).get().size()][datos.size()];
				
			        	for(int i = 0; i < datos.size(); i++)
			        	{
			        		boolean masked = datos.get(i).isMasked();
			        		ArrayList<Record> at = datos.get(i).get();
			        		int tipo = datos.get(i).getTipo();
			        		for(int j = 0; j < at.size(); j++)
			        		{
			        			Record r = at.get(j);
						
			        			if(tipo == 1) //nominal
			        			{
			        				datosTabla[j][i] = r.getSemanticValue();
			        			}
			        			else
			        			{
			        				datosTabla[j][i] = r.getNumericValue();
			        			}
			        		}
			        	}
				
			        	DefaultTableModel tableModel = new DefaultTableModel(datosTabla, encabezados);
				
			        	tableModel = (DefaultTableModel) table.getModel();
			        	tableModel.setRowCount(0);
			        	tableModel.setDataVector(datosTabla, encabezados);						

			        	table = new JTable(tableModel);
			        	recarga = true;
			        	table.setEnabled(false);
			        	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			        	scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			        	table.setFillsViewportHeight(true);
			        	scrollPane.setBounds(10, 42, 256, 245);
			        	panel_2.add(scrollPane);
			        	
			        	chckbxSeeLemmas.setSelected(false);
			        	btnSaveDataset.setEnabled(true);
			        	
			        	return null;
			      }
				}.execute();     
			}
		});
	
		
	}
	
	
	int alreadySelectedAttribute(ArrayList<Integer> atts, int nuevo)
	{
		int index = -1;
		for(int i = 0; i < atts.size(); i++)
			if(atts.get(i) == nuevo)
				return i;
		return index;
	}
	
	
	boolean allAttributesSameType(ArrayList<Integer> atts, Dataset dataset)
	{
		ArrayList<Attribute> attributes = dataset.getDataset();
		int tipoBase = attributes.get(atts.get(0) - 1).getTipo();
		for(Integer i : atts)
		{
			if(tipoBase != attributes.get(i - 1).getTipo())
				return false;
		}
		return true;
	}
	
	
}