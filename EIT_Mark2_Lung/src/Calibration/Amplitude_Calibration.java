package Calibration;


import java.awt.EventQueue;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import MainFunctions.Control;
import MainFunctions.Definition;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Amplitude_Calibration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Amp_Calibration_fun Amp_Cal_fun = new Amp_Calibration_fun(this);
	private JPanel contentPane;
	private JTextField Amp_txt;
	JComboBox<String> Freq_Comb;
	private static Control EIT_Control_Dlg ;
	int m_nFreq;
	int[] m_nAmp =  new int[Definition.TOTAL_CH+1];
	FileWriter[] fout = new FileWriter[Definition.TOTAL_CH];
	int m_nAverage;
	String m_strFreq ;
	
	 String m_strDefaultFilePath =  System.getProperty("user.dir");
	  String m_strCalibration = "Calibration";
	  String m_strCategory = "Amplitude";
	  String m_strLog = "Log"; 
	  String m_strUSBID  = "eit1";
	  
	  int Source,Sink;
	  String FileName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Amplitude_Calibration frame = new Amplitude_Calibration(getEIT_Control_Dlg());
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
	public Amplitude_Calibration(Control control) {
		setTitle("Amplitude Calibration");
		Amplitude_Calibration.setEIT_Control_Dlg(control);
		setBounds(100, 100, 395, 178);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("Frequency");
		label.setBounds(21, 23, 75, 21);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("Amplitude");
		label_1.setBounds(21, 55, 75, 21);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel("Average");
		label_2.setBounds(21, 87, 75, 21);
		contentPane.add(label_2);
		
		String[] m_strFreqs = new String [10];
		m_strFreqs[0] = "11.25Hz";
		m_strFreqs[1] = "56.25Hz";
		m_strFreqs[2] = "112.5Hz";
		m_strFreqs[3] = "1.125KHz";
		m_strFreqs[4] = "5.625KHz";
		m_strFreqs[5] = "11.25KHz";
		m_strFreqs[6] = "56.25KHz";
		m_strFreqs[7] = "112.5KHz";
		m_strFreqs[8] = "247.5KHz";
		m_strFreqs[9] = "450KHz";
	    Freq_Comb = new JComboBox<String>();
		Freq_Comb.setBounds(111, 23, 108, 20);
		
		for (int i =0 ;i< Definition.NUM_OF_FREQUENCY;i++)
		{
			Freq_Comb.addItem(m_strFreqs[i]);
		}
		Freq_Comb.setSelectedIndex(4);
		Freq_Comb.setBounds(111, 23, 108, 20);
		contentPane.add(Freq_Comb);
		
		Amp_txt = new JTextField();
		Amp_txt.setText("512");
		Amp_txt.setColumns(10);
		Amp_txt.setBounds(111, 55, 108, 20);
		contentPane.add(Amp_txt);
		
		JComboBox<String> avg_comb = new JComboBox<String>();
		avg_comb.setBounds(111, 87, 108, 20);
		avg_comb.addItem("1");
		avg_comb.addItem("2");
		avg_comb.addItem("4");
		avg_comb.addItem("8");
		avg_comb.addItem("16");
		avg_comb.addItem("32");
		avg_comb.addItem("64");
		avg_comb.setSelectedIndex(6);
		avg_comb.setBounds(111, 87, 108, 20);
		contentPane.add(avg_comb);
		
		JButton execute_btn = new JButton("Execute");
		execute_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				m_nFreq = Freq_Comb.getSelectedIndex();         
				m_nAmp[0] = Integer.parseInt(Amp_txt.getText());
				m_strFreq = Freq_Comb.getSelectedItem().toString();
				m_nAverage = Integer.parseInt(avg_comb.getSelectedItem().toString());
				getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(0, 1, 1, 0);
				getEIT_Control_Dlg().EIT_Control.MakeCCSTable();
				try {
					Amp_Cal_fun.AmpCalSystemSetting();
					
					 String FilePath;
						FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
						new File(FilePath).mkdirs();
						FilePath = FilePath + "//" + m_strUSBID;
						new File(FilePath).mkdirs();
						FilePath = FilePath + "//" + m_strCategory;
						new File(FilePath).mkdirs();
						FilePath = FilePath + "//" + m_strFreq;
						new File(FilePath).mkdirs();
						FilePath = FilePath + "//" + m_strLog;
						new File(FilePath).mkdirs();
						
						for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
						{
							Source = Ch;
							Sink = Ch + 1;
							if(Sink > 15)
							{
								Sink = 0;
							}
							FileName="//Amplitude"+(Source+1)+"Ch_"+(Sink+1)+"Ch.txt";
							fout[Ch]= new FileWriter(FilePath + FileName);


							if(!Amp_Cal_fun.GetMultiAmpResponse(Source,m_nAmp[Ch],Sink,0,1023,100)) //will not return Data Access Directly Mag,SinkAmp,Overflow
							{
								return;
							}
							fout[Ch].write( "SourceAmp:\t" + m_nAmp[Ch] + "\t" +"MinSinkAmp:\t" + Amp_Cal_fun.SinkAmp + "\t" + "MinMagnitude:\t" + Amp_Cal_fun.MinMagnitude+ "\r\n\r\n" );
							if(!Amp_Cal_fun.GetMultiAmpResponse(Source,m_nAmp[Ch],Sink,Amp_Cal_fun.SinkAmp-100,Amp_Cal_fun.SinkAmp+100,10))
							{
								return;
							}
							fout[Ch].write("SourceAmp:\t" + m_nAmp[Ch] + "\t" +"MinSinkAmp:\t" + Amp_Cal_fun.SinkAmp + "\t" + "MinMagnitude:\t" + Amp_Cal_fun.MinMagnitude+ "\r\n\r\n") ;
							if(!Amp_Cal_fun.GetMultiAmpResponse(Source,m_nAmp[Ch],Sink,Amp_Cal_fun.SinkAmp-10,Amp_Cal_fun.SinkAmp+10,1))
							{
								return;
							}
							fout[Ch].write("SourceAmp:\t" + m_nAmp[Ch] + "\t" +"MinSinkAmp:\t" + Amp_Cal_fun.SinkAmp + "\t" + "MinMagnitude:\t" + Amp_Cal_fun.MinMagnitude + "\r\n\r\n" );
							fout[Ch].close();
							m_nAmp[Ch+1] = Amp_Cal_fun.SinkAmp;
						}
						double [] m_dFactor = new double[Definition.TOTAL_CH];
						for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
						{
							m_dFactor[Ch] = (double)((double)m_nAmp[Ch] / (double)m_nAmp[0]);
						}

						FilePath = m_strDefaultFilePath + "//" + m_strCalibration + "//" + m_strUSBID + "//" + m_strCategory + "//" + m_strFreq;
						Amp_Cal_fun.AmpCalWriteFile(Definition.TOTAL_CH,FilePath,m_dFactor);
						Amp_Cal_fun.ConvertAmpCalibrationData();
						
				 		JOptionPane.showMessageDialog(null, "Finish", "InfoBox: " + "Done", JOptionPane.INFORMATION_MESSAGE);

						
						
						
				} catch (HeadlessException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		execute_btn.setBounds(252, 21, 108, 88);
		contentPane.add(execute_btn);
	}

	public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}
}
