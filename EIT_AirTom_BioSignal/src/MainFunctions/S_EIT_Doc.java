package MainFunctions;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Locale;
import java.util.Scanner;
//import java.util.DoubleSummaryStatistics;

import java.util.TimerTask;

import javax.swing.JOptionPane;

import USBCommuniction.SiUSBXp;
import USBCommuniction.USB_Functions;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.data.time.Millisecond;



public class S_EIT_Doc {
	
	public USB_Functions USB = new USB_Functions();
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;
	public Control control_gui ;


	//////////////Script///////////////////
	String m_strScriptFilePath;
	String m_strProjectionFilePath;
	String m_strScriptFileDirectory;
	String m_strScriptFileTitle;
	String[] m_strScriptProjectionName = new String[100];
	String m_strScriptFileName;
	String m_strProtocolName;
	int m_nScriptLine;
	String m_strCategory;
	String m_strLog;
	String m_strFileName;
	double d_Amp;
	double Temp_d_Amp;
	
	
	String Frequency;
	double Gain;
	
	////////////// Serial Script ////////////////
/////// LG _Script//////////
	int m_nAvg1 = 20,m_nAvg2 = 20,m_nAvg3 = 20;
	String m_strPreFreq1 = "1KHz" , m_strPreFreq2 = "1KHz", m_strPreFreq3 = "10KHz";
	String m_strAmp1 = "1000uA", m_strAmp2 = "1000uA", m_strAmp3 = "1000uA";
	int m_nVM_digit1 =0 , m_nVM_digit2 =0, m_nVM_digit3 =0;
	
	double[] Raw_Data= new double[1536];

	///////////////////////////////////////
	//////////Calibration/////////////////
	int m_bDCOffsetCal;
	int m_bOutputImpedanceCal;
	int m_bAmplitudeCal;
	int m_bVoltmeterCal;
	double[][]Magnitude= new double [3][3000];
	double[][] Phase = new double[3][3000];
	//////////////////////////////////////
	//////////Load Calibration /////////////////
	String[] m_strFreq= new String[Definition.NUM_OF_FREQUENCY];
	String m_strDefaultFilePath =  System.getProperty("user.dir");
	String m_strCalibration = "Calibration";
	String m_strUSBID;
	
	///////////Calibration Data///////////
	public int[][] m_nDCOffsetValue = new int[Definition.Serial_EIT_ALL_CH][Definition.NUM_OF_FREQUENCY];
	public double[][] m_dAmplitudeCalibrationFactor = new double [16][Definition.NUM_OF_FREQUENCY];
	//int m_nOutputImpedanceCalibrationData[MAX_CH][NUM_OF_FREQUENCY][4];
	//double *m_dVoltmeterMagnitudeFactor;
	public double[] m_dVoltmeterMagnitudeFactor;
	public double[] m_dVoltmeterPhaseFactor;
	public int m_nVoltmeterCalibrationIndex;
	public int[][][] CCSCalData = new int[16][Definition.NUM_OF_FREQUENCY][4];
	public int[][] DCCalData = new int[16][Definition.NUM_OF_FREQUENCY];
	public int[][][] CCSTable = new int[16][Definition.NUM_OF_FREQUENCY+6][Definition.CCSTABLEROW];	
	
	//////////////SW Table///////////////
	public int[][][] SWTable = new int[1][16][5];
			
	////////////Projection File////////////
	int ProjectionTableIndex;
	String[] ProjectionName = new String[100];
	double[][][] TempProjectionTable = new double[100][Definition.MAX_CH][13];		 // 13 fabric projection 	
	int[][][]  ProjectionTable = new int[100][Definition.MAX_CH][Definition.PROjECTIONTABLEROW]; // [ProjectionIndex][ChIndex][Data]
	///////////////////////////////////////	
	///////Data///////////////////////////
	//byte[] RawData = new byte[20000];
	public int[] RawData = new int[20000];
	double[][] Real = new  double[3][3000];
	double[][] Quad = new double [3][3000];
	int [][] VMOverflow= new int[3][3000];
	int[][]  ChNum = new int[3][3000];
	
	double[][] Real_source = new  double[3][1536];
	double[][] Quad_source = new double [3][1536];
	int [][] VMOverflow_source= new int[3][1536];
	int[][]  ChNum_source = new int[3][1536];
	
	/*double Magnitude[3][3000];
	double Phase[3][3000];
	BOOL VMOverflow[3][3000];
	*/

	double[][] TempReal= new double[3][1536];
	double[][] TempQuad= new double[3][1536];
	double[][] TempMagnitude= new double[3][1536];
	double[][] TempPhase= new double[3][1536];
	int[][] TempVMOverflow= new int[3][1536];  /////check
	int[][] TempCh = new int[3][1536];
	
	int[][] TempVMOverflow_source= new int[3][1536];  /////check
	int[][] TempCh_source = new int[3][1536];
	double[][] TempReal_source= new double[3][1536];
	double[][] TempQuad_source= new double[3][1536];	
	
	//////////////////////////////////////
	
	
	//////////Algorithm///////////////////
	int numtriangle, numSencol, sqgrid, mulresol, resol;
	int trinum[] = new int [24*24];
	int trinum2[] = new int[24*24*4];
	float Sen[] = new float[258*1024];
	float Sen_d[] = new float[258*1024];
	
	double Image_dsigma[] = new double[1024*2];//1024*2
	
	double[] Image_RefReal= new double[Definition.Fab_All_Data];
	double[] Image_RefQuad = new double [Definition.Fab_All_Data];
	double[] Image_RefMag = new double[Definition.Fab_All_Data];
	double[] Image_RefTheta = new double[Definition.Fab_All_Data];
	
	double[][] DipolX = new double[16][1184];
	double[][] DipolY = new double[16][1184];
	int[]  DipolIndex= new int[1600];
	double[] indicator= new double[1184];
	int AlgorithmFlag;
	double[] FactoraizationIndex = new double[256];
	
	int GREIT_TetrahedralMeshNum;
	int GREIT_DataIndex;
	int[] GREIT_RemoveIndex = new int[96];
	double[][] GREIT_RMatrix = new double[812][928];
	double[] GREIT_Reference = new double[928];
	double[] GREIT_Pertb = new double[928];
	int[] GREIT_Map = new int [48*48];
	
	
	double[] Image_ObjReal = new double[Definition.Fab_All_Data];  // change the size 
	double[] Image_ObjQuad = new double[Definition.Fab_All_Data];
	double[] Image_ObjMag = new double [Definition.Fab_All_Data];
	double[] Image_ObjTheta = new double[Definition.Fab_All_Data];
	double[] Image_ObjVM = new double[Definition.Fab_All_Data];
	
	/*long double alpha1;
	long double alpha2;*/
	double[][] delta_NtD = new double[16][16];
	
	// Fabric
	int Fab_GREIT_TetrahedralMeshNum;
	int Fab_GREIT_DataIndex;
	int[] Fab_GREIT_RemoveIndex = new int[Definition.Fab_NUM_Saturation];
	double[][] Fab_GREIT_RMatrix = new double[4096][Definition.Fab_All_Data_NO_Saturation];
	double[] Fab_GREIT_Reference = new double[Definition.Fab_All_Data_NO_Saturation];
	double[] Fab_GREIT_Pertb = new double[Definition.Fab_All_Data_NO_Saturation];
	////////////////////// SNR ///////////////////
	double[][] snr_rawdata = new double[20][Definition.Fab_All_Data];  // 
	int snr_count = 0;
	double[]  snr  = new double[Definition.Fab_All_Data];  // 
	double[]  tmp_snr_mask  = new double[Definition.Fab_All_Data];  // 
	double[]  snr_mask  = new double[Definition.Fab_All_Data_NO_Saturation];  // 
	boolean snr_enable = false;

	/////////////////Graph////////////////
	double [] Rec_Error = new double[120];
	double Rec_Error_val_temp = 0;
	double Rec_Error_val = 0;
	double[] Zc =new double[16];
	//////////////////////////////////////
	public int m_nTotalCh;
	public int m_nTotalProjection;
	int m_nNumofAVG;
	int m_nSelectDevice;
	int m_nAmp;
	int m_nDelay;
	int m_nNumofDevice;
	int m_nMethod;
	int m_nTotalFreqNum;
	int m_nMode;
	public int m_nTotalDataLength;
	int m_nPresentScanNum;
	int m_nDummyScanNum;
	int m_nTotalReadBlock;
	int m_nTimeInfoHigh;
	int m_nTimeInfoMid;
	int m_nTimeInfoLow;
	int m_nInjDelayHigh;
	int m_nInjDelayLow;
	int m_nMixedFlag;
	int m_nNumofScans;
	int m_nScanInterval;
	int m_nPresentWriteNum;
	public int m_nSEIT_TotalProjection;		// m_nTotalCh * m_nTotalProjection
	
	
	String m_strSaveFileName;
	String m_strSaveFileName_source;
	////////Thread////////////////////////
	Thread m_hThreadComm;
	Thread m_hThreadWriteFile;
	Thread m_hThreadDataProcess;
	Thread m_hThreadCalRecipError;
	int WriteScanNum;
	
	boolean m_bThreadCommWorkingFlag;
	boolean m_bThreadDataProcessWorkingFlag;
	
	boolean m_bThreadDataProcessFinishFlag;

	boolean m_bThreadDataProcessFinishFlag2;
	boolean m_bThreadWriteFileWorkingFlag;
	boolean m_bThreadRecipErrorWorkingFlag;
	boolean m_bThreadWriteFileFinishFlag;
	boolean m_bMessageImageReconFinishFlag1;
	boolean m_bMessageGraphReconFinishFlag;
	boolean m_bMessageGraphContactImpedanceFinishFlag;
	boolean m_bMessageGraphBio_SignalFinishFlag;
	boolean m_bScanIntervalTimerFlag;
	
	public int m_nThreadDataIndex;
	public int m_nThreadRQDataIndex;
	public int m_nReadBlock;
	public int m_nWriteFileIndex;

	int Thread_Freq ;
	int Thread_projection;
	int Thread_Ch;
	///////////////Pipline/////////////////////
	public int m_nOpMode;
	boolean m_bSave;
	//////////////Serial////////////
	int SerialDataIndex = 0;
	
	
	/////////////////////////////////////
	
	public String m_strSaveFilePath;
	FileWriter Mag_fout = null;
	//////////// airTom///////////////
	public double[] header = new double[50];
	public double[] External_trigger = new double[50];
	public double[] Gyro_Z = new double[50];
	public double[] Gyro_Y = new double[50];
	public double[] Gyro_X = new double[50];
	public double[] Tempreture = new double[50];
	public double[] Accelometer_Z = new double[50];
	public double[] Accelometer_Y = new double[50];
	public double[] Accelometer_X = new double[50];
	public double[] temp_External_trigger = new double[50];
	public double[] temp_Gyro_Z = new double[50];
	public double[] temp_Gyro_Y = new double[50];
	public double[] temp_Gyro_X = new double[50];
	public double[] temp_Tempreture = new double[50];
	public double[] temp_Accelometer_Z = new double[50];
	public double[] temp_Accelometer_Y = new double[50];
	public double[] temp_Accelometer_X = new double[50];
	boolean timreflag = true;

	 /*************  Default Constructor *****************/
	public  S_EIT_Doc(Control gui)
	{

		this.control_gui=gui;
		
//		m_nTotalCh = Definition.Serial_EIT_ALL_CH; // YE
//		m_nTotalProjection = Definition.Serial_EIT_ALL_CH;
		m_nTotalProjection = Definition.TOTAL_CH;
		m_nNumofAVG = 1;
		m_nNumofDevice = 0;
		m_nSelectDevice = 0;
		m_nDelay = 0;
		m_nTotalFreqNum = 0;
		m_nMode = Definition.MANUAL_MODE;
		m_bThreadCommWorkingFlag = false;
		m_bThreadDataProcessWorkingFlag = false;
		
		m_bThreadDataProcessFinishFlag = false;
		
		m_bThreadWriteFileWorkingFlag = false;
		m_bThreadRecipErrorWorkingFlag= false;
		m_bThreadWriteFileFinishFlag = false;
		m_bMessageImageReconFinishFlag1 = true; 
		m_bMessageGraphReconFinishFlag = true; 
		m_bMessageGraphContactImpedanceFinishFlag = true;
		m_bMessageGraphBio_SignalFinishFlag =true;
		m_bScanIntervalTimerFlag = true;
		//m_bRecon = FALSE;
		m_nThreadDataIndex = 0;
		m_bSave = false;
		m_nPresentScanNum = 0;
		//m_nTotalDataLength = 1536;
		m_nTotalDataLength = 800;
//		m_nTotalDataLength = 6144;
		//m_nThreadFileoutIndex = 1;
		m_nTimeInfoHigh = 0;
		m_nTimeInfoMid = 3;
		m_nTimeInfoLow = 255;
		m_nMixedFlag = Definition.SINGLE_FREQUENCY;
		m_nNumofScans = 50;
		m_nScanInterval = 0;
		m_nInjDelayHigh = 0;
		m_nInjDelayLow = 0;
		//AlgorithmFlag = 0;
		m_nDummyScanNum = 0;
		//sqgrid=20; 
		mulresol=2;
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
		//m_dVoltmeterMagnitudeFactor = NULL;
		//m_dVoltmeterPhaseFactor = NULL;
		m_nVoltmeterCalibrationIndex = 0;
		m_bDCOffsetCal = FALSE;
		m_bOutputImpedanceCal = FALSE;
		m_bAmplitudeCal = FALSE;
		m_bVoltmeterCal = FALSE;
		GREIT_TetrahedralMeshNum = 812;
		GREIT_DataIndex = 928;
		SerialDataIndex =0;
		
		Fab_GREIT_TetrahedralMeshNum = 4096; 
		Fab_GREIT_DataIndex = 870;
		
		
		
	for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)   // GA
	{
		for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
		{
			m_nDCOffsetValue[Ch][Freq] = 0;
			m_dAmplitudeCalibrationFactor[Ch][Freq] = 1;
		}
	}
	
	LoadGREITAlgorithmFile();
	LoadGREITRemoveData();
	LoadGREITMap();
	
	LoadFabGREITAlgorithmFile();
	LoadFabGREITRemoveData();
	
	
	
 	
	}

	 /************* Load Script File *****************/
	 public int LoadScriptFile(String FilePath, String ProjectionFilePath) throws HeadlessException, NumberFormatException, IOException
		{
			
		  Locale.setDefault(new Locale("ko_KR"));
		  
			char[] contents = new char[150];
			char[] Tempcontents = new char[150];
			
			StringBuilder strTemp = new StringBuilder();
			int CommentIndex = 0;
			int ScriptLevel = Definition.SCRIPTLEVEL1;
			m_nTotalProjection = 0;
			m_nScriptLine = 0;
			
			 Scanner fin = null;
			    try {

			        fin = new Scanner(new File(FilePath));
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();  
			    }
			   
			    while (fin.hasNextLine()) {
			    	
			            Scanner s2 = new Scanner(fin.nextLine());
			    
			        while (s2.hasNext()) {
			            Tempcontents = s2.next().toCharArray();
			          
						CommentIndex = CheckCommnet(Tempcontents);
						if(CommentIndex == Definition.NOCOMMENT)	// No Comment
						{	
							strTemp.delete(0, strTemp.length());
							strTemp.append(Tempcontents);
						}
						else if(CommentIndex == Definition.INCLUDE) //Include
						{
							strTemp.delete(0, strTemp.length());
							strTemp.append(Tempcontents);
							strTemp.deleteCharAt(0);
							///System.out.println("Inside INCLUDE ");
							
							
						}
						else	// Comment
						{
						
							///System.out.println("inside Comment");
							s2.next();
							int i;
							for( i = 0; i < CommentIndex; i++)
							{
								contents[i] = Tempcontents[i];
							}
							contents[i] = 0;
							strTemp.append("");
							///System.out.println("inside Comment script");
							
						}
					
						if(!strTemp.toString().equals("")) 
						{///System.out.println("strTemp "+strTemp.toString());
							m_nScriptLine++;
							
						
							switch(ScriptLevel)
							{	
							case Definition.SCRIPTLEVEL1:
								if(strTemp.toString().toUpperCase().equals("INCLUDE"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL2_INCLUDE;
								///	System.out.println("ScriptLevel "+ScriptLevel);
								}

								else if(strTemp.toString().toUpperCase().equals("START")) 
								{
									ScriptLevel = Definition.SCRIPTLEVEL2_START;
								///	System.out.println("ScriptLevel "+ScriptLevel);
								}
								else
								{
									
									JOptionPane.showMessageDialog(null, "Error Script in Top Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
									fin.close();
									return FALSE;
								}
								
								break;

							case Definition.SCRIPTLEVEL2_START:
								if(strTemp.toString().toUpperCase().equals("SETTING"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								}

								else if(strTemp.toString().toUpperCase().equals("SCAN"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL3_SCAN;
								}
								else if(strTemp.toString().toUpperCase().equals("CALIBRATION"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								}
								else if(strTemp.toString().toUpperCase().equals("END"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL1;
								}
								else
								{
									
									JOptionPane.showMessageDialog(null, "Error Script in START Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
									fin.close();
									return FALSE;
								}
								break;

							case Definition.SCRIPTLEVEL2_INCLUDE:
							  
								int FirstIndex, EndIndex;
								///System.out.println("Path "+strTemp);
								
								FirstIndex = strTemp.indexOf("\"");
								strTemp.deleteCharAt(FirstIndex);
								EndIndex = strTemp.indexOf("\"");
								strTemp.deleteCharAt(EndIndex);
						
								strTemp.replace(strTemp.indexOf("\\"),strTemp.indexOf("\\")+1,"/");
								///System.out.println("ProjectionFilePath  "+(ProjectionFilePath+strTemp.toString()));
								if(LoadFabricProjectionFile(ProjectionFilePath+strTemp.toString())==1)
								{
									ScriptLevel = Definition.SCRIPTLEVEL1;
								}
								else
								{
									
									JOptionPane.showMessageDialog(null, "Error Script in INCLUDE Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
									fin.close();
									return FALSE;
								}
								break;

							case Definition.SCRIPTLEVEL3_SETTING:
								if(strTemp.toString().toUpperCase().equals("CHANNEL"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CHANNEL;
								}
								else if(strTemp.toString().toUpperCase().equals("AVERAGE"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_AVERAGE;
								}
								else if(strTemp.toString().toUpperCase().equals("DELAY"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_DELAY;
								}
								else if(strTemp.toString().toUpperCase().equals( "FREQ") || strTemp.toString().toUpperCase().equals("FREQUENCY"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_FREQUENCY;
								}
								else if(strTemp.toString().toUpperCase().equals("TIMEINFOHIGH"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_TIMEINFOHIGH;
								}
								else if(strTemp.toString().toUpperCase().equals("TIMEINFOMID"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_TIMEINFOMID;
								}
								else if(strTemp.toString().toUpperCase().equals("TIMEINFOLOW"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_TIMEINFOLOW;
								}
								else if(strTemp.toString().toUpperCase().equals("STOP"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL2_START;
								}
								else if(strTemp.toString().toUpperCase().equals("INJDELAYHIGH"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_INJ_DELAYHIGH;
								}
								else if(strTemp.toString().toUpperCase().equals("INJDELAYLOW"))
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_INJ_DELAYLOW;
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Error Script in SETTING Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
									fin.close();
									return FALSE;
								}
								break;

							case Definition.SCRIPTLEVEL3_SCAN:
								if(strTemp.toString().toUpperCase().equals("STOP"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL2_START;
									
								}
								else
								{
									/////// check equal strTemp.toString().toUpperCase().equals(
									m_strScriptProjectionName[m_nTotalProjection] = strTemp.toString();	
									m_nTotalProjection++;
								}
							
								break;

							case Definition.SCRIPTLEVEL3_CALIBRATION:
								if(strTemp.toString().toUpperCase().equals("DCOFFSET"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CAL_DCOFFSET;
								}
								else if(strTemp.toString().toUpperCase().equals("OUTPUTIMPEDANCE"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CAL_OUTPUTIMPEDANCE;
								}
								else if(strTemp.toString().toUpperCase().equals("AMPLITUDE"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CAL_AMPLITUDE;
								}
								else if(strTemp.toString().toUpperCase().equals("VOLTMETER"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CAL_VOLTMETER;
								}
								else if(strTemp.toString().toUpperCase().equals("PROTOCOL"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL4_CAL_PROTOCOLNAME;
								}
								else if(strTemp.toString().toUpperCase().equals("STOP"))	
								{
									ScriptLevel = Definition.SCRIPTLEVEL2_START;
								}
								else
								{
								}

								break;

							case Definition.SCRIPTLEVEL4_CHANNEL:
								m_nTotalCh = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;

							case Definition.SCRIPTLEVEL4_AVERAGE:
								m_nNumofAVG = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;

							case Definition.SCRIPTLEVEL4_DELAY:
								m_nDelay = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;

							case Definition.SCRIPTLEVEL4_FREQUENCY:
								m_nTotalFreqNum = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;
							case Definition.SCRIPTLEVEL4_TIMEINFOHIGH:
								m_nTimeInfoHigh = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;
							case Definition.SCRIPTLEVEL4_TIMEINFOMID:
								m_nTimeInfoMid = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;
							case Definition.SCRIPTLEVEL4_TIMEINFOLOW:
								m_nTimeInfoLow = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;
							case Definition.SCRIPTLEVEL4_INJ_DELAYHIGH:
								m_nInjDelayHigh = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;
							case Definition.SCRIPTLEVEL4_INJ_DELAYLOW:
								m_nInjDelayLow = Integer.parseInt(strTemp.toString());
								ScriptLevel = Definition.SCRIPTLEVEL3_SETTING;
								break;

							case Definition.SCRIPTLEVEL4_CAL_DCOFFSET:
								if(strTemp.toString().toUpperCase().equals("ON"))	
								{
									m_bDCOffsetCal = TRUE;
								}
								else if(strTemp.toString().toUpperCase().equals("OFF"))
								{
									m_bDCOffsetCal = FALSE;
								}
								else
								{
									m_bDCOffsetCal = FALSE;
									JOptionPane.showMessageDialog(null, "Error DCOffset Calibration Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								}
								ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								break;
							case Definition.SCRIPTLEVEL4_CAL_OUTPUTIMPEDANCE:
								if(strTemp.toString().toUpperCase().equals("ON"))
								{
									m_bOutputImpedanceCal = TRUE;
								}
								else if(strTemp.toString().toUpperCase().equals("OFF"))
								{
									m_bOutputImpedanceCal = FALSE;
								}
								else
								{
									m_bOutputImpedanceCal = FALSE;
									JOptionPane.showMessageDialog(null, "Error DCOffset Calibration Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								}
								ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								break;
							case Definition.SCRIPTLEVEL4_CAL_AMPLITUDE:
								if(strTemp.toString().toUpperCase().equals("ON"))	
								{
									m_bAmplitudeCal = TRUE;
								}
								else if(strTemp.toString().toUpperCase().equals("OFF"))
								{
									m_bAmplitudeCal = FALSE;
								}
								else
								{
									m_bAmplitudeCal = FALSE;
									JOptionPane.showMessageDialog(null, "Error DCOffset Calibration Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								}
								ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								break;
							case Definition.SCRIPTLEVEL4_CAL_VOLTMETER:
								if(strTemp.toString().toUpperCase().equals("ON"))	
								{
									m_bVoltmeterCal = TRUE;
								}
								else if(strTemp.toString().toUpperCase().equals("OFF"))
								{
									m_bVoltmeterCal = FALSE;
								}
								else
								{
									m_bVoltmeterCal = FALSE;
									JOptionPane.showMessageDialog(null, "Error DCOffset Calibration Level", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								}
								ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								break;
							case Definition.SCRIPTLEVEL4_CAL_PROTOCOLNAME:
								m_strProtocolName = strTemp.toString();
								ScriptLevel = Definition.SCRIPTLEVEL3_CALIBRATION;
								break;

							}
						}
					
			        }
			        s2.close();
			    }
			    
			  ///  System.out.println("*********************************** ");
			    fin.close();
				
			
			MakeProjectionTable();
			m_nVoltmeterCalibrationIndex = m_nTotalCh * m_nTotalProjection;
			//m_nTotalDataLength = 6;
			//m_nTotalDataLength = m_nTotalCh * m_nTotalProjection * m_nTotalFreqNum * 6;
			m_nSEIT_TotalProjection = m_nTotalCh * m_nTotalProjection;
			//m_nTotalDataLength = 32 * 32  * 6;  //change to 6144
			m_nTotalDataLength = 800; // AirTOm
			m_dVoltmeterMagnitudeFactor = new double[m_nVoltmeterCalibrationIndex];
			m_dVoltmeterPhaseFactor = new double[m_nVoltmeterCalibrationIndex];
			
			
			File f = new File(FilePath);
			m_strScriptFileName = f.getName();
			
			String[] s = m_strScriptFileName.split(".txt", 2);
			m_strScriptFileTitle = s[0];
			//System.out.println(m_strScriptFileTitle);

			return TRUE;
		}

	 /************* Load Projection File *****************/
	 int LoadProjectionFile(String FilePath)
	 {
		 char[] contents = new char[150];
		 
		 char[] Tempcontents = new char[150];
		 StringBuilder strTemp = new StringBuilder();
		 int CommentIndex = 0;
		 int ProjectionLevel =Definition.SCRIPTLEVEL1;
		 int Data_Index = 0;
	 	 int ChIndex = 0;
	 	 int n;
	 	 ProjectionTableIndex = 0;
	 	 // System.out.println(FilePath);
	 	Scanner fin = null;
	    try {

	        fin = new Scanner(new File(FilePath));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
	 
	    while (fin.hasNextLine()) {
	    	
	    	Scanner s2 = new Scanner(fin.nextLine());
	        while (s2.hasNext()) {
	            Tempcontents = s2.next().toCharArray();
	            CommentIndex = CheckCommnet(Tempcontents);
	            //System.out.println( CommentIndex  );
	        	if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 			{	
	        		strTemp.delete(0, strTemp.length());
	 				strTemp.append(Tempcontents) ;
	 			}
	 			else	// Comment
	 			{
	 			    while (s2.hasNext())
	 			    	s2.next();
					int i;
					for( i = 0; i < CommentIndex; i++)
					{
						contents[i] = Tempcontents[i];
					}
					contents[i] = 0;
					strTemp.delete(0, strTemp.length());
					strTemp.append("");
					///System.out.println("finish Comment");
	 			}
	        	if(!strTemp.toString().equals("")) 
	 			{ 
	        		//System.out.println("strTemp "+ strTemp);
	 				switch(ProjectionLevel)
	 				{
	 				case Definition.SCRIPTLEVEL1:
	 					ProjectionName[ProjectionTableIndex] = strTemp.toString();//////////////////check
	 				///	System.out.println("ProjectionName[ProjectionTableIndex] "+ProjectionName[ProjectionTableIndex]);
	 					ProjectionLevel = Definition.SCRIPTLEVEL2;
	 					break;
	 				case Definition.SCRIPTLEVEL2:
	 					if(!strTemp.toString().equals("")) 
	 					{
	 						if(strTemp.toString().toUpperCase().equals("END"))
	 						{
	 							ProjectionTableIndex++;
	 							ChIndex = 0;
	 							Data_Index = 0;
	 							ProjectionLevel = Definition.SCRIPTLEVEL1;
	 						}
	 						else
	 						{
	 							switch(Data_Index)
	 							{
	 							case Definition.PROJECTIONFILE_CH:
	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							
	 							case Definition.PROJECTIONFILE_SIGN:
	 							
	 								 d_Amp = 1000;
	 								
	 								
	 								if(strTemp.toString().toUpperCase().equals("NULL") || strTemp.toString().toUpperCase().equals("VM"))
	 								{
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.VM;
	 									Data_Index++;
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = d_Amp;
	 									Data_Index++;
	 								}
	 								else
	 								{
	 									//Test
	 									n = strTemp.toString().toUpperCase().indexOf("M");	// YE ??
	 									
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									
	 									n = strTemp.toString().toUpperCase().indexOf("U"); //remove U
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									
	 									n = strTemp.toString().toUpperCase().indexOf("A"); //remove A
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									n = strTemp.indexOf("-"); 
	 									
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);  //remove -
	 										TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.SINK; // negative // SINK
	 									}
	 									else
	 									{		
	 										n = strTemp.indexOf("+");
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);  //remove +
	 										}
	 										TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.SOURCE; // positive // SOURCE
	 									}
	 									Data_Index++;
	 									d_Amp = Double.parseDouble(strTemp.toString());
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = d_Amp;
	 									Temp_d_Amp = TempProjectionTable[0][0][2];	// for reciprocity error calculation
	 									Data_Index++;
	 								}
	 								break;

	 							case Definition.PROJECTIONFILE_FREQUENCY:
	 								double Freq=0;
	 								int FreqIndex;
	 								//strTemp = ChangeCapitalCharacter(strTemp);
	 								// Frequency = strTemp.toString();						// YE
	 								if(!strTemp.toString().toUpperCase().equals("NULL"))
	 								{
	 									if(strTemp.toString().toUpperCase().equals("MIXED1"))
	 									{
	 										FreqIndex = 16;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED2"))
	 									{
	 										FreqIndex = 17;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED3"))
	 									{
	 										FreqIndex = 18;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED4"))
	 									{
	 										FreqIndex = 19;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED5"))
	 									{
	 										FreqIndex = 20;
	 									}
	 									else
	 									{
	 									
	 										n = strTemp.toString().toUpperCase().indexOf("H"); 
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);
	 										}

	 										n = strTemp.toString().toUpperCase().indexOf("Z");
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);
	 										}

	 										n = strTemp.toString().toUpperCase().indexOf("K");	// kHz
	 										if(n >= 0)
	 										{	
	 											strTemp.deleteCharAt(n);
	 											Freq = Double.parseDouble(strTemp.toString()) * 1000;
	 											
	 										}
	 										else
	 										{
	 											n = strTemp.toString().toUpperCase().indexOf("M");		// MHz
	 											if(n >= 0)
	 											{
	 												Freq = Double.parseDouble(strTemp.toString()) * 1000000;
	 											}
	 											else				// Hz
	 											{
	 												Freq = Double.parseDouble(strTemp.toString());
	 											}
	 										}
	 										FreqIndex = ScaleFreq(Freq); 
	 									}

	 								}
	 								else
	 								{
	 									FreqIndex = 15;
	 								}


	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = FreqIndex;
	 								Data_Index++;
	 								break;

	 							case Definition.PROJECTIONFILE_GAIN:
	 								n = strTemp.toString().toUpperCase().indexOf("X");
	 								if(n >= 0)
	 								{
	 									strTemp.deleteCharAt(n);
	 								}
	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Double.parseDouble(strTemp.toString());
	 								//Temp_d_Gain = TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index];		// YE
	 								Gain = TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index];
	 								ChIndex++;
	 								Data_Index = 0;
	 								break;
	 							}
	 						}
	 					}

	 					break;
	 				}
	 			}

	 		}
	 	s2.close();	
	 }
	    fin.close();
	 	return TRUE;
	 }
	 
	 
	 /************* Load Projection File *****************/
	 int LoadFabricProjectionFile(String FilePath)
	 {
		 char[] contents = new char[150];
		 
		 char[] Tempcontents = new char[150];
		 StringBuilder strTemp = new StringBuilder();
		 int CommentIndex = 0;
		 int ProjectionLevel =Definition.SCRIPTLEVEL1;
		 int Data_Index = 0;
	 	 int ChIndex = 0;
	 	 int n;
	 	 ProjectionTableIndex = 0;
	 	 // System.out.println(FilePath);
	 	Scanner fin = null;
	    try {

	        fin = new Scanner(new File(FilePath));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
	 
	    while (fin.hasNextLine()) {
	    	
	            
	    	Scanner s2 = new Scanner(fin.nextLine());
	        
	        while (s2.hasNext()) {
	            Tempcontents = s2.next().toCharArray();
	            CommentIndex = CheckCommnet(Tempcontents);
	            //System.out.println( CommentIndex  );
	        	if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 			{	
	        		strTemp.delete(0, strTemp.length());
	 				strTemp.append(Tempcontents) ;
	 			}
	 			else	// Comment
	 			{
	 			    while (s2.hasNext())
	 			    	s2.next();
	 				
					int i;
					for( i = 0; i < CommentIndex; i++)
					{
						contents[i] = Tempcontents[i];
					}
					contents[i] = 0;
					strTemp.delete(0, strTemp.length());
					strTemp.append("");
	 			}
	        	if(!strTemp.toString().equals("")) 
	 			{ 
	 				switch(ProjectionLevel)
	 				{

	 				case Definition.SCRIPTLEVEL1:
	 					ProjectionName[ProjectionTableIndex] = strTemp.toString();//////////////////check
	 					ProjectionLevel = Definition.SCRIPTLEVEL2;
	 					break;
	 				case Definition.SCRIPTLEVEL2:
	 					if(!strTemp.toString().equals("")) 
	 					{
	 						if(strTemp.toString().toUpperCase().equals("END"))
	 						{
	 							ProjectionTableIndex++;
	 							ChIndex = 0;
	 							Data_Index = 0;
	 							ProjectionLevel = Definition.SCRIPTLEVEL1;
	 						}
	 						else
	 						{
	 							switch(Data_Index)
	 							{
	 							case Definition.PROJECTIONFILE_CH:
	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							
	 							case Definition.PROJECTIONFILE_SIGN:
	 							
	 								 //d_Amp = 1000;
	 								
	 								
	 								if(strTemp.toString().toUpperCase().equals("NULL") || strTemp.toString().toUpperCase().equals("VM"))
	 								{
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.VM;
	 									Data_Index++;
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = d_Amp;
	 									Data_Index++;
	 								}
	 								else
	 								{
	 									//Test
	 									n = strTemp.toString().toUpperCase().indexOf("M");	// YE ??
	 									
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									
	 									n = strTemp.toString().toUpperCase().indexOf("U"); //remove U
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									
	 									n = strTemp.toString().toUpperCase().indexOf("A"); //remove A
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);
	 									}
	 									n = strTemp.indexOf("-"); 
	 									
	 									if(n >= 0)
	 									{
	 										strTemp.deleteCharAt(n);  //remove -
	 										TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.SINK; // negative // SINK
	 									}
	 									else
	 									{		
	 										n = strTemp.indexOf("+");
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);  //remove +
	 										}
	 										TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = Definition.SOURCE; // positive // SOURCE
	 									}
	 									Data_Index++;
	 									d_Amp = Double.parseDouble(strTemp.toString());
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = d_Amp;
	 									Temp_d_Amp = TempProjectionTable[0][0][2];	// for reciprocity error calculation
	 									Data_Index++;
	 								}
	 								break;

	 							case Definition.PROJECTIONFILE_FREQUENCY:
	 								double Freq=0;
	 								int FreqIndex;
	 								//strTemp = ChangeCapitalCharacter(strTemp);
	 								// Frequency = strTemp.toString();						// YE
	 								if(!strTemp.toString().toUpperCase().equals("NULL"))
	 								{
	 									if(strTemp.toString().toUpperCase().equals("MIXED1"))
	 									{
	 										FreqIndex = 16;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED2"))
	 									{
	 										FreqIndex = 17;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED3"))
	 									{
	 										FreqIndex = 18;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED4"))
	 									{
	 										FreqIndex = 19;
	 									}
	 									else if(strTemp.toString().toUpperCase().equals("MIXED5"))
	 									{
	 										FreqIndex = 20;
	 									}
	 									else
	 									{
	 									
	 										n = strTemp.toString().toUpperCase().indexOf("H"); 
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);
	 										}

	 										n = strTemp.toString().toUpperCase().indexOf("Z");
	 										if(n >= 0)
	 										{
	 											strTemp.deleteCharAt(n);
	 										}

	 										n = strTemp.toString().toUpperCase().indexOf("K");	// kHz
	 										if(n >= 0)
	 										{	
	 											strTemp.deleteCharAt(n);
	 											Freq = Double.parseDouble(strTemp.toString()) * 1000;
	 											
	 										}
	 										else
	 										{
	 											n = strTemp.toString().toUpperCase().indexOf("M");		// MHz
	 											if(n >= 0)
	 											{
	 												Freq = Double.parseDouble(strTemp.toString()) * 1000000;
	 											}
	 											else				// Hz
	 											{
	 												Freq = Double.parseDouble(strTemp.toString());
	 											}
	 										}
	 										FreqIndex = ScaleFreq(Freq); 
	 									}

	 								}
	 								else
	 								{
	 									FreqIndex = 15;
	 								}


	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = FreqIndex;
	 								Data_Index++;
	 								break;

	 							case Definition.PROJECTIONFILE_GAIN:
	 								n = strTemp.toString().toUpperCase().indexOf("X");
	 								if(n >= 0)
	 								{
	 									strTemp.deleteCharAt(n);
	 								}
	 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Double.parseDouble(strTemp.toString());
	 								//Temp_d_Gain = TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index];		// YE
	 								Gain = TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index];
	 								Data_Index++;
	 								break;
	 								
	 								
	 								
	 							case Definition.PROJECTIONFILE_SRC1:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_SINK1:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_VM_POS1:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_VM_NEG1:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString());
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_SRC2:    // data to mux2 value - 16 bcz it is 5 bit 
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString()) - 16 ;
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_SINK2:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString()) - 16;
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_VM_POS2:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString()) - 16;
	 								Data_Index++;
	 								break;
	 							case Definition.PROJECTIONFILE_VM_NEG2:
	 								if(strTemp.toString().toUpperCase().equals("NULL"))
	 								{
		 								TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] = 16;
	 								}
	 								else
	 									TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index] =  Integer.parseInt(strTemp.toString()) - 16 ;
	 								Data_Index = 0;
	 								ChIndex++;
	 								break;
	 							}
	 						}
	 					}

	 					break;
	 				}
	 			}

	 		}
	 	s2.close();	
	 }
	    fin.close();
	 	return TRUE;
	 }
	 
	 /************* Make Projection Table Function *****************/
	 int MakeProjectionTable()
	 {
		int[] FreqtoCNTGap_data = new int[3];
		int SmallestFreq =10 ;
	 	int SmallestFreqCNTHigh=0,SmallestFreqCNTLow=0,SmallestFreqGap=0;
	 	int CNTHigh1=0,CNTLow1=0,Gap1=0;
	 	int MultiGap1 = 1;
	 	int MultiGap2 = 1;
	 	int TotalFreq =0 ;
	 	int[]  DemodulationFreq =  new int[10];
	 	int[]  AmplitudeFactor =  new int[1];	// YE
	 	AmplitudeFactor[0] = 1;		// YE
	 	
	 	for(int x = 0; x <10; x++)
	 	{
	 		DemodulationFreq[x] = 0;
	 	}
	 	int DemodulationFreqIndex =0;
	 	
	 	int i=0,a,j=0,k=0;
	 	for(int x = 0; x < 10; x++)
	 	{
	 		DemodulationFreq[x] = -1;
	 	}
	 	m_nMixedFlag = Definition.SINGLE_FREQUENCY;
	 	i = 0; 
	 	for(i = 0; i < m_nTotalProjection ; i++)
	 	{
	 		for(a = 0; a < ProjectionTableIndex; a++)
	 		{
	 			if(m_strScriptProjectionName[i].equals(ProjectionName[a]))
	 			{
	 				TotalFreq = 0;
	 				SmallestFreq = 10;
	 				int TempIndex=0;		//YE ??
	 				for(j = 0; j < Definition.TOTAL_CH; j++)
	 				{
	 					for(k = 0; k < Definition.PROjECTIONTABLEROW; k++)
	 					{
	 						switch(k)
	 						{
	 						case Definition.PROJECTIONINDEX:
	 							ProjectionTable[i][j][k] = i;
	 							break;

	 						case Definition.CHANNELINDEX:
	 							ProjectionTable[i][j][k] = j;
	 							break;

	 						case Definition.CHANNELINFO:
	 							TempIndex = j;
	 							ProjectionTable[i][TempIndex][k] = 0;
	 							break;

	 						case Definition.CHANNELCTRL:
	 							if(TempProjectionTable[a][TempIndex][Definition.PROJECTIONFILE_SIGN] == Definition.SOURCE)
	 							{
	 								ProjectionTable[i][TempIndex][k] = 2;
	 							}
	 							else if(TempProjectionTable[a][TempIndex][Definition.PROJECTIONFILE_SIGN] == Definition.SINK)
	 							{
	 								ProjectionTable[i][TempIndex][k] = 3;
	 							}
	 							else
	 							{
	 								ProjectionTable[i][TempIndex][k] = 0;
	 							}
	 							break;

	 						case Definition.INJECTIONCURRENTFREQUENCY:
	 							if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 16) // 10Hz 50Hz 100Hz
	 							{
	 								ProjectionTable[i][j][k] = 16;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 17) // 1kHz	5kHz	10kHz
	 							{
	 								ProjectionTable[i][j][k] = 17;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 18) // 10kHz	50kHz	100kHz
	 							{
	 								ProjectionTable[i][j][k] = 18;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 19) // 50kHz	250kHz	450kHz
	 							{
	 								ProjectionTable[i][j][k] = 19;
	 							}
	 							else
	 							{	
	 								ProjectionTable[i][j][k] = (int)TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY];
	 							}
	 							break;

	 						case Definition.AMP1_HIGH:
//	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) >> 8);
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],AmplitudeFactor[0]) >> 8);
	 							break;
	 						case Definition.AMP1_LOW:
//	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) & 0xFF);
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],AmplitudeFactor[0]) & 0xFF);
	 							break;

	 						case Definition.AMP2_HIGH:
//	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) >> 8);
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],AmplitudeFactor[0]) >> 8);
	 							break;
	 						case Definition.AMP2_LOW:
//	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) & 0xFF);
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],AmplitudeFactor[0]) & 0xFF);
	 							break;

	 						case Definition.GAIN1:
	 						ProjectionTable[i][j][k] = (int) TempProjectionTable[a][j][Definition.PROJECTIONFILE_GAIN];
	 						break;
	 
	 						/*case GAIN2:
	 							ProjectionTable[i][j][k] = TempProjectionTable[a][j][PROJECTIONFILE_GAIN];
	 							break;
	 */
	 						case Definition.ACQUREFREQUENCY_CNT_HIGH:
	 							if(-1 < TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] && TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] < SmallestFreq)
	 							{
	 								SmallestFreq = (int) TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY];
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 16) // 10Hz 50Hz 100Hz
	 							{
	 								SmallestFreq = 0;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 17) // 1kHz	5kHz	10kHz
	 							{
	 								SmallestFreq = 3;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 18) // 10kHz	50kHz	100kHz
	 							{
	 								SmallestFreq = 5;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 19) // 50kHz	250kHz	450kHz
	 							{
	 								SmallestFreq = 6;
	 							}
	 							break;

	 						case Definition.TOTALDEMODULATIONFREQUENCY:
	 							if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] < 10)
	 							{
	 								TotalFreq++;
	 								int Check;
	 								Check = 0;
	 								for(int b = 0; b < DemodulationFreqIndex; b++)
	 								{
	 									if(DemodulationFreq[b] == TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY])
	 									{
	 										Check = 1;
	 									}
	 								}
	 								if(Check == 0)
	 								{
	 									DemodulationFreq[DemodulationFreqIndex] = (int) TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY];
	 									DemodulationFreqIndex++;
	 								}

	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 16) // 10Hz 50Hz 100Hz
	 							{
	 								m_nMixedFlag = Definition.MIXED_FREQUENCY;
	 								TotalFreq = 6;
	 								DemodulationFreqIndex = 3;
	 								DemodulationFreq[0] = 0;
	 								DemodulationFreq[1] = 1;
	 								DemodulationFreq[2] = 2;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 17) // 1kHz	5kHz	10kHz
	 							{
	 								m_nMixedFlag = Definition.MIXED_FREQUENCY;
	 								TotalFreq = 6;
	 								DemodulationFreqIndex = 3;
	 								DemodulationFreq[0] = 3;
	 								DemodulationFreq[1] = 4;
	 								DemodulationFreq[2] = 5;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 18) // 10kHz	50kHz	100kHz
	 							{
	 								m_nMixedFlag = Definition.MIXED_FREQUENCY;
	 								TotalFreq = 6;
	 								DemodulationFreqIndex = 3;
	 								DemodulationFreq[0] = 5;
	 								DemodulationFreq[1] = 6;
	 								DemodulationFreq[2] = 7;
	 							}
	 							else if(TempProjectionTable[a][j][Definition.PROJECTIONFILE_FREQUENCY] == 19) // 50kHz 250kHz	450kHz
	 							{
	 								m_nMixedFlag = Definition.MIXED_FREQUENCY;
	 								TotalFreq = 6;
	 								DemodulationFreqIndex = 3;
	 								DemodulationFreq[0] = 6;
	 								DemodulationFreq[1] = 7;
	 								DemodulationFreq[2] = 9;
	 							}
	 							break;
	 						} 
	 					}
	 				}
	 				
	 				TotalFreq = TotalFreq / 2;
	 				
	 				if(DemodulationFreqIndex > 1)
	 				{
	 					if(DemodulationFreq[0] > DemodulationFreq[1])
	 					{
	 						int temp = 0;
	 						temp = DemodulationFreq[1];
	 						DemodulationFreq[1] = DemodulationFreq[0];
	 						DemodulationFreq[0] = temp;
	 					}
	 				}
	 				
	 				FreqtoCNTGap_data = FreqtoCNTGap(SmallestFreq);
	 				SmallestFreqCNTHigh = FreqtoCNTGap_data[0];
	 				SmallestFreqCNTLow = FreqtoCNTGap_data[1];
	 				SmallestFreqGap= FreqtoCNTGap_data[2];	
	 				
	 				FreqtoCNTGap_data = FreqtoCNTGap(DemodulationFreq[1]);
	 				CNTHigh1 = FreqtoCNTGap_data[0];
	 				CNTLow1 = FreqtoCNTGap_data[1];
	 				Gap1= FreqtoCNTGap_data[2];	
	 				
	 				FreqtoCNTGap_data = FreqtoCNTGap(DemodulationFreq[2]);
	 				/*CNTHigh2 = FreqtoCNTGap_data[0];
	 				CNTLow2 = FreqtoCNTGap_data[1];
	 				Gap2= FreqtoCNTGap_data[2];*/
	 				
	 				if(m_nMixedFlag == Definition.MIXED_FREQUENCY)
	 				{
	 					for(j = 0; j < Definition.TOTAL_CH; j++)
	 					{
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_CNT_HIGH] = SmallestFreqCNTHigh;
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_CNT_LOW] = SmallestFreqCNTLow;
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_GAP] = SmallestFreqGap;

	 						ProjectionTable[i][j][Definition.TOTALDEMODULATIONFREQUENCY] = m_nTotalFreqNum-1;

	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY1] = SmallestFreqGap;
	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY2] = SmallestFreqGap*5;
	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY3] = SmallestFreqGap*10;
	 						
	 					}
	 				}
	 				else
	 				{
	 					for(j = 0; j < Definition.TOTAL_CH; j++)
	 					{
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_CNT_HIGH] = SmallestFreqCNTHigh;
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_CNT_LOW] = SmallestFreqCNTLow;
	 						ProjectionTable[i][j][Definition.ACQUREFREQUENCY_GAP] = SmallestFreqGap;

	 						ProjectionTable[i][j][Definition.TOTALDEMODULATIONFREQUENCY] = m_nTotalFreqNum-1;

	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY1] = SmallestFreqGap;
	 						if(DemodulationFreqIndex > 1)
	 						{
	 							MultiGap1 = ConvertMultiFreq(SmallestFreqCNTHigh,SmallestFreqCNTLow,SmallestFreqGap, CNTHigh1,CNTLow1,Gap1);
	 							if(MultiGap1 > 50)
	 							{
	 								MultiGap1 = 50;
	 							}
	 						}
	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY2] =MultiGap1 ;					
	 						ProjectionTable[i][j][Definition.DEMODULATIONFREQUENCY3] = MultiGap1 ;
	 						
	 					}
	 				}
	 				
	 				break;
	 			}
	 		}
	 		if(i == ProjectionTableIndex)
	 		{
	 			String str;
	 			str = "Define  : " + m_strScriptProjectionName[a];
				JOptionPane.showMessageDialog(null, str, "InfoBox " , JOptionPane.INFORMATION_MESSAGE);

	 		}
	 	}
	 	if(DemodulationFreqIndex > 1)
	 	{
	 		MultiGap2 = ConvertMultiFreq(SmallestFreqCNTHigh,SmallestFreqCNTLow,SmallestFreqGap, CNTHigh1,CNTLow1,Gap1);
	 		if(MultiGap2 > 50)
	 		{
				JOptionPane.showMessageDialog(null, "The ration of multiple freq. should be within 50 times", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 		}
	 	}
	 	return TRUE;
	 }
	 
	 
	 /************* Load Calibration File Function *****************/
	 public int LoadCalibrationFile(int DCOffsetCal, int OutputImpedanceCal, int AmplitudeCal, int VMCal)
	 {
	 	String FilePath;
///////////////////////////////////////	m_strUSBID = String.valueOf(USB.m_strDeviceString[USB.m_nDeviceList]);  ///// check
	 	m_strUSBID ="eit1";
	 	if(DCOffsetCal == 1) /// 1 = True
	 	{
	 		m_strCategory = "DCOffset";
	 		m_strFileName = "DCOffsetValue.txt";
	 		FilePath = m_strDefaultFilePath + "/" + m_strCalibration + "/" + m_strUSBID + "/" + m_strCategory;
	 		if(!LoadDCOffsetCalibrationFile(FilePath))
	 		{ 	
	 			StringBuilder strError = new StringBuilder();
	 			strError.append("Load DCOffset Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 			for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)  // GA
	 			{
	 				for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 				{
	 					DCCalData[Ch][Freq] = 0;			
	 				}
	 			}
	 		}
	 	}
	 	else
	 	{
	 		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
	 		{
	 			for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 			{
	 				DCCalData[Ch][Freq] = 0;
	 			}
	 		}
	 		
	 	}

	 	if(OutputImpedanceCal==1)
	 	{
	 		m_strCategory = "OutputImpedance";
	 		FilePath = m_strDefaultFilePath + "/" + m_strCalibration + "/" + m_strUSBID + "/" + m_strCategory;
	 		if(!LoadOutImpedanceCalibrationFile(FilePath))
	 		{
	 			StringBuilder strError = new StringBuilder();
	 			strError.append("Load OutputImpedance Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 		
	 			for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
	 			{
	 				for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 				{
	 					for(int i = 0; i < 4; i++)
	 					{
	 						CCSCalData[Ch][Freq][i] = 128;
	 					}
	 				}
	 			}
	 		}
	 	}
	 	else
	 	{	
	 		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
	 		{
	 			for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 			{
	 				for(int i = 0; i < 4; i++)
	 				{
	 					CCSCalData[Ch][Freq][i] = 128;
	 				}
	 			}
	 		}
	 	}

	 	if(AmplitudeCal==1)
	 	{
	 		m_strCategory = "Amplitude";
	 		m_strFileName = "AmplitudeValue.txt";
	 		FilePath = m_strDefaultFilePath + "//" + m_strCalibration + "//" + m_strUSBID + "//" + m_strCategory;
	 		if(!LoadAmplitudeCalibrationFile(FilePath))
	 		{
	 			
	 			StringBuilder strError = new StringBuilder();
	 			strError.append("Load Amplitude Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 			for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
	 			{
	 				for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 				{
	 					m_dAmplitudeCalibrationFactor[Ch][Freq] = 1;
	 				}
	 			}
	 		}
	 	}
	 	else
	 	{
	 		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
	 		{
	 			for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 			{
	 				m_dAmplitudeCalibrationFactor[Ch][Freq] = 1;
	 			}
	 		}
	 	}

	 	if(VMCal==1)
	 	{
	 		m_strCategory = "Voltmeter";
	 		//m_strProtocolName = "2DNeighboring_mV";
	 		FilePath = m_strDefaultFilePath + "/" + m_strCalibration + "/" + m_strUSBID + "/" + m_strCategory + "/" + m_strProtocolName;
	 		String MagFile, PhaseFile;
	 		MagFile = m_strProtocolName + '_' + m_strScriptFileTitle + '_' + "MagnitudeFactor.txt";
	 		PhaseFile = m_strProtocolName + '_' + m_strScriptFileTitle + '_' + "PhaseFactor.txt";
	 		Object[] obj= LoadVoltmeterCalibrationFile(FilePath,MagFile,PhaseFile,m_dVoltmeterMagnitudeFactor,m_dVoltmeterPhaseFactor);
	 		
	 		if((int)obj[0]== 0 && (int)obj[1]== 0)
	 		{
	 			StringBuilder strError = new StringBuilder();
	 			strError.append("Load Voltmeter Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 			for(int i = 0; i < m_nVoltmeterCalibrationIndex; i++)
	 			{
	 				m_dVoltmeterMagnitudeFactor[i] = 1;
	 				m_dVoltmeterPhaseFactor[i] = 0;
	 			}
	 		}
	 		m_dVoltmeterMagnitudeFactor =(double[]) obj[0];
	 		m_dVoltmeterPhaseFactor =(double[]) obj[1];
	 	}
	 	else
	 	{
	 		for(int i = 0; i < m_nVoltmeterCalibrationIndex; i++)
	 		{
	 			m_dVoltmeterMagnitudeFactor[i] = 1;
	 			m_dVoltmeterPhaseFactor[i] = 0;
	 		}
	 	}
	 	
	 	return TRUE;
	 }
	 
	 /************* Load DCOffset CalibrationFile Function *****************/
	 boolean LoadDCOffsetCalibrationFile(String FilePath)
	 {
		 Scanner fin = null;   
		      
	 	String  FileName ,  strFilePath;
	 	FileName = "DCOffset.txt";
	 	int Temp;
	 	for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 	{
	 		strFilePath = FilePath + "//" + m_strFreq[Freq] + "//" + FileName;
	 		try {

		        fin = new Scanner(new File(strFilePath));
		    } catch (FileNotFoundException e) {
		        StringBuilder strError = new StringBuilder();
	 			strError.append("Load DCOffset Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 			return false;
		    }
	 			char[] contents = new char[150];
	 			char[] Tempcontents= new char[150];
	 			StringBuilder strTemp = new StringBuilder();
	 	
	 			int CommentIndex = 0;
	 			int Ch = 0;
	 			while (fin.hasNextLine()) {
			    	
		            
			    	Scanner finDCOffset = new Scanner(fin.nextLine());
			        
			        while (finDCOffset.hasNext()) {
			            Tempcontents = finDCOffset.next().toCharArray();
			            CommentIndex = CheckCommnet(Tempcontents);
	 				if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 				{	
	 					strTemp.delete(0, strTemp.length());
		 				strTemp.append(Tempcontents) ;
	 				}
	 				else	// Comment
	 				{
	 					 while (finDCOffset.hasNext())
	 		 				finDCOffset.next();
	 					 
	 	 				
	 					int i;
	 					for( i = 0; i < CommentIndex; i++)
	 					{
	 						contents[i] = Tempcontents[i];
	 					}
	 					contents[i] = 0;
	 					strTemp.delete(0, strTemp.length());
	 					strTemp.append("");
	 					
	 				}
	 				if(!strTemp.toString().equals("")) 
	 				{
	 					Temp = Integer.parseInt(strTemp.toString());
	 					DCCalData[Ch][Freq] = Temp;
	 					Ch++;
	 				}

	 			}
			        finDCOffset.close();
	 		}
	 		fin.close();
	 		

	 	}
	
	 	return true;
	 }
	 
	 /************* Load Out Impedance Calibration File Function *****************/
	 boolean LoadOutImpedanceCalibrationFile(String FilePath)
	 {
		 
		 Scanner fin = null;   
	      
		 //	ifstream finOutputImpedanceCalibration[Definition.NUM_OF_FREQUENCY];
		 	String  FileName ,  strFilePath;
		 	FileName = "OutputImpedance.txt";
		 	int Temp;

		 	for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
		 	{
		 		strFilePath = FilePath + "/" + m_strFreq[Freq] + "/" + FileName;
		 		try {
		 			
			        fin = new Scanner(new File(strFilePath));
			    } catch (FileNotFoundException e) {
			    	StringBuilder strError = new StringBuilder();
		 			strError.append("Load OutputImpedance Calibration File Error\n");
		 			strError.append(FilePath);
					JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			       // fin.close();
		 			return false;
			    }
		 			char[] contents = new char[150];
		 			char[] Tempcontents= new char[150];
		 			StringBuilder strTemp = new StringBuilder();
		 	
		 			int CommentIndex = 0;
		 			int DataIndex = 0;
		 			int Ch = 0;
		 			while (fin.hasNextLine()) {
				    	Scanner finOutputImpedanceCalibration = new Scanner(fin.nextLine());
				        
				    while (finOutputImpedanceCalibration.hasNext()) 
				    {
				    	Tempcontents = finOutputImpedanceCalibration.next().toCharArray();
				        CommentIndex = CheckCommnet(Tempcontents);
	 				if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 				{	
	 					strTemp.delete(0, strTemp.length());
		 				strTemp.append(Tempcontents) ;
	 				}
	 				else	// Comment
	 				{
	 					 while (finOutputImpedanceCalibration.hasNext())
		 		 				finOutputImpedanceCalibration.next();
	 					int i;
	 					for(i = 0; i < CommentIndex; i++)
	 					{
	 						contents[i] = Tempcontents[i];
	 					}
	 					contents[i] = 0;
	 					strTemp.delete(0, strTemp.length());
	 					strTemp.append("");

	 				}
	 				if(!strTemp.toString().equals("")) 
	 				{
	 					//System.out.println(strTemp.toString());
	 					Temp = Integer.parseInt(strTemp.toString());
	 					CCSCalData[Ch][Freq][DataIndex] = Temp;
	 					DataIndex++;
	 				}
	 				if(DataIndex >= 4)
	 				{
	 					DataIndex = 0;
	 					Ch++;
	 				}

	 			}
				    	finOutputImpedanceCalibration.close();
	 		}
	 		fin.close();
	 	}

	 	return true;
	 }
	 
	 /************* Load Amplitude Calibration File Function *****************/
	 boolean LoadAmplitudeCalibrationFile(String FilePath)
	 {
		String  FileName ,  strFilePath;
	 	FileName = "Amplitude.txt";
	 	Scanner fin= null;

	 	for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
	 	{
	 		strFilePath = FilePath + "/" + m_strFreq[Freq] + "/" + FileName;
	 		try {

		        fin = new Scanner(new File(strFilePath));
		    } catch (FileNotFoundException e) {
		    	StringBuilder strError = new StringBuilder();
	 			strError.append("Load Amplitude Calibration File Error\n");
	 			strError.append(FilePath);
				JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		       // fin.close();
	 			return false;
		    }
	 			char[] contents = new char[150];
	 			char[] Tempcontents= new char[150];
	 			StringBuilder strTemp = new StringBuilder();
	 	
	 			int CommentIndex = 0;
	 			int Ch = 0;
	 			while (fin.hasNextLine()) {
			    	Scanner finAmplitude = new Scanner(fin.nextLine());
			        
			    while (finAmplitude.hasNext()) 
			    {
			    	Tempcontents = finAmplitude.next().toCharArray();
			        CommentIndex = CheckCommnet(Tempcontents);
	 				if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 				{	
	 					strTemp.delete(0, strTemp.length());
		 				strTemp.append(Tempcontents) ;
		 				
	 				}
	 				else	// Comment
	 				{
	 					 while (finAmplitude.hasNext())
		 		 				finAmplitude.next();
	 					int i;
	 					for(i = 0; i < CommentIndex; i++)
	 					{
	 						contents[i] = Tempcontents[i];
	 					}
	 					contents[i] = 0;
	 					strTemp.delete(0, strTemp.length());
	 					strTemp.append("");
	 				}
	 				if(!strTemp.toString().equals(""))
	 				{
	 					m_dAmplitudeCalibrationFactor[Ch][Freq] = Double.parseDouble(strTemp.toString());
	 					Ch++;
	 				}
	 				if(Ch >= m_nTotalCh)
	 				{
	 					//finAmplitude.close();
	 					break;
	 					//return true;
	 				}

	 			}
			    finAmplitude.close();
	 		}
	 		fin.close();
	 	}
	 	return true;
	 }

	 
	 /************* Load Volt meter Calibration File Function *****************/
	 Object[] LoadVoltmeterCalibrationFile(String FilePath, String MagFileName, String PhaseFileName, double[] MagCalFactor, double[] PhaseCalFactor)
	 {
		 char[] contents = new char[150];
		 char[] Tempcontents = new char[150];
		 StringBuilder strTemp = new StringBuilder();
		 int CommentIndex = 0;
		 
		 int DataIndex = 0;
	 	Scanner fin = null;
	    try {

	        fin = new Scanner(new File(FilePath + "/" + MagFileName));
	    
	    } catch (FileNotFoundException e) {
	    	StringBuilder strError = new StringBuilder();
 			strError.append("Load Voltmeter Calibration File Error\n");
 			strError.append(FilePath);
			JOptionPane.showMessageDialog(null, strError, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	     //   fin.close();
	 		
	        return new Object[]{0, 0}; ///////check
	    }
	 
	    while (fin.hasNextLine()) {
	    	
	            
	    	Scanner finMag = new Scanner(fin.nextLine());
	        
	        while (finMag.hasNext()) {
	            Tempcontents = finMag.next().toCharArray();
	            CommentIndex = CheckCommnet(Tempcontents);

	 			if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 			{	
	 				strTemp.delete(0, strTemp.length());
	 				strTemp.append(Tempcontents) ;
	 			}
	 			else	// Comment
	 			{
	 			    while (finMag.hasNext())
	 				 finMag.next();
	 				
					int i;
					for( i = 0; i < CommentIndex; i++)
					{
						contents[i] = Tempcontents[i];
					}
					contents[i] = 0;
					strTemp.delete(0, strTemp.length());
					strTemp.append("");
					
	 			}
	 			if(!strTemp.toString().equals(""))
	 			{
	 				Double.parseDouble(strTemp.toString());
	 				DataIndex++;
	 			}
	 		}
	        finMag.close();
		 	
	 	}
	 	
	 	fin.close();
	 	
	 	
	 	if(DataIndex != m_nTotalCh * m_nTotalProjection)
	 	{
	 		JOptionPane.showMessageDialog(null, "Voltmeter Calibration Data is not matched", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return new Object[]{0, 0}; 
	 	}
	 	DataIndex = 0;
	    try {
	        fin = new Scanner(new File(FilePath + "/" + MagFileName));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	        fin.close();
	 		
	        return new Object[]{0, 0}; 
	    }
	 
	    while (fin.hasNextLine()) {
	    	
	            
	    	Scanner finMag = new Scanner(fin.nextLine());
	        
	        while (finMag.hasNext()) {
	            Tempcontents = finMag.next().toCharArray();
	            CommentIndex = CheckCommnet(Tempcontents);

	 			if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 			{	
	 				strTemp.delete(0, strTemp.length());
	 				strTemp.append(Tempcontents) ;
	 			}
	 			else	// Comment
	 			{
	 				 while (finMag.hasNext())
	 	 				 finMag.next();
	 					int i;
	 					for( i = 0; i < CommentIndex; i++)
	 					{
	 						contents[i] = Tempcontents[i];
	 					}
	 					contents[i] = 0;
	 					strTemp.delete(0, strTemp.length());
	 					strTemp.append("");
	 			}
	 			if(!strTemp.toString().equals(""))
	 			{
	 				MagCalFactor[DataIndex] =  Double.parseDouble(strTemp.toString());
	 				DataIndex++;
	 			}
	 		}
	    	finMag.close();
	 	}
	 
	 

	    try {

	        fin = new Scanner(new File(FilePath + "/" + PhaseFileName));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	        fin.close();
	        return new Object[]{0, 0}; ///////check
	    }
	 
	    while (fin.hasNextLine()) {
	    	
	            
	    	Scanner finPhase = new Scanner(fin.nextLine());
	        
	        while (finPhase.hasNext()) {
	            Tempcontents = finPhase.next().toCharArray();
	            CommentIndex = CheckCommnet(Tempcontents);

	 			if(CommentIndex == Definition.NOCOMMENT)	// No Comment
	 			{	
	 				strTemp.delete(0, strTemp.length());
	 				strTemp.append(Tempcontents) ;
	 			}
	 			else	// Comment
	 			{
	 				 while (finPhase.hasNext())
	 	 				 finPhase.next();
	 					int i;
	 					for( i = 0; i < CommentIndex; i++)
	 					{
	 						contents[i] = Tempcontents[i];
	 					}
	 					contents[i] = 0;
	 					strTemp.delete(0, strTemp.length());
	 					strTemp.append("");
	 			}
	 			if(!strTemp.toString().equals(""))
	 			{
	 				Double.parseDouble(strTemp.toString());
	 				DataIndex++;
	 			}
	 		}
	        finPhase.close();
	 	}
	 	
	 	
	 	fin.close();
	 	
	 	if(DataIndex != m_nTotalCh * m_nTotalProjection)
	 	{
	 		JOptionPane.showMessageDialog(null, "Voltmeter Calibration Data is not matched", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return new Object[]{0, 0}; ///////check
	 	}
	 	DataIndex = 0;
	 	  try {

		        fin = new Scanner(new File(FilePath + "/" + PhaseFileName));
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();  
		        fin.close();
		 		 return new Object[]{0, 0}; 
		    }
		 
		    while (fin.hasNextLine()) {
		    	
		            
		    	Scanner finPhase = new Scanner(fin.nextLine());
		        
		        while (finPhase.hasNext()) {
		            Tempcontents = finPhase.next().toCharArray();
		            CommentIndex = CheckCommnet(Tempcontents);

		 			if(CommentIndex == Definition.NOCOMMENT)	// No Comment
		 			{	
		 				strTemp.delete(0, strTemp.length());
		 				strTemp.append(Tempcontents) ;
		 			}
		 			else	// Comment
		 			{
		 				 while (finPhase.hasNext())
		 	 				 finPhase.next();
		 					int i;
		 					for( i = 0; i < CommentIndex; i++)
		 					{
		 						contents[i] = Tempcontents[i];
		 					}
		 					contents[i] = 0;
		 					strTemp.delete(0, strTemp.length());
		 					strTemp.append("");
		 			}
		 			if(!strTemp.toString().equals(""))
		 			{
		 				PhaseCalFactor[DataIndex] = Double.parseDouble(strTemp.toString());
		 				DataIndex++;
		 			}
		 		}
		        finPhase.close();
		 	}
		 	
		 	fin.close();
	 	 return new Object[]{MagCalFactor, PhaseCalFactor};
	 }


	 public boolean MakeCCSTable()
	 {
		int[] FreqtoCNTGap_data = new int[3];
	 	for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)   // GA
	 	{
	 		for(int j = 0; j < Definition.NUM_OF_FREQUENCY; j++)
	 		{

	 			for(int k = 0; k < 17; k++)
	 			{
	 				switch(k)
	 				{
	 				case Definition.FREQ_NUM:
	 					CCSTable[i][j][k] = j;
	 					break;
	 				case Definition.GIC_ON_OFF:
	 					if(j < 5)
	 					{
	 						CCSTable[i][j][k] = 8;
	 					}
	 					else if(j == 5)
	 					{
	 						CCSTable[i][j][k] = 8;
	 					}
	 					else if(j == 6)
	 					{
	 						CCSTable[i][j][k] = 4;

	 					}	
	 					else if( j == 7)
	 					{
	 						CCSTable[i][j][k] = 4;
	 					}
	 					else if(j == 8)
	 					{
	 						CCSTable[i][j][k] = 2;
	 					}
	 					else if( j == 9)
	 					{
	 						CCSTable[i][j][k] = 1;
	 					}
	 					break;
	 				case Definition.CLK_CNT_HIGH:
	 					int CNTHigh,CNTLow,Gap;
	 					
	 					FreqtoCNTGap_data = FreqtoCNTGap(j);
	 					CNTHigh = FreqtoCNTGap_data[0];
	 					CNTLow = FreqtoCNTGap_data[1];
	 					Gap= FreqtoCNTGap_data[2];
	 					
	 					CCSTable[i][j][k] = CNTHigh;	// CLK_CNT_HITH
	 					k++;
	 					CCSTable[i][j][k] = CNTLow;	// CLK_CNT_LOW
	 					k++;
	 					CCSTable[i][j][k] = Gap;	//GAP_DATA

	 					break;


	 				case Definition.HCP_CCS_A:
	 					CCSTable[i][j][k] = CCSCalData[i][j][0];
	 					break;

	 				case Definition.HCP_CCS_B:
	 					CCSTable[i][j][k] = CCSCalData[i][j][1];
	 					break;

	 				case Definition.GIC1A:
	 					CCSTable[i][j][k] = CCSCalData[i][j][2];
	 					break;

	 				case Definition.GIC1B:
	 					CCSTable[i][j][k] = CCSCalData[i][j][3];
	 					break;

	 				case Definition.GIC2A:
	 					CCSTable[i][j][k] = CCSCalData[i][j][2];
	 					break;

	 				case Definition.GIC2B:
	 					CCSTable[i][j][k] = CCSCalData[i][j][3];
	 					break;

	 				case Definition.GIC3A:
	 					CCSTable[i][j][k] = CCSCalData[i][j][2];
	 					break;

	 				case Definition.GIC3B:
	 					CCSTable[i][j][k] = CCSCalData[i][j][3];
	 					break;

	 				case Definition.GIC4A:
	 					CCSTable[i][j][k] = CCSCalData[i][j][2];
	 					break;

	 				case Definition.GIC4B:
	 					CCSTable[i][j][k] = CCSCalData[i][j][3];
	 					break;
	 				case Definition.DAC_OFFSET_HIGH:
	 					CCSTable[i][j][k] = ((DCCalData[i][j] >> 8));		//YE
	 					k++;
	 					CCSTable[i][j][k] = (DCCalData[i][j] & 0xFF);

	 					break;


	 				}
	 			}
	 		}

	 		for(int k = 0; k < 17; k++)					// 10Hz		50Hz	100Hz
	 		{
	 			switch(k)
	 			{
	 			case Definition.FREQ_NUM:
	 				CCSTable[i][10][k] = 16;
	 				break;
	 			case Definition.GIC_ON_OFF:
	 				CCSTable[i][10][k] = 8;		
	 				break;
	 			case Definition.CLK_CNT_HIGH:
	 				int CNTHigh,CNTLow,Gap;
	 		
	 				FreqtoCNTGap_data = FreqtoCNTGap(0); //10HZ
	 				CNTHigh = FreqtoCNTGap_data[0];
	 				CNTLow = FreqtoCNTGap_data[1];
	 				Gap= FreqtoCNTGap_data[2];
	 				
	 				CCSTable[i][10][k] = CNTHigh;	// CLK_CNT_HITH
	 				k++;
	 				CCSTable[i][10][k] = CNTLow;	// CLK_CNT_LOW
	 				k++;
	 				CCSTable[i][10][k] = Gap;	//GAP_DATA
	 				break;

	 			case Definition.HCP_CCS_A:
	 				CCSTable[i][10][k] = CCSCalData[i][0][0];
	 				break;

	 			case Definition.HCP_CCS_B:
	 				CCSTable[i][10][k] = CCSCalData[i][0][1];
	 				break;

	 			case Definition.GIC1A:
	 				CCSTable[i][10][k] = CCSCalData[i][0][2];
	 				break;

	 			case Definition.GIC1B:
	 				CCSTable[i][10][k] = CCSCalData[i][0][3];
	 				break;

	 			case Definition.GIC2A:
	 				CCSTable[i][10][k] = CCSCalData[i][0][2];
	 				break;

	 			case Definition.GIC2B:
	 				CCSTable[i][10][k] = CCSCalData[i][0][3];
	 				break;

	 			case Definition.GIC3A:
	 				CCSTable[i][10][k] = CCSCalData[i][0][2];
	 				break;

	 			case Definition.GIC3B:
	 				CCSTable[i][10][k] = CCSCalData[i][0][3];
	 				break;

	 			case Definition.GIC4A:
	 				CCSTable[i][10][k] = CCSCalData[i][0][2];
	 				break;

	 			case Definition.GIC4B:
	 				CCSTable[i][10][k] = CCSCalData[i][0][3];
	 				break;
	 			case Definition.DAC_OFFSET_HIGH:
	 				CCSTable[i][10][k] = ((DCCalData[i][0] >> 8));
	 				k++;
	 				CCSTable[i][10][k] = (DCCalData[i][0] & 0xFF);

	 				break;


	 			}
	 		}
	 		for(int k = 0; k < 17; k++)					// 1kHz		5kHz		10kHz
	 		{
	 			switch(k)
	 			{
	 			case Definition.FREQ_NUM:
	 				CCSTable[i][11][k] = 17;
	 				break;
	 			case Definition.GIC_ON_OFF:

	 				CCSTable[i][11][k] = 8;

	 				break;
	 			case Definition.CLK_CNT_HIGH:
	 				int CNTHigh,CNTLow,Gap;
	 			
	 				FreqtoCNTGap_data = FreqtoCNTGap(3);  //1HZ
	 				CNTHigh = FreqtoCNTGap_data[0];
	 				CNTLow = FreqtoCNTGap_data[1];
	 				Gap= FreqtoCNTGap_data[2];
	 				
	 				CCSTable[i][11][k] = CNTHigh;	// CLK_CNT_HITH
	 				k++;
	 				CCSTable[i][11][k] = CNTLow;	// CLK_CNT_LOW
	 				k++;
	 				CCSTable[i][11][k] = Gap;	//GAP_DATA

	 				break;

	 			case Definition.HCP_CCS_A:
	 				CCSTable[i][11][k] = CCSCalData[i][3][0];
	 				break;

	 			case Definition.HCP_CCS_B:
	 				CCSTable[i][11][k] = CCSCalData[i][3][1];
	 				break;

	 			case Definition.GIC1A:
	 				CCSTable[i][11][k] = CCSCalData[i][3][2];
	 				break;

	 			case Definition.GIC1B:
	 				CCSTable[i][11][k] = CCSCalData[i][3][3];
	 				break;

	 			case Definition.GIC2A:
	 				CCSTable[i][11][k] = CCSCalData[i][3][2];
	 				break;

	 			case Definition.GIC2B:
	 				CCSTable[i][11][k] = CCSCalData[i][3][3];
	 				break;

	 			case Definition.GIC3A:
	 				CCSTable[i][11][k] = CCSCalData[i][3][2];
	 				break;

	 			case Definition.GIC3B:
	 				CCSTable[i][11][k] = CCSCalData[i][3][3];
	 				break;

	 			case Definition.GIC4A:
	 				CCSTable[i][11][k] = CCSCalData[i][3][2];
	 				break;

	 			case Definition.GIC4B:
	 				CCSTable[i][11][k] = CCSCalData[i][3][3];
	 				break;
	 			case Definition.DAC_OFFSET_HIGH:
	 				CCSTable[i][11][k] = ((DCCalData[i][3] >> 8));
	 				k++;
	 				CCSTable[i][11][k] = (DCCalData[i][3] & 0xFF);

	 				break;
	 			}
	 		}
	 		for(int k = 0; k < 17; k++)					// 10kHz	50kHz		100kHz
	 		{
	 			switch(k)
	 			{
	 			case Definition.FREQ_NUM:
	 				CCSTable[i][12][k] = 18;
	 				break;
	 			case Definition.GIC_ON_OFF:

	 				CCSTable[i][12][k] = 8;
	 				break;
	 			case Definition.CLK_CNT_HIGH:
	 				int CNTHigh,CNTLow,Gap;
	 			
	 				FreqtoCNTGap_data = FreqtoCNTGap(5);  //10KHZ
	 				CNTHigh = FreqtoCNTGap_data[0];
	 				CNTLow = FreqtoCNTGap_data[1];
	 				Gap= FreqtoCNTGap_data[2];
	 				
	 				CCSTable[i][12][k] = CNTHigh;	// CLK_CNT_HITH
	 				k++;
	 				CCSTable[i][12][k] = CNTLow;	// CLK_CNT_LOW
	 				k++;
	 				CCSTable[i][12][k] = Gap;	//GAP_DATA

	 				break;


	 			case Definition.HCP_CCS_A:
	 				CCSTable[i][12][k] = CCSCalData[i][5][0];
	 				break;

	 			case Definition.HCP_CCS_B:
	 				CCSTable[i][12][k] = CCSCalData[i][5][1];
	 				break;

	 			case Definition.GIC1A:
	 				CCSTable[i][12][k] = CCSCalData[i][5][2];
	 				break;

	 			case Definition.GIC1B:
	 				CCSTable[i][12][k] = CCSCalData[i][5][3];
	 				break;

	 			case Definition.GIC2A:
	 				CCSTable[i][12][k] = CCSCalData[i][5][2];
	 				break;

	 			case Definition.GIC2B:
	 				CCSTable[i][12][k] = CCSCalData[i][5][3];
	 				break;

	 			case Definition.GIC3A:
	 				CCSTable[i][12][k] = CCSCalData[i][5][2];
	 				break;

	 			case Definition.GIC3B:
	 				CCSTable[i][12][k] = CCSCalData[i][5][3];
	 				break;

	 			case Definition.GIC4A:
	 				CCSTable[i][12][k] = CCSCalData[i][5][2];
	 				break;

	 			case Definition.GIC4B:
	 				CCSTable[i][12][k] = CCSCalData[i][5][3];
	 				break;
	 			case Definition.DAC_OFFSET_HIGH:
	 				CCSTable[i][12][k] = ((DCCalData[i][5] >> 8));
	 				k++;
	 				CCSTable[i][12][k] = (DCCalData[i][5] & 0xFF);

	 				break;


	 			}
	 		}

	 		for(int k = 0; k < 17; k++)					// 50kHz	250kHz		450kHz
	 		{
	 			switch(k)
	 			{
	 			case Definition.FREQ_NUM:
	 				CCSTable[i][13][k] = 19;
	 				break;
	 			case Definition.GIC_ON_OFF:

	 				CCSTable[i][13][k] = 4;
	 				break;
	 			case Definition.CLK_CNT_HIGH:
	 				int CNTHigh,CNTLow,Gap;
	 				//FreqtoCNTGap(6,&CNTHigh,&CNTLow,&Gap);	// 10kHz
	 				FreqtoCNTGap_data = FreqtoCNTGap(6);  //10KHZ
	 				CNTHigh = FreqtoCNTGap_data[0];
	 				CNTLow = FreqtoCNTGap_data[1];
	 				Gap= FreqtoCNTGap_data[2];
	 				
	 				CCSTable[i][13][k] = CNTHigh;	// CLK_CNT_HITH
	 				k++;
	 				CCSTable[i][13][k] = CNTLow;	// CLK_CNT_LOW
	 				k++;
	 				CCSTable[i][13][k] = Gap;	//GAP_DATA

	 				break;


	 			case Definition.HCP_CCS_A:
	 				CCSTable[i][13][k] = CCSCalData[i][6][0];
	 				break;

	 			case Definition.HCP_CCS_B:
	 				CCSTable[i][13][k] = CCSCalData[i][6][1];
	 				break;

	 			case Definition.GIC1A:
	 				CCSTable[i][13][k] = CCSCalData[i][6][2];
	 				break;

	 			case Definition.GIC1B:
	 				CCSTable[i][13][k] = CCSCalData[i][6][3];
	 				break;

	 			case Definition.GIC2A:
	 				CCSTable[i][13][k] = CCSCalData[i][6][2];
	 				break;

	 			case Definition.GIC2B:
	 				CCSTable[i][13][k] = CCSCalData[i][6][3];
	 				break;

	 			case Definition.GIC3A:
	 				CCSTable[i][13][k] = CCSCalData[i][6][2];
	 				break;

	 			case Definition.GIC3B:
	 				CCSTable[i][13][k] = CCSCalData[i][6][3];
	 				break;

	 			case Definition.GIC4A:
	 				CCSTable[i][13][k] = CCSCalData[i][6][2];
	 				break;

	 			case Definition.GIC4B:
	 				CCSTable[i][13][k] = CCSCalData[i][6][3];
	 				break;
	 			case Definition.DAC_OFFSET_HIGH:
	 				CCSTable[i][13][k] = ((DCCalData[i][6] >> 8));
	 				k++;
	 				CCSTable[i][13][k] = (DCCalData[i][6] & 0xFF);

	 				break;

	 			}
	 		}
	 	}

	 	return true;
	 }
	 public boolean MakeSWTable()
	 {
		int index = 0;
		int src= 0;
		int sink = 15;
		int v1 = 0;
		int v2 = 15;
		
	 	index = 0;
	 	for(int j = 0; j < Definition.MAX_CH; j++)
	 	{
	 		if (src > 15) {src = 0;}
	 		if (sink > 15) {sink = 0;}
	 		if (v1 > 15) {v1 = 0;}
	 		if (v2 > 15) {v2 = 0;}
 			for(int k = 0; k < 5; k++)
 			{
 				switch(k)
 				{
 				case 0:
 					SWTable[0][j][k] = index;
 					break;
 				case 1:
 					SWTable[0][j][k] = src;
 					break;
 				case 2:
 					SWTable[0][j][k] = sink;
 					break;	
 				case 3:
 					SWTable[0][j][k] = v1;
 					break;		
 				case 4:
 					SWTable[0][j][k] = v2;
 					break;	
 				}
 			}
 			index++;
 			src++;
 			sink++;
 			v1++;
 			v2++;
 		}


	 	return true;
	 }
	 
	 
	 /************* EIT Pipeline Scan Function 
	 * @throws InterruptedException 
	 * @throws HeadlessException 
	 * @throws IOException *****************/
	 boolean EIT_PipelineScan() throws HeadlessException, InterruptedException, IOException
	 {  

		 /*
			if(!EIT_CommonSystemSetting())
			{
				return false;
			}
			if(!EIT_PipelineSystemSetting())
			{
				return false;
			}
				
			PipelineScanStart();		// Start : 1
			*/
		 if(!Multi_Para_CommonSetting())
			{
				return false;
			}
		 SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
		 
			 m_hThreadComm = new  Thread(new Runnable() {
					
					@Override
					public void run() {
						m_bThreadCommWorkingFlag = true;
						try {
							ThreadComm(S_EIT_Doc.this);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		 if(!m_hThreadComm.isAlive())
		 {
			  m_hThreadComm.start();
		 }
		 Thread.sleep(50);
		 m_hThreadDataProcess = new  Thread(new Runnable() {
				
				@Override
				public void run() {
					m_bThreadDataProcessWorkingFlag = true;
					ThreadDataProcess(S_EIT_Doc.this);
				}
			});
		 if(!m_hThreadDataProcess.isAlive())
		 {
			 m_hThreadDataProcess.start();
		 }
		 
		 m_hThreadWriteFile = new  Thread(new Runnable() {
				
				@Override
				public void run() {
					m_bThreadWriteFileWorkingFlag = true;
					try {
						ThreadWriteFile(S_EIT_Doc.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		 if(!m_hThreadWriteFile.isAlive())
		 {
			 m_hThreadWriteFile.start();
		 }
		
			return true; 
	 }
	 
	 
	
	 /************* EIT Common System Setting *****************/
	 boolean EIT_CommonSystemSetting() throws HeadlessException, InterruptedException
	 {
	 	
		 if(!Reset())
		 	{
		 		JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 else if(!CheckNumofIMM())
		 	{
		 		JOptionPane.showMessageDialog(null, "IMM Check Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 else if(!Average(m_nNumofAVG - 1))
		 	{
		 		JOptionPane.showMessageDialog(null, "Send Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}

		 else if(!OverflowReset())
		 	{
		 		JOptionPane.showMessageDialog(null, "Reset Overflow Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 else if(!OverflowNumber(Definition.ALL_CH,10))
		 	{
		 		JOptionPane.showMessageDialog(null, "Send Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 else if(!DACSet(Definition.ALL_CH,2,128))
		 	{
		 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}

		 else if(!AnalogBackplaneSWSetting(Definition.ALL_CH,1,1,0,0,1,0))  
		 	{
		 		JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 
		 else if(!OpModeSetting(0,0,0,0,0))
		 	{
		 		JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 	
		else if(!TimeSlotSetting(0))	
		 	{
		 		JOptionPane.showMessageDialog(null, "Time Slot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	}

		if(!CalSWSetting(Definition.ALL_CH,0))	// Modified by eun
		 	{
				JOptionPane.showMessageDialog(null, "Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}

	 	return true;
	 	}
	 
	 
	 
	 /********* Multi-Parameter setting *****/
	 boolean Multi_Para_CommonSetting() throws HeadlessException, InterruptedException
	 {
		 if(!Reset())
		 {
		 	JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	return false;
		 }
		 else if(!CheckMulti())
		 {
		 	JOptionPane.showMessageDialog(null, "Multi Check Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	return false;
		 }
		 else if(!AccelGyroSetting())
		 {
		 	JOptionPane.showMessageDialog(null, "AccelGyroSetting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	return false;
		 }
		 else if(!Multi_CommFPGASet(16,0,7,49))
		 {
		 	JOptionPane.showMessageDialog(null, "Multi_CommFPGASet Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	return false;
		 }
		 else if(!MotionAcqData())
		 {
		 	JOptionPane.showMessageDialog(null, "MotionAcqData Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	return false;
		 }
		 
		 try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 return true;
	 }
	 
	 
	 boolean CheckMulti()
	 {
		 USB.USBComm((byte)Definition.COMMAND_CHECK_Multi); 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_CHECK_Multi)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) 0) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 
	 boolean AccelGyroSetting()
	 {
		 USB.USBComm((byte)Definition.COMMAND_Multi_AccelGyroSetting); 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_Multi_AccelGyroSetting)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) 0) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 
	 boolean Multi_CommFPGASet(int Mode, int NumOfScans, int DataSize, int NumofMeas)
	 {
		 USB.USBComm((byte)Definition.COMMAND_Multi_CommFPGASet, (byte)Mode, (byte)NumOfScans, (byte)DataSize, (byte)NumofMeas); 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_Multi_CommFPGASet)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) Mode) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] !=(byte) NumOfScans) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] !=(byte) DataSize) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] !=(byte) NumofMeas) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 
	 boolean MotionAcqData()
	 {
		 USB.USBComm((byte)Definition.COMMAND_Multi_MotionAcqData); 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_Multi_MotionAcqData)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) 0) // channel miss cnt
	 	{
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 /********* Serial Frequency setting *****/
	 boolean FerquencySetting(String m_strPreFreq , int CH_Num)
	 {
		 if(m_strPreFreq.toUpperCase().equals("100HZ"))
		 {
			 	/*if(!CCSDigiSetting(CH_Num,100,214))  
				{
					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GICSWSet(CH_Num,0))
				{
					JOptionPane.showMessageDialog(null, "GIC Switch Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GIC1Setting(CH_Num,128,240))
				{
						JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return false;
				}
			 	else */if(!InjFreqSetting(CH_Num,1,0,199))
				{
					JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			 	else if(!AcqFreqSetting(CH_Num,1,0,199))
			 	{
					JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
		 }
		 else if(m_strPreFreq.toUpperCase().equals("2KHZ"))
		 {
			 /*if(!CCSDigiSetting(CH_Num,100,214))  
				{
					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GICSWSet(CH_Num,0))
				{
					JOptionPane.showMessageDialog(null, "GIC Switch Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GIC1Setting(CH_Num,128,240))
				{
						JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return false;
				}
			 	else */if(!InjFreqSetting(CH_Num,1,0,9))
				{
					JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			 	else if(!AcqFreqSetting(CH_Num,1,0,9))
			 	{
					JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
		 }
		 else if(m_strPreFreq.toUpperCase().equals("10KHZ"))
		 {
			 /*if(!CCSDigiSetting(CH_Num,150,145))  
				{
					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GICSWSet(CH_Num,8))
				{
					JOptionPane.showMessageDialog(null, "GIC Switch Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GIC1Setting(CH_Num,130,117))
				{
						JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return false;
				}
			 	else */if(!InjFreqSetting(CH_Num,1,0,1))
				{
					JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			 	else if(!AcqFreqSetting(CH_Num,1,0,1))
			 	{
					JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
		 }
		 else if(m_strPreFreq.toUpperCase().equals("20KHZ"))
		 {
			/* if(!CCSDigiSetting(CH_Num,125,72))  
				{
					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GICSWSet(CH_Num,4))
				{
					JOptionPane.showMessageDialog(null, "GIC Switch Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GIC1Setting(CH_Num,123,97))
				{
						JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return false;
				}
			 	else */if(!InjFreqSetting(CH_Num,1,0,0))
				{
					JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			 	else if(!AcqFreqSetting(CH_Num,1,0,0))
			 	{
					JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
		 }
		 else if(m_strPreFreq1.toUpperCase().equals("100KHZ"))
		 {
			/* if(!CCSDigiSetting(CH_Num,132,84))  
				{
					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GICSWSet(CH_Num,4))
				{
					JOptionPane.showMessageDialog(null, "GIC Switch Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}	
			 	else if(!GIC1Setting(CH_Num,120,26))
				{
						JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						return false;
				}
			 	else */if(!InjFreqSetting(CH_Num,5,0,0))
				{
					JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			 	else if(!AcqFreqSetting(CH_Num,5,0,0))
			 	{
					JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
		 }
		 return true;
		 
	 }
	 
	 
	 /************* Serial Amplitude Setting *****************/
	 boolean Amplitude_Setting(double d_Amp , int CH_Num)
	 {
		// if(m_strAmp1.equals("0uA"))
		if(d_Amp ==0)
		 {
			 if(!DACSet(CH_Num,11,0))
			 {
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,12,0))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,15,0))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,16,0))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 
		 }
		 //else if(m_strAmp1.equals("300uA"))
		else if(d_Amp ==300)
		 {
			 if(!DACSet(CH_Num,11,44))
			 {
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,12,1))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,15,44))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,16,1))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 
		 }
		/// else if(m_strAmp1.equals("600uA"))
		else if(d_Amp ==600)
		 {
			 if(!DACSet(CH_Num,11,88))
			 {
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,12,2))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,15,88))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,16,2))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 
		 }
		 //else if(m_strAmp1.equals("1000uA"))
		else if(d_Amp ==1000)
		 {
			 if(!DACSet(CH_Num,11,232))
			 {
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,12,3))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,15,232))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 	else if(!DACSet(CH_Num,16,3))
			 	{
			 		JOptionPane.showMessageDialog(null, "DACSet1 Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			 		return false;
			 	}
			 
		 }
		 return true;
	 }
	 /************* EIT Reset Setting *****************/
	 public boolean Reset() throws InterruptedException
	 {
		USB.USBComm((byte)Definition.COMMAND_RESET);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_RESET)
	 	{	//System.out.println("Reset"+ (USB.m_nResponse[0]&0xff));
	 	
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 
	 /************* EIT Check Number of IMM Function *****************/
	 public boolean CheckNumofIMM() throws InterruptedException
	 {
		 USB.USBComm((byte)Definition.COMMAND_CHECK_IMM); 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_CHECK_IMM)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) 0) // channel count high
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] !=(byte) 3) // channel count low
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] !=(byte) 2) // Number of channel
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] !=(byte) 0) // Miss channel
	 	{
	 		return false;
	 	}
	 	/*			// Modified by Eun
	 	else
	 	{
//	 		m_nTotalCh = USB.m_nResponse[2]&0xFF;		// YE
	 		if(USB.m_nResponse[2] != Definition.Serial_EIT_ALL_CH)
	 		{
	 			JOptionPane.showMessageDialog(null, "Total Ch : "+ m_nTotalCh, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
 			
	 		}
	 	}
	 	*/
	 	
	 	return true;
	 }
	 
	 /************* EIT Average Setting Function *****************/
	 public boolean Average(int AVGNum)
	 {
    	SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	USB.USBComm((byte)Definition.COMMAND_AVERAGE, (byte)AVGNum);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_AVERAGE)
	 	{
	 		return false;
	 	}

	 	else if(USB.m_nResponse[1] !=(byte) AVGNum)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 /************* EIT DACSet  Setting Function *****************/
	 public boolean DACSet(int Ch, int DACAddr, int DACData)
	 {
		SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	USB.USBComm((byte)Definition.COMMAND_DAC_SETTING, (byte)Ch,(byte) DACAddr,(byte)DACData);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_DAC_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)DACAddr)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)DACData)
	 	{
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 /************* S_EIT DM_Delay Setting Function *****************/
	 public boolean DM_Delay(int  val1 ,int val2)
		{

			USB.USBComm((byte)Definition.COMMAND_DM_DELAY,(byte)val1,(byte)val2);	
			if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_DM_DELAY)
			{
				return false;
			}
			else if(USB.m_nResponse[1] != (byte)val1)
			{
				return false;
			}
			else if(USB.m_nResponse[2] != (byte)val2)
			{
				return false;
			}
				
			return true;
		}
	 /************* EIT Over flow Number Setting Function *****************/
	 public boolean OverflowReset()
	 {
	 	USB.USBComm((byte)Definition.COMMAND_OVERFLOW_RESET);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_OVERFLOW_RESET)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 /************* EIT Over flow Number Setting Function *****************/
	 public boolean OverflowNumber(int Ch, int Num)
	 {
		SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0); 
	 	USB.USBComm((byte)Definition.COMMAND_OVERFLOW_NUM, (byte)Ch, (byte)Num);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_OVERFLOW_NUM)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Num)
	 	{
	 		return false;
	 	}

	 	return true;
	 }
	 
	 /************* EIT Over flow Number Setting Function *****************/
/*	 public boolean SWMuxNumberOfMeas(int Num_High, int Num_Low)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_SWMuxNumberOfMeas, (byte)Num_High, (byte)Num_Low);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_SWMuxNumberOfMeas)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Num_High)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Num_Low)
	 	{
	 		return false;
	 	}
	 	return true;
	 }*/
	 
	 /************* EIT Analog Back plane Switch Setting Function *****************/
	 public boolean AnalogBackplaneSWSetting(int Ch, int Ctrl, int SW1, int SW2, int SW3, int SW4, int SW5)
	 {
		 byte SW;
	 	SW = 0;
	 	if(SW1 == 1)
	 	{
	 		SW += 1;
	 	}
	 	if(SW2 == 1)
	 	{
	 		SW += 2;
	 	}
	 	if(SW3 == 1)
	 	{
	 		SW += 4;
	 	}
	 	if(SW4 == 1)
	 	{
	 		SW += 8;
	 	}
	 	if(SW5 == 1)
	 	{
	 		SW += 16;
	 	}
	 	USB.USBComm((byte)Definition.COMMAND_ANALOG_BP_SW,(byte)Ch,(byte)Ctrl,(byte)SW);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_ANALOG_BP_SW)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)Ctrl)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)SW)
	 	{
	 		return false;
	 	}
	 
	 	return true;
	 }
	 
	 /************* EIT Op ModeSetting Function *****************/
	 public boolean OpModeSetting(int OldNew, int SingleCascade, int MasterSlave, int EventTriggerOnOff, int OneMultiScan)	// Modified by YE
	 {
	 	/*if(OldNew==0) //////////////////check 
	 	{
	 		m_nOpMode = 0;
	 	}	
	 	else
	 	{
	 		m_nOpMode = (1 << 7) + (SingleCascade << 6) + (MasterSlave << 5) + (EventTriggerOnOff << 4) + (OneMultiScan << 3);
	 	}*/

		m_nOpMode = (OldNew << 7) + (SingleCascade << 6) + (MasterSlave << 5) + (EventTriggerOnOff << 4) + (OneMultiScan << 3);
		
	 	return true;
	 }

	 /************* EIT Time Slot Setting Function *****************/
	 
	 public boolean TimeSlotSetting(int TimeSlot)
	 {
	 	int Timeslot1, Timeslot2, Timeslot3;
	 	Timeslot3 = (TimeSlot & 0xFF0000)>>16;
	 	Timeslot2 = (TimeSlot & 0x00FF00)>>8;
	 	Timeslot1 = (TimeSlot & 0x0000FF);
	 	
	 	

	 	USB.USBComm((byte)Definition.COMMAND_TIMESLOT,(byte)Timeslot3, (byte)Timeslot2, (byte)Timeslot1);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_TIMESLOT)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)Timeslot3)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)Timeslot2)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)Timeslot1)
	 	{
	 		return false;
	 	}
	 	return true;

	 }

	 public boolean TestDC_Setting()
	 {
		SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0); 
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	USB.USBComm((byte)3);
	 	//System.out.println((USB.m_nResponse[0]&0xFF));
	 	if(USB.m_nResponse[0] != (byte)132)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 /************* EIT Calculate SW Setting Function *****************/
	 public boolean CalSWSetting(int Ch, int SW)
	 {
		SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0); 		 
	 	USB.USBComm((byte)Definition.COMMAND_CAL_SW_SETTING,(byte) Ch, (byte)SW);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_CAL_SW_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)SW)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 
	 
	 /************* EIT Pipelin System Setting Function 
	 * @throws IOException 
	 * @throws HeadlessException *****************/
	 boolean EIT_PipelineSystemSetting() throws HeadlessException, IOException
	 {
	 	int DMNum = 0;
	 	int TriggerNum = 0;
	
	 	if(m_nMixedFlag == Definition.MIXED_FREQUENCY)				// Mixed Mode
	 	{
	 		DMNum = 2;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	else											// Single Mode
	 	{
	 		DMNum = 0;							
	 	}
	 	
	 	/*if(!SWMuxNumberOfMeas(1,255)) // Modified by eun
	 	{
	 		JOptionPane.showMessageDialog(null, "SW Number of Measure setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}*/
	 	
	 	
	 	if(CCSTableDown()!=0) // 0 refer to true
	 	{
	 		JOptionPane.showMessageDialog(null, "CCS Table Down Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	if(SWTableDown()!=0) // 0 refer to true 
	 	{
	 		JOptionPane.showMessageDialog(null, "SW Table Down Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	if(ProjectionTalbeDown(m_nTotalProjection )!=0) // 0 refer to true
	 	{
	 		JOptionPane.showMessageDialog(null, "Projection Table Down Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	

	 	if(!InjectionDelay(m_nInjDelayHigh,m_nInjDelayLow))
	 	{
	 		JOptionPane.showMessageDialog(null, "InjectionDelay Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}

	 	if(!AnalogBackplaneSWSetting(Definition.ALL_CH,0,0,0,0,0,0))
	 	{
	 		JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}

	 	if(!TimeSlotSetting(100000))
	 	{
	 		JOptionPane.showMessageDialog(null, "Time Slot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	
	 	
	 	if(control_gui.m_bEventTrigger == true)		//	Event Triggered Mode
	 	{

	 		if(control_gui.m_nEventNumber <= 1)			
	 		{
	 			if(!OpModeSetting(1,0,0,1,0))						// Single Event Trigger 
	 			{
	 				JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 				return false;
	 			}
	 			TriggerNum = 0;
	 		}
	 		else								
	 		{
	 			if(!OpModeSetting(1,0,0,1,1))						// Multi Event Trigger
	 			{
	 				JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 				return false;
	 			}
	 			TriggerNum = control_gui.m_nEventNumber;
	 		}
	 	}
	 	else
	 	{
	 		if(!OpModeSetting(1,0,0,0,1))							// Single Multi-Scan Mode
	 		{
	 			JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 			return false;
	 		}
	 		TriggerNum = 0;
	 	}


//	 	if(!CommFPGASet((m_nOpMode >> 3), TriggerNum , DMNum, m_nTotalProjection-1))
/*	 	if(!CommFPGASet((m_nOpMode >> 3), TriggerNum , DMNum, 0,1))
	 	{
	 		JOptionPane.showMessageDialog(null, "CommFPGA Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}*/
	 	
	 	
//	 	if(!PipelineScanSetting(0,m_nSEIT_TotalProjection))
	 	byte[] data = new byte[2];
//	 	data[0] = (byte) (511 >> 8);  // high
//		data[1] = (byte) (511 );
	 	data[0] = (byte) (255 >> 8);  // high
		data[1] = (byte) (255);
		
/*		if(!SWNumberOfMeas(data[0], data[1]))
	 	{
	 		JOptionPane.showMessageDialog(null, "CommFPGA SW Number of measurement setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}*/
		
	 	if(!PipelineScanSetting(0,1,data[0],data[1]))
	 	{
	 		JOptionPane.showMessageDialog(null, "Pipeline Scan Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	
	 	if(!TimeInforSetting(m_nTimeInfoHigh, m_nTimeInfoMid,m_nTimeInfoLow))
	 	{
	 		JOptionPane.showMessageDialog(null, "Time Infor Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}

	 	if(!NewScanModeSetting(m_nOpMode, 0, data[0], data[1] ))
	 	{
	 		JOptionPane.showMessageDialog(null, "New Scan Mode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	return true;
	 }

	 
	 
	 /************* CCS Table Down Function *****************/
	
	 
	 int CCSTableDown() throws IOException
	 {
		 
        int Status =0 ;
	 	byte[] data = new byte[Definition.NUM_OF_FREQUENCY * Definition.CCSTABLEROW];
	 	
	 	int index = 0;
	 	data[index] = Definition.COMMAND_CCSTABLE;

	
	
	 	FileWriter foutCCSSend = null;
	    FileWriter foutCCSRecieve = null;
	    foutCCSSend = new FileWriter(m_strSaveFilePath + "CCSTableDownSendLog.txt");
	    foutCCSRecieve = new FileWriter(m_strSaveFilePath + "CCSTableDownRecieveLog.txt");


	 
	 	foutCCSSend.write("Ch" + "\t" + "FreqNum" + "\t" + "GIC_SW" + "\t" + "CNTHigh" + "\t" +"CNTLow" + "\t" +"GAP" + "\t" +"CCS_A" + "\t" +
	 		"CCS_B" + "\t" +"GIC1A" + "\t" +"GIC1B" + "\t" +"GIC2A" + "\t" +"GIC2B" + "\t"+"GIC3A" + "\t" +"GIC3B" + "\t"+"GIC4A" + "\t" +"GIC4B" + "\t"
	 		+"OffsetHigh" + "\t" +"OffsetLow" + "\r\n");
	 	foutCCSRecieve.write("Ch" + "\t"+ "FreqNum" + "\t" + "GIC_SW" + "\t" + "CNTHigh" + "\t" +"CNTLow" + "\t" +"GAP" + "\t" +"CCS_A" + "\t" +
	 		"CCS_B" + "\t" +"GIC1A" + "\t" +"GIC1B" + "\t" +"GIC2A" + "\t" +"GIC2B" + "\t"+"GIC3A" + "\t" +"GIC3B" + "\t"+"GIC4A" + "\t" +"GIC4B" + "\t"
	 		+"OffsetHigh" + "\t" +"OffsetLow" + "\r\n");



	 	for(int i = 0;i < Definition.Serial_EIT_ALL_CH; i++)  
	 	{
	 		index = 1;
	 		data[index] = (byte)i;
	 		for(int j = 0; j < Definition.NUM_OF_FREQUENCY + Definition.NUM_OF_MIXED_FREQUENCY; j++)
	 		{
	 			index = 2;
	 			for(int k = 0; k <Definition.CCSTABLEROW; k++)
	 			{
	 				data[index] = (byte)CCSTable[i][j][k] ;  // based  on calibration data 
	 				index++;
	 			}

	 			for(int y = 1; y < index; y++)
	 			{
	 				foutCCSSend.write( ((int)data[y] &0xFF) + "\t");
	 			
	 			}
	 			
	 			Status = USB.USBComm2(Definition.CCSTABLEROW + 2, Definition.CCSTABLEROW + 2, 1, data);
	 		
	 			for(int x = 1; x < USB.m_nReadByte; x++)
	 			{
	 				foutCCSRecieve.write((int)(USB.m_nResponse[x] &0xFF)+"\t");
	 			}
	 			foutCCSSend.write("\r\n");
	 			foutCCSRecieve.write("\r\n");
	 		}
	 	}

	 	foutCCSSend.close();
	 	foutCCSRecieve.close();

	 	
	 	return Status; ///////if 0 then true
	 }
	 
	 int SWTableDown() throws IOException
	 {
		 
        int Status =0 ;
	 	byte[] data = new byte[9];
	 	
	 	int index = 0;
	 	data[index] = Definition.COMMAND_SWTABLE;

	 	FileWriter foutSWSend = null;
	    FileWriter foutSWRecieve = null;
	    foutSWSend = new FileWriter(m_strSaveFilePath + "SWTableDownSendLog.txt");
	    foutSWRecieve = new FileWriter(m_strSaveFilePath + "SWTableDownRecieveLog.txt");

	    foutSWSend.write("Source1" + "\t" + "Sink1" + "\t" + "VM+1" + "\t" + "VM-1" + "\t" + "Source2" + "\t" +"Sink2" + "\t" + "VM+2" + "\t" + "VM-2" +"\r\n");
	    foutSWRecieve.write("Source1" + "\t" + "Sink1" + "\t" + "VM+1" + "\t" + "VM-1" + "\t" + "Source2" +"\t" + "Sink2" + "\t" + "VM+2" + "\t" + "VM-2" + "\r\n");
	    
	 	for(int i = 0;i < Definition.SEIT_ALL_Projection; i++)
	 	{
	 		for(int j = 0; j < Definition.TOTAL_CH; j++)
	 		{
	 			index = 1;
	 			data[index] = (byte)((i*16)+j);
	 			index = 2;
	 			for(int k = 5; k <9; k++)  /// Send SW data src1, sink1, VM1+, VM1-
	 			{
	 				System.out.println(TempProjectionTable[i][j][k]);
	 				data[index] = (byte)TempProjectionTable[i][j][k] ;
	 				index++;
	 			}

	 			for(int y = 2; y < index; y++)
	 			{
	 				foutSWSend.write( ((int)data[y] &0xFF) + "\t");
	 			}
	 			
	 			Status = USB.USBComm2(9, 9, 1, data);
	 		
	 			for(int x = 2; x < USB.m_nReadByte; x++)
	 			{
	 				foutSWRecieve.write((int)(USB.m_nResponse[x] &0xFF)+"\t");
	 			}
	 			foutSWSend.write("\r\n");
	 			foutSWRecieve.write("\r\n");
	 		}
	 	}

	 	foutSWSend.close();
	 	foutSWRecieve.close();

	 	
	 	return Status;
	 }
	 
	 /************* Projection Table Down Function *****************/
	 int ProjectionTalbeDown(int Line) throws IOException
	 {
		 int Status =0 ;
//	 	int[] data= new int[Definition.MAX_CH * Definition.PROjECTIONTABLEROW];
//	 	byte[] data= new byte[Definition.Serial_EIT_ALL_CH * Definition.PROjECTIONTABLEROW];
		byte[] data= new byte[Definition.MAX_CH * Definition.PROjECTIONTABLEROW];
		int index = 0;
		int FreqInfo = 0;
		int cnt = 0;
	 	data[index] = Definition.COMMAND_PROJECTIONTABLE;


		FileWriter foutProjSend = null;
	    FileWriter foutProjRecieve = null;
	    foutProjSend = new FileWriter(m_strSaveFilePath + "ProjectionTableSendLog.txt");
	    foutProjRecieve = new FileWriter(m_strSaveFilePath + "ProjectionTableRecieveLog.txt");
	    
	  
	 	foutProjSend.write("ProjIndex" + "\t" + "ChIndex" + "\t" + "ChInfo" + "\t" + "ChCtrl" + "\t"+"Inj.Freq" + "\t" +"Amp1High" + "\t" +"AmpLow" + "\t" +
	 		"Amp2High" + "\t" +"Amp2Low" + "\t" +"Gain1" + "\t" +"AcqGap" + "\t" +"AcqCNTHigh" + "\t"+"AcqCntLow" + "\t" +"TotalDMFreq" + "\t" +"DM1" + "\t"
	 		+"DM2" + "\t" +"DM3" + "\r\n");
	 	foutProjRecieve.write("ProjIndex" + "\t" + "ChIndex" + "\t" + "ChInfo" + "\t" + "ChCtrl" + "\t" +"Inj.Freq" + "\t" +"Amp1High" + "\t" +"AmpLow" + "\t" +
	 		"Amp2High" + "\t" +"Amp2Low" + "\t" +"Gain1" + "\t" +"AcqGap" + "\t" +"AcqCNTHigh" + "\t"+"AcqCntLow"  + "\t"+"TotalDMFreq" + "\t" +"DM1" + "\t"
	 		+"DM2" + "\t" +"DM3" + "\r\n");
	 	
	 	//byte[] result= new byte[2];
	 	
	 	  
	 	Line = Definition.TOTAL_CH;     //  number of projection 
	 	for(int i = 0;i < Line; i++)
	 	{
//	 		for(int j = 0; j < Definition.Serial_EIT_ALL_CH; j++)
	 		for(int j = 0; j < Definition.TOTAL_CH; j++)
	 		{	
	 			index = 1;
	 			for (int a = 0; a < Definition.TOTAL_CH; a++)
	 			{
	 				if ((byte)ProjectionTable[i][a][3] == 2)
	 				{
	 					FreqInfo = (byte)ProjectionTable[i][a][4];
	 				}
	 			}
	 			for(int k = 0; k <Definition.PROjECTIONTABLEROW; k++)
	 			{
	 				data[index] = (byte)ProjectionTable[i][j][k];
	 				if (index ==1)
	 				{
	 					data[index] = (byte) (cnt >> 8);  // high
	 					index++;
	 					data[index] = (byte) (cnt );

	 				}
	 				else if (index==3)
	 				{
	 					data[index] = (byte)0;		// Channel number
	 				}		
	 				else if (index==5)
	 				{
	 					data[index] = (byte)2;
	 				}
	 				else if (index==6)				// find freq index
	 				{
	 					data[index] = (byte)FreqInfo;
	 				}
	 				index++;
	 			}

	 			for(int y = 1; y < index; y++)
	 			{
	 				if(y ==1 )
	 				{
	 					foutProjSend.write(((data[1] & 0xFF) << 8 | (data[2] & 0xFF)) + "\t");
	 					y++;
	 				}
	 				else
	 					foutProjSend.write((int)(data[y]&0xFF) + "\t");
	 			}

	 			Status = USB.USBComm2(Definition.PROjECTIONTABLEROW + 2, Definition.PROjECTIONTABLEROW + 2, 1, data);   // + 2 H/l data and command

	 			for(int x = 1; x < USB.m_nReadByte; x++)
	 			{
	 				if(x ==1 )
	 				{
	 					foutProjRecieve.write(((USB.m_nResponse[1] & 0xFF) << 8 | (USB.m_nResponse[2] & 0xFF)) + "\t");
	 					x++;
	 				}
	 				else
	 					foutProjRecieve.write((int)(USB.m_nResponse[x]  &0xFF) +"\t");   
	 			}
	 			foutProjSend.write( "\r\n");
	 			foutProjRecieve.write("\r\n");
	 			cnt ++;
	 		}
	 		
	 	}
	 	
	 	cnt = 0; 
	 	for(int i = 0;i < Line; i++)
	 	{
//	 		for(int j = 0; j < Definition.Serial_EIT_ALL_CH; j++)
	 		for(int j = 0; j < Definition.TOTAL_CH; j++)
	 		{	
	 			index = 1;
	 			for (int a = 0; a < Definition.TOTAL_CH; a++)
	 			{
	 				if ((byte)ProjectionTable[i][a][3] == 3)
	 				{
	 					FreqInfo = (byte)ProjectionTable[i][a][4];
	 				}
	 			}
	 			for(int k = 0; k <Definition.PROjECTIONTABLEROW; k++)
	 			{
	 				data[index] = (byte)ProjectionTable[i][j][k];
	 				if (index ==1)
	 				{
	 					data[index] = (byte) (cnt >> 8);index++;
	 					data[index] = (byte) (cnt );
	 				}
	 				else if (index==3)
	 				{
	 					data[index] = (byte)1;	// Channel number
	 				}				
	 				else if (index==5)
	 				{
	 					data[index] = (byte)3;
	 				}
	 				else if (index==6)
	 				{
	 					data[index] = (byte)FreqInfo;
	 				}
	 				index++;
	 			}

	 			for(int y = 1; y < index; y++)
	 			{
	 				if(y ==1 )
	 				{
	 					foutProjSend.write(((data[1] & 0xFF) << 8 | (data[2] & 0xFF)) + "\t");
	 					y++;
	 				}
	 				else
	 					foutProjSend.write((int)(data[y]&0xFF) + "\t");
	 			}

	 			Status = USB.USBComm2(Definition.PROjECTIONTABLEROW + 2, Definition.PROjECTIONTABLEROW + 2, 1, data);

	 			for(int x = 1; x < USB.m_nReadByte; x++)
	 			{
	 				if(x ==1 )
	 				{
	 					foutProjRecieve.write(((USB.m_nResponse[1] & 0xFF) << 8 | (USB.m_nResponse[2] & 0xFF)) + "\t");
	 					x++;
	 				}
	 				else
	 					foutProjRecieve.write((int)(USB.m_nResponse[x]  &0xFF) +"\t");  
	 			}
	 			foutProjSend.write( "\r\n");
	 			foutProjRecieve.write("\r\n");
	 			cnt ++;
	 	
	 		}
	 		
	 	}
	 	
	 	foutProjSend.close();
	 	foutProjRecieve.close();
	 	return Status;
	 }

	 
	 /************* Injection Delay Function *****************/
	 public boolean InjectionDelay(int TimeHigh, int TimeLow)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_CCS_DELAY_SETTING, (byte)TimeHigh,(byte) TimeLow);
	 	if(USB.m_nResponse[0] !=(byte) Definition.RESPONSE_CCS_DELAY_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)TimeHigh)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)TimeLow)
	 	{
	 		return false;
	 	}

	 	return true;
	 }
	 
	 /************* SW Number Of Meas Function *****************/
	 public boolean SWNumberOfMeas(int Num_High, int Num_Low)
	 {
	 	
	 	USB.USBComm((byte)Definition.COMMAND_SWMuxNumberOfMeas,(byte) Num_High,(byte) Num_Low);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_SWMuxNumberOfMeas)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Num_High)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Num_Low)
	 	{
	 		return false;
	 	}

	 	return true;

	 }
	 
	 /************* Comm FPGA Set Function *****************/
	 public boolean CommFPGASet(int Mode, int ScanNum, int DMNum, int ProjNum, int Muxcontrol)
	 {
	 	
	 	USB.USBComm((byte)Definition.COMMAND_COMMFPGA_SET,(byte) Mode,(byte) ScanNum,(byte) DMNum,(byte)ProjNum,(byte)Muxcontrol);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_COMMFPGA_SET)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Mode)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)ScanNum)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)DMNum)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] != (byte)ProjNum)
	 	{
	 		return false;
	 	}
	 	return true;

	 }
	 
	 public boolean  VoltageScanStart() 
	 {
		return true;
	 }
	 public boolean NumberOfSamples()
	 {
		 return true ;
	 }
	 /************* Pipeline Scan Setting Function *****************/
	 public boolean PipelineScanSetting(int StartProjNum,int FreqMode, int TotalProjection_High, int TotalProjection_Low)
	 {
	 	//BYTE Command[10];

	 	//Command[0] = COMMAND_PIPELINESCAN_SETTING;		//
	 	//Command[1] = FreqMode;							// MixedFreq : 0 / SingleFreq : 1

	 	int DMNum = 0;

	 	if(m_nMixedFlag == Definition.MIXED_FREQUENCY)				// Mixed Mode
	 	{
	 		DMNum = 2;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	else											// Single Mode
	 	{
	 		DMNum = 0;								// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	
//	 	USB.USBComm((byte)Definition.COMMAND_PIPELINESCAN_SETTING,(byte)DMNum,(byte)FreqMode,(byte)StartProjNum, (byte)TotalProjection_High, (byte)TotalProjection_Low, (byte)m_nTimeInfoHigh,(byte)m_nTimeInfoMid, (byte)m_nTimeInfoLow);//USB 데이터 전송 잘 안됨 이거 가잘오면 확인
//		USB.USBComm((byte)Definition.COMMAND_PIPELINESCAN_SETTING,(byte)m_nMixedFlag, (byte)DMNum,(byte)StartProjNum, (byte)15,(byte)m_nTimeInfoHigh,(byte)m_nTimeInfoMid, (byte)m_nTimeInfoLow);
	 	USB.USBComm((byte)Definition.COMMAND_PIPELINESCAN_SETTING,(byte)DMNum,(byte)FreqMode,(byte)StartProjNum, (byte)TotalProjection_High, (byte)TotalProjection_Low);
	 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_PIPELINESCAN_SETTING)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)DMNum)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)FreqMode)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)StartProjNum)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[4] != (byte)TotalProjection_High)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[5] != (byte)TotalProjection_Low)
	 	{
	 		return false;
	 	}
	 	/*if(USB.m_nResponse[6] != (byte)m_nTimeInfoHigh)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[7] != (byte)m_nTimeInfoMid)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[8] != (byte)m_nTimeInfoLow)
	 	{
	 		return false;
	 	}*/
	 	return true;

	 }
	 
	 public boolean TimeInforSetting(int TimeInfoHigh,int TimeInfoMid, int TimeInfoLow)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_TIMEINFOR_SETTING,(byte)TimeInfoHigh,(byte)TimeInfoMid,(byte)TimeInfoLow);
	 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_TIMEINFOR_SETTING)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)m_nTimeInfoHigh)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)m_nTimeInfoMid)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)m_nTimeInfoLow)
	 	{
	 		return false;
	 	}

	 	return true;

	 }
	 
	 
	 /************* New Scan Mode Setting Function *****************/
	 public boolean NewScanModeSetting(int OpMode, int RepeatationNum, int TotalProjection_High, int TotalProjection_Low)
	 {
//		 SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	int DMNum = 0;
	 	if(m_nMixedFlag == (byte)Definition.MIXED_FREQUENCY)				// Mixed Mode
	 	{
	 		DMNum = 2;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	else											// Single Mode
	 	{
	 		DMNum = 0;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}

	 	USB.USBComm((byte)Definition.COMMAND_NEW_SCAN_MODE,(byte)OpMode,(byte)DMNum,(byte)TotalProjection_Low, (byte)TotalProjection_High,(byte) RepeatationNum);

	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_NEW_SCAN_MODE)
	 	{

	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)OpMode)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] !=(byte) DMNum)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte) TotalProjection_Low)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[4] != (byte) TotalProjection_High)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[5] != (byte)RepeatationNum)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 /************* Pipeline Scan Start Function *****************/
	 public boolean PipelineScanStart()
	 {
	 	byte[] Command = new byte[10];

	 	Command[0] =(byte) Definition.COMMAND_NEW_SCAN_START_STOP;
	 	Command[1] = 1;
	 	USB.USBComm2(10, 0, 0,Command);	// New Scan Start ( IMM Ready )
	 	
	 	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	
	 	Command[0] =(byte) Definition.COMMAND_COMM_SCAN_START_STOP;		
	 	Command[1] = 1;
	 	USB.USBComm2(10, 0, 0, Command);	// Comm Scan Start (Comm Sending Start Signal)

	 	return true;
	 	
	 }
	 
	 
	 /************* Thread communication Function 								//YEthread command
	 * @throws InterruptedException 
		 * @throws Fi leNotFoundException *****************/
	 /*
	 	int ThreadComm(S_EIT_Doc pDoc) throws InterruptedException 
		 {
			 pDoc.m_bThreadCommWorkingFlag = true;
			 	pDoc.m_nPresentScanNum = 0;
			 	int CurrentProjection = 0;
				int[] FreqtoCNTGap_data = new int[3];
				
			 	while (pDoc.m_bThreadCommWorkingFlag)
			 	{
						SiUSBXp.INSTANCE.SI_FlushBuffers(USB.dev_handle_ref.getValue(),(byte)0,(byte)0);
					 	int src = 0;
						int sink = 15;
						int vin1 = 0;  // Vin+
						int vin2 = 15;   // Vin-
						double[][] Real_Data = new double[16][16];				
						double[][] Quad_Data= new double[16][16];
						double[][] Real_Data_src = new double[16][16];
						double[][] Quad_Data_src= new double[16][16];
						int[][]   ChInfo= new int [16][16];
						int[][]  Overflow= new int [16][16];
						int InjCNTHigh =0;
						int InjCNTLow = 0;
						int InjGap = 0;
						
						for(int i=0;i<16;i++)
						{
							//if(src > 15) src = 0;
							//if(sink > 15) sink = 0;
							if(CurrentProjection>15) CurrentProjection = 0;
							for (int k=0;k<16;k++)
							{
								if (ProjectionTable[CurrentProjection][k][Definition.CHANNELCTRL] == 2)
								{src = k;}
								else if (ProjectionTable[CurrentProjection][k][Definition.CHANNELCTRL] == 3)
								{sink = k;}
								
							}
							
							
							if(!CCSAmp1Setting(0,ProjectionTable[CurrentProjection][src][Definition.AMP1_HIGH],ProjectionTable[CurrentProjection][src][Definition.AMP1_LOW]))
				 			{
				 				JOptionPane.showMessageDialog(null, "CCS Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 			}
				 			if(!CCSAmp2Setting(0,ProjectionTable[CurrentProjection][src][Definition.AMP2_HIGH],ProjectionTable[CurrentProjection][src][Definition.AMP2_LOW]))
				 			{
				 				JOptionPane.showMessageDialog(null, "CCS Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 			}		
				 			
				 			if(!CCSAmp1Setting(1,ProjectionTable[CurrentProjection][sink][Definition.AMP1_HIGH],ProjectionTable[CurrentProjection][src][Definition.AMP1_LOW]))
				 			{
				 				JOptionPane.showMessageDialog(null, "CCS Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 			}
				 			if(!CCSAmp2Setting(1,ProjectionTable[CurrentProjection][sink][Definition.AMP2_HIGH],ProjectionTable[CurrentProjection][src][Definition.AMP2_LOW]))
				 			{
				 				JOptionPane.showMessageDialog(null, "CCS Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 			}	
				 			
				 			if(!AcqFreqSetting(0,ProjectionTable[CurrentProjection][src][Definition.ACQUREFREQUENCY_GAP],ProjectionTable[CurrentProjection][src][Definition.ACQUREFREQUENCY_CNT_HIGH],ProjectionTable[CurrentProjection][src][Definition.ACQUREFREQUENCY_CNT_LOW]))
				 		 	{
				 				JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 		 	}
							if(!AcqFreqSetting(1,ProjectionTable[CurrentProjection][sink][Definition.ACQUREFREQUENCY_GAP],ProjectionTable[CurrentProjection][sink][Definition.ACQUREFREQUENCY_CNT_HIGH],ProjectionTable[CurrentProjection][sink][Definition.ACQUREFREQUENCY_CNT_LOW]))
				 		 	{
				 				JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				 				break;
				 		 	}
							
							if(!DMFreqSetting(0,ProjectionTable[CurrentProjection][src][Definition.TOTALDEMODULATIONFREQUENCY],ProjectionTable[CurrentProjection][src][Definition.DEMODULATIONFREQUENCY2],ProjectionTable[CurrentProjection][src][Definition.DEMODULATIONFREQUENCY3]))
						 	{
								JOptionPane.showMessageDialog(null, "Demodulation Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								break;
						 	}
							if(!DMFreqSetting(1,ProjectionTable[CurrentProjection][sink][Definition.TOTALDEMODULATIONFREQUENCY],ProjectionTable[CurrentProjection][sink][Definition.DEMODULATIONFREQUENCY2],ProjectionTable[CurrentProjection][sink][Definition.DEMODULATIONFREQUENCY3]))
						 	{
								JOptionPane.showMessageDialog(null, "Demodulation Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
								break;
						 	}

							for(int j=0;j<16;j++)
							{
								if(vin1 > 15) vin1 = 0;
								if(vin2 > 15) vin2 = 0;

								if(!VMDigi1Setting(1,ProjectionTable[CurrentProjection][j][Definition.GAIN1]))
						 		{
									JOptionPane.showMessageDialog(null, "VM1 Gain Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						 			break;
						 		}

								FreqtoCNTGap_data = FreqtoCNTGap(ProjectionTable[CurrentProjection][src][Definition.INJECTIONCURRENTFREQUENCY]);	//FreqIndex
				 				InjCNTHigh = FreqtoCNTGap_data[0];
				 				InjCNTLow = FreqtoCNTGap_data[1];
				 				InjGap= FreqtoCNTGap_data[2];	
				 				if(!InjFreqSetting(0,InjGap,InjCNTHigh,InjCNTLow))
					 			{
					 				JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					 				break;
					 			}
				 				
				 				FreqtoCNTGap_data = FreqtoCNTGap(ProjectionTable[CurrentProjection][sink][Definition.INJECTIONCURRENTFREQUENCY]);	//FreqIndex
				 				InjCNTHigh = FreqtoCNTGap_data[0];
				 				InjCNTLow = FreqtoCNTGap_data[1];
				 				InjGap= FreqtoCNTGap_data[2];	
				 				if(!InjFreqSetting(1,InjGap,InjCNTHigh,InjCNTLow))
					 			{
					 				JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					 				break;
					 			}
								
								if(!AcqFreqSetting(Definition.ALL_CH,ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_GAP],ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_CNT_HIGH],ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_CNT_LOW]))
					 		 	{
					 				JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					 				break;
					 		 	}
					 		 	
								
								
				 				USB.USBComm((byte)56);	// WG START
				 				
								if(!pDoc.MuxBoardControl(1,src,sink))
								{
									System.out.println("Injection error");
									break;
								}
								else
								{
									if(pDoc.VMControl(1, vin1, vin2))
									{
										if(!Serial_Projection())
										{
												JOptionPane.showMessageDialog(null, "Projection Time out Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
												return FALSE;
										}
										
										
										vin1++;
										vin2++;
										Overflow[i][j] = (pDoc.USB.m_nResponse[0]&0xff)-128;
										ChInfo[i][j] = ProjectionTable[CurrentProjection][j][Definition.CHANNELINDEX];
										Real_Data[i][j]=((pDoc.USB.m_nResponse[2]&0xff)*256)+((pDoc.USB.m_nResponse[3]&0xff));
										Quad_Data[i][j]=((pDoc.USB.m_nResponse[4]&0xff)*256)+((pDoc.USB.m_nResponse[5]&0xff));
										Real_Data_src[i][j]=((pDoc.USB.m_nResponse[6]&0xff)*256)+((pDoc.USB.m_nResponse[7]&0xff));
										Quad_Data_src[i][j]=((pDoc.USB.m_nResponse[8]&0xff)*256)+((pDoc.USB.m_nResponse[9]&0xff));

										if(Real_Data[i][j] > 32767)
										{
											Real_Data[i][j] = Real_Data[i][j] - 65536;
										}
										if(Quad_Data[i][j] > 32767)
										{
											Quad_Data[i][j] = Quad_Data[i][j] - 65536;
										}
										if(Real_Data_src[i][j] > 32767)
										{
											Real_Data_src[i][j] = Real_Data_src[i][j] - 65536;
										}
										if(Quad_Data_src[i][j] > 32767)
										{
											Quad_Data_src[i][j] = Quad_Data_src[i][j] - 65536;
										}
									}
								}
							}
							src++;
							sink++;
							CurrentProjection++;
						}
						
						int m=0;
						int Freq =0;
						int count = 0;
						for(int k = 0; k < 16; k++)
						{	
							for( m = 0; m < 16; m++)
							{
		 						pDoc.TempReal[Freq][count] = Real_Data[k][m];
		 						pDoc.TempQuad[Freq][count] = Quad_Data[k][m];
		 						pDoc.TempVMOverflow[Freq][count] = Overflow[k][m];
		 						pDoc.TempCh[Freq][count] = ChInfo[k][m];
		 						//pDoc.TempMagnitude[Freq][pDoc.m_nThreadRQDataIndex] = Math.sqrt(Math.pow((double)pDoc.TempReal[Freq][count],2)+Math.pow((double)pDoc.TempQuad[Freq][count],2));
								//pDoc.TempPhase[Freq][count] = Math.atan2((double)pDoc.TempQuad[Freq][count],(double)pDoc.TempReal[Freq][count]);
		 						count++;
							}
							m=0;
						}
						////////////////////RQ Data Write///////////////////////////
							pDoc.m_nPresentScanNum++;
//							raw_count = 0;
	 						pDoc.m_bThreadDataProcessFinishFlag = true;
	 						pDoc.m_bThreadDataProcessFinishFlag2 = true;
	 						pDoc.control_gui.UpdateScanNumber(pDoc.m_nPresentScanNum);  
							pDoc.m_nReadBlock = 0;
							pDoc.m_nWriteFileIndex = pDoc.m_nThreadRQDataIndex;
	 					if(pDoc.control_gui.m_bImageDlg1)   //////check when add image
	 					{
	 						if(pDoc.m_bMessageImageReconFinishFlag1 == true)
	 						{
	 							pDoc.control_gui.m_pImageDlg.ImageRecon();
	 						}
	 					}
	 					if(pDoc.control_gui.m_bNumofScans) 
	 			 		{ 
	 			 			if(pDoc.m_nPresentScanNum >= pDoc.control_gui.m_nNumofScans)
	 			 			{
	 			 				pDoc.control_gui.stopScan(); 
	 			 			}
	 			 		}
	 					}
						pDoc.m_nReadBlock = 0;
						pDoc.m_nThreadDataIndex = 0;
						pDoc.USB.m_bReadOK = FALSE;
	 	
				 		if(pDoc.control_gui.m_bNumofScans) 
				 		{ 
				 			if(pDoc.m_nPresentScanNum >= pDoc.control_gui.m_nNumofScans)
				 			{
				 				pDoc.control_gui.stopScan(); 
				 				
				 				control_gui.m_bNumofScans= false;  
				 			}
				 		}
						return TRUE;
		 	}
		 */
		 int ThreadComm(S_EIT_Doc pDoc) throws InterruptedException 
		 {
			{
				pDoc.m_bThreadCommWorkingFlag = true;
				while(pDoc.m_bThreadCommWorkingFlag)   /// stop in stop button
				{
					//System.out.println("pDoc.m_nTotalDataLength  "+ pDoc.m_nTotalDataLength );
					//pDoc.USB.ReadData3(pDoc.m_nTotalDataLength);
					pDoc.USB.ReadData3(pDoc.m_nTotalDataLength);
					//System.out.println("pDoc.m_nTotalDataLength  "+ pDoc.m_nTotalDataLength );

				}
				return TRUE;
			}
		 }
		
		 
		
	
	 /************* Thread Process Scan Data Function *****************/
	 int ThreadDataProcess(S_EIT_Doc pDoc)
	 {
		
	 	pDoc.m_bThreadDataProcessWorkingFlag = true;
	 	int ReadByte = 0;
	 	pDoc.m_nReadBlock = 0;
	 	pDoc.m_nThreadDataIndex = 0;

	 	pDoc.m_nTotalCh = Definition.MAX_CH; //16
	 	
	 	pDoc.m_nPresentScanNum = 0;
	 	int DummyScanNum = 0;
	 	pDoc.m_nDummyScanNum = 0;
	 	double TempReal, TempQuad;
	 	pDoc.m_nPresentWriteNum =0;
	 	
	 	int ch_num = 0;

	 	pDoc.m_strSaveFileName = pDoc.m_strSaveFilePath +"Scan.txt";
	 
	 	while(pDoc.m_bThreadDataProcessWorkingFlag)
	 	{
	 		//System.out.println("m_bReadOK"+pDoc.USB.m_bReadOK);
	 		if(pDoc.USB.m_bReadOK == TRUE )   ////// check 1 or TRUE 
	 		{
	 				ReadByte = pDoc.USB.m_nReadByte;
	 				if(pDoc.m_nPresentScanNum == pDoc.m_nPresentWriteNum)   ////// to force it to do not read twice
	 				for(int i = 0; i < ReadByte; i++)
	 				{ 
	 					pDoc.RawData[pDoc.m_nThreadDataIndex] = (pDoc.USB.m_nResponse[i]&0xFF);
	 					pDoc.m_nThreadDataIndex++;
	 				}
	 				else  //////////to prevent Stop
	 				{
	 					/*try {
							Thread.sleep(5);// with this time test one hour no missing just need to change receive buffer to make smooth display
							// we can make 7 read command or we can change buffer
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
	 					pDoc.m_nPresentScanNum = pDoc.m_nPresentWriteNum;           
	 				}
	 				
	 				
	 				pDoc.m_nReadBlock++;
	 				pDoc.m_nThreadRQDataIndex = 0;

	 				if (pDoc.control_gui.m_bioGraph)
	 				{

	 					if(m_bMessageGraphBio_SignalFinishFlag == true)
	 					{
			 				arrange_BioSignal_data(pDoc); // arrange the data
			 				//System.out.println("arrange    "+pDoc.m_nPresentScanNum);

	 						
	 					}
	 					if(timreflag)  // only to start the timer 
	 					{
	 						control_gui.BioSig_Dlg.GetBioSignalData();
	 						timreflag = false ;
	 					}
	 				}
	 				/*else 
	 					control_gui.BioSig_Dlg.timer_cancel();
	 				*/	
	 					
	 	
 					pDoc.m_bThreadDataProcessFinishFlag = true;
	 				pDoc.m_nPresentScanNum++;
	 				pDoc.control_gui.UpdateScanNumber(pDoc.m_nPresentScanNum);  ////check
	 				pDoc.m_nReadBlock = 0;
	 				pDoc.m_nWriteFileIndex = pDoc.m_nThreadRQDataIndex;
	 				pDoc.m_nThreadDataIndex = 0;
	 				pDoc.USB.m_bReadOK = FALSE;
	 				
	 				
	 		}

	 		if(pDoc.control_gui.m_bNumofScans) 
	 		{ 
	 			if(pDoc.m_nPresentScanNum >= pDoc.control_gui.m_nNumofScans)
	 			{
	 				pDoc.control_gui.stopScan(); 
	 			}
	 		}

	 	}
			
	 	return TRUE;
	 }
	 

	 void arrange_IMM_data(S_EIT_Doc pDoc)
	 {
		 int half_range = Definition.Fab_All_Data/2;
		 int[][] temp_Of = new int[3][Definition.Fab_All_Data];
		 double[][] temp_R = new double[3][Definition.Fab_All_Data];
		 double[][] temp_Q = new double[3][Definition.Fab_All_Data];
		 double[][] temp_M = new double[3][Definition.Fab_All_Data];
		 double[][] temp_ph = new double[3][Definition.Fab_All_Data];
		 
		 int counter = 0 ;
		 int IMM_data_size = 16;
		 for (int i = 0 ; i < (half_range ) ; i = i+IMM_data_size)
		 {
			 System.out.println(i);
			 for(int j = i ; j < (i + IMM_data_size) ;j++)
			 {
				 temp_Of[0][counter] = pDoc.TempVMOverflow[0][j];
				 temp_R[0][counter] = pDoc.TempReal[0][j];
				 temp_Q[0][counter] = pDoc.TempQuad[0][j];
				 temp_M[0][counter] = pDoc.TempMagnitude[0][j];
				 temp_ph[0][counter] = pDoc.TempPhase[0][j];
				 counter++;
			 }
			 for (int k = (i + half_range); k < (i + half_range+IMM_data_size); k++) 
			 {
				 temp_Of[0][counter] = pDoc.TempVMOverflow[0][k];
				 temp_R[0][counter] = pDoc.TempReal[0][k];
				 temp_Q[0][counter] = pDoc.TempQuad[0][k];
				 temp_M[0][counter] = pDoc.TempMagnitude[0][k];
				 temp_ph[0][counter] = pDoc.TempPhase[0][k];
				 counter++;
			 }
		 }
		 pDoc.TempVMOverflow = temp_Of;
		 pDoc.TempReal =  temp_R ;
		 pDoc.TempQuad = temp_Q;
		 pDoc.TempMagnitude = temp_M;
		 pDoc.TempPhase = temp_ph;
	 }
	 void arrange_BioSignal_data(S_EIT_Doc pDoc)
	 {
		 int data_index = 0;

		 for(int i =0 ; i < 800 ;i+=16)
		 {
			 header[data_index] =  pDoc.RawData[i];
			 temp_External_trigger[data_index] = pDoc.RawData[(i) + 1];
			 temp_Gyro_Z[data_index] = pDoc.RawData[(i) + 2] + (pDoc.RawData[(i) + 3] * 256);
			 temp_Gyro_Y[data_index] = pDoc.RawData[(i) + 4] + (pDoc.RawData[(i) + 5] * 256);
			 temp_Gyro_X[data_index] = pDoc.RawData[(i) + 6] + (pDoc.RawData[(i) + 7] * 256);
			 temp_Tempreture[data_index] =  pDoc.RawData[(i) + 8] + (pDoc.RawData[(i) + 9] * 256);
			 temp_Accelometer_Z[data_index] = pDoc.RawData[(i) + 10] + (pDoc.RawData[(i) + 11] * 256);
			 temp_Accelometer_Y[data_index] = pDoc.RawData[(i) + 12] + (pDoc.RawData[(i) + 13] * 256);
			 temp_Accelometer_X[data_index] = pDoc.RawData[(i) + 14] + (pDoc.RawData[(i) + 15] * 256);
			 if( header[data_index] > 32767)
			 {
				 header[data_index] =  header[data_index] - 65536;
			 }
				 if( temp_External_trigger[data_index] > 32767)
				 {
					 External_trigger[data_index] =  temp_External_trigger[data_index] - 65536;
				 }
				 else
				 {
					 External_trigger[data_index] = temp_External_trigger[data_index] ;
				 }
			 
			 if( temp_Gyro_Z[data_index] > 32767)
			 {
				 Gyro_Z[data_index] =  temp_Gyro_Z[data_index] - 65536;
			 }
			 else
			 {
				 Gyro_Z[data_index] = temp_Gyro_Z[data_index] ;
			 }
				 
				 if( temp_Gyro_Y[data_index] > 32767)
				 {
					 Gyro_Y[data_index] =  temp_Gyro_Y[data_index] - 65536;
				 }
				 else
				 {
					 Gyro_Y[data_index] = temp_Gyro_Y[data_index] ;
				 }
			 
			 if( temp_Gyro_X[data_index] > 32767)
			 {
				 Gyro_X[data_index] =  temp_Gyro_X[data_index] - 65536;
			 }
			 else
			 {
				 Gyro_X[data_index] = temp_Gyro_X[data_index] ;
			 }
				 if( temp_Tempreture[data_index] > 32767)
				 {
					 temp_Tempreture[data_index] =  temp_Tempreture[data_index] - 65536;
				 }
			 
			 if( temp_Accelometer_Z[data_index] > 32767)
			 {
				 temp_Accelometer_Z[data_index] =  temp_Accelometer_Z[data_index] - 65536;
			 }
			 
				 if( temp_Accelometer_Y[data_index] > 32767)
				 {
					 Accelometer_Y[data_index] =  temp_Accelometer_Y[data_index] - 65536;
				 }
				 else
				 {
					 Accelometer_Y[data_index] = temp_Accelometer_Y[data_index] ;
				 }
			 
			 if( temp_Accelometer_X[data_index] > 32767)
			 {
				 Accelometer_X[data_index] =  temp_Accelometer_X[data_index] - 65536;
			 }
			 else
			 {
				 Accelometer_X[data_index] = temp_Accelometer_X[data_index] ;
			 }
			 
				 if( temp_Accelometer_Z[data_index] > 0)
				 {
					 Accelometer_Z[data_index] = temp_Accelometer_Z[data_index] - 16384;
				 }
				 else
				 {
					 Accelometer_Z[data_index] =  temp_Accelometer_Z[data_index] + 16384;
				 }
			 Tempreture[data_index] =  (((temp_Tempreture[data_index]) -21)/333.87)+21;
			 data_index++;
		 }
		
	 } 
	 
	 
	 /************* Thread Write Scan File Function *****************/
/*	 int ThreadWriteFile(S_EIT_Doc pDoc) throws IOException
	 {	
	 	FileWriter fout = null;
	 	FileWriter raw_file = null;

	 	while(pDoc.m_bThreadWriteFileWorkingFlag)
	 	{ 
	 		pDoc.WriteScanNum++ ;
	 		if(pDoc.m_bThreadDataProcessFinishFlag == true)
	 		{
	 			if(pDoc.m_bSave)
	 			{	//System.out.println("inside  flag save Data write thread");
	 				//for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)
	 				{int Freq = 0;
	 					for(int i = 0; i < 256; i++)  ////////////////////////////for(int i = 0; i < pDoc.m_nWriteFileIndex; i++)
	 					{//System.out.println("inside  2 save Data write thread");
	 						pDoc.Real[Freq][i] = -1* pDoc.TempReal[Freq][i];
	 						pDoc.Quad[Freq][i] = -1*pDoc.TempQuad[Freq][i];
	 						pDoc.VMOverflow[Freq][i] = pDoc.TempVMOverflow[Freq][i];
	 						pDoc.ChNum[Freq][i] = pDoc.TempCh[Freq][i];
	 					}
	 				}
	 				////////////////////RQ Data Write///////////////////////////
	 				pDoc.m_strSaveFileName = pDoc.m_strSaveFilePath +pDoc.m_nPresentScanNum+"Scan.txt";
	 				fout = new FileWriter(pDoc.m_strSaveFileName);
	 				//for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)  
	 				{	int Freq = 0;
	 					for(int i = 0; i < 256; i++)////////////////////////////for(int i = 0; i < pDoc.m_nThreadRQDataIndex; i++) ///m_nThreadRQDataIndex 16*16=256
	 					{//System.out.println("inside "+m_strSaveFileName +"data   "+i);
	 						fout.write((pDoc.ChNum[Freq][i])+"\t"+(pDoc.VMOverflow[Freq][i])+"\t"+ pDoc.Real[Freq][i]+"\t"+pDoc.Quad[Freq][i]);
	 						fout.write("\r\n");
	 						//System.out.println("write"+ pDoc.Real[Freq][i]);
	 					}
	 				}
	 				fout.close();
	 		//		String RawFileName = pDoc.m_strSaveFilePath +pDoc.m_nPresentScanNum+"raw.txt";
	 		//		raw_file = new FileWriter(RawFileName);
	 		//		for (int row_c =0 ; row_c<1536;row_c++)
	 		//		{
	 		//			raw_file.write(Raw_Data[row_c]+"\r\n");
	 		//		}
	 		//		raw_file.close();
	 			}
	 				pDoc.m_bThreadDataProcessFinishFlag = false;
	 			}
	 		}
	 	
	 	return TRUE;
	 }
*/
	 
	 /////////////////////////////////////////////////////////////////
	 int ThreadWriteFile(S_EIT_Doc pDoc) throws IOException
	 {	
	 	FileWriter fout = null;
	 	/*FileWriter fout_source = null;*/
	 	//FileWriter raw_file = null;
	 	while(pDoc.m_bThreadWriteFileWorkingFlag)
	 	{ 
	 		if(pDoc.m_bThreadDataProcessFinishFlag == true)
	 		{	//System.out.println("inside  1 Data write thread");
	 			if(pDoc.m_bSave)
	 			{			
	 				pDoc.m_strSaveFileName = pDoc.m_strSaveFilePath +pDoc.m_nPresentScanNum+"Scan.txt";
	 				fout = new FileWriter(pDoc.m_strSaveFileName);
	 				
	 				for (int row_c =0 ; row_c<800;row_c++)
	 			 	{
	 					fout.write(pDoc.RawData[row_c]+"\r\n");
	 			 	}

	 				fout.close();
	 			
	 			}
	 			pDoc.m_nPresentWriteNum++;
	 			pDoc.m_bThreadDataProcessFinishFlag = false;
	 			}
	 				 	 
	 		}
	 	
	 	return TRUE;
	 }
	 
	 
	 
	 
	 int ThreadReciprocityError(S_EIT_Doc pDoc) throws IOException
	 {
				
	 	double [][] tempRec_Error = null;
	 	double [] tempRec_Error2 = null;
	 	while(pDoc.m_bThreadRecipErrorWorkingFlag)
	 	{ 
	 		if(pDoc.m_bThreadDataProcessFinishFlag2 == true)
	 		{	
	 			{	
	 			pDoc.m_strSaveFileName = pDoc.m_strSaveFilePath +pDoc.m_nPresentScanNum+"RecError.txt";
	 			tempRec_Error= new double[256][256];
	 			tempRec_Error2= new double[256];
	 		
	 			for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)  
	 				{	
	 				for(int i = 0; i < pDoc.m_nThreadRQDataIndex; i++) ///m_nThreadRQDataIndex 16*16=256
	 					{
	 							tempRec_Error2[i]= pDoc.TempMagnitude[Freq][i];		
	 	
	 					}
	 				}
	 		
	 			}
	 			for(int i =0 ;i< 16 ; i++)
	 				{for(int j =0; j< 16 ; j++)
	 				{
	 					tempRec_Error[i][j]=tempRec_Error2[(j*16) + i];
	 				
	 					double l = (j-i)% 16 ;
						if (l<0)
							l=l+16;
						if(l==1)
						{
							Zc[i] = ( tempRec_Error[i][j]*2)/(Math.pow(2, 15)*(Temp_d_Amp*Math.pow(10, -6)));   /// *2 ADC signal peak to peak ...convert to volt pow(2, 16)... /1000000 convert to Amp .... Round(zc*100)/100 to number after dot
							//System.out.println(i +">>>>>>"+tempRec_Error[i][j]);
						
						}
	 				}
	 				
	 				}
	 			
	 		    Rec_Error_val_temp = 0;
	 			Rec_Error_val = 0;
	 			

	 			for(int i = 0; i < 16; i++) ///m_nThreadRQDataIndex 16*16=256
					{//check i
						for (int j = i+1; j < 16; j++)
						{
					
								double l = (j-i)% 16 ;
								if (l<0)
									l=l+16;
								if(l!=0 && l!=1 && l!= (16-1))  // Remove saturation data  0 itself//1 Next //16-1 previous
								{
									Rec_Error_val_temp += (Math.abs(tempRec_Error[i][j]-tempRec_Error[j][i])/((tempRec_Error[i][j]+tempRec_Error[j][i])/2 ))* 100 ;
									//System.out.println(Rec_Error_val_temp);
									
								}
						}
						
					}
	 		
	 			Rec_Error_val = Rec_Error_val_temp/104;
	 			/*if(pDoc.control_gui.m_bGraphDlg1)   //////check when to add graph
					{
						if(pDoc.m_bMessageGraphReconFinishFlag == true)
						{
							pDoc.control_gui.m_pCont_Imp_GraphDlg.GetRecipError();
						}
						if(pDoc.m_bMessageGraphContactImpedanceFinishFlag == true)
						{
							pDoc.control_gui.m_pCont_Imp_GraphDlg.GetContactImpedance();
						}

					}*/
	 				pDoc.m_bThreadDataProcessFinishFlag2 = false;
	 			}
	 		}
	 	

	 	return TRUE;
		
	 }

	 /************* Stop Scan Function *****************/
	 boolean EIT_PipelineScanStop() throws InterruptedException
	 {

		Accelometer_X = new double[50];
		Accelometer_Y = new double[50];
		Accelometer_Z = new double[50];
		Gyro_X = new double[50];
		Gyro_Y = new double[50];
		Gyro_Z = new double[50];
		header = new double[50];
		External_trigger = new double[50];
		Tempreture = new double[50];
		
	 	m_bThreadDataProcessWorkingFlag = false;
	 	m_bThreadWriteFileWorkingFlag = false;
	 	//m_bThreadRecipErrorWorkingFlag = false;
	 	m_bThreadCommWorkingFlag = false;
	 	USB.m_bBusy = FALSE;
	 	USB.m_bCommBreak = TRUE;

	
	 	if(m_hThreadDataProcess.isAlive())
	 	{
	 		m_bThreadDataProcessWorkingFlag = false;
	 		Thread.sleep(100);
	 	}

	
	 	if(m_hThreadWriteFile.isAlive())
	 	{
	 		m_bThreadWriteFileWorkingFlag = false;
	 		Thread.sleep(100);
	 	}
	 	
	 	/*if(m_hThreadCalRecipError.isAlive())
	 	{
	 		m_bThreadRecipErrorWorkingFlag = false;
	 		Thread.sleep(100);
	 	}
*/
	 	if(m_hThreadComm.isAlive())
	 	{
	 		m_bThreadCommWorkingFlag = false;
	 		USB.m_bBusy = FALSE;
	 		USB.m_bCommBreak = TRUE;
	 	}
	 	Thread.sleep(100);
	 	
	 	
	 	//PipelineScanStop();		// Stop : 0
	 	//Reset();
	    SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	USB.m_bCommBreak = FALSE;
	 	PipelineScanStop();		// Stop : 0
		SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);

	 	return true;
	 }
	 
	 /************* Stop Pipeline Command Function *****************/
	 boolean PipelineScanStop()
	 {
		 byte[]  Command = new byte[10];//check why 9 
		 Command[0] = (byte)Definition.COMMAND_COMM_SCAN_START_STOP;  
		 Command[1] = 0;
		 USB.USBComm2(10, 0, 0, Command);	// Comm Scan Start (Comm Sending Start Signal)
		 SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
		 return true;
	 }
	 
	 
	 /************* Load Image Algorithm File Function 
	 * @throws IOException *****************/
	 boolean IMAGE_LoadAlgorithmFile(int toCH) throws IOException
	 {
	 	
	 	String FilePath, AlgorithmName;
	 	AlgorithmName = "Linear";
	 	FilePath = m_strDefaultFilePath + "/" + "Algorithm";
		 FloatBuffer sin_file = null;
			 FloatBuffer sind_file= null;
			 IntBuffer tri_file= null;
	 
	     switch(toCH)
	 	{
	 	
	 	case 16:
	 		 numtriangle=64; numSencol=256; sqgrid=20;
	 		 resol = sqgrid*mulresol;
			  try( FileChannel s_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "Sen16.bin", "r").getChannel())
			  {
				  sin_file = s_file.map(MapMode.READ_ONLY, 0, s_file.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			  }
			 try(  FileChannel sd_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "Sen16d.bin", "r").getChannel()) 
			 {
				 sind_file = sd_file.map(MapMode.READ_ONLY, 0, sd_file.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			 }
			 try(FileChannel t_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "trinum16.bin", "r").getChannel()) 
			 {
				 tri_file = t_file.map(MapMode.READ_ONLY, 0, t_file.size()).order(ByteOrder.nativeOrder()).asIntBuffer();
			 }

		     
		 	
	 		break;
	 	case 32:
	 		 numtriangle=258; numSencol=1024; sqgrid=24;
	 		 resol = sqgrid*mulresol;
	 		try( FileChannel s_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "Sen32.bin", "r").getChannel())
			  {
				  sin_file = s_file.map(MapMode.READ_ONLY, 0, s_file.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			  }
			 try(  FileChannel sd_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "Sen32d.bin", "r").getChannel()) 
			 {
				 sind_file = sd_file.map(MapMode.READ_ONLY, 0, sd_file.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			 }
			 try(FileChannel t_file = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "trinum32.bin", "r").getChannel()) 
			 {
				 tri_file = t_file.map(MapMode.READ_ONLY, 0, t_file.size()).order(ByteOrder.nativeOrder()).asIntBuffer();
			 }

		 	
	 		break;
	 	}

	    
		
	 		for(int i =0 ;i< numtriangle*numSencol;i++) 
	 			Sen[i] = sin_file.get(i);
	 			
	 		for(int i =0 ;i< sqgrid*sqgrid;i++) 
	 			trinum[i]= tri_file.get(i)-1; 

	 		for(int i =0 ;i< numtriangle*numSencol;i++) 
	 			Sen_d[i] = sind_file.get(i);
	 			


	/* 	File finDipolX_f,finDipolY_f,finDipolIndex_f;
	 	AlgorithmName = "Factorarization";
	 	finDipolX_f = new File(FilePath + "/" + AlgorithmName + "/" + "Dipolx16.txt");
	 	finDipolY_f = new File(FilePath + "/" + AlgorithmName + "/" + "Dipoly16.txt");
	 	finDipolIndex_f = new File(FilePath + "/" + AlgorithmName + "/" + "DipolIndex.txt");
	 	Scanner finDipolX = new Scanner(finDipolX_f);
	 	Scanner finDipolY = new Scanner(finDipolY_f);
	 	Scanner finDipolIndex = new Scanner(finDipolIndex_f);
	 	
	 	for(int i = 0; i < 1600; i++)
	 	{
	 		 DipolIndex[i]=finDipolIndex.nextInt();  // read file and put in array
	 	}
	 	finDipolIndex.close();

	 	for(int i = 0; i < 16; i++)
	 	{
	 		for(int j = 0; j <1184;	j++)
	 		{
	 			DipolX[i][j] = finDipolX.nextDouble();   //check way of reading
	 			DipolY[i][j] = finDipolY.nextDouble(); 
	 		}
	 	}	
	 	finDipolX.close();
	 	finDipolY.close();*/

	
	 	return true;
	 }
	 /************* Process Delta Sigma Function *****************/
	 boolean IMAGE_DispProc(int DispData,int toCH, int Algorithm)
	 {
	 	switch (Algorithm)
	 	{
	 	/*case Definition.ALGORITHM_tSVD:
	 		switch(DispData)
	 		{
	 		case Definition.IMAGE_MAGNITUDE:
	 			Image_dsigma = IMAGE_CalcDSigma(toCH, Image_RefMag, Image_ObjMag);
	 			break;
	 		case Definition.IMAGE_PHASE:
	 			Image_dsigma = IMAGE_CalcDSigma(toCH, Image_RefTheta, Image_ObjTheta);
	 			break;
	 		case Definition.IMAGE_REAL:
	 			Image_dsigma = IMAGE_CalcDSigma(toCH, Image_RefReal, Image_ObjReal);
	 			break;
	 		case Definition.IMAGE_IMAGINARY:
	 			Image_dsigma = IMAGE_CalcDSigma(toCH, Image_RefQuad, Image_ObjQuad);
	 			break;			
	 		}
	 		break;

	 	*/
	 	case Definition.ALGORITHM_Fabric_Adj:
	 		switch (DispData)
	 		{
	 		case Definition.IMAGE_MAGNITUDE:
	 			Image_dsigma = Fabric_IMAGE_GREIT(toCH, Image_RefMag, Image_ObjMag);
	 			break;
	 		
	 		}
	 		break;
	 	case Definition.ALGORITHM_GREIT:
	 		switch (DispData)
	 		{
	 		case Definition.IMAGE_MAGNITUDE:
	 			Image_dsigma = GREITAlgorithm(toCH, Image_RefMag, Image_ObjMag);
	 			break;
	 		case Definition.IMAGE_PHASE:
	 			Image_dsigma= GREITAlgorithm(toCH, Image_RefTheta, Image_ObjTheta);
	 			break;
	 		case Definition.IMAGE_REAL:
	 			Image_dsigma= GREITAlgorithm(toCH, Image_RefReal, Image_ObjReal);
	 			break;
	 		case Definition.IMAGE_IMAGINARY:
	 			Image_dsigma  = GREITAlgorithm(toCH, Image_RefQuad, Image_ObjQuad);
	 			break;	
	 		}
	 	
	 		break;
	 		
	 		
	 	}
	 	return true;

	 }
	 
	 /************* Calculate Delta Sigma Function *****************/
	 double[] IMAGE_CalcDSigma(int ch, double[] ref_dvol, double[] obj_dvol )  //check ref data , obj_dvol from image dialog get measured data
	{
		double dsigma[] = new  double[1024*2];
		int i,k,l, j, ii,jj, ki,kj,kk,k1,k2,iii,jjj,kkk;
		double[] tri_dsigma= new double[258] ;
		double[] dif = new 	double[1024];
		double[] adif = new double[1024];
		int[] onoff = new int [24*24];
		int[]regi = new int[24*24];
		double val;
		double[] tdsigma = new double[24*24];
		int method = 0; // for neighboring injection method 
		switch (method)
		{
		case 0:
			for (k=0 ; k<numSencol;k++ )
			{
				dif[k]=(obj_dvol[k]-ref_dvol[k])/200;    // calculate difference between measurement and reference //200 scalar
			}
			j=0;
			jj=0;
			for(i =0 ;i< ch;i++)
			{
				for(k=0;k< ch;k++)
				{
					adif[jj]=0;
					l = (k-i)% ch ;
					if (l<0)
						l=l+ch;
					if(l!=0 && l!=1 && l!= (ch-1)) // remove 3 saturation channel 
					{
						adif[j]= dif[jj]; // data without saturation channel 208 data j = 208
						j++;
					}
					jj++;
				}
			}
			j=0;
			for (i=0;i<numtriangle;i++)
			{
				tri_dsigma[i]=0;
				for (k=0; k< numSencol;k++)
				{
					tri_dsigma[i]= tri_dsigma[i]+Sen[j]*adif[k];   //General Equation 
					j++;
				}
			}
			for(k=0; k <numtriangle;k++)  //  why again ??
			{
				dif[k]=(obj_dvol[k]-ref_dvol[k])/200;    // calculate difference between measurement and reference //200 scalar
			}
			break;
			
		case 1:
			for(k=0; k<numSencol; k++) 
				dif[k]=obj_dvol[k]-ref_dvol[k];
			for(i=0; i < ch; i++)
			{ 
				for(j=1; j<=ch/2; j++)
				{
					k=i*ch+i+1+j;		// Diagonal Index
					dif[k]=-dif[k];   // data without saturation channel 
				} 
			}
			j=0;
			jj=0;
			for(i=0; i<ch; i++) 
			{
				for(k=0; k<ch; k++)
				{
					adif[jj]=0;
					l=(k-i)%ch;
					if(l<0) l=l+ch;
					if(l!=0 && l!=1 && l!=ch/2 && l!=ch/2+1)
					{	
						adif[j]=dif[jj];
						j++;	
					}
					jj++;
				}
			}
			j=0;
			for(i=0;i<numtriangle;i++)
			{
				tri_dsigma[i]=0;
				for(k=0; k<numSencol; k++)
				{
					tri_dsigma[i]=tri_dsigma[i]+Sen_d[j]*adif[k];		 //General Equation 	  
					j++;
				}
			}
			break;		
		} //finish switch
		int nsqgridSquareSize,dsqgridSquareSize;
		nsqgridSquareSize = sqgrid*sqgrid;   //check 12800 size of int 32 should 1600
		dsqgridSquareSize = sqgrid*sqgrid; //25600  should 3200
		for(int index =0 ; index<dsqgridSquareSize;index++)
			tdsigma[index]=0;
		for(int index =0 ; index<nsqgridSquareSize;index++)
			onoff[index]=0;
		for(int index =0 ; index<nsqgridSquareSize;index++)
			regi[index]=0;
		
		
		for(i=0; i<sqgrid*sqgrid; i++)
		{
			if(trinum[i] >=0 )
			{
				ii=i/sqgrid+1;      // i > 20 enter to next for loop
				jj=i-(ii-1)*sqgrid+1;  // jj refer  to row in square grid 
				val=tri_dsigma[trinum[i]];
				onoff[i]=1;
				if (ii>1 && ii < sqgrid && jj < sqgrid) 
				{// get square info from triangle data so we need index for this triangle array 
					for(k1=-1; k1<=1; k1++)
					{
						for(k2=-1; k2<=1; k2++)
						{
							//  chose one square grid then add data to all neighbor of this square 
							ki=ii+k1;
							kj=jj+k2;
							kk=(ki-1)*sqgrid+kj-1;        // convert matrix index to array index  kk:1:400
							tdsigma[kk]=tdsigma[kk]+val; // kk array index  //tdsigma add for all area
							regi[kk]=regi[kk]+1;		// counter of how many times we add data to this square depend on its position Example: "4 corner ,6 edge,9 center"
						}
					}
				}
			}
		}
		
		for(i=0; i<sqgrid*sqgrid; i++)
		{
			if(trinum[i] >=0 )
				tdsigma[i] = tdsigma[i]/regi[i];   // calculate average data in each square
			else
				tdsigma[i]=0;		  
		}
		
		int resolSquareSize;
		resol = 40;  ////////////// check
		resolSquareSize = resol*resol;
		for(int index =0 ; index<resolSquareSize;index++)
			dsigma[index]=0;
		for(i=0; i<resol*resol; i++)   // extend the 20*20 to 40*40 and do the same thing again "like smoothing filter"
		{
			ii=i/resol+1; iii=(ii-1)/mulresol+1;
			jj=i-(ii-1)*resol+1; jjj=(jj-1)/mulresol+1;
			kkk=(iii-1)*sqgrid+jjj-1;
			trinum2[i]=trinum[kkk];
			val=tdsigma[kkk];
			if(ii>1 && ii<resol && jj>1 && jj<resol)
			{
				for(k1=-1; k1<=1; k1++)
				{
					for(k2=-1; k2<=1; k2++)
					{
						ki=ii+k1;
						kj=jj+k2;
						kk=(ki-1)*resol+kj-1;
						dsigma[kk]=dsigma[kk]+val;
					}
				}
			}
		}
	// depend on rest of the code may need to return this arrays
		return dsigma;
	}
		 
	 /************* Load GREIT Algorithm Files Function *****************/
	 boolean LoadGREITAlgorithmFile()
	 {
		 String FilePath;
		 	
		 	FilePath = m_strDefaultFilePath + "/Algorithm/GREIT/RMatrix.txt";
		 	File f_RMatrix = new File(FilePath);
		 	Scanner finRMatrix;
			try {
				finRMatrix = new Scanner(f_RMatrix);
				for(int i = 0; i < GREIT_TetrahedralMeshNum; i++)
			 	{
			 		for(int j = 0; j < GREIT_DataIndex; j++)
			 		{
			 			 GREIT_RMatrix[i][j] = finRMatrix.nextDouble();
			 		}
			 	}
			 	finRMatrix.close();
			 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
	 }
	 
	 /************* Load GREIT Remove Data File Function *****************/
	 boolean LoadGREITRemoveData()
	 {
		 String FilePath;
		 	
		 	FilePath = m_strDefaultFilePath + "/Algorithm/GREIT/RemoveIndex.txt";
		 	File f_RemoveIndex = new File(FilePath);
		 	Scanner finRemoveIndex;
			try {
				finRemoveIndex = new Scanner(f_RemoveIndex);
				for(int i = 0; i < 96; i++)
			 	{
			 			GREIT_RemoveIndex[i] = finRemoveIndex.nextInt(); //check int
			 		
			 	}
				finRemoveIndex.close();
			 
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	 	return true;
	 }

	 /************* Load GREIT Map Data File Function *****************/
	 boolean LoadGREITMap()
	 {
		 String FilePath;
		 	FilePath = m_strDefaultFilePath + "/Algorithm/GREIT/Map.txt";
			File f_Map = new File(FilePath);
		 	Scanner finMap;
			try {
				finMap = new Scanner(f_Map);
				for(int i = 0; i < 48*48; i++)  //change 
			 	{
					GREIT_Map[i] = finMap.nextInt(); //check int
			 		
			 	}
				finMap.close();
			 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		 	return true;
	 }

	 
	 /************* GREIT Algorithm Function *****************/
	 double[] GREITAlgorithm(int TotalCh, double Ref[], double []Obj)//, double[] ImageData) //check ref ImageData may needed to return
	 {
		 int Index,RemoveIndex;
		 	double Temp;
		 	double[] ImageData = new double[Definition.Fab_All_Data*2];
		 	double[] Diff = new double[928];
		 	Index = 0;
		 	RemoveIndex = 0;
		 	Temp = 0;
		 	for(int i = 0; i < Definition.Fab_All_Data; i++)
		 	{
		 		if(GREIT_RemoveIndex[RemoveIndex] - 1 != i)  //  it start from 0 to remove saturation channel 
		 		{
		 			GREIT_Reference[Index] = Ref[i];
		 			GREIT_Pertb[Index] = Obj[i];
		 			Diff[Index] = GREIT_Pertb[Index] - GREIT_Reference[Index];
		 			Index++;
		 		}
		 		else
		 		{
		 			RemoveIndex++;
		 		}
		 	}
		 	
		 	for(int i = 0; i < GREIT_TetrahedralMeshNum; i++)
		 	{
		 		Temp = 0;
		 		for(int j = 0; j < GREIT_DataIndex; j++)
		 		{
		 			Temp += GREIT_RMatrix[i][j] * Diff[j];   //// matrix multiplication
		 		}
		 		ImageData[i] = Temp;
		 	}

		 	return ImageData; 
	 }
	 
	 public  double[] Fabric_IMAGE_GREIT(int TotalCh, double Ref[], double []Obj)  //check ref data
	 {
		 
		
	 	 String str3 = m_strSaveFilePath + "matrix.txt";
		 int Index,RemoveIndex;
		 	double Temp;
		 //	double[] ImageData = new double[1024*2];
		 	double[] ImageData = new double[Fab_GREIT_TetrahedralMeshNum];

		 	double[] Diff = new double[Definition.Fab_All_Data_NO_Saturation];
		 	Index = 0;
		 	RemoveIndex = 0;
		 	Temp = 0;
		 	for(int i = 0; i < Definition.Fab_All_Data; i++)
		 	{
		 		if(Fab_GREIT_RemoveIndex[RemoveIndex] - 1 != i)
		 		{
		 			Fab_GREIT_Reference[Index] = Ref[i];
		 			Fab_GREIT_Pertb[Index] = Obj[i];
		 			Diff[Index] = Fab_GREIT_Pertb[Index] - Fab_GREIT_Reference[Index];
		 			/*if(snr_enable)
		 			{
		 				snr_mask[Index] = tmp_snr_mask[i];   // snr mask for saturation
			 			System.out.println("snr_mask  " +snr_mask[Index]);

		 				
		 			}*/

		 			Index++;
		 		}
		 		else
		 		{
		 			RemoveIndex++;
		 		}
		 		
		 		
		 		
		 	}
		 	for(int i = 0; i < Fab_GREIT_TetrahedralMeshNum; i++)
		 	{
		 		Temp = 0;
			 	int snrRemoveIndex = 0;
		 		for(int j = 0; j < Fab_GREIT_DataIndex; j++)  //870
		 		{
		 			// SNR
		 			if(snr_enable)
		 			{
		 				//System.out.println("Scanmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  ");
		 				if (tmp_snr_mask[snrRemoveIndex]==j)
			 			{
			 				Fab_GREIT_RMatrix[i][j] = 0;
			 				snrRemoveIndex++;
			 			}
		 			}
		 			
		 			Temp += Fab_GREIT_RMatrix[i][j] * Diff[j];
		 			
		 		}
		 		
		 		ImageData[i] = Temp;
		 	}
		 	snr_enable = false;
		 	
		 	return ImageData;  //check
		}
	 
	 
	 boolean LoadFabGREITRemoveData()
	 {
		 String FilePath;
		 	
		 	FilePath = m_strDefaultFilePath + "/Algorithm/GREIT/Fabric/Fab_Mask_ch32.txt";
		 	File f_RemoveIndex = new File(FilePath);
		 	Scanner finRemoveIndex;
			try {
				finRemoveIndex = new Scanner(f_RemoveIndex);
				for(int i = 0; i < Definition.Fab_NUM_Saturation; i++)
			 	{
			 			Fab_GREIT_RemoveIndex[i] = finRemoveIndex.nextInt(); //check int
			 		
			 	}
				finRemoveIndex.close();
			 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	 	return true;
	 }
	 boolean LoadFabGREITAlgorithmFile()
	 {
		 FloatBuffer sin_file = null;
		 String FilePath;
		 FilePath = m_strDefaultFilePath + "/Algorithm/GREIT/Fabric/RM_Fabric32.bin";
		 int counter = 0 ;
		 
		 try( FileChannel s_file = new RandomAccessFile(FilePath, "r").getChannel())
		  {
			  sin_file = s_file.map(MapMode.READ_ONLY, 0, s_file.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			  for(int i = 0; i < Fab_GREIT_DataIndex; i++)
			 	{
			 		for(int j = 0; j < Fab_GREIT_TetrahedralMeshNum; j++)
			 		{
			 			 Fab_GREIT_RMatrix[j][i] = sin_file.get(counter );
			 			counter++;
			 		}
			 	}		 			
		 	s_file.close();
		  } 
		 
		 
		 catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    	return true;
	 }
	 
	 public void getSNRdata(S_EIT_Doc doc) {
		 if(doc.m_nPresentScanNum > 5 && doc.m_nPresentScanNum < 26)
		 {
		 snr_rawdata[snr_count] = doc.TempMagnitude[0];   // check 
			 snr_count ++;
			 
		 }
		 if(snr_count == 20)
		 {
			 calculate_SNR();
			 snr_count  =0 ;
		 }
		
	}
	 public void calculate_SNR()
	 {
		 int indx =0 ;
		 if( control_gui.SNR_value  == 0 )
		 {
			 snr_enable = false;
			 //System.out.println("snr_enable sssssssssssssssssssssssssssssssssssssssssssssssssssssssssr  "+snr_enable);

		 }
		 else
		 {
			 for( int i = 0; i < Definition.Fab_All_Data; i++)  //1024
			 {	
				 DescriptiveStatistics stat = new DescriptiveStatistics();
				 for( int j = 0; j <20; j++)
				 {
				        stat.addValue(snr_rawdata[j][i]);
				 }
				 snr[i] = 20 * Math.log10(stat.getMean() /  stat.getStandardDeviation());
				 if(snr [i ]< control_gui.SNR_value )
				 {
					 tmp_snr_mask[indx] = i ;  // index of ch 
					// System.out.println("snr ch  "+tmp_snr_mask[indx] );
					 indx++;
				 }
						}
			 snr_enable = true;
			 //System.out.println("snr_enable tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt  "+snr_enable);

		 }
		
	 }
	
	public boolean Projection(int Mode, int FreqMode)
	 {

	 	
	 	USB.USBComm((byte)Definition.COMMAND_PROJECTION,(byte)Mode,(byte)FreqMode);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_PROJECTION)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Mode)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)FreqMode)
	 	{
	 		return false;
	 	}

	 	return true;
	 }
	public boolean Serial_Projection()
	 {
	 	USB.USBComm((byte)Definition.COMMAND_PROJECTION);
	 	//System.out.println((USB.m_nResponse[0]&0xFF)+ " "+(USB.m_nResponse[1]&0xFF)+" "+(USB.m_nResponse[2]&0xFF)+" "+(USB.m_nResponse[3]&0xFF)+ " "+(USB.m_nResponse[4]&0xFF) + " "+(USB.m_nResponse[5]&0xFF) + " "+(USB.m_nResponse[6]&0xFF)+ " "+(USB.m_nResponse[7]&0xFF));
	 	if(USB.m_nResponse[0] == (byte)255)
	 	{
	 		return false;
	 	}


	 	return true;
	 }
	public boolean Serial_Read_RQ()
	 {
	 	USB.USBComm((byte)Definition.SERIAL_EIT_RQ);
	 	if((USB.m_nResponse[0]&0xff) == 255)
	 	{
	 		return false;
	 	}


	 	return true;
	 }
	public boolean RQDataRead_MB_CAL(int ChNum, int Mode)
	{
		USB.USBComm((byte)Definition.COMMAND_DATA_READ,(byte)ChNum,(byte)Mode);
		if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_DATA_READ)
		{
			return false;
		}
		else if(USB.m_nResponse[1] != (byte)ChNum)
		{
			return false;
		}
		else if(USB.m_nResponse[2] != (byte)Mode)
		{
			return false;
		}

		return true;
	}
	 
	 /*
	 boolean ProjectionEnd()
	 {
	 	USB.USBComm((byte)Definition.COMMAND_MANUAL_PROJ_END);
	 	if(USB.m_nResponse[0] != Definition.RESPONSE_MANUAL_PROJ_END)
	 	{
	 		return false;
	 	}
	 	
	 	return true;
	 }
	 
	 boolean SetSourceSinkCh(byte Ch, byte Source, byte Sink)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_SOURCESINK,Ch, Source, Sink);
	 	if(USB.m_nResponse[0] != Definition.RESPONSE_SOURCESINK)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != Source)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != Sink)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 */
	public boolean WG_ChSetting(int Ch, int WGChInfo, int WGControl)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_WG_CHANNEL_SETTING,(byte)Ch, (byte)WGChInfo, (byte)WGControl);
	 	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_WG_CHANNEL_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)WGChInfo)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)WGControl)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 public boolean WGStop()
	 {
	 	USB.USBComm((byte)Definition.COMMAND_WG_STOP);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_WG_STOP)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 public boolean InjFreqSetting(int Ch, int Gap, int CNTHigh, int CNTLow)
	 {
 
	 	USB.USBComm((byte)Definition.COMMAND_INJ_FREQ_SETTING,(byte)Ch,(byte)Gap,(byte)CNTHigh,(byte)CNTLow);
	 	
	 	
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_INJ_FREQ_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Gap)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)CNTHigh)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] != (byte)CNTLow)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 
	 public boolean AcqFreqSetting(int Ch, int Gap, int CNTHigh, int CNTLow)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_ACQ_FREQ_SETTING,(byte)Ch,(byte)Gap,(byte)CNTHigh,(byte)CNTLow);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_ACQ_FREQ_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Gap)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)CNTHigh)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] != (byte)CNTLow)
	 	{
	 		return false;
	 	}

	 	return true;
	 }
	 public boolean DMFreqSetting(int Ch, int DMNum, int Gap2, int Gap3)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_DM_FREQ_SETTING,(byte)Ch,(byte)DMNum,(byte)Gap2,(byte)Gap3);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_DM_FREQ_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)DMNum)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] !=(byte) Gap2)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[4] != (byte)Gap3)
	 	{
	 		return false;
	 	}

	 	return true;
	 }
	 public boolean CCSDigiSetting(int Ch, int Digi1, int Digi2)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_CCS_DIGI_SETTING, (byte)Ch, (byte)Digi1,(byte)Digi2);
	 	System.out.println("CCSDigiSetting" + " "+ Digi1+ " "+  Digi2);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_CCS_DIGI_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Digi1)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)Digi2)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 public boolean CCSOffsetSetting(int Ch, int Amp)	
	 {
	 	int OffsetData;
	 	int AmpLow, AmpHigh;
	 	if(Amp < 0)
	 	{
	 		OffsetData = (1 << 15) + (Math.abs(Amp) & 0x3FF);
	 	}
	 	else
	 	{
	 		OffsetData = (Math.abs(Amp) & 0x3FF);
	 	}

	 	AmpHigh = ((OffsetData & 0xFF00) >> 8);
	 	AmpLow = OffsetData & 0x00FF;
	 	/*System.out.println("AQ_AmpHigh "+ AmpHigh);
	 	System.out.println("AQ_AmpLow "+ AmpLow);*/
	 	if(!DACSet(Ch,13,AmpLow))
	 	{
	 		return false;
	 	}
	 	if(!DACSet(Ch,14,AmpHigh))
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 /*
	 boolean SetSinkCh(byte Ch, byte Sink)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_SINKCH,Ch, Sink);
	 	if(USB.m_nResponse[0] != Definition.RESPONSE_SINKCH)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != Sink)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

*/
	 public boolean CCSAmp1Setting(int Ch, int AmpHigh, int AmpLow)
	 {
	 	if(!DACSet(Ch,11,AmpLow))
	 	{
	 		return false;
	 	}
	 	if(!DACSet(Ch,12,AmpHigh))
	 	{
	 		return false;
	 	}
/*System.out.println("AmpLow1   "+AmpLow);
System.out.println("AmpHigh1  "+AmpHigh);
*/	 	return true;
	 }
	 public boolean CCSAmp2Setting(int Ch, int AmpHigh, int AmpLow)
	 {

	 	if(!DACSet(Ch,15,AmpLow))
	 	{
	 		return false;
	 	}
	 	if(!DACSet(Ch,16,AmpHigh))
	 	{
	 		return false;
	 	}
	 	/*System.out.println("AmpLow2   "+AmpLow);
	 	System.out.println("AmpHigh2  "+AmpHigh);*/
	 	return true;
	 }

	 public boolean GICSWSet(int Ch, int SW)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_GIC_SW,(byte) Ch, (byte)SW );
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_GIC_SW)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)SW)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 public boolean GIC1Setting(int Ch, int Digi1, int Digi2)
	{
		USB.USBComm((byte)Definition.COMMAND_GIC1_SETTING, (byte)Ch, (byte)Digi1,(byte)Digi2);
		System.out.println("GIC1Setting" + " " + Digi1+ " "+  Digi2);
		if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_GIC1_SETTING)
		{
			return false;
		}
		else if(USB.m_nResponse[1] != (byte)Ch)
		{
			return false;
		}
		else if(USB.m_nResponse[2] != (byte)Digi1)
		{
			return false;
		}
		else if(USB.m_nResponse[3] != (byte)Digi2)
		{
			return false;
		}
		return true;
	}
	 /*	 
	 boolean GIC2Setting(int Ch, int Digi1, int Digi2)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_GIC2_SETTING, (byte)Ch,(byte) Digi1,(byte)Digi2);
	 	if(USB.m_nResponse[0] !=(byte) Definition.RESPONSE_GIC2_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Digi1)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] != (byte)Digi2)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 
	 boolean GIC3Setting(int Ch, int Digi1, int Digi2)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_GIC3_SETTING, (byte)Ch,(byte) Digi1,(byte)Digi2);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_GIC3_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Digi1)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] !=(byte) Digi2)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 boolean GIC4Setting(int Ch, int Digi1, int Digi2)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_GIC4_SETTING, (byte)Ch, (byte)Digi1,(byte)Digi2);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_GIC4_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] !=(byte) Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Digi1)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[3] !=(byte) Digi2)
	 	{
	 		return false;
	 	}
	 	return true;
	 }
	 */
	 public int GICSWCal(int FreqNum)
	 {
	 	int SW = 0;
	 	if(FreqNum == 6)
	 	{
	 		SW = 4;
	 	}
	 	else if(FreqNum == 7)
	 	{
	 		SW = 4;
	 	}
	 	else if(FreqNum == 8)
	 	{
	 		SW = 2;
	 	}
	 	else if(FreqNum == 9)
	 	{
	 		SW = 1;
	 	}
	 	else
	 	{
	 		SW = 8;
	 	}
	 	return SW;
	 }

	 public boolean StartOffsetCal(int Ch)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_START_OFFSET_CAL,(byte)Ch);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_START_OFFSET_CAL)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 public boolean SendOffsetData(int Ch)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_START_OFFSET_DATA,(byte)Ch);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_START_OFFSET_DATA)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	return true;
	 }

	 public long[] SendOffsetVal(int Ch, long[] OffsetData)
	 {
		 	byte[] Command = new byte [1];
		 	int[] ChID = new int [16];
		 	Command[0] = Definition.COMMAND_SEND_OFFSET_VAL;
		 	if(Ch == Definition.ALL_CH)
		 	{
		 		USB.USBComm2(1,(4*Definition.Serial_EIT_ALL_CH),1,Command);
		 		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		 		{
		 			ChID[i] = ((USB.m_nResponse[i*4]&0xFF) & 0xF0);
		 			
		 			////////////4 byte
		 			OffsetData[i] = (((USB.m_nResponse[i * 4]&0xFF) & 0x0F) << 24) + ((USB.m_nResponse[i * 4 +1]&0xFF) << 16) + ((USB.m_nResponse[i * 4 + 2]&0xFF) << 8) + ((USB.m_nResponse[i * 4 + 3]&0xFF));
		 			if(OffsetData[i] > 134217728)
		 			{
		 				OffsetData[i] = OffsetData[i] - 268435456;
		 			}


		 		}
		 	}
		 	else
		 	{
		 		USB.USBComm2(1,4,1,Command);
		 		ChID[0] = ((USB.m_nResponse[4]&0xFF) & 0xF0);
		 		////////////4 byte
		 		OffsetData[0] = (((USB.m_nResponse[0]&0xFF) & 0x0F) << 24) + ((USB.m_nResponse[1]&0xFF) << 16) + ((USB.m_nResponse[2]&0xFF) << 8) + ((USB.m_nResponse[3]&0xFF));
		 		if(OffsetData[0] > 134217728)
		 		{
		 			OffsetData[0] = OffsetData[0] - 268435456;
		 		}
		 		
		 	}
		 	return OffsetData;
		 }

	 boolean VMDigi1Setting(int Ch, double Digi)
	 {
	 	USB.USBComm((byte)Definition.COMMAND_VM_DIGI1_SETTING, (byte)Ch,(byte) Digi);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_VM_DIGI1_SETTING)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[1] != (byte)Ch)
	 	{
	 		return false;
	 	}
	 	else if(USB.m_nResponse[2] != (byte)Digi)
	 	{
	 		return false;
	 	}
	 	SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);

	 	return true;
	 }
	 
	 
	 /************* EIT Manual Projection Function *****************/
	 Object[] EIT_ManualProjection(int Mode, int CurrentProjection, int[] ChInfo, int[] Overflow, double[] Real, double[] Quad) throws IOException
	 {
	 	int InjGap, InjCNTHigh, InjCNTLow;
	 	int InjChCount;
	 	int SRCChCount = 0,SinkChCount = 0;
	 	int[] SRCCh = new int [16];
	 	int[] SinkCh = new int[16];
	 	int ChCheckSRC,ChCheckSink;


	 	InjChCount = 0;
	 	SinkChCount = 0;
	 	SRCChCount = 0;
	 	
	 	SiUSBXp.INSTANCE.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	InjectionDelay(m_nInjDelayHigh,m_nInjDelayLow);
	
	 	OpModeSetting(0,0,0,0,0);

	 	/*if(!CommFPGASet((m_nOpMode >> 3),0,ProjectionTable[CurrentProjection][0][Definition.TOTALDEMODULATIONFREQUENCY],m_nTotalProjection-1))
	 	{
			JOptionPane.showMessageDialog(null, "CommFPGASet Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;

	 	}
*/
	 	if(!PipelineScanSetting(0,1, 0,16))	// check this setting number
	 	{
			JOptionPane.showMessageDialog(null, "Pipeline Scan Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	 	}


	 	for(int j = 0; j < Definition.MAX_CH; j++)
	 	{
	 		ChCheckSRC = ProjectionTable[CurrentProjection][j][Definition.CHANNELCTRL];
	 		ChCheckSink = ProjectionTable[CurrentProjection][j][Definition.CHANNELCTRL];
	 		if(ChCheckSRC == 2 || ChCheckSink == 3)
	 		{
	 			if(ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY] <= 15)
	 			{
	 				if(!GICSWSet(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.GIC_ON_OFF]))
	 				{
	 					JOptionPane.showMessageDialog(null, "GIC SW Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}

	 				if(!GIC1Setting(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.GIC1A],CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.GIC1B]))
	 				{
	 					JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}


	 				if(!CCSDigiSetting(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.HCP_CCS_A],CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.HCP_CCS_B]))
	 				{
	 					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}	
	 			}
	 			else
	 			{
	 				if(!GICSWSet(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]-6][Definition.GIC_ON_OFF]))
	 				{
	 					JOptionPane.showMessageDialog(null, "GIC SW Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}

	 				if(!GIC1Setting(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]-6][Definition.GIC1A],CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.GIC1B]))
	 				{
	 					JOptionPane.showMessageDialog(null, "GIC1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}
	 	

	 				if(!CCSDigiSetting(j,CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]-6][Definition.HCP_CCS_A],CCSTable[j][ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]][Definition.HCP_CCS_B]))
	 				{
	 					JOptionPane.showMessageDialog(null, "CCS Digi Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}	
	 			}
	 			if(!CCSAmp1Setting(j,ProjectionTable[CurrentProjection][j][Definition.AMP1_HIGH],ProjectionTable[CurrentProjection][j][Definition.AMP1_LOW]))
	 			{
	 				JOptionPane.showMessageDialog(null, "CCS Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 				return null;
	 			}
	 			if(!CCSAmp2Setting(j,ProjectionTable[CurrentProjection][j][Definition.AMP2_HIGH],ProjectionTable[CurrentProjection][j][Definition.AMP2_LOW]))
	 			{
	 				JOptionPane.showMessageDialog(null, "CCS Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 				return null;
	 			}
	 			//Source Sink Ch Set

	 			int[] FreqtoCNTGap_data = new int[3];
	 			if(ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY] < 15)
	 			{
	 				FreqtoCNTGap_data = FreqtoCNTGap(ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]);
	 				InjCNTHigh = FreqtoCNTGap_data[0];
	 				InjCNTLow = FreqtoCNTGap_data[1];
	 				InjGap= FreqtoCNTGap_data[2];	
	 			}
	 			else
	 			{
	 				FreqtoCNTGap_data = FreqtoCNTGap(ProjectionTable[CurrentProjection][j][Definition.INJECTIONCURRENTFREQUENCY]-6);
	 				InjCNTHigh = FreqtoCNTGap_data[0];
	 				InjCNTLow = FreqtoCNTGap_data[1];
	 				InjGap= FreqtoCNTGap_data[2];	
	 			}


	 			if(!InjFreqSetting(j,InjGap,InjCNTHigh,InjCNTLow))
	 			{
	 				JOptionPane.showMessageDialog(null, "Injection Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 				return null;
	 			}

	 			if(((ProjectionTable[CurrentProjection][j][Definition.CHANNELCTRL])) == 2)
	 			{
	 				SRCCh[SRCChCount] = j;
	 				SRCChCount++;
	 			}
	 			else if((ProjectionTable[CurrentProjection][j][Definition.CHANNELCTRL]) == 3)
	 			{
	 				SinkCh[SinkChCount] = j;
	 				SinkChCount++;
	 			}
	 			else
	 			{

	 				if(!WG_ChSetting(j, 0, 0))
	 				{
	 					JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 					return null;
	 				}
	 			}
	 			InjChCount++;
	 		}

	 		if(!VMDigi1Setting(j,ProjectionTable[CurrentProjection][j][Definition.GAIN1]))
	 		{
				JOptionPane.showMessageDialog(null, "VM1 Gain Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 			return null;
	 		}

	 	}
	 	InjChCount = InjChCount / 2;
	 	if(!WG_ChSetting(SRCCh[0], 0, 2))
	 	{
			JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;
	 	}

	 	if(!WG_ChSetting(SinkCh[0], 0, 3))
	 	{
			JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;
	 	}

	 	for(int k = 1; k < InjChCount; k++)
	 	{
	 		if(!WG_ChSetting(SRCCh[k], 0, 2))
	 		{
				JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 			return null;
	 		}
	 		if(!WG_ChSetting(SinkCh[k], 0, 3))
	 		{
				JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 			return null;
	 		}
	 	}

	 	if(!AcqFreqSetting(Definition.ALL_CH,ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_GAP],ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_CNT_HIGH],ProjectionTable[CurrentProjection][0][Definition.ACQUREFREQUENCY_CNT_LOW]))
	 	{
			JOptionPane.showMessageDialog(null, "Acquire Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;
	 	}


	 	if(!DMFreqSetting(Definition.ALL_CH,ProjectionTable[CurrentProjection][0][Definition.TOTALDEMODULATIONFREQUENCY],ProjectionTable[CurrentProjection][0][Definition.DEMODULATIONFREQUENCY2],ProjectionTable[CurrentProjection][0][Definition.DEMODULATIONFREQUENCY3]))
	 	{
			JOptionPane.showMessageDialog(null, "Demodulation Freq Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;
	 	}

	 	m_bThreadCommWorkingFlag = false;
	 	m_bThreadDataProcessWorkingFlag = false;
	 	m_bThreadWriteFileWorkingFlag = false;
	 	m_bThreadDataProcessFinishFlag = true;



	 	if(!Projection(Mode,m_nMixedFlag))
	 	{
			JOptionPane.showMessageDialog(null, "Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;
	 	}
	 	else
	 	{
	 		USB.ReadData3(m_nTotalCh * m_nTotalFreqNum * 6);
	 		FileWriter fout;
	 		String str;
	 		str = m_strSaveFilePath + m_strScriptProjectionName[CurrentProjection] +".txt";
	 		fout = new FileWriter(str);

	 		for(int i = 0; i < USB.m_nReadByte; i = i + 6)
	 		{
	 			fout.write((int)(USB.m_nResponse[i]&0xFF) + "\t" + (int)(USB.m_nResponse[i+1]&0xFF) + "\t" + (int)(USB.m_nResponse[i+2]&0xFF)+ "\t" + (int)(USB.m_nResponse[i+3]&0xFF)+ "\t" + (int)(USB.m_nResponse[i+4]&0xFF)+ "\t" + (int)(USB.m_nResponse[i+5] &0xFF)+ "\r\n");
	 		}
	 		fout.close();

	 		for(int Ch = 0; Ch < m_nTotalCh; Ch++)
	 		{
	 			ChInfo[Ch] = USB.m_nResponse[Ch * 6]&0xFF;
	 			Overflow[Ch] = USB.m_nResponse[Ch * 6 + 1]&0xFF;
	 			Real[Ch] = ((USB.m_nResponse[Ch * 6 + 2]&0xFF) << 8) + (USB.m_nResponse[Ch * 6 + 3]&0xFF);
	 			Quad[Ch] = ((USB.m_nResponse[Ch * 6 + 4]&0xFF) << 8) + (USB.m_nResponse[Ch * 6 + 5]&0xFF);
	 			if(Real[Ch] > 32767)
	 			{
	 				Real[Ch] = Real[Ch] - 65536;
	 			}
	 			if(Quad[Ch] > 32767)
	 			{
	 				Quad[Ch] = Quad[Ch] - 65536;
	 			}
	 		}
	 	}

	 	return  new Object[]{ChInfo, Overflow,Real,Quad};
	 }

	 /************* Check Comments Function *****************/
	int CheckCommnet(char[] Data)
	{

		int CommentIndex = 0;
		while(true) //while(1)
		{
			if(Data[CommentIndex] == '%')
			{
				//System.out.println(Data[CommentIndex]);
				return CommentIndex;
			}
			else if(Data[CommentIndex] == '#')
			{
				//System.out.println(Data[CommentIndex]);
				return Definition.INCLUDE;
			}
			else //if( Data[CommentIndex] == 0)
			{
				//System.out.println(Data[CommentIndex]);
				return Definition.NOCOMMENT;
			}
			//CommentIndex++;
		}
		//return -1;
	}
	/************* Scale the Frequency to correct value Function *****************/
	int ScaleFreq(double Freq)
	{
		int FreqIndex = 0;
		/*if(Freq < 50.5)
		{
			FreqIndex = 0;
			Freq = 22.5;
		}
		else if(Freq < 150.5)
		{
			FreqIndex = 1;
			Freq = 112.5;
		}
		else if(Freq < 250.5)
		{
			FreqIndex = 2;
			Freq = 225;
		}
		else if(Freq < 2500.5)
		{
			FreqIndex = 3;
			Freq = 2250;
		}
		else if(Freq < 15000)
		{
			FreqIndex = 4;
			Freq = 11250;
		}
		else if(Freq < 25000)
		{
			FreqIndex = 5;
			Freq = 22500;
		}
		else if(Freq < 150000)
		{
			FreqIndex = 6;
			Freq = 112500;
		}
		else if(Freq < 250000)
		{
			FreqIndex = 7;
			Freq = 225000;
		}
		else if(Freq < 600000)
		{
			FreqIndex = 8;
			Freq = 495000;
		}
		else
		{
			FreqIndex = 9;
			Freq = 900000;
		}*/
		
		if(Freq < 33.625)
		{
			FreqIndex = 0;
			Freq = 11.25;
		}
		else if(Freq < 84.25)
		{
			FreqIndex = 1;
			Freq = 56;
		}
		else if(Freq < 618.75)
		{
			FreqIndex = 2;
			Freq = 112.5;
		}
		else if(Freq < 3362.5)
		{
			FreqIndex = 3;
			Freq = 1125;
		}
		else if(Freq < 8425)
		{
			FreqIndex = 4;
			Freq = 5600;
		}
		else if(Freq < 33750)
		{
			FreqIndex = 5;
			Freq = 11250;
		}
		else if(Freq < 78750)
		{
			FreqIndex = 6;
			Freq = 56250;
		}
		else if(Freq < 174375)
		{
			FreqIndex = 7;
			Freq = 101250;
		}
		else if(Freq < 371250)
		{
			FreqIndex = 8;
			Freq = 247500;
		}
		else
		{
			FreqIndex = 9;
			Freq = 495000;
		}
		
		return FreqIndex;
	}
	
	/************* Amplitude Converter Function *****************/
	public int AmpConvert(double Input, double CalibrationFactor)	
	{
		int Output = (int)Input * (int)CalibrationFactor;
		return Output;
	}
	
	/*public int[] FreqtoCNTGap(int FreqIndex)
	{	int[] return_data = new int[3];
		switch(FreqIndex)
		{
		case 0:	// 22.5Hz
			return_data[0] = 3;	// CLK_CNT_HITH
			return_data[1] = 231;	// CLK_CNT_LOW
			return_data[2] = 1;	//*Gap_DATA	
			break;
		case 1:	// 112.5Hz
			return_data[0] = 0;
			return_data[1] = 199;
			return_data[2] = 1;
			break;
		case 2:	// 225Hz
			return_data[0] = 0;
			return_data[1] = 99;
			return_data[2] = 1;
			break;
		case 3:	//2.25kHz
			return_data[0] = 0;
			return_data[1] = 9;
			return_data[2] = 1;
			break;
		case 4:	// 11.25kHz	
			return_data[0] = 0;
			return_data[1] = 1;
			return_data[2] = 1;
			break;
		case 5:	//22.5kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 1;
			break;
		case 6:	// 112.5kHz	
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 5;
			break;
		case 7:	// 225kHz
			return_data[0] = 0;	
			return_data[1] = 0;
			return_data[2] = 10;
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 10;
			break;
		case 8:	// 495kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 25;
			break;
		case 9:	// 900KHz	
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 40;
			break;
		default:
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 40;
			break;
		}
		 return return_data;
	}*/
	
	
	public int[] FreqtoCNTGap(int FreqIndex)
	{	int[] return_data = new int[3];
		switch(FreqIndex)
		{
		case 0:	// 11.25Hz	-- 18.75Hz
			return_data[0] = 3;	// CLK_CNT_HITH
			return_data[1] = 231;	// CLK_CNT_LOW
			return_data[2] = 1;	//*Gap_DATA	
			break;
		case 1:	// 56.25Hz	--93.75Hz
			return_data[0] = 0;
			return_data[1] = 199;
			return_data[2] = 1;
			break;
		case 2:	// 112.5Hz	--187.5Hz
			return_data[0] = 0;
			return_data[1] = 99;
			return_data[2] = 1;
			break;
		case 3:	// 1.125kHz	--1.875kHz
			return_data[0] = 0;
			return_data[1] = 9;
			return_data[2] = 1;
			break;
		case 4:	// 5.6kHz		-- 9.375kHz
			return_data[0] = 0;
			return_data[1] = 1;
			return_data[2] = 1;
			break;
		case 5:	// 11.25kHz		-- 18.75kHz
			return_data[0] = 0;	//modified by YE - 9.375kHz
			return_data[1] = 1; //modified by YE - 9.375kHz
			return_data[2] = 1;	//modified by YE - 9.375kHz
			
			return_data[0] = 0;	
			return_data[1] = 0;
			return_data[2] = 1;
			break;
		case 6:	// 56.25kHz		--93.75kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 5;
			break;
		case 7:	// 112.2kHz		--187.5kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 10;
			break;
		case 8:	// 281.25kHz	-- 468.75kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 25;
			break;
		case 9:	// 562.5KHz		-- 750kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 40;
			break;
		default:
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 40;
			break;
		}
		 return return_data;
	}
	
	/************* Multiple Frequency Converter Function *****************/
	public int ConvertMultiFreq(int RefCNTHigh, int RefCNTLow, int RefGap, int CompCNTHigh, int CompCNTLow, int CompGap)
	{
		int RefCNT = RefCNTHigh *256 + RefCNTLow + 1;
		int CompCNT = CompCNTHigh *256 + CompCNTLow + 1;
		return  CompGap * (RefCNT/CompCNT);  //OutputGap
	}
	
	public boolean MuxBoardControl(int MuxEnSel, int source,int sink)
	{

		USB.USBComm((byte)Definition.COMMAND_MuxBoardControl,(byte)MuxEnSel,(byte)source,(byte)sink);	
		/*if(USB.m_nResponse[0] != (byte)Definition.Response_MuxBoardControl)
		{
			System.out.println("MUX"+(USB.m_nResponse[0]&0xFF));
			return false;
		}
		else if(USB.m_nResponse[1] != (byte)MuxEnSel)
		{
			return false;
		}*/
//		else if(USB.m_nResponse[2] != (byte)source)
//		{
//			return false;
//		}
//		else if(USB.m_nResponse[3] != (byte)sink)
//		{
//			return false;
//		}
		
		return true;
	}
	public boolean VMControl(int VMEnSel, int AddressVM1,int AddressVM2)
	{

		USB.USBComm((byte)Definition.COMMAND_VMControl,(byte)VMEnSel,(byte)AddressVM1,(byte)AddressVM2);	
/*		if(USB.m_nResponse[0] != (byte)Definition.Response_VMControl)
		{
			return false;
		}
		else if(USB.m_nResponse[1] != (byte)VMEnSel)
		{
			return false;
		}*/
//		else if(USB.m_nResponse[2] != (byte)AddressVM1)
//		{
//			return false;
//		}
//		else if(USB.m_nResponse[3] != (byte)AddressVM2)
//		{
//			return false;
//		}
		
		return true;
	}

	
}
