package MainFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Calibration.Amplitude_Calibration;
import Calibration.DCoffSet_Calibration;
import Calibration.Output_Impedance_Calibration;
import USBCommuniction.SiUSBXp;
import USBCommuniction.USB_Functions;

import javax.swing.JRadioButton;

public class Control {
	USB_Functions Usb = new USB_Functions();
	
	public S_EIT_Doc EIT_Control = new S_EIT_Doc(this);  
	public S_EIT_Image m_pImageDlg = new S_EIT_Image (this);
	DCoffSet_Calibration DCoffSet_Cal = new DCoffSet_Calibration(this);
	Output_Impedance_Calibration Output_Cal = new Output_Impedance_Calibration(this);
	Amplitude_Calibration Amp_Cal = new Amplitude_Calibration(this);
	Bio_Siganl_Graphs BioSig_Dlg = new Bio_Siganl_Graphs ("Bio-Signals",this);
	//MultiSignalGraph BioSig_Dlg = new MultiSignalGraph ("Bio-Signals",this);

	
	int m_nUSBConnection=0;
	int m_nDeviceNum=0;
	int m_nPortStatus =0;
	byte[] m_nData = new byte[10];
	int[] m_nData_int = new int[10];
	int[] m_nResponse = new int[20];
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;

	private JFrame frmEitMark;
	private JTextField USBConnection_txt;
	private JTextField data1_txt;
	private JTextField data2_txt;
	private JTextField data3_txt;
	private JTextField data4_txt;
	private JTextField data5_txt;
	private JTextField data6_txt;
	private JTextField data7_txt;
	private JTextField data8_txt;
	private JTextField data9_txt;
	private JTextField data10_txt;
	private JTextField open_txt;
	private final JComboBox<String> m_comboScriptFile = new JComboBox<String>();

	
	public String DirectoryPath = System.getProperty("user.dir");
	public String ProjectionFilePath = DirectoryPath+"/ScriptFile/";
	public String[] files= fileNames(DirectoryPath+"/ScriptFile");
	private JLabel label;
	public String Path ;
	
	JFileChooser fc = new JFileChooser(DirectoryPath);
	JFileChooser fileSvaeChooser = new JFileChooser(DirectoryPath);
	File file = null;
	public static final String newlines = "\n";
	String m_strSaveFilePath;
	boolean m_bSaveData;
	boolean m_bNumofScans = false;
	boolean m_bEventTrigger= false;
	boolean m_bScanInterval= false;
	int m_nNumofScans=0;
	int m_nScanNumber=0;
	int m_nEventNumber=0;
	Component[] panel_com;
	Component[] panel_1_com;
	Component[] panel_2_com;
	
	
	// Image Part 
	boolean m_bImageDlg1= false;
	boolean m_bGraphDlg1= false;
	boolean m_bioGraph = false;
	
	public float SNR_value = 0;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");   ///  this line for Grid exception may need change later
					Control window = new Control();
					window.frmEitMark.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Control() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEitMark = new JFrame();
		frmEitMark.getContentPane().setLocation(-357, -22);
		frmEitMark.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Ghazal\\workspace\\EIT_Mark2\\Images\\logo.png"));
		frmEitMark.setTitle("Bio_Signals");
		frmEitMark.setBounds(100, 100, 735, 446);
		frmEitMark.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEitMark.getContentPane().setLayout(null);
		
		if(m_nPortStatus == 0)
		{
			System.out.printf("Port Close");
		}
		else if(m_nPortStatus == 1)
		{
			System.out.printf("Port Open");
		}
		else
		{
			System.out.printf("Port Error");
		}
		m_comboScriptFile.addItem("----------------------------------------------");
		for (int i =0 ;i< files.length;i++)
		{
			m_comboScriptFile.addItem(files[i]);
		}
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Step 2", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 268, 337, 113);
		frmEitMark.getContentPane().add(panel);
		panel.setLayout(null);
		
		label = new JLabel("Script File:");
		label.setBounds(10, 30, 67, 14);
		panel.add(label);
		m_comboScriptFile.setBounds(91, 22, 226, 31);
		panel.add(m_comboScriptFile);
		
		JCheckBox viewCheckBox = new JCheckBox("View File");
		viewCheckBox.setBounds(6, 70, 79, 23);
		panel.add(viewCheckBox);
		 
		JButton other_script_btn = new JButton("Other");
		other_script_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);
				 int returnVal = fc.showOpenDialog(frmEitMark);
				 
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                file = fc.getSelectedFile();
		                //This is where a real application would open the file.
		              
		            
		                try {
							EIT_Control.LoadScriptFile(file.getAbsolutePath(), file.getParent()+"/");
							viewCheckBox.setSelected(false);
						} catch (HeadlessException | NumberFormatException
								| IOException e1) {
							e1.printStackTrace();
							System.err.format("IOException: %s%n", e1);
						}
						
		                
		            } else {
		                path_txt.setText("Open command cancelled by user.: .\n" );
		            }
		      
		        } 
		});
		
		other_script_btn.setBounds(91, 66, 226, 31);
		panel.add(other_script_btn);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Step 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(10, 11, 337, 246);
		frmEitMark.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
			
			USBConnection_txt = new JTextField();
			USBConnection_txt.setText("USB Status");
			USBConnection_txt.setBounds(20, 28, 120, 31);
			panel_1.add(USBConnection_txt);
			USBConnection_txt.setColumns(10);
			
			JButton chk_btn = new JButton("Check");
			chk_btn.setBounds(20, 70, 120, 31);
			panel_1.add(chk_btn);
			
			open_txt = new JTextField();
			open_txt.setText("Port Status");
			open_txt.setBounds(174, 28, 141, 31);
			panel_1.add(open_txt);
			open_txt.setColumns(10);
			
			JButton open_btn = new JButton("Open");
			open_btn.setBounds(172, 70, 64, 31);
			panel_1.add(open_btn);
			
			JButton colse_btn = new JButton("Close");
			colse_btn.setBounds(246, 70, 69, 31);
			panel_1.add(colse_btn);
			
			data1_txt = new JTextField();
			data1_txt.setBounds(20, 112, 51, 20);
			panel_1.add(data1_txt);
			data1_txt.setText("0");
			data1_txt.setColumns(10);
			
			data2_txt = new JTextField();
			data2_txt.setBounds(81, 112, 51, 20);
			panel_1.add(data2_txt);
			data2_txt.setText("0");
			data2_txt.setColumns(10);
			
			data3_txt = new JTextField();
			data3_txt.setBounds(142, 112, 51, 20);
			panel_1.add(data3_txt);
			data3_txt.setText("0");
			data3_txt.setColumns(10);
			
			data4_txt = new JTextField();
			data4_txt.setBounds(203, 112, 51, 20);
			panel_1.add(data4_txt);
			data4_txt.setText("0");
			data4_txt.setColumns(10);
			
			data5_txt = new JTextField();
			data5_txt.setBounds(264, 112, 51, 20);
			panel_1.add(data5_txt);
			data5_txt.setText("0");
			data5_txt.setColumns(10);
			
			data10_txt = new JTextField();
			data10_txt.setBounds(264, 143, 51, 20);
			panel_1.add(data10_txt);
			data10_txt.setText("0");
			data10_txt.setColumns(10);
			
			data9_txt = new JTextField();
			data9_txt.setBounds(203, 143, 51, 20);
			panel_1.add(data9_txt);
			data9_txt.setText("0");
			data9_txt.setColumns(10);
			
			data8_txt = new JTextField();
			data8_txt.setBounds(142, 143, 51, 20);
			panel_1.add(data8_txt);
			data8_txt.setText("0");
			data8_txt.setColumns(10);
			
			data7_txt = new JTextField();
			data7_txt.setBounds(81, 143, 51, 20);
			panel_1.add(data7_txt);
			data7_txt.setText("0");
			data7_txt.setColumns(10);
			
			data6_txt = new JTextField();
			data6_txt.setBounds(20, 143, 51, 20);
			panel_1.add(data6_txt);
			data6_txt.setText("0");
			data6_txt.setColumns(10);
			
			JButton send_btn = new JButton("Send");
			send_btn.setBounds(20, 174, 295, 40);
			panel_1.add(send_btn);
			
			panel_2 = new JPanel();
			panel_2.setBorder(new TitledBorder(null, "Step 3", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_2.setBounds(357, 11, 352, 370);
			frmEitMark.getContentPane().add(panel_2);
			panel_2.setLayout(null);
			
			JCheckBox chckbxSaveData = new JCheckBox("Save Data");
			chckbxSaveData.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(m_bSaveData == true)
					{
						m_bSaveData = false;		
					}
					else
					{
						m_bSaveData = true;
					}
				}
			});
			chckbxSaveData.setBounds(21, 33, 117, 23);
			panel_2.add(chckbxSaveData);
			
			JCheckBox chckbxNumberOfScans = new JCheckBox("Number of Scans");
			chckbxNumberOfScans.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(m_bNumofScans == true)
					{
						m_bNumofScans = false;
						num_scan_txt.setEnabled(false);
					}
					else
					{
						m_bNumofScans = true;
						num_scan_txt.setEnabled(true);
					}
				}
			});
			chckbxNumberOfScans.setBounds(21, 106, 142, 23);
			panel_2.add(chckbxNumberOfScans);
			
			JCheckBox chckbxEventTrigger = new JCheckBox("Event Trigger");
			chckbxEventTrigger.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(m_bEventTrigger == true)
					{
						m_bEventTrigger = false;
						event_trig_txt.setEnabled(false);
					}
					else
					{
						m_bEventTrigger = true;
						event_trig_txt.setEnabled(true);
					}
					
				}
			});
			chckbxEventTrigger.setBounds(21, 136, 117, 23);
			panel_2.add(chckbxEventTrigger);
			
			JButton stop_btn = new JButton("Stop");
			JButton path_btn = new JButton("File Path");
			path_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES(*.txt)", "txt", "text");
					fileSvaeChooser.setFileFilter(filter);
					fileSvaeChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
					int returnVal = fileSvaeChooser.showSaveDialog(frmEitMark);
					
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fileSvaeChooser.getSelectedFile();
		                m_strSaveFilePath= file.getAbsolutePath();
		                EIT_Control.m_strSaveFilePath = m_strSaveFilePath+"/";
		                path_txt.setText(m_strSaveFilePath);
		            } else {
		            	m_strSaveFilePath ="";
		            }
		         
			
				}
			});
			path_btn.setBounds(151, 33, 173, 25);
			panel_2.add(path_btn);
			
			path_txt = new JTextField();
			path_txt.setColumns(10);
			path_txt.setBounds(25, 70, 299, 25);
			panel_2.add(path_txt);
			
			num_scan_txt = new JTextField();
			num_scan_txt.setEnabled(false);
			num_scan_txt.setText("0");
			num_scan_txt.setColumns(10);
			num_scan_txt.setBounds(182, 105, 142, 25);
			panel_2.add(num_scan_txt);
			
			event_trig_txt = new JTextField();
			event_trig_txt.setEnabled(false);
			event_trig_txt.setText("0");
			event_trig_txt.setColumns(10);
			event_trig_txt.setBounds(182, 135, 142, 25);
			panel_2.add(event_trig_txt);
			
			chckbxImag = new JCheckBox("Image ");
			chckbxImag.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					if(m_bImageDlg1 == true)
					{
						m_bImageDlg1 = false;
						m_pImageDlg.setVisible(false);
						chckbxImag.setSelected(false);
					}
					else
					{
						m_bImageDlg1 = true;
						// Declare other GUI 
						 
						m_pImageDlg.setVisible(true);
						chckbxImag.setSelected(true);
					}
				}
			});
			chckbxImag.setBounds(21, 205, 117, 23);
			panel_2.add(chckbxImag);
			
			JLabel lblScanNumber = new JLabel("Scan Number");
			lblScanNumber.setBounds(21, 172, 117, 23);
			panel_2.add(lblScanNumber);
			
			scan_num_txt = new JTextField();
			scan_num_txt.setText("0");
			scan_num_txt.setColumns(10);
			scan_num_txt.setBounds(182, 171, 142, 25);
			panel_2.add(scan_num_txt);
			
			panel_com = panel.getComponents();
			panel_1_com = panel_1.getComponents();
			panel_2_com = panel_2.getComponents();
			
			 
			JButton scan_btn = new JButton("Scan");
			
			scan_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EIT_Control.LoadFabGREITAlgorithmFile();
					
				
					
					EIT_Control.LoadCalibrationFile(EIT_Control.m_bDCOffsetCal,EIT_Control.m_bOutputImpedanceCal,EIT_Control.m_bAmplitudeCal,EIT_Control.m_bVoltmeterCal);
					EIT_Control.MakeCCSTable();
					EIT_Control.MakeProjectionTable();
					EIT_Control.MakeSWTable();
					//SNR_value = Float.valueOf(snrtxt.getText());
					EIT_Control.m_bSave=m_bSaveData;
					SiUSBXp.INSTANCE.SI_FlushBuffers(EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);
					EIT_Control.USB.USBComm((byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0);
					if((int) (EIT_Control.USB.buf[0] &0xFF)  != 129)
						JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					else
					{
						DeActiveGUI();
						chckbxImag.setEnabled(true);
					
						m_nEventNumber = Integer.parseInt(event_trig_txt.getText());
						m_nNumofScans = Integer.parseInt(num_scan_txt.getText());
						try {
						//	EIT_Control.IMAGE_LoadAlgorithmFile(Definition.TOTAL_CH);
								if(!EIT_Control.EIT_PipelineScan())
								{
									ActiveGUI();
								}
								
							
						
						} catch (HeadlessException e1) {
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				
					
				}
			});
			scan_btn.setBounds(21, 280, 303, 31);
			panel_2.add(scan_btn);
			
			
			stop_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopScan();
				}
			});
			stop_btn.setBounds(21, 321, 303, 31);
			panel_2.add(stop_btn);
			
			JCheckBox chckbxBioSignalData = new JCheckBox("Bio Signal Data");
			chckbxBioSignalData.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(m_bioGraph == true)
					{
						m_bioGraph = false;
						BioSig_Dlg.setVisible(false);
						chckbxBioSignalData.setSelected(false);
					}
					else
					{
						m_bioGraph = true;
						BioSig_Dlg.setVisible(true);
						chckbxBioSignalData.setSelected(true);
					}
					
				}
			});
			chckbxBioSignalData.setBounds(21, 231, 117, 23);
			panel_2.add(chckbxBioSignalData);
			
			menuBar = new JMenuBar();
			frmEitMark.setJMenuBar(menuBar);
			
			mnCalibration = new JMenu("Calibration");
			menuBar.add(mnCalibration);
			
			JMenuItem mntmDcOffSet = new JMenuItem("DC Off Set");
			mntmDcOffSet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					DCoffSet_Cal.setVisible(true);
				}
			});
			mnCalibration.add(mntmDcOffSet);
			
			JMenuItem mntmOutput = new JMenuItem("Output Impedance  ");
			mntmOutput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Output_Cal.setVisible(true);
				}
			});
			mnCalibration.add(mntmOutput);
			
			JMenuItem mntmAmplitude = new JMenuItem("Amplitude");
			mntmAmplitude.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Amp_Cal.setVisible(true);
				}
			});
			
			mntmVoltmeter = new JMenuItem("Voltmeter");
			mntmVoltmeter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//Volt_Cal.setVisible(true);
				}
			});
			mnCalibration.add(mntmVoltmeter);
			mnCalibration.add(mntmAmplitude);
			send_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SiUSBXp.INSTANCE.SI_FlushBuffers(EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);
					try
					{
					/*int temp0 =Integer.valueOf(data1_txt.getText());
					int temp1 =Integer.valueOf(data2_txt.getText());
					int temp2 =Integer.valueOf(data3_txt.getText());
					int temp3 =Integer.valueOf(data4_txt.getText());
					int temp4 =Integer.valueOf(data5_txt.getText());
					int temp5 =Integer.valueOf(data6_txt.getText());
					int temp6 =Integer.valueOf(data7_txt.getText());
					int temp7 =Integer.valueOf(data8_txt.getText());
					int temp8 =Integer.valueOf(data9_txt.getText());
					int temp9 =Integer.valueOf(data10_txt.getText());
					
					m_nData[0] = (byte)temp0;
					m_nData[1] = (byte)temp1;
					m_nData[2] = (byte)temp2;
					m_nData[3] = (byte)temp3;
					m_nData[4] = (byte)temp4;
					m_nData[5] = (byte)temp5;
					m_nData[6] = (byte)temp6;
					m_nData[7] = (byte)temp7;
					m_nData[8] = (byte)temp8;
					m_nData[9] = (byte)temp9;*/
					
					m_nData[0]=Byte.valueOf(data1_txt.getText());
					m_nData[1]=Byte.valueOf(data2_txt.getText());
					m_nData[2]=Byte.valueOf(data3_txt.getText());
					m_nData[3]=Byte.valueOf(data4_txt.getText());
					m_nData[4]=Byte.valueOf(data5_txt.getText());
					m_nData[5]=Byte.valueOf(data6_txt.getText());
					m_nData[6]=Byte.valueOf(data7_txt.getText());
					m_nData[7]=Byte.valueOf(data8_txt.getText());
					m_nData[8]=Byte.valueOf(data9_txt.getText());
					m_nData[9]=Byte.valueOf(data10_txt.getText());

					EIT_Control.USB.USBComm(m_nData[0],m_nData[1],m_nData[2],m_nData[3],m_nData[4],m_nData[5],m_nData[6],m_nData[7],m_nData[8],m_nData[9]);
					
					}
					catch(NumberFormatException e1)
					{
						JOptionPane.showMessageDialog(null, "Please enter an integer between 0 and 127", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					}
					for (int j =0; j<10;j++)
					{
						m_nResponse[j]= (int) (EIT_Control.USB.buf[j] &0xFF) ;
					}
					JOptionPane.showMessageDialog(null,m_nResponse[0]+"  "+(int)m_nResponse[1]+"  "+(int)m_nResponse[2]+"  "+ (int)m_nResponse[3]+"  "+(int)m_nResponse[4]+"  "+(int)m_nResponse[5]+"  "+(int)m_nResponse[6]+"  "+(int)m_nResponse[7]+"  "+(int)m_nResponse[8]+"  "+(int)m_nResponse[9], "InfoBox: " + "Result", JOptionPane.INFORMATION_MESSAGE);
				
					
				}
			});
			colse_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(m_nPortStatus != 0)
					{
						if(EIT_Control.USB.USBPortClose(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
						{
							open_txt.setText("Port Open Error");
							m_nPortStatus = -1;
						}
						else
						{
							m_nPortStatus = 0;
						}
						

						if(m_nPortStatus == 0)
						{
							open_txt.setText("Port Close");
						}
						else if(m_nPortStatus == 1)
						{
							open_txt.setText("Port Open");
						}
						else
						{
							open_txt.setText("Port Error");
						}
					
					}
					else 
					{
						open_txt.setText("Port Already Close");
					}
				}
			});
			open_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(m_nPortStatus != 1)
					{
						if(EIT_Control.USB.USBPortOpen(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
						{
							System.out.println("Port Open Error");
							m_nPortStatus = -1;
						}
						else
						{
							m_nPortStatus = 1;
						}

						if(m_nPortStatus == 0)
						{
							open_txt.setText("Port Close");
						}
						else if(m_nPortStatus == 1)
						{
							open_txt.setText("Port Open");
						}
						else
						{
							open_txt.setText("Port Error");
						}
					}
					
				}
			});
			chk_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					EIT_Control.USB.FillDeviceList();
					m_nDeviceNum = EIT_Control.USB.m_nNumofDevice;
					EIT_Control.USB.DeviceSelect(m_nDeviceNum-1);
					m_nUSBConnection = m_nDeviceNum;
				
					if (m_nDeviceNum >= 1)
					{
						USBConnection_txt.setText("Connected");
					}
					else {
						USBConnection_txt.setText("Disconnect");
					}
				
				}
			});
		viewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(viewCheckBox.isSelected())
				{
					Files_View f;
					try {
						if (m_comboScriptFile.getSelectedIndex()!=0 )
						{
							String Path = DirectoryPath+"/ScriptFile/"+m_comboScriptFile.getSelectedItem();
							SetString(Path);
							f = new Files_View();
							f.setVisible(true);
							
							
						}
						else if (file != null)
						{
							
							String Path =file.getAbsolutePath();
							SetString(Path);
							f = new Files_View();
							f.setVisible(true);
						}
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Please Choose One File", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
					}
				}
				
				else
				{
				Files_View f;
				try {
					f = new Files_View();
					f.setVisible(false);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				}
				
			}
		});
		
		m_comboScriptFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (m_comboScriptFile.getSelectedIndex()!=0 )
					{
						EIT_Control.LoadScriptFile(DirectoryPath+"/ScriptFile/"+m_comboScriptFile.getSelectedItem(), ProjectionFilePath);
						viewCheckBox.setSelected(false);
						EIT_Control.LoadCalibrationFile(EIT_Control.m_bDCOffsetCal,EIT_Control.m_bOutputImpedanceCal,EIT_Control.m_bAmplitudeCal,EIT_Control.m_bVoltmeterCal);
				//		EIT_Control.MakeCCSTable();
				//		EIT_Control.MakeProjectionTable();
					}	
					
				} catch (HeadlessException | NumberFormatException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		
		
	}
	public static String[] fileNames(String directoryPath) {

	    File dir = new File(directoryPath);

	    Collection<String> files  =new ArrayList<String>();

	    if(dir.isDirectory()){
	        File[] listFiles = dir.listFiles();

	        for(File file : listFiles){
	            if(file.isFile()) {
	                files.add(file.getName());
	            }
	        }
	    }

	    return files.toArray(new String[]{});
	}
	
	void UpdateScanNumber(int ScanNum)
	{
		m_nScanNumber = ScanNum;
		scan_num_txt.setText(String.valueOf(m_nScanNumber));

		
	}
	
	
	public static String str;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel;
	private JTextField path_txt;
	private JTextField num_scan_txt;
	private JTextField event_trig_txt;
	private JCheckBox chckbxImag;
	private JTextField scan_num_txt;
	private JMenuBar menuBar;
	private JMenu mnCalibration;
	private JMenuItem mntmVoltmeter;
	public String SetString(String pathFile)
	{
		Control.str = pathFile;
		return  Control.str = pathFile;
	}
	 public String getString(){
	        return  Control.str ;   
	     }
	 void DeActiveGUI()
	 {
		 for (int a = 0; a < panel_com.length; a++) {
				panel_com[a].setEnabled(false);
			}
			for (int a = 0; a < panel_1_com.length; a++) {
				panel_1_com[a].setEnabled(false);
			}
			for (int a = 0; a < panel_2_com.length; a++) {
				panel_2_com[a].setEnabled(false);
			}
	 }
	 void ActiveGUI()
	 {
		 for (int a = 0; a < panel_com.length; a++) {
				panel_com[a].setEnabled(true);
				}
				for (int a = 0; a < panel_1_com.length; a++) {
					panel_1_com[a].setEnabled(true);
				}
				for (int a = 0; a < panel_2_com.length; a++) {
					panel_2_com[a].setEnabled(true);
				}
	 }
	 void stopScan()
		{
			
		 ActiveGUI();
			try {
				EIT_Control.EIT_PipelineScanStop();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	 
	 /*void UpdateOverfolw(boolean overflow_happened,String of_file)
		{
			if(!overflow_happened)
			{
	        	lblOverFlow.setIcon(new ImageIcon(DirectoryPath+"\\img\\yes.png"));
	        	
			}
			else if (overflow_happened)
			{
	        	lblOverFlow.setIcon(new ImageIcon(DirectoryPath+"\\img\\no.png"));
	        	OF_comboBox.addItem(of_file);

			}
			
	    	lblOverFlow.repaint();
			
		}*/
}
