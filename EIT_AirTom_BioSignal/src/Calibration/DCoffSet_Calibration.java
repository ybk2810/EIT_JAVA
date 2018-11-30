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
import java.io.IOException;


public class DCoffSet_Calibration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField Amp_txt;
	private static Control EIT_Control_Dlg ;

	DC_Calibration_fun DC_Cal_fun = new DC_Calibration_fun(this);
	int m_nAverage;
	int[] m_nAmp = new int[Definition.Serial_EIT_ALL_CH];
	int[] m_nFreq = new int[Definition.Serial_EIT_ALL_CH];
	public JComboBox<String> Freq_Comb;
	public int Cal1_ch  ;
	public int Cal2_ch  ;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DCoffSet_Calibration frame = new DCoffSet_Calibration(getEIT_Control_Dlg());
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
	public DCoffSet_Calibration(Control control) {
		setTitle("DC Offset Calibration");
		DCoffSet_Calibration.setEIT_Control_Dlg(control);
		setBounds(100, 100, 395, 178);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Frequency");
		lblNewLabel.setBounds(21, 23, 75, 21);
		contentPane.add(lblNewLabel);
		String[] m_strFreq = new String [10];
		m_strFreq[0] = "11.25Hz";
		m_strFreq[1] = "56.25Hz";
		m_strFreq[2] = "112.5Hz";
		m_strFreq[3] = "1.125KHz";
		m_strFreq[4] = "5.625KHz";
		m_strFreq[5] = "11.25KHz";
		m_strFreq[6] = "56.25KHz";
		m_strFreq[7] = "112.5KHz";
		m_strFreq[8] = "247.5KHz";
		m_strFreq[9] = "450KHz";
	    Freq_Comb = new JComboBox<String>();
		Freq_Comb.setBounds(111, 23, 108, 20);
		
		for (int i =0 ;i< Definition.NUM_OF_FREQUENCY;i++)
		{
			Freq_Comb.addItem(m_strFreq[i]);
		}
		Freq_Comb.setSelectedIndex(4);
		contentPane.add(Freq_Comb);
		
		JLabel lblAmplitude = new JLabel("Amplitude");
		lblAmplitude.setBounds(21, 55, 75, 21);
		contentPane.add(lblAmplitude);
		
		Amp_txt = new JTextField();
		Amp_txt.setText("200");
		Amp_txt.setBounds(111, 55, 108, 20);
		contentPane.add(Amp_txt);
		Amp_txt.setColumns(10);
		
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
		contentPane.add(avg_comb);
		
		JLabel lblAverage = new JLabel("Average");
		lblAverage.setBounds(21, 87, 75, 21);
		contentPane.add(lblAverage);
		
		JButton execute_btn = new JButton("Execute");
		execute_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_nAverage = Integer.parseInt(avg_comb.getSelectedItem().toString());
				Cal1_ch = 0;
				Cal2_ch = 3;
				for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
				{
					m_nFreq[i] = Freq_Comb.getSelectedIndex();         ///////////check
					m_nAmp[i] = Integer.parseInt(Amp_txt.getText());
				}
				getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(0, 1, 0, 0);
				getEIT_Control_Dlg().EIT_Control.MakeCCSTable();

				try {
					if(!DC_Cal_fun.DCOffsetCalSystemSetting())
					{
						return;
					}

					if(!DC_Cal_fun.DCOffsetGNDValue(Definition.Serial_EIT_ALL_CH))
					{
						return;
					}

					if(!DC_Cal_fun.DCOffsetCalSystemSetting())
					{
						return;
					}

					if(!DC_Cal_fun.DCOffset())
					{
						return;
					}

			 		JOptionPane.showMessageDialog(null, "Finish", "InfoBox: " + "Done", JOptionPane.INFORMATION_MESSAGE);
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
