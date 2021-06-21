package src.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import src.data.Distances;
import src.data.Ontologia;
import src.data.SOperators;
import src.data.SnomedBD;
import src.data.WordNetAccess;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.mays.snomed.ConceptBasic;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.SystemColor;

public class IfrGenerateDistances extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	int ontologyNum = 1; //WordNet por defecto
	JComboBox comboBox;
	private JTextField textField;
	JFileChooser fileChooser;
	String dominio;
	JButton btnSearchFile, btnGenerate;
	private JTextField textField_1;
	JRadioButton chckbxSelectDomainFile, chckbxIntroduceDomainCategory;
	Distances distances;
	JButton btnSearch;
	JTable table;
	JScrollPane scrollPane;
	boolean recarga = false;
	public static Ontologia ontology;
	private JProgressBar progressBar;
	JTextArea lblYouHaveSelected;
	Long offset;
	JLabel lblFicherotxt;
	
	/**
	 * Create the frame.
	 */
	public IfrGenerateDistances() {
		setTitle("Data Anonymizer - Generate Initial Calculations of Semantic Domain");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 701, 553);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		setLocationRelativeTo(FrmMain.frmDataAnonimator);
		
		JLabel lblSelectOntology = new JLabel("Select Ontology: ");
		lblSelectOntology.setToolTipText("");
		lblSelectOntology.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSelectOntology.setBounds(36, 25, 115, 27);
		contentPane.add(lblSelectOntology);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"WordNet", "SNOMED-CT"}));
		comboBox.setBounds(161, 30, 167, 20);
		contentPane.add(comboBox);
		
		if(IfrCompareData.generarCompare)
		{
			lblSelectOntology.setVisible(false);
			comboBox.setVisible(false);
			ontologyNum = IfrCompareData.ontologyNum;
		}
		else
		{
			lblSelectOntology.setVisible(true);
			comboBox.setVisible(true);
		}
		
		
		comboBox.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   ontologyNum = comboBox.getSelectedIndex() + 1;
			   }
		});	
		
		btnGenerate = new JButton("Generate");
		btnGenerate.setEnabled(true);
		btnGenerate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnGenerate.setBounds(562, 447, 89, 27);
		contentPane.add(btnGenerate);
		
		btnGenerate.addActionListener(new ActionListener() {
		
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
			            
			        	switch(ontologyNum)
						{
							case 1: ontology = new WordNetAccess(); break;
							case 2: ontology = new SnomedBD(); break;
						}
						
						if(chckbxSelectDomainFile.isSelected()) //Se obtiene de fichero
						{
							try 
							{
								distances = SOperators.computeDistances(dominio);
							}
							catch(Exception exDominio)
							{
								progressBar.setVisible(false);
								JOptionPane.showMessageDialog(null,
										"You must select a domain file with the appropriate format for this application.", 
										"Error obtaining calculations", JOptionPane.ERROR_MESSAGE);
								return null;
							}
						}
						else if(chckbxIntroduceDomainCategory.isSelected()) //A partir de una categoría
						{
							try
							{
								distances = SOperators.computeDistances(offset);
							}
							catch(Exception exOffset)
							{		
								progressBar.setVisible(false);
								JOptionPane.showMessageDialog(null,
										"You must provide a suitable offset for the selected ontology.", 
										"Error obtaining calculations", JOptionPane.ERROR_MESSAGE);
								return null;
							}
						}
						
						progressBar.setVisible(false);
						
						JFileChooser fileChooser2 = new JFileChooser();
						fileChooser2.setCurrentDirectory(new File(System.getProperty("user.home")));
						int result = fileChooser2.showSaveDialog(IfrGenerateDistances.this);
						
						if(result == JFileChooser.APPROVE_OPTION)
						{
							File selectedFile = fileChooser2.getSelectedFile();
							fileChooser2.setDialogTitle("Specify a file to save");
							distances.saveDistances(selectedFile.getAbsolutePath());
							if(IfrCompareData.generarCompare)
							{
								IfrCompareData.dominio = selectedFile.getAbsolutePath();
								IfrCompareData.labelDistances.setText(selectedFile.getName());
								FrmMain.ifrCompareData.setVisible(true);
							}
							else
							{
								IfrMaskData.dominio = selectedFile.getAbsolutePath();
								IfrMaskData.labelDistances.setText(selectedFile.getName());
								FrmMain.ifrMaskData.setVisible(true);
							}
							setVisible(false);
						}
			            return null;
			        }
			    }.execute();
			}
			
		});
		
		btnSearchFile = new JButton("Search File");
		btnSearchFile.setBounds(245, 149, 157, 23);
		contentPane.add(btnSearchFile);
		btnSearchFile.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(IfrGenerateDistances.this);
				
				if(result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					dominio = selectedFile.getAbsolutePath();
					lblFicherotxt.setText(selectedFile.getName());
				}
			}
			
		});
		
		textField_1 = new JTextField();
		textField_1.setEnabled(false);
		textField_1.setBounds(248, 192, 154, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		chckbxSelectDomainFile = new JRadioButton("Select semantic domain file:");
		chckbxSelectDomainFile.setSelected(true);
		chckbxSelectDomainFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxSelectDomainFile.setBounds(36, 149, 157, 23);
		contentPane.add(chckbxSelectDomainFile);
		chckbxSelectDomainFile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
					textField_1.setEnabled(false);
					btnSearchFile.setEnabled(true);
					chckbxIntroduceDomainCategory.setSelected(false);
					lblFicherotxt.setEnabled(true);
					btnSearch.setEnabled(false);
					chckbxSelectDomainFile.setSelected(true);
			}
			
		});
		
		chckbxIntroduceDomainCategory = new JRadioButton("Introduce semantic domain category:");
		chckbxIntroduceDomainCategory.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbxIntroduceDomainCategory.setBounds(36, 191, 205, 23);
		contentPane.add(chckbxIntroduceDomainCategory);
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBack.setEnabled(true);
		btnBack.setBounds(36, 447, 89, 27);
		contentPane.add(btnBack);
		
		JTextArea txtrTheUserMust = new JTextArea();
		txtrTheUserMust.setEditable(false);
		txtrTheUserMust.setWrapStyleWord(true);
		txtrTheUserMust.setBackground(Color.LIGHT_GRAY);
		txtrTheUserMust.setLineWrap(true);
		txtrTheUserMust.setBounds(36, 63, 615, 75);
		contentPane.add(txtrTheUserMust);
		txtrTheUserMust.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrTheUserMust.setText("The user must choose between one of the two options below. If \"Select semantic domain file\" is chosen, you must search a file with a predefined domain (a .txt file with a column of IDs). Instead, if \"Introduce semantic domain category\" is selected, you must write a WordNet or a Snomed concept, depending on the selected ontology.");
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		txtrTheUserMust.setBorder(BorderFactory.createCompoundBorder(border, 
		      BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBar.setBounds(0, 499, 695, 14);
		contentPane.add(progressBar);
		
		btnSearch = new JButton("Search");
		btnSearch.setBounds(412, 191, 89, 23);
		contentPane.add(btnSearch);
		btnSearch.setEnabled(false);
		
		lblYouHaveSelected = new JTextArea("");
		lblYouHaveSelected.setEditable(false);
		lblYouHaveSelected.setWrapStyleWord(true);
		lblYouHaveSelected.setLineWrap(true);
		lblYouHaveSelected.setBackground(SystemColor.menu);
		lblYouHaveSelected.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblYouHaveSelected.setBounds(36, 352, 615, 84);
		contentPane.add(lblYouHaveSelected);
		
		lblFicherotxt = new JLabel("");
		lblFicherotxt.setToolTipText("");
		lblFicherotxt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFicherotxt.setBounds(412, 149, 199, 20);
		contentPane.add(lblFicherotxt);
		
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
			            progressBar.setVisible(true);
			            return null;
			        }
			    }.execute();
				
			    new SwingWorker<Void, Void>() {
			        public Void doInBackground() throws Exception{
			            
			        	Object[][] datosTabla = null;
			        	String[] encabezados = null;
			        	
			        	try {
							
							String concepto = textField_1.getText();
							ArrayList<Long> offsets = new ArrayList<Long>();
							ArrayList<Word[]> conceptos = new ArrayList<Word[]>();
							
							if(ontologyNum == 1) //si es WordNet
							{
								ontology = new WordNetAccess();
								Synset[] senses = WordNetAccess.getIndexWord(concepto).getSenses();
							
								for(int i = 0; i < senses.length; i++)
								{
									offsets.add(senses[i].getOffset());
									conceptos.add(senses[i].getWords());
								}
								
								encabezados = new String[2];
								encabezados[0] = "Offset";
								encabezados[1] = "Lemmas";
								
								datosTabla = new Object[offsets.size()][2];
								
								for(int i = 0; i < offsets.size(); i++)
								{
									datosTabla[i][0] = offsets.get(i);
									
									String lemas = "";
									Word[] words = conceptos.get(i);
									for(int j = 0; j < words.length; j++)
									{
										if(j != words.length - 1)
											lemas = lemas.concat(words[j].getLemma() + ", ");
										else
											lemas = lemas.concat(words[j].getLemma());
									}
									
									datosTabla[i][1] = lemas;
								}
							}
							else //si es SnomedBD
							{
								ontology = new SnomedBD();
								
								ArrayList<ConceptBasic> cb = SnomedBD.obtainConcept(concepto);
								if(cb.size() > 0)
								{
									encabezados = new String[2];
									encabezados[0] = "Snomed ID";
									encabezados[1] = "Name";
									
									datosTabla = new Object[cb.size()][2];
									
									for(int i = 0; i < cb.size(); i++)
									{
										ConceptBasic c = cb.get(i);
										datosTabla[i][0] = c.getConceptId();
										datosTabla[i][1] = c.getFullySpecifiedName();
									}
								}
								else
								{
									progressBar.setVisible(false);
									JOptionPane.showMessageDialog(null,
											"The introduced concept does not exist in ontology.", 
											"Error obtaining concept", JOptionPane.ERROR_MESSAGE);
									return null;
								}
							}
						}
						catch(Exception e)
						{
							progressBar.setVisible(false);
							JOptionPane.showMessageDialog(null,
									"The introduced concept does not exist in ontology.", 
									"Error obtaining concept", JOptionPane.ERROR_MESSAGE);
						}
			        	
			        	TableModel tableModel = new DefaultTableModel(datosTabla, encabezados)
						{
							public boolean isCellEditable(int row, int column)
						    {
						      return false;
						    }
						};
						
						if(recarga)
						{	
							tableModel = (DefaultTableModel) table.getModel();
							((DefaultTableModel) tableModel).setRowCount(0);
							((DefaultTableModel) tableModel).setDataVector(datosTabla, encabezados);
						}							

						table = new JTable(tableModel);
						recarga = true;
						scrollPane = new JScrollPane(table);
						table.setFillsViewportHeight(true);
						scrollPane.setBounds(36, 232, 615, 104);
						contentPane.add(scrollPane);
						
						table.addMouseListener(new MouseAdapter() {
							
							@Override
							public void mouseClicked(MouseEvent e) {
							      JTable target = (JTable)e.getSource();
							      int row = target.getSelectedRow();
							      offset = (Long) target.getValueAt(row, 0);
							      
							      if(ontologyNum == 1)
							      {
							    	  Synset s = WordNetAccess.getSynset(offset);
							    	  lblYouHaveSelected.setText("You have selected: " + offset + " (" + s.getGloss() + ")");
							    	  lblYouHaveSelected.setToolTipText(s.getGloss());
							      }
							      else
							    	  lblYouHaveSelected.setText("You have selected: " + offset); 
							  }
						});
			        	
			        	progressBar.setVisible(false);
			            return null;
			        }
			    }.execute();
		
			}
		});
		
		table = new JTable();
		
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(IfrCompareData.generarCompare)
					FrmMain.ifrCompareData.setVisible(true);
				else
					FrmMain.ifrMaskData.setVisible(true);
				
				IfrGenerateDistances.this.dispose();
			}
		});
		
		chckbxIntroduceDomainCategory.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
					btnSearchFile.setEnabled(false);
					textField_1.setEnabled(true);
					chckbxSelectDomainFile.setSelected(false);
					lblFicherotxt.setEnabled(false);
					btnSearch.setEnabled(true);
					chckbxIntroduceDomainCategory.setSelected(true);			
			}
			
		});	
	}
}
