package Calibration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JOptionPane;

import MainFunctions.Definition;


public class Voltmeter_Calibration_fun {
	Voltmeter_Calibrations VoltCal;
	  String m_strDefaultFilePath =  System.getProperty("user.dir");
	  String m_strCalibration = "Calibration";
	  String m_strCategory = "Voltmeter";
	  String m_strSimulation = "Simulation";
	  String m_strLog = "Log"; 
	  String m_strUSBID  = "eit1";
	  double[] m_dSimulationData = null;
	  int m_nSimulationDataIndex =0;
	private FileWriter f_out;
	private Scanner fin2;
	public Voltmeter_Calibration_fun(Voltmeter_Calibrations VoltCal) {
		this.VoltCal = VoltCal;
	}
	
	
	boolean VoltmeterCalibrationSystemSetting() throws HeadlessException, InterruptedException, IOException
	{
		if(!Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Reset())
		{
			JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
		}

		if(!Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.CheckNumofIMM())
		{
			JOptionPane.showMessageDialog(null, "IMM Check Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
		}

		if(!Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Average(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.m_nNumofAVG-1))
		{
			JOptionPane.showMessageDialog(null, "Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
		}
		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.OverflowNumber(0xFF,10);
		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.DACSet(255,2,128);

		if(!Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.InjectionDelay(0, 60))
		{
			JOptionPane.showMessageDialog(null, "Injection Delay Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
		}

		if(!Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.CalSWSetting(Definition.ALL_CH,0))
		{
			JOptionPane.showMessageDialog(null, "CCS Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
		}
		int Proj =0 ; 
		int[]Ch = new int[Definition.TOTAL_CH];
		int[] OV = new int[Definition.TOTAL_CH];
		double[] Real = new double[Definition.TOTAL_CH];
		double[] Quad =  new double[Definition.TOTAL_CH];
		Object[] Volt_Data = Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.EIT_ManualProjection(0,Proj,Ch, OV, Real, Quad);
		if(Volt_Data==null)
		{
			JOptionPane.showMessageDialog(null, "Manual Projection Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;		
	  		
		}
		else
		{
			Ch= (int[]) Volt_Data[0];
			OV = (int[]) Volt_Data[1];
			Real = (double[]) Volt_Data[2];
			Quad = (double[]) Volt_Data[3];
		}
		

		return true;
	}
	
	Object[] VoltmeterCalibration(int TotalCh, int TotalProjection, double[] SimulationData, int TotalSimulationDataIndex, double[] MagnitudeFactor, double[] PhaseFactor) throws IOException //return MagnitudeFactor PhaseFactor
	{
		if(TotalCh * TotalProjection != TotalSimulationDataIndex)
		{
			JOptionPane.showMessageDialog(null, "Simulation Data is not match","InfoBox: " + "Error",JOptionPane.INFORMATION_MESSAGE);
			return null;
		}

		int[] Ch = new int[TotalCh*TotalProjection];
		int[] Overflow = new int[TotalCh*TotalProjection];
		double[] Real = new double[TotalCh*TotalProjection];
		double[] Quad = new double[TotalCh*TotalProjection];
		double[] Magnitude = new double[TotalCh*TotalProjection];
		double[] Phase = new double[TotalCh*TotalProjection];

		

		//int TempIndex = 0;
		/*for(int Proj = 0; Proj < TotalProjection; Proj++)
		{/////////////////check maybe index Ch [Proj * TotalCh] or adding
		Object[] Volt_Data = VoltCal.EIT_Control_Dlg.EIT_Control.EIT_ManualProjection(0,Proj,(Ch + (Proj * TotalCh)), (Overflow + (Proj * TotalCh)), (Real + (Proj * TotalCh)), (Quad + (Proj * TotalCh));
			if(Volt_Data==null)
			{
				JOptionPane.showMessageDialog(null, "Manual Projection Fail","InfoBox: " + "Error",JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			else 
			Ch= (int[]) Volt_Data[0];
			Overflow = (int[]) Volt_Data[1];
			Real = (double[]) Volt_Data[2];
			Quad = (double[]) Volt_Data[3];

		}
		String FilePath;
		FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strCategory;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + VoltCal.m_strSimulationFileTitle;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strLog;
		new File(FilePath).mkdirs();
		VoltCal.fout[0]= new FileWriter( FilePath + "//Log.txt");
		
		for(int i = 0; i < TotalCh * TotalProjection; i++)
		{
			Magnitude[i] = Math.sqrt(Math.pow(Real[i] , 2) + Math.pow(Quad[i] , 2));
			Phase[i] = Math.atan2(Quad[i],Real[i]);
			if(Magnitude[i] != 0)
			{
				MagnitudeFactor[i] = SimulationData[i] / Magnitude[i];
			}
			else
			{
				MagnitudeFactor[i] = 1;
			}

			PhaseFactor[i] = Phase[i];
			
			VoltCal.fout[0].write(Ch[i] + "\t" + Overflow[i]+ "\t" + Real[i]*MagnitudeFactor[i] + "\t" + Quad[i]*MagnitudeFactor[i] + "\r\n");
			
		}
		VoltCal.fout[0].close();*/
		/**
		 * 
		 * 
		 * Different way try to directly get and write
		 **/
		int k =0;
		String FilePath;
		FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strCategory;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + VoltCal.m_strSimulationFileTitle;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strLog;
		new File(FilePath).mkdirs();
		f_out = new FileWriter(FilePath + "//Log.txt");
		System.out.println(FilePath + "//Log.txt");
		for(int Proj = 0; Proj < TotalProjection; Proj++)
		{
		Object[] Volt_Data = Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.EIT_ManualProjection(0,Proj,Ch,Overflow , Real, Quad );
			if(Volt_Data==null)
			{
				JOptionPane.showMessageDialog(null, "Manual Projection Fail","InfoBox: " + "Error",JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			else 
			{
				Ch= (int[]) Volt_Data[0];
				Overflow = (int[]) Volt_Data[1];
				Real = (double[]) Volt_Data[2];
				Quad = (double[]) Volt_Data[3];
				for(int i = 0; i < TotalCh; i++)
				{
					Magnitude[i] = Math.sqrt(Math.pow(Real[i] , 2) + Math.pow(Quad[i] , 2));
					Phase[i] = Math.atan2(Quad[i],Real[i]);
					if(Magnitude[i] != 0)
					{
						MagnitudeFactor[k] = SimulationData[i] / Magnitude[i];
					}
					else
					{
						MagnitudeFactor[k] = 1;
					}

					PhaseFactor[k] = Phase[i];
					f_out.write(Ch[i] + "\t" + Overflow[i]+ "\t" + Real[i]*MagnitudeFactor[k] + "\t" + Quad[i]*MagnitudeFactor[k]+"\r\n");
					k++;
				}
			}
		

		}
		f_out.close();
		
		return new Object[]{MagnitudeFactor,PhaseFactor};
	}

	
	Object[] VoltmeterCalibrationPipeline(double[] SimulationData, int TotalSimulationDataIndex, double[] MagnitudeFactor, double[] PhaseFactor,String m_strSimulationFileTitle) throws IOException, HeadlessException, InterruptedException
	{	

		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.EIT_PipelineScan();
		
		if(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.m_bThreadCommWorkingFlag != true)
		{
			return null;
		}
		
		boolean bTest = true;
		while(bTest)
		{
			if(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.m_nDummyScanNum >= 4)
			{
				bTest = false;
			}
		}	
		
		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.EIT_PipelineScanStop();
		String FilePath;
		FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strCategory;
		new File(FilePath).mkdirs();
		System.err.println(FilePath);
		FilePath = FilePath + "//" + m_strSimulationFileTitle;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strLog;
		new File(FilePath).mkdirs();
		VoltCal.fout[0] = new FileWriter(FilePath + "//Log_Pipeline.txt");
		
		for(int i = 0; i < TotalSimulationDataIndex; i++)
		{
			Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Magnitude[0][i] = Math.sqrt(Math.pow(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempReal[0][i] , 2) + Math.pow(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempQuad[0][i] , 2));
			
			Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Phase[0][i] = Math.atan2(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempQuad[0][i],Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempReal[0][i]);
			if(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Magnitude[0][i] != 0)
			{
				MagnitudeFactor[i] = SimulationData[i] / Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Magnitude[0][i];
			}
			else
			{
				MagnitudeFactor[i] = 1;
			}

			PhaseFactor[i] = Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.Phase[0][i];
			VoltCal.fout[0].write(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempCh[0][i] + "\t" +  Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempVMOverflow[0][i] + "\t" + Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempReal[0][i]*MagnitudeFactor[i] + "\t" + Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.TempQuad[0][i]*MagnitudeFactor[i] + "\r\n");

		}
		VoltCal.fout[0].close();
		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.USB_lib.SI_FlushBuffers(Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.USB.m_hUSBDevice[Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.USB.m_nDeviceList],(byte)0,(byte)0);
		Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.USB.USBComm((byte)0);
		return new Object[]{MagnitudeFactor,PhaseFactor};
	}

	
	
	boolean VoltmeterCalWriteFile(String FilePath, String ScriptFileName, String ProtocolFileName ,int TotalIndex, double[] MagnitudeFactor, double[] PhaseFactor) throws IOException
	{
		FileWriter foutMagnitudeFactor, foutPhaseFactor;
		String FileNameMagnitudeFactor = ProtocolFileName + '_' + ScriptFileName + "_MagnitudeFactor.txt";
		String FileNamePhaseFactor = ProtocolFileName + '_' + ScriptFileName + "_PhaseFactor.txt";
		foutMagnitudeFactor = new FileWriter(FilePath + "//" + FileNameMagnitudeFactor);
		foutPhaseFactor =   new FileWriter(FilePath + "//" + FileNamePhaseFactor);

		for(int i = 0; i < TotalIndex; i++)
		{
			foutMagnitudeFactor.write(MagnitudeFactor[i] + "\r\n");
			foutPhaseFactor.write(PhaseFactor[i]+"\r\n");
		}
		foutMagnitudeFactor.close();
		foutPhaseFactor.close();
		return true;
	}
	
	boolean LoadSimulationData(String FilePath)
	{
		 Locale.setDefault(new Locale("ko_KR"));
		char[] Tempcontents = new char[150];
		StringBuilder strTemp = new StringBuilder();
		char[] contents = new char[150];
		int CommentIndex = 0;
		int DataIndex = 0;
		Scanner fin = null;
	    try {

	        fin = new Scanner(new File(FilePath));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		
		  while (fin.hasNextLine()) {
		    	
	            Scanner finSimulationData = new Scanner(fin.nextLine());
	    
	        while (finSimulationData.hasNext()) {
	        	Tempcontents = finSimulationData.next().toCharArray();
	        	CommentIndex = Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.CheckCommnet(Tempcontents);
	        	if(CommentIndex == Definition.NOCOMMENT)	// No Comment
				{	
					strTemp.delete(0, strTemp.length());
					strTemp.append(Tempcontents);
				}
	        	else	// Comment
				{
	        		finSimulationData.next();
					int i;
					for( i = 0; i < CommentIndex; i++)
					{
						contents[i] = Tempcontents[i];
					}
					contents[i] = 0;
					strTemp.append("");
				}
	        	if(!strTemp.toString().equals("")) 
				{
					Double.parseDouble(strTemp.toString());
					DataIndex++;
					
				}
	        }
	        finSimulationData.close();
		  }
		
		fin.close();
		
		
		m_dSimulationData = new double[DataIndex];
		m_nSimulationDataIndex = 0;    /////// check
	    try {

	    	fin2 = new Scanner(new File(FilePath));
	    
		  while (fin2.hasNextLine()) {
		    	
	            Scanner finSimulationData = new Scanner(fin2.nextLine());
	    
	        while (finSimulationData.hasNext()) {
	        	Tempcontents = finSimulationData.next().toCharArray();
	        	CommentIndex = Voltmeter_Calibrations.getEIT_Control_Dlg().EIT_Control.CheckCommnet(Tempcontents);
	        	if(CommentIndex == Definition.NOCOMMENT)	// No Comment
				{	
					strTemp.delete(0, strTemp.length());
					strTemp.append(Tempcontents);
				}
	        	else	// Comment
				{
	        	    finSimulationData.next();
					int i;
					for( i = 0; i < CommentIndex; i++)
					{
						contents[i] = Tempcontents[i];
					}
					contents[i] = 0;
					strTemp.append("");
				}
	        	if(!strTemp.toString().equals("")) 
				{
	        		m_dSimulationData[m_nSimulationDataIndex] = Float.parseFloat(strTemp.toString());
	        		m_nSimulationDataIndex++;
				}
	        }
	        finSimulationData.close();
		  }
		
		fin.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		
		
		return true;
	}



}
