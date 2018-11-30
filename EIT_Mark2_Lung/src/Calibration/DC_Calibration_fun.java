package Calibration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import MainFunctions.Definition;
import MainFunctions.EIT_Mark2Doc;
import USBCommuniction.SiUSBXp;


public class DC_Calibration_fun {
	
    EIT_Mark2Doc m_pDoc ;
    SiUSBXp USB_lib = SiUSBXp.INSTANCE;
    DCoffSet_Calibration DcOffCal;
	int[]  m_nOverflow= new int[Definition.TOTAL_CH];
	int[]  m_nReal= new int[Definition.TOTAL_CH];
	int[]  m_nQuad= new int[Definition.TOTAL_CH];
	
	int[]  m_nDCOffsetValue= new int[Definition.TOTAL_CH];
	long[] m_nAccumulatedData = new long [Definition.TOTAL_CH];
	long[] m_nAccumulatedGNDData = new long[Definition.TOTAL_CH];
	
	String m_strUSBID  = "eit1";
	String m_strDefaultFilePath =  System.getProperty("user.dir");
	String m_strFreq ;
	String m_strFileName;
	String m_strCalibration = "Calibration";
	String m_strCategory = "DCOffset";
	String m_strLog = "Log";
	int[] FreqtoCNTGap_data = new int[3];
	FileWriter[] fout = new FileWriter[Definition.TOTAL_CH];
	
	public DC_Calibration_fun(DCoffSet_Calibration DcOffCal) {
		this.DcOffCal = DcOffCal;
		//this.m_pDoc = DcOffCal.EIT_Control_Dlg.EIT_Control;
	}
	
	boolean DCOffsetCalSystemSetting() throws HeadlessException, InterruptedException
		{

		 	if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.Reset())
		 	{
		 		JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 	else if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CheckNumofIMM())
		 	{
		 		JOptionPane.showMessageDialog(null, "IMM Check Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		 	else if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.Average(DcOffCal.m_nAverage - 1))
		 	{
		 		JOptionPane.showMessageDialog(null, "Send Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
		 	}
		
		 	DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.OverflowNumber(0xFF,10);
		 	DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.DACSet(255,2,128);
		
		    if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,1,1,0,0,1,0))
		 	{
		 		JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 	}

		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.TimeSlotSetting(10))
			{
				JOptionPane.showMessageDialog(null, "Time Slot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		
		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.OpModeSetting(0,0,0,0,0))
			{
				JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CommFPGASet(DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode >> 3,0,0,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.m_nTotalProjection-1))
			{
				JOptionPane.showMessageDialog(null, "CommFPGA Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
			}
		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.InjectionDelay(0, 60))
			{
				JOptionPane.showMessageDialog(null, "InjectionDelay Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
		 		return false;
			}
		
			int AmpHigh,AmpLow;
			int TempCh;
			int CNTHigh =0, CNTLow = 0, Gap =0 ;
			
		
			for(int i = 0; i < Definition.TOTAL_CH; i++)
			{
				AmpHigh = ((DcOffCal.m_nAmp[i] & 0xFF00) >> 8);
				AmpLow = (DcOffCal.m_nAmp[i] & 0x00FF);
		
				if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp1Setting(i,AmpHigh,AmpLow))
				{
					JOptionPane.showMessageDialog(null, "Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp2Setting(i,AmpHigh,AmpLow))
				{
					JOptionPane.showMessageDialog(null, "Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

					return false;
				}
		
				TempCh = i + 1;
				if(TempCh > 15)
				{
					TempCh = 0;
				}
		
		
				if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(i, 0, 2))
				{
					JOptionPane.showMessageDialog(null, "Source, Sink Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

					return false;
				}
			
				//DcOffCal.EIT_Control_Dlg.EIT_Control.FreqtoCNTGap(m_nFreq[i],CNTHigh,CNTLow,Gap); ////////////////////////////////////////////////////check
				FreqtoCNTGap_data = DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(DcOffCal.m_nFreq[i]);
				CNTHigh = FreqtoCNTGap_data[0];
				CNTLow = FreqtoCNTGap_data[1];
				Gap= FreqtoCNTGap_data[2];	
				if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(i,Gap,CNTHigh,CNTLow))
				{
					JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.AcqFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
			{
				JOptionPane.showMessageDialog(null, "Acq Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.DMFreqSetting(Definition.ALL_CH,0,2,5))
			{
				JOptionPane.showMessageDialog(null, "DM Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CalSWSetting(Definition.ALL_CH,1))
			{
				JOptionPane.showMessageDialog(null, "CCS Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		boolean m_bInitCCSGIC = false;
			for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
			{
				if(!m_bInitCCSGIC)  //from hidden check box
				{
					if(DcOffCal.m_nFreq[Ch] == 6)									// GIC3 + 50kHz
					{
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,4))
						{
							JOptionPane.showMessageDialog(null, "GIC SW Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
		
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][11],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][12]))
						{
							JOptionPane.showMessageDialog(null, "GIC Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
					else if(DcOffCal.m_nFreq[Ch] == 7)								// GIC3 + 100kHz
					{
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,4))
						{
							JOptionPane.showMessageDialog(null, "GIC SW Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
		
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][11],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][12]))
						{
							JOptionPane.showMessageDialog(null, "GIC Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
					else if(DcOffCal.m_nFreq[Ch] == 8)								// GIC2 + 250kHz
					{
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,2))
						{
							JOptionPane.showMessageDialog(null, "GIC SW Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
		
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][9],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][10]))
						{
							JOptionPane.showMessageDialog(null, "GIC Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
					else if(DcOffCal.m_nFreq[Ch] == 9)								// GIC1 + 450kHz
					{
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,1))
						{
							JOptionPane.showMessageDialog(null, "GIC SW Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
		
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][7],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][8]))
						{
							JOptionPane.showMessageDialog(null, "GIC Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
					else
					{
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,8))					// GIC4 + 10kHz + others
						{
							JOptionPane.showMessageDialog(null, "GIC SW Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
		
						if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][13],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][14]))
						{
							JOptionPane.showMessageDialog(null, "GIC Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
							return false;
						}
					}
				}
				
				if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSDigiSetting(Ch,DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][5],DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][DcOffCal.m_nFreq[Ch]][6]))
				{
					JOptionPane.showMessageDialog(null, "CCS Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				/////Remove GIC part
				DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSOffsetSetting(Ch,-300);
			}
			m_nOverflow = DCOffsetProjection(Definition.TOTAL_CH,m_nOverflow);
			if(m_nOverflow == null)
			{
				JOptionPane.showMessageDialog(null, "DC Offset Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		
	return true;
}
	int[] DCOffsetProjection(int TotalCh, int[] Overflow)
	{
		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.Projection(DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode,1))
		{
			JOptionPane.showMessageDialog(null, "Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return null;
		}

		DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.ReadData3(TotalCh * 6);
		
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			Overflow[Ch] = (DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 0]&0xFF) - 128;
			m_nReal[Ch] = ((DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 2]&0xFF) << 8) + (DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 3]&0xFF);
			m_nQuad[Ch] = ((DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 4]&0xFF) << 8) + (DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 5]&0xFF);
			if(m_nReal[Ch] > 32767)
			{
				m_nReal[Ch] = m_nReal[Ch] - 65536;
			}
			if(m_nQuad[Ch] > 32767)
			{
				m_nQuad[Ch] = m_nQuad[Ch] - 65536;
			}
		}
		
		USB_lib.SI_FlushBuffers(DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.USB.dev_handle_ref.getValue(),(byte)0,(byte)0);

		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.WGStop())
		{
			JOptionPane.showMessageDialog(null, "WG Stop Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return null;
		}

		return Overflow;
	}

	boolean DCOffsetGNDValue(int TotalCh)
	{
		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,1,1,0,0,1,0))	// SW1, SW4 are GND switch
		{
			JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}


		m_nOverflow = DCOffsetProjection(Definition.TOTAL_CH,m_nOverflow);
		if(m_nOverflow == null)			// Dummy Proj.
		{
			JOptionPane.showMessageDialog(null, "DCOffset Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}
		

		m_nOverflow = DCOffsetProjection(Definition.TOTAL_CH,m_nOverflow);
		if(m_nOverflow == null)
		{
			JOptionPane.showMessageDialog(null, "DCOffset Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}

		m_nOverflow = DCOffsetProjection(Definition.TOTAL_CH,m_nOverflow);
		if(m_nOverflow == null)
		{
			JOptionPane.showMessageDialog(null, "DCOffset Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}


		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.StartOffsetCal(Definition.ALL_CH))
		{
			JOptionPane.showMessageDialog(null, "Start OffsetCal Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}
		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.SendOffsetData(Definition.ALL_CH))
		{
			JOptionPane.showMessageDialog(null, "Send OffsetData Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}
		m_nAccumulatedGNDData = DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.SendOffsetVal(Definition.ALL_CH,m_nAccumulatedGNDData);
		/*{
			JOptionPane.showMessageDialog(null, "Send OffsetVal Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}*/
		
		for(int j = 0; j < TotalCh; j++)
		{
			m_nAccumulatedGNDData[j] = m_nAccumulatedGNDData[j] / (DcOffCal.m_nAverage);
		}
		return true;
	}
	
	boolean DCOffset() throws HeadlessException, InterruptedException, IOException
	{
		
		m_strUSBID ="eit1";//DcOffCal.EIT_Control_Dlg.EIT_Control.USB.m_strDeviceString[DcOffCal.EIT_Control_Dlg.EIT_Control.USB.m_nDeviceList];
        m_strFreq = DcOffCal.Freq_Comb.getSelectedItem().toString();
		String FilePath;
		FilePath = m_strDefaultFilePath + "\\" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strCategory;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strFreq;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strLog;
		new File(FilePath).mkdirs();
		
	    

		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			m_strFileName= "DCOffset"+(i+1)+"Ch.txt";
			m_strFileName = FilePath + "\\" + m_strFileName;
			
			fout[i] = new FileWriter(m_strFileName);
			//////// check number of file 
			fout[i].write( "OffsetValue" +"\t" + "AccumulatedValue" + "\t" +"Overflow" +"\r\n");
		}

		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,1,0,0,0,1,1))	// SW1, SW4 are GND switch
		{
			JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,0,0,0,0,0,0))	// SW1, SW4 are GND switch OFF
		{

			JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		int[] Min = new int [Definition.TOTAL_CH];
		int[] Max = new int [Definition.TOTAL_CH];
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			Max[i] = 300;
			Min[i] = -300;
		}
		
		Object[] DC_DataLoop1 = DCOffsetLoop(Min,Max,100,m_nDCOffsetValue,m_nAccumulatedData,m_nAccumulatedGNDData);
		m_nDCOffsetValue = (int[]) DC_DataLoop1[0];
		m_nAccumulatedData  = (long[]) DC_DataLoop1[1];
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			Max[i] = m_nDCOffsetValue[i] + 100;
			Min[i] = m_nDCOffsetValue[i] - 100;
			fout[i].write("\r\n"); 
		}
		
		Object[] DC_DataLoop2 =  DCOffsetLoop(Min,Max,10,m_nDCOffsetValue,m_nAccumulatedData,m_nAccumulatedGNDData);
		m_nDCOffsetValue = (int[]) DC_DataLoop2[0];
		m_nAccumulatedData  = (long[]) DC_DataLoop2[1];
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			Max[i] = m_nDCOffsetValue[i] + 10;
			Min[i] = m_nDCOffsetValue[i] - 10;
			fout[i].write("\r\n"); 
		}


		Object[] DC_DataLoop3 = DCOffsetLoop(Min,Max,1,m_nDCOffsetValue,m_nAccumulatedData,m_nAccumulatedGNDData);
		m_nDCOffsetValue = (int[]) DC_DataLoop3[0];
		m_nAccumulatedData  = (long[]) DC_DataLoop3[1];

		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			fout[i].write("GNDAccumulatedData\t" + m_nAccumulatedGNDData[i] + "\t" + "MinAccumulatedData\t" + m_nAccumulatedData[i] + "\t" + "MinDCOffsetValue\t" + m_nDCOffsetValue[i] + "\r\n");
			fout[i].close();
		}

		FilePath = m_strDefaultFilePath + "\\" + m_strCalibration + "\\" + m_strUSBID + "\\" + m_strCategory + "\\" + m_strFreq;
		DCOffsetWriteFile(Definition.TOTAL_CH,m_nDCOffsetValue,FilePath);
		ConvertDCCalibrationData();
		
		return true;
	}
	//boolean DCOffsetLoop(int[] RangeMin, int[] RangeMax, int Step, int[] OffsetValue, long[] AccumulatedData, long[] AccumulatedGNDData) throws HeadlessException, InterruptedException, IOException

	Object[] DCOffsetLoop(int[] RangeMin, int[] RangeMax, int Step,int[] OffsetValue, long[] AccumulatedData,  long[] AccumulatedGNDData) throws HeadlessException, InterruptedException, IOException
	{
		int Range, Length;
		Range = RangeMax[0] - RangeMin[0];
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			if((RangeMax[i] - RangeMin[i]) > Range)
			{
				Range = RangeMax[i] - RangeMin[i];
			}

		}
		Length = (Range / Step) + 1;

		int[][] TempOffsetValue;
		long[][] TempAccumulatedData;
		int[] OffsetIndex;
		long[] Temp = new long[Definition.TOTAL_CH];

		TempAccumulatedData = new long[Definition.TOTAL_CH][Length];
		TempOffsetValue = new int[Definition.TOTAL_CH][Length];
		OffsetIndex = new int[Definition.TOTAL_CH];

		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
		//////////////////	TempAccumulatedData[i] = new long[Length];
		/////////////////	TempOffsetValue[i] = new int[Length];
		}

		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			TempOffsetValue[i][0] = RangeMin[i];
		}
		
		for(int i = 0; i < Length; i++)
		{
			DCOffsetCalSystemSetting2();
			for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
			{
				if(i == 0)
				{
					TempOffsetValue[Ch][i] = TempOffsetValue[Ch][0];
				}
				else
				{
					TempOffsetValue[Ch][i] = TempOffsetValue[Ch][i-1] + Step;
				}

				if(TempOffsetValue[Ch][i] > 1023)
				{
					TempOffsetValue[Ch][i] = 1023;
				}
				else if(TempOffsetValue[Ch][i] < -1023)
				{
					TempOffsetValue[Ch][i] = -1023;
				}
				
			
				DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CCSOffsetSetting(Ch,TempOffsetValue[Ch][i]);
		
			}

			m_nOverflow = DCOffsetProjection(Definition.TOTAL_CH,m_nOverflow);	
			
			
			
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.StartOffsetCal(Definition.ALL_CH))
			{
				JOptionPane.showMessageDialog(null, "Start Offset Cal Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

				return null;
			}

			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.SendOffsetData(Definition.ALL_CH))
			{
				JOptionPane.showMessageDialog(null, "Send Offset Data Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			
			Temp = DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.SendOffsetVal(Definition.ALL_CH,Temp);
			if(Temp == null)
			{
				JOptionPane.showMessageDialog(null, "Send Offset Val Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}

			for(int j = 0; j < Definition.TOTAL_CH; j++)
			{
				Temp[j] = Temp[j] / DcOffCal.m_nAverage;
				TempAccumulatedData[j][i] = Temp[j];
			}

		}

		
		Object[] Sim_data = FindAbsSimilarValue(TempAccumulatedData,Definition.TOTAL_CH,Length,AccumulatedGNDData,AccumulatedData,OffsetIndex); 
		OffsetIndex = (int[]) Sim_data[0];
		AccumulatedData = (long[]) Sim_data[1];
		
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{
			for(int j = 0; j < Length; j++)
			{
				fout[i].write(TempOffsetValue[i][j] + "\t" + TempAccumulatedData[i][j] + "\r\n");
			}
		}


		for(int j = 0; j < Definition.TOTAL_CH; j++)
		{

			OffsetValue[j] = TempOffsetValue[j][OffsetIndex[j]];
		}


	

		return new Object[]{OffsetValue,AccumulatedData};
	}

	
	boolean DCOffsetCalSystemSetting2() throws HeadlessException, InterruptedException
	{
		if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.CheckNumofIMM())
		{
			JOptionPane.showMessageDialog(null, "Check IMM Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

		}

		int CNTHigh, CNTLow, Gap;
		int[] FreqtoCNTGap_data = new int[3];
		for(int i = 0; i < Definition.TOTAL_CH; i++)
		{

			FreqtoCNTGap_data = DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(DcOffCal.m_nFreq[i]);
			CNTHigh = FreqtoCNTGap_data[0];
			CNTLow = FreqtoCNTGap_data[1];
			Gap= FreqtoCNTGap_data[2];
			if(!DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(i,Gap,CNTHigh,CNTLow))
			{
				JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

				return false;
			}
		}
		return true;
	}
	
	
	//boolean FindAbsSimilarValue(long[][]dArr, int TotalCh, int Length, long[] RealValue, long[] SimilarValue, int[] SimilarValueIndex)  ///should return  SimilarValue,SimilarValueIndex
	Object[] FindAbsSimilarValue(long[][]dArr, int TotalCh, int Length, long[] RealValue,  long[] SimilarValue, int[] SimilarValueIndex)  ///should return  SimilarValue,SimilarValueIndex
	{
		double[] CompareValue, DiffValue;
		CompareValue = new double[TotalCh];
		DiffValue = new double[TotalCh];
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			CompareValue[Ch] = RealValue[Ch] - dArr[Ch][0];
			SimilarValue[Ch] =(long) Math.abs(CompareValue[Ch]);
			SimilarValueIndex[Ch] = 0;
			for(int i = 0; i < Length; i++)
			{
				DiffValue[Ch] = RealValue[Ch] - dArr[Ch][i];
				if(Math.abs(DiffValue[Ch]) < CompareValue[Ch])
				{
					SimilarValue[Ch] = dArr[Ch][i];
					CompareValue[Ch] = Math.abs(DiffValue[Ch]);
					SimilarValueIndex[Ch] = i;
				}
			}
		}
	
		return new Object[]{SimilarValueIndex,SimilarValue};
	}
	
	boolean DCOffsetWriteFile(int TotalCh, int[] Value, String FilePath) throws IOException
	{
		FileWriter foutDCOffsetValue;
		String FileName;
		FileName = "\\DCOffset.txt";
		foutDCOffsetValue = new FileWriter(FilePath+FileName);
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			foutDCOffsetValue.write(Value[Ch] +"\r\n");
		}
		foutDCOffsetValue.close();

		return true;
	}
	
	boolean ConvertDCCalibrationData() throws IOException
	{
		DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(1,1,0,0); 
		FileWriter foutDCOffset;
		String FilePath, FileName;
		FilePath = m_strDefaultFilePath + "\\" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strCategory;
		FileName = "DCOffsetValue.txt";
		foutDCOffset = new FileWriter(FilePath + "\\" + FileName);
		for(int Ch = 0; Ch < Definition.TOTAL_CH; Ch++)
		{
			for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
			{
				foutDCOffset.write(DCoffSet_Calibration.getEIT_Control_Dlg().EIT_Control.DCCalData[Ch][Freq]+"\t");
				foutDCOffset.write("\r\n"); 
			}
		}

		foutDCOffset.close();
		return true;
	}
}

