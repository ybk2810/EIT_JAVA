package Calibration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import MainFunctions.Definition;
import USBCommuniction.SiUSBXp;



public class Amp_Calibration_fun {
	 SiUSBXp USB_lib = SiUSBXp.INSTANCE;
	  Amplitude_Calibration AmpCal;
	  String m_strDefaultFilePath =  System.getProperty("user.dir");
	  String m_strCalibration = "Calibration";
	  String m_strCategory = "Amplitude";
	  String m_strLog = "Log"; 
	  String m_strUSBID  = "eit1";
	  double MinMagnitude =0;
	  int SinkAmp =0;
	  int[] Overflow = new int [Definition.Fabric_SEIT_ALL_CH]; 
	  double[] Real = new double [Definition.Fabric_SEIT_ALL_CH]; 
	  double[] Quad  = new double [Definition.Fabric_SEIT_ALL_CH]; 
	  double[] Magnitude = new double [Definition.Fabric_SEIT_ALL_CH];
	  double[] Phase = new double [Definition.Fabric_SEIT_ALL_CH];
		
	  public Amp_Calibration_fun(Amplitude_Calibration AmpCal) {
			this.AmpCal = AmpCal;
		}
	  
	  boolean AmpCalSystemSetting() throws HeadlessException, InterruptedException
	  {

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.Reset())
	  	{
	 		JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CheckNumofIMM())
	  	{
	 		JOptionPane.showMessageDialog(null, "IMM Check Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.Average(AmpCal.m_nAverage-1))
	  	{
	 		JOptionPane.showMessageDialog(null, "Send Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}
	  	Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.OverflowNumber(0xFF,10);
	  	Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.DACSet(255,2,128);

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.TimeSlotSetting(10))
	  	{
			JOptionPane.showMessageDialog(null, "Time Slot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.OpModeSetting(0,0,0,0,0))
	  	{
			JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CommFPGASet(Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode >> 3,0,0,Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_nTotalProjection-1,0))
	  	{
			JOptionPane.showMessageDialog(null, "CommFPGA Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,0,0,0,0,0,0))
	  	{
	 		JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  	}


	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(Definition.ALL_CH, 0, 2))
	  	{
	 		JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	int AmpHigh,AmpLow; 

	  	AmpHigh = ((AmpCal.m_nAmp[0] & 0xFF00) >> 8);
	  	AmpLow = (AmpCal.m_nAmp[0] & 0x00FF);

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp1Setting(Definition.ALL_CH,AmpHigh,AmpLow))
	  	{
			JOptionPane.showMessageDialog(null, "Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp2Setting(Definition.ALL_CH,0,0))
	  	{
			JOptionPane.showMessageDialog(null, "Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.InjectionDelay(0, 15))
	  	{
			JOptionPane.showMessageDialog(null, "Injection Delay Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	  		return false;
	  	}

	  	int CNTHigh, CNTLow, Gap;
		int[] FreqtoCNTGap_data = new int[3];
	  	FreqtoCNTGap_data = Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(AmpCal.m_nFreq);
		CNTHigh = FreqtoCNTGap_data[0];
		CNTLow = FreqtoCNTGap_data[1];
		Gap= FreqtoCNTGap_data[2];	

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
	  	{
			JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.AcqFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
	  	{
			JOptionPane.showMessageDialog(null, "Acq Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.DMFreqSetting(Definition.ALL_CH,0,2,5))
	  	{
			JOptionPane.showMessageDialog(null, "DM Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

	  		return false;
	  	}

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CalSWSetting(Definition.ALL_CH,1))
	  	{
			JOptionPane.showMessageDialog(null, "CCS Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	int GICSW;
	  	GICSW = Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWCal(AmpCal.m_nFreq);
	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Definition.ALL_CH,GICSW))
	  	{
			JOptionPane.showMessageDialog(null, "GIC Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	for(int Ch = 0; Ch < Definition.Fabric_SEIT_ALL_CH; Ch++)
	  	{
	  		if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSOffsetSetting(Ch,Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_nDCOffsetValue[Ch][AmpCal.m_nFreq]))
	  		{
				JOptionPane.showMessageDialog(null, "Offset Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  			return false;
	  		}
	  		if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSDigiSetting(Ch,Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSCalData[Ch][AmpCal.m_nFreq][0], Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSCalData[Ch][AmpCal.m_nFreq][1]))
	  		{
				JOptionPane.showMessageDialog(null, "CCS Digipot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  			return false;
	  		}
	  		if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSCalData[Ch][AmpCal.m_nFreq][2], Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSCalData[Ch][AmpCal.m_nFreq][3]))
	  		{
				JOptionPane.showMessageDialog(null, "GIC Digipot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  			return false;
	  		}
	  	}


	  	if(!AmpCalProjection(Definition.Fabric_SEIT_ALL_CH))
	  	{
	  		return false;
	  	}

	  	return true;
	  }
	  
	  boolean AmpCalProjection(int TotalCh)
	  {
	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.Projection(Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode,Definition.SINGLE_FREQUENCY))
	  	{
			JOptionPane.showMessageDialog(null, "CCS Cal Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.ReadData3(Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_nTotalCh* 6);

	  	for(int Ch = 0; Ch < TotalCh; Ch++)
	  	{
	  		Overflow[Ch] = (Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6]&0xFF) - 128;
	  		Real[Ch] = ((Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 2]&0xFF) << 8) + (Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 3]&0xFF) ;
	  		Quad[Ch] = ((Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 4]&0xFF)  << 8) + (Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 5]&0xFF) ;
	  		if(Real[Ch] > 32767)
	  		{
	  			Real[Ch] = Real[Ch] - 65536;
	  		}
	  		if(Quad[Ch] > 32767)
	  		{
	  			Quad[Ch] = Quad[Ch] - 65536;
	  		}

			USB_lib.SI_FlushBuffers(Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);

	  		if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.WGStop())
	  		{
				JOptionPane.showMessageDialog(null, "WG Stop Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  			return false;
	  		}

	  		Magnitude[Ch] = Math.sqrt(Math.pow(Real[Ch],2)+Math.pow(Quad[Ch],2));    ///////check may refrence
	  		Phase[Ch] = Math.atan2(Quad[Ch],Real[Ch]);								///////check may refrence
	  	}
	  	return true;
	  }
	  boolean GetMultiAmpResponse(int SRCCh, int SRCAmp, int SinkCh, int MinSinkAmp , int MaxSinkAmp, int SinkStep) // I delete REf value
	  {
			int MinIndex =0;
			int Range = MaxSinkAmp - MinSinkAmp;
			int Length = (Range / SinkStep) + 1;

			int[] Amp = new int[Length];
			int[] TempOverflow = new int[Length];
			double[] TempReal = new double[Length];
			double[] TempQuad = new double[Length];
			double[] TempMagnitude = new double[Length];
			double[] TempPhase = new double[Length];
			Amp[0] = MinSinkAmp;

		
			for(int i = 0 ; i < Definition.Fabric_SEIT_ALL_CH; i++)
			{
				if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(Definition.ALL_CH, 0, 0))
				{
					JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}

			if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(SRCCh, 0, 2))
			{
				JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

				return false;
			}

			if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(SinkCh, 0, 3))
			{
				JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}


			for(int i = 0; i < Length;i++)
			{
				double[] ReturnData = new double[5];
				if(i > 0)
				{
					Amp[i] = Amp[i - 1] + SinkStep;
				}
				if(Amp[i] > 1023)
				{
					Amp[i] = 1023;
				}
				else if(Amp[i] < 0)
				{
					Amp[i] = 0;
				}
				ReturnData = GetSingleAmpResponse(SRCCh,SRCAmp,SinkCh,Amp[i],TempOverflow[i],TempReal[i],TempQuad[i],TempMagnitude[i],TempPhase[i]);
				if(ReturnData==null)
				{
					return false;
				}
				TempOverflow[i]= (int)ReturnData[0];
				TempReal[i]= ReturnData[1];
				TempQuad[i]= ReturnData[2];
				TempMagnitude[i]= ReturnData[3];
				TempPhase[i]= ReturnData[4];
				

			}

			Object[] Min_data = FindAbsMinValue(TempMagnitude,Length,MinMagnitude,MinIndex);
			MinMagnitude  = (double) Min_data[0];
			MinIndex = (int) Min_data[1];
			SinkAmp = Amp[MinIndex];

			try {
				AmpCal.fout[SRCCh].write("Amp\t" + "Real\t" + "Quad\t" + "Magnitude\t" + "Phase\t" + "Overflow \r\n");
				for(int i = 0; i < Length; i++)
				{
					AmpCal.fout[SRCCh].write(Amp[i] + "\t" + TempReal[i] + "\t" + TempQuad[i] + "\t" + TempMagnitude[i] + "\t" + TempPhase[i] + "\t" + TempOverflow[i] +"\r\n");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	  double[] GetSingleAmpResponse(int SRCCh, int SRCAmp, int SinkCh, int SinkAmp2, int GoldChOverflow, double GoldChReal, double GoldChQuad, double GoldChMagnitude, double GoldChPhase)
	  {
			int GoldCh = 0;
			int SRCAmpHigh, SRCAmpLow, SinkAmpHigh, SinkAmpLow;
			SRCAmpHigh = ((SRCAmp & 0xFF00) >> 8);
			SRCAmpLow = (SRCAmp & 0x00FF);
			SinkAmpHigh = ((SinkAmp2 & 0xFF00) >> 8);
			SinkAmpLow = (SinkAmp2 & 0x00FF);

			AmpCalSystemSetting2();

			if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp1Setting(SRCCh,SRCAmpHigh,SRCAmpLow))
			{
				JOptionPane.showMessageDialog(null, "Amp Cal Source Amp Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp1Setting(SinkCh,SinkAmpHigh,SinkAmpLow))
			{
				JOptionPane.showMessageDialog(null, "Amp Cal Sink Amp Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			if(!AmpCalProjection(Definition.Fabric_SEIT_ALL_CH))
			{
				JOptionPane.showMessageDialog(null, "Amp Cal Sink Amp Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			
			double[] ReturnData = new double[5];
			ReturnData[0] = Overflow[GoldCh];
			ReturnData[1] = Real[GoldCh];
			ReturnData[2] = Quad[GoldCh];
			ReturnData[3] = Magnitude[GoldCh];
			ReturnData[4] = Phase[GoldCh];
		  return ReturnData;
	  }
	  

	  boolean AmpCalSystemSetting2()
	  {
	  	int CNTHigh, CNTLow, Gap;
	  	int[] FreqtoCNTGap_data = new int[3];
	  	FreqtoCNTGap_data = Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(AmpCal.m_nFreq);
		CNTHigh = FreqtoCNTGap_data[0];
		CNTLow = FreqtoCNTGap_data[1];
		Gap= FreqtoCNTGap_data[2];	

	  	if(!Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
	  	{
			JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	  		return false;
	  	}

	  	return true;
	  }
	  
	  boolean ConvertAmpCalibrationData()
	  {
	  	FileWriter foutAmpCalibration;
	  	String FilePath, FileName;
	  	FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
	  	new File(FilePath).mkdirs();
	  	FilePath = FilePath + "//" + m_strUSBID;
	  	new File(FilePath).mkdirs();
	  	FilePath = FilePath + "//" + m_strCategory;
	  	FileName = "AmplitudeValue.txt";
	  	try {
			foutAmpCalibration = new FileWriter(FilePath + "//" + FileName);
			for(int Ch = 0; Ch < Definition.Fabric_SEIT_ALL_CH; Ch++)
		  	{
		  		for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
		  		{
		  			foutAmpCalibration.write(Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_dAmplitudeCalibrationFactor[Ch][Freq]+"\t");
		  			foutAmpCalibration.write("\r\n");
		  		}
		  	}

		  	foutAmpCalibration.close();
		  	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	return true;
	  }
	  
	  
	  boolean AmpCalWriteFile(int TotalCh, String FilePath ,double[] Factor)
	  {
	  	String FileName;
	  	FileName = "//Amplitude.txt";
	  	FileWriter foutAmplitudeCalibration;
		try {
			foutAmplitudeCalibration = new FileWriter(FilePath + FileName);
			for(int Ch = 0; Ch < TotalCh; Ch++)
		  	{
		  		foutAmplitudeCalibration .write(Factor[Ch] + "\r\n") ;
		  		Amplitude_Calibration.getEIT_Control_Dlg().EIT_Control.m_dAmplitudeCalibrationFactor[Ch][AmpCal.m_nFreq] = Factor[Ch];
		  	}
		  	foutAmplitudeCalibration.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  

	  	return true;
	  }
	  Object[] FindAbsMinValue(double[] dArr, int Length, double MinMagnitude, int MinIndex)
	  {
		MinMagnitude = Math.abs(dArr[0]);
	  	MinIndex = 0;
	  	for(int i = 0; i < Length; i++)
	  	{
	  		if(Math.abs(dArr[i]) < MinMagnitude)
	  		{
	  			MinMagnitude = Math.abs(dArr[i]);
	  			MinIndex = i;
	  		}
	  	}
	  	return new Object[]{MinMagnitude,MinIndex} ;
	  }

}
