package MainFunctions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EngineeringControlSNR extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Component frame = null;
	private JPanel contentPane;
	private JTextField load_txt;
	private JLabel lblNewLabel;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EngineeringControlSNR frame = new EngineeringControlSNR();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EngineeringControlSNR() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Ghazal\\workspace\\EIT_Mark2\\Images\\logo.png"));
		setTitle("Engineering Control");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 704, 539);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton load_btn = new JButton("Load");
		load_btn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		load_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileSvaeChooser = new JFileChooser(System.getProperty("user.dir"));
				String m_strSaveFilePath;
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES(*.txt)", "txt", "text");
				fileSvaeChooser.setFileFilter(filter);
				fileSvaeChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); 
				int returnVal = fileSvaeChooser.showSaveDialog(frame);
				
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fileSvaeChooser.getSelectedFile();
	                m_strSaveFilePath= file.getPath();
	                load_txt.setText(m_strSaveFilePath);
	            } else {
	            	m_strSaveFilePath ="";
	            }
	         
			}
		});
		load_btn.setBounds(516, 47, 115, 28);
		contentPane.add(load_btn);
		
		load_txt = new JTextField();
		load_txt.setBounds(152, 48, 330, 28);
		contentPane.add(load_txt);
		load_txt.setColumns(10);
		
		lblNewLabel = new JLabel("Load File Path : ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(47, 47, 95, 28);
		contentPane.add(lblNewLabel);
		
	
		

		 Scanner fin = null;
		double[] Ch = new double[256];
		double[] Over_flow = new double[256]; 
		double[] Real = new double[256]; 
		double[] Quad = new double[256]; 
		double[] Mag = new double[256];
		
		try {

	         fin = new Scanner(new File("C://Users//Ghazal//Desktop/Scan.txt"));
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		for (int i=0 ; i< 256;i++)
		{
			Ch[i]= fin.nextDouble();
			Over_flow[i]= fin.nextDouble();
			Real[i]= fin.nextDouble();
			Quad[i]= fin.nextDouble();
			Mag[i]= Math.sqrt(Math.pow(Real[i],2)+Math.pow(Quad[i],2));

			//System.out.println(++count);
		}		
		fin.close();
		
		XYSeries series = new XYSeries("SNR Graph");
		for (int j=0 ; j< 256;j++)
		{
			series.add(j+1, Mag[j]);
			//System.out.println(SNR[j]);
		}
		
		
		
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(
		"U_Shape ", // Title
		"", // x-axis Label
		"", // y-axis Label
		dataset, // Dataset
		PlotOrientation.VERTICAL, // Plot Orientation
		true, // Show Legend
		true, // Use tooltips
		false // Configure chart to generate URLs?
				);
		
		 final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 200));
		
	
		
	     //   final ChartPanel chartPanel = new ChartPanel(chart);
	       // chartpanel.setDomainZoomable(true);
	        chartPanel.setPreferredSize(new java.awt.Dimension(600, 200));
	        //setContentPane(chartPanel);
	       // chartPanel.setDomainZoomable(true);0
	    	panel = new JPanel();
			panel.setBounds(44, 153, 600, 300);
			panel.setLayout(new BorderLayout());
			panel.add(chartPanel, BorderLayout.CENTER);

			contentPane.add(panel);
			
			JButton ushape_btn = new JButton("U_Shape");
			ushape_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {/*
					 Scanner fin = null;
					double[] SNR = new double[256];
					double[] Temp_Avg = new double[256]; 
					double[] Avg = new double[256]; 
					double[] Std = new double[256]; 
					double[][] Temp_Data = new double[256][1000];
					
					try {

				         fin = new Scanner(new File("C://Users//Ghazal//Desktop//Java_Scan//Scan.txt"));
				        
				    } catch (FileNotFoundException e) {
				        e.printStackTrace();  
				    }
					for (int i=0 ; i< 3;i++)
					{
						for (int j=0 ; j< 256;j++)
						{
							Temp_Data[i][j]= fin.nextDouble();
							Temp_Avg[j]+=Temp_Data[i][j]; // each row one scan
						}
						
					}
					for (int j=0 ; j< 256;j++)
					{
						Avg[j]=Temp_Avg[j] /1000;
						
					}
					for (int i=0 ; i< 3;i++)
					{
						for (int j=0 ; j< 256;j++)
						{
							Std[j]+=(Math.pow((Temp_Data[i][j]-Avg[j]), 2))/2;  //over n-1
						}
						
					}
					XYSeries series = new XYSeries("XYGraph");
					for (int j=0 ; j< 256;j++)
					{
						SNR[j] = -20 * Math.log(Avg[j]/Std[j]);
						series.add(j+1, SNR[j]);
						System.out.println(SNR[j]);
					}
					
					
					
					// Add the series to your data set
					XYSeriesCollection dataset = new XYSeriesCollection();
					dataset.addSeries(series);
					// Generate the graph
					JFreeChart chart = ChartFactory.createXYLineChart(
					"SNR", // Title
					"SNR-axis", // x-axis Label
					"", // y-axis Label
					dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot Orientation
					true, // Show Legend
					true, // Use tooltips
					false // Configure chart to generate URLs?
							);
					
					 final ChartPanel chartPanel = new ChartPanel(chart);
					chartPanel.setPreferredSize(new java.awt.Dimension(600, 200));
					panel.add(chartPanel, BorderLayout.NORTH);
					contentPane.add(panel);
					try {
					ChartUtilities.saveChartAsJPEG(new File("C:\\chart.jpg"), chart, 500, 300);
						} 
					catch (IOException e) {
						System.err.println("Problem occurred creating chart.");
						}
					*/}
				
			});
			ushape_btn.setFont(new Font("Tahoma", Font.PLAIN, 13));
			ushape_btn.setBounds(516, 86, 115, 28);
			contentPane.add(ushape_btn);
	}
}
