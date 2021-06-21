package src.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class FrmMain {

	public static JFrame frmDataAnonimator;
	public static IfrMaskData ifrMaskData;
	public static IfrCompareData ifrCompareData;
	public static boolean firstTime = true;
	//public static IfrGenerateDistances ifrGenerateDistances;

	/**
	 * Lanzar la aplicación:
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmMain window = new FrmMain();
					window.frmDataAnonimator.setVisible(true);
					window.frmDataAnonimator.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public FrmMain() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDataAnonimator = new JFrame();
		frmDataAnonimator.setTitle("Data Anonymizer");
		frmDataAnonimator.setBounds(100, 100, 314, 252);
		frmDataAnonimator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDataAnonimator.getContentPane().setLayout(null);
		frmDataAnonimator.setLocationRelativeTo(null);
		
		JButton btnMaskData = new JButton("Anonymize Dataset");
		btnMaskData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnMaskData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				ifrMaskData = null;
				try {
					ifrMaskData = new IfrMaskData();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"The window can't be opened.", 
							"Error loading Anonymize Dataset", JOptionPane.ERROR_MESSAGE);
				}
				frmDataAnonimator.setVisible(false);
				ifrMaskData.setVisible(true);
			}
		});
		btnMaskData.setBounds(42, 44, 214, 40);
		frmDataAnonimator.getContentPane().add(btnMaskData);
		
		JButton btnCompareData = new JButton("Evaluate Dataset");
		btnCompareData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCompareData.setBounds(42, 120, 214, 40);
		frmDataAnonimator.getContentPane().add(btnCompareData);
		btnCompareData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0)
			{
				ifrCompareData = null;
				
				try {
					ifrCompareData = new IfrCompareData();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"The window can't be opened.", 
							"Error loading Evaluate Dataset", JOptionPane.ERROR_MESSAGE);
				}
				frmDataAnonimator.setVisible(false);
				ifrCompareData.setVisible(true);
			}
		});
	}
}