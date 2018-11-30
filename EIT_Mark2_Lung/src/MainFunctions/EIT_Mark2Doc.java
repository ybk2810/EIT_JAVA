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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JOptionPane;

import USBCommuniction.SiUSBXp;
import USBCommuniction.USB_Functions;
public class EIT_Mark2Doc {
	
	
	
	public USB_Functions USB = new USB_Functions();
	public SiUSBXp USB_lib = SiUSBXp.INSTANCE;
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;
	Control control_gui ;


	//////////////Script///////////////////
	public String m_strScriptFilePath;
	String m_strProjectionFilePath;
	String m_strScriptFileDirectory;
	String m_strScriptFileTitle;
	String[] m_strScriptProjectionName = new String[100];
	public String m_strScriptFileName;
	String m_strProtocolName;
	int m_nScriptLine;
	String m_strCategory;
	String m_strLog;
	String m_strFileName;
	double d_Amp;
	double Temp_d_Amp;
	double Temp_d_Gain;
	///////////////////////////////////////
	//////////Calibration/////////////////
	int m_bDCOffsetCal;
	int m_bOutputImpedanceCal;
	int m_bAmplitudeCal;
	int m_bVoltmeterCal;
	public double[][]Magnitude= new double [3][3000];
	public double[][] Phase = new double[3][3000];
	//////////////////////////////////////
	//////////Load Calibration /////////////////
	String[] m_strFreq= new String[Definition.NUM_OF_FREQUENCY];
	public String m_strDefaultFilePath =  System.getProperty("user.dir");
	public String m_strCalibration = "Calibration";
	public String m_strUSBID;
	
	///////////Calibration Data///////////
	public int[][] m_nDCOffsetValue = new int[Definition.MAX_CH][Definition.NUM_OF_FREQUENCY];
	public double[][] m_dAmplitudeCalibrationFactor = new double [Definition.MAX_CH][Definition.NUM_OF_FREQUENCY];
	//int m_nOutputImpedanceCalibrationData[MAX_CH][NUM_OF_FREQUENCY][4];
	//double *m_dVoltmeterMagnitudeFactor;
	public double[] m_dVoltmeterMagnitudeFactor;
	public double[] m_dVoltmeterPhaseFactor;
	public int m_nVoltmeterCalibrationIndex;
	public int[][][] CCSCalData = new int[Definition.MAX_CH][Definition.NUM_OF_FREQUENCY][4];
	public int[][] DCCalData = new int[Definition.MAX_CH][Definition.NUM_OF_FREQUENCY];
	public int[][][] CCSTable = new int[Definition.MAX_CH][Definition.NUM_OF_FREQUENCY+6][Definition.CCSTABLEROW];
	//////////////////////////////////////

	////////////Projection File////////////
	int ProjectionTableIndex;
	String[] ProjectionName = new String[100];
	double[][][] TempProjectionTable = new double[100][Definition.MAX_CH][5];			
	int[][][]  ProjectionTable = new int[100][Definition.MAX_CH][Definition.PROjECTIONTABLEROW]; // [ProjectionIndex][ChIndex][Data]
	///////////////////////////////////////	
	///////Data///////////////////////////
	//byte[] RawData = new byte[20000];
	int[] RawData = new int[20000];
	double[][] Real = new  double[3][3000];
	double[][] Quad = new double [3][3000];
	int [][] VMOverflow= new int[3][3000];
	int[][]  ChNum = new int[3][3000];
	/*double Magnitude[3][3000];
	double Phase[3][3000];
	BOOL VMOverflow[3][3000];
	*/

	public double[][] TempReal= new double[3][3000];
	public double[][] TempQuad= new double[3][3000];
	double[][] TempMagnitude= new double[3][3000];
	double[][] TempPhase= new double[3][3000];
	public int[][] TempVMOverflow= new int[3][3000];  /////check
	public int[][] TempCh = new int[3][3000];
	
	//////////////////////////////////////
	//////////Algorithm///////////////////
	int numtriangle, numSencol, sqgrid, mulresol, resol;
	int trinum[] = new int [24*24];
	int trinum2[] = new int[24*24*4];
	float Sen[] = new float[258*1024];
	float Sen_d[] = new float[258*1024];
	
	double Image_dsigma[] = new double[1024*2];
	
	double[] Image_RefReal= new double[256];
	double[] Image_RefQuad = new double [256];
	double[] Image_RefMag = new double[256];
	double[] Image_RefTheta = new double[256];
	
	double[][] DipolX = new double[16][1184];
	double[][] DipolY = new double[16][1184];
	int[]  DipolIndex= new int[1600];
	double[] indicator= new double[1184];
	int AlgorithmFlag;
	double[] FactoraizationIndex = new double[256];
	
	int GREIT_TetrahedralMeshNum;
	int GREIT_DataIndex;
	int[] GREIT_RemoveIndex = new int[48];
	double[][] GREIT_RMatrix = new double[812][208];
	double[] GREIT_Reference = new double[208];
	double[] GREIT_Pertb = new double[208];
	int[] GREIT_Map = new int [40*40];
	
	double[] Image_ObjReal = new double[256];
	double[] Image_ObjQuad = new double[256];
	double[] Image_ObjMag = new double [256];
	double[] Image_ObjTheta = new double[256];
	double[] Image_ObjVM = new double[256];
	
	/*long double alpha1;
	long double alpha2;*/
	double[][] delta_NtD = new double[16][16];
	
	/////////////////Graph////////////////
	double [] Rec_Error = new double[120];
	double Rec_Error_val_temp = 0;
	double Rec_Error_val = 0;
	double[] Zc =new double[16];
	//////////////////////////////////////
	public int m_nTotalCh;
	public int m_nTotalProjection;
	public int m_nNumofAVG;
	int m_nSelectDevice;
	int m_nAmp;
	int m_nDelay;
	int m_nNumofDevice;
	int m_nMethod;
	int m_nTotalFreqNum;
	int m_nMode;
	int m_nTotalDataLength;
	int m_nPresentScanNum;
	public int m_nDummyScanNum;
	public int m_nTotalReadBlock;
	public int m_nTimeInfoHigh;
	public int m_nTimeInfoMid;
	public int m_nTimeInfoLow;
	public int m_nInjDelayHigh;
	public int m_nInjDelayLow;
	int m_nMixedFlag;
	int m_nNumofScans;
	int m_nScanInterval;
	int m_nPresentWriteNum;

	
	
	String m_strSaveFileName;
	////////Thread////////////////////////
	Thread m_hThreadComm;
	Thread m_hThreadWriteFile;
	Thread m_hThreadDataProcess;
	Thread m_hThreadCalRecipError;
	int WriteScanNum;
	
	public boolean m_bThreadCommWorkingFlag;
	public boolean m_bThreadDataProcessWorkingFlag;
	public boolean m_bThreadDataProcessFinishFlag;
	public boolean m_bThreadDataProcessFinishFlag2;
	public boolean m_bThreadWriteFileWorkingFlag;
	public boolean m_bThreadRecipErrorWorkingFlag;
	public boolean m_bThreadWriteFileFinishFlag;
	public boolean m_bMessageImageReconFinishFlag1;
	public boolean m_bMessageGraphReconFinishFlag;
	public boolean m_bMessageGraphContactImpedanceFinishFlag;
	public boolean m_bScanIntervalTimerFlag;
	
	int m_nThreadDataIndex;
	int m_nThreadRQDataIndex;
	int m_nReadBlock;
	int m_nWriteFileIndex;

	int Thread_Freq ;
	int Thread_projection;
	int Thread_Ch;
	///////////////Pipline/////////////////////
	public int m_nOpMode;
	public boolean m_bSave;
	
	
	public String m_strSaveFilePath;
	FileWriter Mag_fout = null;
	
	
	 /*************  Default Constructor *****************/
	public  EIT_Mark2Doc(Control gui)
	{

		this.control_gui=gui;
		
		m_nTotalCh = Definition.TOTAL_CH;
		m_nTotalProjection = Definition.TOTAL_CH;
		m_nNumofAVG = 1;
		m_nNumofDevice = 0;
		m_nSelectDevice = 0;
		m_nDelay = 0;
		m_nTotalFreqNum = 0;
		WriteScanNum =0;
		m_nPresentWriteNum =0;
		m_nMode = Definition.MANUAL_MODE;
		m_bThreadCommWorkingFlag = false;
		m_bThreadDataProcessWorkingFlag = false;
		m_bThreadDataProcessFinishFlag = false;
		m_bThreadDataProcessFinishFlag2 = false;
		m_bThreadWriteFileWorkingFlag = false;
		m_bThreadRecipErrorWorkingFlag= false;
		m_bThreadWriteFileFinishFlag = false;
		m_bMessageImageReconFinishFlag1 = true; 
		m_bMessageGraphReconFinishFlag = true; 
		m_bMessageGraphContactImpedanceFinishFlag = true;
		m_bScanIntervalTimerFlag = true;
		//m_bRecon = FALSE;
		m_nThreadDataIndex = 0;
		m_bSave = false;
		m_nPresentScanNum = 0;
		//m_nTotalDataLength = 1536;
		m_nTotalDataLength = 3072;
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
		GREIT_DataIndex = 208;
		
		
		
		
	for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
	{
		for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
		{
			m_nDCOffsetValue[Ch][Freq] = 0;
			m_dAmplitudeCalibrationFactor[Ch][Freq] = 1;
		}
	}
	
	try {
		IMAGE_LoadAlgorithmFile(Definition.TOTAL_CH);
		LoadGREITAlgorithmFile();
		LoadGREITRemoveData();
		LoadGREITMap();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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
								if(LoadProjectionFile(ProjectionFilePath+strTemp.toString())==1)
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
						///	System.out.println("ScriptLevel break "+ScriptLevel);
						}
					
			        }
			        s2.close();
			    }
			    
			  ///  System.out.println("*********************************** ");
			    fin.close();
				
			
			MakeProjectionTable();
			m_nVoltmeterCalibrationIndex = m_nTotalCh * m_nTotalProjection;
			m_nTotalDataLength = m_nTotalCh * m_nTotalProjection * m_nTotalFreqNum * 6;
			
			
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
	 public int LoadProjectionFile(String FilePath)
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
	 									n = strTemp.toString().toUpperCase().indexOf("M");
	 									
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
	 									Temp_d_Amp = TempProjectionTable[0][0][2];
	 									Data_Index++;
	 								}
	 								break;

	 							case Definition.PROJECTIONFILE_FREQUENCY:
	 								double Freq=0;
	 								int FreqIndex;
	 								//strTemp = ChangeCapitalCharacter(strTemp);
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
	 								Temp_d_Gain = TempProjectionTable[ProjectionTableIndex][ChIndex][Data_Index];
	 								
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
	 
	 /************* Make Projection Table Function *****************/
	 public int MakeProjectionTable()
	 {
		int[] FreqtoCNTGap_data = new int[3];
		int SmallestFreq =10 ;
	 	int SmallestFreqCNTHigh=0,SmallestFreqCNTLow=0,SmallestFreqGap=0;
	 	int CNTHigh1=0,CNTLow1=0,Gap1=0;
	 	int MultiGap1 = 1;
	 	int MultiGap2 = 1;
	 	int TotalFreq =0 ;
	 	int[]  DemodulationFreq =  new int[10];
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
	 	for(i = 0; i < m_nTotalProjection; i++)
	 	{
	 		for(a = 0; a < ProjectionTableIndex; a++)
	 		{
	 			if(m_strScriptProjectionName[i].equals(ProjectionName[a]))
	 			{//System.out.println( ProjectionName[a]);
	 				TotalFreq = 0;
	 				SmallestFreq = 10;
	 				int TempIndex=0;
	 				for(j = 0; j < Definition.TOTAL_CH; j++)
	 				{//System.out.println("TOTAL_CH   ");
	 					for(k = 0; k < Definition.PROjECTIONTABLEROW; k++)
	 					{
	 						switch(k)
	 						{
	 						case Definition.PROJECTIONINDEX:
	 							ProjectionTable[i][j][k] = i;
	 							///System.out.print(ProjectionTable[i][j][k]  );
	 							break;

	 						case Definition.CHANNELINDEX:
	 							ProjectionTable[i][j][k] = j;
	 							///System.out.print(ProjectionTable[i][j][k]  );
	 							break;

	 						case Definition.CHANNELINFO:
	 							TempIndex = j;
	 						/*	TempIndexNext = TempIndex + 1;
	 							TempIndexBefore = TempIndex - 1;*/
	 							ProjectionTable[i][TempIndex][k] = 0;
	 							///System.out.print(ProjectionTable[i][j][k]  );
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
	 							//System.out.println(TempProjectionTable[a][TempIndex][Definition.PROJECTIONFILE_SIGN]);
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
	 							///System.out.print(ProjectionTable[i][j][k]  );
	 							break;

	 						case Definition.AMP1_HIGH:
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) >> 8);
	 							//System.out.println(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP] );
	 							break;
	 						case Definition.AMP1_LOW:
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) & 0xFF);
	 							//System.out.println(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP] );
	 							break;

	 						case Definition.AMP2_HIGH:
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) >> 8);
	 							//System.out.println(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP] );
	 							break;
	 						case Definition.AMP2_LOW:
	 							ProjectionTable[i][j][k] = (AmpConvert(TempProjectionTable[a][j][Definition.PROJECTIONFILE_AMP],m_dAmplitudeCalibrationFactor[j][k]) & 0xFF);
	 							//System.out.println(m_dAmplitudeCalibrationFactor[j][k]  );
	 							break;

	 						case Definition.GAIN1:
	 						ProjectionTable[i][j][k] = (int) TempProjectionTable[a][j][Definition.PROJECTIONFILE_GAIN];
	 						//System.out.println(ProjectionTable[i][j][k]  );	
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
	 							///System.out.print(ProjectionTable[i][j][k]  );
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
	 							//System.out.println(DemodulationFreq[0]+""+DemodulationFreq[1]+""+DemodulationFreq[2]);
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
	 			//AfxMessageBox(str);
				JOptionPane.showMessageDialog(null, str, "InfoBox " , JOptionPane.INFORMATION_MESSAGE);

	 		}
	 	}
	 	if(DemodulationFreqIndex > 1)
	 	{
	 		MultiGap2 = ConvertMultiFreq(SmallestFreqCNTHigh,SmallestFreqCNTLow,SmallestFreqGap, CNTHigh1,CNTLow1,Gap1);
	 		if(MultiGap2 > 50)
	 		{
	 			//AfxMessageBox("The ration of multiple freq. should be within 50 times");
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

	 			for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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
	 		for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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

	 		
	 			for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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
	 		for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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

	 			for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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
	 		for(int Ch = 0; Ch < Definition.MAX_CH; Ch++)
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
	 		m_dVoltmeterMagnitudeFactor =(double[]) obj[0];//////////// check if need try catch or out in
	 		m_dVoltmeterPhaseFactor =(double[]) obj[1];
	 	}
	 	else
	 	{
	 		for(int i = 0; i < m_nVoltmeterCalibrationIndex; i++)
	 		{
	 			m_dVoltmeterMagnitudeFactor[i] = 1;
	 			m_dVoltmeterPhaseFactor[i] = 0;
	 			///////System.out.println("finnnnnnnn");
	 		}
	 	}
	 	
	 	return TRUE;
	 }
	 
	 /************* Load DCOffset CalibrationFile Function *****************/
	 public boolean LoadDCOffsetCalibrationFile(String FilePath)
	 {
		 Scanner fin = null;   
		      
	 //	ifstream finDCOffset[Definition.NUM_OF_FREQUENCY];
	 	String  FileName ,  strFilePath;
	 	FileName = "DCOffset.txt";
	 	int Temp;
	 	//System.out.println("hh");
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
	 					//finOutputImpedanceCalibration[Freq].getline(contents,150);
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
	 					///System.out.println(CCSCalData[Ch][Freq][DataIndex]);
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
	 //	Scanner fin = null;
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
	 		//		System.out.println(MagCalFactor[DataIndex]);
	 				DataIndex++;
	 			}
	 		}
	    	finMag.close();
	 	}
	 
	 

	  //  Scanner fin = null;
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
	 	for(int i = 0; i < Definition.MAX_CH; i++)
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
	 					CCSTable[i][j][k] = ((DCCalData[i][j] >> 8) & 0xFF);
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
	 				CCSTable[i][10][k] = ((DCCalData[i][0] >> 8) & 0xFF);
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
	 				CCSTable[i][11][k] = ((DCCalData[i][3] >> 8) & 0xFF);
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
	 				CCSTable[i][12][k] = ((DCCalData[i][5] >> 8) & 0xFF);
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
	 				CCSTable[i][13][k] = ((DCCalData[i][6] >> 8) & 0xFF);
	 				k++;
	 				CCSTable[i][13][k] = (DCCalData[i][6] & 0xFF);

	 				break;

	 			}
	 		}
	 	}

	 	return true;
	 }

	 /************* EIT Pipeline Scan Function 
	 * @throws InterruptedException 
	 * @throws HeadlessException 
	 * @throws IOException *****************/
	 public boolean EIT_PipelineScan() throws HeadlessException, InterruptedException, IOException
	 {  
//			BYTE Command[10];
		// HANDLE m_hThreadComm;
			if(!EIT_CommonSystemSetting())
			{
				return false;
			}
			if(!EIT_PipelineSystemSetting())
			{
				return false;
			}
			
			PipelineScanStart();		// Start : 1
			USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
		 
			 m_hThreadComm = new  Thread(new Runnable() {
					
					@Override
					public void run() {
						m_bThreadCommWorkingFlag = true;
						ThreadComm(EIT_Mark2Doc.this);
					}
				});
		 if(!m_hThreadComm.isAlive())
		 {
			
			  m_hThreadComm.start();
			
		 }
		 
		 m_hThreadDataProcess = new  Thread(new Runnable() {
				
				@Override
				public void run() {
					m_bThreadDataProcessWorkingFlag = true;
					ThreadDataProcess(EIT_Mark2Doc.this);
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
						ThreadWriteFile(EIT_Mark2Doc.this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		 if(!m_hThreadWriteFile.isAlive())
		 {
			 m_hThreadWriteFile.start();
		 }
		 
		 m_hThreadCalRecipError = new  Thread(new Runnable() {
				
				@Override
				public void run() {
					m_bThreadRecipErrorWorkingFlag = true;
				
					try {
						ThreadReciprocityError(EIT_Mark2Doc.this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		 if(!m_hThreadCalRecipError.isAlive())
		 {
			 m_hThreadCalRecipError.start();
		 }
			
			
			
		
			return true; 
	 }
	 
	 /************* EIT Common System Setting *****************/
	 public boolean EIT_CommonSystemSetting() throws HeadlessException, InterruptedException
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

	 	if(!CalSWSetting(Definition.ALL_CH,0))
	 	{
			JOptionPane.showMessageDialog(null, "Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	
	 	m_nTotalDataLength = m_nTotalCh * m_nTotalProjection * m_nTotalFreqNum * 6;

	 	return true;
	 }
	 /************* EIT Reset Setting *****************/
	 public boolean Reset() throws InterruptedException
	 {
		USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
		Thread.sleep(10);
	 	USB.USBComm((byte)Definition.COMMAND_RESET);
	 	//System.out.println(USB.buf[0] &0xFF);
	 	/*for(int i =0 ; i< 10 ;i++)
	 	System.out.print(USB.m_nResponse[i]&0xFF);
	 	System.out.print("   ");*/
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_RESET)
	 	{
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
	 	else
	 	{
	 		m_nTotalCh =(byte) USB.m_nResponse[3];
	 		if(m_nTotalCh != Definition.TOTAL_CH)
	 		{
	 			JOptionPane.showMessageDialog(null, "Total Ch : "+ m_nTotalCh, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
 			
	 		}
	 	}
	 	return true;
	 }
	 /************* EIT Average Setting Function *****************/
	 public boolean Average(int AVGNum)
	 {
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
	 
	 /************* EIT Over flow Number Setting Function *****************/
	 public boolean OverflowNumber(int Ch, int Num)
	 {
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
	 public boolean OpModeSetting(int OldNew, int SingleCascade, int MasterSlave, int EventTriggerOnOff, int OneMultiScan)
	 {
	 	if(OldNew==0) //////////////////check 
	 	{
	 		m_nOpMode = 0;
	 	}	
	 	else
	 	{
	 		m_nOpMode = (1 << 7) + (SingleCascade << 6) + (MasterSlave << 5) + (EventTriggerOnOff << 4) + (OneMultiScan << 3);
	 	}
	 	return true;
	 }

	 /************* EIT Time Slot Setting Function *****************/
	 public boolean TimeSlotSetting(int TimeSlot)
	 {
	 	int Timeslot1, Timeslot2, Timeslot3, Timeslot4;
	 	Timeslot4 = TimeSlot & 0xFF000000;
	 	Timeslot3 = TimeSlot & 0x00FF0000;
	 	Timeslot2 = TimeSlot & 0x0000FF00;
	 	Timeslot1 = TimeSlot & 0x000000FF;

	 	USB.USBComm((byte)Definition.COMMAND_TIMESLOT,(byte)Timeslot4,(byte)Timeslot3, (byte)Timeslot2, (byte)Timeslot1);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_TIMESLOT)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)Timeslot4)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)Timeslot3)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)Timeslot2)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[4] != (byte)Timeslot1)
	 	{
	 		return false;
	 	}
	 	return true;

	 }

	 /************* EIT Calculate SW Setting Function *****************/
	 public boolean CalSWSetting(int Ch, int SW)
	 {
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
	 public boolean EIT_PipelineSystemSetting() throws HeadlessException, IOException
	 {
	 	////////m_pControlUser->UpdateData(TRUE);
	 	int DMNum = 0;
	 	int TriggerNum = 0;
	 	
	 	if(m_nMixedFlag == Definition.MIXED_FREQUENCY)				// Mixed Mode
	 	{
	 		DMNum = 2;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	else											// Single Mode
	 	{
	 		DMNum = 0;								// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}

	 	if(CCSTableDown()!=0) // 0 refer to true
	 	{
	 		JOptionPane.showMessageDialog(null, "CCS Table Down Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	if(ProjectionTalbeDown(m_nTotalProjection)!=0) // 0 refer to true
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

	 	if(!TimeSlotSetting(10))
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

	 	if(!CommFPGASet((m_nOpMode >> 3), TriggerNum , DMNum, m_nTotalProjection-1))
	 	{
	 		JOptionPane.showMessageDialog(null, "CommFPGA Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}

	 	if(!PipelineScanSetting(0,m_nTotalProjection))
	 	{
	 		JOptionPane.showMessageDialog(null, "Pipeline Scan Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}

	 	if(!NewScanModeSetting(m_nOpMode, 0 ))
	 	{
	 		JOptionPane.showMessageDialog(null, "New Scan Mode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
	 	}
	 	return true;
	 }

	 
	 
	 /************* CCS Table Down Function *****************/
	
	 
	 public int CCSTableDown() throws IOException
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



	 	for(int i = 0;i < Definition.MAX_CH; i++)
	 	{
	 		index = 1;
	 		//data[index] = (byte)i;
	 		data[index] = (byte)i;
	 		for(int j = 0; j < Definition.NUM_OF_FREQUENCY + Definition.NUM_OF_MIXED_FREQUENCY; j++)
	 		{
	 			index = 2;
	 			for(int k = 0; k <Definition.CCSTABLEROW; k++)
	 			{
	 				data[index] = (byte)CCSTable[i][j][k] ;
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
	 
	 /************* Projection Table Down Function *****************/
	 public int ProjectionTalbeDown(int Line) throws IOException
	 {
		 int Status =0 ;
	 	//int[] data= new int[Definition.MAX_CH * Definition.PROjECTIONTABLEROW];
	 	byte[] data= new byte[Definition.MAX_CH * Definition.PROjECTIONTABLEROW];
	 	int index = 0;
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



	 	for(int i = 0;i < Line; i++)
	 	{
	 		for(int j = 0; j < Definition.MAX_CH; j++)
	 		{	
	 			index = 1;
	 			for(int k = 0; k <Definition.PROjECTIONTABLEROW; k++)
	 			{
	 				data[index] = (byte)ProjectionTable[i][j][k];
	 				index++;
	 			}

	 			for(int y = 1; y < index; y++)
	 			{
	 				foutProjSend.write((int)(data[y]&0xFF) + "\t");
	 			}

	 			Status = USB.USBComm2(Definition.PROjECTIONTABLEROW + 1, Definition.PROjECTIONTABLEROW + 1, 1, data);

	 			for(int x = 1; x < USB.m_nReadByte; x++)
	 			{
	 				foutProjRecieve.write((int)(USB.m_nResponse[x]  &0xFF) +"\t");  /// check value int or byte 
	 			}
	 			foutProjSend.write( "\r\n");
	 			foutProjRecieve.write("\r\n");
	 	
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

	 /************* Comm FPGA Set Function *****************/
	 public boolean CommFPGASet(int Mode, int ScanNum, int DMNum, int ProjNum)
	 {
	 	
	 	USB.USBComm((byte)Definition.COMMAND_COMMFPGA_SET,(byte) Mode,(byte) ScanNum,(byte) DMNum,(byte)ProjNum);
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
	 
	 /************* Pipeline Scan Setting Function *****************/
	 public boolean PipelineScanSetting(int StartProjNum, int m_nTotalProjection)
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

	 	USB.USBComm((byte)Definition.COMMAND_PIPELINESCAN_SETTING,(byte)m_nMixedFlag, (byte)DMNum,(byte)StartProjNum, (byte)(m_nTotalProjection-1),(byte)m_nTimeInfoHigh,(byte)m_nTimeInfoMid, (byte)m_nTimeInfoLow);
	 	if(USB.m_nResponse[0] != (byte)Definition.RESPONSE_PIPELINESCAN_SETTING)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[1] != (byte)m_nMixedFlag)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[2] != (byte)DMNum)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[3] != (byte)StartProjNum)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[4] != (byte)m_nTotalProjection-1)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[5] != (byte)m_nTimeInfoHigh)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[6] != (byte)m_nTimeInfoMid)
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[7] != (byte)m_nTimeInfoLow)
	 	{
	 		return false;
	 	}
	 	return true;

	 }
	 
	 /************* New Scan Mode Setting Function *****************/
	 public boolean NewScanModeSetting(int OpMode, int RepeatationNum)
	 {
	 	USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	int DMNum = 0;
	 	if(m_nMixedFlag == (byte)Definition.MIXED_FREQUENCY)				// Mixed Mode
	 	{
	 		DMNum = 2;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}
	 	else											// Single Mode
	 	{
	 		DMNum = 0;									// Mixed Freq DM Num : 2  / SingleFreq DM Num : 0
	 	}

	 	USB.USBComm((byte)Definition.COMMAND_NEW_SCAN_MODE,(byte)OpMode,(byte)DMNum,(byte)( m_nTotalProjection-1),(byte) RepeatationNum);
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
	 	if(USB.m_nResponse[3] != (byte)(m_nTotalProjection-1))
	 	{
	 		return false;
	 	}
	 	if(USB.m_nResponse[4] != (byte)RepeatationNum)
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


	 	Command[0] =(byte) Definition.COMMAND_COMM_SCAN_START_STOP;
	 	Command[1] = 1;
	 	USB.USBComm2(10, 0, 0, Command);	// Comm Scan Start (Comm Sending Start Signal)

	 
	 	return true;
	 	
	 }
	 
	 /************* Thread communication Function *****************/
	 public int ThreadComm(EIT_Mark2Doc pDoc)
	 {
		// synchronized (this) 
			{
	 	pDoc.m_bThreadCommWorkingFlag = true;
	 	while(pDoc.m_bThreadCommWorkingFlag)   /// stop in stop button
	 	{
	 		pDoc.USB.ReadData3(pDoc.m_nTotalDataLength);
	 	}
	 	return TRUE;
			}
	 }
	 
	 /************* Thread Process Scan Data Function *****************/
	 public int ThreadDataProcess(EIT_Mark2Doc pDoc)
	 {
		
	 	pDoc.m_bThreadDataProcessWorkingFlag = true;
	 	int ReadByte = 0;
	 	pDoc.m_nReadBlock = 0;
	 	pDoc.m_nThreadDataIndex = 0;

	 	pDoc.m_nTotalCh = Definition.MAX_CH;
	 	
	 	pDoc.m_nPresentScanNum = 0;
	 	int DummyScanNum = 0;
	 	pDoc.m_nDummyScanNum = 0;
	 	double TempReal, TempQuad;
	 	pDoc.m_nPresentWriteNum =0;

	 	
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
	 					try {
							Thread.sleep(5);// with this time test one hour no missing just need to change receive buffer to make smooth display
							// we can make 7 read command or we can change buffer
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	 					pDoc.m_nPresentScanNum = pDoc.m_nPresentWriteNum;           
	 				}
	 			
	 				pDoc.m_nReadBlock++;
	 				pDoc.m_nThreadRQDataIndex = 0;
	 				if(pDoc.m_nThreadDataIndex  >=  pDoc.m_nTotalDataLength)  //////////////// here
	 				{  
	 					pDoc.m_bThreadDataProcessFinishFlag = false;
	 					pDoc.m_bThreadDataProcessFinishFlag2 = false;
	 					for(pDoc.Thread_Freq = 0; pDoc.Thread_Freq < pDoc.m_nTotalFreqNum; pDoc.Thread_Freq++)
	 					{   
	 						pDoc.m_nThreadRQDataIndex = 0;
	 						for(pDoc.Thread_projection = 0; pDoc.Thread_projection < pDoc.m_nTotalProjection; pDoc.Thread_projection++)
	 						{
	 							for(pDoc.Thread_Ch = 0; pDoc.Thread_Ch < pDoc.m_nTotalCh; pDoc.Thread_Ch++)
	 							{
	 								if(pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum * 6) + (pDoc.Thread_Freq * 6) + 1] == pDoc.Thread_Ch )
	 								{   
	 									pDoc.TempVMOverflow[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6)] - 128;
	 									pDoc.TempCh[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6)+ 1];
	 									// check data I change the code //concatenate two bytes 2 3(real) and 4 5(quad)// Remove 0xFF and put in response
	 									pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = ((pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6) + 2] << 8)) + ((pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6) + 3]));
	 									pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = ((pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6)  + 4] << 8))  + ((pDoc.RawData[(pDoc.Thread_Ch * pDoc.m_nTotalFreqNum * pDoc.m_nTotalProjection * 6) + (pDoc.Thread_projection * pDoc.m_nTotalFreqNum* 6) + (pDoc.Thread_Freq * 6) + 5]) );
	 									if(pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] > 32767)
	 									{
	 										pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] - 65536;
	 									}
	 									if(pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] > 32767)
	 									{
	 										pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] - 65536;
	 									}
	 									if(pDoc.m_bVoltmeterCal == TRUE)
	 									{
	 										TempReal = pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex];
	 										TempQuad = pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex];

	 										pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = Math.cos(pDoc.m_dVoltmeterPhaseFactor[pDoc.m_nThreadRQDataIndex]) * TempReal + Math.sin(pDoc.m_dVoltmeterPhaseFactor[pDoc.m_nThreadRQDataIndex]) * TempQuad;
	 										pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = Math.cos(pDoc.m_dVoltmeterPhaseFactor[pDoc.m_nThreadRQDataIndex]) * TempQuad - Math.sin(pDoc.m_dVoltmeterPhaseFactor[pDoc.m_nThreadRQDataIndex]) * TempReal;
	 										pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] * pDoc.m_dVoltmeterMagnitudeFactor[pDoc.m_nThreadRQDataIndex];
	 										pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] * pDoc.m_dVoltmeterMagnitudeFactor[pDoc.m_nThreadRQDataIndex];
	 									}						
	 									pDoc.TempMagnitude[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = Math.sqrt(Math.pow((double)pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex],2)+Math.pow((double)pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex],2));
	 									pDoc.TempPhase[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex] = Math.atan2((double)pDoc.TempQuad[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex],(double)pDoc.TempReal[pDoc.Thread_Freq][pDoc.m_nThreadRQDataIndex]);
	 							
	 								}
	 								pDoc.m_nThreadRQDataIndex++;
	 							}
	 							
	 						}
	 					}
	 				
	 					if(DummyScanNum < 5)
	 					{
	 						DummyScanNum++;
	 						pDoc.m_nDummyScanNum++;
	 					}
	 					else    //start write
	 					{
	 						//System.out.println("inside start write flag");
	 						pDoc.m_bThreadDataProcessFinishFlag = true;
	 						pDoc.m_bThreadDataProcessFinishFlag2 = true;
	 						pDoc.m_nPresentScanNum++;
	 						pDoc.control_gui.UpdateScanNumber(pDoc.m_nPresentScanNum);  ////check
	 					}
	 				
	 					pDoc.m_nReadBlock = 0;

	 					pDoc.m_nWriteFileIndex = pDoc.m_nThreadRQDataIndex;

	 					pDoc.m_nThreadDataIndex = 0;

	 					if(pDoc.m_nPresentScanNum == 1)
	 					{//System.out.println("inside 7 DataProcess thread");
	 						
	 						for(int i = 0; i < 256; i++)
	 						{
	 							pDoc.Image_RefReal[i] = pDoc.TempReal[0][i];
	 							pDoc.Image_RefQuad[i] = pDoc.TempQuad[0][i];
	 							pDoc.Image_RefMag[i] = pDoc.TempMagnitude[0][i];
	 							pDoc.Image_RefTheta[i] = pDoc.TempPhase[0][i];
	 						}
	 					}
	 					if(pDoc.control_gui.m_bImageDlg1)   //////check whan add image
	 					{
	 						if(pDoc.m_bMessageImageReconFinishFlag1 == true)
	 						{
	 							pDoc.control_gui.m_pImageDlg.ImageRecon();
	 						}

	 					}
	 					
	 				
	 				}
	 			
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

	 /************* Thread Write Scan File Function *****************/
	 public int ThreadWriteFile(EIT_Mark2Doc pDoc) throws IOException
	 {
	 synchronized (this) 
	{

				
	 	FileWriter fout = null;
	 	int OF_Counter = 0;
	 	
	 	while(pDoc.m_bThreadWriteFileWorkingFlag)
	 	{ 
		 	pDoc.WriteScanNum++ ;
	 		if(pDoc.m_bThreadDataProcessFinishFlag == true)
	 		{	//System.out.println("inside  1 Data write thread");
	 			if(pDoc.m_bSave)
	 			{	OF_Counter = 0;
	 				for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)
	 				{
	 					for(int i = 0; i < pDoc.m_nWriteFileIndex; i++)
	 					{
	 						pDoc.Real[Freq][i] = -1*pDoc.TempReal[Freq][i];
	 						pDoc.Quad[Freq][i] = -1*pDoc.TempQuad[Freq][i];
	 						pDoc.VMOverflow[Freq][i] = pDoc.TempVMOverflow[Freq][i];
	 						//if(pDoc.VMOverflow[Freq][i] == 1)
	 							//OF_Counter++;
	 						pDoc.ChNum[Freq][i] = pDoc.TempCh[Freq][i];
	 					}
	 				}


	 				////////////////////RQ Data Write///////////////////////////
	 				pDoc.m_strSaveFileName = pDoc.m_strSaveFilePath +pDoc.m_nPresentScanNum+"Scan.txt";
	 				
	 				fout = new FileWriter(pDoc.m_strSaveFileName);
	 				for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)  
	 				{	
	 					for(int i = 0; i < pDoc.m_nThreadRQDataIndex; i++) ///m_nThreadRQDataIndex 16*16=256
	 					{
	 						fout.write((pDoc.ChNum[Freq][i])+"\t"+(pDoc.VMOverflow[Freq][i])+"\t"+ pDoc.Real[Freq][i]+"\t"+pDoc.Quad[Freq][i]);
	 						fout.write("\r\n");
	 					}
	 					
	 			
	 				}
			        Date instant = new Date( System.currentTimeMillis());
			        SimpleDateFormat sdf = new SimpleDateFormat(  "HH\t mm\t ss\t SS\r\n" );
			  
			        // Define the String, time, to be our formatted
			        // view of the milliseconds since the epoch.
			        String time = sdf.format( instant );
			  
			        // Print out this "view" of MSEC_SINCE_EPOCH.
			        fout.write( time );
			        //fout.write("counter  "+OF_Counter+"\r\n");
	 				fout.close();
	 			
	 			}
	 			pDoc.m_nPresentWriteNum++;
	 				pDoc.m_bThreadDataProcessFinishFlag = false;
	 			}
	 				 	 
	 		}
	 
	 	return TRUE;
			}
	 }


	 /** This function to calculate 
	  * real time reciprocity error and contact impedance   
	  * **/
	 int ThreadReciprocityError(EIT_Mark2Doc pDoc) throws IOException
	 {
		// synchronized (this) 
			{
	 	double [][] tempRec_Error = null;
	 	double [] tempRec_Error2 = null;
	 	double [] temp_overflow2 = null;
	 	double [][] temp_overflow = null;
	 	
	 	while(pDoc.m_bThreadRecipErrorWorkingFlag)
	 	{ 
	 		if(pDoc.m_bThreadDataProcessFinishFlag2 == true)
	 		{	
	 			{	
	 			tempRec_Error= new double[256][256];
	 			tempRec_Error2= new double[256];
	 			temp_overflow= new double[256][256];
	 			temp_overflow2= new double[256];
	 		
	 			for(int Freq = 0; Freq < pDoc.m_nTotalFreqNum; Freq++)  
	 				{	
	 				for(int i = 0; i < pDoc.m_nThreadRQDataIndex; i++) ///m_nThreadRQDataIndex 16*16=256
	 					{
	 							tempRec_Error2[i]= pDoc.TempMagnitude[Freq][i];	
	 							temp_overflow2[i] = pDoc.TempVMOverflow[Freq][i];
	 					}
						
	 				}
	 		
	 			
	 			}
	 			for(int i =0 ;i< 16 ; i++)   // Contact Impedance Calculation
	 				{for(int j =0; j< 16 ; j++)
	 				{
	 					tempRec_Error[i][j]=tempRec_Error2[(i*16) + j];     /// check i and j 
	 					temp_overflow[i][j] = temp_overflow2[(i*16) + j];
	 					double l = (j-i)% 16 ;
						if (l<0)
							l=l+16;
						if(l==1)
						{
					
							double gain = (400+(40 * Temp_d_Gain))/50;   // depend on system 50 and 200
							double Mag = tempRec_Error[i][j]/gain;
							double Voltage = ( Mag * 2) / (Math.pow(2, 15));
							Zc[i] = Voltage  / (Temp_d_Amp *Math.pow(10, -6));   /// *2 ADC signal peak to peak ...convert to volt pow(2, 15)... /1000000 convert to Amp .... Round(zc*100)/100 to number after dot
						
						}
	 				}
	 				
	 				}
	 			
	 	
	 			//int count =0;
	 		    Rec_Error_val_temp = 0;
	 			Rec_Error_val = 0;
	 			//int overflow_count = 0;
	 			//Reciprocity Error Calculation
	 			for(int i = 0; i < 16; i++) ///m_nThreadRQDataIndex 16*16=256
					{
						for (int j = i+1; j < 16; j++)
						{
							double l = (j-i)% 16 ;
							if (l<0)
								l=l+16;
							if(l!=0 && l!=1 && l!= (16-1))  // Remove saturation data  0 itself//1 Next //16-1 previous
							{
								Rec_Error_val_temp += (Math.abs(tempRec_Error[i][j]-tempRec_Error[j][i])/((tempRec_Error[i][j]+tempRec_Error[j][i])/2 ))* 100 ;
								//count ++;
								/*if(temp_overflow[i][j]==1)
								{
									overflow_count++;
									  
								}*/
								
							}
						}
						
					}
	 			
	 			Rec_Error_val = Rec_Error_val_temp/104;
	 			if(pDoc.control_gui.m_bRecpDlg1)   //////check when to add graph
					{
						if(pDoc.m_bMessageGraphReconFinishFlag == true)
						{
							pDoc.control_gui.m_p_Imp_GraphDlg.GetRecipError();
						}
						/*if(pDoc.m_bMessageGraphContactImpedanceFinishFlag == true)
						{
							if(pDoc.m_nPresentScanNum % 10 == 0)   // display contact impedance every 10scan 
							{
								pDoc.control_gui.m_p_Rec_Imp_GraphDlg.GetContactImpedance();
							}
						}
*/
					}
				
				pDoc.m_bThreadDataProcessFinishFlag2 = false;
	 			}
	 		}
	 	
	 	return TRUE;
		}
	 }
	 /************* Stop Scan Function *****************/
	 public boolean EIT_PipelineScanStop() throws InterruptedException
	 {

	 	m_bThreadDataProcessWorkingFlag = false;
	 	m_bThreadWriteFileWorkingFlag = false;
	 	m_bThreadRecipErrorWorkingFlag = false;
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
	 	
	 	if(m_hThreadCalRecipError.isAlive())
	 	{
	 		m_bThreadRecipErrorWorkingFlag = false;
	 		Thread.sleep(100);
	 	}

	 	if(m_hThreadComm.isAlive())
	 	{
	 		m_bThreadCommWorkingFlag = false;
	 		USB.m_bBusy = FALSE;
	 		USB.m_bCommBreak = TRUE;
	 	}
	 	Thread.sleep(100);
	 	USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	USB.m_bCommBreak = FALSE;

	 	PipelineScanStop();		// Stop : 0

	 	USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);

	 	return true;
	 }
	 
	 /************* Stop Pipeline Command Function *****************/
	 public boolean PipelineScanStop()
	 {
		 byte[]  Command = new byte[10];//check why 9 
		 Command[0] = (byte)Definition.COMMAND_COMM_SCAN_START_STOP;  
		 Command[1] = 0;
		 USB.USBComm2(10, 0, 0, Command);	// Comm Scan Start (Comm Sending Start Signal)
		 USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
		 return true;
	 }
	 
	 
	 /************* Load Image Algorithm File Function 
	 * @throws IOException *****************/
	 public boolean IMAGE_LoadAlgorithmFile(int toCH) throws IOException
	 {
	 	
	 	String FilePath, AlgorithmName;
	 	AlgorithmName = "Linear";
	 	FilePath = m_strDefaultFilePath + "/" + "Algorithm";
		 FloatBuffer sin_file = null;
			 FloatBuffer sind_file= null;
			 IntBuffer tri_file= null;
	 /* Right way
	  * 	RandomAccessFile f = new RandomAccessFile(FilePath + "/" + AlgorithmName + "/" + "Sen16.bin", "r");
        byte[] bytes = new byte[(int)f.length()];
        f.read(bytes);

        float fl = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();     
      */
	 	
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
	 			


	 	File finDipolX_f,finDipolY_f,finDipolIndex_f;
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
	 	finDipolY.close();

	// 	ifstream fin;
	 	//double Real,Quad,Mag,Phase;
	 	
	/* 	File Fact_f = new File(FilePath + "/" + AlgorithmName + "/" + "index.txt");  ////////////find the file
	 	Scanner Fact_file = new Scanner(Fact_f);
	 	for(int k = 0; k < 256; k++)
	 	{
	 		FactoraizationIndex[k] = 0;
	 	}

	 	for(int j = 0; j < 256; j++)
	 	{
	 		FactoraizationIndex[j] = Fact_file.nextDouble();
	 	//	>> Real >> Quad >> Mag >> Phase >> FactoraizationIndex[j];
	 	}
	 	Fact_file.close();
	 	*/
	 	return true;
	 }
	 /************* Process Delta Sigma Function *****************/
	 public boolean IMAGE_DispProc(int DispData,int toCH, int Algorithm)
	 {
	 	switch (Algorithm)
	 	{
	 	case Definition.ALGORITHM_tSVD:
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
	 public double[] IMAGE_CalcDSigma(int ch, double[] ref_dvol, double[] obj_dvol )  //check ref data
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
			// TODO Auto-generated catch block
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
				for(int i = 0; i < 48; i++)
			 	{
			 			GREIT_RemoveIndex[i] = finRemoveIndex.nextInt(); //check int
			 		
			 	}
				finRemoveIndex.close();
			 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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
			for(int i = 0; i < 40*40; i++)
		 	{
				GREIT_Map[i] = finMap.nextInt(); //check int
		 		
		 	}
			finMap.close();
		 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	return true;
	 }

	 
	 /************* GREIT Algorithm Function *****************/
	 double[] GREITAlgorithm(int TotalCh, double Ref[], double []Obj)//, double[] ImageData) //check ref ImageData may needed to return
	 {
	 	int Index,RemoveIndex;
	 	double Temp;
	 	double[] ImageData = new double[1024*2];
	 	double[] Diff = new double[208];
	 	Index = 0;
	 	RemoveIndex = 0;
	 	Temp = 0;
	 	for(int i = 0; i < 256; i++)
	 	{
	 		if(GREIT_RemoveIndex[RemoveIndex] - 1 != i)
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
	 			Temp += GREIT_RMatrix[i][j] * Diff[j];
	 		}
	 		ImageData[i] = Temp;
	 	}

	 	return ImageData;  //check
	 }
	 
////////// these functions not used now 
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

	 	return true;
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
	 		USB.USBComm2(1,(4*Definition.TOTAL_CH),1,Command);
	 		for(int i = 0; i < Definition.TOTAL_CH; i++)
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

	 public boolean VMDigi1Setting(int Ch, int Digi)
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

	 	return true;
	 }
	 
	 
	 /************* EIT Manual Projection Function *****************/
	 public Object[] EIT_ManualProjection(int Mode, int CurrentProjection, int[] ChInfo, int[] Overflow, double[] Real, double[] Quad) throws IOException
	 {
	 	// TODO: 여기에 컨트롤 알림 처리기 코드를 추가합니다.
	 	int InjGap, InjCNTHigh, InjCNTLow;
	 	int InjChCount;
	 	int SRCChCount = 0,SinkChCount = 0;
	 	int[] SRCCh = new int [16];
	 	int[] SinkCh = new int[16];
	 	int ChCheckSRC,ChCheckSink;


	 	InjChCount = 0;
	 	SinkChCount = 0;
	 	SRCChCount = 0;
	 	
	 	USB_lib.SI_FlushBuffers(USB.m_hUSBDevice[USB.m_nDeviceList],(byte)0,(byte)0);
	 	InjectionDelay(m_nInjDelayHigh,m_nInjDelayLow);
	
	 	OpModeSetting(0,0,0,0,0);

	 	if(!CommFPGASet((m_nOpMode >> 3),0,ProjectionTable[CurrentProjection][0][Definition.TOTALDEMODULATIONFREQUENCY],m_nTotalProjection-1))
	 	{
			JOptionPane.showMessageDialog(null, "CommFPGASet Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return null;

	 	}

	 	if(!PipelineScanSetting(0, Definition.TOTAL_CH))
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
	public int CheckCommnet(char[] Data)
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
	int AmpConvert(double Input, double CalibrationFactor)	
	{
		int Output = (int)Input * (int)CalibrationFactor;
		return Output;
	}
	
	public int[] FreqtoCNTGap(int FreqIndex)
	{	int[] return_data = new int[3];
		switch(FreqIndex)
		{
		case 0:	// 11.25Hz
			return_data[0] = 3;	// CLK_CNT_HITH
			return_data[1] = 231;	// CLK_CNT_LOW
			return_data[2] = 1;	//*Gap_DATA	
			break;
		case 1:	// 56.25Hz
			return_data[0] = 0;
			return_data[1] = 199;
			return_data[2] = 1;
			break;
		case 2:	// 112.5Hz
			return_data[0] = 0;
			return_data[1] = 99;
			return_data[2] = 1;
			break;
		case 3:	// 1.125kHz
			return_data[0] = 0;
			return_data[1] = 9;
			return_data[2] = 1;
			break;
		case 4:	// 5.6kHz
			return_data[0] = 0;
			return_data[1] = 1;
			return_data[2] = 1;
			break;
		case 5:	// 11.25kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 1;
			break;
		case 6:	// 56.25kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 5;
			break;
		case 7:	// 112.2kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 10;
			break;
		case 8:	// 281.25kHz
			return_data[0] = 0;
			return_data[1] = 0;
			return_data[2] = 25;
			break;
		case 9:	// 562.5KHz
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
	int ConvertMultiFreq(int RefCNTHigh, int RefCNTLow, int RefGap, int CompCNTHigh, int CompCNTLow, int CompGap)
	{
		int RefCNT = RefCNTHigh *256 + RefCNTLow + 1;
		int CompCNT = CompCNTHigh *256 + CompCNTLow + 1;
		return  CompGap * (RefCNT/CompCNT);  //OutputGap
	}
	
	
	
}
