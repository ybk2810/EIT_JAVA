package USBCommuniction;

import javax.swing.JOptionPane;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;



public class USB_Functions  {
	
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;
	
//////////////////////////////////////////////
	public static final int MAX_PACKET_SIZE_WRITE =	512;
	public static final int MAX_PACKET_SIZE_READ  =	20000;  //change 30000 depend on size

	public static final int MAX_WRITE_PKTS	=	0x01;


	public static final int FT_READ_MSG		=	0x00;
	public static final int FT_WRITE_MSG	=	0x01;
	public static final int FT_READ_ACK		=	0x02;
	public static final int FT_MSG_SIZE		=	0x0a;
//////////////////////////////////////////////
	public int m_nNumofDevice;
	byte [] m_strDeviceString = new byte[10];
	//HANDLE m_hUSBDevice[10];
	public HANDLE [] m_hUSBDevice = new HANDLE[10];
	public HANDLEByReference dev_handle_ref = new HANDLEByReference();
	public byte[]  buf= new byte[MAX_PACKET_SIZE_WRITE];

	int[] m_PortOpenStatus = new int[10];
	public int m_nDeviceList;
	HANDLE INVALID_HANDLE_VALUE;
	public int m_bReadOK;
	public int m_bBusy;
	public int  m_nReadByte;
	public int m_bCommBreak;
	public int count = 0 ;

/////////////////////////////////////////////////////
	public byte[] m_nResponse = new byte[MAX_PACKET_SIZE_READ];
	
	byte[]  m_nCommand = new byte[MAX_PACKET_SIZE_WRITE];
	/////////////////////////////////////////////////
	
	
	public USB_Functions()
	{
		m_nDeviceList = 0;
		m_nNumofDevice = 0;
		m_nReadByte = 0;
		m_bReadOK = FALSE;
		m_bBusy = FALSE;
		for(int i = 0; i < 10; i++)
		{
			m_PortOpenStatus[i] = SiUSBXp.SI_DEVICE_NOT_FOUND;
		}


	}
	public int USBComm(byte Data1, byte Data2,byte Data3,byte Data4,byte Data5,byte Data6,byte Data7,byte Data8,byte Data9,byte Data10)
	{
		int success = FALSE;
		int dwBytesWritten = 0;
		int dwBytesRead = 0;

		buf[0] = (char)(10 & 0x000000FF);
		buf[1] = (char)((10 & 0x0000FF00) >> 8);

		buf[2] = Data1;
		buf[3] = Data2;
		buf[4] = Data3;
		buf[5] = Data4;
		buf[6] = Data5;
		buf[7] = Data6;
		buf[8] = Data7;
		buf[9] = Data8;
		buf[10] = Data9;
		buf[11] = Data10;

		
		
		while(m_bBusy==TRUE);
		m_bBusy = TRUE;
		IntByReference IBR_BytesWritten = new IntByReference();
		IBR_BytesWritten.setValue(dwBytesWritten);
		
		IntByReference IBR_BytesRead = new IntByReference();
		IBR_BytesRead.setValue(dwBytesRead);
		
		
		if (DeviceWrite2( buf, FT_MSG_SIZE,IBR_BytesWritten,0)==0) 
		//if(DeviceWrite2(buf, 12, &dwBytesWritten,0))
		{ 
			int status;
			
			//status = DeviceRead2(buf,10,&dwBytesRead);
			status = DeviceRead2(buf, FT_MSG_SIZE, IBR_BytesRead,0);
			
			if(status == TRUE)  
			{
				success = TRUE;
			}
			m_nReadByte = IBR_BytesRead.getValue();
			
			for (int i =0 ; i < 20 ; i++)
				m_nResponse[i]=  buf[i] ;
		}

		m_bBusy = FALSE;
		
		return success;
	}

	public int USBComm2(int SendLength, int RecieveLength, int NumofRecieveBlock, byte[] Data)
	{
		int success = FALSE;
		byte[] buf = new byte [MAX_PACKET_SIZE_READ];
	
		int dwBytesWritten = 0;
		int dwBytesRead = 0 ;
		buf[0] = (byte)(SendLength & 0x000000FF);  ////check
		buf[1] = (byte)((SendLength & 0x0000FF00) >> 8);

		int index = 2;
		for(int i = 0; i < SendLength; i++)
		{
			buf[index] = Data[i];
			index++;
		}

		while(m_bBusy==TRUE);
		m_bBusy = TRUE;
		index = 0;
		m_nReadByte = 0;
		IntByReference IBR_BytesWritten = new IntByReference();
		IBR_BytesWritten.setValue(dwBytesWritten);
		
		IntByReference IBR_BytesRead = new IntByReference();
		IBR_BytesRead.setValue(dwBytesRead);
		

		if(DeviceWrite2(buf, SendLength + 2, IBR_BytesWritten,0)==0) 
		{
			int status ;
			for(int i = 0; i < NumofRecieveBlock; i++)
			{
				status = DeviceRead3(buf, RecieveLength, IBR_BytesRead,0);
				
				if(status == TRUE) // means false reading
				{
					success = TRUE;
					
				}
				m_nReadByte += IBR_BytesRead.getValue();
				for(int j = 0; j < IBR_BytesRead.getValue(); j++)
				{
					m_nResponse[index] = buf[j];
					index++;
				}
			}
		}
		m_bBusy = FALSE;
		
		return success;
	}

	
	public int  FillDeviceList()
	{
		
		
		
		IntByReference dwNumDevices = new IntByReference(); 
		int status = SiUSBXp.INSTANCE.SI_GetNumDevices(dwNumDevices);
		
		
		if (status == SiUSBXp.SI_SUCCESS)
		{
			
			for (int d = 0; d < dwNumDevices.getValue(); d++)
			{
				status = SiUSBXp.INSTANCE.SI_GetProductString(d, m_strDeviceString, SiUSBXp.SI_RETURN_SERIAL_NUMBER);
			
			
				if (status == SiUSBXp.SI_SUCCESS)
				{
					m_nNumofDevice = dwNumDevices.getValue();
					
				}
				else
				{
					return FALSE;
				}
			}
			return m_nNumofDevice;
		}
		return FALSE;
	}
	
	public int DeviceSelect(int DeviceNum)
	{
		if(DeviceNum >= m_nNumofDevice)
		{
			
			JOptionPane.showMessageDialog(null, "USB Device Select Error", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			return FALSE;
		}
		else
		{
			m_nDeviceList = DeviceNum;
		
		}

		return TRUE;
	}
	
	public int USBPortOpen(int DeviceNum)
	{
		if (m_nNumofDevice > 0)
		{
			
		
			m_PortOpenStatus[DeviceNum] = SiUSBXp.INSTANCE.SI_Open(DeviceNum,dev_handle_ref);
	
			return m_PortOpenStatus[DeviceNum];
		}
		return SiUSBXp.SI_DEVICE_NOT_FOUND;
	}

	public int USBPortClose(int DeviceNum)
	{
		int status = SiUSBXp.SI_DEVICE_NOT_FOUND;
		if(m_PortOpenStatus[DeviceNum] == SiUSBXp.SI_SUCCESS)
		{
			
			status = SiUSBXp.INSTANCE.SI_Close(dev_handle_ref);
			
			m_hUSBDevice[DeviceNum] = INVALID_HANDLE_VALUE;
		}
		return status;
	}
	
	
	public int readData()
	{
		//Boolean success;
		int success = TRUE;
		// Open the temporary file for writing
		int		dwBytesRead		= 0;
		int		dwBytesWritten	= 0;
		
		
		byte[] buf = new byte[MAX_PACKET_SIZE_READ];
		byte[] msg = new byte[FT_MSG_SIZE];

		msg[0] = (byte)FT_READ_MSG;
		msg[1] = (byte)0xFF;
		msg[2] = (byte)0xFF;
		
		IntByReference IBR_BytesWritten = new IntByReference();
		IBR_BytesWritten.setValue(dwBytesWritten);
		
		IntByReference IBR_BytesRead = new IntByReference();
		IBR_BytesWritten.setValue(dwBytesRead);

		if (DeviceWrite(msg, FT_MSG_SIZE, IBR_BytesWritten,0) ==0) 
		{
			int size		= 0;
			int counterPkts	= 0;
			int numPkts		= 0;

	
			for(int i=0; i<=FT_MSG_SIZE;i++)
				buf[i]=0;

			if (DeviceRead(buf, FT_MSG_SIZE, IBR_BytesRead,0) ==0)
			{
				size	= (buf[1] & 0x000000FF) | ((buf[2] << 8) & 0x0000FF00);
				numPkts	= (size/MAX_PACKET_SIZE_READ) + (((size % MAX_PACKET_SIZE_READ) > 0)? 1 : 0);

				// Next line added in from Test Example
				int totalRead = 0;
				// Now read data from board
				while (counterPkts < numPkts && success==1)
				{
					int dwReadLength = 0;
					dwBytesRead = 0;
					if ((size - totalRead) < MAX_PACKET_SIZE_READ)
					{
						dwReadLength = size - totalRead;
					}
					else
					{
						dwReadLength = MAX_PACKET_SIZE_READ;
					}						
				
						for(int k =0 ; k<=dwReadLength;k++){
						buf[k]=0;
				}
					if (DeviceRead(m_nResponse, dwReadLength, IBR_BytesRead,0) ==0) //check 0
					{		
						m_bReadOK = TRUE;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Failed reading file packet from target device.", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
						success = FALSE;
					}
					counterPkts++;
				}					
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Failed reading file size message from target device.", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				success =FALSE;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Failed sending read file message to target device.", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
			success = FALSE;
		}

		return success;
	}
	
	
	public int WriteData(byte Data1, byte Data2,byte Data3,byte Data4,byte Data5,byte Data6,byte Data7,byte Data8,byte Data9,byte Data10)
	{
		
		int success = TRUE;

		int size = 10;
		int dwBytesWritten = 0;
		int dwBytesRead = 0;
		byte[]  buf = new byte[MAX_PACKET_SIZE_WRITE];	

		int numPkts;		
		int numLoops;		
		int counterPkts;	
		int counterLoops;	
		int totalWritten;	
		int dwWriteLength;
		int i,j;

		buf[0] = FT_WRITE_MSG;
		buf[1] = (char)(10 & 0x000000FF); 
		buf[2] = (char)((10 & 0x0000FF00) >> 8);

		IntByReference IBR_BytesWritten = new IntByReference();
		IBR_BytesWritten.setValue(dwBytesWritten);
		
		
		if (DeviceWrite( buf, FT_MSG_SIZE,IBR_BytesWritten,0)==0) //check 0 
		{
			if (dwBytesWritten == FT_MSG_SIZE)
			{
				numPkts = (size / MAX_PACKET_SIZE_WRITE) + (((size % MAX_PACKET_SIZE_WRITE) > 0)? 1 : 0);
				numLoops = (numPkts / MAX_WRITE_PKTS) + (((numPkts % MAX_WRITE_PKTS) > 0)? 1 : 0);
				counterPkts = 0;
				counterLoops = 0;
				totalWritten = 0;

				while (counterLoops < numLoops && success == 1)// success == 1 instead of success
				{
					i = 0;

					while ( i < MAX_WRITE_PKTS && counterPkts < numPkts && success==1)
					{
						dwWriteLength	= 0;

						if ((size - totalWritten) < MAX_PACKET_SIZE_WRITE)
						{
							dwWriteLength = size - totalWritten;
						}
						else
						{
							dwWriteLength = MAX_PACKET_SIZE_WRITE;
						}

						//memset(buf, 0, dwWriteLength);
						for (j = 0; j < dwWriteLength; j++)
						buf[j] = 0;   
						
						
						buf[0] = Data1;
						buf[1] = Data2;
						buf[2] = Data3;
						buf[3] = Data4;
						buf[4] = Data5;
						buf[5] = Data6;
						buf[6] = Data7;	
						buf[7] = Data8;	
						buf[8] = Data9;	
						buf[9] = Data10;
						
						for(int k=0; k<10; k++)
							m_nCommand[k] = buf[k];
						dwBytesWritten = 0;

						success = DeviceWrite(buf, dwWriteLength, IBR_BytesWritten,0);
						totalWritten += dwWriteLength;
						counterPkts++;
						i++;
					}

					if (success ==1 )
					{
						
						buf[0]=0;  //memset(buf, 0, 1);
						IntByReference IBR_BytesRead = new IntByReference();
						IBR_BytesWritten.setValue(dwBytesRead);
						while ((buf[0] != 0xFF) && success==1)
						{
							success = DeviceRead(buf, 1, IBR_BytesRead,0);
						}
					}

					counterLoops++;
				}

				if (success != 1) // !success
				{
					success = FALSE;
				}
			}
			else
			{
				success = FALSE;
			}
		}
		else
		{
			success = FALSE;
		}

		return success;
	}
	
	

	public int DeviceWrite(byte[] buffer, int dwSize, IntByReference lpdwBytesWritten, int dwTimeout)
	{
		int tmpReadTO=0 ,tmpWriteTO =0;				
		int	status = SiUSBXp.SI_SUCCESS;

		SiUSBXp.INSTANCE.SI_GetTimeouts(tmpReadTO, tmpWriteTO);

		SiUSBXp.INSTANCE.SI_SetTimeouts(0, dwTimeout);

		status = SiUSBXp.INSTANCE.SI_Write(dev_handle_ref.getValue(), buffer, dwSize, lpdwBytesWritten);

		SiUSBXp.INSTANCE.SI_SetTimeouts(tmpReadTO, tmpWriteTO);

		status =SiUSBXp.SI_SUCCESS;
		return (status);
	}
	

	public int DeviceRead(byte[] buffer, int dwSize, IntByReference lpdwBytesRead, int dwTimeout)
	{
		int tmpReadTO =0 , tmpWriteTO = 0 ;					// Current timeout values.
		int	status = SiUSBXp.SI_SUCCESS;
		int dwQueueStatus	= SiUSBXp.SI_RX_NO_OVERRUN;
		int dwBytesInQueue = 0;
		
		IntByReference int_dwBytesInQueue = new IntByReference();
		int_dwBytesInQueue.setValue(dwBytesInQueue);
		
		IntByReference int_dwQueueStatus = new IntByReference();
		int_dwQueueStatus.setValue(dwQueueStatus);


		SiUSBXp.INSTANCE.SI_GetTimeouts(tmpReadTO, tmpWriteTO);

		if (dwTimeout == 0)
		{
			while (status == SiUSBXp.SI_SUCCESS && (status !=(int_dwQueueStatus.getValue() & SiUSBXp.SI_RX_READY)))
			{
				status = SiUSBXp.INSTANCE.SI_CheckRXQueue(dev_handle_ref.getValue(), int_dwBytesInQueue, int_dwQueueStatus);
			
		
			}
		}
		else
		{
			SiUSBXp.INSTANCE.SI_SetTimeouts(dwTimeout, 0);
		}

		if (status == SiUSBXp.SI_SUCCESS)
		{
			status = SiUSBXp.INSTANCE.SI_Read(dev_handle_ref.getValue(), buffer, dwSize, lpdwBytesRead);
		}

		SiUSBXp.INSTANCE.SI_SetTimeouts(tmpReadTO, tmpWriteTO);
		
		status = SiUSBXp.SI_SUCCESS;
		return (status );
	}
	public int DeviceWrite2(byte[] buffer, int dwSize, IntByReference lpdwBytesWritten, int dwTimeout)
	{
		
		int	status = SiUSBXp.SI_SUCCESS;
	
		status = SiUSBXp.INSTANCE.SI_Write(dev_handle_ref.getValue(), buffer, dwSize, lpdwBytesWritten);
		status = SiUSBXp.SI_SUCCESS;


		return (status);
	}
	
	public int DeviceRead2(byte[] buffer, int dwSize, IntByReference lpdwBytesRead, int dwTimeout)
	{
		int	status = SiUSBXp.SI_SUCCESS;
		int dwQueueStatus	= SiUSBXp.SI_RX_NO_OVERRUN;
		int dwBytesInQueue = 0;
		
		IntByReference int_dwBytesInQueue = new IntByReference();
		int_dwBytesInQueue.setValue(dwBytesInQueue);
		
		IntByReference int_dwQueueStatus = new IntByReference();
		int_dwQueueStatus.setValue(dwQueueStatus);


		if (dwTimeout == 0)
		{
			
			while (status == SiUSBXp.SI_SUCCESS && (status !=(int_dwQueueStatus.getValue() & SiUSBXp.SI_RX_READY)))
			{
				
				status = SiUSBXp.INSTANCE.SI_CheckRXQueue(dev_handle_ref.getValue(), int_dwBytesInQueue, int_dwQueueStatus);
				
				if(m_bCommBreak == TRUE)
				{
					break;
				}
			}
		}


		if (status == SiUSBXp.SI_SUCCESS)
		{
			status = SiUSBXp.INSTANCE.SI_Read(dev_handle_ref.getValue(), buffer, dwSize, lpdwBytesRead);
		
		}
		status = SiUSBXp.SI_SUCCESS;
		
		return (status);

	}

	public int DeviceRead3(byte[] buffer, int dwSize, IntByReference lpdwBytesRead, int dwTimeout)
	{
		int	status = SiUSBXp.SI_SUCCESS;
		int dwQueueStatus	= SiUSBXp.SI_RX_NO_OVERRUN;
		int dwBytesInQueue = 0;


		int size = 0;

		IntByReference int_dwBytesInQueue = new IntByReference();
		int_dwBytesInQueue.setValue(dwBytesInQueue);
		
		IntByReference int_dwQueueStatus = new IntByReference();
		int_dwQueueStatus.setValue(dwQueueStatus);
		if (dwTimeout == 0)
		{
			while (true)
			{
				status = SiUSBXp.INSTANCE.SI_CheckRXQueue(dev_handle_ref.getValue(), int_dwBytesInQueue, int_dwQueueStatus);	
				if(((int_dwQueueStatus.getValue() & 0xFF00) >> 8) == 0)
				{
					size = int_dwBytesInQueue.getValue();
				}
				else
				{
					size = ((int_dwBytesInQueue.getValue() & 0x00FF) << 8 ) + ((int_dwQueueStatus.getValue() & 0xFF00) >> 8);
				}

				if(size >= dwSize)
				{
					break;
				}
				if(m_bCommBreak == TRUE)
				{
					break;
				}
				
				//System.out.println("size" + size);
			}
		}

		if (status == SiUSBXp.SI_SUCCESS)
		{
			status = SiUSBXp.INSTANCE.SI_Read(dev_handle_ref.getValue(), buffer, dwSize, lpdwBytesRead);
			System.out.println("Counter  "+ count++);
		}
		/*long stopTime = System.currentTimeMillis();


		 long time = stopTime - startTime;

		System.out.println("Time "+ time);*/
		status = SiUSBXp.SI_SUCCESS;
		
		return (status);


	}

	public int ReadData3(int DataLength)	// Reduce Stop
	{
		int success = TRUE;
		int status ;
		int		dwBytesRead		= 0;
		int size = DataLength;
		byte[] buf =new byte[MAX_PACKET_SIZE_READ];

		for(int i = 0 ;i<MAX_PACKET_SIZE_READ;i++ )
			buf[i]=0;
		while(m_bBusy == TRUE);
		m_bBusy = TRUE;
	
		IntByReference IBR_BytesRead = new IntByReference();
		IBR_BytesRead.setValue(dwBytesRead);
		status = DeviceRead3(buf, size, IBR_BytesRead,0);
		if(status == 0) //means sucess reading 
		{
			m_nReadByte = IBR_BytesRead.getValue();
			if(m_nReadByte != 0)
			{
				//for (int i =0 ;i < m_nReadByte * Byte.SIZE;i++)   ///memcopy
					for (int i =0 ;i < m_nReadByte ;i++)   ///memcopy
					m_nResponse[i]=buf[i];
				m_bReadOK = TRUE;
			}
		}
		//else
		{
			success = FALSE;
		}
		m_bBusy = FALSE;

		return success;
	}
	
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4, byte Data5, byte Data6, byte Data7, byte Data8, byte Data9)
	{
		return USBComm(Data1, Data2, Data3, Data4, Data5, Data6, Data7, Data8, Data9, (byte)0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4, byte Data5, byte Data6, byte Data7, byte Data8)
	{
		return USBComm(Data1, Data2, Data3, Data4, Data5, Data6, Data7, Data8, (byte)0,(byte) 0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4, byte Data5, byte Data6, byte Data7)
	{
		return USBComm(Data1, Data2, Data3, Data4, Data5, Data6, Data7,(byte) 0,(byte) 0,(byte) 0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4, byte Data5, byte Data6)
	{
		return USBComm(Data1, Data2, Data3, Data4, Data5, Data6,(byte) 0,(byte) 0,(byte)0,(byte) 0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4, byte Data5)
	{
		return USBComm(Data1, Data2, Data3, Data4, Data5,(byte) 0, (byte)0,(byte) 0,(byte) 0,(byte) 0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3, byte Data4)
	{
		return USBComm(Data1, Data2, Data3, Data4,(byte) 0, (byte)0,(byte) 0,(byte) 0, (byte)0,(byte) 0);
	}
	public final int USBComm(byte Data1, byte Data2, byte Data3)
	{
		return USBComm(Data1, Data2, Data3, (byte)0,(byte) 0,(byte) 0, (byte)0, (byte)0, (byte)0, (byte)0);
	}
	public final int USBComm(byte Data1, byte Data2)
	{
		return USBComm(Data1, Data2,(byte) 0,(byte) 0,(byte) 0, (byte)0,(byte) 0,(byte) 0,(byte) 0, (byte)0);
	}
	public final int USBComm(byte Data1)
	{
		return USBComm(Data1,(byte) 0,(byte) 0,(byte) 0,(byte) 0, (byte)0,(byte) 0,(byte) 0,(byte) 0,(byte) 0);
	}
	public final int USBComm()
	{
		return USBComm((byte)0,(byte) 0, (byte)0, (byte)0, (byte)0,(byte) 0,(byte) 0,(byte) 0, (byte)0, (byte)0);
	}
}
