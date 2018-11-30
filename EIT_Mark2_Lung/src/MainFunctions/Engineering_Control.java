package MainFunctions;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Calibration.Amplitude_Calibration;
import Calibration.DCoffSet_Calibration;
import Calibration.Output_Impedance_Calibration;
import Calibration.Voltmeter_Calibrations;
import USBCommuniction.SiUSBXp;
import USBCommuniction.USB_Functions;

public class Engineering_Control extends JFrame {
	
	static Control EIT_Control_Dlg;    //check if we need to change 
	USB_Functions Usb = new USB_Functions();
	public EIT_Mark2Doc EIT_Control = new EIT_Mark2Doc(EIT_Control_Dlg);  
	public EIT_Image m_pImageDlg = new EIT_Image (EIT_Control_Dlg);
	public EIT_Graph m_p_Rec_Imp_GraphDlg = new EIT_Graph(EIT_Control_Dlg); 
	DCoffSet_Calibration DCoffSet_Cal = new DCoffSet_Calibration(EIT_Control_Dlg);
	Output_Impedance_Calibration Output_Cal = new Output_Impedance_Calibration(EIT_Control_Dlg);
	Amplitude_Calibration Amp_Cal = new Amplitude_Calibration(EIT_Control_Dlg);
	Voltmeter_Calibrations Volt_Cal = new Voltmeter_Calibrations(EIT_Control_Dlg);
	
	int m_nUSBConnection=0;
	int m_nDeviceNum=0;
	int m_nPortStatus =0;
	byte[] m_nData = new byte[10];
	int[] m_nData_int = new int[10];
	int[] m_nResponse = new int[20];
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;


	private JPanel contentPane;
	private JTextField USBConnection_txt;
	private JTextField open_txt;
	private JTextField data1_txt;
	private JTextField data2_txt;
	private JTextField data3_txt;
	private JTextField data4_txt;
	private JTextField data5_txt;
	private JTextField data10_txt;
	private JTextField data9_txt;
	private JTextField data8_txt;
	private JTextField data7_txt;
	private JTextField data6_txt;
	private JTextField path_txt;
	private JTextField num_scan_txt;
	private JTextField event_trig_txt;
	private JTextField scan_num_txt;
	private JCheckBox viewCheckBox;
	private final JComboBox<String> m_comboScriptFile = new JComboBox<String>();

	
	public String DirectoryPath = System.getProperty("user.dir");
	public String ProjectionFilePath = DirectoryPath+"/ScriptFile/";
	public String[] files= fileNames(DirectoryPath+"/ScriptFile");
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
	boolean m_bUshapeDlg= false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Engineering_Control frame = new Engineering_Control(EIT_Control_Dlg);
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
	public Engineering_Control(Control control) {
		setTitle("Engineering Control");
		this.EIT_Control_Dlg = control; //check
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(16, 38, 761, 469);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(null, "Step 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(25, 42, 337, 246);
		contentPane.add(panel_1);
		
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
		
		
		USBConnection_txt = new JTextField();
		USBConnection_txt.setText("USB Status");
		USBConnection_txt.setColumns(10);
		USBConnection_txt.setBounds(20, 28, 120, 31);
		panel_1.add(USBConnection_txt);
		
		JButton chk_btn = new JButton("Check");
		chk_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			//	Usb.FillDeviceList();
			//	m_nDeviceNum = Usb.m_nNumofDevice;
			//	Usb.DeviceSelect(m_nDeviceNum-1);
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
		chk_btn.setBounds(20, 70, 120, 31);
		panel_1.add(chk_btn);
		
		open_txt = new JTextField();
		open_txt.setText("Port Status");
		open_txt.setColumns(10);
		open_txt.setBounds(174, 28, 141, 31);
		panel_1.add(open_txt);
		
		JButton open_btn = new JButton("Open");
		open_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(m_nPortStatus != 1)
				{
					//if(Usb.USBPortOpen(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
					if(EIT_Control.USB.USBPortOpen(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
					{
						//System.out.println(TestFunc.USBPortOpen(m_nDeviceNum-1)); ///SI_DEVICE_NOT_FOUND	
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
					//UpdateData(false);
				}
				

			
			}
		});
		open_btn.setBounds(172, 70, 64, 31);
		panel_1.add(open_btn);
		
		JButton colse_btn = new JButton("Close");
		colse_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(m_nPortStatus != 0)
				{
					if(EIT_Control.USB.USBPortClose(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
				//	if(Usb.USBPortClose(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
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
		colse_btn.setBounds(246, 70, 69, 31);
		panel_1.add(colse_btn);
		
		data1_txt = new JTextField();
		data1_txt.setText("0");
		data1_txt.setColumns(10);
		data1_txt.setBounds(20, 112, 51, 20);
		panel_1.add(data1_txt);
		
		data2_txt = new JTextField();
		data2_txt.setText("0");
		data2_txt.setColumns(10);
		data2_txt.setBounds(81, 112, 51, 20);
		panel_1.add(data2_txt);
		
		data3_txt = new JTextField();
		data3_txt.setText("0");
		data3_txt.setColumns(10);
		data3_txt.setBounds(142, 112, 51, 20);
		panel_1.add(data3_txt);
		
		data4_txt = new JTextField();
		data4_txt.setText("0");
		data4_txt.setColumns(10);
		data4_txt.setBounds(203, 112, 51, 20);
		panel_1.add(data4_txt);
		
		data5_txt = new JTextField();
		data5_txt.setText("0");
		data5_txt.setColumns(10);
		data5_txt.setBounds(264, 112, 51, 20);
		panel_1.add(data5_txt);
		
		data10_txt = new JTextField();
		data10_txt.setText("0");
		data10_txt.setColumns(10);
		data10_txt.setBounds(264, 143, 51, 20);
		panel_1.add(data10_txt);
		
		data9_txt = new JTextField();
		data9_txt.setText("0");
		data9_txt.setColumns(10);
		data9_txt.setBounds(203, 143, 51, 20);
		panel_1.add(data9_txt);
		
		data8_txt = new JTextField();
		data8_txt.setText("0");
		data8_txt.setColumns(10);
		data8_txt.setBounds(142, 143, 51, 20);
		panel_1.add(data8_txt);
		
		data7_txt = new JTextField();
		data7_txt.setText("0");
		data7_txt.setColumns(10);
		data7_txt.setBounds(81, 143, 51, 20);
		panel_1.add(data7_txt);
		
		data6_txt = new JTextField();
		data6_txt.setText("0");
		data6_txt.setColumns(10);
		data6_txt.setBounds(20, 143, 51, 20);
		panel_1.add(data6_txt);
		
		JButton send_btn = new JButton("Send");
		send_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//USB_lib.SI_FlushBuffers(Usb.dev_handle_ref.getValue(),(byte)0,(byte)0);
				SiUSBXp.INSTANCE.SI_FlushBuffers(EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);
				try
				{
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
				//Usb.USBComm(m_nData[0],m_nData[1],m_nData[2],m_nData[3],m_nData[4],m_nData[5],m_nData[6],m_nData[7],m_nData[8],m_nData[9]);

				EIT_Control.USB.USBComm(m_nData[0],m_nData[1],m_nData[2],m_nData[3],m_nData[4],m_nData[5],m_nData[6],m_nData[7],m_nData[8],m_nData[9]);
				
				}
				catch(NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(null, "Please enter an integer between 0 and 127", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				for (int j =0; j<10;j++)
				{
					//m_nResponse[j]= (int) (Usb.buf[j] &0xFF) ;
					m_nResponse[j]= (int) (EIT_Control.USB.buf[j] &0xFF) ;
				
				}
				JOptionPane.showMessageDialog(null,m_nResponse[0]+"  "+(int)m_nResponse[1]+"  "+(int)m_nResponse[2]+"  "+ (int)m_nResponse[3]+"  "+(int)m_nResponse[4]+"  "+(int)m_nResponse[5]+"  "+(int)m_nResponse[6]+"  "+(int)m_nResponse[7]+"  "+(int)m_nResponse[8]+"  "+(int)m_nResponse[9], "InfoBox: " + "Result", JOptionPane.INFORMATION_MESSAGE);
			
				
			
			}
		});
		send_btn.setBounds(20, 174, 295, 40);
		panel_1.add(send_btn);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(null, "Step 3", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(382, 42, 352, 375);
		contentPane.add(panel_2);
		
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
		chckbxNumberOfScans.setBounds(21, 117, 142, 23);
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
		chckbxEventTrigger.setBounds(21, 153, 117, 23);
		panel_2.add(chckbxEventTrigger);
		
		JButton path_btn = new JButton("File Path");
		path_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES(*.txt)", "txt", "text");
				fileSvaeChooser.setFileFilter(filter);
				fileSvaeChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
				int returnVal = fileSvaeChooser.showSaveDialog(Engineering_Control.this);
				
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fileSvaeChooser.getSelectedFile();
	                //m_strSaveFilePath= file.getParent()+"/";
	                m_strSaveFilePath= file.getAbsolutePath();
	                EIT_Control.m_strSaveFilePath = m_strSaveFilePath+"/";
	             //   System.out.println(m_strSaveFilePath);
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
		num_scan_txt.setText("0");
		num_scan_txt.setEnabled(false);
		num_scan_txt.setColumns(10);
		num_scan_txt.setBounds(182, 116, 142, 25);
		panel_2.add(num_scan_txt);
		
		event_trig_txt = new JTextField();
		event_trig_txt.setText("0");
		event_trig_txt.setEnabled(false);
		event_trig_txt.setColumns(10);
		event_trig_txt.setBounds(182, 152, 142, 25);
		panel_2.add(event_trig_txt);
		
		JCheckBox chckbxImag = new JCheckBox("Image ");
		chckbxImag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(m_bImageDlg1 == true)
				{
					m_bImageDlg1 = false;
				//	EIT_Control.m_pImageDlg.setVisible(false);
					m_pImageDlg.setVisible(false);
					chckbxImag.setSelected(false);
				}
				else
				{
					m_bImageDlg1 = true;
					//EIT_Control.m_pImageDlg.setVisible(true);
					// Declare other GUI 
					 
					m_pImageDlg.setVisible(true);
					chckbxImag.setSelected(true);
				}
			
			}
		});
		chckbxImag.setBounds(21, 219, 117, 23);
		panel_2.add(chckbxImag);
		
		JLabel lblScanNumber = new JLabel("Scan Number");
		lblScanNumber.setBounds(21, 189, 117, 23);
		panel_2.add(lblScanNumber);
		
		scan_num_txt = new JTextField();
		scan_num_txt.setText("0");
		scan_num_txt.setColumns(10);
		scan_num_txt.setBounds(182, 188, 142, 25);
		panel_2.add(scan_num_txt);
		
		JButton scan_btn = new JButton("Scan");
		scan_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/// just till finish the test 
				EIT_Control.m_bSave=m_bSaveData;
				SiUSBXp.INSTANCE.SI_FlushBuffers(EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);
				EIT_Control.USB.USBComm((byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0);
				if((int) (EIT_Control.USB.buf[0] &0xFF)  != 129)
					JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				else
				{
					DeActiveGUI();
					chckbxImag.setEnabled(true);
				/////////	UpdateData(TRUE);
				
					m_nEventNumber = Integer.parseInt(event_trig_txt.getText());
					m_nNumofScans = Integer.parseInt(num_scan_txt.getText());
					try {
					//	EIT_Control.IMAGE_LoadAlgorithmFile(Definition.TOTAL_CH);
					//	EIT_Control.EIT_PipelineScan();
						if(!EIT_Control.EIT_PipelineScan())
						{
							ActiveGUI();
						}
						
					} catch (HeadlessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			
				
			
			}
		});
		scan_btn.setBounds(21, 286, 303, 31);
		panel_2.add(scan_btn);
		
		JButton stop_btn = new JButton("Stop");
		stop_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopScan();
			}
		});
		stop_btn.setBounds(21, 323, 303, 31);
		panel_2.add(stop_btn);
		
		JCheckBox chckbxGraph = new JCheckBox("Graph");
		chckbxGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(m_bGraphDlg1 == true)
				{
					m_bGraphDlg1 = false;
				//	EIT_Control.m_pImageDlg.setVisible(false);
				//	m_pGraphDlg.setVisible(false);
					m_p_Rec_Imp_GraphDlg.setVisible(false);
					chckbxGraph.setSelected(false);
				}
				else
				{
					m_bGraphDlg1 = true;
					//EIT_Control.m_pImageDlg.setVisible(true);
					// Declare other GUI 
					 
					//m_pGraphDlg.setVisible(true);
					m_p_Rec_Imp_GraphDlg.setVisible(true);
					chckbxGraph.setSelected(true);
				}
			
			}
		});
		chckbxGraph.setBounds(182, 219, 117, 23);
		panel_2.add(chckbxGraph);
		
		JCheckBox chbxushape = new JCheckBox("U Shape");
		chbxushape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (m_bUshapeDlg)
				{
					m_bUshapeDlg = false ;
					//CombinedPlot  m_p_UshapeDlg = new CombinedPlot(); 
					//m_p_UshapeDlg.setVisible(false);
					chbxushape.setSelected(false);
				}
				else
				{
					m_bUshapeDlg = true;
					UWave_Graph  m_p_UshapeDlg = new UWave_Graph(EIT_Control_Dlg); 
					//m_p_UshapeDlg.setVisible(true);
					chbxushape.setSelected(true);
				}
			
			}
		});
		chbxushape.setBounds(182, 245, 117, 23);
		panel_2.add(chbxushape);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Step 2", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(25, 299, 337, 118);
		contentPane.add(panel);
		
		JLabel label_1 = new JLabel("Script File:");
		label_1.setBounds(10, 30, 67, 14);
		panel.add(label_1);
		

		m_comboScriptFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//script_File_txt.setText(DirectoryPath+"/"+m_comboScriptFile.getSelectedItem());
				//proj_File_txt.setText(ProjectionFilePath);
				try {
					if (m_comboScriptFile.getSelectedIndex()!=0 )
					{
						EIT_Control.LoadScriptFile(DirectoryPath+"/ScriptFile/"+m_comboScriptFile.getSelectedItem(), ProjectionFilePath);
						viewCheckBox.setSelected(false);
						EIT_Control.LoadCalibrationFile(EIT_Control.m_bDCOffsetCal,EIT_Control.m_bOutputImpedanceCal,EIT_Control.m_bAmplitudeCal,EIT_Control.m_bVoltmeterCal);
						EIT_Control.MakeCCSTable();
						EIT_Control.MakeProjectionTable();
					}	
					
				} catch (HeadlessException | NumberFormatException
						| IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			
			}
		});
		m_comboScriptFile.setBounds(91, 22, 226, 31);
		panel.add(m_comboScriptFile);
		
	    viewCheckBox = new JCheckBox("View File");
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
						//	System.out.println(Path);
							SetString(Path);
							f = new Files_View();
							f.setVisible(true);
						}
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				}
				
			
	    	}
	    });
		viewCheckBox.setBounds(6, 70, 79, 23);
		panel.add(viewCheckBox);
		
		JButton other_script_btn = new JButton("Other");
		other_script_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);
				 int returnVal = fc.showOpenDialog(Engineering_Control.this);
				 
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                file = fc.getSelectedFile();
		                //This is where a real application would open the file.
		              
		            
		                try {
							EIT_Control.LoadScriptFile(file.getAbsolutePath(), file.getParent()+"/");
							viewCheckBox.setSelected(false);
						} catch (HeadlessException | NumberFormatException
								| IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							System.err.format("IOException: %s%n", e1);
						}
						
		                
		            } else {
		                path_txt.setText("Open command cancelled by user.: .\n" );
		            }
		      
		 //
		        //Handle save button action.
		        
			}
		});
		other_script_btn.setBounds(91, 66, 226, 31);
		panel.add(other_script_btn);
		
		panel_com = panel_1.getComponents();
		panel_1_com = panel_2.getComponents();
		panel_2_com = panel.getComponents();
	
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 745, 21);
		contentPane.add(menuBar);
		
		JMenu mnCalibration = new JMenu("Calibration");
		menuBar.add(mnCalibration);
		
		JMenuItem mntmDcOffSet = new JMenuItem("DC Off Set");
		mntmDcOffSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		
		JMenuItem mntmVoltmeter = new JMenuItem("Voltmeter");
		mntmVoltmeter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Volt_Cal.setVisible(true);
			}
		});
		mnCalibration.add(mntmVoltmeter);
		
		JMenuItem mntmAmplitude = new JMenuItem("Amplitude");
		mntmAmplitude.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Amp_Cal.setVisible(true);
			}
		});
		mnCalibration.add(mntmAmplitude);
		
		JMenu menu_1 = new JMenu("View");
		menuBar.add(menu_1);
		
		JMenuItem mntmUser = new JMenuItem("User");
		menu_1.add(mntmUser);
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
		//scan_num_txt.updateUI();   //check

		
	}
	
	public String SetString(String pathFile)
	{
		Control.str = pathFile;
		// System.out.println("File Path 1 "+ Control.str);
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
}
