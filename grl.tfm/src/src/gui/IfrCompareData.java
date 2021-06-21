package src.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.didion.jwnl.data.Synset;
import src.data.Attribute;
import src.data.Dataset;
import src.data.Distances;
import src.data.Ontologia;
import src.data.Operators;
import src.data.Record;
import src.data.SOperators;
import src.data.SnomedBD;
import src.data.Statistics;
import src.data.WordNetAccess;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.SystemColor;
import javax.swing.JTextPane;
import java.awt.Panel;
import java.awt.Component;
import javax.swing.SwingConstants;

public class IfrCompareData extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public Dataset dataset, anonimDataset;
	JButton btnNext, button, btnSearchFile, btnSaveFile;
	public Attribute att, att2;
	JLabel lblNombreDelFichero, lblNombreDelFichero2;
	Statistics estadisticas;
	JRadioButton rdbtnObtainStatisticsOf;
	JRadioButton rdbtnCompareAnAttribute;
	JLabel lblSelectAnonymizedFile;
	private JButton buttonDistances;
	public static JLabel labelDistances;
	public static String dominio;
	public static int ontologyNum; //WordNet por defecto
	JLabel lblSelectOntology;
	int numAt = 1;
	JComboBox comboBox_1;
	JTable table, tableCovOrig, tableCovMask;
	JProgressBar progressBar;
	boolean recarga = false, recargaStat = false, recargaCovOrig = false, recargaCovMask = false;
	JTable table1;
	JButton btnGenerate;
	JRadioButton rdbtnGenerateInitialCalculations, lblSelectSemanticDistances;
	JLabel lblOpciones;
	public static boolean generarCompare = false;
	JLabel lblNumOfRecords;
	JPanel panel_2;
	private JScrollPane scrollPane;
	private JPanel panel_4;
	JCheckBox chckbxSeeLemmas;
	Object[][] datosTabla, datosCovOrig, datosCovMask;
	String[] encabezados, encabezadosCov, encabezadosCovMask;
	
	/**
	 * Create the frame.
	 */
	public IfrCompareData() {
		setTitle("Data Anonymizer - Evaluate Dataset");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 963, 692);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(FrmMain.frmDataAnonimator);
		setResizable(false);
		
		btnNext = new JButton("Compute");
		btnNext.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNext.setBounds(403, 587, 89, 35);
		contentPane.add(btnNext);
		btnNext.addActionListener(new ActionListener() {
			
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
			        	
			        	DecimalFormat numberFormat = new DecimalFormat("0.00000");
			        	
			        	btnSaveFile.setEnabled(false);
			        	//Object[][] datosTabla, datosCovOrig, datosCovMask;
			        	
			        	Ontologia ontologia;
						if(ontologyNum == 1) //WordNet
							ontologia = new WordNetAccess();
						else //Snomed
							ontologia = new SnomedBD();
			        	
			        	if(rdbtnCompareAnAttribute.isSelected()) //si se van a comparar atributos
						{
			        		datosTabla = new Object[9][dataset.getDataset().size()+1];
			        		datosCovOrig = new Object[dataset.getDataset().size()][dataset.getDataset().size()+1];
			        		datosCovMask = new Object[dataset.getDataset().size()][dataset.getDataset().size()+1];
			        		
			        		for(int i = 1; i <= dataset.getDataset().size(); i++)
			        		{
			        			try
								{
									att = dataset.getAttribute(i);
									att2 = anonimDataset.getAttribute(i);
								}
								catch(Exception NoAttribute)
								{
									progressBar.setVisible(false);
									JOptionPane.showMessageDialog(null,
											"You must choose a dataset to compare.", 
											"Error loading dataset", JOptionPane.ERROR_MESSAGE);
									return null;
								}
			        			
								if(att.getTipo() == att2.getTipo() && att.get().size() == att2.get().size())
								{
									if(att.getTipo() == 0) //si es numérico
									{	
										estadisticas = new Statistics(2);
										
										estadisticas.numericMeanOrig = Operators.Mean(att.get());
										estadisticas.numericMeanMask = Operators.Mean(att2.get());
										
										estadisticas.varianceOrig = Operators.Variance(att.get());
										estadisticas.varianceMask = Operators.Variance(att2.get());
											
										estadisticas.stdDevOrig = Operators.StandardDeviation(att.get());
										estadisticas.stdDevMask = Operators.StandardDeviation(att2.get());
											
										estadisticas.mae = Operators.MAE(att.get(), att2.get());
											
										estadisticas.rmse = Operators.RMSE(att.get(), att2.get());
							
										datosTabla[0][0] = "Original mean";
										datosTabla[1][0] = "Masked mean";
										datosTabla[2][0] = "Original variance";
										datosTabla[3][0] = "Masked variance";
										datosTabla[4][0] = "Original Standard Deviation";
										datosTabla[5][0] = "Masked Standard Deviation";
										datosTabla[6][0] = "MAE";
										datosTabla[7][0] = "RMSE";
										
										datosTabla[0][i] = numberFormat.format(estadisticas.numericMeanOrig);
										datosTabla[1][i] = numberFormat.format(estadisticas.numericMeanMask);
										datosTabla[2][i] = numberFormat.format(estadisticas.varianceOrig);
										datosTabla[3][i] = numberFormat.format(estadisticas.varianceMask);
										datosTabla[4][i] = numberFormat.format(estadisticas.stdDevOrig);
										datosTabla[5][i] = numberFormat.format(estadisticas.stdDevMask);
										datosTabla[6][i] = numberFormat.format(estadisticas.mae);
										datosTabla[7][i] = numberFormat.format(estadisticas.rmse);
										datosTabla[8][i] = "";
										
										/* COVARIANZA Y CORRELACIÓN */
										
										for(Attribute a : dataset.getDataset())
										{	
											if(a.getTipo() == 0)
												estadisticas.covariancesOrig.add(Operators.covariance(att.get(), a.get()));
										}
											
										for(Attribute a : anonimDataset.getDataset())
										{
											if(a.getTipo() == 0)
												estadisticas.covariancesMask.add(Operators.covariance(att2.get(), a.get()));
										}
										
										for(Attribute a : dataset.getDataset())
										{
											if(a.getTipo() == 0)
												estadisticas.correlationOrig.add(Operators.correlation(att.get(), a.get()));
										}
											
										for(Attribute a : anonimDataset.getDataset())
										{
											if(a.getTipo() == 0)
												estadisticas.correlationMask.add(Operators.correlation(att2.get(), a.get()));
										}
								
										datosCovOrig[i-1][0] = att.getNombre();
										datosCovMask[i-1][0] = att.getNombre();
										
										int iStat = 0;
										
										for(int k = 1; k <= dataset.getDataset().size(); k++)
										{
											if(dataset.getAttribute(k).getTipo() == 0)
											{
												datosCovOrig[i-1][k] = numberFormat.format(estadisticas.covariancesOrig.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationOrig.get(iStat));
												datosCovMask[i-1][k] = numberFormat.format(estadisticas.covariancesMask.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationMask.get(iStat));
												iStat++;
											}
											else
											{
												datosCovOrig[i-1][k] = " ";
												datosCovMask[i-1][k] = " ";
											}
										}				
									}
									
									else //si es semántico
									{		
										/*Ontologia ontologia;
										if(ontologyNum == 1) //WordNet
											ontologia = new WordNetAccess();
										else //Snomed
											ontologia = new SnomedBD();*/
										
										estadisticas = new Statistics(1);
										Distances distances = null;
										
										try
										{
											distances = new Distances(dominio);
										}
										catch(Exception distancesException)
										{
											progressBar.setVisible(false);
											JOptionPane.showMessageDialog(null,
													"You must select a file with the initial calculations of the semantic domain, or generate it.", 
													"Error loading initial calculations file", JOptionPane.ERROR_MESSAGE);
											return null;
										}
										
										try
										{
											Long media1 = SOperators.semanticMean(att, distances);
											Long media2 = SOperators.semanticMean(att2, distances);
											
											estadisticas.semanticMeanOrig = media1;
											estadisticas.semanticMeanMask = media2;
												
											if(ontologyNum == 1)
											{
												Synset s1 = WordNetAccess.getSynset(media1);
												Synset s2 = WordNetAccess.getSynset(media2);
												estadisticas.lemaOriginal = s1.getWords()[0].getLemma();
												estadisticas.lemaMasked = s2.getWords()[0].getLemma();
												estadisticas.distancia = ontologia.distance_WP(media1, media2);
											}
											else
											{
												estadisticas.lemaOriginal = SnomedBD.obtainConcept(media1);
												estadisticas.lemaMasked = SnomedBD.obtainConcept(media2);
												estadisticas.distancia = ontologia.distance_WP(media1, media2);
											}
												
											estadisticas.varianceOrig = new Double(SOperators.semanticVariance(att, distances, media1));
											estadisticas.varianceMask = new Double(SOperators.semanticVariance(att2, distances, media2));
										
											estadisticas.stdDevOrig = new Double(SOperators.semanticStdDeviation(att, distances, media1));
											estadisticas.stdDevMask = new Double(SOperators.semanticStdDeviation(att2, distances, media2));
											
											estadisticas.mae = new Double(SOperators.semanticMAE(att, att2, distances));		
												
											estadisticas.rmse = new Double(SOperators.semanticRMSE(att, att2, distances));

											datosTabla[0][0] = "Original mean";
											datosTabla[1][0] = "Masked mean";
											datosTabla[2][0] = "Original variance";
											datosTabla[3][0] = "Masked variance";
											datosTabla[4][0] = "Original Standard Deviation";
											datosTabla[5][0] = "Masked Standard Deviation";
											datosTabla[6][0] = "MAE";
											datosTabla[7][0] = "RMSE";
											datosTabla[8][0] = "Distance between means";
											
											datosTabla[0][i] = estadisticas.semanticMeanOrig + " (" + SnomedBD.obtainConcept(estadisticas.semanticMeanOrig) + ")";
											datosTabla[1][i] = estadisticas.semanticMeanMask + " (" + SnomedBD.obtainConcept(estadisticas.semanticMeanMask) + ")";
											datosTabla[2][i] = numberFormat.format(estadisticas.varianceOrig);
											datosTabla[3][i] = numberFormat.format(estadisticas.varianceMask);
											datosTabla[4][i] = numberFormat.format(estadisticas.stdDevOrig);
											datosTabla[5][i] = numberFormat.format(estadisticas.stdDevMask);
											datosTabla[6][i] = numberFormat.format(estadisticas.mae);
											datosTabla[7][i] = numberFormat.format(estadisticas.rmse);
											datosTabla[8][i] = numberFormat.format(ontologia.distance_WP(estadisticas.semanticMeanOrig, estadisticas.semanticMeanMask));
											
											
											/* COVARIANZA Y CORRELACIÓN */
											
											for(Attribute a : dataset.getDataset())
											{
												System.out.println("Covarianza de " + i + " con " + a.getColumn());
												
												if(a.getTipo() == 1)
													estadisticas.covariancesOrig.add(SOperators.semanticCovariance(att, a, distances));
											}
												
											for(Attribute a : anonimDataset.getDataset())
											{
												if(a.getTipo() == 1)
													estadisticas.covariancesMask.add(SOperators.semanticCovariance(att2, a, distances));
											}
											
											for(Attribute a : dataset.getDataset())
											{
												if(a.getTipo() == 1)
													estadisticas.correlationOrig.add(SOperators.semanticCorrelation(att, a, distances));
											}
												
											for(Attribute a : anonimDataset.getDataset())
											{
												if(a.getTipo() == 1)
													estadisticas.correlationMask.add(SOperators.semanticCorrelation(att2, a, distances));
											}
											
											datosCovOrig[i-1][0] = att.getNombre();
											datosCovMask[i-1][0] = att.getNombre();
											
											int iStat = 0;
											
											for(int k = 1; k <= dataset.getDataset().size(); k++)
											{
												if(dataset.getAttribute(k).getTipo() == 1)
												{
													datosCovOrig[i-1][k] = numberFormat.format(estadisticas.covariancesOrig.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationOrig.get(iStat));
													datosCovMask[i-1][k] = numberFormat.format(estadisticas.covariancesMask.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationMask.get(iStat));
													iStat++;
												}
												else
												{
													datosCovOrig[i-1][k] = " ";
													datosCovMask[i-1][k] = " ";
												}
											}
											
										}
										catch(Exception operationException)
										{
											progressBar.setVisible(false);
											JOptionPane.showMessageDialog(null,
													"Verify that the used domain belongs to the ontology of the selected datasets.", 
													"Error calculating statistics", JOptionPane.ERROR_MESSAGE);
											return null;
										}
									}
								}
								else
								{
									progressBar.setVisible(false);
									JOptionPane.showMessageDialog(null,
											"Attributes to compare must be of the same type and have the same number of records.", 
											"Error in Compare Data", JOptionPane.ERROR_MESSAGE);
								}
			        		}

						}
						
						else //si se van a obtener estadísticas individuales
						{
							datosTabla = new Object[3][dataset.getDataset().size()+1];
							datosCovOrig = new Object[dataset.getDataset().size()][dataset.getDataset().size()+1];
			        		datosCovMask = new Object[dataset.getDataset().size()][dataset.getDataset().size()+1];
							
							for(int i = 1; i <= dataset.getDataset().size(); i++)
			        		{	
								try
								{
									att = dataset.getAttribute(i);
								}
								catch(Exception NoAttribute)
								{
									progressBar.setVisible(false);
									JOptionPane.showMessageDialog(null,
											"You must choose a dataset to compare.", 
											"Error loading dataset", JOptionPane.ERROR_MESSAGE);
									return null;
								}
								
								if(att.getTipo() == 0) //si es numérico
								{
									estadisticas = new Statistics(4);
									
									estadisticas.numericMeanOrig = Operators.Mean(att.get());
									estadisticas.varianceOrig = Operators.Variance(att.get());
									estadisticas.stdDevOrig = Operators.StandardDeviation(att.get());
									
									datosTabla[0][0] = "Original mean";
									datosTabla[1][0] = "Original variance";
									datosTabla[2][0] = "Original Standard Deviation";
										
									datosTabla[0][i] = numberFormat.format(estadisticas.numericMeanOrig);
									datosTabla[1][i] = numberFormat.format(estadisticas.varianceOrig);
									datosTabla[2][i] = numberFormat.format(estadisticas.stdDevOrig);
									
									/* COVARIANZA Y CORRELACIÓN */
									
									for(Attribute a : dataset.getDataset())
									{
										if(a.getTipo() == 0)
											estadisticas.covariancesOrig.add(Operators.covariance(att.get(), a.get()));
									}
									
									for(Attribute a : dataset.getDataset())
									{
										if(a.getTipo() == 0)
											estadisticas.correlationOrig.add(Operators.correlation(att.get(), a.get()));
									}
									
									datosCovOrig[i-1][0] = att.getNombre();
									datosCovMask[i-1][0] = att.getNombre();
									
									int iStat = 0;
									
									for(int k = 1; k <= dataset.getDataset().size(); k++)
									{
										if(dataset.getAttribute(k).getTipo() == 0)
										{
											datosCovOrig[i-1][k] = numberFormat.format(estadisticas.covariancesOrig.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationOrig.get(iStat));
											//datosCovMask[i-1][k] = estadisticas.correlationOrig.get(iStat);
											iStat++;
										}
										else
										{
											datosCovOrig[i-1][k] = " ";
											//datosCovMask[i-1][k] = " ";
										}
									}
									
								}
								
								else //si es semántico
								{	
									/*Ontologia ontologia;
									if(ontologyNum == 1) //WordNet
										ontologia = new WordNetAccess();
									else //Snomed
										ontologia = new SnomedBD();*/
									
									estadisticas = new Statistics(3);
									Distances distances = null;
									
									try
									{
										distances = new Distances(dominio);
									}
									catch(Exception distancesException)
									{
										progressBar.setVisible(false);
										JOptionPane.showMessageDialog(null,
												"You must select a file with the initial calculations of the semantic domain, or generate it.", 
												"Error loading initial calculations file", JOptionPane.ERROR_MESSAGE);
										return null;
									}
									
									try
									{
										Long media = SOperators.semanticMean(att, distances);
										
										estadisticas.semanticMeanOrig = media;
										if(ontologyNum == 1)
										{
											Synset s = WordNetAccess.getSynset(media);
											estadisticas.lemaOriginal = s.getWords()[0].getLemma();
										}
										else
										{
											estadisticas.lemaOriginal = SnomedBD.obtainConcept(media);
										}
										
										estadisticas.varianceOrig = new Double(SOperators.semanticVariance(att, distances, media));
											
										estadisticas.stdDevOrig = new Double(SOperators.semanticStdDeviation(att, distances, media));
										
										datosTabla[0][0] = "Original mean";
										datosTabla[1][0] = "Original variance";
										datosTabla[2][0] = "Original Standard Deviation";
										
										datosTabla[0][i] = estadisticas.semanticMeanOrig + " (" + SnomedBD.obtainConcept(estadisticas.semanticMeanOrig) + ")";
										datosTabla[1][i] = numberFormat.format(estadisticas.varianceOrig);
										datosTabla[2][i] = numberFormat.format(estadisticas.stdDevOrig);
										
										/* COVARIANZA Y CORRELACIÓN */
										
										for(Attribute a : dataset.getDataset())
										{										
											if(a.getTipo() == 1)
												estadisticas.covariancesOrig.add(SOperators.semanticCovariance(att, a, distances));
										}
										
										System.out.println("No llega");
										
										for(Attribute a : dataset.getDataset())
										{
											if(a.getTipo() == 1)
												estadisticas.correlationOrig.add(SOperators.semanticCorrelation(att, a, distances));
										}
										
										datosCovOrig[i-1][0] = att.getNombre();
										datosCovMask[i-1][0] = att.getNombre();
										
										int iStat = 0;
										
										for(int k = 1; k <= dataset.getDataset().size(); k++)
										{
											if(dataset.getAttribute(k).getTipo() == 1)
											{	
												datosCovOrig[i-1][k] = numberFormat.format(estadisticas.covariancesOrig.get(iStat)) + "/" + numberFormat.format(estadisticas.correlationOrig.get(iStat));
												//datosCovMask[i-1][k] = estadisticas.correlationOrig.get(iStat);
												iStat++;
											}
											else
											{
												datosCovOrig[i-1][k] = " ";
												datosCovMask[i-1][k] = " ";
											}
										}
									}
									catch(Exception operationException)
									{
										progressBar.setVisible(false);
										JOptionPane.showMessageDialog(null,
												"Verify that the domain used belongs to the ontology of the selected dataset.", 
												"Error calculating statistics", JOptionPane.ERROR_MESSAGE);
										return null;
									}
								}
			        		}		
						}
			        	
			        	encabezados = new String[dataset.getDataset().size() + 1];
			        	encabezados[0] = "Statistics";
			        	for(int i = 1; i <= dataset.getDataset().size(); i++)
							encabezados[i] = dataset.getAttribute(i).getNombre();
			        	
			        	DefaultTableModel tableModel = new DefaultTableModel(datosTabla, encabezados);
						
			        	if(recargaStat)
			        	{
			        		tableModel = (DefaultTableModel) table.getModel();
				        	tableModel.setRowCount(0);
				        	tableModel.setDataVector(datosTabla, encabezados);
			        	}

			        	table = new JTable(tableModel);
			        	recargaStat = true;
			        	table.setEnabled(false);
			        	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			        	scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			        	table.setFillsViewportHeight(true);
			        	scrollPane.setBounds(403, 104, 543, 249);
			        	contentPane.add(scrollPane);
			        	
			        	encabezadosCov = new String[dataset.getDataset().size() + 1];
			        	encabezadosCov[0] = "Orig. Cov/Corr";
			        	for(int i = 1; i <= dataset.getDataset().size(); i++)
							encabezadosCov[i] = dataset.getAttribute(i).getNombre();
			        	
			        	encabezadosCovMask = new String[dataset.getDataset().size() + 1];
			        	encabezadosCovMask[0] = "Masked Cov/Corr";
			        	for(int i = 1; i <= dataset.getDataset().size(); i++)
							encabezadosCovMask[i] = dataset.getAttribute(i).getNombre();
			        	
			        	
			        	DefaultTableModel tableModelCovOrig = new DefaultTableModel(datosCovOrig, encabezadosCov);
			        	
			        	if(recargaCovOrig)
			        	{
			        		tableModelCovOrig = (DefaultTableModel) tableCovOrig.getModel();
				        	tableModelCovOrig.setRowCount(0);
				        	tableModelCovOrig.setDataVector(datosCovOrig, encabezadosCov);
			        	}
			        	
			        	tableCovOrig = new JTable(tableModelCovOrig);
			        	recargaCovOrig = true;
			        	tableCovOrig.setEnabled(false);
			        	tableCovOrig.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			        	scrollPane = new JScrollPane(tableCovOrig, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			        	tableCovOrig.setFillsViewportHeight(true);
			        	scrollPane.setBounds(403, 364, 264, 169);
			        	contentPane.add(scrollPane);
			        
			        	
			        	DefaultTableModel tableModelCovMask = new DefaultTableModel(datosCovMask, encabezadosCovMask);
			        	
			        	if(recargaCovMask)
			        	{
			        		tableModelCovMask = (DefaultTableModel) tableCovMask.getModel();
			        		tableModelCovMask.setRowCount(0);
				        	tableModelCovMask.setDataVector(datosCovMask, encabezadosCovMask);
			        	}
			        	
			        	
			        	if(rdbtnCompareAnAttribute.isSelected())
			        	{
			        		tableCovMask = new JTable(tableModelCovMask);
				        	recargaCovMask = true;
				        	tableCovMask.setEnabled(false);
				        	tableCovMask.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				        	scrollPane = new JScrollPane(tableCovMask, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				        	tableCovMask.setFillsViewportHeight(true);
				        	scrollPane.setBounds(672, 364, 276, 169);
				        	scrollPane.setVisible(true);
			        		contentPane.add(scrollPane);
			        	}
			        	else
			        	{
			        		tableCovMask.setVisible(false);
			        	}
			        	      	
			        	btnSaveFile.setEnabled(true);
			        	progressBar.setVisible(false);
			        	
			        	return null;
			        }
			    }.execute();
			}
		});
		
		table1 = new JTable();
		
		btnSaveFile = new JButton("Save Results");
		btnSaveFile.setEnabled(false);
		btnSaveFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveFile.setBounds(790, 587, 119, 35);
		contentPane.add(btnSaveFile);
		
		JLabel lblWhatDoYou = new JLabel("What do you want to do?");
		lblWhatDoYou.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblWhatDoYou.setBounds(68, 11, 195, 27);
		contentPane.add(lblWhatDoYou);
		
		rdbtnCompareAnAttribute = new JRadioButton("Compare a non-anonymized dataset with its anonymized version");
		rdbtnCompareAnAttribute.setBackground(SystemColor.inactiveCaptionBorder);
		rdbtnCompareAnAttribute.setSelected(true);
		rdbtnCompareAnAttribute.setBounds(68, 45, 363, 23);
		contentPane.add(rdbtnCompareAnAttribute);
		
		rdbtnCompareAnAttribute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				rdbtnCompareAnAttribute.setSelected(true);
				rdbtnObtainStatisticsOf.setSelected(false);
				button.setVisible(true);
				lblNombreDelFichero2.setVisible(true);
				lblSelectAnonymizedFile.setVisible(true);
			}
		});	
		
		rdbtnObtainStatisticsOf = new JRadioButton("Obtain statistics of the attributes from a dataset");
		rdbtnObtainStatisticsOf.setBackground(SystemColor.inactiveCaptionBorder);
		rdbtnObtainStatisticsOf.setBounds(68, 66, 258, 23);
		contentPane.add(rdbtnObtainStatisticsOf);
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBack.setBounds(36, 587, 89, 35);
		contentPane.add(btnBack);
		
		lblSelectSemanticDistances = new JRadioButton("Select file with initial calculations:");
		lblSelectSemanticDistances.setBackground(SystemColor.inactiveCaptionBorder);
		lblSelectSemanticDistances.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSelectSemanticDistances.setBounds(69, 472, 183, 27);
		contentPane.add(lblSelectSemanticDistances);
		lblSelectSemanticDistances.setVisible(false);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBar.setBounds(0, 638, 957, 14);
		contentPane.add(progressBar);
		
		rdbtnGenerateInitialCalculations = new JRadioButton("Generate initial calculations:");
		rdbtnGenerateInitialCalculations.setBackground(SystemColor.inactiveCaptionBorder);
		rdbtnGenerateInitialCalculations.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnGenerateInitialCalculations.setBounds(69, 442, 159, 23);
		rdbtnGenerateInitialCalculations.setVisible(false);
		rdbtnGenerateInitialCalculations.setSelected(true);
		contentPane.add(rdbtnGenerateInitialCalculations);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(SystemColor.activeCaption));
		panel_1.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.setBounds(36, 11, 911, 82);
		contentPane.add(panel_1);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(SystemColor.activeCaption));
		panel_2.setForeground(new Color(0, 0, 0));
		panel_2.setBackground(SystemColor.inactiveCaptionBorder);
		panel_2.setBounds(36, 104, 329, 249);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblSelectOriginalFile = new JLabel("Select original dataset:");
		lblSelectOriginalFile.setBounds(42, 37, 110, 14);
		panel_2.add(lblSelectOriginalFile);
		lblSelectOriginalFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnSearchFile = new JButton("Search file");
		btnSearchFile.setBounds(157, 32, 83, 23);
		panel_2.add(btnSearchFile);
		btnSearchFile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) 
			{	
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrCompareData.this);
				try
				{
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						dataset = new Dataset(selectedFile.getAbsolutePath());
						lblNombreDelFichero.setText(selectedFile.getName());
						
						ArrayList<Attribute> datos = dataset.getDataset();
						String[] encabezados = new String[dataset.getDataset().size()];
						Object[][] datosTabla = new Object[datos.get(0).get().size()][datos.size()];
						
						for(int i = 1; i <= dataset.getDataset().size(); i++)
							encabezados[i-1] = datos.get(i-1).getNombre();
						
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
							tableModel = (DefaultTableModel) table1.getModel();
							tableModel.setRowCount(0);
							tableModel.setDataVector(datosTabla, encabezados);
						}
						
						table1 = new JTable(tableModel);
						recarga = true;
						table1.setEnabled(false);
						scrollPane = new JScrollPane(table1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						table1.setFillsViewportHeight(true);
						table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						scrollPane.setBounds(42, 75, 197, 99);
						panel_2.add(scrollPane);
						lblNumOfRecords.setVisible(true);
						chckbxSeeLemmas.setVisible(true);
						chckbxSeeLemmas.setSelected(false);
						lblNumOfRecords.setText("Num. of records: " + dataset.getAttribute(1).get().size());
						
						lblSelectSemanticDistances.setVisible(true);
						buttonDistances.setVisible(true);
						labelDistances.setVisible(true);
						lblSelectOntology.setVisible(true);
						comboBox_1.setVisible(true);
						btnGenerate.setVisible(true);
						rdbtnGenerateInitialCalculations.setVisible(true);
						lblOpciones.setVisible(true);
						ontologyNum = 1;
						
						
						/*if(dataset.getAttribute(1).getTipo() == 1)
						{
							lblSelectSemanticDistances.setVisible(true);
							buttonDistances.setVisible(true);
							labelDistances.setVisible(true);
							lblSelectOntology.setVisible(true);
							comboBox_1.setVisible(true);
							btnGenerate.setVisible(true);
							rdbtnGenerateInitialCalculations.setVisible(true);
							lblOpciones.setVisible(true);
							ontologyNum = 1;
						}
						else
						{
							lblSelectSemanticDistances.setVisible(false);
							buttonDistances.setVisible(false);
							lblSelectOntology.setVisible(false);
							labelDistances.setVisible(false);
							btnGenerate.setVisible(false);
							rdbtnGenerateInitialCalculations.setVisible(false);
							lblOpciones.setVisible(false);
							comboBox_1.setVisible(false);
						}*/
						
					}
				}
				catch(Exception fileException)
				{
					JOptionPane.showMessageDialog(null,
							"You must choose a file with the dataset format of this application.", 
							"Error selecting dataset", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		
		JLabel lblSelectDatasets = new JLabel("Select datasets");
		lblSelectDatasets.setBounds(32, 11, 104, 17);
		panel_2.add(lblSelectDatasets);
		lblSelectDatasets.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		lblNombreDelFichero = new JLabel("");
		lblNombreDelFichero.setBounds(157, 57, 142, 14);
		panel_2.add(lblNombreDelFichero);
		
		lblNumOfRecords = new JLabel("Num of records: ");
		lblNumOfRecords.setBounds(42, 175, 124, 23);
		panel_2.add(lblNumOfRecords);
		lblNumOfRecords.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblSelectAnonymizedFile = new JLabel("Select anonymized dataset:");
		lblSelectAnonymizedFile.setBounds(42, 200, 137, 27);
		panel_2.add(lblSelectAnonymizedFile);
		lblSelectAnonymizedFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		button = new JButton("Search file");
		button.setBounds(180, 202, 89, 23);
		panel_2.add(button);
		
		
		lblNombreDelFichero2 = new JLabel("");
		lblNombreDelFichero2.setBounds(180, 230, 119, 14);
		panel_2.add(lblNombreDelFichero2);
		
		chckbxSeeLemmas = new JCheckBox("See lemmas");
		chckbxSeeLemmas.setBackground(SystemColor.inactiveCaptionBorder);
		chckbxSeeLemmas.setBounds(180, 175, 97, 23);
		chckbxSeeLemmas.setVisible(false);
		panel_2.add(chckbxSeeLemmas);
		
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
							Attribute attribute = dataset.getAttribute(numAt);
							boolean isWordNet = true;
							if(attribute.getTipo() == 1) //es nominal
							{
								if(isWordNet)
								{
									try {
										for(Record r : attribute.get())
										{
											Long offset = r.getSemanticValue();
										
											String lema = "";
											lema = WordNetAccess.getSynset(offset).getWords()[0].getLemma();
											table1.setValueAt(lema, r.getId() - 1, numAt - 1);
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
											table1.setValueAt(lema, r.getId() - 1, numAt - 1);
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
						else
						{
							Attribute attribute = dataset.getAttribute(numAt);
							if(attribute.getTipo() == 1) //es nominal
							{
								for(Record r : attribute.get())
								{
									Long offset = r.getSemanticValue();
									table1.setValueAt(offset, r.getId() - 1, numAt - 1);
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
		
		panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(SystemColor.activeCaption));
		panel_4.setBackground(SystemColor.inactiveCaptionBorder);
		panel_4.setBounds(36, 364, 329, 169);
		contentPane.add(panel_4);
		panel_4.setLayout(null);
		
		lblOpciones = new JLabel("Initial calculations of semantic domain");
		lblOpciones.setBounds(28, 11, 260, 17);
		panel_4.add(lblOpciones);
		lblOpciones.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		lblSelectOntology = new JLabel("Select ontology:");
		lblSelectOntology.setBounds(38, 39, 88, 27);
		panel_4.add(lblSelectOntology);
		lblSelectOntology.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(125, 42, 125, 20);
		panel_4.add(comboBox_1);
		comboBox_1.setVisible(false);
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"WordNet", "SNOMED-CT"}));
		
		btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(224, 79, 88, 23);
		panel_4.add(btnGenerate);
		
		buttonDistances = new JButton("Search file");
		buttonDistances.setBounds(224, 110, 88, 23);
		panel_4.add(buttonDistances);
		buttonDistances.setVisible(false);
		buttonDistances.setEnabled(false);
		
		
		labelDistances = new JLabel("");
		labelDistances.setBounds(185, 144, 127, 14);
		panel_4.add(labelDistances);
		
		buttonDistances.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrCompareData.this);
				
				if(result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					dominio = selectedFile.getAbsolutePath();
					labelDistances.setText(selectedFile.getName());		
				}	
			}
		});
		btnGenerate.setVisible(false);
		
			btnGenerate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IfrGenerateDistances ifrGenerateDistances = null;
					try {
						generarCompare = true;
						ifrGenerateDistances = new IfrGenerateDistances();
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null,
								"The window can't be opened.", 
								"Error loading Generate semantic domain initial calculations", JOptionPane.ERROR_MESSAGE);
					}
					IfrCompareData.this.setVisible(false);
					ifrGenerateDistances.setVisible(true);
				}
			});
		
		
		
		comboBox_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ontologyNum = comboBox_1.getSelectedIndex() + 1;
			}
		});
		lblSelectOntology.setVisible(false);
		lblOpciones.setVisible(false);
		
		
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrCompareData.this);
				try
				{
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						anonimDataset = new Dataset(selectedFile.getAbsolutePath());
						lblNombreDelFichero2.setText(selectedFile.getName());
						btnNext.setEnabled(true);
					}
				}	
				catch(Exception fileException)
				{
					JOptionPane.showMessageDialog(null,
							"You must choose a file with the dataset format of this application.", 
							"Error selecting dataset", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}			
		});
		
		
		lblNumOfRecords.setVisible(false);
		
		
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				FrmMain.frmDataAnonimator.setVisible(true);
				IfrCompareData.this.dispose();
				//setVisible(false);
			}
		});
		
		
		rdbtnObtainStatisticsOf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				rdbtnObtainStatisticsOf.setSelected(true);
				rdbtnCompareAnAttribute.setSelected(false);
				button.setVisible(false);
				lblNombreDelFichero2.setVisible(false);
				lblSelectAnonymizedFile.setVisible(false);
			}
		});
		
		
		btnSaveFile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				//try
				//{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Specify a file to save");
					int result = fileChooser.showSaveDialog(IfrCompareData.this);
					
					if(result == JFileChooser.APPROVE_OPTION)
					{
						File fileToSave = fileChooser.getSelectedFile();
						File file = new File(fileToSave.getAbsolutePath());
						
						try
						{
							BufferedWriter bw = new BufferedWriter(new FileWriter(file));
						
							for(int i = 0; i < encabezados.length; i++)
							{
								if(i == encabezados.length - 1)
									bw.write(encabezados[i]);
								else
									bw.write(encabezados[i]+"|");
							}
									
							bw.newLine();
							
							for(int i = 0; i < datosTabla.length; i++)
							{
								for(int j = 0; j < datosTabla[i].length; j++)
								{
									if(j == datosTabla[i].length - 1)
										bw.write(datosTabla[i][j].toString());
									else
										bw.write(datosTabla[i][j]+"|");
								}
								bw.newLine();
							}
							
							bw.newLine();
							
							for(int i = 0; i < encabezadosCov.length; i++)
							{
								if(i == encabezadosCov.length - 1)
									bw.write(encabezadosCov[i]);
								else
									bw.write(encabezadosCov[i]+"|");
							}
							
							bw.newLine();
							
							for(int i = 0; i < datosCovOrig.length; i++)
							{
								for(int j = 0; j < datosCovOrig[i].length; j++)
								{
									if(j == datosCovOrig[i].length - 1)
										bw.write(datosCovOrig[i][j].toString());
									else
										bw.write(datosCovOrig[i][j]+"|");
								}
								bw.newLine();
							}
							
							bw.newLine();
							
							for(int i = 0; i < encabezadosCovMask.length; i++)
							{
								if(i == encabezadosCovMask.length - 1)
									bw.write(encabezadosCovMask[i]);
								else
									bw.write(encabezadosCovMask[i]+"|");
							}
							
							bw.newLine();
							
							for(int i = 0; i < datosCovMask.length; i++)
							{
								for(int j = 0; j < datosCovMask[i].length; j++)
								{
									if(j == datosCovMask[i].length - 1)
										bw.write(datosCovMask[i][j].toString());
									else
										bw.write(datosCovMask[i][j]+"|");
								}
								bw.newLine();
							}
						
							bw.close();	
						}
						catch (IOException e1) {e1.printStackTrace();}
						
					
					}
				/*}
				catch(Exception saveException)
				{
					JOptionPane.showMessageDialog(null,
							"No data to save.", 
							"Error saving", JOptionPane.ERROR_MESSAGE);
					return;
				}*/
			}
		});	
		
		
		rdbtnGenerateInitialCalculations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lblSelectSemanticDistances.setSelected(false);
				rdbtnGenerateInitialCalculations.setSelected(true);
				btnGenerate.setEnabled(true);
				buttonDistances.setEnabled(false);
				labelDistances.setEnabled(false);
			}
		});
		
		lblSelectSemanticDistances.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblSelectSemanticDistances.setSelected(true);
				rdbtnGenerateInitialCalculations.setSelected(false);
				buttonDistances.setEnabled(true);
				btnGenerate.setEnabled(false);
				labelDistances.setEnabled(true);
			}
		});
		
	}
}
