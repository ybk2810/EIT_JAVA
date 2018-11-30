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
import javax.swing.border.TitledBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

import MainFunctions.Control;
import MainFunctions.Definition;


public class Output_Impedance_Calibration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField FR_txt ;
	private JTextField CR_txt;
	private JTextField FC_txt;
	private JTextField CC_txt;
	JComboBox<String> GIC_Sel_com;
	JComboBox<String> FreqComb ;
	JComboBox<String> avg_comb;
	JComboBox<String> select_comb1  ;
	JComboBox<String> select_comb2 ;
	JComboBox<String> select_comb3 ;
	JComboBox<String> select_comb4 ;
	private static Control EIT_Control_Dlg ;
	int m_nAverage;
	int m_nGICSelect;
	Output_Impedance_Calibration_fun DC_Cal_fun = new Output_Impedance_Calibration_fun(this);
	private JTextField range_min1_txt;
	private JTextField range_min2_txt;
	private JTextField range_max1_txt;
	private JTextField range_max2_txt;
	private JTextField step1_txt;
	private JTextField step2_txt;
	private JTextField range_min3_txt;
	private JTextField range_min4_txt;
	private JTextField range_max3_txt;
	private JTextField range_max4_txt;
	private JTextField step3_txt;
	private JTextField step4_txt;
	
	public int Cal1_ch  ;
	public int Cal2_ch  ;
	
	int m_nComponentSelect1;
	int m_nComponentSelect2;
	
	int m_nFreq;
	int m_nRangeMin;
	int m_nRangeMin2;
	int m_nRangeMin3;
	int m_nRangeMin4;
	
	int m_nRangeMax;
	int m_nRangeMax2;
	int m_nRangeMax3;
	int m_nRangeMax4;
	
	int m_nStep;
	int m_nStep2;
	int m_nStep3;
	int m_nStep4;
	
	int m_nR1;
	int m_nR2;
	int m_nC1;
	int m_nC2;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Output_Impedance_Calibration frame = new Output_Impedance_Calibration(getEIT_Control_Dlg());
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
	public Output_Impedance_Calibration(Control control) {
		setTitle("Output Impedance Calibration");
		Output_Impedance_Calibration.setEIT_Control_Dlg(control);
		setBounds(100, 100, 850, 559);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Output Impedance Calibration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(29, 11, 795, 128);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblFrequency = new JLabel("Frequency:");
		lblFrequency.setBounds(28, 34, 62, 21);
		panel.add(lblFrequency);
		
		
		String[] m_strFreq = new String [10];
		/*m_strFreq[0] = "22.5Hz";
		m_strFreq[1] = "112.5Hz";
		m_strFreq[2] = "225Hz";
		m_strFreq[3] = "2.25KHz";
		m_strFreq[4] = "11.25KHz";
		m_strFreq[5] = "22.5KHz";
		m_strFreq[6] = "112.5KHz";
		m_strFreq[7] = "225KHz";
		m_strFreq[8] = "495KHz";
		m_strFreq[9] = "900KHz";*/
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
	    
		
		FreqComb = new JComboBox<String>();
		for (int i =0 ;i< Definition.NUM_OF_FREQUENCY;i++)
		{
			FreqComb.addItem(m_strFreq[i]);
		}
		FreqComb.setSelectedIndex(4);
		FreqComb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int FreqIndex;
				FreqIndex = FreqComb.getSelectedIndex();
				switch (FreqIndex)
				{
				case 0:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(0);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(0);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 1:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(0);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(0);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 2:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(0);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(0);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 3:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(0);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(0);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 4:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(0);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(0);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 5:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(3);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(4);
					GIC_Sel_com.setSelectedIndex(4);
					break;
				case 6:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(3);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(4);
					GIC_Sel_com.setSelectedIndex(3);
					break;
				case 7:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(3);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(4);
					GIC_Sel_com.setSelectedIndex(3);
					break;
				case 8:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(3);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(4);
					GIC_Sel_com.setSelectedIndex(2);
					break;
				case 9:
					select_comb1.setSelectedIndex(1);
					select_comb2.setSelectedIndex(3);
					select_comb3.setSelectedIndex(2);
					select_comb4.setSelectedIndex(4);
					GIC_Sel_com.setSelectedIndex(1);
					break;

				}

			}
		});
		
		FreqComb.setBounds(106, 34, 108, 25);
		
		panel.add(FreqComb);
		
		JButton btnCalibrateAll = new JButton("Calibrate All");
		btnCalibrateAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Cal1_ch = 0;
				Cal2_ch = 3;
				getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,0,0,0);
				getEIT_Control_Dlg().EIT_Control.MakeCCSTable();
				
				m_nComponentSelect1 = select_comb1.getSelectedIndex();
				m_nComponentSelect2 = select_comb2.getSelectedIndex();

				m_nAverage = Integer.parseInt(avg_comb.getSelectedItem().toString());
				m_nGICSelect = GIC_Sel_com.getSelectedIndex();   ///  check
				
				m_nFreq =FreqComb.getSelectedIndex();
				m_nRangeMin = Integer.parseInt(range_min1_txt.getText());
				m_nRangeMin2 = Integer.parseInt(range_min2_txt.getText());
				m_nRangeMin3 = Integer.parseInt(range_min3_txt.getText());
				m_nRangeMin4 = Integer.parseInt(range_min4_txt.getText());
				
				m_nRangeMax = Integer.parseInt(range_max1_txt.getText());
				m_nRangeMax2 = Integer.parseInt(range_max2_txt.getText());
				m_nRangeMax3 = Integer.parseInt(range_max3_txt.getText());
				m_nRangeMax4 = Integer.parseInt(range_max4_txt.getText());
				
				
				m_nStep = Integer.parseInt(step1_txt.getText());
				m_nStep2 = Integer.parseInt(step2_txt.getText());
				m_nStep3 = Integer.parseInt(step1_txt.getText());
				m_nStep4 = Integer.parseInt(step2_txt.getText());
				
				m_nR1 = Integer.parseInt(FR_txt.getText());
				m_nR2 = Integer.parseInt(CR_txt.getText());
				
				m_nC1 = Integer.parseInt(FC_txt.getText());
				m_nC2 = Integer.parseInt(CC_txt.getText());
			
				int []  CoarseR = new int[Definition.Serial_EIT_ALL_CH];
				int []  FineR = new int[Definition.Serial_EIT_ALL_CH];
				int []  CoarseC = new int[Definition.Serial_EIT_ALL_CH];
				int []  FineC = new int[Definition.Serial_EIT_ALL_CH];
				try {
					if(!DC_Cal_fun.CCSCalSystemSetting())
					{
						JOptionPane.showMessageDialog(null, "CCS Calibration Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					}	
						DC_Cal_fun.CCSCalOpenFile();


						Object [] Cal_Data = DC_Cal_fun.CCSCalibration(CoarseR,FineR,CoarseC,FineC);
						CoarseR = (int[]) Cal_Data[0];
						FineR = (int[]) Cal_Data[1];
						CoarseC = (int[]) Cal_Data[2];
						FineC= (int[]) Cal_Data[3];

						DC_Cal_fun.CCSCalWriteFile(Definition.Serial_EIT_ALL_CH,DC_Cal_fun.m_strFilePath,FineR,CoarseR,FineC,CoarseC);
						
						DC_Cal_fun.CCSCalCloseFile();

						DC_Cal_fun.ConvertCalibrationData();
						
						JOptionPane.showMessageDialog(null, "Finish", "InfoBox: " + "Calibration", JOptionPane.INFORMATION_MESSAGE);

						return;
					
				} catch (HeadlessException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
			}
		});
		btnCalibrateAll.setBounds(664, 34, 108, 25);
		panel.add(btnCalibrateAll);
		
		textField = new JTextField();
		textField.setText("200");
		textField.setColumns(10);
		textField.setBounds(320, 34, 108, 25);
		panel.add(textField);
		
		JLabel lblAmplitude = new JLabel("Amplitude:");
		lblAmplitude.setBounds(243, 34, 62, 21);
		panel.add(lblAmplitude);
		
		JLabel lblAverage = new JLabel("Average:");
		lblAverage.setBounds(469, 34, 62, 21);
		panel.add(lblAverage);
		
	    avg_comb = new JComboBox<String>();
		avg_comb.setBounds(541, 34, 108, 25);
		avg_comb.addItem("1");
		avg_comb.addItem("2");
		avg_comb.addItem("4");
		avg_comb.addItem("8");
		avg_comb.addItem("16");
		avg_comb.addItem("32");
		avg_comb.addItem("64");
		avg_comb.setSelectedIndex(6);
		panel.add(avg_comb);
		
		  //getContentPane().setLayout(new FlowLayout());
	        
		   /*   Vector<JCheckBox> v = new Vector<JCheckBox>();
		      v.add(new JCheckBox("Channel 1", false));
		      v.add(new JCheckBox("Channel 2", false));
		      v.add(new JCheckBox("Channel 3", false));
		      v.add(new JCheckBox("Channel 4", false));
		      v.add(new JCheckBox("Channel 5", false));
		      v.add(new JCheckBox("Channel 6", false));
		      v.add(new JCheckBox("Channel 7", false));
		      v.add(new JCheckBox("Channel 8", false));
		      v.add(new JCheckBox("Channel 9", false));
		      v.add(new JCheckBox("Channel 10", false));
		      v.add(new JCheckBox("Channel 11", false));
		      v.add(new JCheckBox("Channel 12", false));
		      v.add(new JCheckBox("Channel 13", false));
		      v.add(new JCheckBox("Channel 14", false));
		      v.add(new JCheckBox("Channel 15", false));
		      v.add(new JCheckBox("Channel 16", false));
		      JComboCheckBox Channel_check = new JComboCheckBox(v);
		      Channel_check.setBounds(541, 76, 108, 25);
		      panel.add(Channel_check);*/
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Digipot Setup", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(29, 150, 795, 357);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		FR_txt = new JTextField();
		FR_txt.setBounds(129, 23, 108, 20);
		FR_txt.setText("128");
		panel_1.add(FR_txt);
		FR_txt.setColumns(10);
		
		CR_txt = new JTextField();
		CR_txt.setBounds(129, 56, 108, 20);
		CR_txt.setText("128");
		CR_txt.setColumns(10);
		panel_1.add(CR_txt);
		
		FC_txt = new JTextField();
		FC_txt.setBounds(657, 23, 108, 20);
		FC_txt.setText("128");
		FC_txt.setColumns(10);
		panel_1.add(FC_txt);
		
		CC_txt = new JTextField();
		CC_txt.setBounds(657, 56, 108, 20);
		CC_txt.setText("128");
		CC_txt.setColumns(10);
		panel_1.add(CC_txt);
		
		JLabel lblFiner = new JLabel("FineR");
		lblFiner.setBounds(28, 26, 62, 21);
		panel_1.add(lblFiner);
		
		JLabel lblCoarser = new JLabel("CoarseR");
		lblCoarser.setBounds(28, 59, 62, 21);
		panel_1.add(lblCoarser);
		
		JLabel lblFinec = new JLabel("FineC");
		lblFinec.setBounds(571, 26, 62, 21);
		panel_1.add(lblFinec);
		
		JLabel lblCoarsec = new JLabel("CoarseC");
		lblCoarsec.setBounds(571, 59, 62, 21);
		panel_1.add(lblCoarsec);
		
		JLabel lblGicSelect = new JLabel("GIC Select");
		lblGicSelect.setBounds(291, 22, 62, 21);
		panel_1.add(lblGicSelect);
		
	    GIC_Sel_com = new JComboBox<String>();
	    GIC_Sel_com.setBounds(392, 22, 108, 20);
		GIC_Sel_com.addItem("None");
		GIC_Sel_com.addItem("GIC 1");
		GIC_Sel_com.addItem("GIC 2");
		GIC_Sel_com.addItem("GIC 3");
		GIC_Sel_com.addItem("GIC 4");
		GIC_Sel_com.setSelectedIndex(4);
		panel_1.add(GIC_Sel_com);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBounds(10, 99, 775, 105);
		panel_1.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setBounds(10, 18, 62, 21);
		panel_2.add(lblSelect);
		
		JLabel label = new JLabel("Select");
		label.setBounds(10, 58, 62, 21);
		panel_2.add(label);
		
	    select_comb1 = new JComboBox<String>();
		select_comb1.addItem("None");
		select_comb1.addItem("CoraseR");
		select_comb1.addItem("FineR");
		select_comb1.addItem("CoraseC");
		select_comb1.addItem("FineC");
		select_comb1.setSelectedIndex(1);
		select_comb1.setBounds(82, 18, 108, 20);
		panel_2.add(select_comb1);
		
		select_comb2 = new JComboBox<String>();
		select_comb2.addItem("None");
		select_comb2.addItem("CoraseR");
		select_comb2.addItem("FineR");
		select_comb2.addItem("CoraseC");
		select_comb2.addItem("FineC");
		select_comb2.setSelectedIndex(3);
		select_comb2.setBounds(82, 58, 108, 20);
		panel_2.add(select_comb2);
		
		JLabel lblRange = new JLabel("Range");
		lblRange.setBounds(240, 18, 62, 21);
		panel_2.add(lblRange);
		
		JLabel label_1 = new JLabel("Range");
		label_1.setBounds(240, 58, 62, 21);
		panel_2.add(label_1);
		
		range_min1_txt = new JTextField();
		range_min1_txt.setText("0");
		range_min1_txt.setColumns(10);
		range_min1_txt.setBounds(293, 18, 108, 20);
		panel_2.add(range_min1_txt);
		
		range_min2_txt = new JTextField();
		range_min2_txt.setText("0");
		range_min2_txt.setColumns(10);
		range_min2_txt.setBounds(293, 58, 108, 20);
		panel_2.add(range_min2_txt);
		
		range_max1_txt = new JTextField();
		range_max1_txt.setText("255");
		range_max1_txt.setColumns(10);
		range_max1_txt.setBounds(450, 18, 108, 20);
		panel_2.add(range_max1_txt);
		
		range_max2_txt = new JTextField();
		range_max2_txt.setText("255");
		range_max2_txt.setColumns(10);
		range_max2_txt.setBounds(450, 58, 108, 20);
		panel_2.add(range_max2_txt);
		
		JLabel lblStep = new JLabel("Step");
		lblStep.setBounds(609, 18, 41, 21);
		panel_2.add(lblStep);
		
		JLabel label_2 = new JLabel("Step");
		label_2.setBounds(609, 58, 41, 21);
		panel_2.add(label_2);
		
		step1_txt = new JTextField();
		step1_txt.setText("10");
		step1_txt.setColumns(10);
		step1_txt.setBounds(654, 18, 108, 20);
		panel_2.add(step1_txt);
		
		step2_txt = new JTextField();
		step2_txt.setText("10");
		step2_txt.setColumns(10);
		step2_txt.setBounds(654, 58, 108, 20);
		panel_2.add(step2_txt);
		
		JLabel label_3 = new JLabel("   ~");
		label_3.setBounds(411, 18, 41, 21);
		panel_2.add(label_3);
		
		JLabel label_4 = new JLabel("   ~");
		label_4.setBounds(411, 58, 41, 21);
		panel_2.add(label_4);
		
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_3.setBounds(10, 227, 775, 105);
		panel_1.add(panel_3);
		
		JLabel label_5 = new JLabel("Select");
		label_5.setBounds(10, 18, 62, 21);
		panel_3.add(label_5);
		
		JLabel label_6 = new JLabel("Select");
		label_6.setBounds(10, 58, 62, 21);
		panel_3.add(label_6);
		
		select_comb3 = new JComboBox<String>();
		select_comb3.addItem("None");
		select_comb3.addItem("CoraseR");
		select_comb3.addItem("FineR");
		select_comb3.addItem("CoraseC");
		select_comb3.addItem("FineC");
		select_comb3.setSelectedIndex(2);
		select_comb3.setBounds(82, 18, 108, 20);
		panel_3.add(select_comb3);
		
		select_comb4 = new JComboBox<String>();
		select_comb4.addItem("None");
		select_comb4.addItem("CoraseR");
		select_comb4.addItem("FineR");
		select_comb4.addItem("CoraseC");
		select_comb4.addItem("FineC");
		select_comb4.setSelectedIndex(4);
		select_comb4.setBounds(82, 58, 108, 20);
		panel_3.add(select_comb4);
		
		JLabel label_7 = new JLabel("Range");
		label_7.setBounds(240, 18, 62, 21);
		panel_3.add(label_7);
		
		JLabel label_8 = new JLabel("Range");
		label_8.setBounds(240, 58, 62, 21);
		panel_3.add(label_8);
		
		range_min3_txt = new JTextField();
		range_min3_txt.setText("0");
		range_min3_txt.setColumns(10);
		range_min3_txt.setBounds(293, 18, 108, 20);
		panel_3.add(range_min3_txt);
		
		range_min4_txt = new JTextField();
		range_min4_txt.setText("0");
		range_min4_txt.setColumns(10);
		range_min4_txt.setBounds(293, 58, 108, 20);
		panel_3.add(range_min4_txt);
		
		range_max3_txt = new JTextField();
		range_max3_txt.setText("255");
		range_max3_txt.setColumns(10);
		range_max3_txt.setBounds(450, 18, 108, 20);
		panel_3.add(range_max3_txt);
		
		range_max4_txt = new JTextField();
		range_max4_txt.setText("255");
		range_max4_txt.setColumns(10);
		range_max4_txt.setBounds(450, 58, 108, 20);
		panel_3.add(range_max4_txt);
		
		JLabel label_9 = new JLabel("Step");
		label_9.setBounds(609, 18, 41, 21);
		panel_3.add(label_9);
		
		JLabel label_10 = new JLabel("Step");
		label_10.setBounds(609, 58, 41, 21);
		panel_3.add(label_10);
		
		step3_txt = new JTextField();
		step3_txt.setText("10");
		step3_txt.setColumns(10);
		step3_txt.setBounds(654, 18, 108, 20);
		panel_3.add(step3_txt);
		
		step4_txt = new JTextField();
		step4_txt.setText("10");
		step4_txt.setColumns(10);
		step4_txt.setBounds(654, 58, 108, 20);
		panel_3.add(step4_txt);
		
		JLabel label_11 = new JLabel("   ~");
		label_11.setBounds(411, 18, 41, 21);
		panel_3.add(label_11);
		
		JLabel label_12 = new JLabel("   ~");
		label_12.setBounds(411, 58, 41, 21);
		panel_3.add(label_12);
	}

	public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}
}
