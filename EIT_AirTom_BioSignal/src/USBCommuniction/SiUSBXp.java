package USBCommuniction;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;


public interface SiUSBXp extends StdCallLibrary {
	SiUSBXp INSTANCE = (SiUSBXp) Native.loadLibrary("SiUSBXp" ,SiUSBXp.class);
	
	// GetProductString() function flags
	public static final int 	SI_RETURN_SERIAL_NUMBER = 	0x00;
	public static final int		SI_RETURN_DESCRIPTION	=	0x01;
	public static final int		SI_RETURN_LINK_NAME		=	0x02;
	public static final int		SI_RETURN_VID			=	0x03;
	public static final int		SI_RETURN_PID			=	0x04;
	 

	// Return codes
	public static final int		SI_SUCCESS				=	0x00;
	public static final int		SI_DEVICE_NOT_FOUND		=	0xFF;
	public static final int		SI_INVALID_HANDLE		=	0x01;
	public static final int		SI_READ_ERROR			=	0x02;
	public static final int		SI_RX_QUEUE_NOT_READY	=	0x03;
	public static final int		SI_WRITE_ERROR			=	0x04;
	public static final int		SI_RESET_ERROR			=	0x05;
	public static final int		SI_INVALID_PARAMETER	=	0x06;
	public static final int		SI_INVALID_REQUEST_LENGTH=	0x07;
	public static final int		SI_DEVICE_IO_FAILED		=	0x08;
	public static final int		SI_INVALID_BAUDRATE		=	0x09;
	public static final int		SI_FUNCTION_NOT_SUPPORTED=	0x0a;
	public static final int		SI_GLOBAL_DATA_ERROR	=	0x0b;
	public static final int		SI_SYSTEM_ERROR_CODE	=	0x0c;
	public static final int		SI_READ_TIMED_OUT		=	0x0d;
	public static final int		SI_WRITE_TIMED_OUT		=	0x0e;

	// RX Queue status flags
	public static final int		SI_RX_NO_OVERRUN		=	0x00;
	// 7/27/05
	// added SI_RX_EMPTY (same as SI_RX_NO_OVERRUN)
	public static final int		SI_RX_EMPTY				=	0x00;
	public static final int		SI_RX_OVERRUN			=	0x01;
	public static final int		SI_RX_READY				=	0x02;

	// Buffer size limits
	public static final int		SI_MAX_DEVICE_STRLEN	=	256;
	public static final int		SI_MAX_READ_SIZE		=	4096*16;
	public static final int		SI_MAX_WRITE_SIZE		=	4096;

	// Type definitions
	//typedef		int		SI_STATUS;
	//typedef		char	SI_DEVICE_STRING[SI_MAX_DEVICE_STRLEN];
	

	// Input and Output pin Characteristics
	public static final int		SI_HELD_INACTIVE		=	0x00;
	public static final int		SI_HELD_ACTIVE			=	0x01;
	public static final int		SI_FIRMWARE_CONTROLLED	=	0x02;
	public static final int		SI_RECEIVE_FLOW_CONTROL	=	0x02;
	public static final int		SI_TRANSMIT_ACTIVE_SIGNAL=	0x03;
	public static final int		SI_STATUS_INPUT			=	0x00;
	public static final int		SI_HANDSHAKE_LINE		=	0x01;	// 01


	// Mask and Latch value bit definitions
	public static final int		SI_GPIO_0	=	0x01;
	public static final int		SI_GPIO_1	=	0x02;
	public static final int		SI_GPIO_2	=	0x04;
	public static final int		SI_GPIO_3	=	0x08;

	// GetDeviceVersion() return codes
	public static final int		SI_CP2101_VERSION	=	0x01;
	public static final int		SI_CP2102_VERSION	=	0x02;
	public static final int		SI_CP2103_VERSION	=	0x03;


	
	// Function From USBComm.cpp file 
	int SI_GetNumDevices (IntByReference lpdwNumDevices);
	int SI_GetProductString( int dwDeviceNum , byte[] lpvDeviceString , int dwFlags);
	int SI_GetTimeouts (int ReadTimeout, int WriteTimeout);
	int SI_SetTimeouts (int ReadTimeout, int WriteTimeout);
	int SI_Write (HANDLE Handle, byte[] lpBuffer, int dwBytesToWrite, IntByReference lpdwBytesWritten);
	int SI_Write (HANDLE Handle, int[] lpBuffer, int dwBytesToWrite, IntByReference lpdwBytesWritten);
	int SI_Read (HANDLE Handle, byte[] Buffer, int NumBytesToRead,IntByReference NumBytesReturned);
	int SI_Read (HANDLE Handle, int[] Buffer, int NumBytesToRead,IntByReference NumBytesReturned);
	int SI_Open (int dwDevice , HANDLEByReference Handle);
	int SI_Close(HANDLEByReference Handle);
	int SI_GetPartNumber (HANDLE cyHandle , ByteByReference lpbPartNum);
	int SI_CheckRXQueue (HANDLE Handle, IntByReference NumBytesInQueue,IntByReference QueueStatus);
	int SI_FlushBuffers (HANDLE Handle, byte FlushTransmit,byte FlushReceive);
	
	
	

}
