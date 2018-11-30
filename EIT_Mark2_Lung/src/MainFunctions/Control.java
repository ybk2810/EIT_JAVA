package MainFunctions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.GridBagLayout;

import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Calibration.Amplitude_Calibration;
import Calibration.DCoffSet_Calibration;
import Calibration.Output_Impedance_Calibration;
import Calibration.Voltmeter_Calibrations;
import USBCommuniction.SiUSBXp;
import USBCommuniction.USB_Functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.Toolkit;

public class Control {
	static String str;
	private JFrame frame_1;
	private JPanel panel_1;
	private JPanel panel_2;
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
	private JTextField event_trig_txt;
	private JTextField num_scan_txt;
	private JTextField scan_num_txt;
	private JTextField path_txt;
	private JTextField USBConnection_txt;
	private  JCheckBox chckbxImag;
	private  JCheckBox viewCheckBox;
	private JCheckBox chckbxGraph;
	private JCheckBox chbxushape; 
	private JCheckBox chckbxReciprocityError;
	private final JComboBox<String> m_comboScriptFile = new JComboBox<String>();

	
	USB_Functions Usb = new USB_Functions();
	public EIT_Mark2Doc EIT_Control = new EIT_Mark2Doc(this);  
	public EIT_Image m_pImageDlg = new EIT_Image (this);
	public EIT_Graph m_p_Imp_GraphDlg = new EIT_Graph(this); 
	DCoffSet_Calibration DCoffSet_Cal = new DCoffSet_Calibration(this);
	Output_Impedance_Calibration Output_Cal = new Output_Impedance_Calibration(this);
	Amplitude_Calibration Amp_Cal = new Amplitude_Calibration(this);
	Voltmeter_Calibrations Volt_Cal = new Voltmeter_Calibrations(this);
	UWave_Graph  m_p_UshapeDlg = new UWave_Graph(this); 
	ContImp_Graph m_p_ContImpDlg = new ContImp_Graph(this);

	
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
	Component[] panel_1_com;
	Component[] panel_2_comm;
	Component[] panel_2_scr;
	Component[] panel_2_mod;
	Component[] panel_2_calib;

	
	String[] calib = {"..........................","DC Off Set","Output Impedance  ","Voltmeter","Amplitude"}; 
	
	
	// Image Part 
	boolean m_bImageDlg1= false;
	boolean m_bGraphDlg1= false;
	boolean m_bRecpDlg1= false;
	boolean m_bUshapeDlg= false;
	String scriptName = "Script_6.10kHz_fast_res_phantom_fixed.txt";   // by default 
	private JPanel panel_2_Calib;
	private JPanel panel_2_Comm;
	private JRadioButton resis_ph_rbtn;
	
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Control().createAndShowUI();
            }
        });
    }

    private void createAndShowUI() {
        frame_1 = new JFrame("EIT Mark 2");
        frame_1.setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));
        frame_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        frame_1.getContentPane().setLayout(new GridLayout(1, 2, 10, 10));
          //frame_1.setResizable(false);
        frame_1.pack();
        frame_1.setVisible(true);
    }

    private void initComponents() {
    	frame_1.setLocation(10, 10);
    	 panel_1 = new JPanel();
         panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "User", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
         GridBagLayout gbl_panel_1 = new GridBagLayout();
         gbl_panel_1.columnWidths = new int[]{2,121, 100, 100,30};
         gbl_panel_1.rowHeights = new int[] {30, 30, 31, 30, 30, 30, 30, 35, 31, 30, 50, 50};
         gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, 10};
         gbl_panel_1.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10};
         panel_1.setLayout(gbl_panel_1);
         
         panel_2 = new JPanel();
         
         frame_1.getContentPane().add(panel_1);
         
         JPanel panel_1_abs = new JPanel();
         panel_1_abs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Control", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
         panel_1_abs.setLayout(null);
         GridBagConstraints gbc_panel_1_abs = new GridBagConstraints();
         gbc_panel_1_abs.gridheight = 12;
         gbc_panel_1_abs.gridwidth = 5;
         gbc_panel_1_abs.insets = new Insets(0, 0, 5, 5);
         gbc_panel_1_abs.fill = GridBagConstraints.BOTH;
         gbc_panel_1_abs.gridx = 0;
         gbc_panel_1_abs.gridy = 0;
         panel_1.add(panel_1_abs, gbc_panel_1_abs);
         
         JButton stop_btn = new JButton("Stop");
         stop_btn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
				stopScan();
         	}
         });
         stop_btn.setBounds(21, 361, 316, 30);
         panel_1_abs.add(stop_btn);
         
         JButton scan_btn = new JButton("Scan");
         scan_btn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		
				/////////////////// directly before scan load every thing /////////////////
				try {
					EIT_Control.LoadScriptFile(DirectoryPath+"/ScriptFile/"+scriptName, ProjectionFilePath);
				} catch (HeadlessException | NumberFormatException
						| IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				EIT_Control.LoadCalibrationFile(EIT_Control.m_bDCOffsetCal,EIT_Control.m_bOutputImpedanceCal,EIT_Control.m_bAmplitudeCal,EIT_Control.m_bVoltmeterCal);
				EIT_Control.MakeCCSTable();
				EIT_Control.MakeProjectionTable();
				////////////////////////////////////////
         		
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
					chckbxGraph.setEnabled(true);
					chbxushape.setEnabled(true);
					chckbxReciprocityError.setEnabled(true);
					stop_btn.setEnabled(true);
				
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
         scan_btn.setBounds(21, 320, 316, 30);
         panel_1_abs.add(scan_btn);
         
         chbxushape = new JCheckBox("U Shape");
         chbxushape.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
				if (m_bUshapeDlg)
				{
					m_bUshapeDlg = false ;
					m_p_UshapeDlg.setVisible(false);
					chbxushape.setSelected(false);

				}
				else
				{
					m_bUshapeDlg = true;
					m_p_UshapeDlg.setVisible(true);
					chbxushape.setSelected(true);
				}
			
         	}
         });
         chbxushape.setBounds(21, 232, 116, 23);
         panel_1_abs.add(chbxushape);
         
         chckbxImag = new JCheckBox("Image ");
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
         chckbxImag.setBounds(21, 202, 116, 23);
         panel_1_abs.add(chckbxImag);
         
         chckbxGraph = new JCheckBox("Contact Impedance");
         chckbxGraph.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
				if(m_bGraphDlg1 == true)
				{
					m_bGraphDlg1 = false;
					m_p_ContImpDlg.setVisible(false);
					chckbxGraph.setSelected(false);
				}
				else
				{
					m_bGraphDlg1 = true;
					m_p_ContImpDlg.setVisible(true);
					chckbxGraph.setSelected(true);
				}
			
         	}
         });
         chckbxGraph.setBounds(174, 202, 150, 23);
         panel_1_abs.add(chckbxGraph);
         
         scan_num_txt = new JTextField();
         scan_num_txt.setText("0");
         scan_num_txt.setColumns(10);
         scan_num_txt.setBounds(174, 162, 163, 23);
         panel_1_abs.add(scan_num_txt);
         
         path_txt = new JTextField();
         path_txt.setColumns(10);
         path_txt.setBounds(21, 120, 316, 23);
         panel_1_abs.add(path_txt);
         
         JLabel label = new JLabel("Scan Number");
         label.setBounds(21, 167, 111, 15);
         panel_1_abs.add(label);
         
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
         chckbxSaveData.setBounds(21, 77, 116, 23);
         panel_1_abs.add(chckbxSaveData);
         
         JButton path_btn = new JButton("File Path");
         path_btn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {

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
         path_btn.setBounds(174, 77, 163, 30);
         panel_1_abs.add(path_btn);
         
         USBConnection_txt = new JTextField();
         USBConnection_txt.setText("Check connection");
         USBConnection_txt.setColumns(10);
         USBConnection_txt.setBounds(21, 27, 116, 30);
         panel_1_abs.add(USBConnection_txt);
         
         JButton chk_btn = new JButton("Connection");
         chk_btn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
					EIT_Control.USB.FillDeviceList();
					m_nDeviceNum = EIT_Control.USB.m_nNumofDevice;
					EIT_Control.USB.DeviceSelect(m_nDeviceNum-1);
					m_nUSBConnection = m_nDeviceNum;
				/*
					if (m_nDeviceNum >= 1)
					{
						USBConnection_txt.setText("Connected");
					
					}
					else {
						USBConnection_txt.setText("Disconnected");
					}
				
					
					//port open
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
	         	
					// close port
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
				
	         	*/
					

					if (m_nDeviceNum >= 1)
					{
						if(m_nPortStatus != 1)
						{
							if(EIT_Control.USB.USBPortOpen(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
							{
								USBConnection_txt.setText("Disconnected");
								m_nPortStatus = -1;
							}
							else
							{
								m_nPortStatus = 1;
							}
							if(m_nPortStatus == 0)
							{
								USBConnection_txt.setText("Disconnected");
							}
							else if(m_nPortStatus == 1)
							{
								USBConnection_txt.setText("Connected");

							}
							else
							{
								USBConnection_txt.setText("Disconnected");
							}
						}
						else if(m_nPortStatus != 0)
						{
							if(EIT_Control.USB.USBPortClose(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
							{
								USBConnection_txt.setText("Disconnected");
								m_nPortStatus = -1;
							}
							else
							{
								m_nPortStatus = 0;
							}
							

							if(m_nPortStatus == 0)
							{
								USBConnection_txt.setText("Disconnected");
							}
							else if(m_nPortStatus == 1)
							{
								USBConnection_txt.setText("Connected");
							}
							else
							{
								USBConnection_txt.setText("Disconnected");
							}
						
						}
						
						//USBConnection_txt.setText("Connected");
					
					}
					else {
						/*if(m_nPortStatus == 0) 
						{
							open_txt.setText("Port Already Close");
						}*/
						USBConnection_txt.setText("Disconnected");
					}
				
					
					//port open
				
	         	
					// close port
					
				
	         	
				
         	}
         });
         chk_btn.setBounds(174, 26, 163, 30);
         panel_1_abs.add(chk_btn);
         
         JCheckBox chckbxNewCheckBox = new JCheckBox("Engineering");
         chckbxNewCheckBox.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		if(chckbxNewCheckBox.isSelected())
         		{
         			panel_2.revalidate();
         			panel_2.repaint();
         	 		frame_1.getContentPane().add(panel_2); // extend part
                    frame_1.pack();
         		}
         		else
         		{
         			frame_1.getContentPane().remove(panel_2);
         	 		frame_1.repaint();
                    frame_1.pack();
         			
         		}
         	}
         });
         chckbxNewCheckBox.setBounds(21, 287, 97, 23);
         panel_1_abs.add(chckbxNewCheckBox);
         
         JRadioButton Human_rbtn = new JRadioButton("Human");
         Human_rbtn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
				if (Human_rbtn.isSelected())
				{
					resis_ph_rbtn.setSelected(false);
					scriptName = "Script_6.10kHz_fast_human_fixed.txt";
				}
				Human_rbtn.setSelected(true);
				
			}
         });
         Human_rbtn.setBounds(21, 262, 121, 23);
         panel_1_abs.add(Human_rbtn);
         
         resis_ph_rbtn = new JRadioButton("Phantom");
         resis_ph_rbtn.setSelected(true);
         resis_ph_rbtn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		if (resis_ph_rbtn.isSelected())
				{
         			Human_rbtn.setSelected(false);
         			scriptName = "Script_6.10kHz_fast_res_phantom_fixed.txt";
				}
         		resis_ph_rbtn.setSelected(true);
				
				
         	}
         });
         resis_ph_rbtn.setBounds(174, 262, 121, 23);
         panel_1_abs.add(resis_ph_rbtn);
         
         chckbxReciprocityError = new JCheckBox("Reciprocity Error");
         chckbxReciprocityError.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		if(m_bRecpDlg1 == true)
				{
         			m_bRecpDlg1 = false;
					m_p_Imp_GraphDlg.setVisible(false);
					chckbxReciprocityError.setSelected(false);
				}
				else
				{
					m_bRecpDlg1 = true;
					m_p_Imp_GraphDlg.setVisible(true);
					chckbxReciprocityError.setSelected(true);
				}
         	}
         });
         chckbxReciprocityError.setBounds(174, 232, 150, 23);
         panel_1_abs.add(chckbxReciprocityError);
         panel_2.setLayout(new GridLayout(0, 1, 0, 0));
         
         JPanel panel_2_abs = new JPanel();
         panel_2_abs.setLayout(null);
         panel_2_abs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Engineering", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
         panel_2.add(panel_2_abs);
         
         JPanel panel_2_Scr = new JPanel();
         panel_2_Scr.setBorder(new TitledBorder(null, "Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
         panel_2_Scr.setBounds(10, 167, 330, 92);
         panel_2_abs.add(panel_2_Scr);
         panel_2_Scr.setLayout(null);
         
         m_comboScriptFile.addItem("----------------------------------------------");
 		for (int i =0 ;i< files.length;i++)
 		{
 			m_comboScriptFile.addItem(files[i]);
 		}
         m_comboScriptFile.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
				//script_File_txt.setText(DirectoryPath+"/"+m_comboScriptFile.getSelectedItem());
				//proj_File_txt.setText(ProjectionFilePath);
				/*try {
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
				}*/
         		if (m_comboScriptFile.getSelectedIndex()!=0 )
         			scriptName = (String) m_comboScriptFile.getSelectedItem();
         	}
         });
         m_comboScriptFile.setBounds(87, 13, 233, 31);
         panel_2_Scr.add(m_comboScriptFile);
         
         JLabel label_1 = new JLabel("Script File:");
         label_1.setBounds(10, 19, 67, 14);
         panel_2_Scr.add(label_1);
         
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
         viewCheckBox.setBounds(6, 57, 79, 23);
         panel_2_Scr.add(viewCheckBox);
         
         JButton other_script_btn = new JButton("Other");
         other_script_btn.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {


				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);
				 int returnVal = fc.showOpenDialog(frame_1);
				 
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
         other_script_btn.setBounds(91, 53, 229, 31);
         panel_2_Scr.add(other_script_btn);
         
         panel_2_Comm = new JPanel();
         panel_2_Comm.setBorder(new TitledBorder(null, "Command", TitledBorder.LEADING, TitledBorder.TOP, null, null));
         panel_2_Comm.setBounds(10, 21, 330, 135);
         panel_2_abs.add(panel_2_Comm);
         panel_2_Comm.setLayout(null);
         
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
         send_btn.setBounds(15, 84, 295, 40);
         panel_2_Comm.add(send_btn);
         
         data6_txt = new JTextField();
         data6_txt.setBounds(15, 52, 51, 20);
         panel_2_Comm.add(data6_txt);
         data6_txt.setText("0");
         data6_txt.setColumns(10);
         
         data7_txt = new JTextField();
         data7_txt.setBounds(76, 52, 51, 20);
         panel_2_Comm.add(data7_txt);
         data7_txt.setText("0");
         data7_txt.setColumns(10);
         
         data8_txt = new JTextField();
         data8_txt.setBounds(137, 52, 51, 20);
         panel_2_Comm.add(data8_txt);
         data8_txt.setText("0");
         data8_txt.setColumns(10);
         
         data9_txt = new JTextField();
         data9_txt.setBounds(198, 52, 51, 20);
         panel_2_Comm.add(data9_txt);
         data9_txt.setText("0");
         data9_txt.setColumns(10);
         
         data10_txt = new JTextField();
         data10_txt.setBounds(259, 52, 51, 20);
         panel_2_Comm.add(data10_txt);
         data10_txt.setText("0");
         data10_txt.setColumns(10);
         
         data1_txt = new JTextField();
         data1_txt.setBounds(15, 21, 51, 20);
         panel_2_Comm.add(data1_txt);
         data1_txt.setText("0");
         data1_txt.setColumns(10);
         
         data2_txt = new JTextField();
         data2_txt.setBounds(76, 21, 51, 20);
         panel_2_Comm.add(data2_txt);
         data2_txt.setText("0");
         data2_txt.setColumns(10);
         
         data3_txt = new JTextField();
         data3_txt.setBounds(137, 21, 51, 20);
         panel_2_Comm.add(data3_txt);
         data3_txt.setText("0");
         data3_txt.setColumns(10);
         
         data4_txt = new JTextField();
         data4_txt.setBounds(198, 21, 51, 20);
         panel_2_Comm.add(data4_txt);
         data4_txt.setText("0");
         data4_txt.setColumns(10);
         
         data5_txt = new JTextField();
         data5_txt.setBounds(259, 21, 51, 20);
         panel_2_Comm.add(data5_txt);
         data5_txt.setText("0");
         data5_txt.setColumns(10);
         
         JPanel panel_2_Mode = new JPanel();
         panel_2_Mode.setBorder(new TitledBorder(null, "Mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));
         panel_2_Mode.setBounds(10, 270, 330, 82);
         panel_2_abs.add(panel_2_Mode);
         panel_2_Mode.setLayout(null);
         
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
         chckbxNumberOfScans.setBounds(6, 17, 153, 23);
         panel_2_Mode.add(chckbxNumberOfScans);
         
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
         chckbxEventTrigger.setBounds(6, 48, 128, 23);
         panel_2_Mode.add(chckbxEventTrigger);
         
         event_trig_txt = new JTextField();
         event_trig_txt.setText("0");
         event_trig_txt.setEnabled(false);
         event_trig_txt.setColumns(10);
         event_trig_txt.setBounds(178, 47, 142, 25);
         panel_2_Mode.add(event_trig_txt);
         
         num_scan_txt = new JTextField();
         num_scan_txt.setText("0");
         num_scan_txt.setEnabled(false);
         num_scan_txt.setColumns(10);
         num_scan_txt.setBounds(178, 16, 142, 25);
         panel_2_Mode.add(num_scan_txt);
         
         panel_2_Calib = new JPanel();
         panel_2_Calib.setLayout(null);
         panel_2_Calib.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Calibration", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
         panel_2_Calib.setBounds(10, 363, 330, 57);
         panel_2_abs.add(panel_2_Calib);
         
         JComboBox<String> comboBox_1 = new JComboBox<String>();
         comboBox_1.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		if(comboBox_1.getSelectedIndex()==1)
    				DCoffSet_Cal.setVisible(true);
         		else if(comboBox_1.getSelectedIndex()==2)
    				Output_Cal.setVisible(true);
             	else if(comboBox_1.getSelectedIndex()==3)
        			Volt_Cal.setVisible(true);
               	else if(comboBox_1.getSelectedIndex()==4)
            		Amp_Cal.setVisible(true);

         	}
         });
         comboBox_1.setBounds(109, 15, 211, 31);
         for(int i =0 ;i< 4;i++)
        	 comboBox_1.addItem(calib[i]);
         panel_2_Calib.add(comboBox_1);
         
         JLabel lblCalibration = new JLabel("Calibration");
         lblCalibration.setBounds(10, 25, 67, 14);
         panel_2_Calib.add(lblCalibration);
         
         
 		panel_1_com = panel_1_abs.getComponents();
 		panel_2_comm = panel_2_Comm.getComponents();
 		panel_2_scr = panel_2_Scr.getComponents();
 		panel_2_mod = panel_2_Mode.getComponents();
 		panel_2_calib = panel_2_Calib.getComponents();
         
	   //frame_1.getContentPane().add(panel_2); // extend part


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
		
			for (int a = 0; a < panel_1_com.length; a++) {
				panel_1_com[a].setEnabled(false);
			}
			for (int a = 0; a < panel_2_comm.length; a++) {
				panel_2_comm[a].setEnabled(false);
			}
			for (int a = 0; a < panel_2_scr.length; a++) {
				panel_2_scr[a].setEnabled(false);
			}
			for (int a = 0; a < panel_2_mod.length; a++) {
				panel_2_mod[a].setEnabled(false);
			}
			for (int a = 0; a < panel_2_calib.length; a++) {
				panel_2_calib[a].setEnabled(false);
			}
	 }
	 void ActiveGUI()
	 {
				for (int a = 0; a < panel_1_com.length; a++) {
					panel_1_com[a].setEnabled(true);
				}
				for (int a = 0; a < panel_2_comm.length; a++) {
					panel_2_comm[a].setEnabled(true);
				}
				for (int a = 0; a < panel_2_scr.length; a++) {
					panel_2_scr[a].setEnabled(true);
				}
				for (int a = 0; a < panel_2_mod.length; a++) {
					panel_2_mod[a].setEnabled(true);
				}
				for (int a = 0; a < panel_2_calib.length; a++) {
					panel_2_calib[a].setEnabled(true);
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