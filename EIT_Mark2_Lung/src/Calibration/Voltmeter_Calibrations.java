package Calibration;
import java.awt.EventQueue;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import MainFunctions.Control;
import MainFunctions.Definition;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class Voltmeter_Calibrations extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField Script_txt;
	private JTextField Simulation_txt;
	private JComboBox<String> script_comb = new JComboBox<String>();
	private JComboBox<String> Simu_comb = new JComboBox<String>();
	public String DirectoryPath = System.getProperty("user.dir");
	public String[] SimulationFilefiles = fileNames(DirectoryPath+"/Simulation");
	public String[] files= fileNames(DirectoryPath+"/ScriptFile");
	FileWriter[] fout = new FileWriter[Definition.TOTAL_CH];
	String m_strSimulationFileTitle="";
	String m_strScriptFilePath;
	String m_strSimulationFileName;
	String m_strSimulationFilePath;
	String m_strDefaultFilePath =  System.getProperty("user.dir");
	String m_strCalibration = "Calibration";
	String m_strCategory = "Voltmeter";
	String m_strSimulation = "Simulation";
	String m_strLog = "Log"; 
	String m_strUSBID  = "eit1";

	private static Control EIT_Control_Dlg ;
	Voltmeter_Calibration_fun Volt_Cal_fun = new Voltmeter_Calibration_fun(this);


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Voltmeter_Calibrations frame = new Voltmeter_Calibrations(getEIT_Control_Dlg());
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
	public Voltmeter_Calibrations(Control control) {
		Voltmeter_Calibrations.setEIT_Control_Dlg(control);
		setBounds(100, 100, 651, 311);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "Load Script File", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(23, 22, 597, 109);
		contentPane.add(panel);
		
		JButton scrp_Other_btn = new JButton("Other");
		scrp_Other_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DirectoryPath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);
				 int returnVal = fc.showOpenDialog(contentPane);
				 
		            if (returnVal == JFileChooser.APPROVE_OPTION)
		            {
		               File file = fc.getSelectedFile();		
		               Script_txt.setText(file.getAbsolutePath());
		               try {
						if(getEIT_Control_Dlg().EIT_Control.LoadScriptFile(file.getAbsolutePath(), file.getParent()+"/")==0)
							{
								return;
							}
						   	else
						   	{
						   		getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,1,1,0);
						   		getEIT_Control_Dlg().EIT_Control.MakeCCSTable();
						   		getEIT_Control_Dlg().EIT_Control.MakeProjectionTable();

						   		/*EIT_Control_Dlg.EIT_Control.m_pScriptStatusDlg->ScriptUpdate();
						   		EIT_Control_Dlg.EIT_Control.m_pScriptStatusDlg->ProjectionUpdate();*/
						   		m_strScriptFilePath = getEIT_Control_Dlg().EIT_Control.m_strScriptFilePath;
						   	}
					} catch (HeadlessException | NumberFormatException
							| IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		            } else 
		            {
		            	Script_txt.setText("Open command cancelled by user.: .\n" );
		            }
		            
		            
			}
		});
		scrp_Other_btn.setBounds(220, 23, 200, 31);
		panel.add(scrp_Other_btn);
		
		script_comb = new JComboBox<String>();
		script_comb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(script_comb.getSelectedIndex() > 0)
				{
					
					try {
						String FilePath, FileName;
						FilePath = getEIT_Control_Dlg().EIT_Control.m_strDefaultFilePath + "//ScriptFile//";
						FileName = script_comb.getSelectedItem().toString();
						m_strScriptFilePath = FilePath + FileName;
						getEIT_Control_Dlg().EIT_Control.m_strScriptFileName =script_comb.getSelectedItem().toString();
						getEIT_Control_Dlg().EIT_Control.LoadScriptFile(m_strScriptFilePath,FilePath);
						getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,1,1,0);
						getEIT_Control_Dlg().EIT_Control.MakeCCSTable();
						getEIT_Control_Dlg().EIT_Control.MakeProjectionTable();
					} catch (HeadlessException | NumberFormatException
							| IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				else
				{
					m_strScriptFilePath = "";
				}

			}
		});
		script_comb.setBounds(10, 23, 200, 31);
		script_comb.addItem("-------------------------------------------");
		for (int i =0 ;i< files.length;i++)
		{
			script_comb.addItem(files[i]);
		}
		panel.add(script_comb);
		
		Script_txt = new JTextField();
		Script_txt.setColumns(10);
		Script_txt.setBounds(10, 62, 410, 31);
		panel.add(Script_txt);
		
		JButton Exec_btn = new JButton("Execute");
		Exec_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try
				{
					getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,1,1,0);
					if(!Volt_Cal_fun.VoltmeterCalibrationSystemSetting())
					{
						return;
					}
	
					double[] MagnitudeFactor = new double[Volt_Cal_fun.m_nSimulationDataIndex];
					double[] PhaseFactor = new double[Volt_Cal_fun.m_nSimulationDataIndex];
	
					String FilePath, strScriptFileTitle, strSimulationFileTitle;
					int n;
					strScriptFileTitle = getEIT_Control_Dlg().EIT_Control.m_strScriptFileName;
					strSimulationFileTitle = m_strSimulationFileName;
					if (strScriptFileTitle!= null)
					{
						n = strScriptFileTitle.lastIndexOf(".txt");
						if(n >= 0)
						{
							strScriptFileTitle = strScriptFileTitle. substring(0,n);
						}
						
					}
					
					if (strSimulationFileTitle!= null)
					{
						n = strSimulationFileTitle.lastIndexOf(".txt");
						if(n >= 0)
						{
							strSimulationFileTitle = strSimulationFileTitle.substring(0,n);
						}
					}
					
					
					m_strSimulationFileTitle = strSimulationFileTitle;
					FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
					new File(FilePath).mkdirs();
					m_strUSBID ="eit1";
					FilePath = FilePath + "//" + m_strUSBID;
					new File(FilePath).mkdirs();
					FilePath = FilePath + "//" + m_strCategory;
					new File(FilePath).mkdirs();
					FilePath = FilePath + "//" + m_strSimulationFileTitle;
					new File(FilePath).mkdirs();
					
						Object[] Volt_Cal_Data =Volt_Cal_fun.VoltmeterCalibration(getEIT_Control_Dlg().EIT_Control.m_nTotalCh,getEIT_Control_Dlg().EIT_Control.m_nTotalProjection,Volt_Cal_fun.m_dSimulationData,Volt_Cal_fun.m_nSimulationDataIndex,MagnitudeFactor,PhaseFactor);
	
						if(Volt_Cal_Data==null)						{
			            	JOptionPane.showMessageDialog(null, "Voltmeter Calibration Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						else //done
						{
							MagnitudeFactor =(double[]) Volt_Cal_Data[0];
							PhaseFactor = (double[]) Volt_Cal_Data[1];
							Volt_Cal_fun.VoltmeterCalWriteFile(FilePath,strScriptFileTitle,strSimulationFileTitle,Volt_Cal_fun.m_nSimulationDataIndex,MagnitudeFactor,PhaseFactor);
			            	JOptionPane.showMessageDialog(null, "Finish", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);
						}
				}
				 catch (HeadlessException | NumberFormatException exp) {
						// TODO Auto-generated catch block
						exp.printStackTrace();
					} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				

				
			}
		});
		Exec_btn.setBounds(439, 23, 148, 73);
		panel.add(Exec_btn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Load Simulation File", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(23, 150, 597, 109);
		contentPane.add(panel_1);
		
		JButton SimOther_btn = new JButton("Other");
		SimOther_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DirectoryPath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);
				 int returnVal = fc.showOpenDialog(contentPane);
				 
		            if (returnVal == JFileChooser.APPROVE_OPTION)
		            {
		               File file = fc.getSelectedFile();		
		               Simulation_txt.setText(file.getAbsolutePath());
		               m_strSimulationFilePath = file.getAbsolutePath();
		               m_strSimulationFileName = file.getName();
		               if(!Volt_Cal_fun.LoadSimulationData(m_strSimulationFilePath))
		       			{
		            	   JOptionPane.showMessageDialog(null, "Load Simulation Data Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		            	   return;
		       			}
		       		return;
		                
		            }
		            else 
		            {
		            	Simulation_txt.setText("Open command cancelled by user.: .\n" );
		            }
			}
			
		});
		SimOther_btn.setBounds(220, 23, 200, 31);
		panel_1.add(SimOther_btn);
		
		Simu_comb = new JComboBox<String>();
		Simu_comb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Simu_comb.getSelectedIndex() > 0)
				{
					
					try {
						String FilePath, FileName;
						FilePath = getEIT_Control_Dlg().EIT_Control.m_strDefaultFilePath + "//"+m_strSimulation;
						FileName = Simu_comb.getSelectedItem().toString();
						m_strSimulationFileName = Simu_comb.getSelectedItem().toString();
						if(!Volt_Cal_fun.LoadSimulationData(FilePath + "//" + FileName))
						{
							return;
						}
						m_strSimulationFilePath = FilePath + "//" + FileName;
					} catch (HeadlessException | NumberFormatException exp) {
						// TODO Auto-generated catch block
						exp.printStackTrace();
					}
					
				}
				/*else
				{
					m_strScriptFilePath = "";
				}
*/
			}
		});
		Simu_comb.setBounds(10, 23, 200, 31);
		Simu_comb.addItem("-------------------------------------------");
		for (int i =0 ;i< SimulationFilefiles.length;i++)
		{
			Simu_comb.addItem(SimulationFilefiles[i]);
		}
		panel_1.add(Simu_comb);
		
		Simulation_txt = new JTextField();
		Simulation_txt.setColumns(10);
		Simulation_txt.setBounds(10, 65, 410, 31);
		panel_1.add(Simulation_txt);
		
		JButton Exc_pip_btn = new JButton("Execute Pipline");
		Exc_pip_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try
				{
					getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,1,1,0);
					double[] MagnitudeFactor = new double[Volt_Cal_fun.m_nSimulationDataIndex];
					double[] PhaseFactor = new double[Volt_Cal_fun.m_nSimulationDataIndex];

					String FilePath, strScriptFileTitle, strSimulationFileTitle;
					int n;
					strScriptFileTitle =getEIT_Control_Dlg().EIT_Control.m_strScriptFileName;
					strSimulationFileTitle = m_strSimulationFileName;

					n = strScriptFileTitle.indexOf(".txt");
					if(n >= 0)
					{
						strScriptFileTitle = strScriptFileTitle.substring(0,n);
					}
					n = strSimulationFileTitle.indexOf(".txt");
					if(n >= 0)
					{
						strSimulationFileTitle = strSimulationFileTitle.substring(0,n);
					}
					System.out.println("btn "+strSimulationFileTitle);
					m_strSimulationFileTitle = strSimulationFileTitle;
					FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
					new File(FilePath).mkdirs();
					m_strUSBID ="eit1";
					FilePath = FilePath + "//" + m_strUSBID;
					new File(FilePath).mkdirs();
					FilePath = FilePath + "//" + m_strCategory;
					new File(FilePath).mkdirs();
					FilePath = FilePath + "//" + m_strSimulationFileTitle;
					new File(FilePath).mkdirs();

					Object[] Volt_Cal_Sim_Data = Volt_Cal_fun.VoltmeterCalibrationPipeline(Volt_Cal_fun.m_dSimulationData,Volt_Cal_fun.m_nSimulationDataIndex,MagnitudeFactor,PhaseFactor,strSimulationFileTitle);
					if(Volt_Cal_Sim_Data == null)
					{
						JOptionPane.showMessageDialog(null, "Voltmeter Calibration Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					else
					{
						MagnitudeFactor=(double[]) Volt_Cal_Sim_Data[0];
						PhaseFactor=(double[]) Volt_Cal_Sim_Data[1];
						Volt_Cal_fun.VoltmeterCalWriteFile(FilePath,strScriptFileTitle,strSimulationFileTitle,Volt_Cal_fun.m_nSimulationDataIndex,MagnitudeFactor,PhaseFactor);

					}
					JOptionPane.showMessageDialog(null, "Finish", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);

				}
				catch (HeadlessException | NumberFormatException exp) {
					// TODO Auto-generated catch block
					exp.printStackTrace();
				} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		Exc_pip_btn.setBounds(439, 23, 148, 73);
		panel_1.add(Exc_pip_btn);
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

	public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}
}
