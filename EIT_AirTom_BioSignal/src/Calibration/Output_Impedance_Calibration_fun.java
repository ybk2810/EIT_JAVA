package Calibration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import MainFunctions.Definition;



public class Output_Impedance_Calibration_fun {
	int m_nAmp;
	String m_strFreq;
	String m_strUSBID;
	int[] FreqtoCNTGap_data = new int[3];
	//int[]  m_nFreq= new int[Definition.Fabric_SEIT_ALL_CH]; 
	Output_Impedance_Calibration Output_imp_cal;
	String m_strDefaultFilePath =  System.getProperty("user.dir");
	String m_strCalibration = "Calibration";
	String m_strCategory = "OutputImpedance";
	String m_strLog ="Log";
	String m_strFilePath;
	FileWriter[] foutCout = new FileWriter[Definition.Serial_EIT_ALL_CH];
	FileWriter[] foutRout = new FileWriter[Definition.Serial_EIT_ALL_CH];
	FileWriter[] foutZout = new FileWriter[Definition.Serial_EIT_ALL_CH];

	double[]  TempZout = new double[Definition.Serial_EIT_ALL_CH];
	double[] TempRout = new double[Definition.Serial_EIT_ALL_CH];
	double[] TempCout = new double[Definition.Serial_EIT_ALL_CH];
	
	
	public Output_Impedance_Calibration_fun(Output_Impedance_Calibration Output_imp_cal) {
		this.Output_imp_cal = Output_imp_cal;
		this.m_nAmp = 200;
	}
	
	boolean CCSCalSystemSetting() throws HeadlessException, InterruptedException
	{

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.Reset())
		{
			JOptionPane.showMessageDialog(null, "Reset Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
		}

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CheckNumofIMM())
		{
			//AfxMessageBox("Check IMM Error");
			//return FALSE;
		}

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.Average(Output_imp_cal.m_nAverage-1))
		{
			JOptionPane.showMessageDialog(null, "Average Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
	 		return false;
		}
		Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.OverflowNumber(0xFF,10);
		Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.DACSet(255,2,128);

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.OpModeSetting(0,0,0,0,0))
		{
			JOptionPane.showMessageDialog(null, "OpMode Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

		}

		/*if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CommFPGASet(Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode >> 3,0,0,Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.m_nTotalProjection-1,0))
		{
			JOptionPane.showMessageDialog(null, "Comm FPGA Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}*/

		/*if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.AnalogBackplaneSWSetting(Definition.ALL_CH,0,0,0,0,0,0))
		{
			JOptionPane.showMessageDialog(null, "AnalogBackplane Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

		}*/
		
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.MuxBoardControl(1,Output_imp_cal.Cal1_ch,Output_imp_cal.Cal2_ch))// GND SW, source, sink 
		{
			JOptionPane.showMessageDialog(null, "Mux Control Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

		}

		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)		
		{
			if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.WG_ChSetting(Ch, 0, 2))
			{
				JOptionPane.showMessageDialog(null, "Source Ch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}	
		}

		int AmpHigh,AmpLow;

		AmpHigh = ((m_nAmp & 0xFF00) >> 8);
		AmpLow = (m_nAmp & 0x00FF);

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp1Setting(Definition.ALL_CH,AmpHigh,AmpLow))
		{
			JOptionPane.showMessageDialog(null, "Amp1 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSAmp2Setting(Definition.ALL_CH,AmpHigh,AmpLow))
		{
			JOptionPane.showMessageDialog(null, "Amp2 Set Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}


		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.InjectionDelay(0, 60))
		{
			JOptionPane.showMessageDialog(null, "Injection Delay Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!CCSCalSWSetting(Definition.ALL_CH,1))	// Low
		{
			return false;
		}

		int CNTHigh, CNTLow, Gap;
		FreqtoCNTGap_data = Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(Output_imp_cal.m_nFreq); 
		CNTHigh = FreqtoCNTGap_data[0];
		CNTLow = FreqtoCNTGap_data[1];
		Gap= FreqtoCNTGap_data[2];	
		
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
		{
			JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.AcqFreqSetting(Definition.ALL_CH,Gap,CNTHigh,CNTLow))
		{
			JOptionPane.showMessageDialog(null, "Acq Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.DMFreqSetting(Definition.ALL_CH,0,2,5))
		{
			JOptionPane.showMessageDialog(null, "Acq Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!CCSCalGICSWSetting(Definition.ALL_CH, Output_imp_cal.m_nGICSelect))
		{
			return false;
		}

		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
		{
			int Offset; 
			//if(!m_bNoDCOffsetCal)  //////////////////// check
			{
				Offset = (Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][Output_imp_cal.m_nFreq][15] << 8) + Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSTable[Ch][Output_imp_cal.m_nFreq][16];

				if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSOffsetSetting(Ch,Offset))
				{
					JOptionPane.showMessageDialog(null, "Offset Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
			/*//else
			{
				if(!Output_imp_cal.EIT_Control_Dlg.EIT_Control.CCSOffsetSetting(Ch,0))
				{
					JOptionPane.showMessageDialog(null, "Offset Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}*/
		}

		int[] OV =  new int[Definition.Serial_EIT_ALL_CH];
		int [] Real =  new int[Definition.Serial_EIT_ALL_CH];
		int[] Quad =  new int[Definition.Serial_EIT_ALL_CH];
		if(!CCSCalProjection(Definition.Serial_EIT_ALL_CH,OV,Real,Quad))
		{
			return false;
		}
		return true;
	}

	boolean CCSCalSWSetting(int Ch, int SW)
	{
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CalSWSetting(Ch,SW))
		{
			JOptionPane.showMessageDialog(null, "CCS Cal SW Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}
	
	boolean CCSCalGICSWSetting(int Ch, int GICSelect)
	{
		int SW = 0;
		switch(GICSelect)
		{
		case 0:					// None
			SW = 0;
			break;
		case 1:					// GIC1
			SW = 1;
			break;
		case 2:					// GIC2
			SW = 2;
			break;
		case 3:					// GIC3
			SW = 4;
			break;
		case 4:					// GIC4
			SW = 8;
			break;
		default:
			SW = 8;				// Default is GIC4 status
			break;
		}
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.GICSWSet(Ch,SW))
		{
			JOptionPane.showMessageDialog(null, "GIC Switch Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}
		return true;
	}
	boolean CCSCalProjection(int TotalCh, int[] Overflow, int[] Real, int[] Quad)
	{
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.Projection(Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.m_nOpMode,1))
		{
			JOptionPane.showMessageDialog(null, "Projection Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

//		Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.ReadData3(Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.m_nTotalCh* 6);
		Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.ReadData3(Definition.Serial_EIT_ALL_CH * 6);
		
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			// Channel ,overflow,real,quad 6 byte
			Overflow[Ch] = (Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6]&0xFF);
			Real[Ch] = ((Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 2]&0xFF) << 8) + (Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 3]&0xFF);
			Quad[Ch] = ((Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 4] &0xFF)<< 8) + (Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.USB.m_nResponse[Ch * 6 + 5]&0xFF);
			if(Real[Ch] > 32767)
			{
				Real[Ch] = Real[Ch] - 65536;
			}
			if(Quad[Ch] > 32767)
			{
				Quad[Ch] = Quad[Ch] - 65536;
			}
		}

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.WGStop())
		{
			JOptionPane.showMessageDialog(null, "WG Stop Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		return true;
	}
	
	
	boolean CCSCalOpenFile() throws IOException
	{
		m_strFreq = Output_imp_cal.FreqComb.getSelectedItem().toString();
		m_strUSBID ="eit1";
		String FilePath;
		FilePath = m_strDefaultFilePath + "\\" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strCategory;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strFreq;
		m_strFilePath = FilePath;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "\\" + m_strLog;
		new File(FilePath).mkdirs();
		
		String[]  LogFilePath = new String[3];
		String[]  LogFileName = new String[3];
		LogFilePath[0] = FilePath + "\\Zout";
		LogFilePath[1] = FilePath + "\\Rout";
		LogFilePath[2] = FilePath + "\\Cout";
		new File(LogFilePath[0]).mkdirs();
		new File(LogFilePath[1]).mkdirs();
		new File(LogFilePath[2]).mkdirs();
		

		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			LogFileName[0] = LogFilePath[0] + "\\Zout"+(i+1)+"Ch.txt";
			LogFileName[1] = LogFilePath[1] + "\\Rout"+(i+1)+"Ch.txt";
			LogFileName[2] = LogFilePath[2] + "\\Cout"+(i+1)+"Ch.txt";
			foutZout[i] = new FileWriter(LogFileName[0]);
			foutRout[i] = new FileWriter(LogFileName[1]);
			foutCout[i] = new FileWriter(LogFileName[2]);
		}
		return true;
	}
	Object[] CCSCalibration(int[] CoarseR, int[] FineR, int[] CoarseC, int[] FineC) throws IOException  /////////////// check
	{
		int[] TempSelect1 = new int [Definition.Serial_EIT_ALL_CH];
		int[] TempSelect2 = new int[Definition.Serial_EIT_ALL_CH];
		int[] TempMin1 = new int[Definition.Serial_EIT_ALL_CH];
		int[] TempMax1 =  new int[Definition.Serial_EIT_ALL_CH];
		int[] TempMin2 = new int [Definition.Serial_EIT_ALL_CH];
		int[] TempMax2 = new int[Definition.Serial_EIT_ALL_CH];
		int TempStep1 = 0,TempStep2 = 0;
		int[] TempCoarseR = new int[Definition.Serial_EIT_ALL_CH];
		int[] TempFineR = new int[Definition.Serial_EIT_ALL_CH];
		int[] TempCoarseC = new int[Definition.Serial_EIT_ALL_CH];
		int[] TempFineC = new int[Definition.Serial_EIT_ALL_CH];
	
		double[] TempMaxZout = new double[Definition.Serial_EIT_ALL_CH];
		double[] TempMaxRout = new double[Definition.Serial_EIT_ALL_CH];
		double[] TempMaxCout = new double[Definition.Serial_EIT_ALL_CH];

		int[][]TempDigi1 = new int[Definition.Serial_EIT_ALL_CH][3];
		int[][]TempDigi2 = new int[Definition.Serial_EIT_ALL_CH][3];
		/*for(int i = 0; i < Definition.Fabric_SEIT_ALL_CH; i++)
		{
			TempDigi1 = new int[3][]; ////////////////////////////////////// check [i][3]
			TempDigi2 = new int[3][];
		}
*/
		/*int Max1,Max2,Min1,Min2, Step1, Step2;
		Max1 = Output_imp_cal.m_nRangeMax;
		Max2 = Output_imp_cal.m_nRangeMax2;
		Min1 = Output_imp_cal.m_nRangeMin;
		Min2 = Output_imp_cal.m_nRangeMin2;
		Step1 = Output_imp_cal.m_nStep;
		Step2 = Output_imp_cal.m_nStep2;*/
		if(Output_imp_cal.select_comb1.getSelectedIndex() == 0)
		{
			Output_imp_cal.m_nRangeMax = 0;
			Output_imp_cal.m_nRangeMin = 0;
			Output_imp_cal.m_nStep = 1;
		}
		if(Output_imp_cal.select_comb2.getSelectedIndex() == 0)
		{
			Output_imp_cal.m_nRangeMax2 = 0;
			Output_imp_cal.m_nRangeMin2 = 0;
			Output_imp_cal.m_nStep2 = 1;
		}
		if(Output_imp_cal.select_comb3.getSelectedIndex() == 0)
		{
			Output_imp_cal.m_nRangeMax3 = 0;
			Output_imp_cal.m_nRangeMin3 = 0;
			Output_imp_cal.m_nStep3 = 1;
		}
		if(Output_imp_cal.select_comb4.getSelectedIndex() == 0)
		{
			Output_imp_cal.m_nRangeMax4 = 0;
			Output_imp_cal.m_nRangeMin4 = 0;
			Output_imp_cal.m_nStep4 = 1;
		}
		



		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			TempSelect1[i] =Output_imp_cal.select_comb1.getSelectedIndex();
			TempSelect2[i] = Output_imp_cal.select_comb2.getSelectedIndex();
			TempMin1[i] = Output_imp_cal.m_nRangeMin;
			TempMax1[i] = Output_imp_cal.m_nRangeMax;
			TempStep1 =  Output_imp_cal.m_nStep;
			TempMin2[i] = Output_imp_cal.m_nRangeMin2;
			TempMax2[i] = Output_imp_cal.m_nRangeMax2;
			TempStep2 = Output_imp_cal.m_nStep2;
			TempCoarseR[i] = Output_imp_cal.m_nR2;
			TempFineR[i] = Output_imp_cal.m_nR1;
			TempCoarseC[i] =Output_imp_cal. m_nC2;
			TempFineC[i] = Output_imp_cal.m_nC1;

		}
//Coarse
		Object[] Mat_DAta1 = GetOutputImpedanceMatrix(Definition.Serial_EIT_ALL_CH,TempSelect1,TempMax1,TempMin1,TempStep1,TempSelect2,TempMax2,TempMin2,TempStep2,TempCoarseR,TempFineR,TempCoarseC,TempFineC,TempDigi1,TempDigi2,TempMaxZout,TempMaxRout,TempMaxCout);
	
		TempDigi1 = (int[][]) Mat_DAta1[0];
		TempDigi2= (int[][]) Mat_DAta1[1];
		TempMaxZout= (double[]) Mat_DAta1[2];
		TempMaxRout= (double[]) Mat_DAta1[3];
		TempMaxCout = (double[]) Mat_DAta1[4];
		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			TempSelect1[i] = Output_imp_cal.select_comb1.getSelectedIndex();
			TempSelect2[i] = Output_imp_cal.select_comb2.getSelectedIndex();
			TempStep1 = 1;
			TempStep2 = 1;
			if(Output_imp_cal.m_nFreq > 4)
			{
				TempMin1[i] = TempDigi1[i][0] - 10;
				TempMax1[i] = TempDigi1[i][0] + 10;
				TempMin2[i] = TempDigi2[i][0] - 10;
				TempMax2[i] = TempDigi2[i][0] + 10;
			}
			else
			{
				TempMin1[i] = TempDigi1[i][1] - 10;
				TempMax1[i] = TempDigi1[i][1] + 10;
				TempMin2[i] = TempDigi2[i][1] - 10;
				TempMax2[i] = TempDigi2[i][1] + 10;
			}

		}

		Object[] Mat_DAta2 = GetOutputImpedanceMatrix(Definition.Serial_EIT_ALL_CH,TempSelect1,TempMax1,TempMin1,TempStep1,TempSelect2,TempMax2,TempMin2,TempStep2,TempCoarseR,TempFineR,TempCoarseC,TempFineC,
		TempDigi1,TempDigi2,TempMaxZout,TempMaxRout,TempMaxCout);
		TempDigi1 = (int[][]) Mat_DAta2[0];
		TempDigi2= (int[][]) Mat_DAta2[1];
		TempMaxZout= (double[]) Mat_DAta2[2];
		TempMaxRout= (double[]) Mat_DAta2[3];
		TempMaxCout = (double[]) Mat_DAta2[4];

		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			CoarseR[i] = TempCoarseR[i];
			FineR[i] = TempFineR[i];
			CoarseC[i] = TempCoarseC[i];
			FineC[i] = TempFineC[i];
		}
		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			if (Output_imp_cal.m_nFreq > 4)
			{
				switch (TempSelect1[i])
				{
				case 1:
					CoarseR[i] = TempDigi1[i][0];
					break;
				case 2:
					FineR[i] = TempDigi1[i][0];
					break;
				case 3:
					CoarseC[i] = TempDigi1[i][0];
					break;
				case 4:
					FineC[i] = TempDigi1[i][0];
					break;
				}
				switch (TempSelect2[i])
				{
				case 1:
					CoarseR[i] = TempDigi2[i][0];
					break;
				case 2:
					FineR[i] = TempDigi2[i][0];
					break;
				case 3:
					CoarseC[i] = TempDigi2[i][0];
					break;
				case 4:
					FineC[i] = TempDigi2[i][0];
					break;
				}
		}
		else
			{
				switch (TempSelect1[i])
				{
				case 1:
					CoarseR[i] = TempDigi1[i][1];
					break;
				case 2:
					FineR[i] = TempDigi1[i][1];
					break;
				case 3:
					CoarseC[i] = TempDigi1[i][1];
					break;
				case 4:
					FineC[i] = TempDigi1[i][1];
					break;
				}
				switch (TempSelect2[i])
				{
				case 1:
					CoarseR[i] = TempDigi2[i][1];
					break;
				case 2:
					FineR[i] = TempDigi2[i][1];
					break;
				case 3:
					CoarseC[i] = TempDigi2[i][1];
					break;
				case 4:
					FineC[i] = TempDigi2[i][1];
					break;
				}
			}

		}

	/*	int Count = 0;
		if(Output_imp_cal.m_nFreq > 4)
		{
			for(int i = 0; i < Definition.Fabric_SEIT_ALL_CH; i++)
			{
				if(TempMaxZout[i] > 1000000)
				{
					Count++;
				}
			}
		}
		else
		{
			for(int i = 0; i < Definition.Fabric_SEIT_ALL_CH; i++)
			{
				if(TempMaxRout[i] > 1000000)
				{
					Count++;
				}
			}
		}
		*/
		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			TempSelect1[i] =  Output_imp_cal.select_comb3.getSelectedIndex();
			TempSelect2[i] =  Output_imp_cal.select_comb4.getSelectedIndex();
			TempCoarseR[i] = CoarseR[i];
			TempFineR[i] = FineR[i];
			TempCoarseC[i] = CoarseC[i];
			TempFineC[i] = FineC[i];
			TempMin1[i] = Output_imp_cal.m_nRangeMin3;
			TempMax1[i] = Output_imp_cal.m_nRangeMax3;
			TempStep1 = Output_imp_cal.m_nStep3;
			TempMin2[i] = Output_imp_cal.m_nRangeMin4;
			TempMax2[i] = Output_imp_cal.m_nRangeMax4;
			TempStep2 = Output_imp_cal.m_nStep4;
		}
//Fine
		Object[] Mat_DAta3 =GetOutputImpedanceMatrix(Definition.Serial_EIT_ALL_CH,TempSelect1,TempMax1,TempMin1,TempStep1,TempSelect2,TempMax2,TempMin2,TempStep2,TempCoarseR,TempFineR,TempCoarseC,TempFineC,
			TempDigi1,TempDigi2,TempMaxZout,TempMaxRout,TempMaxCout);
		TempDigi1 = (int[][]) Mat_DAta3[0];
		TempDigi2= (int[][]) Mat_DAta3[1];
		TempMaxZout= (double[]) Mat_DAta3[2];
		TempMaxRout= (double[]) Mat_DAta3[3];
		TempMaxCout = (double[]) Mat_DAta3[4];

		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			TempSelect1[i] =  Output_imp_cal.select_comb3.getSelectedIndex();
			TempSelect2[i] =  Output_imp_cal.select_comb4.getSelectedIndex();
			TempStep1 = 1;
			TempStep2 = 1;
			if(Output_imp_cal.m_nFreq > 4)
			{
				TempMin1[i] = TempDigi1[i][0] - 10;
				TempMax1[i] = TempDigi1[i][0] + 10;
				TempMin2[i] = TempDigi2[i][0] - 10;
				TempMax2[i] = TempDigi2[i][0] + 10;
			}
			else
			{
				TempMin1[i] = TempDigi1[i][1] - 10;
				TempMax1[i] = TempDigi1[i][1] + 10;
				TempMin2[i] = TempDigi2[i][1] - 10;
				TempMax2[i] = TempDigi2[i][1] + 10;
			}

		}

		Object[] Mat_DAta4 =GetOutputImpedanceMatrix(Definition.Serial_EIT_ALL_CH,TempSelect1,TempMax1,TempMin1,TempStep1,TempSelect2,TempMax2,TempMin2,TempStep2,TempCoarseR,TempFineR,TempCoarseC,TempFineC,
			TempDigi1,TempDigi2,TempMaxZout,TempMaxRout,TempMaxCout);
		TempDigi1 = (int[][]) Mat_DAta4[0];
		TempDigi2= (int[][]) Mat_DAta4[1];
		TempMaxZout= (double[]) Mat_DAta4[2];
		TempMaxRout= (double[]) Mat_DAta4[3];
		TempMaxCout = (double[]) Mat_DAta4[4];

		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			CoarseR[i] = TempCoarseR[i];
			FineR[i] = TempFineR[i];
			CoarseC[i] = TempCoarseC[i];
			FineC[i] = TempFineC[i];
		}
		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			if (Output_imp_cal.m_nFreq > 4)
			{
				switch (TempSelect1[i])
				{
				case 1:
					CoarseR[i] = TempDigi1[i][0];
					break;
				case 2:
					FineR[i] = TempDigi1[i][0];
					break;
				case 3:
					CoarseC[i] = TempDigi1[i][0];
					break;
				case 4:
					FineC[i] = TempDigi1[i][0];
					break;
				}
				switch (TempSelect2[i])
				{
				case 1:
					CoarseR[i] = TempDigi2[i][0];
					break;
				case 2:
					FineR[i] = TempDigi2[i][0];
					break;
				case 3:
					CoarseC[i] = TempDigi2[i][0];
					break;
				case 4:
					FineC[i] = TempDigi2[i][0];
					break;
				}
			}
			else
			{
				switch (TempSelect1[i])
				{
				case 1:
					CoarseR[i] = TempDigi1[i][1];
					break;
				case 2:
					FineR[i] = TempDigi1[i][1];
					break;
				case 3:
					CoarseC[i] = TempDigi1[i][1];
					break;
				case 4:
					FineC[i] = TempDigi1[i][1];
					break;
				}
				switch (TempSelect2[i])
				{
				case 1:
					CoarseR[i] = TempDigi2[i][1];
					break;
				case 2:
					FineR[i] = TempDigi2[i][1];
					break;
				case 3:
					CoarseC[i] = TempDigi2[i][1];
					break;
				case 4:
					FineC[i] = TempDigi2[i][1];
					break;
				}
			}

		}

	/*	Count = 0;
		if(Output_imp_cal.m_nFreq > 4)
		{
			for(int i = 0; i < Definition.Fabric_SEIT_ALL_CH; i++)
			{
				if(TempMaxZout[i] > 1000000)
				{
					Count++;
				}
			}
		}
		else
		{
			for(int i = 0; i < Definition.Fabric_SEIT_ALL_CH; i++)
			{
				if(TempMaxRout[i] > 1000000)
				{
					Count++;
				}
			}
		}
*/
	
		return new Object[]{CoarseR, FineR, CoarseC, FineC};
		
	}
	boolean CCSCalWriteFile(int TotalCh, String FilePath, int[] FineR, int[] CoarseR, int[] FineC, int[] CoarseC) throws IOException
	{
		String FileName;
		FileName = "//OutputImpedance.txt";
		FileWriter foutCCSOutputImpedance = new FileWriter(FilePath + FileName);
		foutCCSOutputImpedance.write("%FineR\tCoarseR\tFineC\tCoarseC\r\n");
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			foutCCSOutputImpedance.write(FineR[Ch] + "\t" + CoarseR[Ch] + "\t" + FineC[Ch] + "\t" + CoarseC[Ch] + "\r\n");
		}
		foutCCSOutputImpedance.close();
		return true;
	}	
	boolean CCSCalCloseFile() throws IOException
	{
		for(int i = 0; i < Definition.Serial_EIT_ALL_CH; i++)
		{
			foutZout[i].close();
			foutRout[i].close();
			foutCout[i].close();
		}

		return true;
	}
	
	boolean ConvertCalibrationData() throws IOException
	{
		Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.LoadCalibrationFile(0,1,0,0);
		String FilePath, FileName;
		FilePath = m_strDefaultFilePath + "//" + m_strCalibration;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strUSBID;
		new File(FilePath).mkdirs();
		FilePath = FilePath + "//" + m_strCategory;
		FileName = "CCSCalibrationData.txt";
		FileWriter foutOutputImpedance = new FileWriter(FilePath + "//" + FileName);
		for(int Ch = 0; Ch < Definition.Serial_EIT_ALL_CH; Ch++)
		{
			for(int Freq = 0; Freq < Definition.NUM_OF_FREQUENCY; Freq++)
			{
				for(int i = 0; i < 4; i++)
				{
					foutOutputImpedance.write( Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSCalData[Ch][Freq][i] + "\t");
				}
				foutOutputImpedance.write("\r\n");
			}
		}
		
		foutOutputImpedance.close();
		return true;
	}
	
	Object[] GetOutputImpedanceMatrix(int TotalCh, int[] Select1, int[] Max1, int[] Min1, int Step1, int[] Select2, int[] Max2, int[] Min2, int Step2, 
			   int[] CoarseR, int[] FineR, int[] CoarseC, int[] FineC, int[][] DigiValue1, int[][] DigiValue2, 
			   double[] MaxZout, double[] MaxRout, double[] MinCout) throws IOException
	{
		//Select - 0 : None, 1 : CoarseR, 2 : FineR, 3 : CoarseC, 4 : FineC
		int Range1, Length1, Range2, Length2; // Length1 : Height, Length2 : Width
		int Index;

		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
		if(Max1[Ch] > 255)
		{
			Max1[Ch] = 255;
		}
		if(Max2[Ch] > 255)
		{
			Max2[Ch] = 255;
		}
		if(Min1[Ch] < 0)
		{
			Min1[Ch] = 0;
		}
		if(Min2[Ch] < 0)
		{
			Min2[Ch] = 0;
		}
		}

		Range1 = Max1[0] - Min1[0];
		Range2 = Max2[0] - Min2[0];
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
		if((Max1[Ch] - Min1[Ch]) > Range1)
		{
			Range1 = Max1[Ch] - Min1[Ch];
		}

		if((Max2[Ch] - Min2[Ch]) > Range2)
		{
			Range2 = Max2[Ch] - Min2[Ch];
		}

		if(Select1[Ch] == 0)
		{
			Max1[Ch] = 0;
			Min1[Ch] = 0;
			Step1 = 0;
		}
		if(Select2[Ch] == 0)
		{
			Max2[Ch] = 0;
			Min2[Ch] = 0;
			Step2 = 0;
		}
		if(Step1 < 0)
		{
			Step1 = 0;
		}
		if(Step2 < 0)
		{
			Step2 = 0;
		}

		}

		if(Step1 == 0)
		{
			Length1 = 1;
		}
		else
		{
			Length1 = (Range1 / Step1) + 1 ;
		}
		if(Step2 == 0)
		{
			Length2 = 1;
		}
		else
		{
			Length2 = (Range2 / Step2) + 1;
		}



		double[][] ZoutMatrix, RoutMatrix, CoutMatrix;  //double **ZoutMatrix, **RoutMatrix, **CoutMatrix;
		int[] TempCoarseR, TempFineR, TempCoarseC, TempFineC;
		int[][] TempDigi1, TempDigi2;  //int **TempDigi1, **TempDigi2;
		String[] strSelect1, strSelect2, strDigi1, strDigi2;
		int[] Digi1Index, Digi2Index;

		
		TempCoarseR = new int[TotalCh];
		TempFineR = new int[TotalCh];
		TempCoarseC = new int[TotalCh];
		TempFineC = new int[TotalCh];
		TempDigi1 = new int[TotalCh][];////////////////////
		TempDigi2 = new int[TotalCh][];//////////////
		ZoutMatrix = new double[TotalCh][];//////////////
		RoutMatrix = new double[TotalCh][];///////////////
		CoutMatrix = new double[TotalCh][];////////////////
		strSelect1 = new String[TotalCh];
		strSelect2 = new String[TotalCh];
		strDigi1 = new String[TotalCh];
		strDigi2 = new String[TotalCh];
		Digi1Index = new int[TotalCh];
		Digi2Index = new int[TotalCh];

		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			TempCoarseR[Ch] = CoarseR[Ch];
			TempFineR[Ch] = FineR[Ch];
			TempCoarseC[Ch] = CoarseC[Ch];
			TempFineC[Ch] = FineC[Ch];
			TempDigi1[Ch] = new int[Length1];///////////
			TempDigi2[Ch] = new int[Length2];//////////
			ZoutMatrix[Ch] = new double[Length1*Length2];//////
			RoutMatrix[Ch] = new double[Length1*Length2];///////
			CoutMatrix[Ch] = new double[Length1*Length2];////////
		}


		Index = 0;
		for(int i = 0; i < Length1; i++)
			{
				for(int Ch = 0; Ch < TotalCh; Ch++)
					{
						switch(Select1[Ch])
							{
								case 0:
								TempDigi1[Ch][i] = 0;
								break;
								
								case 1:
								if(i == 0)
								{
									TempDigi1[Ch][i] = Min1[Ch];
								}
								else
								{
									TempDigi1[Ch][i] = TempDigi1[Ch][i - 1] + Step1;
								if(TempDigi1[Ch][i] > 255)
								{
									TempDigi1[Ch][i] = 255;
								}
								else if(TempDigi1[Ch][i] < 0)
								{
									TempDigi1[Ch][i] = 0;
								}
						
								}
								TempCoarseR[Ch] = TempDigi1[Ch][i];
								break;
						
								case 2:
								if(i == 0)
								{
									TempDigi1[Ch][i] = Min1[Ch];
								}
								else
								{
									TempDigi1[Ch][i] = TempDigi1[Ch][i - 1] + Step1;
								if(TempDigi1[Ch][i] > 255)
								{
									TempDigi1[Ch][i] = 255;
								}
								else if(TempDigi1[Ch][i] < 0)
								{
									TempDigi1[Ch][i] = 0;
								}
						
								}
								TempFineR[Ch] = TempDigi1[Ch][i];
						
								break;
						
								case 3:
								if(i == 0)
								{
									TempDigi1[Ch][i] = Min1[Ch];
								}
								else
								{
								TempDigi1[Ch][i] = TempDigi1[Ch][i - 1] + Step1;
								if(TempDigi1[Ch][i] > 255)
								{
									TempDigi1[Ch][i] = 255;
								}
								else if(TempDigi1[Ch][i] < 0)
								{
									TempDigi1[Ch][i] = 0;
								}
						
								}
								TempCoarseC[Ch] = TempDigi1[Ch][i];
						
								break;
								case 4:
								if(i == 0)
								{
									TempDigi1[Ch][i] = Min1[Ch];
								}
								else
								{
									TempDigi1[Ch][i] = TempDigi1[Ch][i - 1] + Step1;
								if(TempDigi1[Ch][i] > 255)
								{
									TempDigi1[Ch][i] = 255;
								}
								else if(TempDigi1[Ch][i] < 0)
								{
									TempDigi1[Ch][i] = 0;
								}
						
								}
								TempFineC[Ch] = TempDigi1[Ch][i];
						
								break;
						
								}
						}
		
				for(int j = 0; j < Length2; j++)
					{
						for(int Ch = 0; Ch < TotalCh; Ch++)
							{
								switch(Select2[Ch])
									{
										case 0:
										TempDigi2[Ch][j] = 0;
										break;
										
										case 1:
										if(j == 0)
										{
											TempDigi2[Ch][j] = Min2[Ch];
										}
										else
										{
											TempDigi2[Ch][j] = TempDigi2[Ch][j - 1] + Step2;
										if(TempDigi2[Ch][j] > 255)
										{
											TempDigi2[Ch][j] = 255;
										}
										else if(TempDigi2[Ch][j] < 0)
										{
											TempDigi2[Ch][j] = 0;
										}
								
										}
										TempCoarseR[Ch] = TempDigi2[Ch][j];
										break;
								
										case 2:
										if(j == 0)
										{
											TempDigi2[Ch][j] = Min2[Ch];
										}
										else
										{
											TempDigi2[Ch][j] = TempDigi2[Ch][j - 1] + Step2;
										if(TempDigi2[Ch][j] > 255)
										{
											TempDigi2[Ch][j] = 255;
										}
										else if(TempDigi2[Ch][j] < 0)
										{
											TempDigi2[Ch][j] = 0;
										}
								
										}
										TempFineR[Ch] = TempDigi2[Ch][j];
								
										break;
								
										case 3:
										if(j == 0)
										{
											TempDigi2[Ch][j] = Min2[Ch];
										}
										else
										{
											TempDigi2[Ch][j] = TempDigi2[Ch][j - 1] + Step2;
										if(TempDigi2[Ch][j] > 255)
										{
											TempDigi2[Ch][j] = 255;
										}
										else if(TempDigi2[Ch][j] < 0)
										{
											TempDigi2[Ch][j] = 0;
										}
								
										}
										TempCoarseC[Ch] = TempDigi2[Ch][j];
								
										break;
										case 4:
										if(j == 0)
										{
											TempDigi2[Ch][j] = Min2[Ch];
										}
										else
										{
											TempDigi2[Ch][j] = TempDigi2[Ch][j - 1] + Step2;
										if(TempDigi2[Ch][j] > 255)
										{
											TempDigi2[Ch][j] = 255;
										}
										else if(TempDigi2[Ch][j] < 0)
										{
											TempDigi2[Ch][j] = 0;
										}
								
										}
										TempFineC[Ch] = TempDigi2[Ch][j];
										break;
								
										}
								}
						if(!GetSingleOutputImpedance(Definition.Serial_EIT_ALL_CH,TempCoarseR,TempFineR,TempCoarseC,TempFineC))  /// should return 3 matrix
						{
						return null;
						}
				
						for(int n = 0; n < Definition.Serial_EIT_ALL_CH; n++)  ////// from CalRout
						{
							ZoutMatrix[n][Index] = TempZout[n];   
							RoutMatrix[n][Index] = TempRout[n];
							CoutMatrix[n][Index] = TempCout[n];
						}
						Index++;
						}
		
				}

		Object[] Z_COL_ROW = FindMaxValue(TotalCh,ZoutMatrix,Length2,Length1,Digi2Index,Digi1Index,MaxZout);
		Digi2Index = (int[]) Z_COL_ROW[0];
		Digi1Index = (int[]) Z_COL_ROW[1];
		MaxZout = (double[]) Z_COL_ROW[2];
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			DigiValue1[Ch][0] = TempDigi1[Ch][Digi1Index[Ch]];
			DigiValue2[Ch][0] = TempDigi2[Ch][Digi2Index[Ch]];
		}
		Object[] R_COL_ROW = FindMaxValue(TotalCh,RoutMatrix,Length2,Length1,Digi2Index,Digi1Index,MaxRout);
		Digi2Index = (int[]) R_COL_ROW[0];
		Digi1Index = (int[]) R_COL_ROW[1];
		MaxRout = (double[]) R_COL_ROW[2];
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			DigiValue1[Ch][1] = TempDigi1[Ch][Digi1Index[Ch]];
			DigiValue2[Ch][1] = TempDigi2[Ch][Digi2Index[Ch]];
		}
		Object[] C_COL_ROW = FindAbsMinValue(TotalCh,CoutMatrix,Length2,Length1,Digi2Index,Digi1Index,MinCout);  ////////////// should return Digi2Index Digi1Index
		Digi2Index = (int[]) C_COL_ROW[0];
		Digi1Index = (int[]) C_COL_ROW[1];
		MinCout =  (double[]) C_COL_ROW[2];
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			DigiValue1[Ch][2] = TempDigi1[Ch][Digi1Index[Ch]];
			DigiValue2[Ch][2] = TempDigi2[Ch][Digi2Index[Ch]];
		}

		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			switch (Select1[Ch])
			{
				case 0:
				strSelect1[Ch] = "None";
				break;
				
				case 1:
				strSelect1[Ch] = "CoarseR";
				break;
				
				case 2:
				strSelect1[Ch] = "FineR";
				break;
				
				case 3:
				strSelect1[Ch] = "CoarseC";
				break;
				
				case 4:
				strSelect1[Ch] = "FineC";
				break;
		}

			switch (Select2[Ch])
			{
				case 0:
				strSelect2[Ch] = "None";
				break;
				
				case 1:
				strSelect2[Ch] = "CoarseR";
				break;
				
				case 2:
				strSelect2[Ch] = "FineR";
				break;
				
				case 3:
				strSelect2[Ch] = "CoarseC";
				break;
				
				case 4:
				strSelect2[Ch] = "FineC";
				break;
			}

		foutZout[Ch].write("\t");
		foutRout[Ch].write("\t");
		foutCout[Ch] .write("\t");
		for(int i = 0; i < Length2; i++)
		{
		strDigi2[Ch]=String.valueOf(TempDigi2[Ch][i]);
		foutZout[Ch].write(strSelect2[Ch] + strDigi2[Ch] + "\t");
		foutRout[Ch].write(strSelect2[Ch] + strDigi2[Ch] + "\t");
		foutCout[Ch].write(strSelect2[Ch] + strDigi2[Ch] + "\t");
		}
		foutZout[Ch].write("\r\n");
		foutRout[Ch].write("\r\n");
		foutCout[Ch].write("\r\n");
		}
		Index = 0;
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
		Index = 0;
		for(int i = 0; i < Length1; i++)
		{
		strDigi1[Ch]=String.valueOf(TempDigi1[Ch][i]);
		foutZout[Ch].write(strSelect1[Ch] + strDigi1[Ch] +"\t");
		foutRout[Ch].write(strSelect1[Ch] + strDigi1[Ch] + "\t");
		foutCout[Ch].write(strSelect1[Ch] + strDigi1[Ch] + "\t");
		for (int j = 0; j < Length2; j++)
		{
		foutZout[Ch].write(ZoutMatrix[Ch][Index] + "\t");
		foutRout[Ch].write(RoutMatrix[Ch][Index] + "\t");
		foutCout[Ch].write(CoutMatrix[Ch][Index] + "\t");
		Index++;
		}
		foutZout[Ch].write("\r\n");
		foutRout[Ch].write("\r\n");
		foutCout[Ch].write("\r\n");
		}
		foutZout[Ch].write("MaxZout:\t" +  MaxZout[Ch] + "\t"+ strSelect1[Ch] + ":\t" + DigiValue1[Ch][0] + "\t" + strSelect2[Ch] + ":\t" + DigiValue2[Ch][0] + "\r\n\r\n");
		foutRout[Ch].write("MaxRout:\t" + MaxRout[Ch] + "\t" + strSelect1[Ch] + ":\t" + DigiValue1[Ch][1] + "\t" + strSelect2[Ch] + ":\t" + DigiValue2[Ch][1] + "\r\n\r\n");
		foutCout[Ch].write("MinCout:\t" + MinCout[Ch] + "\t" + strSelect1[Ch] + ":\t" + DigiValue1[Ch][2] + "\t" + strSelect2[Ch] + ":\t" + DigiValue2[Ch][2] + "\r\n\r\n");
		///// for new version take [ch] MaxZout MaxRout MinCout depend on this we can calibrate again just channel which have problem to save time 
		/// other way to make check box and select which IMMM need to calibrate
		}


		 return new Object[]{DigiValue1, DigiValue2, MaxZout, MaxRout,  MinCout};		
	}
		
	
	boolean GetSingleOutputImpedance(int TotalCh, int[] CoarseR, int[] FineR, int[] CoarseC, int[] FineC)
	{
		int[] Overflow = new int[Definition.Serial_EIT_ALL_CH];
		int[] m_nRealHigh = new int[Definition.Serial_EIT_ALL_CH];
		int[] m_nQuadHigh = new int[Definition.Serial_EIT_ALL_CH];
		int[] m_nRealLow = new int[Definition.Serial_EIT_ALL_CH];
		int[] m_nQuadLow = new int[Definition.Serial_EIT_ALL_CH];

		if(!CCSCalSystemSetting2(Definition.ALL_CH))
		{
			JOptionPane.showMessageDialog(null, "CCS Calibration Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			if(!CCSCalDigiSetting(Ch, CoarseR[Ch], FineR[Ch], CoarseC[Ch], FineC[Ch]))
			{
				JOptionPane.showMessageDialog(null, "CCS Digipot Setting Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;

			}
		}

		if(!CCSCalSWSetting(Definition.ALL_CH,3))	// High
		{
			return false;
		}

		if(!CCSCalProjection(TotalCh,Overflow,m_nRealHigh,m_nQuadHigh))
		{
			return false;
		}

		if(!CCSCalSystemSetting2(Definition.ALL_CH))
		{
			JOptionPane.showMessageDialog(null, "CCS Calibration Fail", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if(!CCSCalSWSetting(Definition.ALL_CH,1))	// Low
		{
			return false;
		}

		if(!CCSCalProjection(TotalCh,Overflow,m_nRealLow,m_nQuadLow))
		{
			return false;
		}

		CalRout(TotalCh,Output_imp_cal.m_nFreq,m_nRealHigh,m_nQuadHigh,m_nRealLow,m_nQuadLow);
		return true;
	}
	
	boolean CCSCalSystemSetting2(int Ch)
	{
		int CNTHigh, CNTLow, Gap;

		FreqtoCNTGap_data = Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.FreqtoCNTGap(Output_imp_cal.m_nFreq); 
		CNTHigh = FreqtoCNTGap_data[0];
		CNTLow = FreqtoCNTGap_data[1];
		Gap= FreqtoCNTGap_data[2];	

		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.InjFreqSetting(Ch,Gap,CNTHigh,CNTLow))
		{
			JOptionPane.showMessageDialog(null, "Source Injection Freq Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);

			return false;
		}

		return true;
	}
	
	boolean CCSCalDigiSetting(int Ch, int CoarseR, int FineR, int CoarseC, int FineC)
	{
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.CCSDigiSetting(Ch,FineR,CoarseR))
		{
			JOptionPane.showMessageDialog(null, "CCS Digipot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		if(!Output_Impedance_Calibration.getEIT_Control_Dlg().EIT_Control.GIC1Setting(Ch,FineC,CoarseC))
		{
			JOptionPane.showMessageDialog(null, "GIC Digipot Setting Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		return true;
	}
	
	boolean CalRout(int TotalCh, int FreqIndex, int[] Real_High, int[] Quad_High, int[]Real_Low, int[] Quad_Low)
	{
		double f[]  = {11.25,56.25,112.5,1125,5625,11250,56250,112500,247500,450000}; 

		for(int Ch = 0; Ch < TotalCh; Ch++)
		{
			if((Quad_Low[Ch] * Quad_High[Ch] + Real_Low[Ch] * Real_High[Ch])  - Math.pow((double)Real_High[Ch],2) - Math.pow((double)Quad_High[Ch],2) == 0)
				TempRout[Ch] = 1e+9;
			else
				TempRout[Ch] = (double)(Math.pow((double)Quad_High[Ch],2) + Math.pow((double)Real_High[Ch],2)) * 1000 / ((Quad_Low[Ch] * Quad_High[Ch] + Real_Low[Ch] * Real_High[Ch])  - Math.pow((double)Real_High[Ch],2) - Math.pow((double)Quad_High[Ch],2));

			TempRout[Ch] = Math.abs(TempRout[Ch]);

			TempCout[Ch] = (double)(Quad_Low[Ch]*Real_High[Ch] - Real_Low[Ch]*Quad_High[Ch]) / ((Math.pow((double)Quad_High[Ch],2)+Math.pow((double)Real_High[Ch],2))*1000*2*Math.PI*f[FreqIndex]);

			TempZout[Ch] = TempRout[Ch] / Math.sqrt( 1 + Math.pow(2 * Math.PI * TempRout[Ch] * TempCout[Ch] * f[FreqIndex],2));
		}

		return true;
	}

	Object[] FindMaxValue(int TotalCh, double[][] dArr, int nWidth, int nHeight, int[] MaxCol, int[] MaxRow, double[] MaxValue)
	{
		int[] MaxIndex = new int[TotalCh];

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			MaxValue[Ch] = dArr[Ch][0];
			MaxIndex[Ch] = 0;
		}

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			for(int i = 0; i < nWidth*nHeight; i++)
			{
				if(dArr[Ch][i] > MaxValue[Ch])
				{
					MaxValue[Ch] = dArr[Ch][i];
					MaxIndex[Ch] = i;
				}
			}
		}

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			MaxCol[Ch] = MaxIndex[Ch] % nWidth;
			MaxRow[Ch] = MaxIndex[Ch] / nWidth;
		}



		return new Object[]{MaxCol, MaxRow,MaxValue};
	}
	
	Object[] FindAbsMinValue(int TotalCh, double[][] dArr, int nWidth, int nHeight, int[] MinCol, int[] MinRow, double[] MinValue)
	{	
		int[] MinIndex = new int[TotalCh];
		double[] AbsMinValue = new double[TotalCh];

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			AbsMinValue[Ch] = Math.abs(dArr[Ch][0]);
			MinIndex[Ch] = 0;
		}

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			for(int i = 0; i < nWidth*nHeight; i++)
			{
				if(Math.abs(dArr[Ch][i]) < AbsMinValue[Ch])
				{
					AbsMinValue[Ch] = Math.abs(dArr[Ch][i]);
					MinIndex[Ch] = i;
				}
			}
		}

		for (int Ch = 0; Ch < TotalCh; Ch++)
		{
			MinCol[Ch] = MinIndex[Ch] % nWidth;
			MinRow[Ch] = MinIndex[Ch] / nWidth;
			MinValue[Ch] = dArr[Ch][(MinRow[Ch] * nWidth) + MinCol[Ch]];
		}



		return new Object[]{MinCol, MinRow,MinValue};
	}

	
	
}
